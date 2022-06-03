package com.scalable.recommendations.commands;

import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.BaseEdgeDocument;
import com.scalable.recommendations.constants.DatabaseConstants;
import scalable.com.exceptions.ValidationException;
import scalable.com.shared.classes.Arango;
import scalable.com.shared.classes.Responder;

import javax.validation.constraints.NotBlank;

public class CreateUserEdge extends RecommendationsCommand {
    private String followed_user_id;
    @Override
    public String getCommandName() {
        return "CreateUserEdge";
    }

    @Override
    public String execute() {
         int user_id =Integer.parseInt(this.tokenPayload.getString("id"));
        Arango arango;
        try{
            arango=Arango.getInstance();
            System.out.println(arango.createEdgeDocument(DatabaseConstants.DATABASE_NAME,DatabaseConstants.USER_EDGE_COLLECTION,DatabaseConstants.USER_DOCUMENT_COLLECTION+"/"+followed_user_id,DatabaseConstants.USER_DOCUMENT_COLLECTION+"/"+user_id+""));
             System.out.println("added edge") ;

        } catch (Exception e){
            Responder.makeErrorResponse(e.getMessage(),404);
        }
        return Responder.makeMsgResponse("User Edge Created Successfully");
    }

    @Override
    public String getRestAPIMethod() {
        return "POST";
    }

    @Override
    public boolean isAuthNeeded() {
        return true;
    }

    @Override
    public void validateAttributeTypes() throws ValidationException {
        try{
            this.followed_user_id=body.getString("followed_user_id");
        }
        catch (Exception e){
            e.printStackTrace();
            throw new ValidationException("attributes data types are wrong: "+e.getMessage());
        }


    }
}
