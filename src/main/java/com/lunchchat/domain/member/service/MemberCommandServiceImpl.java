package com.lunchchat.domain.member.service;

import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.member.exception.MemberException;
import com.lunchchat.domain.member.repository.MemberRepository;
import com.lunchchat.domain.notification.service.FcmTokenCacheService;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandServiceImpl implements MemberCommandService {

    private final MemberRepository memberRepository;
    private final FcmTokenCacheService fcmTokenCacheService;

    @Override
    public void updateFcmToken(String email, String fcmToken) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new MemberException(ErrorStatus.USER_NOT_FOUND));
        member.updateFcmToken(fcmToken);

        fcmTokenCacheService.updateFcmTokenCache(member.getId(), fcmToken);
    }
}
