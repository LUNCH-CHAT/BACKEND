package com.lunchchat.domain.match.service;

import com.lunchchat.domain.match.converter.MatchConverter;
import com.lunchchat.domain.match.entity.MatchStatus;
import com.lunchchat.domain.match.entity.Matches;
import com.lunchchat.domain.match.repository.MatchRepository;
import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.member.exception.MemberException;
import com.lunchchat.domain.member.repository.MemberRepository;
import com.lunchchat.domain.user_statistics.service.UserStatisticsCommandService;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import com.lunchchat.global.apiPayLoad.exception.handler.MatchException;
import com.lunchchat.global.apiPayLoad.exception.handler.MemberHandler;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchCommandServiceImpl implements MatchCommandService {

    private final MatchRepository matchRepository;
    private final MemberRepository memberRepository;
    private final UserStatisticsCommandService userStatisticsCommandService;
    private final MatchNotificationService matchNotificationService;

    @Override
    @Transactional
    public Matches requestMatch(String senderEmail, Long toMemberId) {
        Member fromMember = memberRepository.findByEmail(senderEmail)
            .orElseThrow(() -> new MemberHandler(ErrorStatus.USER_NOT_FOUND));

        if (fromMember.getId().equals(toMemberId)) {
            throw new MatchException(ErrorStatus.SELF_MATCH_REQUEST);
        }

        Optional<Matches> existingMatch = matchRepository.findActiveMatchBetween(fromMember.getId(),
            toMemberId);
        if (existingMatch.isPresent()) {
            throw new MatchException(ErrorStatus.ALREADY_MATCHED);
        }

        Member toMember = memberRepository.findById(toMemberId)
            .orElseThrow(() -> new MemberHandler(ErrorStatus.USER_NOT_FOUND));

        Matches newMatch = MatchConverter.toMatchEntity(fromMember, toMember);
        userStatisticsCommandService.incrementRequestedCount(fromMember);
        userStatisticsCommandService.incrementReceivedCount(toMember);

        matchNotificationService.sendMatchRequestNotification(fromMember, toMember);

        return matchRepository.save(newMatch);
    }

    @Override
    @Transactional
    public void acceptMatch(Long otherMemberId, String memberEmail) {
        Member me = memberRepository.findByEmail(memberEmail)
            .orElseThrow(() -> new MemberException(ErrorStatus.USER_NOT_FOUND));

        Member other = memberRepository.findById(otherMemberId)
            .orElseThrow(() -> new MemberException(ErrorStatus.USER_NOT_FOUND));

        // 나(me)를 to로, 상대(other)를 from으로 한 매칭 요청 조회
        Matches match = matchRepository.findByFromMemberAndToMember(other, me)
            .orElseThrow(() -> new MatchException(ErrorStatus.MATCH_NOT_FOUND));

        if (match.getStatus() != MatchStatus.REQUESTED) {
            throw new MatchException(ErrorStatus.INVALID_MATCH_STATUS);
        }

        match.updateStatus(MatchStatus.ACCEPTED);

        userStatisticsCommandService.incrementAcceptedCount(me);
        userStatisticsCommandService.incrementAcceptedCount(other);

        matchNotificationService.sendMatchAcceptNotification(other, me);

        matchRepository.save(match);
    }
}
