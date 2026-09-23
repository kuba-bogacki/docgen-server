package com.notification.infrastructure;

import com.notification.model.dto.CompanyDto;
import com.notification.model.dto.UserDto;
import com.notification.model.dto.UserPrincipalDto;
import org.springframework.http.ResponseEntity;

public interface HttpClient {
    UserDto getUserDtoById(String userId, String userEmail);
    UserDto getCurrentUserDto(String userEmail);
    CompanyDto getCompanyDtoById(String companyId, String userEmail);
    ResponseEntity<?> addUserPrincipalName(UserPrincipalDto userPrincipalDto, String userEmail);
    ResponseEntity<?> addNewCompanyMember(String companyId, String userId, String userEmail);
}
