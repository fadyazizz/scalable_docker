package com.scalable.recommendations.tests;

import com.scalable.recommendations.commands.CreateMusicEdge;
import com.scalable.recommendations.commands.CreateMusicTrackNode;
import com.scalable.recommendations.commands.CreateUserEdge;
import com.scalable.recommendations.commands.CreateUserNode;
import com.scalable.recommendations.constants.DatabaseConstants;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Test;
import scalable.com.shared.testsHelper.TestHelper;

public class RecommendationsAppMetadataTests extends RecommendationsMetadataTests {
    public static String user_id_1="1";
    public static boolean is_artist_1=false;

    public static String user_id_2="2";
    public static boolean is_artist_2=true;

    public static String track_id = "1";
    public static String track_name = "Fadel Takka";

    public static JSONObject RequestSimulatorCreateMusicNode(String track_id,String track_name){
        JSONObject body = new JSONObject();
        JSONObject token= new JSONObject();
        token.put("id", "2");
        body.put("track_id", track_id);
        body.put("track_name",track_name);
        JSONObject request = makeRequest(body, "POST", new JSONObject(),true,token);
        System.out.println("Request " + request.toString());

        return new JSONObject(TestHelper.execute(new CreateMusicTrackNode(),request));
    }
    public static JSONObject RequestSimulatorCreateUserNode(String user_id,boolean is_artist){
        JSONObject body = new JSONObject();
        body.put("user_id", user_id);
        body.put("is_artist",is_artist);
        JSONObject request = makeRequest(body, "POST", new JSONObject(),false,null);
        System.out.println("Request " + request.toString());

        return new JSONObject(TestHelper.execute(new CreateUserNode(),request));
    }
    public static JSONObject RequestSimulatorCreateUserEdge(String from,String to){
        JSONObject body = new JSONObject();
        JSONObject token= new JSONObject();
        token.put("id", from);
        body.put("followed_user_id", to);
        JSONObject request = makeRequest(body, "POST", new JSONObject(),true,token);
        System.out.println("Request " + request.toString());

        return new JSONObject(TestHelper.execute(new CreateUserEdge(),request));
    }
    public static JSONObject RequestSimulatorCreateMusicEdge(String user_id,String track_id){
        JSONObject body = new JSONObject();
        JSONObject token= new JSONObject();
        token.put("id", "1");
        body.put("tokenPayload", token);
        body.put("track_id", track_id);
        JSONObject request = makeRequest(body, "POST", new JSONObject(),true,token);
        System.out.println("Request " + request.toString());

        return new JSONObject(TestHelper.execute(new CreateMusicEdge(),request));
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
    public void addUserNode_NonArtist(){
        JSONObject  response = RequestSimulatorCreateUserNode(user_id_1,is_artist_1);
        assert RecommendationsMetadataTests.arangoPool.documentExists(DatabaseConstants.DATABASE_NAME,DatabaseConstants.USER_DOCUMENT_COLLECTION,user_id_1);
        assert response.getInt("statusCode") ==200 ;
        assert response.getString("msg").equals("Node User Created Successfully");

    }
    @Test
    public void addUserNode_Artist(){
        JSONObject  response = RequestSimulatorCreateUserNode(user_id_2,is_artist_2);
        System.out.println(response);
        assert RecommendationsMetadataTests.arangoPool.documentExists(DatabaseConstants.DATABASE_NAME,DatabaseConstants.USER_DOCUMENT_COLLECTION,user_id_2);
        assert response.getInt("statusCode") ==200;
        assert response.getString("msg").equals("Node User Created Successfully");
    }
    @Test
    public void addMusicNode(){
        JSONObject  response = RequestSimulatorCreateMusicNode(track_id,track_name);
        System.out.println(response);
        assert RecommendationsMetadataTests.arangoPool.documentExists(DatabaseConstants.DATABASE_NAME,DatabaseConstants.MUSIC_DOCUMENT_COLLECTION,track_id);

        assert response.getInt("statusCode") ==200 ;
        assert response.getString("msg").equals("Node Track Created Successfully");
    }
    @Test
    public void addUserEdge(){
        JSONObject  response = RequestSimulatorCreateUserEdge(user_id_1,user_id_2);
        System.out.println(response);
        assert response.getInt("statusCode") ==200 ;
        assert response.getString("msg").equals("User Edge Created Successfully");
    }
    @Test
    public void addMusicEdge(){
        JSONObject  response = RequestSimulatorCreateMusicEdge(user_id_1,track_id);
        System.out.println(response);
        assert response.getInt("statusCode") ==200 ;
        assert response.getString("msg").equals("Music Edge Created Successfully");
    }
    @AfterClass
    public static void dropAllCollections() {
        System.out.println("In after test");
        RecommendationsMetadataTests.arangoPool.dropCollection(DatabaseConstants.DATABASE_NAME,DatabaseConstants.USER_DOCUMENT_COLLECTION);
        RecommendationsMetadataTests.arangoPool.dropCollection(DatabaseConstants.DATABASE_NAME,DatabaseConstants.MUSIC_DOCUMENT_COLLECTION);
        RecommendationsMetadataTests.arangoPool.dropCollection(DatabaseConstants.DATABASE_NAME,DatabaseConstants.USER_EDGE_COLLECTION);
        RecommendationsMetadataTests.arangoPool.dropCollection(DatabaseConstants.DATABASE_NAME,DatabaseConstants.MUSIC_EDGE_COLLECTION);

    }
}
