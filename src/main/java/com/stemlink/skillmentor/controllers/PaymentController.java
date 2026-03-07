package com.stemlink.skillmentor.controllers;



import com.auth0.jwt.JWT;
import com.stemlink.skillmentor.dto.PaymentDTO;
import com.stemlink.skillmentor.entities.Payment;
import com.stemlink.skillmentor.security.UserPrincipal;
import com.stemlink.skillmentor.services.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;
    @PostMapping
    public Payment createPayment(
            @RequestBody PaymentDTO paymentDTO,
            Authentication authentication
    ) {

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        String email = userPrincipal.getEmail();

        return paymentService.createPayment(paymentDTO, email);
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayment(){
        return ResponseEntity.ok(paymentService.getAllPayment());

    }
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Integer id){
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Payment> updatePayment(@PathVariable Integer id, PaymentDTO paymentDTO){
        return ResponseEntity.ok(paymentService.updatePayment(id,paymentDTO));

    }

    @DeleteMapping("/{id}")
    public void deletePayment(@PathVariable Integer id){
        paymentService.deletePayment(id);
    }



}
