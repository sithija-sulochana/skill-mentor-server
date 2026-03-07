package com.stemlink.skillmentor.services;

import com.stemlink.skillmentor.dto.ReviewDTO;
import com.stemlink.skillmentor.entities.Reviews;

import java.util.List;

public interface ReviewService {
    List<Reviews> getAllReviews();
    Reviews addNewReview( ReviewDTO reviewDTO);
    Reviews getReviewById(Long id);
    Reviews updateReviewById(Long id, ReviewDTO updatedReviewDTO);
    void deleteReview(Long id);

}
