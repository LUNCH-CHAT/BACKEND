package com.lunchchat.domain.match.controller;

import com.lunchchat.domain.match.dto.MentorDTO;
import com.lunchchat.domain.match.service.MentorService;
import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.member.repository.MemberRepository;
import com.lunchchat.global.apiPayLoad.ApiResponse;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import com.lunchchat.global.apiPayLoad.exception.AuthException;
import com.lunchchat.global.security.auth.dto.CustomUserDetails;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mentor")
public class MentorController {

    private final MentorService mentorService;
    private final MemberRepository memberRepository;

    public MentorController(MentorService mentorService, MemberRepository memberRepository) {
        this.mentorService = mentorService;
        this.memberRepository = memberRepository;
    }

    @PostMapping("/monthlyM")
    public ApiResponse<String> submit(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestBody MentorDTO.MonthlyMentorDTO dto
    ) {
        String email = userDetails.getUsername();
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new AuthException(ErrorStatus.USER_NOT_FOUND));

        String interestNames = member.getInterests()
            .stream()
            .map(interest -> interest.getType().name())
            .collect(Collectors.joining(", "));

        mentorService.appendRow(List.of(
            dto.phone(),
            dto.question(),
            member.getUniversity().getName(),
            member.getEmail(),
            member.getDepartment().getName(),
            member.getStudentNo(),
            member.getMembername(),
            interestNames
        ));

        return ApiResponse.onSuccess("엑셀 작성 완료");
    }

}
