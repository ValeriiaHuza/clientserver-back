package Server;

import DBConnection.Product;
import com.sun.net.httpserver.*;
//import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import DBConnection.ProductDB;
import DBConnection.ProductGroup;

import java.io.*;
import java.net.InetSocketAddress;
//import java.security.acl.Group;


public class MyHttpServer {
    public static ProductDB db = new ProductDB();
    private static HttpServer server;

    public static void main(String[] args) throws IOException {

        db.initDB("test");

//        db.insertUserToDB(new User("login", DigestUtils.md5Hex("password")));
//        db.insertUserToDB(new User("login1",DigestUtils.md5Hex("password1")));
//        db.insertUserToDB(new User("login2",DigestUtils.md5Hex("password2")));
//
//          db.insertGroupToDB(new ProductGroup("одяг","description"));
//          db.insertGroupToDB(new ProductGroup("крупи","description"));
//          db.insertGroupToDB(new ProductGroup("напої","description"));
//
//          db.insertProductToDB(new Product("кофтина",1,"description","maker",200,20));
//          db.insertProductToDB(new Product("взуття",1,"description","maker",2000,1));
//          db.insertProductToDB(new Product("рис",2,"description","maker",30,999));
//          db.insertProductToDB(new Product("гречка",2,"description","maker",56,88));
//          db.insertProductToDB(new Product("кока-кола",3,"description","maker",10,290));
//
//        db.insertProductToDB(new Product("кока-кола1",3,"description","maker",10,290));
//        db.insertProductToDB(new Product("кока-кола2",3,"description","maker",10,290));
//        db.insertProductToDB(new Product("кока-кола3",3,"description","maker",10,290));
//        db.insertProductToDB(new Product("кока-кола4",3,"description","maker",10,290));
//        db.insertProductToDB(new Product("кока-кола5",3,"description","maker",10,290));
//        db.insertProductToDB(new Product("кока-кола6",3,"description","maker",10,290));
//        db.insertProductToDB(new Product("кока-кола7",3,"description","maker",10,290));
//        db.insertProductToDB(new Product("кока-кола8",3,"description","maker",10,290));
//        db.insertProductToDB(new Product("кока-кола9",3,"description","maker",10,290));


        serverStart();
    }

    public static void serverStart() throws IOException {
        server  = HttpServer.create(new InetSocketAddress(5000), 0);

      //  server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());

//        HttpContext context = server.createContext("/", MyHttpServer::myHandler);

        HttpContext context2 = server.createContext("/api/groups", new HandlerAllGroups(db));
        HttpContext context3 = server.createContext("/api/group", new HandlerOneGroup(db));
        HttpContext context4 = server.createContext("/api/goods", new HandlerAllGoods(db));
        HttpContext context5 = server.createContext("/api/good", new HandlerOneGood(db));

      // context2.setAuthenticator(new Auth());
       context3.setAuthenticator(new Auth(db));
       //context4.setAuthenticator(new Auth(db));
       context5.setAuthenticator(new Auth(db));


     //   server.setExecutor(null);
        server.start();
//        context.setAuthenticator(new Authenticator() {
//            @Override
//            public Result authenticate(HttpExchange httpExchange) {
//                String res = httpExchange.getRequestHeaders()
//                        .getFirst("Authorization");
//
//                if (res!=null){
//                    Tokens t = new Tokens();
//                    String userLogin = t.parseJWT(res);
//
//                    User user = db.getByLogin(userLogin);
//
//                    if(user!=null){
//                        return new Success(new HttpPrincipal(userLogin,"something"));
//                    }
//                }
//                return new Failure(403);
//            }
//        });
    }


//    private static void myHandler(HttpExchange httpExchange) throws IOException {
//
//        if(httpExchange.getRequestURI().getPath().equals("/login")){
//            HandlerLogin.handle(httpExchange);
//        }
//        else {
//            //apiGood(httpExchange);
//        }
//    }

    public static JSONObject getJsonFromQuery(String query){
        String res = "{";
        String[] ar1 = query.split("&");
        for(int i=0; i<ar1.length; i++){
            String[] ar2 = ar1[i].split("=");
            res += "'"+ar2[0]+"':'"+ar2[1]+"'";
            if(i!=ar1.length-1) res += ",";
        }
        res += "}";

        System.out.println(res);
        return new JSONObject(res);
    }

}
