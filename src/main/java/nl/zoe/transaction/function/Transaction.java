package nl.zoe.transaction.function;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.zoe.transaction.event.TransactionEvent;
import nl.zoe.transaction.service.TransactionService;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
@Component
public class Transaction {

    private final TransactionService transactionService;

    @Bean
    public Consumer<Message<TransactionEvent>> poll() {
        return message -> {
            log.debug("Consuming message: {}", message);
            TransactionEvent transactionEvent = message.getPayload();
            transactionService.save(transactionEvent);
        };
    }
}
