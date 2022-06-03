package shared.testsHelper;

import org.json.JSONObject;
import shared.App;
import shared.classes.Command;
import shared.classes.JWTHandler;


import java.util.Properties;

public class TestHelper {
    public static App appBeingTested;
    public static String execute(Command command, JSONObject request){
        if (!request.has(JWTHandler.IS_AUTHENTICATED)){
             request.put(JWTHandler.IS_AUTHENTICATED,false);
        }
       Properties properties=  appBeingTested.classManager.validationMap.get(command.getCommandName());
       command.validationProperties=properties;
       return command.execute(request);
    }
    public static void attachTokenPayLoad(String token,JSONObject request){
          
          Object tokenPayload=JWTHandler.decodeToken(token);
         
          JSONObject temp=(JSONObject)tokenPayload;
          request.put(JWTHandler.TOKEN_PAYLOAD,((JSONObject) tokenPayload).get(JWTHandler.TOKEN_PAYLOAD));

          request.put(JWTHandler.IS_AUTHENTICATED,true);

    }

}
