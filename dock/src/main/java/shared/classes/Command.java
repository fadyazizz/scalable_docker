package shared.classes;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Properties;

public abstract class Command {
    private static HashMap<String,Double> classesVersions=new HashMap<String,Double>();
     public abstract String getCommandName();
    public abstract String execute(JSONObject request);
    public Properties validationProperties;

     public String getClassVersion(String className){
        if (classesVersions.containsKey(className)){
            return   Responder.makeMsgResponse("class version is: "+classesVersions.get(className).toString());
        }

         return Responder.makeMsgResponse("class version is: "+1.0);
     }
     public String setClassVersion(String className,double classVer){
         
        classesVersions.put(className,classVer);
         return Responder.makeMsgResponse("class version is set successfully with value: "+classVer);
     }
    
}
