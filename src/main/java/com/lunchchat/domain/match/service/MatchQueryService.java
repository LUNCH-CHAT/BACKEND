package com.lunchchat.domain.match.service;

import com.lunchchat.domain.match.dto.MatchResponseDto;
import com.lunchchat.domain.match.dto.MatchResponseDto.MatchListDto;
import com.lunchchat.domain.match.dto.enums.MatchStatusType;

import com.lunchchat.domain.match.entity.Matches;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface MatchQueryService {
    Page<Matches> getMatchesByStatus(MatchStatusType status, Long memberId, PageRequest pageable);
    MatchResponseDto.MatchListPageDto getMatchListDtosByStatus(MatchStatusType status, String email, int page);
}
