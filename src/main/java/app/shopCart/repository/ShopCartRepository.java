package app.shopCart.repository;

import app.shopCart.model.ShopCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShopCartRepository extends JpaRepository<ShopCart, UUID> {
}