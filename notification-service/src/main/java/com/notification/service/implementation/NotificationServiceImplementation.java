package com.notification.service.implementation;

import com.notification.exception.InvitationSendFailureException;
import com.notification.mapper.NotificationMapper;
import com.notification.model.dto.CompanyDto;
import com.notification.model.dto.NotificationDto;
import com.notification.model.dto.NotificationRequest;
import com.notification.model.dto.UserDto;
import com.notification.repository.NotificationRepository;
import com.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.Principal;
import java.util.Objects;

import static com.notification.util.ApplicationConstants.API_VERSION;
import static com.notification.util.ApplicationConstants.PROTOCOL;
import static com.notification.util.UrlBuilder.addTokenHeader;
import static com.notification.util.UrlBuilder.buildUrl;

@Service
@RequiredArgsConstructor
class NotificationServiceImplementation implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final WebClient.Builder webClientBuilder;

    @Override
    public void sendMembershipPetition(NotificationRequest request, String jwtToken, Principal principal) {

        CompanyDto companyDto = webClientBuilder
                .filter(addTokenHeader(jwtToken))
                .build().get()
                .uri(buildUrl(PROTOCOL, "company-service", API_VERSION, "/company/details/" + request.getCompanyId()))
                .retrieve()
                .bodyToMono(CompanyDto.class)
                .block();

        UserDto userDto = webClientBuilder
                .filter(addTokenHeader(jwtToken))
                .build().get()
                .uri(buildUrl(PROTOCOL, "authentication-service", API_VERSION, "/authentication/user"))
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();

        if (Objects.isNull(companyDto) || Objects.isNull(userDto)) {
            throw new InvitationSendFailureException("Impossible to send membership petition - current user or current company is null");
        }

        final var updatedMessage = updateRequestMessage(request.getNotificationMessage(), userDto, companyDto);
        request.setNotificationMessage(updatedMessage);
        companyDto.getCompanyMembers().forEach(memberUuid -> sendPetitionNotification(request, principal, userDto.getUserId(), memberUuid.toString()));
    }

    @Override
    public Boolean putCustomHandshake(String userId) {
        return null;
    }

    private String updateRequestMessage(String message, UserDto userDto, CompanyDto companyDto) {
        return message.replace("[[user]]", String.format("%s %s", userDto.getUserFirstNameI(), userDto.getUserLastNameI()))
                .replace("[[email]]", userDto.getUserEmail()).replace("[[company]]", companyDto.getCompanyName());
    }

    private void sendPetitionNotification(NotificationRequest request, Principal principal, String requesterId, String receiverId) {
        final var notification = notificationMapper.toEntity(createNotificationDto(request, requesterId, receiverId));
        notificationRepository.save(notification);
        simpMessagingTemplate.convertAndSendToUser(principal.getName(), "/topic/replay", request.getNotificationMessage());
//        simpMessagingTemplate.convertAndSend("/queue/replay/" + receiverId, request.getNotificationMessage());
    }

    private NotificationDto createNotificationDto(NotificationRequest request, String requesterId, String receiverId) {
        return NotificationDto.builder()
                .notificationRequesterId(requesterId)
                .notificationReceiverId(receiverId)
                .notificationMessage(request.getNotificationMessage())
                .notificationType(request.getNotificationType())
                .build();
    }
}
