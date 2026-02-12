package com.wcc.bootcamp.java.mentorship.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a match between a mentor and a mentee.
 * Contains matching score and matched skills information.
 */
public class Match {
    private final String id;
    private final Mentor mentor;
    private final Mentee mentee;
    private final List<String> matchedSkills;
    private final double matchScore;
    private final LocalDateTime matchDate;
    private MatchStatus status;

    public enum MatchStatus {
        PENDING,
        ACTIVE,
        COMPLETED,
        CANCELLED
    }

    public Match(Mentor mentor, Mentee mentee, List<String> matchedSkills, double matchScore) {
        this.id = UUID.randomUUID().toString();
        this.mentor = mentor;
        this.mentee = mentee;
        this.matchedSkills = new ArrayList<>(matchedSkills);
        this.matchScore = matchScore;
        this.matchDate = LocalDateTime.now();
        this.status = MatchStatus.PENDING;
    }

    // Getters
    public String getId() {
        return id;
    }

    public Mentor getMentor() {
        return mentor;
    }

    public Mentee getMentee() {
        return mentee;
    }

    public List<String> getMatchedSkills() {
        return new ArrayList<>(matchedSkills);
    }

    public double getMatchScore() {
        return matchScore;
    }

    public LocalDateTime getMatchDate() {
        return matchDate;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    /**
     * Activates the match, updating mentor and mentee status.
     */
    public void activate() {
        this.status = MatchStatus.ACTIVE;
        mentor.incrementMenteeCount();
        mentee.setMatched(true);
    }

    /**
     * Cancels the match, freeing up the mentor and mentee.
     */
    public void cancel() {
        if (this.status == MatchStatus.ACTIVE) {
            mentor.decrementMenteeCount();
            mentee.setMatched(false);
        }
        this.status = MatchStatus.CANCELLED;
    }

    /**
     * Completes the match (mentorship ended successfully).
     */
    public void complete() {
        if (this.status == MatchStatus.ACTIVE) {
            mentor.decrementMenteeCount();
            mentee.setMatched(false);
        }
        this.status = MatchStatus.COMPLETED;
    }

    /**
     * Gets a formatted string representation for file storage.
     */
    public String toFileFormat() {
        return String.format("%s|%s|%s|%s|%s|%.2f|%s|%s",
                id,
                mentor.getId(),
                mentor.getName(),
                mentee.getId(),
                mentee.getName(),
                matchScore,
                String.join(",", matchedSkills),
                status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;
        return Objects.equals(id, match.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Match{mentor='%s', mentee='%s', skills=%s, score=%.2f%%, status=%s}",
                mentor.getName(), mentee.getName(), matchedSkills, matchScore * 100, status);
    }
}
