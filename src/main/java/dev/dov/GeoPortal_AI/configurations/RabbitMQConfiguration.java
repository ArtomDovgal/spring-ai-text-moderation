package dev.dov.GeoPortal_AI.configurations;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

    // Exchange
    @Bean
    public TopicExchange moderationExchange(
            @Value("${amqp.exchange.moderation}") String name) {
        return ExchangeBuilder.topicExchange(name).durable(true).build();
    }

    // Черги модерації
    @Bean
    public Queue moderationRequestQueue(
            @Value("${amqp.queue.moderation.request}") String queueName) {
        return QueueBuilder.durable(queueName).maxLength(2000).build();
    }
    @Bean
    public Queue moderationResponseQueue(
            @Value("${amqp.queue.moderation.response}") String queueName) {
        return QueueBuilder.durable(queueName).maxLength(2000).build();
    }

    // Зв'язування
    @Bean
    public Binding bindRequests(Queue moderationRequestQueue, TopicExchange moderationExchange) {
        return BindingBuilder.bind(moderationRequestQueue)
                .to(moderationExchange)
                .with("moderation.text.request");
    }
    @Bean
    public Binding bindResponse(Queue moderationResponseQueue, TopicExchange moderationExchange) {
        return BindingBuilder.bind(moderationResponseQueue)
                .to(moderationExchange)
                .with("moderation.text.response");
    }

    //
    @Bean
    public Jackson2JsonMessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
