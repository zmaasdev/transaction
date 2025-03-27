package nl.zoe.transaction.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String accountId;
    @Column(columnDefinition = "DECIMAL(10, 2) DEFAULT 0")
    private BigDecimal amount;
    @Column(columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private BigDecimal balance;
    private LocalDateTime createdAt;
}
