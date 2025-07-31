package com.lunchchat.domain.user_interests.converter;

import com.lunchchat.domain.user_interests.dto.UserInterestResponseDTO;
import com.lunchchat.domain.user_interests.entity.Interest;
import java.util.List;

public class UserInterestConverter {

  public static UserInterestResponseDTO.UserInterestListDTO toUserInterestPreviewDTO(List<Interest> interests) {
    List<UserInterestResponseDTO.UserInterestPreviewDTO> previewList = interests.stream()
        .map(interest -> new UserInterestResponseDTO.UserInterestPreviewDTO(
            interest.getId(),
            interest.getType()))
        .toList();

    return new UserInterestResponseDTO.UserInterestListDTO(previewList);
  }

}