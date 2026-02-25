package com.wcc.bootcamp.java.mentorship.controller;

import com.wcc.bootcamp.java.mentorship.model.Mentee;
import com.wcc.bootcamp.java.mentorship.model.Mentor;
import com.wcc.bootcamp.java.mentorship.service.MentorshipService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for MentorController using MockMvc.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("MentorController")
class MentorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MentorshipService mentorshipService;

    @Nested
    @DisplayName("GET /mentors")
    class ListMentorsTests {

        @Test
        @DisplayName("should return mentors list page")
        void shouldReturnMentorsListPage() throws Exception {
            Mentor mentor = new Mentor("Alice", "alice@example.com", 
                    List.of("java", "spring boot"));
            when(mentorshipService.getAllMentors()).thenReturn(List.of(mentor));

            mockMvc.perform(get("/mentors"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("mentors/list"))
                    .andExpect(model().attributeExists("mentors"));
        }

        @Test
        @DisplayName("should handle empty mentors list")
        void shouldHandleEmptyMentorsList() throws Exception {
            when(mentorshipService.getAllMentors()).thenReturn(List.of());

            mockMvc.perform(get("/mentors"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("mentors/list"));
        }
    }

    @Nested
    @DisplayName("GET /mentors/register")
    class ShowRegistrationFormTests {

        @Test
        @DisplayName("should return registration form")
        void shouldReturnRegistrationForm() throws Exception {
            mockMvc.perform(get("/mentors/register"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("mentors/register"))
                    .andExpect(model().attributeExists("mentorForm"));
        }
    }

    @Nested
    @DisplayName("POST /mentors/register")
    class RegisterMentorTests {

        @Test
        @DisplayName("should register mentor with valid data")
        void shouldRegisterMentorWithValidData() throws Exception {
            Mentor mentor = new Mentor("Alice Johnson", "alice@example.com", 
                    List.of("java"));
            when(mentorshipService.registerMentor(anyString(), anyString(), anyList(), anyInt()))
                    .thenReturn(mentor);

            mockMvc.perform(post("/mentors/register")
                            .param("name", "Alice Johnson")
                            .param("email", "alice@example.com")
                            .param("skills", "java, spring boot")
                            .param("maxMentees", "3"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/mentors"))
                    .andExpect(flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("should reject registration with empty name")
        void shouldRejectRegistrationWithEmptyName() throws Exception {
            mockMvc.perform(post("/mentors/register")
                            .param("name", "")
                            .param("email", "alice@example.com")
                            .param("skills", "java")
                            .param("maxMentees", "3"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("mentors/register"))
                    .andExpect(model().hasErrors());
        }

        @Test
        @DisplayName("should reject registration with invalid email")
        void shouldRejectRegistrationWithInvalidEmail() throws Exception {
            mockMvc.perform(post("/mentors/register")
                            .param("name", "Alice Johnson")
                            .param("email", "not-an-email")
                            .param("skills", "java")
                            .param("maxMentees", "3"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("mentors/register"))
                    .andExpect(model().hasErrors());
        }

        @Test
        @DisplayName("should reject registration with empty skills")
        void shouldRejectRegistrationWithEmptySkills() throws Exception {
            mockMvc.perform(post("/mentors/register")
                            .param("name", "Alice Johnson")
                            .param("email", "alice@example.com")
                            .param("skills", "")
                            .param("maxMentees", "3"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("mentors/register"))
                    .andExpect(model().hasErrors());
        }
    }

    @Nested
    @DisplayName("GET /mentors/{id}")
    class ViewMentorTests {

        @Test
        @DisplayName("should return mentor profile page")
        void shouldReturnMentorProfilePage() throws Exception {
            Mentor mentor = new Mentor("Alice", "alice@example.com", 
                    List.of("java"));
            when(mentorshipService.findMentorById(mentor.getId()))
                    .thenReturn(Optional.of(mentor));
            when(mentorshipService.findMatchesForMentor(mentor.getId()))
                    .thenReturn(List.of());

            mockMvc.perform(get("/mentors/" + mentor.getId()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("mentors/view"))
                    .andExpect(model().attributeExists("mentor"));
        }

        @Test
        @DisplayName("should redirect when mentor not found")
        void shouldRedirectWhenMentorNotFound() throws Exception {
            when(mentorshipService.findMentorById("invalid-id"))
                    .thenReturn(Optional.empty());

            mockMvc.perform(get("/mentors/invalid-id"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/mentors"));
        }
    }

    @Nested
    @DisplayName("POST /mentors/{id}/delete")
    class DeleteMentorTests {

        @Test
        @DisplayName("should delete mentor and redirect")
        void shouldDeleteMentorAndRedirect() throws Exception {
            doNothing().when(mentorshipService).deleteMentor("test-id");

            mockMvc.perform(post("/mentors/test-id/delete"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/mentors"))
                    .andExpect(flash().attributeExists("successMessage"));

            verify(mentorshipService).deleteMentor("test-id");
        }
    }
}
