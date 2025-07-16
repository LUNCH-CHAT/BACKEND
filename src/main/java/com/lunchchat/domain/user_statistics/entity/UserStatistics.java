package com.lunchchat.domain.user_statistics.entity;

import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserStatistics extends BaseEntity {
  @Id
  private Long memberId;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "member_id")
  private Member member;

  @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
  private int matchRequestedCount;

  @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
  private int matchReceivedCount;

  @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
  private int matchCompletedCount;

  public void incrementRequested() { this.matchRequestedCount++; }
  public void incrementReceived() { this.matchReceivedCount++; }
  public void incrementCompleted() { this.matchCompletedCount++; }
}
