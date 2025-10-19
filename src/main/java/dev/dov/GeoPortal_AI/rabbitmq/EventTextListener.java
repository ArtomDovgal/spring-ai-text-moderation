package dev.dov.GeoPortal_AI.rabbitmq;

import dev.dov.GeoPortal_AI.ai_clients.GeminiLLMClient;
import dev.dov.GeoPortal_AI.dto.ModerationRequest;
import dev.dov.GeoPortal_AI.dto.ModerationResponse;
import dev.dov.GeoPortal_AI.dto.ModerationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventTextListener {
    private final GeminiLLMClient llmClient;
    private final AmqpTemplate amqpTemplate;

    @Value("${amqp.exchange.moderation}")
    private String moderationExchange;

    @RabbitListener(queues = "${amqp.queue.moderation.request}")
    public void handleModerationRequest(ModerationRequest request) {
        log.info("Received moderation request [{}]", request.getRequestId());

        try {

            ModerationResult result = llmClient.refactorText(request.getText());
            log.info("Moderation result: {}", result);

            ModerationResponse moderationResponse =
                    new ModerationResponse(request.getRequestId(),request.getElementType(),result);

            amqpTemplate.convertAndSend(
                    moderationExchange,
                    "moderation.text.response",
                    moderationResponse
            );

        } catch (Exception e) {
            log.error("Error while processing moderation request {}", request.getRequestId(), e);
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }
}
