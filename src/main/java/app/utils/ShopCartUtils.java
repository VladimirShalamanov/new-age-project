package app.utils;

import app.shopCart.model.CartItem;
import app.shopCart.model.ShopCart;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class ShopCartUtils {

    public static int getTotalItemsCount(ShopCart shopCart) {

        return shopCart.getItems()
                .stream()
                .mapToInt(CartItem::getCount)
                .sum();
    }

    public static BigDecimal getTotalItemsPrice(ShopCart shopCart) {

        return shopCart.getItems()
                .stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}