package com.lunchchat.domain.user_interests.service;

import com.lunchchat.domain.user_interests.entity.Interest;
import java.util.List;

public interface UserInterestQueryService {
  List<Interest> getInterests();
}