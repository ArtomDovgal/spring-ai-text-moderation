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
                    """.formatted(text))
                    .options(ChatOptions.builder()
                            .temperature(0.2)
                            .build())
                    .call()
                    .entity(ModerationResult.class);
        } catch (Exception e) {
            log.error("Gemini moderation failed: {}", e.getMessage());
            return new ModerationResult(0.5, 0.0, 0.0, ModerationCategory.SAFE, "Fallback response");
        }
    }
}
