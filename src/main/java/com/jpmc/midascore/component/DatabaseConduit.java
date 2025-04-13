package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseConduit {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public DatabaseConduit(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public void save(UserRecord userRecord) {
        userRepository.save(userRecord);
    }
    
    public void save(TransactionRecord transactionRecord) {
        transactionRepository.save(transactionRecord);
    }

    public UserRecord findUserById(long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public boolean processTransaction(TransactionRecord transaction) {
        UserRecord sender = transaction.getSender();
        UserRecord recipient = transaction.getRecipient();
        float amount = transaction.getAmount();

        // Update balances
        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount);
        
        // Save updated users
        userRepository.save(sender);
        userRepository.save(recipient);
        
        // Save transaction record
        transactionRepository.save(transaction);
        
        return true;
    }
}
