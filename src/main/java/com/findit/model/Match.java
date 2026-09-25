package com.findit.model;

/** A possible match between a report and an opposite-type report, with a 0-100 score. */
public class Match implements Comparable<Match> {

    private final Item source;
    private final Item candidate;
    private final int score;

    public Match(Item source, Item candidate, int score) {
        this.source = source;
        this.candidate = candidate;
        this.score = score;
    }

    public Item getSource() { return source; }
    public Item getCandidate() { return candidate; }
    public int getScore() { return score; }

    @Override
    public int compareTo(Match other) {
        return Integer.compare(other.score, this.score); // highest score first
    }
}
