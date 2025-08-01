package com.lunchchat.domain.member.service;

import com.google.api.client.util.SecurityUtils;
import com.lunchchat.domain.match.dto.enums.MatchStatusType;
import com.lunchchat.domain.match.entity.MatchStatus;
import com.lunchchat.domain.match.repository.MatchRepository;
import com.lunchchat.domain.member.converter.MemberConverter;
import com.lunchchat.domain.member.converter.MemberRecommendationConverter;
import com.lunchchat.domain.member.dto.MemberFilterRequestDTO;
import com.lunchchat.domain.member.dto.MemberResponseDTO;
import com.lunchchat.domain.member.dto.MemberResponseDTO.MemberDetailResponseDTO;
import com.lunchchat.domain.member.dto.MemberResponseDTO.MemberRecommendationResponseDTO;
import com.lunchchat.domain.member.dto.MemberResponseDTO.MyPageResponseDTO;
import com.lunchchat.domain.member.dto.MemberScore;
import com.lunchchat.domain.member.dto.MemberScoreWrapper;
import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.member.entity.enums.InterestType;
import com.lunchchat.domain.member.exception.MemberException;
import com.lunchchat.domain.member.repository.MemberRepository;
import com.lunchchat.domain.time_table.entity.TimeTable;
import com.lunchchat.domain.time_table.service.TimeTableQueryService;
import com.lunchchat.domain.user_interests.entity.Interest;
import com.lunchchat.domain.user_interests.repository.InterestRepository;
import com.lunchchat.domain.user_keywords.repository.UserKeywordsRepository;
import com.lunchchat.domain.user_statistics.entity.UserStatistics;
import com.lunchchat.domain.user_statistics.repository.UserStatisticsRepository;
import com.lunchchat.global.apiPayLoad.PaginatedResponse;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import com.lunchchat.global.security.auth.dto.CustomUserDetails;
import com.lunchchat.global.security.jwt.JwtTokenProvider;

