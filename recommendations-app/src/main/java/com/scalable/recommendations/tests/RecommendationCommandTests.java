package com.scalable.recommendations.tests;

import com.arangodb.ArangoCursor;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.BaseEdgeDocument;
import com.arangodb.velocypack.VPackSlice;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalable.recommendations.RecommendationsApp;
import com.scalable.recommendations.commands.GetRecommendedArtists;
import com.scalable.recommendations.commands.GetRecommendedMusicTracks;
import com.scalable.recommendations.constants.DatabaseConstants;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import scalable.com.shared.App;
import scalable.com.shared.classes.Arango;
import scalable.com.shared.testsHelper.TestHelper;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;

public class RecommendationCommandTests {
    static String[] users;
    static String[] songs;
    static String[] listens;
    static String[] follows;
    static int[] isArtist;
    static Arango arango;
    static GetRecommendedMusicTracks getRecommendedMusicTracks;
    static GetRecommendedArtists getRecommendedArtists;
    static HashMap<String, ArrayList<String>> toBeDeleted;
    @BeforeClass
    public static void createSetup() throws TimeoutException, IOException, ClassNotFoundException {
        RecommendationsApp app = new RecommendationsApp();
        app.start();
        TestHelper.appBeingTested=app;
        String DB_NAME = DatabaseConstants.DATABASE_NAME;
        String USER_COLLECTION =  DatabaseConstants.USER_DOCUMENT_COLLECTION;
        String SONG_COLLECTION =  DatabaseConstants.MUSIC_DOCUMENT_COLLECTION;
        String LISTENS_COLLECTION =  DatabaseConstants.MUSIC_EDGE_COLLECTION;
        String FOLLOWS_COLLECTION =  DatabaseConstants.USER_EDGE_COLLECTION;
        arango = Arango.getConnectedInstance();
        arango.createDatabaseIfNotExists(DB_NAME);
        arango.createCollectionIfNotExists(DB_NAME,USER_COLLECTION,false);
        arango.createCollectionIfNotExists(DB_NAME,DatabaseConstants.MUSIC_DOCUMENT_COLLECTION,false);
        arango.createCollectionIfNotExists(DB_NAME,DatabaseConstants.USER_EDGE_COLLECTION,true);
        arango.createCollectionIfNotExists(DB_NAME,DatabaseConstants.MUSIC_EDGE_COLLECTION,true);
        getRecommendedMusicTracks = new  GetRecommendedMusicTracks();
        getRecommendedArtists = new  GetRecommendedArtists();
        toBeDeleted = new HashMap<>();
        toBeDeleted.put(USER_COLLECTION, new ArrayList<>());
        toBeDeleted.put(SONG_COLLECTION, new ArrayList<>());
        toBeDeleted.put(LISTENS_COLLECTION, new ArrayList<>());
        toBeDeleted.put(FOLLOWS_COLLECTION, new ArrayList<>());
        InitializeUserCollection();
        InitializeSongCollection();
        InitializeListensEdgeCollection();
        InitializeFollowsEdgeCollection();

    }

    public static void InitializeUserCollection(){
        // Dummy Data
        users = new String[5];
        isArtist = new int[]{0,1,0,1,0};

        String DB_NAME = DatabaseConstants.DATABASE_NAME;
        String COLLECTION =  DatabaseConstants.USER_DOCUMENT_COLLECTION;
        //create Test user Documents

        for (int i = 0; i < users.length; i++) {
            String id = Integer.toString(i+1);
            BaseDocument doc = new BaseDocument();
            String arango_default_key = (String) getRecommendedMusicTracks.get_arango_default_key(id, arango,COLLECTION);
            if (arango_default_key != null) {
                arango.deleteDocument(DB_NAME, COLLECTION, arango_default_key);
//                doc.setKey(arango_default_key);
            }
            doc.addAttribute("id", id);
            doc.addAttribute("isArtist",isArtist[i]);
            BaseDocument inserted_doc = arango.createDocument(DB_NAME, COLLECTION, doc);
            System.out.println(" USERS DOCUMENT :::::::: " + inserted_doc + " KEY " + inserted_doc.getKey());
            toBeDeleted.get(COLLECTION).add(inserted_doc.getKey());
            users[i] = inserted_doc.getKey();
        }
    }

