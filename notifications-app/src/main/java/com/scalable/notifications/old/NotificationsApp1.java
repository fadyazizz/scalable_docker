//package com.scalable.notifications.old;
//
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import com.google.firebase.messaging.FirebaseMessaging;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.Bean;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//
//@SpringBootApplication
//public class NotificationsApp1 {
//
//    @Bean
//    FirebaseMessaging firebaseMessaging() throws IOException {
////        GoogleCredentials googleCredentials = GoogleCredentials
////                .fromStream(new ClassPathResource("firebase-service-account.json").getInputStream());
////        FirebaseOptions firebaseOptions = FirebaseOptions
////                .builder()
////                .setCredentials(googleCredentials)
////                .build();
//        FileInputStream serviceAccount =
//                new FileInputStream("/Users/abdelrahman.sweilam/Desktop/GUC/Semester 10/Scalable Apps/2-Project/Final-Destination/notifications-app/src/main/resources/firebase-service-account.json");
//
//        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
//                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                .build();
//        FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "notifications-app");
//        return FirebaseMessaging.getInstance(app);
//    }
//
//    public static void main(String[] args) {
//        SpringApplication.run(NotificationsApp1.class,args);
//
//    }
//}
