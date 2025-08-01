package com.lunchchat.domain.member.service;

import com.lunchchat.domain.member.dto.MemberRequestDTO;
import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.member.exception.MemberException;
import com.lunchchat.domain.member.repository.MemberRepository;
import com.lunchchat.domain.user_interests.entity.Interest;
import com.lunchchat.domain.user_interests.repository.InterestRepository;
import com.lunchchat.domain.notification.service.FcmTokenCacheService;
import com.lunchchat.domain.user_keywords.entity.KeywordType;
import com.lunchchat.domain.user_keywords.entity.UserKeyword;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    @Override
    @Transactional
    public void updateKeywords(String email, MemberRequestDTO.UpdateKeywordListDTO keywordsDto) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new MemberException(ErrorStatus.USER_NOT_FOUND));

        List<MemberRequestDTO.UpdateKeywordDTO> keywords = keywordsDto.getKeywords();

        // 중복 타입 검사
        Set<KeywordType> types = new HashSet<>();
        for (MemberRequestDTO.UpdateKeywordDTO dto : keywords) {
            if (!types.add(dto.getType())) {
                throw new MemberException(ErrorStatus.DUPLICATE_KEYWORD_TYPE);
            }
        }

        // 기존 키워드 삭제
        member.clearKeywords();

        // 새 키워드 추가
        member.addKeywords(keywords);
    }
}