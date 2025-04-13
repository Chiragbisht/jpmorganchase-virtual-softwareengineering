package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
    
    private final TransactionProcessor transactionProcessor;
    
    public KafkaConsumer(TransactionProcessor transactionProcessor) {
        this.transactionProcessor = transactionProcessor;
    }

    @KafkaListener(topics = "${general.kafka-topic}")
    public void receiveTransaction(Transaction transaction) {
        logger.info("Received transaction: {}", transaction);
        
        // Process the transaction
        boolean processed = transactionProcessor.processTransaction(transaction);
        
        if (processed) {
            logger.info("Transaction processed successfully: {}", transaction);
        } else {
            logger.warn("Failed to process transaction: {}", transaction);
        }
    }
} 