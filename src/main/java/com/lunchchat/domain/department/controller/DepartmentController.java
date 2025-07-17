package com.lunchchat.domain.department.controller;

import com.lunchchat.domain.department.dto.DepartmentResponseDTO;
import com.lunchchat.domain.department.service.DepartmentQueryService;
import com.lunchchat.global.apiPayLoad.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/colleges")
public class DepartmentController {

    private final DepartmentQueryService departmentQueryService;

    @GetMapping("/{collegeId}/departments")
    @Operation(summary = "단과대에 속한 학과 목록 조회", description = "단과대 ID로 학과 목록을 조회합니다.")
    public ApiResponse<List<DepartmentResponseDTO>> getDepartmentsByCollege(@PathVariable Long collegeId) {
        return ApiResponse.onSuccess(departmentQueryService.getDepartmentsByCollege(collegeId));
    }
}
