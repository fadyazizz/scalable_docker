package com.scalable.notifications;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.FileInputStream;

public class FirebaseInit {
    private static boolean initialized;

    public static boolean initialize() {
        try {
            if (!initialized) {
                FileInputStream serviceAccount =
                        new FileInputStream(System.getProperty("user.dir") + "/src/main/resources/firebase-service-account.json");

                FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
                FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "notifications-app");
                initialized = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
