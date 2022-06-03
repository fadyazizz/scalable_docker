package tests;
import com.arangodb.ArangoCursor;
import com.arangodb.entity.BaseDocument;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import scalable.com.music.commands.GetSongCommand;
import scalable.com.shared.classes.Arango;
import scalable.com.shared.classes.DatabaseTablesNames;

import java.util.Map;

import static org.junit.Assert.*;

public class GetSongCommandTest {
    private static final String songID = "1";
    private static final String AudioPath = "/ya/rab/nekhlas";
    private static Arango arango;
    private static BaseDocument song;
    private static String documentKey;
    
    @BeforeClass
    public static void setUp() {
        try {
            DatabaseTablesNames constant_db_vars = new DatabaseTablesNames();
            arango = Arango.getConnectedInstance();
            String DB_NAME = constant_db_vars.ARANGODB_DB_NAME;
            String collectionName =  constant_db_vars.ARANGODB_SONGS_TABLE_NAME;
            arango.createDatabaseIfNotExists(DB_NAME);

            //create Test Song Documents
            song = new BaseDocument();
            song.addAttribute("ID",songID);
            song.addAttribute("AudioPath", AudioPath);
            arango.createCollectionIfNotExists(DB_NAME, collectionName, false);
            BaseDocument inserted_doc = arango.createDocument(DB_NAME, collectionName, song);
            documentKey = inserted_doc.getKey();
            System.out.println(documentKey);


        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    public static JSONObject RequestSimulator(){
        JSONObject body = new JSONObject();
        body.put("songID", songID);
        JSONObject request = makeRequest(body, "POST", new JSONObject());
        System.out.println("Request " + request.toString());
        return new JSONObject(new GetSongCommand().execute(request));
    }
    public static JSONObject makeRequest(JSONObject body, String methodType, JSONObject uriParams) {
        JSONObject request = new JSONObject();
        request.put("body", body);
        request.put("methodType", methodType);
        request.put("uriParams", uriParams);
        JSONObject authenticationParams = new JSONObject();
        authenticationParams.put("isAuthenticated", false);
        request.put("authenticationParams", authenticationParams);
        return request;
    }
    
    @Test
    public void canGetSong() {

        JSONObject response = RequestSimulator();
        System.out.println("Response " + response.toString());
        JSONObject data = response.getJSONObject("data");
        assertEquals(AudioPath, data.getString("AudioPath"));
    }

    @AfterClass
    public static void deleteFromArango() {
        DatabaseTablesNames constant_db_vars = new DatabaseTablesNames();
        String DB_NAME = constant_db_vars.ARANGODB_DB_NAME;
        String collectionName =  constant_db_vars.ARANGODB_SONGS_TABLE_NAME;
        arango.deleteDocument(DB_NAME, collectionName,documentKey);

    }




    
}
