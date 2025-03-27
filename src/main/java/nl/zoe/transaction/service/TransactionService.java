package nl.zoe.transaction.service;

import lombok.RequiredArgsConstructor;
import nl.zoe.transaction.event.TransactionEvent;
import nl.zoe.transaction.model.Transaction;
import nl.zoe.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public void save(TransactionEvent transactionEvent) {
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
    }
}
