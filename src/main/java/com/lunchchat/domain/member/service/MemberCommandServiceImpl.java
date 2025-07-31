package com.lunchchat.domain.member.service;

import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.member.exception.MemberException;
import com.lunchchat.domain.member.repository.MemberRepository;
import com.lunchchat.domain.user_interests.entity.Interest;
import com.lunchchat.domain.user_interests.repository.InterestRepository;
import com.lunchchat.domain.notification.service.FcmTokenCacheService;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandServiceImpl implements MemberCommandService {

    private final MemberRepository memberRepository;
    private final InterestRepository interestRepository;
    private final FcmTokenCacheService fcmTokenCacheService;

    @Override
    public void updateFcmToken(String email, String fcmToken) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new MemberException(ErrorStatus.USER_NOT_FOUND));
        member.updateFcmToken(fcmToken);

        fcmTokenCacheService.updateFcmTokenCache(member.getId(), fcmToken);
    }

    @Override
    @Transactional
    public void updateInterests(String email, List<Long> interestIds) {
        if (interestIds.size() > 3) {
            throw new MemberException(ErrorStatus.INTERESTS_LIMIT_EXCEEDED);
        }

        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new MemberException(ErrorStatus.USER_NOT_FOUND));

        List<Interest> interests = interestRepository.findAllById(interestIds);

        if (interests.size() != interestIds.size()) {
            throw new MemberException(ErrorStatus.INTERESTS_NOT_FOUND);
        }

        member.setInterests(new HashSet<>(interests));
    }
}