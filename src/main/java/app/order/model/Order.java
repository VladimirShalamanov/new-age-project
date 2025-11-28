package app.order.model;

import app.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int totalCount;

    private BigDecimal totalPrice;

    private String email;

    private String fullNames;

    private String fullAddress;

    private LocalDateTime createdOn;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;
}