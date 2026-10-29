package com.findit.service;

import com.findit.dao.ItemDAO;
import com.findit.model.Item;
import com.findit.model.Match;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Rule-based matching: compares a LOST report with FOUND
 * reports (or the other way round) using Java string processing.
 *
 *   Category match            = +40
 *   Location match            = +40 (partial word overlap = +20)
 *   Item-name keyword overlap = up to +20
 */
@Service
public class MatchService {

    public static final int THRESHOLD = 60;
    private static final int MAX_RESULTS = 5;
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "a", "an", "the", "of", "and", "my", "with", "near", "in", "on", "at", "for", "to"));

    private final ItemDAO itemDAO;

    public MatchService(ItemDAO itemDAO) {
        this.itemDAO = itemDAO;
    }

    public List<Match> findMatches(Item item) throws SQLException {
        String opposite = item.isLost() ? "FOUND" : "LOST";
        List<Item> candidates = itemDAO.findActiveByType(opposite);
        List<Match> matches = new ArrayList<>();
        for (Item candidate : candidates) {
            if (candidate.getId() == item.getId()) {
                continue;
            }
            int score = score(item, candidate);
            if (score >= THRESHOLD) {
                matches.add(new Match(item, candidate, score));
            }
        }
        Collections.sort(matches);
        return matches.size() > MAX_RESULTS ? matches.subList(0, MAX_RESULTS) : matches;
    }

    public int score(Item a, Item b) {
        int score = 0;

        if (a.getCategory() != null && a.getCategory().equalsIgnoreCase(b.getCategory())) {
            score += 40;
        }

        String locA = normalise(a.getLocation());
        String locB = normalise(b.getLocation());
        if (!locA.isEmpty() && (locA.equals(locB) || locA.contains(locB) || locB.contains(locA))) {
            score += 40;
        } else if (overlap(tokens(locA), tokens(locB)) > 0) {
            score += 20;
        }

        Set<String> nameA = tokens(normalise(a.getItemName()));
        Set<String> nameB = tokens(normalise(b.getItemName()));
        double similarity = overlap(nameA, nameB);
        score += (int) Math.round(similarity * 20);

        return Math.min(score, 100);
    }

    /** Ratio of shared words to the smaller word set (0.0 - 1.0). */
    private double overlap(Set<String> a, Set<String> b) {
        if (a.isEmpty() || b.isEmpty()) {
            return 0;
        }
        Set<String> common = new HashSet<>(a);
        common.retainAll(b);
        return (double) common.size() / Math.min(a.size(), b.size());
    }

    private Set<String> tokens(String text) {
        Set<String> result = new HashSet<>();
        for (String word : text.split("\\s+")) {
            if (word.length() > 1 && !STOP_WORDS.contains(word)) {
                result.add(word);
            }
        }
        return result;
    }

    private String normalise(String s) {
        if (s == null) {
            return "";
        }
        return s.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9 ]", " ").trim().replaceAll("\\s+", " ");
    }
}
