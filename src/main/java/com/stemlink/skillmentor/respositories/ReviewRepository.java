package com.stemlink.skillmentor.respositories;

import com.stemlink.skillmentor.entities.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ReviewRepository extends JpaRepository<Reviews, Long> {
    List<Reviews> findByMentorId(Integer mentorId);
}
