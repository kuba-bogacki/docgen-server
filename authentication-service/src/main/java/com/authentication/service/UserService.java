package com.authentication.service;

import com.authentication.exception.*;
import com.authentication.model.dto.UserDto;
import com.authentication.security.AuthenticationRequest;
import org.springframework.web.multipart.MultipartFile;

import java.rmi.NoSuchObjectException;

public interface UserService {
    UserDto getUserDtoByUserEmail(String userEmail) throws UserNotFoundException;
    Boolean sendVerificationEmail(String userEmail) throws UserNotFoundException, UserWebClientException;
    Boolean resetUserPassword(String verificationCode, AuthenticationRequest authenticationRequest) throws UserNotFoundException, UserAuthenticationException;
    UserDto updateUserData(UserDto userDto) throws UserNotFoundException;
    String uploadNewUserPhoto(MultipartFile multipartFile, String userEmail) throws UserNotFoundException, UserUploadPhotoException;
    UserDto getUserNotCompanyMember(String companyId, String jwtToken, String userEmail) throws NoSuchObjectException, UserAlreadyExistException;
    UserDto getUserDtoByUserId(String userId) throws UserNotFoundException;
}
