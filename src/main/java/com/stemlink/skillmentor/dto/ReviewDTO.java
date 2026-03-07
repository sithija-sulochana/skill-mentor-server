package com.stemlink.skillmentor.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReviewDTO {


    private Integer studentId;
    private Long mentorId;
    private Integer sessionId;

    private Integer rating;

    @Size(max = 1000, message = "Review text cannot exceed 1000 characters")
    private String review;
}
