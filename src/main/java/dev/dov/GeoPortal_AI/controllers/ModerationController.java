package dev.dov.GeoPortal_AI.controllers;


import dev.dov.GeoPortal_AI.ai_clients.LLMClient;
import dev.dov.GeoPortal_AI.dto.ModerationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/moderation")
@RequiredArgsConstructor
public class ModerationController {

    private final LLMClient decisionService;

    @PostMapping("/check")
    public ResponseEntity<ModerationResult> checkText(@RequestBody Map<String, String> body) {

       String text = body.get("text");

       ModerationResult moderationResult = decisionService.refactorText(text);

       return ResponseEntity.ok(moderationResult);
    }
}
