package app.order.service;

import app.event.SuccessfulOrderPaymentEvent;
import app.notification.service.NotificationService;
import app.order.model.Order;
import app.order.repository.OrderRepository;
import app.shopCart.model.ShopCart;
import app.shopCart.service.ShopCartService;
import app.user.model.User;
import app.user.service.UserService;
import app.utils.ShopCartUtils;
import app.utils.UserUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final ApplicationEventPublisher eventPublisher;
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final ShopCartService shopCartService;
    private final NotificationService notificationService;

    public OrderService(ApplicationEventPublisher eventPublisher,
                        OrderRepository orderRepository,
                        UserService userService,
                        ShopCartService shopCartService,
                        NotificationService notificationService) {

        this.eventPublisher = eventPublisher;
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.shopCartService = shopCartService;
        this.notificationService = notificationService;
    }

    public List<Order> getAllOrders() {

        return orderRepository.findAll();
    }

    public List<Order> getAllOrdersByOwnerId(UUID ownerId) {

        return orderRepository.findAllByOwnerId(ownerId);
    }

    public void createOrderByUserId(UUID userId) {

        User user = userService.getById(userId);
        ShopCart shopCart = shopCartService.getShopCartByUserOwnerId(userId);

        int totalCount = ShopCartUtils.getTotalItemsCount(shopCart);
        BigDecimal totalPrice = ShopCartUtils.getTotalItemsPrice(shopCart);
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

        SuccessfulOrderPaymentEvent event = SuccessfulOrderPaymentEvent.builder()
                .userId(user.getId())
                .shopCartId(shopCart.getId())
                .email(user.getEmail())
                .totalPrice(totalPrice)
                .createdOn(LocalDateTime.now())
                .build();
        eventPublisher.publishEvent(event);

        String formattedDate = order.getCreatedOn().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"));
        String subject = "Successful order payment.";
        String body = ("Hello %s,\n" +
                "You have successfully completed a payment of %.2f EUR.\n" +
                "Your order was created on %s with number [%s]. It will soon be successfully delivered to %s.")
                .formatted(order.getFullNames(), order.getTotalPrice(), formattedDate,
                        order.getId(), order.getFullAddress());

        notificationService.sendEmail(userId, subject, body);
    }
}