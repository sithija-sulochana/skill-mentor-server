package com.stemlink.skillmentor.respositories;

import com.stemlink.skillmentor.entities.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;



public interface ReviewRepository extends JpaRepository<Reviews, Long> {

}
