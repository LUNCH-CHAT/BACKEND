package com.lunchchat.domain.match.service;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MentorService {

    private final Sheets sheets;

    public MentorService(Sheets sheets) {
        this.sheets = sheets;
    }

    @Value("${google.sheet.id}")
    private String sheetId;

    //
    public void appendRow(List<Object> rowData) {
        ValueRange body = createValueRange(rowData);
        // 엑셀 내 "이달의멘토" 탭에 작성
        writeToSheet("이달의멘토", body);
    }

    //2차원 리스트로 변경
    private ValueRange createValueRange(List<Object> rowData) {
        return new ValueRange()
            .setValues(List.of(rowData));
    }

    //구글 시트에 값 추가
    private void writeToSheet(String sheetName, ValueRange valueRange) {
        try {
            sheets.spreadsheets().values()
                //!A1 = 시작점, valueRange = 데이터
                .append(sheetId, sheetName + "!A1", valueRange)
                .setValueInputOption("USER_ENTERED")
                .execute();
        } catch (IOException e) {
            throw new RuntimeException("엑셀에 추가하지 못했습니다", e);
        }
    }
}
