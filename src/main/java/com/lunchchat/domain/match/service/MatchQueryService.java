package com.lunchchat.domain.match.service;

import com.lunchchat.domain.match.dto.MatchResponseDto.MatchListDto;
import com.lunchchat.domain.match.dto.enums.MatchStatusType;

import com.lunchchat.domain.match.entity.Matches;
import java.util.List;

public interface MatchQueryService {
    List<Matches> getMatchesByStatus(MatchStatusType status, Long memberId);
    List<MatchListDto> getMatchListDtosByStatus(MatchStatusType status, String email);
}
