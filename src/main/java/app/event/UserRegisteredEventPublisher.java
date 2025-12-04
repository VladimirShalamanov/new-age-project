package app.event;

import app.event.payload.UserRegisteredEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static app.config.KafkaConfiguration.USER_REGISTERED_EVENT_KAFKA_TOPIC_NAME;

@Slf4j
@Component
public class UserRegisteredEventPublisher {

    private KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate;

    @Autowired
    public UserRegisteredEventPublisher(KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(UserRegisteredEvent event) {

        kafkaTemplate.send(USER_REGISTERED_EVENT_KAFKA_TOPIC_NAME, event);

        log.info("Successfully sent event to topic=[{}] for user with id=[{}]",
                USER_REGISTERED_EVENT_KAFKA_TOPIC_NAME, event.getUserId());
    }
}
