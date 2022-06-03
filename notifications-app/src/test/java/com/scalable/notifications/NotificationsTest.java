package com.scalable.notifications;

import com.scalable.notifications.commands.SendNotification;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;
import scalable.com.shared.classes.Arango;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

public class NotificationsTest {

    private static Arango arango;
    @BeforeClass
    public static void setUp() {
        try {
            arango = Arango.getConnectedInstance();
            NotificationsApp app= new NotificationsApp();
            app.dbInit();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public static String sendNotification(String title, String notification_body, String topic) {
        JSONObject body = new JSONObject();
        body.put("title", title);
        body.put("body", notification_body);
        body.put("topic", topic);

        JSONObject uriParams = new JSONObject();

        JSONObject token= new JSONObject();


        JSONObject request = new JSONObject();
        request.put("body", body);
        request.put("methodType", "POST");
        request.put("uriParams", uriParams);
        request.put("isAuthenticated",false);
        request.put("tokenPayload", token);

        SendNotification sendNotification = new SendNotification();
        return sendNotification.execute(request);
    }

    @Test
    public void sendNotificationTest() {
        int beforeCount = arango.documentCount("NotificationsDB", "Notifications");
        String response = sendNotification("test title", "test body", "testtopic");
        JSONObject responseJson = new JSONObject(response);
        int afterCount = arango.documentCount("NotificationsDB", "Notifications");
        assertEquals("Successfully sent message", responseJson.getString("msg").substring(0,25));
        assertEquals(beforeCount + 1, afterCount);
    }
}
