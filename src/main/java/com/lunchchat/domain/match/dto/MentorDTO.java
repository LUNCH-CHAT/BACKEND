package com.lunchchat.domain.match.dto;

public record MentorDTO() {

  public record MonthlyMentorDTO(
    String phone,
    String question
  ){}

}
