package com.lunchchat.domain.college.service;

import com.lunchchat.domain.college.dto.CollegeResponseDTO;
import com.lunchchat.domain.college.repository.CollegeRepository;
import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.member.exception.MemberException;
import com.lunchchat.domain.member.repository.MemberRepository;
import com.lunchchat.domain.university.entity.University;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CollegeQueryServiceImpl implements CollegeQueryService{

    private final CollegeRepository collegeRepository;
    private final MemberRepository memberRepository;

    @Override
    public List<CollegeResponseDTO> getColleges(String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new MemberException(ErrorStatus.USER_NOT_FOUND));
        Long universityId = member.getUniversity().getId();
        return collegeRepository.findByUniversityId(universityId)
                .stream()
                .map(college -> new CollegeResponseDTO(college.getId(), college.getName()))
                .collect(Collectors.toList());
    }
}

