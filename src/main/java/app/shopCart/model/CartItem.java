package app.shopCart.model;

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
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private int count;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private ShopCart shopCart;
}