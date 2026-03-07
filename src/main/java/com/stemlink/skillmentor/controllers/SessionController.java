package com.stemlink.skillmentor.controllers;


import com.stemlink.skillmentor.dto.SessionDTO;
import com.stemlink.skillmentor.dto.response.SessionResponseDTO;
import com.stemlink.skillmentor.entities.Mentor;
import com.stemlink.skillmentor.entities.Session;
import com.stemlink.skillmentor.entities.Subject;
import com.stemlink.skillmentor.security.UserPrincipal;
import com.stemlink.skillmentor.services.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.stemlink.skillmentor.constants.UserRoles.ROLE_ADMIN;

@RestController
@RequestMapping(path = "/api/v1/sessions")
@RequiredArgsConstructor
@Validated
@PreAuthorize("isAuthenticated()")
public class SessionController extends AbstractController {

    private final SessionService sessionService;


    @GetMapping
    @PreAuthorize("hasAnyRole('" + ROLE_ADMIN + "')")
    public ResponseEntity<List<SessionResponseDTO>> getAllSessions() {
        List<Session> sessions = sessionService.getAllSessions();
        List<SessionResponseDTO> response = sessions.stream()
                .map(this::toSessionResponseDTO)
                .collect(Collectors.toList());
        return sendOkResponse(response);
    }
    @GetMapping("{id}")
    public Session getSessionById(@PathVariable Long id) {
        return sessionService.getSessionById(id);
    }

    @PostMapping
    public Session createSession(@Valid @RequestBody SessionDTO sessionDTO) {
        return sessionService.createNewSession(sessionDTO);
    }

    @PutMapping("{id}")
    public Session updateSession(@PathVariable Long id, @Valid @RequestBody SessionDTO updatedSessionDTO) {
        return sessionService.updateSessionById(id, updatedSessionDTO);
    }

    @DeleteMapping("{id}")
    public void deleteSession(@PathVariable Long id) {
        sessionService.deleteSession(id);
    }

    // Enrollment endpoint for students to enroll in a session
    @PostMapping("/enroll")
    public ResponseEntity<SessionResponseDTO> enroll(
            @RequestBody SessionDTO sessionDTO,
            Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Session session = sessionService.enrollSession(userPrincipal, sessionDTO);
        Subject subject = session.getSubject();

        int currentCount = (subject.getSubjectEnrollment() != null) ? subject.getSubjectEnrollment() : 0;
        subject.setSubjectEnrollment(currentCount + 1);
        return sendCreatedResponse(toSessionResponseDTO(session));
    }

    @GetMapping("/my-sessions")
    public ResponseEntity<List<SessionResponseDTO>> getMySessions(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        List<Session> sessions = sessionService.getSessionsByStudentEmail(userPrincipal.getEmail());
        List<SessionResponseDTO> response = sessions.stream()
                .map(this::toSessionResponseDTO)
                .collect(Collectors.toList());
        return sendOkResponse(response);
    }

    private SessionResponseDTO toSessionResponseDTO(Session session) {
        SessionResponseDTO dto = new SessionResponseDTO();
        dto.setId(session.getId());
        dto.setMentorName(session.getMentor().getFirstName() + " " + session.getMentor().getLastName());
        dto.setMentorProfileImageUrl(session.getMentor().getProfileImageUrl());
        dto.setSubjectName(session.getSubject().getSubjectName());
        dto.setSessionAt(session.getSessionAt());
        dto.setDurationMinutes(session.getDurationMinutes());
        dto.setSessionStatus(session.getSessionStatus());
        dto.setPaymentStatus(session.getPaymentStatus());
        dto.setMeetingLink(session.getMeetingLink());
        return dto;
    }

    @GetMapping("/{sessionId}/mentor")
    public ResponseEntity<Mentor> getMentorBySessionId(
            @PathVariable Long sessionId) {

        return ResponseEntity.ok(
                sessionService.getMentorBySessionId(sessionId)
        );
    }

    @GetMapping("/mentors/{mentorId}")
    public ResponseEntity<List<SessionResponseDTO>> getSessionsByMentorId(
            @PathVariable Long mentorId) {

        List<Session> sessions = sessionService.getSessionsByMentorId(mentorId);

        List<SessionResponseDTO> response = sessions.stream()
                .map(this::toSessionResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