    public static void InitializeSongCollection(){
        // Dummy Data
        songs = new String[5];

        String DB_NAME = DatabaseConstants.DATABASE_NAME;
        String COLLECTION =  DatabaseConstants.MUSIC_DOCUMENT_COLLECTION;
        //create Test user Documents
        for (int i = 0; i < songs.length; i++) {
            String id = Integer.toString(i+1);
            BaseDocument doc = new BaseDocument();
            String arango_default_key = (String) getRecommendedMusicTracks.get_arango_default_key(id, arango,COLLECTION);
            if (arango_default_key != null)   {
                arango.deleteDocument(DB_NAME, COLLECTION, arango_default_key);
                //doc.setKey(arango_default_id);
            }

            doc.addAttribute("id", id);
            BaseDocument inserted_doc = arango.createDocument(DB_NAME, COLLECTION, doc);
            System.out.println(" SONGS DOCUMENT :::::::: " + inserted_doc + " KEY " + inserted_doc.getKey());
            toBeDeleted.get(COLLECTION).add(inserted_doc.getKey());
            songs[i] = inserted_doc.getKey();
        }
    }

    public static void InitializeListensEdgeCollection(){
        // Dummy Data
        listens = new String[9];
        String[] ToEdges   = {"2","1","1","3","2","5","1","2","4"};
        String[] FromEdges = {"1","1","2","2","3","3","4","4","4"};

        String DB_NAME = DatabaseConstants.DATABASE_NAME;
        String COLLECTION =  DatabaseConstants.MUSIC_EDGE_COLLECTION;
        String TO_COLLECTION =  DatabaseConstants.MUSIC_DOCUMENT_COLLECTION;
        String FROM_COLLECTION =  DatabaseConstants.USER_DOCUMENT_COLLECTION;

        for (int i = 0; i < ToEdges.length; i++) {
            
            String to_arango_default_key = (String) getRecommendedMusicTracks.get_arango_default_key(ToEdges[i], arango,TO_COLLECTION);
            String from_arango_default_key = (String) getRecommendedMusicTracks.get_arango_default_key(FromEdges[i], arango,FROM_COLLECTION);
            String to_arango_default_id =  TO_COLLECTION + "/" + to_arango_default_key;
            String from_arango_default_id =  FROM_COLLECTION + "/" + from_arango_default_key;



            BaseDocument inserted_doc = arango.createEdgeDocument(DB_NAME, COLLECTION,to_arango_default_id,from_arango_default_id);
            System.out.println(" LISTENS DOCUMENT :::::::: " + inserted_doc + " KEY " + inserted_doc.getKey());
            toBeDeleted.get(COLLECTION).add(inserted_doc.getKey());
            listens[i] = inserted_doc.getKey();
        }

    }




    public static void InitializeFollowsEdgeCollection(){
        // Dummy Data
        follows = new String[6];
        String[] ToEdges   = {"3","2","1","3","4","5"};
        String[] FromEdges = {"1","1","2","2","2","2"};

        String DB_NAME = DatabaseConstants.DATABASE_NAME;
        String COLLECTION =  DatabaseConstants.USER_EDGE_COLLECTION;
        String TO_COLLECTION =  DatabaseConstants.USER_DOCUMENT_COLLECTION;
        String FROM_COLLECTION =  DatabaseConstants.USER_DOCUMENT_COLLECTION;

        for (int i = 0; i < ToEdges.length; i++) {
            String to_arango_default_key = (String) getRecommendedMusicTracks.get_arango_default_key(ToEdges[i], arango,TO_COLLECTION);
            String from_arango_default_key = (String) getRecommendedMusicTracks.get_arango_default_key(FromEdges[i], arango,FROM_COLLECTION);




            String to_arango_default_id =  TO_COLLECTION + "/" + to_arango_default_key;
            String from_arango_default_id =  FROM_COLLECTION + "/" + from_arango_default_key;
            BaseDocument inserted_doc = arango.createEdgeDocument(DB_NAME, COLLECTION,to_arango_default_id,from_arango_default_id);
            System.out.println(" FOLLOWS DOCUMENT :::::::: " + inserted_doc + " KEY " + inserted_doc.getKey());
            toBeDeleted.get(COLLECTION).add(inserted_doc.getKey());
            follows[i] = inserted_doc.getKey();
        }

    }

    public static JSONObject RequestSimulatorMusicTrack(String user_id){
        JSONObject body = new JSONObject();
        JSONObject token= new JSONObject();
        token.put("id", user_id);
        JSONObject request = makeRequest(null, "GET", new JSONObject(),true,token);
        System.out.println("Request " + request.toString());

        return new JSONObject(TestHelper.execute(getRecommendedMusicTracks,request));
    }

