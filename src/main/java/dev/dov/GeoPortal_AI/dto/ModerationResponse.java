package dev.dov.GeoPortal_AI.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ModerationResponse {
    private String requestId;
    private ModerationElementType elementType;
    private ModerationResult result;
}