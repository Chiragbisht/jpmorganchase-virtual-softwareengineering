package com.jpmc.midascore.controller;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BalanceController {

    private final UserRepository userRepository;

    @Autowired
    public BalanceController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/balance")
    public Balance getBalance(@RequestParam Long userId) {
        UserRecord user = userRepository.findById(userId.longValue());
        if (user != null) {
            return new Balance(user.getBalance());
        } else {
            // Return balance of 0 if user does not exist
            return new Balance(0f);
        }
    }
} 