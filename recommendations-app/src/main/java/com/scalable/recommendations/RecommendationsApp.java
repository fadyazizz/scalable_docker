package com.scalable.recommendations;

import com.scalable.recommendations.constants.DatabaseConstants;
import scalable.com.shared.App;
import scalable.com.shared.classes.Arango;


import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

public  class RecommendationsApp extends App {
    public static Arango arangoPool;
    public static void main(String[] args)throws TimeoutException, IOException, ClassNotFoundException, SQLException {
        RecommendationsApp app=new RecommendationsApp();
        app.start();
        arangoPool =Arango.getInstance() ;
        arangoPool.createCollectionIfNotExists(DatabaseConstants.DATABASE_NAME,DatabaseConstants.USER_DOCUMENT_COLLECTION,false);
        arangoPool.createCollectionIfNotExists(DatabaseConstants.DATABASE_NAME,DatabaseConstants.MUSIC_DOCUMENT_COLLECTION,false);
        arangoPool.createCollectionIfNotExists(DatabaseConstants.DATABASE_NAME,DatabaseConstants.USER_EDGE_COLLECTION,true);
        arangoPool.createCollectionIfNotExists(DatabaseConstants.DATABASE_NAME,DatabaseConstants.MUSIC_EDGE_COLLECTION,true);


    }


    @Override
    protected String getAppName() {
        return "Recommendations";
    }
}
