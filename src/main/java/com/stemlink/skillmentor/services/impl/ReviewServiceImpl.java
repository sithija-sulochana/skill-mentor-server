package com.stemlink.skillmentor.services.impl;

import com.stemlink.skillmentor.dto.ReviewDTO;
import com.stemlink.skillmentor.entities.Mentor;
import com.stemlink.skillmentor.entities.Reviews;
import com.stemlink.skillmentor.entities.Session;
import com.stemlink.skillmentor.entities.Student;
import com.stemlink.skillmentor.exceptions.SkillMentorException;
import com.stemlink.skillmentor.respositories.*;
import com.stemlink.skillmentor.services.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ModelMapper modelMapper;

    private final MentorRepository mentorRepository;
    private final StudentRepository studentRepository;
    private final SessionRepository sessionRepository;


    @Override
    public List<Reviews> getAllReviews() {
        try{
            return reviewRepository.findAll();
        } catch (Exception e) {
            log.error("Error fetching reviews: {}", e.getMessage());
            throw new SkillMentorException("Failed to fetch reviews", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public Reviews addNewReview(ReviewDTO reviewDTO) {
        try {

            Student student = studentRepository.findById(reviewDTO.getStudentId())
                    .orElseThrow(() -> new SkillMentorException(
                            "Student not found with id: " + reviewDTO.getStudentId(),
                            HttpStatus.NOT_FOUND));

            Mentor mentor = mentorRepository.findById(reviewDTO.getMentorId())
                    .orElseThrow(() -> new SkillMentorException(
                            "Mentor not found with id: " + reviewDTO.getMentorId(),
                            HttpStatus.NOT_FOUND));

            Session session = sessionRepository.findById(Long.valueOf(reviewDTO.getSessionId()))
                    .orElseThrow(() -> new SkillMentorException(
                            "Session not found with id: " + reviewDTO.getSessionId(),
                            HttpStatus.NOT_FOUND));

            Reviews review = modelMapper.map(reviewDTO, Reviews.class);

            review.setStudent(student);
            review.setMentor(mentor);
            review.setSession(session);

            return reviewRepository.save(review);

        } catch (SkillMentorException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error adding review: {}", e.getMessage());
            throw new SkillMentorException("Failed to add review", HttpStatus.BAD_REQUEST);
        }
    }
    @Override
    public Reviews getReviewById(Long id) {
        try {
            return reviewRepository.findById(id)
                    .orElseThrow(() -> new SkillMentorException("Review not found with id: " + id, HttpStatus.NOT_FOUND));
        } catch (SkillMentorException e) {
            log.error("Error fetching review by id: {}", e.getMessage());
            throw e; // Re-throw custom exception
        } catch (Exception e) {
            log.error("Unexpected error fetching review by id: {}", e.getMessage());
            throw new SkillMentorException("Failed to fetch review", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Reviews updateReviewById(Long id, ReviewDTO updatedReviewDTO) {
        try {

            Reviews review = reviewRepository.findById(id)
                    .orElseThrow(() -> new SkillMentorException(
                            "Review not found with id: " + id,
                            HttpStatus.NOT_FOUND));

            modelMapper.map(updatedReviewDTO, review);

            return reviewRepository.save(review);

        } catch (SkillMentorException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating review: {}", e.getMessage());
            throw new SkillMentorException("Failed to update review",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void deleteReview(Long id) {
        try{
            Reviews review = reviewRepository.findById(id)
                    .orElseThrow(() -> new SkillMentorException(
                            "Review not found with id: " + id,
                            HttpStatus.NOT_FOUND));
            reviewRepository.delete(review);
        } catch (SkillMentorException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting review: {}", e.getMessage());
            throw new SkillMentorException("Failed to delete review", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