import java.time.LocalDateTime;
import java.util.*;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberQueryServiceImpl implements MemberQueryService {

    private final MemberRepository memberRepository;
    private final MemberConverter memberConverter;
    private final TimeTableQueryService timeTableQueryService;
    private final MatchRepository matchRepository;
    private final UserStatisticsRepository userStatisticsRepository;
    private final UserKeywordsRepository userKeywordsRepository;
    private final InterestRepository userInterestsRepository;

    @Override
    @Transactional(readOnly = true)
    public MemberDetailResponseDTO getMemberDetail(Long memberId, String viewerEmail) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorStatus.USER_NOT_FOUND));

        MatchStatusType matchStatus = matchRepository.findMatchStatusBetween(viewerEmail, member.getEmail())
                .map(match -> {
                    if (match.getStatus() == MatchStatus.ACCEPTED) return MatchStatusType.ACCEPTED;
                    else if (match.getFromMember().getEmail().equals(viewerEmail)) return MatchStatusType.REQUESTED;
                    else return MatchStatusType.RECEIVED;
                })
                .orElse(MatchStatusType.NONE);

        return memberConverter.toMemberDetailResponse(member, matchStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberResponseDTO.MemberRecommendationResponseDTO> getRecommendedMembers(Long currentMemberId) {
        Member currentMember = memberRepository.findById(currentMemberId)
                .orElseThrow(() -> new MemberException(ErrorStatus.USER_NOT_FOUND));

        List<TimeTable> currentMemberTimeTables = timeTableQueryService.findByMemberId(currentMemberId);
        List<MemberScoreWrapper> scoreList = new ArrayList<>();

        int page = 0;
        int batchSize = 50;

        while (true) {
            Pageable pageable = PageRequest.of(page, batchSize, Sort.by(Sort.Direction.DESC, "updatedAt"));
            Page<Member> memberPage = memberRepository.findByIdNot(currentMemberId, pageable);

            if (memberPage.isEmpty()) break;

            for (Member member : memberPage) {
                List<TimeTable> memberTimeTables = timeTableQueryService.findByMemberId(member.getId());
                int matchedTimeTableCount = calculateTimeTableOverlap(currentMemberTimeTables, memberTimeTables);
                int matchedInterestsCount = calculateInterestsOverlap(currentMember, member);

                if (matchedTimeTableCount > 0 && matchedInterestsCount > 0) {
                    double score = (matchedTimeTableCount * 0.6) + (matchedInterestsCount * 0.4);
                    scoreList.add(new MemberScoreWrapper(member, score));
                }
            }

            page++;
        }

        return scoreList.stream()
                .sorted(Comparator.comparingDouble(MemberScoreWrapper::score).reversed())
                .limit(10)
                .map(wrapper -> MemberRecommendationConverter.toRecommendationResponse(wrapper.member()))
                .collect(Collectors.toList());
    }


    private int calculateTimeTableOverlap(List<TimeTable> tts1, List<TimeTable> tts2) {
        Set<String> timeBlocks1 = toTimeBlocks(tts1);
        Set<String> timeBlocks2 = toTimeBlocks(tts2);

        timeBlocks1.retainAll(timeBlocks2); // 겹치는 시간 블럭만 남김

        return timeBlocks1.size();
    }

    private Set<String> toTimeBlocks(List<TimeTable> timeTables) {
        Set<String> blocks = new HashSet<>();
        for (TimeTable tt : timeTables) {
            LocalTime start = tt.getStartTime();
            LocalTime end = tt.getEndTime();
            while (start.isBefore(end)) {
                // 예: MON_10, MON_11 등으로 구성
                String block = tt.getDayOfWeek().name() + "_" + start.getHour();
                blocks.add(block);
                start = start.plusHours(1);
            }
        }
        return blocks;
    }


    private int calculateInterestsOverlap(Member m1, Member m2) {
        Set<Interest> m1Interests = m1.getInterests();
        Set<Interest> m2Interests = m2.getInterests();

        Set<Interest> intersection = new HashSet<>(m1Interests);
        intersection.retainAll(m2Interests); // 겹치는 관심사만 남김

        return intersection.size();
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<MemberRecommendationResponseDTO> getFilteredRecommendations(String currentMemberEmail, MemberFilterRequestDTO req) {
        Member currentMember = memberRepository.findByEmail(currentMemberEmail)
                .orElseThrow(() -> new MemberException(ErrorStatus.USER_NOT_FOUND));

        List<Object[]> filteredList = memberRepository.findAll().stream()
                .filter(member -> !member.getEmail().equals(currentMemberEmail))
                .filter(member -> isFilterMatched(member, req))
                .map(member -> {
                    long matchCount = matchRepository.countMatchesByMember(member.getEmail());
                    return new Object[]{member, matchCount, member.getUpdatedAt()};
                })
                .sorted((a, b) -> {
                    if ("recommend".equals(req.getSort())) {
                        return Long.compare((long) b[1], (long) a[1]); // 매칭 수 내림차순
                    } else {
                        return ((LocalDateTime) b[2]).compareTo((LocalDateTime) a[2]); // 최신 수정일 내림차순
                    }
                })
                .toList();

        int total = filteredList.size();
        int startIdx = req.getPage() * req.getSize();
        int endIdx = Math.min(startIdx + req.getSize(), total);

        List<MemberRecommendationResponseDTO> content = filteredList.subList(startIdx, endIdx).stream()
                .map(arr -> MemberRecommendationConverter.toRecommendationResponse((Member) arr[0]))
                .collect(Collectors.toList());

        boolean hasNext = endIdx < total;

        PaginatedResponse.Meta meta = PaginatedResponse.Meta.builder()
                .currentPage(req.getPage())
                .pageSize(req.getSize())
                .totalItems(total)
                .totalPages((int) Math.ceil((double) total / req.getSize()))
                .hasNext(hasNext)
                .build();

        return PaginatedResponse.<MemberRecommendationResponseDTO>builder()
                .data(content)
                .meta(meta)
                .build();
    }

    private boolean isFilterMatched(Member member, MemberFilterRequestDTO req) {
        if (req.getCollege() != null) {
            if (member.getCollege() == null || !req.getCollege().equals(member.getCollege().getName())) return false;
        }

        if (req.getDepartment() != null) {
            if (member.getDepartment() == null || !req.getDepartment().equals(member.getDepartment().getName())) return false;
        }

        if (req.getInterest() != null) {
            if (member.getInterests() == null || member.getInterests().stream()
                    .noneMatch(i -> i.getType().name().equals(req.getInterest()))) return false;
        }

        if (req.getStudentNo() != null) {
            String target = member.getStudentNo();
            if (target == null) return false;

            if ("20학번이상".equals(req.getStudentNo())) {
                if (Integer.parseInt(target.substring(0, 2)) > 20) return false;
            } else {
                if (!target.startsWith(req.getStudentNo().substring(0, 2))) return false;
            }
        }

        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberResponseDTO.MemberRecommendationResponseDTO> getPopularMembers(Long currentMemberId) {

        Member currentMember = memberRepository.findById(currentMemberId)
                .orElseThrow(() -> new MemberException(ErrorStatus.USER_NOT_FOUND));

        List<TimeTable> currentMemberTimeTables = timeTableQueryService.findByMemberId(currentMemberId);
        List<MemberScoreWrapper> scoreList = new ArrayList<>();

        int page = 0;
        int batchSize = 50;

        while (true) {
            Pageable pageable = PageRequest.of(page, batchSize);
            Page<Member> memberPage = memberRepository.findByIdNot(currentMemberId, pageable);

            if (memberPage.isEmpty()) break;

            for (Member member : memberPage.getContent()) {
                int score = 0;

                // 관심사
                int matchedInterests = calculateInterestsOverlap(currentMember, member);
                score += Math.min(matchedInterests, 3) * 3;

                // 시간표
                List<TimeTable> memberTimeTables = timeTableQueryService.findByMemberId(member.getId());
                int matchedTimeTables = calculateTimeTableOverlap(currentMemberTimeTables, memberTimeTables);
                score += Math.min(matchedTimeTables, 3);

                // 매칭 요청 수
                int received = matchRepository.countByToMemberAndStatus(member, MatchStatus.REQUESTED);
                int sent = matchRepository.countByFromMemberAndStatus(member, MatchStatus.REQUESTED);
                score += Math.min(received, 5);
                score += Math.min(sent, 2);

                // 키워드 또는 자기소개 존재 여부
                if (member.getUserKeywords() != null && !member.getUserKeywords().isEmpty()) {
                    score += 1;
                }

                scoreList.add(new MemberScoreWrapper(member, score));
            }

            page++;
        }

        return scoreList.stream()
                .sorted(Comparator.comparingDouble(MemberScoreWrapper::score).reversed()
                        .thenComparing(w -> Optional.ofNullable(w.member().getUpdatedAt()).orElse(LocalDateTime.MIN), Comparator.reverseOrder()))
                .limit(10)
                .map(w -> MemberRecommendationConverter.toRecommendationResponse(w.member()))
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public MemberResponseDTO.MyPageResponseDTO getMyPage(String email) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new MemberException(ErrorStatus.USER_NOT_FOUND));

        UserStatistics userStatistics = userStatisticsRepository.findByMemberId(member.getId())
            .orElseThrow(() -> new MemberException(ErrorStatus.USER_STATISTICS_NOT_FOUND));

        List<String> keywords = userKeywordsRepository.findTitlesByMemberId(member.getId());
        List<InterestType> tags = userInterestsRepository.findInterestTypesByMemberId(member.getId());

        return MemberConverter.toMyPageDto(
            member,
            userStatistics.getMatchCompletedCount(),
            userStatistics.getMatchRequestedCount(),
            userStatistics.getMatchReceivedCount(),
            keywords,
            tags
        );
      }
    private Member getCurrentMember() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = ((CustomUserDetails) authentication.getPrincipal()).getUsername();
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(ErrorStatus.USER_NOT_FOUND));
    }


    private int countInterestMatches(Member member) {
        Member currentMember = getCurrentMember();

        int overlapCount = calculateInterestsOverlap(currentMember, member);

        // 최대 3점까지만 부여
        return Math.min(overlapCount, 3);
    }

    private int countTimetableMatches(Member member) {
        List<TimeTable> currentTables = getCurrentMember().getTimeTables();
        List<TimeTable> targetTables = member.getTimeTables();

        if (currentTables == null || targetTables == null) return 0;

        int overlapCount = calculateTimeTableOverlap(currentTables, targetTables);

        // 최대 점수 3점까지만 부여
        return Math.min(overlapCount, 3);
    }

    private int countReceivedMatchRequests(Member member) {
        // 받은 매칭 요청 (toMember 기준)
        int receivedCount = matchRepository.countByToMemberAndStatus(member, MatchStatus.REQUESTED);
        return Math.min(receivedCount, 5); // 최대 5점
    }

    private int countSentMatchRequests(Member member) {
        // 보낸 매칭 요청 (fromMember 기준)
        int sentCount = matchRepository.countByFromMemberAndStatus(member, MatchStatus.REQUESTED);
        return Math.min(sentCount, 2); // 최대 2점
    }

    private boolean hasKeyword(Member member) {
        return member.getUserKeywords() != null && !member.getUserKeywords().isEmpty();
    }

//
//  @Override
//  public MemberResponseDTO.MyPageResponseDTO getMyPage(Long memberId) {
//    Member member = memberRepository.findById(memberId)
//        .orElseThrow(() -> new MemberException(ErrorStatus.USER_NOT_FOUND));
//
//    UserStatistics userStatistics = userStatisticsRepository.findByMemberId(memberId)
//        .orElseThrow(() -> new MemberException(ErrorStatus.USER_STATISTICS_NOT_FOUND));
//
//    List<String> keywords = userKeywordsRepository.findTitlesByMemberId(memberId);
//    List<String> tags = userInterestsRepository.findInterestNamesByMemberId(memberId);
//
//    return MemberConverter.toMyPageDto(
//        member,
//        userStatistics.getMatchCompletedCount(),
//        userStatistics.getMatchRequestedCount(),
//        userStatistics.getMatchReceivedCount(),
//        keywords,
//        tags
//    );
//  }
}

