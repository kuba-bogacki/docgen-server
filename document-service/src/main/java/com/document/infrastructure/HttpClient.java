package com.document.infrastructure;

import com.document.model.dto.CompanyDto;
import com.document.model.dto.EvidenceNotificationDto;
import com.document.model.dto.UserDto;

public interface HttpClient {

    UserDto getCurrentUserDto(String userEmail);
    CompanyDto getCurrentCompanyDto(String companyId, String userEmail);
    void createCurrentUserNotification(String userEmail, EvidenceNotificationDto evidenceNotificationDto);
}
