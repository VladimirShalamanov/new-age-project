package app.shopCart.model;

import app.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ShopCart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User owner;

    @OneToMany(mappedBy = "shopCart", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdOn ASC")
    private List<CartItem> items = new ArrayList<>();
}