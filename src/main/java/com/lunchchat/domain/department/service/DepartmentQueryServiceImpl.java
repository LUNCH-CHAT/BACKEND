package com.lunchchat.domain.department.service;

import com.lunchchat.domain.department.dto.DepartmentResponseDTO;
import com.lunchchat.domain.department.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentQueryServiceImpl implements DepartmentQueryService {

    private final DepartmentRepository departmentRepository;

    @Override
    public List<DepartmentResponseDTO> getDepartmentsByCollege(Long collegeId) {
        return departmentRepository.findByCollegeId(collegeId)
                .stream()
                .map(department -> new DepartmentResponseDTO(department.getId(), department.getName()))
                .collect(Collectors.toList());
    }
}
