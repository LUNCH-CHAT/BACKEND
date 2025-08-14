package com.lunchchat.domain.college.controller;

import com.lunchchat.domain.college.dto.CollegeResponseDTO;
import com.lunchchat.domain.college.service.CollegeQueryService;
import com.lunchchat.global.security.auth.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import com.lunchchat.global.apiPayLoad.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/colleges")
public class CollegeController {

    private final CollegeQueryService collegeQueryService;

    @GetMapping
    @Operation(summary = "단과대 목록 조회", description = "단과대 목록을 조회합니다.")
    public ApiResponse<List<CollegeResponseDTO>> getColleges(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String memberEmail = userDetails.getUsername();
        List<CollegeResponseDTO> colleges = collegeQueryService.getColleges(memberEmail);
        return ApiResponse.onSuccess(colleges);
    }
}

