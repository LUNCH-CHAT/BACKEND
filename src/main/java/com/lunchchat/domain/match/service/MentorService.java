package com.lunchchat.domain.match.service;

import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MentorService {

    // private final Sheets sheetsService;

    // @Value("${google.sheet.id}")
    // private String sheetId;

    public Object getMentor() throws Exception {
        return Collections.singletonMap("message", "멘토 기능이 현재 비활성화되어 있습니다.");
    }
}