    public static JSONObject RequestSimulatorArtist(String user_id){
        JSONObject body = new JSONObject();
        JSONObject token= new JSONObject();
        token.put("id", user_id);
        JSONObject request = makeRequest(null, "GET", new JSONObject(),true,token);
        System.out.println("Request " + request.toString());

        return new JSONObject(TestHelper.execute(getRecommendedArtists,request));
    }
    public static JSONObject makeRequest(JSONObject body, String methodType, JSONObject uriParams, boolean isAuthenticated, JSONObject tokenPayload) {
        JSONObject request = new JSONObject();
        request.put("body", body);
        request.put("methodType", methodType);
        request.put("uriParams", uriParams);
        JSONObject authenticationParams = new JSONObject();
        request.put("isAuthenticated", isAuthenticated);
        request.put("tokenPayload", tokenPayload);
        request.put("authenticationParams", authenticationParams);
        return request;
    }
    @Test
    public void T01_GetRecommendedArtists() throws JsonProcessingException {

        JSONObject response = RequestSimulatorArtist("1");
        System.out.println("Response " + response.toString());
        JSONObject data = response.getJSONObject("data");
        JSONArray actual = data.getJSONArray("recommended_artist_id");
        JSONArray expected = new JSONArray();
        expected.put("4");
        ObjectMapper mapper = new ObjectMapper();
        assertEquals(mapper.readTree(String.valueOf(actual)), mapper.readTree(String.valueOf(expected)));

    }
    @Test
    public void T01_GetRecommendedMusicTracksTest() throws JsonProcessingException {

        JSONObject response = RequestSimulatorMusicTrack("1");
        System.out.println("Response " + response.toString());
        JSONObject data = response.getJSONObject("data");
        JSONArray actual = data.getJSONArray("recommended_song_id");
        JSONArray expected = new JSONArray();
        expected.put("4");
        expected.put("3");
        expected.put("5");
        ObjectMapper mapper = new ObjectMapper();
        assertEquals(mapper.readTree(String.valueOf(actual)), mapper.readTree(String.valueOf(expected)));
        
    }
    @Test
    public void T02_GetRecommendedMusicTracksTest() throws JsonProcessingException {

        JSONObject response = RequestSimulatorMusicTrack("2");
        System.out.println("Response " + response.toString());
        JSONObject data = response.getJSONObject("data");
        JSONArray actual = data.getJSONArray("recommended_song_id");
        JSONArray expected = new JSONArray();
        expected.put("2");
        expected.put("4");
        ObjectMapper mapper = new ObjectMapper();
        assertEquals(mapper.readTree(String.valueOf(actual)), mapper.readTree(String.valueOf(expected)));

    }
    @Test
    public void T03_GetRecommendedMusicTracksTest() throws JsonProcessingException {

        JSONObject response = RequestSimulatorMusicTrack("3");
        System.out.println("Response " + response.toString());
        JSONObject data = response.getJSONObject("data");
        JSONArray actual = data.getJSONArray("recommended_song_id");
        JSONArray expected = new JSONArray();
        expected.put("1");
        expected.put("4");
        ObjectMapper mapper = new ObjectMapper();
        assertEquals(mapper.readTree(String.valueOf(actual)), mapper.readTree(String.valueOf(expected)));

    }
    @Test
    public void T04_GetRecommendedMusicTracksTest() throws JsonProcessingException {

        JSONObject response = RequestSimulatorMusicTrack("4");
        System.out.println("Response " + response.toString());
        JSONObject data = response.getJSONObject("data");
        JSONArray actual = data.getJSONArray("recommended_song_id");
        JSONArray expected = new JSONArray();
        expected.put("5");
        expected.put("3");
        ObjectMapper mapper = new ObjectMapper();
        assertEquals(mapper.readTree(String.valueOf(actual)), mapper.readTree(String.valueOf(expected)));

    }
    @Test
    public void T05_GetRecommendedMusicTracksTest() throws JsonProcessingException {

        JSONObject response = RequestSimulatorMusicTrack("5");
        System.out.println("Response " + response.toString());
        JSONObject data = response.getJSONObject("data");
        JSONArray actual = data.getJSONArray("recommended_song_id");
        JSONArray expected = new JSONArray();
        ObjectMapper mapper = new ObjectMapper();
        assertEquals(mapper.readTree(String.valueOf(actual)), mapper.readTree(String.valueOf(expected)));

    }

    @AfterClass
    public static void tearDown() {
        try {
            toBeDeleted.forEach((key, value) -> {
                for (String _key : value) {
                    arango.deleteDocument(DatabaseConstants.DATABASE_NAME, key, _key);
                }
                arango.dropCollection(DatabaseConstants.DATABASE_NAME,key);
            });
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
