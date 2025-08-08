package com.lunchchat.domain.member.dto;

import com.lunchchat.domain.user_keywords.entity.KeywordType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;

public class MemberRequestDTO {
  @Getter
  public static class UpdateInterestDTO {

    @NotNull
    @Size(max = 3, message = "최대 3개의 관심사를 선택할 수 있습니다.")
    List<Long> interestIds;
  }

  @Getter
  public static class UpdateKeywordDTO {

    @NotNull(message = "키워드 타입은 필수입니다.")
    private KeywordType type;

    @Size(max = 5, message = "title은 공백 포함 최대 5자까지 입력 가능합니다.")
    private String title;

    @Size(max = 100, message = "description은 공백 포함 최대 100자까지 입력 가능합니다.")
    private String description;
  }

  @Getter
  @Schema(
      description = "사용자 키워드 3개 입력 DTO",
      example = """
        {
          "keywords": [
            {
              "type": "EXPRESS",
              "title": "프로창업러",
              "description": "상세 설명은 최대 100자"
            },
            {
              "type": "GOAL",
              "title": "교환 준비",
              "description": "상세 설명은 최대 100자"
            },
            {
              "type": "INTEREST",
              "title": "취미 요가",
              "description": "상세 설명은 최대 100자"
            }
          ]
        }
        """
  )
  public static class UpdateKeywordListDTO {
    @Valid
    @Size(min = 3, max = 3, message = "항상 3개의 키워드를 입력해야 합니다.")
    private List<UpdateKeywordDTO> keywords;
  }

  @Getter
  public static class PresignedUrlRequest {
    @NotBlank(message = "파일 이름은 필수입니다.")
    private String fileName;
  }

  @Getter
  public static class UpdateProfileImageRequest {
    @NotBlank(message = "프로필 이미지 URL은 필수입니다.")
    private String profileImageUrl;
  }
}
