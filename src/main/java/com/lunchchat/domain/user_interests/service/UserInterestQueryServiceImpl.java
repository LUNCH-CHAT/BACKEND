package com.lunchchat.domain.user_interests.service;

import com.lunchchat.domain.user_interests.entity.Interest;
import com.lunchchat.domain.user_interests.repository.InterestRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInterestQueryServiceImpl implements UserInterestQueryService{

  private final InterestRepository interestRepository;

  @Override
  public List<Interest> getInterests() {
    return interestRepository.findAll();
  }

}
