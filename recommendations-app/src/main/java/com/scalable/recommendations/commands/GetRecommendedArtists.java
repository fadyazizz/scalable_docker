package com.scalable.recommendations.commands;

import com.arangodb.ArangoCursor;
import com.arangodb.entity.BaseDocument;
import com.scalable.recommendations.constants.DatabaseConstants;
import org.json.JSONObject;
import scalable.com.exceptions.ValidationException;
import scalable.com.shared.classes.Arango;
import scalable.com.shared.classes.Responder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class GetRecommendedArtists extends  RecommendationsCommand
{

    //input nothing, ana ma3aya el userID
    //output recommended artists
    public static int limitno = 10;
    @Override
    public String getCommandName() {
        return "GetRecommendedArtists";
    }

    @Override
    public String execute() {
        System.out.println("I am executing the GetRecommendedArtists command");

        String user_id =this.tokenPayload.getString("id");
        //String user_id = "user4";
        JSONObject response=new JSONObject();
        Arango arango = Arango.getInstance();
        ArrayList<String> artist_ids = new ArrayList<String>();
        try {
            String arango_default_id = (String) get_arango_default_id(user_id, arango);
            System.out.println("Returned Arango Default ID: " + arango_default_id);
            if (arango_default_id != null) {
                String aqlQuery = String.format("FOR artist IN 2..@limitno OUTBOUND @user_id @@collection_name OPTIONS {uniqueVertices: \"global\", bfs: true} FILTER artist.isArtist == 1 RETURN artist");
                HashMap<String, Object> bindVars = new HashMap<>();
                bindVars.put("@collection_name", DatabaseConstants.USER_EDGE_COLLECTION);
                bindVars.put("user_id", arango_default_id);
                bindVars.put("limitno", limitno);
                ArangoCursor<BaseDocument> cursor = arango.query( DatabaseConstants.DATABASE_NAME, aqlQuery, bindVars);
                if(!(Objects.isNull(cursor))){
                    List<BaseDocument> artist = cursor.asListRemaining();
                        for (BaseDocument result_row : artist) {
                            artist_ids.add((String)result_row.getAttribute("id"));
                        }
                    System.out.println(artist_ids.toString());
                    response.put("recommended_artist_id", artist_ids);
                }
            }  else{
                return Responder.makeMsgResponse("No such user exists with that id");
            }
        }
        catch (Exception e){
            System.out.println(e.toString());
            return  Responder.makeErrorResponse("Something went wrong",400);
        }
        return Responder.makeDataResponse(response);


    }

    public Object get_arango_default_id(String user_id, Arango arango){

        try {
            HashMap<String, Object> bindVars = new HashMap<>();
            String aqlQuery = String.format("FOR user IN @@collection FILTER user.id == @userid RETURN user");
            bindVars.put("@collection", DatabaseConstants.USER_DOCUMENT_COLLECTION);
            bindVars.put("userid", user_id);
            ArangoCursor<BaseDocument> cursor = arango.query(DatabaseConstants.DATABASE_NAME, aqlQuery, bindVars);
            if (!(Objects.isNull(cursor))) {
                System.out.println("cursor not null");
                List<BaseDocument> user = cursor.asListRemaining();
                if (user.size() == 1) {
                    for (BaseDocument result_row : user) {
                        return result_row.getId();
                    }
                }
            } else {
                return null;
            }
        }
        catch (Exception e){
            System.out.println(e.toString());
            return  null;
        }
        return null;
    }

    @Override
    public String getRestAPIMethod() {
        return "GET";
    }


    @Override
    public boolean isAuthNeeded() {
        return true;
    }

    @Override
    public void validateAttributeTypes() throws ValidationException {

    }
}