package com.jpmc.midascore;

import com.jpmc.midascore.component.BalanceChecker;
import com.jpmc.midascore.foundation.Balance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BalanceQuerier {
    
    @Autowired
    private BalanceChecker balanceChecker;
    
    public Float getWaldorfBalance() {
        return getUserBalance("waldorf");
    }
    
    public Float getUserBalance(String name) {
        return balanceChecker.getBalanceByName(name);
    }
    
    public Balance query(Long id) {
        // You can query actual balance by ID from database
        return new Balance(getUserBalance("waldorf") != null ? getUserBalance("waldorf") : 0f);
    }
}
