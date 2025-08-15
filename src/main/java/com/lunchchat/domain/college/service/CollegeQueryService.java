package com.lunchchat.domain.college.service;

import com.lunchchat.domain.college.dto.CollegeResponseDTO;

import java.util.List;

public interface CollegeQueryService {
    List<CollegeResponseDTO> getColleges(String memberEmail);
}
