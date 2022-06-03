package scalable.com.music.commands;

import com.arangodb.ArangoCursor;
import com.arangodb.entity.BaseDocument;
import org.json.JSONObject;
import scalable.com.exceptions.ValidationException;
import scalable.com.shared.classes.Arango;
import scalable.com.shared.classes.DatabaseTablesNames;
import scalable.com.shared.classes.Responder;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class GetSongCommand extends MusicCommand{
    //Input songID to Songs Collection to output AudioPath

    @NotBlank(message="trackID should not be empty")
    private String songID;
    @Override
    public String getCommandName() {
        return "GetSong";
    }

    @Override
    public String execute() {
        System.out.println("I am executing the GetSong Command");
        JSONObject response=new JSONObject();
        ArangoCursor<BaseDocument> cursor = null;
        DatabaseTablesNames constant_db_vars = new DatabaseTablesNames();
        try{

            Arango arango = Arango.getInstance();
            //arango.createEdgeDocument(constant_db_vars.ARANGODB_DB_NAME, "Listens","Songs/162756","Users/159547");
            HashMap<String, Object> bindVars = new HashMap<>();
            String aqlQuery = String.format("FOR song IN @@table_name FILTER song.ID == @songID RETURN song");
            bindVars.put("@table_name",constant_db_vars.ARANGODB_SONGS_TABLE_NAME);
            bindVars.put("songID",songID);
            cursor = arango.query( constant_db_vars.ARANGODB_DB_NAME, aqlQuery, bindVars);
            //check cursor size == 1 and is not null
            if(!(Objects.isNull(cursor))){
                List<BaseDocument> song = cursor.asListRemaining();
                if(song.size() ==1){
                    for (BaseDocument result_row : song) {
                        response.put("AudioPath",result_row.getAttribute("AudioPath"));

                    }
                }
            }else{
                Responder.makeMsgResponse("No song exists");
            }


        }
        catch (Exception e){
            System.out.println(e.toString());
            return  Responder.makeErrorResponse("Something went wrong",400);
        }
        return Responder.makeDataResponse(response);
    }

    @Override
    public String getRestAPIMethod() {
        return "POST";
    }

    @Override
    //TODO CHANGE TO TRUE
    public boolean isAuthNeeded() {
        return false;
    }

    @Override
    public void validateAttributeTypes() throws ValidationException {

        try {

            this.songID = body.getString("songID");
        }
        catch (Exception e){
            throw new ValidationException("attributes data types are wrong");
        }
        this.validateAnnotations();

    }
}
