package com.lunchchat.domain.member.entity.enums;

public enum LoginType {
  LChat("Lchat"), Google("Google");

  private String code;

  private LoginType(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  public static LoginType fromCode(String code) {
    for (LoginType type : values()) {
      if (type.getCode().equals(code)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown LoginType: " + code);
  }

}
