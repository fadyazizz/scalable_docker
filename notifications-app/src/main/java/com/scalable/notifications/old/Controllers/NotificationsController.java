//package com.scalable.notifications.old.Controllers;
//
//import com.google.firebase.messaging.FirebaseMessagingException;
////import com.scalable.notifications.old.Services.NotificationsService;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//@Controller
//public class NotificationsController {
//    private final NotificationsService firebaseService;
//
//    public NotificationsController(NotificationsService firebaseService) {
//        this.firebaseService = firebaseService;
//    }
//
//    @RequestMapping("/send-notification")
//    @ResponseBody
//    public String sendNotification(@RequestBody String title, @RequestBody String body,
//                                   @RequestParam String topic) throws FirebaseMessagingException {
//        return firebaseService.sendNotification(title, body, topic);
//    }
//}
