package com.stemlink.skillmentor.controllers;

import com.stemlink.skillmentor.dto.ReviewDTO;
import com.stemlink.skillmentor.entities.Reviews;
import com.stemlink.skillmentor.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/reviews")
@RequiredArgsConstructor
@Validated
public class ReviewController {

    private final ReviewService reviewService;
    private final ModelMapper modelMapper;

    @GetMapping
    public List<Reviews> getAllReviews() {

        return reviewService.getAllReviews();
    }

    @GetMapping("{id}")
    public Reviews getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id);
    }

    @PostMapping
    public Reviews addNewReview(@RequestBody ReviewDTO reviewDTO){


        return reviewService.addNewReview(reviewDTO);
    }
    @PutMapping("{id}")
    public Reviews updateReviewById(@PathVariable Long id, @RequestBody ReviewDTO updatedReviewDTO){
        return reviewService.updateReviewById(id, updatedReviewDTO);
    }

    @DeleteMapping("{id}")
    public void deleteReview(@PathVariable Long id){
        reviewService.deleteReview(id);
    }

}
