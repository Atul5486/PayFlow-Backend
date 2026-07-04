package com.paypal.reward_service.service;

import com.paypal.reward_service.entity.Reward;
import org.springframework.stereotype.Service;

import java.util.List;

public interface RewardService {

    Reward sendReward(Reward reward);

    List<Reward> getRewardByUserId(int userId);
}
