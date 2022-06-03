package com.scalable.notifications;

import scalable.com.shared.App;
import scalable.com.shared.classes.Arango;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class NotificationsApp extends App {
    public static Arango arangoPool;

    public static void main(String[] args) throws IOException, ClassNotFoundException, TimeoutException {
        NotificationsApp app = new NotificationsApp();
        arangoPool = new Arango();
        app.dbInit();
        app.start();
    }

    @Override
    public void dbInit() throws IOException {
        Arango arango = Arango.getInstance();
        arango.createPool(15);
        arango.createDatabaseIfNotExists("NotificationsDB");
        arango.createCollectionIfNotExists("NotificationsDB","Notifications",false);
    }
    @Override
    protected String getAppName() {
        return "Notifications";
    }
}
