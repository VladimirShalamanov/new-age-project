package app.order.service;

import app.order.model.Order;
import app.order.repository.OrderRepository;
import app.shopCart.model.ShopCart;
import app.shopCart.service.ShopCartService;
import app.user.model.User;
import app.user.service.UserService;
import app.utils.UserUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final ShopCartService shopCartService;

    public OrderService(OrderRepository orderRepository,
                        UserService userService,
                        ShopCartService shopCartService
    ) {

        this.orderRepository = orderRepository;
        this.userService = userService;
        this.shopCartService = shopCartService;
    }

    public void createOrderByUserId(UUID userId) {

        User user = userService.getById(userId);
        ShopCart shopCart = shopCartService.getShopCartByUserOwnerId(userId);

        int totalCount = shopCartService.getTotalItemsCount(shopCart);
        BigDecimal totalPrice = shopCartService.getTotalItemsPrice(shopCart);
        String fullNames = UserUtils.buildFullName(user);
        String fullAddress = UserUtils.buildFullAddress(user);

        Order order = Order.builder()
                .totalCount(totalCount)
                .totalPrice(totalPrice)
                .email(user.getEmail())
                .fullNames(fullNames)
                .fullAddress(fullAddress)
                .createdOn(LocalDateTime.now())
                .owner(user)
                .build();

        orderRepository.save(order);
    }
}