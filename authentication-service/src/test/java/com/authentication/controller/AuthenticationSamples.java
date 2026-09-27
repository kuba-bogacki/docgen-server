package com.authentication.controller;

import com.authentication.model.dto.*;
import com.authentication.model.type.Currency;
import com.authentication.model.type.Gender;
import com.authentication.model.type.Membership;
import com.authentication.security.AuthenticationRequest;
import com.authentication.security.AuthenticationResponse;
import com.authentication.security.RegisterRequest;

import java.util.UUID;

class AuthenticationSamples {

    final String userId = "3fe91e6c-e5f4-4dc2-990a-0f68cf415740";
    final String companyId = "039561e3-a151-46f3-945c-16dc4f7535e9";
    final String paymentIntentId = "d232639a-a6ea-45c9-9c80-7cc86a848f03";
    final String userFirstNameI = "John";
    final String userLastNameI = "Paul";
    final String updatedUserLastNameI = "Biden";
    final String userEmail = "john.paul@wp.pl";
    final String userPassword = "secret";
    final String userPrincipal = "0123456789";
    final String jwtToken = "jwt-token";
    final String refreshToken = "refresh-token";
    final String paymentStatus = "payment-cancelled";
    final String registrationCode = "registration-code";
    final String userPhotoFileName = "photo_01.jpg";
    final String paymentClientSecret = "payment-client-secret";
    final String userVerificationCode = "user-verification-code";

    final RegisterRequest registerRequest = RegisterRequest.builder()
            .userFirstNameI(userFirstNameI)
            .userLastNameI(userLastNameI)
            .userEmail(userEmail)
            .userPassword(userPassword)
            .userGender(Gender.MALE.toString())
            .termsAndCondition(true)
            .build();

    final AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
            .userEmail(userEmail)
            .userPassword(userPassword)
            .build();

    final AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
            .jwtToken(jwtToken)
            .refreshToken(refreshToken)
            .build();

    final UserPrincipalDto userPrincipalDto = UserPrincipalDto.builder()
            .userId(userId)
            .userPrincipal(userPrincipal)
            .build();

    final UserDto userDto = UserDto.builder()
            .userId(UUID.fromString(userId))
            .userFirstNameI(userFirstNameI)
            .userLastNameI(userLastNameI)
            .userEmail(userEmail)
            .userGender(Gender.MALE)
            .userVerificationCode(userVerificationCode)
            .userPrincipal(userPrincipal)
            .build();

    final PaymentDto paymentDto = PaymentDto.builder()
            .membership(Membership.GOLD)
            .packagePrice(1000)
            .priceCurrency(Currency.USD)
            .build();

    final PaymentIntentDto paymentIntentDto = PaymentIntentDto.builder()
            .paymentIntentId(paymentIntentId)
            .paymentClientSecret(paymentClientSecret)
            .build();

    final MembershipDto membershipDto = MembershipDto.builder()
            .membership(Membership.GOLD)
            .build();
}
