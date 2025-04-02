package nl.zoe.transaction.service;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.zoe.transaction.event.TransactionEvent;
import nl.zoe.transaction.model.Transaction;
import nl.zoe.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final MeterRegistry meterRegistry;

    public void save(TransactionEvent transactionEvent) {
        try {
            Optional<Transaction> optionalTransaction = transactionRepository.findByAccountId(transactionEvent.getAccountId());
            if (optionalTransaction.isPresent()) {
                Transaction transaction = optionalTransaction.get();
                transaction.setBalance(transaction.getBalance().add(transactionEvent.getAmount()));
                transaction.setCreatedAt(LocalDateTime.now());
                transactionRepository.save(transaction);
            } else {
                Transaction transaction = new Transaction();
                transaction.setAccountId(transactionEvent.getAccountId());
                transaction.setAmount(transactionEvent.getAmount());
                transaction.setBalance(transactionEvent.getAmount());
                transaction.setCreatedAt(LocalDateTime.now());
                transactionRepository.save(transaction);
            }
            meterRegistry.counter("transaction.count").increment();
        } catch(Exception e) {
            log.debug("Failed to save transaction", e);
            meterRegistry.counter("transaction.error").increment();
            throw new RuntimeException("Failed to save transaction", e);
        }
    }
}
