//package scalable.com.user_to_user.commands;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.fail;
//
//import org.json.JSONObject;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import scalable.com.shared.classes.Arango;
////import scalable.com.user_to_user.UserToUserApp;
//
//public class UserToUserTest {
//    private static Arango arango;
//    @BeforeClass
//    public static void setUp() {
//        try {
//            arango = Arango.getConnectedInstance();
////            UserToUserApp app= new UserToUserApp();
////            app.dbInit();
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }
//
//    public static String reportUser(int reported_Id, String comment) {
//        JSONObject body = new JSONObject();
//        body.put("reported_id", reported_Id);
//        body.put("comment", comment);
//
//        JSONObject uriParams = new JSONObject();
//
//        JSONObject token= new JSONObject();
//        token.put("id", "1");
//
//        JSONObject request = new JSONObject();
//        request.put("body", body);
//        request.put("methodType", "POST");
//        request.put("uriParams", uriParams);
//        request.put("isAuthenticated",true);
//        request.put("tokenPayload", token);
//
//        //ReportUser reportUser= new ReportUser();
//        //TODO: FIX ME
////        return reportUser.execute(request);
//
//    }
//
//    @Test
//    public void reportUserTest() {
//        int beforeCount= arango.documentCount("user_to_user", "reports");
//        String response = reportUser(200, "test comment");
//        JSONObject responseJson = new JSONObject(response);
//        int afterCount =arango.documentCount("user_to_user", "reports");
//        assertEquals("Reported", responseJson.getString("msg"));
//        assertEquals(beforeCount+1, afterCount);
//    }
//
//}
