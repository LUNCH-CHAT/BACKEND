package com.lunchchat.domain.member.service;

import com.lunchchat.domain.member.converter.MemberConverter;
import com.lunchchat.domain.member.converter.MemberRecommendationConverter;
import com.lunchchat.domain.member.dto.MemberResponseDTO;
import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.member.exception.MemberException;
import com.lunchchat.domain.member.repository.MemberRepository;
import com.lunchchat.domain.time_table.entity.TimeTable;
import com.lunchchat.domain.time_table.service.TimeTableQueryService;
import com.lunchchat.domain.user_interests.repository.UserInterestsRepository;
import com.lunchchat.domain.user_keywords.repository.UserKeywordsRepository;
import com.lunchchat.domain.user_statistics.entity.UserStatistics;
import com.lunchchat.domain.user_statistics.repository.UserStatisticsRepository;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public abstract class MemberQueryServiceImpl implements MemberQueryService {

    private final MemberRepository memberRepository;
    private final MemberConverter memberConverter;
    private final TimeTableQueryService timeTableQueryService;
    private final UserStatisticsRepository userStatisticsRepository;
    private final UserKeywordsRepository userKeywordsRepository;
    private final UserInterestsRepository userInterestsRepository;

//    @Override
//    @Transactional(readOnly = true)
//    public MemberResponseDTO.MemberDetailResponseDTO getMemberDetail(Long memberId) {
//        Member member = memberRepository.findById(memberId)
//                .orElseThrow(() -> new MemberException(ErrorStatus.USER_NOT_FOUND));
//        return memberConverter.toMemberDetailResponse(member);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<MemberResponseDTO.MemberRecommendationResponseDTO> getRecommendedMembers(Long currentMemberId) {
//
//        Member currentMember = memberRepository.findById(currentMemberId)
//                .orElseThrow(() -> new MemberException(ErrorStatus.USER_NOT_FOUND));
//
//        List<TimeTable> currentMemberTimeTables = timeTableQueryService.findByMemberId(currentMemberId);
//
//        return memberRepository.findAll().stream()
//                .filter(member -> !member.getId().equals(currentMemberId))
//                .map(member -> {
//                    List<TimeTable> memberTimeTables = timeTableQueryService.findByMemberId(member.getId());
//                    int matchedTimeTableCount = calculateTimeTableOverlap(currentMemberTimeTables, memberTimeTables);
//                    int matchedInterestsCount = calculateInterestsOverlap(currentMember, member);
//
//                    return new Object[]{member, matchedTimeTableCount, matchedInterestsCount};
//                })
//                .filter(arr -> (int) arr[1] > 0 && (int) arr[2] > 0)
//                .sorted((arr1, arr2) -> {
//                    int total1 = (int) arr1[1] + (int) arr1[2];
//                    int total2 = (int) arr2[1] + (int) arr2[2];
//                    return Integer.compare(total2, total1); // 내림차순
//                })
//                .limit(10)
//                .map(arr -> MemberRecommendationConverter.toRecommendationResponse(
//                        (Member) arr[0]))
//                .collect(Collectors.toList());
//    }
//
//    private int calculateTimeTableOverlap(List<TimeTable> tts1, List<TimeTable> tts2) {
//        return (int) tts1.stream()
//                .filter(tt1 -> tts2.stream()
//                        .anyMatch(tt2 ->
//                                tt1.getDayOfWeek().equals(tt2.getDayOfWeek()) &&
//                                        !(tt1.getEndTime().isBefore(tt2.getStartTime()) ||
//                                                tt1.getStartTime().isAfter(tt2.getEndTime()))
//                        )
//                ).count();
//    }
//
////    private int calculateInterestsOverlap(Member m1, Member m2) {
//        return (int) m1.getUserInterests().stream()
//                .filter(ui1 -> m2.getUserInterests().stream()
//                        .anyMatch(ui2 -> ui1.getInterests().getId().equals(ui2.getInterests().getId())))
//                .count();
//    }
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

