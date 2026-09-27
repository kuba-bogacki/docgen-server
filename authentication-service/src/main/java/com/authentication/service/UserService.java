package com.authentication.service;

import com.authentication.model.dto.PaymentDto;
import com.authentication.model.dto.PaymentIntentDto;
import com.authentication.model.dto.UserDto;
import com.authentication.model.type.Membership;
import com.authentication.security.AuthenticationRequest;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserDto getUserDtoByUserEmail(String userEmail);
    Boolean sendVerificationEmail(String userEmail);
    Boolean resetUserPassword(String verificationCode, AuthenticationRequest authenticationRequest);
    UserDto updateUserData(UserDto userDto);
    String uploadNewUserPhoto(MultipartFile multipartFile, String userEmail);
    UserDto getUserNotCompanyMember(String companyId, String userEmail);
    UserDto getUserDtoByUserId(String userId);
    String cancelPaymentSession(String paymentIntentId);
    PaymentIntentDto createPaymentSession(PaymentDto  paymentDto, String userEmail);
    void updateUserMembership(Membership membership, String userEmail);
}
