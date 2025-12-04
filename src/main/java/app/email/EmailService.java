package app.email;

import app.event.SuccessfulOrderPaymentEvent;
import app.shopCart.model.ShopCart;
import app.user.model.User;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class EmailService {

    @Async
    @EventListener
    public void sendEmail(SuccessfulOrderPaymentEvent event) {

        System.out.println("Thread in EmailService.java: " + Thread.currentThread().getName());

        System.out.printf("Sending Email for new Order Payment for user with [%s].\n", event.getEmail());
    }

    public void sendReminderEmailUncompletedUserOrder(ShopCart cart) {

        User owner = cart.getOwner();
        String itemName = cart.getItems().stream().findFirst().get().getName();

        System.out.printf("[%s] Email sent to [%s] with username [%s]. " +
                        "You have uncompleted order! This product [%s] is waiting for you!\n",
                LocalTime.now(), owner.getRole(), owner.getUsername(), itemName);
    }

    public void sendReminderEmailCheckPromotions() {

        System.out.println("Check out the latest promotions!");
    }
}