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
import com.lunchchat.domain.member.dto.MemberScoreWrapper;
import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.member.exception.MemberException;
import com.lunchchat.domain.member.repository.MemberRepository;
import com.lunchchat.domain.time_table.entity.TimeTable;
import com.lunchchat.domain.time_table.service.TimeTableQueryService;
import com.lunchchat.domain.user_interests.entity.Interest;
import com.lunchchat.domain.user_interests.repository.InterestRepository;
import com.lunchchat.domain.user_keywords.repository.UserKeywordsRepository;
import com.lunchchat.domain.user_statistics.repository.UserStatisticsRepository;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    public List<MemberRecommendationResponseDTO> getFilteredRecommendations(String currentMemberEmail, MemberFilterRequestDTO req) {

        Member currentMember = memberRepository.findByEmail(currentMemberEmail)
                .orElseThrow(() -> new MemberException(ErrorStatus.USER_NOT_FOUND));

        return memberRepository.findAll().stream()
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
                .skip((long) req.getPage() * req.getSize())
                .limit(req.getSize())
                .map(arr -> MemberRecommendationConverter.toRecommendationResponse((Member) arr[0]))
                .collect(Collectors.toList());
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
    public MyPageResponseDTO getMyPage(Long memberId) {
        return null;
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

