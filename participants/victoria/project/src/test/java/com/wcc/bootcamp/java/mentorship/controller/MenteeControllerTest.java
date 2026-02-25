package com.wcc.bootcamp.java.mentorship.controller;

import com.wcc.bootcamp.java.mentorship.model.Mentee;
import com.wcc.bootcamp.java.mentorship.service.MentorshipService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for MenteeController using MockMvc.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("MenteeController")
class MenteeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MentorshipService mentorshipService;

    @Nested
    @DisplayName("GET /mentees")
    class ListMenteesTests {

        @Test
        @DisplayName("should return mentees list page")
        void shouldReturnMenteesListPage() throws Exception {
            Mentee mentee = new Mentee("Bob", "bob@example.com", 
                    List.of("java", "web development"));
            when(mentorshipService.getAllMentees()).thenReturn(List.of(mentee));

            mockMvc.perform(get("/mentees"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("mentees/list"))
                    .andExpect(model().attributeExists("mentees"));
        }

        @Test
        @DisplayName("should handle empty mentees list")
        void shouldHandleEmptyMenteesList() throws Exception {
            when(mentorshipService.getAllMentees()).thenReturn(List.of());

            mockMvc.perform(get("/mentees"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("mentees/list"));
        }
    }

    @Nested
    @DisplayName("GET /mentees/register")
    class ShowRegistrationFormTests {

        @Test
        @DisplayName("should return registration form")
        void shouldReturnRegistrationForm() throws Exception {
            mockMvc.perform(get("/mentees/register"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("mentees/register"))
                    .andExpect(model().attributeExists("menteeForm"));
        }
    }

    @Nested
    @DisplayName("POST /mentees/register")
    class RegisterMenteeTests {

        @Test
        @DisplayName("should register mentee with valid data")
        void shouldRegisterMenteeWithValidData() throws Exception {
            Mentee mentee = new Mentee("Bob Smith", "bob@example.com", 
                    List.of("java"));
            when(mentorshipService.registerMentee(anyString(), anyString(), anyList(), anyString()))
                    .thenReturn(mentee);

            mockMvc.perform(post("/mentees/register")
                            .param("name", "Bob Smith")
                            .param("email", "bob@example.com")
                            .param("learningGoals", "java, spring boot")
                            .param("experienceLevel", "beginner"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/mentees"))
                    .andExpect(flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("should reject registration with empty name")
        void shouldRejectRegistrationWithEmptyName() throws Exception {
            mockMvc.perform(post("/mentees/register")
                            .param("name", "")
                            .param("email", "bob@example.com")
                            .param("learningGoals", "java")
                            .param("experienceLevel", "beginner"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("mentees/register"))
                    .andExpect(model().hasErrors());
        }

        @Test
        @DisplayName("should reject registration with invalid email")
        void shouldRejectRegistrationWithInvalidEmail() throws Exception {
            mockMvc.perform(post("/mentees/register")
                            .param("name", "Bob Smith")
                            .param("email", "invalid-email")
                            .param("learningGoals", "java")
                            .param("experienceLevel", "beginner"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("mentees/register"))
                    .andExpect(model().hasErrors());
        }

        @Test
        @DisplayName("should reject registration with empty learning goals")
        void shouldRejectRegistrationWithEmptyLearningGoals() throws Exception {
            mockMvc.perform(post("/mentees/register")
                            .param("name", "Bob Smith")
                            .param("email", "bob@example.com")
                            .param("learningGoals", "")
                            .param("experienceLevel", "beginner"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("mentees/register"))
                    .andExpect(model().hasErrors());
        }
    }

    @Nested
    @DisplayName("GET /mentees/{id}")
    class ViewMenteeTests {

        @Test
        @DisplayName("should return mentee profile page")
        void shouldReturnMenteeProfilePage() throws Exception {
            Mentee mentee = new Mentee("Bob", "bob@example.com", 
                    List.of("java"));
            when(mentorshipService.findMenteeById(mentee.getId()))
                    .thenReturn(Optional.of(mentee));
            when(mentorshipService.findMatchesForMentee(mentee.getId()))
                    .thenReturn(List.of());

            mockMvc.perform(get("/mentees/" + mentee.getId()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("mentees/view"))
                    .andExpect(model().attributeExists("mentee"));
        }

        @Test
        @DisplayName("should redirect when mentee not found")
        void shouldRedirectWhenMenteeNotFound() throws Exception {
            when(mentorshipService.findMenteeById("invalid-id"))
                    .thenReturn(Optional.empty());

            mockMvc.perform(get("/mentees/invalid-id"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/mentees"));
        }
    }

    @Nested
    @DisplayName("POST /mentees/{id}/delete")
    class DeleteMenteeTests {

        @Test
        @DisplayName("should delete mentee and redirect")
        void shouldDeleteMenteeAndRedirect() throws Exception {
            doNothing().when(mentorshipService).deleteMentee("test-id");

            mockMvc.perform(post("/mentees/test-id/delete"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/mentees"))
                    .andExpect(flash().attributeExists("successMessage"));

            verify(mentorshipService).deleteMentee("test-id");
        }
    }
}
