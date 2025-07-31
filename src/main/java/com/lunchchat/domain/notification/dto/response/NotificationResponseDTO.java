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
    private String senderMembername;
    private String senderProfileImageUrl;
    
    @JsonFormat(pattern = "M/d HH:mm")
    private LocalDateTime createdAt;
    
    public NotificationResponseDTO(Notification notification) {
        this.id = notification.getId();
        this.type = notification.getType();
        this.content = notification.getContent();
        this.isRead = notification.getIsRead();
        this.createdAt = notification.getCreatedAt();
        
        if (notification.getSender() != null) {
            this.senderMembername = notification.getSender().getMembername();
            this.senderProfileImageUrl = "/api/files/default-profile.png";
        } else {
            this.senderMembername = "시스템";
            this.senderProfileImageUrl = "/api/files/default-profile.png";
        }
    }
}
