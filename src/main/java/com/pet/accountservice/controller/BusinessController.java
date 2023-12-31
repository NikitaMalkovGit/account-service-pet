package com.pet.accountservice.controller;

import com.pet.accountservice.dto.payment.PostPaymentDto;
import com.pet.accountservice.model.user.User;
import com.pet.accountservice.service.BusinessService;
import com.pet.accountservice.util.ResponseStatus;
import com.pet.accountservice.util.ValidList;
import com.pet.accountservice.validator.Validators;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("api")
public class BusinessController {

    private final BusinessService businessService;

    @GetMapping("empl/payment")
    public ResponseEntity<?> getPayment(@RequestParam Optional<String> period,
                                        @AuthenticationPrincipal User user) {
        if (period.isPresent()) {
            Validators.validatePeriod(period.get());
            return ResponseEntity.ok(businessService.getUserPaymentByPeriod(user, period.get()));
        }
        return ResponseEntity.ok(businessService.getUserPayments(user));
    }

    @PostMapping("acct/payments")
    public ResponseEntity<?> uploadPayment(@Valid @RequestBody ValidList<PostPaymentDto> payments) {
        businessService.uploadPayment(payments);
        return ResponseStatus.builder()
                .add("status", "Added successfully!")
                .build();
    }

    @PutMapping("acct/payments")
    public ResponseEntity<?> updatePayment(@Valid @RequestBody PostPaymentDto payment) {
        businessService.updatePayment(payment);
        return ResponseStatus.builder()
                .add("status", "Updated successfully!")
                .build();
    }

}
