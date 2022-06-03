package com.scalable.recommendations.commands;

import com.arangodb.ArangoCursor;
import com.arangodb.entity.BaseDocument;
import com.arangodb.velocypack.VPackSlice;
import com.scalable.recommendations.constants.DatabaseConstants;
import org.json.JSONObject;
import scalable.com.exceptions.ValidationException;
import scalable.com.shared.classes.Arango;
import scalable.com.shared.classes.Responder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class GetRecommendedMusicTracks extends RecommendationsCommand{
    //input nothing, ana ma3aya el userID
    //output recommended music tracks
    public static int limitno = 3;
    @Override
    public String getCommandName() {
        return "GetRecommendedMusicTracks";
    }

    @Override
    public String execute() {
        System.out.println("I am executing the GetRecommendedMusicTracks command");
        
        String user_id =this.tokenPayload.getString("id");
        //String user_id = "user4";
        ArrayList<String> song_ids = new ArrayList<String>();
        JSONObject response=new JSONObject();
        Arango arango = Arango.getInstance();
        try {
            String arango_default_key = (String) get_arango_default_key(user_id, arango,DatabaseConstants.USER_DOCUMENT_COLLECTION);
            String arango_default_id = DatabaseConstants.USER_DOCUMENT_COLLECTION + "/" + arango_default_key ;
            System.out.println("Returned Arango Default ID: " + arango_default_id + " ID from token " + user_id);
            if (arango_default_id != null) {
                String aqlQuery = String.format("LET similar_users = (FOR songs_history IN 1..1 OUTBOUND @user_id @@collection_name FOR user IN 1..1 INBOUND songs_history._id @@collection_name FILTER user._id != @user_id RETURN user) LET most_similar_users =(FOR similar_user IN similar_users COLLECT id = similar_user._id, name = similar_user.id, isArtist = similar_user.isArtist WITH COUNT INTO similarityCount SORT similarityCount DESC LIMIT @limitno RETURN {id,name,isArtist}  ) LET curr_user_history = (FOR songs_history IN 1..1 OUTBOUND @user_id @@collection_name RETURN TO_ARRAY(songs_history.id)) LET recommended = (FOR most_similar_user IN most_similar_users FOR song IN 1..1 OUTBOUND most_similar_user.id @@collection_name RETURN DISTINCT MINUS(FLATTEN(TO_ARRAY(song.id)),FLATTEN(curr_user_history))) FOR r in recommended FILTER length(r)>0 RETURN r");
                HashMap<String, Object> bindVars = new HashMap<>();
                bindVars.put("@collection_name", DatabaseConstants.MUSIC_EDGE_COLLECTION);
                bindVars.put("limitno", limitno);
                bindVars.put("user_id", arango_default_id);
                ArangoCursor<List> cursor = arango.query2(DatabaseConstants.DATABASE_NAME, aqlQuery, bindVars);
                for (List element : cursor) {
                    song_ids.add((String)element.get(0));

                }
                System.out.println(song_ids.toString());
                response.put("recommended_song_id", song_ids);
            } else {
                return Responder.makeMsgResponse("No such User Exists with that id");
            }

        }
        catch (Exception e){
            System.out.println(e.toString());
            return  Responder.makeErrorResponse("Something went wrong",400);
        }
        return Responder.makeDataResponse(response);



    }

    public Object get_arango_default_key(String user_id,Arango arango, String collection){

        try {
            HashMap<String, Object> bindVars = new HashMap<>();
            String aqlQuery = String.format("FOR user IN @@collection FILTER user.id == @userid RETURN user");
            bindVars.put("@collection", collection);
            bindVars.put("userid", user_id);
            ArangoCursor<BaseDocument> cursor = arango.query(DatabaseConstants.DATABASE_NAME, aqlQuery, bindVars);
            if (!(Objects.isNull(cursor))) {
                List<BaseDocument> user = cursor.asListRemaining();
                if (user.size() == 1) {
                    for (BaseDocument result_row : user) {
                        //result_row ==> [documentRevision=_eOaJKBW---, documentHandle=users/159547, documentKey=159547, properties={id=1, isArtist=0}]
                        return result_row.getKey();
                    }
                }
            } else {
                return null;
            }
        }
        catch (Exception e){
            System.out.println(e.toString());
            return null;
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
