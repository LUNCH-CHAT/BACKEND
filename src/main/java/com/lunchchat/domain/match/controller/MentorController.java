package com.lunchchat.domain.match.controller;

import com.lunchchat.domain.match.dto.MentorDTO;
import com.lunchchat.domain.match.service.MentorService;
import com.lunchchat.global.apiPayLoad.ApiResponse;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mentor")
public class MentorController{

  private final MentorService mentorService;

  public MentorController(MentorService mentorService) {
    this.mentorService = mentorService;
  }

  @PostMapping("/monthlyM")
  public ApiResponse<String> submit(@RequestBody MentorDTO.MonthlyMentorDTO dto) {
    mentorService.appendRow(List.of(
        dto.phone(),
        dto.question(),
        dto.university(),
        dto.email(),
        dto.department(),
        dto.studentNo(),
        dto.name()
    ));
    return ApiResponse.onSuccess("엑셀 작성 완료");
  }

}
