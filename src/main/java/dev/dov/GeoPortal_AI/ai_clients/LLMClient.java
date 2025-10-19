package dev.dov.GeoPortal_AI.ai_clients;

import dev.dov.GeoPortal_AI.dto.ModerationResult;

public interface LLMClient {

    ModerationResult refactorText(String text);
}
