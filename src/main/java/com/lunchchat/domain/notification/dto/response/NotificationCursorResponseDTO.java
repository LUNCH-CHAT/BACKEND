package com.lunchchat.domain.notification.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationCursorResponseDTO {

    private List<NotificationResponseDTO> notifications;
    private Long lastNotificationId;
    private boolean hasNext;
    private int size;

    public static NotificationCursorResponseDTO of(List<NotificationResponseDTO> notifications,
        boolean hasNext) {
        Long lastId = notifications.isEmpty() ? null :
            notifications.get(notifications.size() - 1).getId();

        return new NotificationCursorResponseDTO(
            notifications,
            lastId,
            hasNext,
            notifications.size()
        );
    }
}
