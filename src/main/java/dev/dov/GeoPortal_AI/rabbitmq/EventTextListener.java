package dev.dov.GeoPortal_AI.rabbitmq;

import dev.dov.GeoPortal_AI.ai_clients.GeminiLLMClient;
import dev.dov.GeoPortal_AI.dto.ModerationCategory;
import dev.dov.GeoPortal_AI.dto.ModerationRequest;
import dev.dov.GeoPortal_AI.dto.ModerationResponse;
import dev.dov.GeoPortal_AI.dto.ModerationResult;
import dev.dov.GeoPortal_AI.services.ProfanityFilter;
import dev.dov.GeoPortal_AI.services.SimilarityFilter;
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
    private final ProfanityFilter profanityFilter;
    private final SimilarityFilter similarityFilter;

    @Value("${amqp.exchange.moderation}")
    private String moderationExchange;

    @RabbitListener(queues = "${amqp.queue.moderation.request}")
    public void handleModerationRequest(ModerationRequest request) {

        log.info("Received moderation request [{}]", request.getRequestId());
        String text = request.getText();

        //перевірка спочатку на нецензурщину
        try {
            if (profanityFilter.containsProfanity(text)) {

                log.info("Text [{}] flagged by ProfanityFilter", request.getRequestId());

                sendResponse(request, new ModerationResult(
                        0.9, 0.0, 0.0,
                        ModerationCategory.TOXIC,
                        "Текст містить нецензурщину"
                ));

                return;
            }

            if (similarityFilter.containsSimilar(text)) {

                log.info("Text [{}] flagged by SimilarityFilter", request.getRequestId());

                sendResponse(request, new ModerationResult(
                        0.7, 0.0, 0.0,
                        ModerationCategory.TOXIC,
                        "Текст містить слова схожі на нецензурщину"
                ));

                return;
            }

            // Якщо нецензурщини не знайдено, то перевіряємо за допомогою LLM
            ModerationResult result = llmClient.refactorText(text);
            log.info("Moderation result: {}", result);

            sendResponse(request, result);

        } catch (Exception e) {
            log.error("Error while processing moderation request {}", request.getRequestId(), e);
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }

    private void sendResponse(ModerationRequest request, ModerationResult result) {
        ModerationResponse moderationResponse =
                new ModerationResponse(request.getRequestId(), request.getElementType(), result);

        amqpTemplate.convertAndSend(
                moderationExchange,
                "moderation.text.response",
                moderationResponse
        );
    }
}
