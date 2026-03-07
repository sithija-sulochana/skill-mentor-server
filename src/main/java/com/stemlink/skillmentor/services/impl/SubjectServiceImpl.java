package com.stemlink.skillmentor.services.impl;

import com.stemlink.skillmentor.entities.Mentor;
import com.stemlink.skillmentor.entities.Subject;
import com.stemlink.skillmentor.respositories.MentorRepository;
import com.stemlink.skillmentor.respositories.SubjectRepository;
import com.stemlink.skillmentor.services.SubjectService;
import com.stemlink.skillmentor.exceptions.SkillMentorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final MentorRepository mentorRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<Subject> getAllSubjects() {
        try {
            return subjectRepository.findAll();
        } catch (Exception exception) {
            log.error("Failed to fetch subjects", exception);
            throw new SkillMentorException("Failed to get all subjects", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
        public Subject addNewSubject(Long mentorId, Subject subject) {
        try {
            // Fix: Use the Long ID directly if that is the primary key type
            Mentor mentor = mentorRepository.findById(mentorId).orElseThrow(
                    () -> new SkillMentorException("Mentor not found with ID: " + mentorId, HttpStatus.NOT_FOUND)
            );

            subject.setMentor(mentor);

            // Initialize enrollment if null, then increment
            int currentEnrollment = (subject.getSubjectEnrollment() == null) ? 0 : subject.getSubjectEnrollment();
            subject.setSubjectEnrollment(currentEnrollment + 1);

            return subjectRepository.save(subject);

        } catch (SkillMentorException e) {
            throw e;
        } catch (DataIntegrityViolationException e) {
            log.error("Conflict while adding subject: {}", e.getMessage());
            throw new SkillMentorException("Subject already exists or constraint violation", HttpStatus.CONFLICT);
        } catch (Exception exception) {
            log.error("Unexpected error adding subject", exception);
            throw new SkillMentorException("Failed to add new subject", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Subject getSubjectById(Long id) {
        return subjectRepository.findById(id).orElseThrow(
                () -> new SkillMentorException("Subject not found with ID: " + id, HttpStatus.NOT_FOUND)
        );
    }

    @Override
    @Transactional
    public Subject updateSubjectById(Long id, Subject updatedSubject) {
        try {
            Subject existingSubject = getSubjectById(id);

            // Map changes from updatedSubject to existingSubject
            // Ensure the ID of the existing entity isn't overwritten
            modelMapper.getConfiguration().setSkipNullEnabled(true);
            modelMapper.map(updatedSubject, existingSubject);

            // Explicitly re-set the ID just in case
            existingSubject.setId(id);

            return subjectRepository.save(existingSubject);
        } catch (SkillMentorException e) {
            throw e;
        } catch (DataIntegrityViolationException e) {
            log.error("Conflict while updating subject: {}", e.getMessage());
            throw new SkillMentorException("Database constraint violation", HttpStatus.CONFLICT);
        } catch (Exception exception) {
            log.error("Error updating subject", exception);
            throw new SkillMentorException("Failed to update subject", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public void deleteSubject(Long id) {
        Subject subject;
        if (!subjectRepository.existsById(id)) {
            throw new SkillMentorException("Cannot delete: Subject not found", HttpStatus.NOT_FOUND);
        }
        try {


            subjectRepository.deleteById(id);


        } catch (Exception exception) {
            log.error("Failed to delete subject ID {}", id, exception);
            throw new SkillMentorException("Failed to delete subject", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}