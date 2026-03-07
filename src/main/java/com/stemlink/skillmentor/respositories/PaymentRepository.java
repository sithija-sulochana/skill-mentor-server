package com.stemlink.skillmentor.respositories;

import com.stemlink.skillmentor.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
}
