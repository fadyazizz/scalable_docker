package scalable.com.user.commands;

import com.arangodb.entity.BaseDocument;
import org.json.JSONObject;
import scalable.com.exceptions.ValidationException;
import scalable.com.shared.App;
import scalable.com.shared.classes.Arango;
import scalable.com.shared.classes.ByCryptHelper;
import scalable.com.shared.classes.PostgresConnection;
import scalable.com.shared.classes.Responder;

import javax.validation.constraints.NotBlank;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends UserCommand  {
    @NotBlank(message = "username should not be empty")
    private String username;
    @NotBlank(message = "password should not be empty")
     private String password;
    @NotBlank(message = "email should not be empty")
     private String email;
    @NotBlank(message = "first name should not be empty")
     private String firstName;
    @NotBlank(message = "last name should not be empty")
     private String lastName;
    
     private boolean isArtist;
     private boolean isPremium;
    @Override
    public String getCommandName() {
        return "SignUp";
    }

    @Override
    public String execute() {


        System.out.println("iam in execute1");
        //cases 1- username already exists 2- email already exists
        

        PreparedStatement preparedStatement = null;
        Connection connection = null;
        try {
            JSONObject j=new JSONObject();
            j.put("fady","sdsdas");
            App.sendMessageToApp("Music",this.origRequest,"POST","Test",null,j);

//            JSONObject communcateWithApp=new JSONObject(this.origRequest.toString());
//
//            communcateWithApp.put("commandName","Test");
//            System.out.println(communcateWithApp);
//            communcateWithApp=App.communicateWithApp(
//                    "user",
//                    "music",
//                    this.origRequest,
//                    "GET",
//                    "Test",
//                    null,
//                    null);
//            System.out.println(communcateWithApp+"message!!!!");
            String password= ByCryptHelper.hash(this.password);
            System.out.println(password+" this is the hashed password");
            String procCall = "call userregister(?,?,?,?,?,?)";
            connection = PostgresConnection.getDataSource().getConnection();
            connection.setAutoCommit(true);
            preparedStatement = connection.prepareStatement(procCall);
            preparedStatement.setString(1, this.username);
            preparedStatement.setString(2, this.email);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, this.firstName);
            preparedStatement.setString(5, this.lastName);
            preparedStatement.setBoolean(6, this.isArtist);
            preparedStatement.executeUpdate();

             
        } catch (SQLException e) {
            if (e.getMessage().contains("(username)"))
                return Responder.makeErrorResponse("username already exists", 406);
            if (e.getMessage().contains("(email)"))
                return Responder.makeErrorResponse("email already registerd", 406);
            System.out.println("here" + e.getMessage());
            return  Responder.makeErrorResponse(e.getMessage(),400);
        } finally {

            PostgresConnection.disconnect(null, preparedStatement, connection);
        }

        return Responder.makeMsgResponse("successfully registered");
        
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
            System.out.println("iam in validate");
            this.username = body.getString("username");
            this.password = body.getString("password");
            this.email=body.getString("email");
            this.firstName= body.getString("firstname");
            this.lastName=body.getString("lastname");
            this.isArtist=body.getBoolean("isArtist");
            this.isPremium=false;
        }
        catch (Exception e){
            throw new ValidationException("attributes data types are wrong: "+e.getMessage());
        }
        
    }
}
