package com.stemlink.skillmentor.services;

import com.stemlink.skillmentor.dto.PaymentDTO;
import com.stemlink.skillmentor.entities.Payment;

import java.util.List;

public interface PaymentService {
    Payment createPayment(PaymentDTO paymentDTO);
    List<Payment> getAllPayment();
    Payment getPaymentById(Integer id);
    Payment updatePayment(Integer id, PaymentDTO updatePaymentDTO);
    void deletePayment(Integer id);
    Payment createPayment(PaymentDTO paymentDTO, String email);
}

