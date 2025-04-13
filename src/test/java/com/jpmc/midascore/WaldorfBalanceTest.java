package com.jpmc.midascore;

import com.jpmc.midascore.component.BalanceChecker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = {"transactions-topic"})
public class WaldorfBalanceTest {

    @Autowired
    private BalanceChecker balanceChecker;
    
    @Autowired
    private UserPopulator userPopulator;
    
    @Autowired
    private FileLoader fileLoader;
    
    @Autowired
    private KafkaProducer kafkaProducer;

    @Test
    public void testWaldorfBalance() throws InterruptedException {
        // First populate the users
        userPopulator.populate();
        
        // Send the transactions
        String[] transactionLines = fileLoader.loadStrings("/test_data/mnbvcxz.vbnm");
        for (String transactionLine : transactionLines) {
            kafkaProducer.send(transactionLine);
        }
        
        // Let kafka consumer process all transactions
        Thread.sleep(5000);
        
        // Get waldorf's balance
        Float balance = balanceChecker.getBalanceByName("waldorf");
        
        // Print the balance
        System.out.println("==============================================");
        System.out.println("WALDORF'S BALANCE IS: " + balance);
        System.out.println("==============================================");
        
        // Assert the balance is correct
        assertEquals(1770.0f, balance, 0.001f, "Waldorf's balance should be 1770.0 after all transactions");
    }
} 