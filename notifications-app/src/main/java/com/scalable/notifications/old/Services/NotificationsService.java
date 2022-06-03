//package com.scalable.notifications.old.Services;
//
//import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.messaging.FirebaseMessagingException;
//import com.google.firebase.messaging.Message;
//import com.google.firebase.messaging.Notification;
//import org.springframework.stereotype.Service;
//
//@Service
//public class NotificationsService {
//
//    private final FirebaseMessaging firebaseMessaging;
//
//    public NotificationsService(FirebaseMessaging firebaseMessaging) {
//        this.firebaseMessaging = firebaseMessaging;
//    }
//
//    public String sendNotification(String title, String body, String topic) throws FirebaseMessagingException {
//
//        Notification notification = Notification
//                .builder()
//                .setTitle(title)
//                .setBody(body)
//                .build();
//
//        Message message = Message
//                .builder()
//                .setTopic(topic)
//                .setNotification(notification)
//                .build();
//
//        return firebaseMessaging.send(message);
//    }
//
//}
