package dev.dov.GeoPortal_AI;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeneralController {

    private ChatClient chatClient;

    public GeneralController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/ai/validation/location")
    public ChatResponse checkIsTextAboutLocation() {

        String systemPrompt = """
                Classify the sentiment of the following text as NORMAL, HARMFUL, or NEUTRAL. \
                Your response must be only one of these three words.""";
        return chatClient.prompt()
                .system(systemPrompt)
                .user("Придумай жарт про німця і українця(70 токенів)")
                .call().chatResponse();

    }
}
