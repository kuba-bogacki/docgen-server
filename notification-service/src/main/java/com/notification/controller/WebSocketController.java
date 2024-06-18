package com.notification.controller;

import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@AllArgsConstructor
public class WebSocketController {

//    @MessageMapping("/broadcast")
//    @SendToUser("/queue/replay")
//    public String petitionNotificationMessage(@Payload String messageToPublish) {
//        return messageToPublish;
//    }

//    @MessageMapping("/hello")
//    @SendToUser("/topic/replay")
//    public String petitionNotificationMessage(@Payload String messageToPublish, Principal principal) throws Exception {
//        greetingService.addUserName(principal.getName()); // store UUID
//        return messageToPublish;
//    }
//
//    @MessageMapping("/user-message-{userName}")
//    public String sendToOtherUser(@Payload String messageToPublish, @DestinationVariable String userName, @Header("simpSessionId") String sessionId) {
//        return messageToPublish;
//    }
}
