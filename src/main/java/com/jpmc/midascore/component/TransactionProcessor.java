package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TransactionProcessor {
    private static final Logger logger = LoggerFactory.getLogger(TransactionProcessor.class);
    
    private final DatabaseConduit databaseConduit;
    private final RestTemplate restTemplate;
    private static final String INCENTIVE_API_URL = "http://localhost:8080/incentive";

    @Autowired
    public TransactionProcessor(DatabaseConduit databaseConduit, RestTemplate restTemplate) {
        this.databaseConduit = databaseConduit;
        this.restTemplate = restTemplate;
    }

    public boolean processTransaction(Transaction transaction) {
        // Validate the transaction
        if (!isValid(transaction)) {
            logger.warn("Invalid transaction: {}", transaction);
            return false;
        }

        // Get user records
        UserRecord sender = databaseConduit.findUserById(transaction.getSenderId());
        UserRecord recipient = databaseConduit.findUserById(transaction.getRecipientId());
        
        // Check if sender has sufficient balance
        if (sender.getBalance() < transaction.getAmount()) {
            logger.warn("Insufficient balance for transaction: {}", transaction);
            return false;
        }
        
        // Call incentive API
        float incentiveAmount = 0f;
        try {
            Incentive incentive = restTemplate.postForObject(INCENTIVE_API_URL, transaction, Incentive.class);
            if (incentive != null) {
                incentiveAmount = incentive.getAmount();
                logger.info("Got incentive amount: {}", incentiveAmount);
            }
        } catch (Exception e) {
            logger.error("Failed to get incentive: {}", e.getMessage());
            // Continue processing even if incentive API fails
        }
        
        // Create transaction record with incentive
        TransactionRecord transactionRecord = new TransactionRecord(
            sender, 
            recipient, 
            transaction.getAmount(),
            incentiveAmount
        );
        
        // Process the transaction, including incentive
        boolean result = processTransactionWithIncentive(transactionRecord);
        logger.info("Transaction processed successfully: {}", transaction);
        return result;
    }
    
    private boolean processTransactionWithIncentive(TransactionRecord transaction) {
        UserRecord sender = transaction.getSender();
        UserRecord recipient = transaction.getRecipient();
        float transactionAmount = transaction.getAmount();
        float incentiveAmount = transaction.getIncentive();
        
        // Update balances - deduct amount from sender
        sender.setBalance(sender.getBalance() - transactionAmount);
        
        // Add transaction amount + incentive to recipient
        recipient.setBalance(recipient.getBalance() + transactionAmount + incentiveAmount);
        
        // Save updated users
        databaseConduit.save(sender);
        databaseConduit.save(recipient);
        
        // Save transaction record
        databaseConduit.save(transaction);
        
        return true;
    }
    
    private boolean isValid(Transaction transaction) {
        // Validate sender exists
        UserRecord sender = databaseConduit.findUserById(transaction.getSenderId());
        if (sender == null) {
            logger.warn("Sender not found: {}", transaction.getSenderId());
            return false;
        }
        
        // Validate recipient exists
        UserRecord recipient = databaseConduit.findUserById(transaction.getRecipientId());
        if (recipient == null) {
            logger.warn("Recipient not found: {}", transaction.getRecipientId());
            return false;
        }
        
        // Validate amount
        if (transaction.getAmount() <= 0) {
            logger.warn("Invalid amount: {}", transaction.getAmount());
            return false;
        }
        
        return true;
    }
} 