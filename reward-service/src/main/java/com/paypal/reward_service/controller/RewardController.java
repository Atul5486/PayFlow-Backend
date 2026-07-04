package com.paypal.reward_service.controller;

import com.paypal.reward_service.entity.Reward;
import com.paypal.reward_service.repository.RewardRepository;
import com.paypal.reward_service.service.RewardService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rewards/")
@CrossOrigin(origins = "http://localhost:5173")
public class RewardController {

    private RewardService rewardService;
    private RewardRepository rewardRepository;

    public RewardController(RewardService rewardService,RewardRepository rewardRepository){
        this.rewardService=rewardService;
        this.rewardRepository=rewardRepository;
    }

    @GetMapping
    public List<Reward> getAllRewards(){
        return  rewardRepository.findAll();
    }

    @GetMapping("/user/{userId}")
    public List<Reward> getRewardsBuUserId(@PathVariable int userId){
        return rewardService.getRewardByUserId(userId);
    }

}
