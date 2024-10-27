package com.authentication.service;

import com.authentication.exception.UserAlreadyExistException;
import com.authentication.exception.UserAuthenticationException;
import com.authentication.exception.UserNotFoundException;
import com.authentication.exception.UserPasswordException;
import com.authentication.model.dto.UserDto;
import com.authentication.security.AuthenticationRequest;
import org.springframework.web.multipart.MultipartFile;

import java.rmi.NoSuchObjectException;

public interface UserService {
    UserDto getUserDtoByUserEmail(String userEmail) throws UserNotFoundException;
    Boolean sendVerificationEmail(String userEmail) throws UserNotFoundException, UserPasswordException;
    Boolean resetUserPassword(String verificationCode, AuthenticationRequest authenticationRequest) throws UserNotFoundException, UserAuthenticationException;
    UserDto updateUserData(UserDto userDto) throws UserNotFoundException;
    String uploadNewUserPhoto(MultipartFile multipartFile, String userEmail) throws Exception;
    UserDto getUserNotCompanyMember(String companyId, String jwtToken, String userEmail) throws NoSuchObjectException, UserAlreadyExistException;
    UserDto getUserDtoByUserId(String userId) throws UserNotFoundException;
}
