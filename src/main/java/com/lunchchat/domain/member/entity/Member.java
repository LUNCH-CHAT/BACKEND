package com.lunchchat.domain.member.entity;

import com.lunchchat.domain.college.entity.College;
import com.lunchchat.domain.department.entity.Department;
import com.lunchchat.domain.user_keywords.entity.UserKeyword;
import com.lunchchat.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String membername;

  private String email;

  private String nickname;

  private Long studentNo;

  @Enumerated(EnumType.STRING)
  private LoginType loginType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "college_id")
  private College college;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "department_id")
  private Department department;

  @Column(columnDefinition = "TEXT")
  private String profileIntro;

  //생성자
  public Member(String email, LoginType loginType) {
    this.email = email;
    this.loginType = loginType;
  }

  //setter
  public void changeNickname(String nickname) {
    this.nickname = nickname;
  }

  public void changeDepartment(Department department) {
    this.department = department;
  }

  public void updateProfileIntro(String profileIntro) {
    this.profileIntro = profileIntro;
  }

}
