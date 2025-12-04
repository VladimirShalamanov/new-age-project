package app.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfiguration {

    public static final String USER_REGISTERED_EVENT_KAFKA_TOPIC_NAME = "user-registered-event.v1";

    @Bean
    public NewTopic buildNewTopic() {

        return TopicBuilder.name(USER_REGISTERED_EVENT_KAFKA_TOPIC_NAME).build();
    }
}