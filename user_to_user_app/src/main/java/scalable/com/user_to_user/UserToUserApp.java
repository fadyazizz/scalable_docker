package scalable.com.user_to_user;

import scalable.com.shared.App;
import scalable.com.shared.classes.Arango;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class UserToUserApp extends App{
    public static Arango arangoPool;

    public static void main(String[] args) throws TimeoutException, IOException, ClassNotFoundException { 

        UserToUserApp app = new UserToUserApp();
        arangoPool = new Arango();
        app.dbInit();
        app.start();
    }

    @Override
    protected void dbInit() throws IOException {
        Arango arango = Arango.getInstance();
        arango.createPool(15);
        arango.createDatabaseIfNotExists("user_to_user");
        arango.createCollectionIfNotExists("user_to_user","blocked_ids",false);
        arango.createCollectionIfNotExists("user_to_user","followed_ids",false);
        arango.createCollectionIfNotExists("user_to_user","reports",false);
    }

    @Override
    protected String getAppName() {
        return "UserToUser";
    }
}
