package com.lunchchat.domain.college.service;

import com.lunchchat.domain.college.dto.CollegeResponseDTO;
import com.lunchchat.domain.college.repository.CollegeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CollegeQueryServiceImpl implements CollegeQueryService{

    private final CollegeRepository collegeRepository;

    @Override
    public List<CollegeResponseDTO> getColleges() {
        return collegeRepository.findAll()
                .stream()
                .map(college -> new CollegeResponseDTO(college.getId(), college.getName()))
                .collect(Collectors.toList());
    }
}

