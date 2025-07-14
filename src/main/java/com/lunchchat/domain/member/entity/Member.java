package com.lunchchat.domain.member.entity;

import com.lunchchat.domain.college.entity.College;
import com.lunchchat.domain.department.entity.Department;
import com.lunchchat.domain.member.entity.enums.LoginType;
import com.lunchchat.domain.member.entity.enums.MemberStatus;
import com.lunchchat.domain.university.entity.University;
import com.lunchchat.domain.user_interests.entity.UserInterests;
import com.lunchchat.domain.user_keywords.entity.UserKeyword;
import com.lunchchat.global.common.BaseEntity;
import jakarta.persistence.CascadeType;
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
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  //실명
  @Column(nullable = false, unique = true)
  private String membername;

  //이메일
  @Column(nullable = false,unique = true)
  private String email;

  //닉네임
  private String nickname;

  //학번
  private String studentNo;

  //프로필 사진
  private String profileImageUrl;

  //로그인 타입
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private LoginType loginType;

  //로그인 상태
  @Enumerated(EnumType.STRING)
  private MemberStatus Status;

  //대학
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "university_id")
  private University university;

  //단과대
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "college_id")
  private College college;

  //학과
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "department_id")
  private Department department;

  @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<UserKeyword> userKeywords = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<UserInterests> userInterests = new ArrayList<>();

  //생성자
  public Member(String email, LoginType loginType) {
    this.email = email;
    this.loginType = loginType;
  }

  //setter
  public void updateNickname(String nickname) {
    this.nickname = nickname;
  }

  public void updateCollege(College college) {
    this.college = college;
  }

  public void updateDepartment(Department department) {
    this.department = department;
  }
}
