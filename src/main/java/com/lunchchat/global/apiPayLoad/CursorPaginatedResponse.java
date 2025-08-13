package com.lunchchat.global.apiPayLoad;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CursorPaginatedResponse<T> {
    private Long userId;
    private Long friendId;
    private List<T> data;
    private CursorMeta meta;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class CursorMeta {
        private int pageSize;
        private boolean hasNext;
        private String nextCursor; //프론트 반환
    }

}
