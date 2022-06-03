package com.scalable.notifications.commands;

import com.arangodb.entity.BaseDocument;
import com.scalable.notifications.FirebaseMessagingConnector;
import scalable.com.exceptions.ValidationException;
import scalable.com.shared.classes.Arango;
import scalable.com.shared.classes.CommandVerifier;
import scalable.com.shared.classes.Responder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class SendNotification extends CommandVerifier {

    @NotNull(message="reported id should not be empty")
    private String title;
    @NotBlank(message="report comment should not be empty")
    private String notification_body;
    @NotBlank(message="report comment should not be empty")
    private String topic;

    @Override
    public String getCommandName() {
        return "SendNotification";
    }

    @Override
    public String execute() {
//        int id=Integer.parseInt(this.tokenPayload.getString("id"));
        Arango arango = null;
        try {
            final String notificationResult = FirebaseMessagingConnector.getInstance().notify(this.title, this.notification_body, this.topic);
            arango = Arango.getInstance();
            arango.createCollectionIfNotExists("NotificationsDB","Notifications",false);
            BaseDocument doc = new BaseDocument();
            doc.addAttribute("title", this.title);
            doc.addAttribute("body", this.notification_body);
            doc.addAttribute("topic", this.topic);
            BaseDocument res = arango.createDocument("NotificationsDB","Notifications", doc);
            System.out.println("Notification "+res+" added to DB");
            return Responder.makeMsgResponse(notificationResult);
        } catch (Exception e){
            return Responder.makeErrorResponse(e.getMessage(), 500);
        }
    }

    @Override
    public String getRestAPIMethod() {
        return "POST";
    }

    @Override
    public boolean isAuthNeeded() {
        return false;
    }

    @Override
    public void validateAttributeTypes() throws ValidationException {
        try {
            this.title = body.getString("title");
            this.notification_body = body.getString("body");
            this.topic = body.getString("topic");
        }
        catch (Exception e){
            throw new ValidationException("attributes data types are wrong");
        }
        this.validateAnnotations();
    }
}
