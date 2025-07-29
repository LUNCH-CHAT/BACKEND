package com.lunchchat.domain.notification.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lunchchat.domain.notification.entity.Notification;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NotificationResponseDTO {
    
    private Long id;
    private String type;
    private String content;
    private Boolean isRead;
    private String senderMembername; // nickname → membername
    private String senderProfileImageUrl;
    
    @JsonFormat(pattern = "M/d HH:mm")
    private LocalDateTime createdAt;
    
    public NotificationResponseDTO(Notification notification) {
        this.id = notification.getId();
        this.type = notification.getType();
        this.content = notification.getContent();
        this.isRead = notification.getIsRead();
        this.createdAt = notification.getCreatedAt();
        
        // 발신자 정보
        if (notification.getSender() != null) {
            this.senderMembername = notification.getSender().getMembername(); // nickname → membername
            this.senderProfileImageUrl = "/api/files/default-profile.png"; // TODO: 실제 프로필 이미지 URL
        } else {
            this.senderMembername = "시스템";
            this.senderProfileImageUrl = "/api/files/default-profile.png";
        }
    }
}
