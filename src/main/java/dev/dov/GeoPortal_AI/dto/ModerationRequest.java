package dev.dov.GeoPortal_AI.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModerationRequest {
    private String requestId;
    private ModerationElementType elementType;
    private String text;
}
