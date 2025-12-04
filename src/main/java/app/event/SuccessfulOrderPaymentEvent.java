package app.event;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SuccessfulOrderPaymentEvent {

    private UUID userId;

    private UUID shopCartId;

    private String email;

    private BigDecimal totalPrice;

    private LocalDateTime createdOn;
}