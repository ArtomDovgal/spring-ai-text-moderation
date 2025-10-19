package dev.dov.GeoPortal_AI.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Getter
@AllArgsConstructor
public class ProfanityFilter {
    private final Set<String> badWords;
    private final double similarityThreshold = 0.8;

    // у /resources
    public ProfanityFilter() throws IOException {
        this.badWords = loadBadWords("badwords.txt");
    }

    private Set<String> loadBadWords(String filePath) throws IOException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(filePath)) {
            return new BufferedReader(new InputStreamReader(in))
                    .lines()
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
        }
    }

    public boolean containsProfanity(String text) {
        String[] words = text.toLowerCase().split("\\W+");
        for (String word : words) {
            if (badWords.contains(word))
                return true;
        }
        return false;
    }
}

