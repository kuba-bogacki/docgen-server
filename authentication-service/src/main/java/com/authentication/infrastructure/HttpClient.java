package com.authentication.infrastructure;

import com.authentication.model.User;
import com.authentication.model.dto.UserEventDto;
import com.authentication.security.AuthenticationResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface HttpClient {
    ResponseEntity<?> getEmailStatus(UserEventDto userEventDto);
    ResponseEntity<?> getJoiningStatus(User user, String companyId, String userEmail);
    ResponseEntity<?> sendRefreshToken(String userPrincipal, AuthenticationResponse authenticationResponse, String userEmail);
    List<UUID> getMemberUuidList(String companyId, String userEmail);
}
