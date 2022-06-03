package com.scalable.recommendations.tests;


import com.scalable.recommendations.RecommendationsApp;
import org.junit.BeforeClass;
import org.junit.Test;
import scalable.com.shared.classes.Arango;

import static org.junit.Assert.*;
import com.scalable.recommendations.constants.DatabaseConstants;
import scalable.com.shared.testsHelper.TestHelper;


public abstract class RecommendationsMetadataTests {
    public static Arango arangoPool;

    @BeforeClass
    public static void setUp(){
        try {
            RecommendationsApp app = new RecommendationsApp();
            app.start();
            TestHelper.appBeingTested=app;
            arangoPool = Arango.getConnectedInstance();
            arangoPool.createCollectionIfNotExists(DatabaseConstants.DATABASE_NAME, DatabaseConstants.USER_DOCUMENT_COLLECTION, false);
            arangoPool.createCollectionIfNotExists(DatabaseConstants.DATABASE_NAME, DatabaseConstants.MUSIC_DOCUMENT_COLLECTION, false);
            arangoPool.createCollectionIfNotExists(DatabaseConstants.DATABASE_NAME, DatabaseConstants.USER_EDGE_COLLECTION, true);
            arangoPool.createCollectionIfNotExists(DatabaseConstants.DATABASE_NAME, DatabaseConstants.MUSIC_EDGE_COLLECTION, true);
        }
        catch (Exception e){
            fail(e.getMessage());
        }

    }
}
