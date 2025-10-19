package dev.dov.GeoPortal_AI.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.units.qual.A;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class ModerationResult {

    private String elementId;
    private ModerationElementType elementType;
    private double toxicity;
    private double offTopic;
    private double piiLeak;
    private ModerationCategory category;
    private String explanation;

    public ModerationResult(double toxicity, double offTopic, double piiLeak,
                            ModerationCategory moderationCategory, String explanation) {

        this.toxicity = toxicity;
        this.offTopic = offTopic;
        this.piiLeak = piiLeak;
        this.category = moderationCategory;
        this.explanation = explanation;
    }
}

