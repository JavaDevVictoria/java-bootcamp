package com.wcc.bootcamp.java.mentorship.dto;

import jakarta.validation.constraints.*;

/**
 * Form object for mentor registration.
 */
public class MentorRegistrationForm {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "At least one skill/expertise area is required")
    private String skills;

    @Min(value = 1, message = "Must accept at least 1 mentee")
    @Max(value = 10, message = "Cannot accept more than 10 mentees")
    private int maxMentees = 3;

    // Getters and Setters
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

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public int getMaxMentees() {
        return maxMentees;
    }

    public void setMaxMentees(int maxMentees) {
        this.maxMentees = maxMentees;
    }
}
