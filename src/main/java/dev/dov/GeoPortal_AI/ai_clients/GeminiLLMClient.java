package dev.dov.GeoPortal_AI.ai_clients;

import dev.dov.GeoPortal_AI.dto.ModerationCategory;
import dev.dov.GeoPortal_AI.dto.ModerationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GeminiLLMClient implements LLMClient {

    private final ChatClient chatClient;

    public GeminiLLMClient(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    private static final String SYSTEM_PROMPT = """
        You are a content moderation assistant for a city service platform.
        Analyze the user's text for the following aspects:
        1. Toxicity — detect insults, hate speech, harassment (0..1)
        2. Off-topic — unrelated or irrelevant content (0..1)
            Off-topic includes commercial ads (e.g., "Продам авто", "Здам квартиру"),
            job offers, buying/selling, personal announcements, and anything not
            related to reporting city infrastructure problems.
        3. PII leak — personal data exposure (phone, address, email, ID, card) (0..1)

        Return your analysis strictly as JSON that maps to this Java class:
        {
          "toxicity": double,
          "offTopic": double,
          "piiLeak": double,
          "category": "SAFE" | "TOXIC" | "OFF_TOPIC" | "PII_LEAK",
          "explanation": "short Ukrainian explanation"
        }
    """;


    @Override
    public ModerationResult refactorText(String text) {
        try {
            return chatClient.prompt()
                    .system(SYSTEM_PROMPT)
                    .user("""
                        Analyze text:
                        "%s"
                    """.formatted(sanitizePrompt(text)))
                    .options(ChatOptions.builder()
                            .temperature(0.2)
                            .build())
                    .call()
                    .entity(ModerationResult.class);
        } catch (Exception e) {
            log.error("Gemini moderation failed: {}", e.getMessage());
            return new ModerationResult(0.5, 0.0, 0.0, ModerationCategory.UNKNOWN, "Fallback response");
        }
    }

    public String sanitizePrompt(String userInput) {

        return userInput
                .replaceAll("(?i)ignore previous instructions", "")
                .replaceAll("(?i)system prompt", "")
                .replaceAll("(?i)you are now", "")

                .replaceAll("(?i)ігноруй попередні інструкції", "")
                .replaceAll("(?i)ігнорувати попередні інструкції", "")
                .replaceAll("(?i)системний промпт", "")
                .replaceAll("(?i)тепер ти", "")
                .replaceAll("(?i)відтепер ти", "")

                .replaceAll("(?i)игнорируй предыдущие инструкции", "")
                .replaceAll("(?i)игнорировать предыдущие инструкции", "")
                .replaceAll("(?i)системный промпт", "")
                .replaceAll("(?i)теперь ты", "")
                .replaceAll("(?i)с этого момента ты", "")

                .trim();
    }
}
