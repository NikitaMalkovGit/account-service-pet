package com.pet.accountservice.service;

import com.pet.accountservice.dto.payment.GetPaymentDto;
import com.pet.accountservice.dto.payment.PostPaymentDto;
import com.pet.accountservice.mapper.Mapper;
import com.pet.accountservice.model.Payment;
import com.pet.accountservice.model.user.User;
import com.pet.accountservice.repository.PaymentRepository;
import com.pet.accountservice.validator.Validators;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class BusinessService {

    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final Mapper mapper;

    public List<GetPaymentDto> getUserPayments(User user) {
        log.info("Getting payments for \"" + user.getUsername() + "\"");
        return paymentRepository.findPaymentsByUser(user)
                .stream()
                .map(mapper::paymentToGetPaymentDto)
                .collect(Collectors.toList());
    }

    public GetPaymentDto getUserPaymentByPeriod(User user, String period) {
        log.info("Getting payment for \"" + user.getUsername() + "\" at \"" + period + "\"");
        LocalDate periodDate = mapper.periodToLocalDate(period);
        return paymentRepository.findPaymentByUserAndPeriod(user, periodDate)
                .stream()
                .map(mapper::paymentToGetPaymentDto)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find resource"));
    }

    public void uploadPayment(List<PostPaymentDto> postPaymentDtoList) {
        log.info("Uploading payment \"" + postPaymentDtoList + "\"");

        Validators.validateIsOneUser(postPaymentDtoList);
        String username = mapper.emailToUsername(postPaymentDtoList.get(0).getEmployee());
        User user = userService.loadUserByUsername(username);

        List<Payment> previousPayments = paymentRepository.findPaymentsByUser(user);
        List<Payment> payments = postPaymentDtoList.stream()
                .map(p -> mapper.postPaymentDtoToPayment(p, userService))
                .sorted((p1, p2) -> p2.getPeriod().compareTo(p1.getPeriod()))
                .collect(Collectors.toList());

        previousPayments.addAll(payments);
        Validators.validateDistinctPeriodUserPairs(previousPayments);
        paymentRepository.saveAll(payments);
    }

    @Transactional
    public void updatePayment(PostPaymentDto postPaymentDto) {
        log.info("Updating payment \"" + postPaymentDto + "\"");
        Payment payment = mapper.postPaymentDtoToPayment(postPaymentDto, userService);
        List<Payment> userPayments = paymentRepository.findPaymentsByUser(payment.getUser());
        Validators.validatePaymentPeriodExist(userPayments, payment);
        paymentRepository.updatePaymentByUserAndPeriod(
                payment.getUser(),
                payment.getPeriod(),
                payment.getSalary()
        );
    }

}
