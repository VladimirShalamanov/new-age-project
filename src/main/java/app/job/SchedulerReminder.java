package app.job;

import app.email.EmailService;
import app.shopCart.model.ShopCart;
import app.shopCart.service.ShopCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SchedulerReminder {

    private final ShopCartService shopCartService;
    private final EmailService emailService;

    @Autowired
    public SchedulerReminder(ShopCartService shopCartService, EmailService emailService) {

        this.shopCartService = shopCartService;
        this.emailService = emailService;
    }

    @Async
    @Scheduled(fixedDelay = 3_600_000)
    public void sendReminderToUsers() throws InterruptedException {

        List<ShopCart> usersFullShopCart = shopCartService.getAllEmptyShopCarts();

        if (!usersFullShopCart.isEmpty()) {

            usersFullShopCart.forEach(emailService::sendReminderEmailUncompletedUserOrder);
        }
    }

    @Async
    @Scheduled(cron = "0 0 7 * * MON,WED,FRI")
    public void sendReminderPromotion() throws InterruptedException {

        emailService.sendReminderEmailCheckPromotions();
    }
}