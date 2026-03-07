package com.stemlink.skillmentor.respositories;

import com.stemlink.skillmentor.entities.Mentor;
import com.stemlink.skillmentor.entities.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session,Long> {
    List<Session> findByStudent_Email(String email);
    Optional<Session> findById(Long id);
    List<Session> findByMentor_Id(Long mentorId);


}
