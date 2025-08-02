package com.lunchchat.domain.match.dto;

public record MentorDTO() {

  public record MonthlyMentorDTO(
    String phone,
    String question,
    String university,
    String email,
    String department,
    String studentNo,
    String name
  ){}

}
