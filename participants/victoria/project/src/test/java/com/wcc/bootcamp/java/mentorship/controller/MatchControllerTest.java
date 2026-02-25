package com.wcc.bootcamp.java.mentorship.controller;

import com.wcc.bootcamp.java.mentorship.model.Match;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for MatchController using MockMvc.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("MatchController")
class MatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MentorshipService mentorshipService;

    private Mentor createSampleMentor() {
        return new Mentor("Alice", "alice@example.com", List.of("java", "spring boot"));
    }

    private Mentee createSampleMentee() {
        return new Mentee("Bob", "bob@example.com", List.of("java"));
    }

    @Nested
    @DisplayName("GET /matches")
    class ListMatchesTests {

        @Test
        @DisplayName("should return active matches page")
        void shouldReturnActiveMatchesPage() throws Exception {
            Mentor mentor = createSampleMentor();
            Mentee mentee = createSampleMentee();
            Match match = new Match(mentor, mentee, List.of("java"), 0.75);
            match.activate();

            when(mentorshipService.getActiveMatches()).thenReturn(List.of(match));

            mockMvc.perform(get("/matches"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("matches/list"))
                    .andExpect(model().attributeExists("activeMatches"));
        }

        @Test
        @DisplayName("should handle empty matches list")
        void shouldHandleEmptyMatchesList() throws Exception {
            when(mentorshipService.getActiveMatches()).thenReturn(List.of());

            mockMvc.perform(get("/matches"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("matches/list"));
        }
    }

    @Nested
    @DisplayName("GET /matches/find")
    class FindMatchesTests {

        @Test
        @DisplayName("should return find matches page")
        void shouldReturnFindMatchesPage() throws Exception {
            Mentor mentor = createSampleMentor();
            Mentee mentee = createSampleMentee();
            Match match = new Match(mentor, mentee, List.of("java"), 0.75);

            when(mentorshipService.findAllPotentialMatches()).thenReturn(List.of(match));

            mockMvc.perform(get("/matches/find"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("matches/find"))
                    .andExpect(model().attributeExists("potentialMatches"));
        }

        @Test
        @DisplayName("should handle no potential matches")
        void shouldHandleNoPotentialMatches() throws Exception {
            when(mentorshipService.findAllPotentialMatches()).thenReturn(List.of());

            mockMvc.perform(get("/matches/find"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("matches/find"));
        }
    }

    @Nested
    @DisplayName("POST /matches/create")
    class CreateMatchTests {

        @Test
        @DisplayName("should create match and redirect")
        void shouldCreateMatchAndRedirect() throws Exception {
            Mentor mentor = createSampleMentor();
            Mentee mentee = createSampleMentee();
            Match match = new Match(mentor, mentee, List.of("java"), 0.75);

            when(mentorshipService.createMatch(anyString(), anyString())).thenReturn(match);

            mockMvc.perform(post("/matches/create")
                            .param("mentorId", mentor.getId())
                            .param("menteeId", mentee.getId()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/matches"))
                    .andExpect(flash().attributeExists("successMessage"));

            verify(mentorshipService).createMatch(mentor.getId(), mentee.getId());
        }

        @Test
        @DisplayName("should handle match creation error")
        void shouldHandleMatchCreationError() throws Exception {
            when(mentorshipService.createMatch(anyString(), anyString()))
                    .thenThrow(new IllegalArgumentException("Mentor not found"));

            mockMvc.perform(post("/matches/create")
                            .param("mentorId", "invalid")
                            .param("menteeId", "invalid"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/matches/find"))
                    .andExpect(flash().attributeExists("errorMessage"));
        }
    }

    @Nested
    @DisplayName("POST /matches/{id}/cancel")
    class CancelMatchTests {

        @Test
        @DisplayName("should cancel match and redirect")
        void shouldCancelMatchAndRedirect() throws Exception {
            doNothing().when(mentorshipService).cancelMatch("match-id");

            mockMvc.perform(post("/matches/match-id/cancel"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/matches"))
                    .andExpect(flash().attributeExists("successMessage"));

            verify(mentorshipService).cancelMatch("match-id");
        }
    }
}
