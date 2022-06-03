package com.scalable.notifications;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.*;

import java.io.IOException;

public class FirebaseMessagingConnector {
    private static FirebaseMessagingConnector instance = null;

    private final FirebaseMessaging firebaseMessaging;

    private FirebaseMessagingConnector() throws IOException {
        FirebaseInit.initialize();
        this.firebaseMessaging = FirebaseMessaging.getInstance(FirebaseApp.getInstance("notifications-app"));
    }

    public static FirebaseMessagingConnector getInstance() {
        if (instance == null) {
            FirebaseMessagingConnector temp = null;
            try {
                temp = new FirebaseMessagingConnector();
            } catch (IOException e) {
                e.printStackTrace();
            }
            instance = temp;
        }
        return instance;
    }

    public String notify(String title, String body, String topic)
            throws FirebaseMessagingException{
        Notification notification = Notification
                .builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message
                .builder()
                .setTopic(topic)
                .setNotification(notification)
                .build();

        String response = null;
        try {
            response = firebaseMessaging.send(message);
        } catch (Exception e){
            e.printStackTrace();
            throw new Error(e);
        }

        return "Successfully sent message: " + response;
    }
}
