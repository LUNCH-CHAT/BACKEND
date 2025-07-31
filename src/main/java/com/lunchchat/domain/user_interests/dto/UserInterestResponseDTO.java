package com.lunchchat.domain.user_interests.dto;

import com.lunchchat.domain.member.entity.enums.InterestType;
import java.util.List;

public class UserInterestResponseDTO {

  public record UserInterestListDTO(List<UserInterestPreviewDTO> interests) {}

  public record UserInterestPreviewDTO(Long id, InterestType interestType) {}

}