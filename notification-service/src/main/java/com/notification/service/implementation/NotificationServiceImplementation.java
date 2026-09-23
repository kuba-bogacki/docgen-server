package com.notification.service.implementation;

import com.notification.configuration.properties.JwtProperties;
import com.notification.exception.CurrentUserNotFoundException;
import com.notification.exception.EntityNotFoundException;
import com.notification.exception.EventSendFailureException;
import com.notification.exception.InvitationSendFailureException;
import com.notification.infrastructure.HttpClient;
import com.notification.mapper.NotificationMapper;
import com.notification.model.Notification;
import com.notification.model.dto.*;
import com.notification.repository.NotificationRepository;
import com.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImplementation implements NotificationService {

    private final HttpClient httpClient;
    private final JwtProperties jwtProperties;
    private final NotificationMapper notificationMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final NotificationRepository notificationRepository;

    @Override
    public List<NotificationDto> getUserNotifications(String userEmail) {
        final var currentUserDto = httpClient.getCurrentUserDto(userEmail);

        if (Objects.isNull(currentUserDto.getUserId())) {
            throw new CurrentUserNotFoundException("Impossible to get current user id by token credential");
        }

        var currentUserNotificationList = notificationRepository.findNotificationsByNotificationReceiverId(currentUserDto.getUserId());
        return notificationMapper.toNotificationDtoList(currentUserNotificationList);
    }

    @Override
    public void sendRefreshToken(AuthenticationResponse authenticationResponse, String userPrincipal) {
        simpMessagingTemplate.convertAndSendToUser(userPrincipal, "/queue/refresh-token", authenticationResponse);
    }

    @Override
    public void sendUserPrincipalName(String jwtToken, Principal principal) {
        final var userEmail = jwtProperties.getUserEmail(jwtToken);
        var currentUserDto = httpClient.getCurrentUserDto(userEmail);
        if (Objects.isNull(currentUserDto)) {
            throw new InvitationSendFailureException("Current user not found in database");
        }

        var userPrincipalDto = createUserPrincipalDto(currentUserDto.getUserId(), principal.getName());
        var response = httpClient.addUserPrincipalName(userPrincipalDto, userEmail);

        if (response.getStatusCode().is4xxClientError()) {
            throw new EntityNotFoundException("Couldn't set user principal name.");
        }
    }

    @Override
    public void deleteUserNotification(String notificationId) {
        deleteNotification(notificationId);
    }

    @Override
    @Transactional
    public void sendMembershipPetition(NotificationRequest request, String jwtToken) {
        final var userEmail = jwtProperties.getUserEmail(jwtToken);
        var currentUserDto = httpClient.getCurrentUserDto(userEmail);

        if (Objects.isNull(currentUserDto)) {
            throw new InvitationSendFailureException("Impossible to send membership petition - current user not found");
        }

        var companyDto = httpClient.getCompanyDtoById(request.getNotificationCompanyId(), userEmail);

        if (Objects.isNull(companyDto)) {
            throw new InvitationSendFailureException("Impossible to send membership petition - current company is null");
        }

        var userDtoList = companyDto.getCompanyMembers().stream()
                .map(memberId -> httpClient.getUserDtoById(memberId.toString(), userEmail))
                .collect(Collectors.toSet());

        if (userDtoList.isEmpty()) {
            throw new InvitationSendFailureException("Impossible to send membership petition - current user list is empty");
        }

        final var updatedMessage = updateMembershipRequestMessage(request.getNotificationMessage(), currentUserDto, companyDto);
        request.setNotificationMessage(updatedMessage);
        userDtoList.forEach(companyUser ->
                sendPetitionRequestNotification(request, currentUserDto.getUserId(), companyUser.getUserId(), companyUser.getUserPrincipal()));
    }

    @Override
    @Transactional
    public void acceptMembershipPetition(NotificationRequest request, String jwtToken) {
        final var userEmail = jwtProperties.getUserEmail(jwtToken);
        var userDto = httpClient.getUserDtoById(request.getNotificationReceiverId(), userEmail);

        if (Objects.isNull(userDto)) {
            throw new InvitationSendFailureException("Impossible to send membership response - receiver user not found");
        }

        var companyDto = httpClient.getCompanyDtoById(request.getNotificationCompanyId(), userEmail);

        if (Objects.isNull(companyDto)) {
            throw new InvitationSendFailureException("Impossible to send membership petition - current company is null");
        }

        final var updatedMessage = updateCompanyInfoMessage(request.getNotificationMessage(), companyDto);
        request.setNotificationMessage(updatedMessage);
        request.setNotificationUserPrincipal(userDto.getUserPrincipal());

        var joiningStatus = httpClient.addNewCompanyMember(request.getNotificationCompanyId(), userDto.getUserId(), userEmail);

        if (joiningStatus.getStatusCode().is4xxClientError()) {
            throw new InvitationSendFailureException(String.format("Couldn't add user as a member to company with id: %s.", request.getNotificationCompanyId()));
        }

        var notificationDto =
                createNotificationDto(request.getNotificationUserPrincipal(), request, request.getNotificationRequesterId(), request.getNotificationReceiverId());
        saveAndSend(notificationDto, "/queue/membership-petition");
        deleteNotification(request.getNotificationId());
    }

    @Override
    @Transactional
    public void sendNewEventInfo(NotificationRequest request, String jwtToken) {
        final var userEmail = jwtProperties.getUserEmail(jwtToken);
        var currentUserDto = httpClient.getCurrentUserDto(userEmail);

        if (Objects.isNull(currentUserDto)) {
            throw new EventSendFailureException("Impossible to send new event request - current user not found");
        }

        var companyDto = httpClient.getCompanyDtoById(request.getNotificationCompanyId(), userEmail);

        if (Objects.isNull(companyDto)) {
            throw new EventSendFailureException("Impossible to send new event request - current company is null");
        }

        var userDtoList = companyDto.getCompanyMembers().stream()
                .map(memberId -> httpClient.getUserDtoById(memberId.toString(), userEmail))
                .collect(Collectors.toSet());

        if (userDtoList.isEmpty()) {
            throw new EventSendFailureException("Impossible to send new event request - current user list is empty");
        }

        final var updatedMessage = updateCompanyInfoMessage(request.getNotificationMessage(), companyDto);
        request.setNotificationMessage(updatedMessage);
        userDtoList.forEach(companyUser -> sendNewEventRequestNotification(request, currentUserDto.getUserId(), companyUser.getUserId(), companyUser.getUserPrincipal()));
    }

    private void deleteNotification(String notificationId) {
        Optional<Notification> entity = notificationRepository.findById(notificationId);
        if (entity.isEmpty()) {
            throw new EntityNotFoundException(String.format("Notification with id [%s] not found", notificationId));
        }
        notificationRepository.delete(entity.get());
    }

    private String updateMembershipRequestMessage(String message, UserDto userDto, CompanyDto companyDto) {
        return message.replace("[[user]]", String.format("%s %s", userDto.getUserFirstNameI(), userDto.getUserLastNameI()))
                .replace("[[email]]", userDto.getUserEmail()).replace("[[company]]", companyDto.getCompanyName());
    }

    private String updateCompanyInfoMessage(String message, CompanyDto companyDto) {
        return message.replace("[[company]]", companyDto.getCompanyName());
    }

    private void sendPetitionRequestNotification(NotificationRequest request, String requesterId, String receiverId, String principal) {
        var notificationDto = createNotificationDto(principal, request, requesterId, receiverId);
        saveAndSend(notificationDto, "/queue/membership-petition");
    }

    private void sendNewEventRequestNotification(NotificationRequest request, String requesterId, String receiverId, String principal) {
        var notificationDto = createNotificationDto(principal, request, requesterId, receiverId);
        saveAndSend(notificationDto, "/queue/event-info");
    }

    private void saveAndSend(NotificationDto notificationDto, String path) {
        final var notification = notificationMapper.toNotificationEntity(notificationDto);
        final var entity = notificationRepository.save(notification);
        simpMessagingTemplate.convertAndSendToUser(notificationDto.getNotificationUserPrincipal(), path, notificationMapper.toNotificationDto(entity));
    }

    private NotificationDto createNotificationDto(String userPrincipal, NotificationRequest request, String requesterId, String receiverId) {
        return NotificationDto.builder()
                .notificationCompanyId(request.getNotificationCompanyId())
                .notificationUserPrincipal(userPrincipal)
                .notificationRequesterId(requesterId)
                .notificationReceiverId(receiverId)
                .notificationMessage(request.getNotificationMessage())
                .notificationType(request.getNotificationType())
                .build();
    }

    private UserPrincipalDto createUserPrincipalDto(String userId, String userPrincipal) {
        return UserPrincipalDto.builder()
                .userId(userId)
                .userPrincipal(userPrincipal)
                .build();
    }
}
