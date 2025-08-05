package com.lunchchat.domain.member.service;

import com.lunchchat.domain.member.dto.MemberRequestDTO;
import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.member.exception.MemberException;
import com.lunchchat.domain.member.repository.MemberRepository;
import com.lunchchat.domain.user_interests.entity.Interest;
import com.lunchchat.domain.user_interests.repository.InterestRepository;
import com.lunchchat.domain.notification.service.FcmTokenCacheService;
import com.lunchchat.domain.user_keywords.converter.UserKeywordsConverter;
import com.lunchchat.domain.user_keywords.entity.KeywordType;
import com.lunchchat.domain.user_keywords.entity.UserKeyword;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
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

        Map<KeywordType, MemberRequestDTO.UpdateKeywordDTO> requestMap =
            keywordsDto.getKeywords().stream()
                .peek(dto -> {
                    if (dto.getType() == null) {
                        throw new MemberException(ErrorStatus.KEYWORD_TYPE_REQUIRED);
                    }
                })
                .collect(Collectors.toMap(MemberRequestDTO.UpdateKeywordDTO::getType, Function.identity()));

        // 중복 타입 검사
        if (requestMap.size() != keywordsDto.getKeywords().size()) {
            throw new MemberException(ErrorStatus.DUPLICATE_KEYWORD_TYPE);
        }

        Map<KeywordType, UserKeyword> currentMap = member.getUserKeywords().stream()
            .collect(Collectors.toMap(UserKeyword::getType, Function.identity()));

        for (KeywordType type : KeywordType.values()) {
            MemberRequestDTO.UpdateKeywordDTO dto = requestMap.get(type);
            UserKeyword existing = currentMap.get(type);

            if (dto != null && existing != null) { // 수정
                existing.updateTitle(dto.getTitle());
                existing.updateDescription(dto.getDescription());
            } else if (dto != null) { // 새로 추가
                UserKeyword newKeyword = UserKeywordsConverter.toEntity(member, dto);
                member.getUserKeywords().add(newKeyword);
            } else if (existing != null) { // 삭제
                member.getUserKeywords().remove(existing);
            }
        }
    }
}