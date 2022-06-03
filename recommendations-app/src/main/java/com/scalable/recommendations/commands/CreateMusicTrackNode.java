package com.scalable.recommendations.commands;

import com.arangodb.entity.BaseDocument;
import com.scalable.recommendations.constants.DatabaseConstants;
import scalable.com.exceptions.ValidationException;
import scalable.com.shared.classes.Arango;
import scalable.com.shared.classes.Responder;

public class CreateMusicTrackNode extends  RecommendationsCommand {
    private String track_id;
    private String track_name;
    @Override
    public String getCommandName() {
        return "CreateMusicTrackNode";
    }

    @Override
    public String execute() {


        Arango arango;
        try{

            arango=Arango.getInstance();
            System.out.println("Got instance");

            System.out.println(arango.containsCollection("spotifyArangoDb","users"));

            BaseDocument trackNode = new BaseDocument();
            trackNode.setKey(track_id);
            trackNode.addAttribute("track_name",track_name);
            trackNode.addAttribute("id",track_id);

             arango.createDocument(DatabaseConstants.DATABASE_NAME,DatabaseConstants.MUSIC_DOCUMENT_COLLECTION,trackNode) ;
        } catch (Exception e){
            Responder.makeErrorResponse(e.getMessage(),404);
        }
        return Responder.makeMsgResponse("Node Track Created Successfully");

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
            this.track_name=body.getString("track_name");

        }   catch (Exception e){
            e.printStackTrace();
            throw new ValidationException("attributes data types are wrong: "+e.getMessage());
        }

    }
}
