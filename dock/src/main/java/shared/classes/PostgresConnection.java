/*
EduMsg is made available under the OSI-approved MIT license.
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
IN THE SOFTWARE.
*/

package shared.classes;


import org.apache.commons.dbcp2.*;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.net.URI;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PostgresConnection {
    private static final Logger LOGGER = Logger
            .getLogger(PostgresConnection.class.getName());
    private String DB_USERNAME;   //your db username
    private String DB_PASSWORD; //your db password
    private String DB_PORT;
    private String DB_HOST;
    private String DB_NAME="postgres";
    private String DB_INIT_CONNECTIONS = "10";
    private String DB_MAX_CONNECTIONS = "10";
    private String DB_URL;
    private PoolingDriver dbDriver;
    private static PoolingDataSource<PoolableConnection> dataSource;

    public void shutdownDriver() throws SQLException {
        dbDriver.closePool(DB_NAME);
    }

    public void printDriverStats() throws SQLException {
        ObjectPool<? extends Connection> connectionPool = dbDriver
                .getConnectionPool(DB_NAME);

        System.out.println("DB Active Connections: "
                + connectionPool.getNumActive());
        System.out.println("DB Idle Connections: "
                + connectionPool.getNumIdle());
    }

    public static PoolingDataSource<PoolableConnection> getDataSource() {
        return dataSource;
    }

    public static void disconnect(ResultSet rs, PreparedStatement statement,
                                  Connection conn, Statement query) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
            }
        }

        if(query != null){
            try {
                query.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void disconnect(ResultSet rs, PreparedStatement statement,
                                  Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
            }
        }


    }

    public void initSource() {
        try {
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException ex) {
                LOGGER.log(Level.SEVERE,
                        "Error loading Postgres driver: " + ex.getMessage(), ex);
            }

            try {
                URI dbUri = new URI(System.getenv("postgres:root@127.0.0.1:5432/postgres"));
                DB_USERNAME = dbUri.getUserInfo().split(":")[0];
                DB_PASSWORD = dbUri.getUserInfo().split(":")[1];
                DB_NAME = dbUri.getPath().replace("/", "");
                DB_URL = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

            } catch (Exception e1){
                try {
                    //readConfFile();
                    setDBHost();
                    setDBPassword();
                    setDBName();
                    setDBPort();
                    setDBUser();
                    if (!formatURL()) {

                     //   throw  new Exception("Wrong Format in Postgres.conf");
                    }

                } catch ( Exception e2 ) {
                    e2.printStackTrace();
                }
                System.out.println("Used Config File For DB");
            }


            Properties props = new Properties();
            //  System.out.println(DB_USERNAME);
            props.setProperty("user", DB_USERNAME);
            props.setProperty("password", DB_PASSWORD);
            props.setProperty("initialSize", DB_INIT_CONNECTIONS);
            props.setProperty("maxActive", DB_MAX_CONNECTIONS);

            ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(
                    DB_URL, props);
            PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(
                    connectionFactory, null);
            poolableConnectionFactory.setPoolStatements(true);

            GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
            poolConfig.setMaxIdle(Integer.parseInt(DB_INIT_CONNECTIONS));
            poolConfig.setMaxTotal(Integer.parseInt(DB_MAX_CONNECTIONS));
            ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(
                    poolableConnectionFactory, poolConfig);
            poolableConnectionFactory.setPool(connectionPool);

            Class.forName("org.apache.commons.dbcp2.PoolingDriver");
            dbDriver = (PoolingDriver) DriverManager
                    .getDriver("jdbc:apache:commons:dbcp:");
            dbDriver.registerPool(DB_NAME, connectionPool);

            dataSource = new PoolingDataSource<>(connectionPool);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Got error initializing data source: "
                    + ex.getMessage(), ex);
        }
    }

    public  void setDBUser() {
        String username=System.getenv("postgres_username")==null?"postgres":System.getenv("postgres_username");
        DB_USERNAME = username;
    }

    public void setDBPassword() {
        String password=System.getenv("postgres_password")==null?"root":System.getenv("postgres_password");
        DB_PASSWORD = password;
    }

    public void setDBPort() {
        String port=System.getenv("postgres_port")==null?"5432":System.getenv("postgres_port");
        DB_PORT = port;
    }

    public void setDBHost() {
        String host=System.getenv("postgres_host")==null?"localhost":System.getenv("postgres_host");
        DB_HOST = host;
    }

    public void setDBURL(String url) {
        DB_URL = url;
    }

    public void setDBName() {
        String dbName=System.getenv("postgres_dbName")==null?"postgres":System.getenv("postgres_dbName");
        DB_NAME = dbName;
    }

    public void setDbInitConnections (String initConnections){ DB_INIT_CONNECTIONS = initConnections;}

    public void setDbMaxConnections (String maxConnections ){DB_MAX_CONNECTIONS =maxConnections;  }

    public String getDbInitConnections() { return DB_INIT_CONNECTIONS;}

    public String getDbMaxConnections(){return DB_MAX_CONNECTIONS;}

    private boolean formatURL() {
        setDBURL("jdbc:postgresql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME);
        System.out.println("database url..."+DB_URL);
        Pattern pattern = Pattern.compile("^\\w+:\\w+:\\/{2}\\d+.\\d+.\\d+.\\d+:\\d+\\/\\w+(?:\\W|\\w)*$");
        Matcher matcher = pattern.matcher(DB_URL);
        return matcher.matches();
    }

//    public void readConfFile() throws Exception {
//        String file = System.getProperty("user.dir") + "/Postgres.conf";
//        System.out.println(file);
//        java.util.List<String> lines = new ArrayList<String>();
//        //extract string between square brackets and compile regex for faster performance
//        Pattern pattern = Pattern.compile("\\[(.+)\\]");
//        Matcher matcher;
//        Exception e;
//        Stream<String> stream = Files.lines(Paths.get(file));
//        lines = stream.filter(line -> !line.startsWith("#")).collect(Collectors.toList());
//
//        //set variables based on matches
//        for (int i = 0; i < lines.size(); i++) {
//            System.out.println(lines.get(i));
//            if (lines.get(i).contains("user")) {
//                matcher = pattern.matcher(lines.get(i));
//                if(matcher.find()){
//                    setDBUser(matcher.group(1));}
//                else
//                    throw  e = new Exception("empty user in Postgres.conf");
//            }
//            if (lines.get(i).contains("database")) {
//                matcher = pattern.matcher(lines.get(i));
//                if(matcher.find())
//                    setDBName(matcher.group(1));
//                else
//                    throw  e = new Exception("empty database name in Postgres.conf");
//            }
//            if (lines.get(i).contains("pass")) {
//                matcher = pattern.matcher(lines.get(i));
//                matcher.find();
//                setDBPassword(matcher.group(1));
//            }
//            if (lines.get(i).contains("host")) {
//                matcher = pattern.matcher(lines.get(i));
//                if (matcher.find())
//                    setDBHost(matcher.group(1));
//                else
//                    setDBHost("localhost");
//            }
//            if (lines.get(i).contains("port")) {
//                matcher = pattern.matcher(lines.get(i));
//                if (matcher.find())
//                    setDBPort(matcher.group(1));
//                else
//                    setDBPort("5432");
//            }
//        }
//        if (!formatURL()) {
//            e = new Exception("Wrong Format in Postgres.conf");
//            throw e;
//        }
//    }

    public static void main(String[] args) {
        try {
            PostgresConnection db = new PostgresConnection();
            db.initSource();
            Connection dbConn = PostgresConnection.getDataSource().getConnection();
            System.out.println(dbConn);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }


}

