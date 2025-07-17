package com.lunchchat.domain.department.service;

import com.lunchchat.domain.department.dto.DepartmentResponseDTO;

import java.util.List;

public interface DepartmentQueryService {
    List<DepartmentResponseDTO> getDepartmentsByCollege(Long collegeId);
}
