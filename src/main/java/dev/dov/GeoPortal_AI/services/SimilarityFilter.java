package dev.dov.GeoPortal_AI.services;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SimilarityFilter {
    private final Set<String> badWords;
    private final double similarityThreshold = 0.8;

    public SimilarityFilter(ProfanityFilter baseFilter) {
        this.badWords = baseFilter.getBadWords();
    }

    public boolean containsSimilar(String text) {
        String[] words = text.toLowerCase().split("\\W+");
        for (String w : words) {
            for (String bad : badWords) {
                double sim = levenshteinSimilarity(w, bad);
                if (sim > similarityThreshold) return true;
            }
        }
        return false;
    }

    private double levenshteinSimilarity(String a, String b) {
        int dist = LevenshteinDistance.getDefaultInstance().apply(a, b);
        int max = Math.max(a.length(), b.length());
        return 1.0 - (double) dist / max;
    }
}
