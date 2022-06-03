package com.scalable.recommendations.commands;

import com.scalable.recommendations.constants.DatabaseConstants;
import scalable.com.exceptions.ValidationException;
import scalable.com.shared.classes.Arango;
import scalable.com.shared.classes.Responder;

public class CreateMusicEdge extends RecommendationsCommand {
    private String track_id;
    @Override
    public String getCommandName() {
        return "CreateMusicEdge";
    }

    @Override
    public String execute() {
         int user_id =Integer.parseInt(this.tokenPayload.getString("id"));
        Arango arango;
        try{
            arango=Arango.getInstance();
            System.out.println(arango.createEdgeDocument(DatabaseConstants.DATABASE_NAME,DatabaseConstants.MUSIC_EDGE_COLLECTION,DatabaseConstants.MUSIC_DOCUMENT_COLLECTION+"/"+track_id,DatabaseConstants.USER_DOCUMENT_COLLECTION+"/"+user_id+""));
             System.out.println("added edge") ;

        } catch (Exception e){
            Responder.makeErrorResponse(e.getMessage(),404);
        }
        return Responder.makeMsgResponse("Music Edge Created Successfully");
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
            this.track_id=body.getString("track_id");
        }
        catch (Exception e){
            e.printStackTrace();
            throw new ValidationException("attributes data types are wrong: "+e.getMessage());
        }


    }
}
