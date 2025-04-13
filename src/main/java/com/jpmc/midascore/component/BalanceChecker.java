package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class BalanceChecker {
    
    private final UserRepository userRepository;
    
    public BalanceChecker(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public Float getBalanceByName(String name) {
        for (UserRecord user : userRepository.findAll()) {
            if (user.getName().equals(name)) {
                return user.getBalance();
            }
        }
        return null;
    }
} 
 