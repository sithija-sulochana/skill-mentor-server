package com.stemlink.skillmentor.controllers;

import com.stemlink.skillmentor.dto.SubjectDTO;
import com.stemlink.skillmentor.entities.Subject;
import com.stemlink.skillmentor.exceptions.SkillMentorException;
import com.stemlink.skillmentor.services.SubjectService;
import com.stemlink.skillmentor.utils.ValidationUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.stemlink.skillmentor.constants.UserRoles.ROLE_ADMIN;
import static com.stemlink.skillmentor.constants.UserRoles.ROLE_MENTOR;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/subjects")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")

public class SubjectController  {

    private final ModelMapper modelMapper;
    private final SubjectService subjectService;

    @GetMapping
    public List<Subject> getAllSubjects() {
        return subjectService.getAllSubjects();
    }



    @GetMapping("{id}")
    public Subject getSubjectById(@PathVariable Long id) {
        return subjectService.getSubjectById(id);
    }



    @PostMapping
    @PreAuthorize("hasAnyRole('" + ROLE_ADMIN + "','" + ROLE_MENTOR + "')")
    public Subject createSubject(@Valid @RequestBody SubjectDTO subjectDTO) {

        // 1. Get the Mentor ID from the DTO (The "9" from your frontend)
        if (subjectDTO.getMentorId() == null) {
            throw new SkillMentorException("Mentor ID is required", HttpStatus.BAD_REQUEST);
        }
        Long mentorId = Long.parseLong(String.valueOf(subjectDTO.getMentorId()));

        // 2. Map DTO to Entity
        Subject subject = modelMapper.map(subjectDTO, Subject.class);

        // 3. Simple Validation (Better to do this in the DTO or a Validator)
        if (subject.getSubjectName() != null && subject.getSubjectName().length() < 3) {
            throw new SkillMentorException("Subject name too short", HttpStatus.BAD_REQUEST);
        }

        // 4. Initialize Enrollment
        if (subject.getSubjectEnrollment() == null) {
            subject.setSubjectEnrollment(0);
        }

        // 5. Pass the dynamic mentorId to the service
        return subjectService.addNewSubject(mentorId, subject);
    }



    @PutMapping("{id}")
    @PreAuthorize("hasAnyRole('" + ROLE_ADMIN + "'), '" + ROLE_MENTOR + "')")
    public Subject updateSubject(@PathVariable Long id, @RequestBody SubjectDTO updatedSubjectDTO) {
        Subject subject = modelMapper.map(updatedSubjectDTO, Subject.class);
        return subjectService.updateSubjectById(id, subject);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyRole('" + ROLE_ADMIN + "'))")
    public void deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
    }
}
