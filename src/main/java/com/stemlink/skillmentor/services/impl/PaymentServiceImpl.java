package com.stemlink.skillmentor.services.impl;

import com.stemlink.skillmentor.dto.PaymentDTO;
import com.stemlink.skillmentor.entities.Payment;
import com.stemlink.skillmentor.entities.Session;
import com.stemlink.skillmentor.entities.Student;
import com.stemlink.skillmentor.exceptions.SkillMentorException;
import com.stemlink.skillmentor.respositories.PaymentRepository;
import com.stemlink.skillmentor.respositories.SessionRepository;
import com.stemlink.skillmentor.respositories.StudentRepository;
import com.stemlink.skillmentor.services.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final SessionRepository sessionRepository;
    private final StudentRepository studentRepository;
    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;

    @Override
    public Payment createPayment(PaymentDTO paymentDTO) {
        Session session = sessionRepository.findById(Long.valueOf(paymentDTO.getSessionId())).orElseThrow(
                () -> new SkillMentorException("Session not found", HttpStatus.NOT_FOUND)
        );
        Student student = studentRepository.findById(paymentDTO.getStudentId())
                .orElseThrow(
                        () -> new SkillMentorException("Student not found", HttpStatus.NOT_FOUND)
                );

        Payment payment = modelMapper.map(paymentDTO, Payment.class);

        payment.setStudent(student);

        payment.setReceipt_url(paymentDTO.getReceipt_url());
        payment.setNote(paymentDTO.getNote());
        payment.setSession(session);
        payment.setStudent(student);

        return paymentRepository.save(payment);
    }

    @Override
    public List<Payment> getAllPayment() {
        return paymentRepository.findAll();
    }

    @Override
    public Payment getPaymentById(Integer id) {
        return paymentRepository.findById(id)
                .orElseThrow(
                        () -> new SkillMentorException("Payment not found", HttpStatus.NOT_FOUND)
                );
    }

    @Override
    public Payment updatePayment(Integer id, PaymentDTO updatePaymentDTO) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(
                        () -> new SkillMentorException("Payment not found", HttpStatus.NOT_FOUND)
                );
        if (updatePaymentDTO.getNote() != null) {
            payment.setNote(updatePaymentDTO.getNote());
        }
        if (updatePaymentDTO.getSessionId() != null) {
            Session session = sessionRepository.findById(Long.valueOf(updatePaymentDTO.getSessionId()))
                    .orElseThrow(() ->
                            new SkillMentorException("Session not found", HttpStatus.NOT_FOUND));
            payment.setSession(session);
        }
        if (updatePaymentDTO.getStudentId() != null) {
            var student = studentRepository.findById(updatePaymentDTO.getStudentId())
                    .orElseThrow(() ->
                            new SkillMentorException("Student not found", HttpStatus.NOT_FOUND));
            payment.setStudent(student);
        }
        log.info("Updated payment id ={}", id);
        return paymentRepository.save(payment);
    }

    @Override
    public void deletePayment(Integer id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(
                        () -> new SkillMentorException("Payment not found", HttpStatus.NOT_FOUND)
                );
        paymentRepository.delete(payment);
    }

    public Payment createPayment(PaymentDTO paymentDTO, String email) {

        Session session = sessionRepository
                .findById(Long.valueOf(paymentDTO.getSessionId()))
                .orElseThrow(() ->
                        new SkillMentorException("Session not found", HttpStatus.NOT_FOUND)
                );

        Student student = studentRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new SkillMentorException("Student not found", HttpStatus.NOT_FOUND)
                );

        Payment payment = modelMapper.map(paymentDTO, Payment.class);

        payment.setSession(session);
        payment.setStudent(student);

        return paymentRepository.save(payment);
    }


}
