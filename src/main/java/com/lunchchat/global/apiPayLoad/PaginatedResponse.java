package com.lunchchat.global.apiPayLoad;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedResponse<T> {
    private List<T> data;
    private Meta meta;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Meta {
        private int currentPage;
        private int pageSize;
        private long totalItems;
        private int totalPages;
        private boolean hasNext;
    }
}
