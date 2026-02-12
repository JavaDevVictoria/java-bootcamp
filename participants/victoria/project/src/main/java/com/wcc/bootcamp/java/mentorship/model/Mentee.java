package com.wcc.bootcamp.java.mentorship.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a mentee with learning goals.
 * Mentees can be matched with mentors based on their desired skills.
 */
public class Mentee {
    private final String id;
    private String name;
    private String email;
    private List<String> learningGoals;
    private String experienceLevel;
    private boolean isMatched;

    public Mentee(String name, String email, List<String> learningGoals) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.learningGoals = new ArrayList<>(learningGoals);
        this.experienceLevel = "beginner"; // Default experience level
        this.isMatched = false;
    }

    public Mentee(String name, String email, List<String> learningGoals, String experienceLevel) {
        this(name, email, learningGoals);
        this.experienceLevel = experienceLevel;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getLearningGoals() {
        return new ArrayList<>(learningGoals);
    }

    public void setLearningGoals(List<String> learningGoals) {
        this.learningGoals = new ArrayList<>(learningGoals);
    }

    public void addLearningGoal(String goal) {
        if (!learningGoals.contains(goal.toLowerCase())) {
            learningGoals.add(goal.toLowerCase());
        }
    }

    public void removeLearningGoal(String goal) {
        learningGoals.remove(goal.toLowerCase());
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public boolean isMatched() {
        return isMatched;
    }

    public void setMatched(boolean matched) {
        isMatched = matched;
    }

    /**
     * Checks if the mentee wants to learn a specific skill (case-insensitive partial match).
     */
    public boolean wantsToLearn(String skill) {
        String lowerSkill = skill.toLowerCase();
        return learningGoals.stream()
                .anyMatch(goal -> goal.toLowerCase().contains(lowerSkill) 
                        || lowerSkill.contains(goal.toLowerCase()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mentee mentee = (Mentee) o;
        return Objects.equals(id, mentee.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Mentee{name='%s', email='%s', goals=%s, level='%s', matched=%s}",
                name, email, learningGoals, experienceLevel, isMatched);
    }
}
