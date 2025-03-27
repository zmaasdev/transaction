package nl.zoe.transaction;

import nl.zoe.transaction.event.TransactionEvent;
import nl.zoe.transaction.model.Transaction;
import nl.zoe.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
        "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer",
        "spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer",
        "spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer",
        "spring.kafka.consumer.properties.spring.json.trusted.packages=*"
})
@Testcontainers
public class TransactionConsumerTest {

    @Container
    static final KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("apache/kafka:latest")
    );

    @DynamicPropertySource
    static void registerKafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    public void whenMessageSentThenConsumeMessage() {
        String accountId = "test_account_id";
        TransactionEvent event = new TransactionEvent(accountId, new BigDecimal("1000"), LocalDateTime.now());
        kafkaTemplate.send("transaction-topic", event);

        await()
            .pollInterval(Duration.ofSeconds(3))
            .atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                Optional<Transaction> optionalTransaction = transactionRepository.findByAccountId(accountId);
                assertThat(optionalTransaction).isPresent();
                assertThat(optionalTransaction.get().getAccountId()).isEqualTo(accountId);
                assertThat(optionalTransaction.get().getAmount()).isEqualTo(new BigDecimal("1000.00"));
                assertThat(optionalTransaction.get().getBalance()).isEqualTo(new BigDecimal("1000.00"));
                assertThat(optionalTransaction.get().getCreatedAt()).isNotNull();
            });

    }
}
