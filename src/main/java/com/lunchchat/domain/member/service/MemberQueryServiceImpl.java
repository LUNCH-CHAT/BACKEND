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
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    public MemberDetailResponseDTO getMemberDetail(Long memberId, Long viewerId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorStatus.USER_NOT_FOUND));

        MatchStatusType matchStatus = matchRepository.findMatchStatusBetween(viewerId, memberId)
                .map(match -> {
                    if (match.getStatus() == MatchStatus.ACCEPTED) return MatchStatusType.ACCEPTED;
                    else if (match.getFromMember().getId().equals(viewerId)) return MatchStatusType.REQUESTED;
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

        return memberRepository.findAll().stream()
                .filter(member -> !member.getId().equals(currentMemberId))
                .map(member -> {
                    List<TimeTable> memberTimeTables = timeTableQueryService.findByMemberId(member.getId());
                    int matchedTimeTableCount = calculateTimeTableOverlap(currentMemberTimeTables, memberTimeTables);
                    int matchedInterestsCount = calculateInterestsOverlap(currentMember, member);

                    double matchingScore = (matchedTimeTableCount * 0.6) + (matchedInterestsCount * 0.4);

                    return new Object[]{member, matchedTimeTableCount, matchedInterestsCount, matchingScore};
                })
                .filter(arr -> (int) arr[1] > 0 && (int) arr[2] > 0) // 시간표/관심사 겹침 ≥ 1
                .sorted((arr1, arr2) -> {
                    double score1 = (double) arr1[3];
                    double score2 = (double) arr2[3];
                    return Double.compare(score2, score1); // 높은 점수 순
                })
                .limit(10)
                .map(arr -> MemberRecommendationConverter.toRecommendationResponse((Member) arr[0]))
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
    public List<MemberRecommendationResponseDTO> getFilteredRecommendations(Long currentMemberId, MemberFilterRequestDTO req) {

        Member currentMember = memberRepository.findById(currentMemberId)
                .orElseThrow(() -> new MemberException(ErrorStatus.USER_NOT_FOUND));

        return memberRepository.findAll().stream()
                .filter(member -> !member.getId().equals(currentMemberId))
                .filter(member -> isFilterMatched(member, req))
                .map(member -> {
                    long matchCount = matchRepository.countMatchesByMember(member.getId());
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
        if (req.getCollege() != null && !req.getCollege().equals(member.getCollege().getName())) return false;
        if (req.getDepartment() != null && !req.getDepartment().equals(member.getDepartment().getName())) return false;

        if (req.getInterest() != null && member.getInterests().stream()
                .noneMatch(i -> i.getType().name().equals(req.getInterest()))) return false;

        if (req.getStudentNo() != null) {
            String target = member.getStudentNo(); // 예: "21학번"
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

