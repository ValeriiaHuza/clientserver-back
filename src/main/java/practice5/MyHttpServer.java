package practice5;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.Header;
//import org.apache.http.impl.bootstrap.HttpServer;
import org.json.JSONArray;
import org.json.JSONObject;
import practice4.Product;
import practice4.ProductDB;
import practice4.ProductGroup;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
//import java.security.acl.Group;
import java.util.ArrayList;

public class MyHttpServer {
    public static ProductDB db = new ProductDB();
    private static HttpServer server;

    public static void main(String[] args) throws IOException {

        db.initDB("test");

//        db.insertUserToDB(new User("login", DigestUtils.md5Hex("password")));
//        db.insertUserToDB(new User("login1",DigestUtils.md5Hex("password1")));
//        db.insertUserToDB(new User("login2",DigestUtils.md5Hex("password2")));

          db.insertGroupToDB(new ProductGroup("одяг","description"));
          db.insertGroupToDB(new ProductGroup("крупи","description"));
          db.insertGroupToDB(new ProductGroup("бакалія","description"));

//        db.insertProductToDB(new Product("meat",1,"description","maker",20,20));

        serverStart();
    }

    public static void serverStart() throws IOException {
        server  = HttpServer.create(new InetSocketAddress(5000), 0);
        server.start();

//        HttpContext context = server.createContext("/", MyHttpServer::myHandler);

        server.createContext("/api/groups", new HandlerGroups(db));
        server.createContext("/api/group", new HandlerGroup(db));
        server.createContext("/api/good", new HandlerGoods(db));

//
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

//    private static void apiGroup(HttpExchange httpExchange) throws IOException {
//        OutputStream os = httpExchange.getResponseBody();
//        String method = httpExchange.getRequestMethod();
//        System.out.println(method);
//        if(method.equals("OPTIONS")){
//            byte[] bytes = "Options".getBytes();
//            httpExchange.sendResponseHeaders(201, bytes.length);
//            os.write(bytes);
//        }
//        else if(method.equals("PUT")) {
//            JSONObject jsonObj = getJsonFromQuery(httpExchange.getRequestURI().getQuery());
//            ObjectMapper om = new ObjectMapper();
//
//            ProductGroup fromreqeust = om.readValue(jsonObj.toString(),ProductGroup.class);
//
//            System.out.println(fromreqeust.toString());
//            db.insertGroupToDB(fromreqeust);
//
//            byte[] bytes = "group created".getBytes();
//
//            httpExchange.sendResponseHeaders(201, bytes.length);
//            os.write(bytes);
//        }
//        else if (method.equals("GET")){
//            ArrayList<ProductGroup> ar = db.showAllGroups();
//            JSONArray res = new JSONArray();
//
//            for ( ProductGroup i : ar){
//                res.put(i.getName()+"#" + i.getDescription());
//            }
//
//            System.out.println(res);
//            byte[] bytes = res.toString().getBytes();
//            httpExchange.sendResponseHeaders(201, bytes.length);
//            os.write(bytes);
//        }
//        os.close();
//    }

//    private static void apiGood(HttpExchange httpExchange) throws IOException {
//        System.out.println(httpExchange.getRequestMethod());
//        if (httpExchange.getRequestMethod().equals("GET")) {
//           getProduct(httpExchange);
//        }
//        else if(httpExchange.getRequestMethod().equals("PUT")){
//            putProduct(httpExchange);
//        }
//        else if(httpExchange.getRequestMethod().equals("POST")){
//             postProduct(httpExchange);
//        }
//        else if (httpExchange.getRequestMethod().equals("DELETE")){
//            deleteProduct(httpExchange);
//        }
//        else {
//            httpExchange.sendResponseHeaders(405, 0);
//        }
//        httpExchange.close();
//    }

//    private static void deleteProduct(HttpExchange httpExchange) throws IOException {
//        String url = httpExchange.getRequestURI().toString();
//
//        String[] array = url.split("/");
//        Integer productId = Integer.parseInt(array[array.length - 1]);
//
//        System.out.println("id - " + productId);
//        Product getProduct = db.getProductByID(productId);
//
//        if(getProduct!=null){
//            db.deleteProductByName(getProduct.getName());
//
//            System.out.println(db.showAllProducts());
//            httpExchange.sendResponseHeaders(204, 0);
//        }
//        else {
//            sendResponse(httpExchange,404,"not found");
//        }
//    }

//    private static void postProduct(HttpExchange httpExchange) throws IOException {
//
//        String url = httpExchange.getRequestURI().toString();
//
//        String[] array = url.split("/");
//        Integer productId = Integer.parseInt(array[array.length - 1]);
//
//        System.out.println("id - " + productId);
//        Product getProduct = db.getProductByID(productId);
//
//
//        InputStreamReader isr =  new InputStreamReader(httpExchange.getRequestBody(),"utf-8");
//        BufferedReader br = new BufferedReader(isr);
//
//        StringBuilder sb = new StringBuilder();
//        String line;
//        while ((line = br.readLine()) != null) {
//            sb.append(line).append("\n");
//        }
//        br.close();
//
//        String productJSON = sb.toString();
//
//        ObjectMapper om = new ObjectMapper();
//
//        Product update = om.readValue(productJSON,Product.class);
//
//        if(update.getName()!=null){
//            db.updateProductName(getProduct.getName(),update.getName());
//            getProduct = db.getProductByName(update.getName());
//        }
//
//        if(om.readTree(productJSON).get("price")!=null) {
//            if (update.getPrice() < 0) {
//                sendResponse(httpExchange,409,"incorrect information");
//            } else {
//                db.updateProductPrice(getProduct.getName(), update.getPrice());
//                getProduct = db.getProductByName(getProduct.getName());
//
//            }
//        }
//
//        if(om.readTree(productJSON).get("amount")!=null) {
//            if (update.getAmount() < 0) {
//                sendResponse(httpExchange,409,"incorrect information");
//            } else {
//                db.updateProductAmount(getProduct.getName(), update.getAmount());
//                getProduct = db.getProductByName(getProduct.getName());
//            }
//        }
//
//        if(update.getDescription()!=null){
//            db.updateProductDescription(getProduct.getName(),update.getDescription());
//            getProduct = db.getProductByName(getProduct.getName());
//        }
//
//        if(update.getMaker()!=null){
//            db.updateProductMaker(getProduct.getName(),update.getMaker());
//            getProduct = db.getProductByName(getProduct.getName());
//        }
//
//        if(om.readTree(productJSON).get("groupId")!=null) {
//            if (update.getGroupId() < 0) {
//                sendResponse(httpExchange,409,"incorrect information");
//            } else {
//                db.updateProductGroup(getProduct.getName(), update.getGroupId());
//                getProduct = db.getProductByName(getProduct.getName());
//            }
//        }
//
//        System.out.println(getProduct);
//
//
//        httpExchange.sendResponseHeaders(204, 0);
//    }
//
//    private static void putProduct(HttpExchange httpExchange) throws IOException {
//        InputStreamReader isr =  new InputStreamReader(httpExchange.getRequestBody(),"utf-8");
//        BufferedReader br = new BufferedReader(isr);
//
//        StringBuilder sb = new StringBuilder();
//        String line;
//        while ((line = br.readLine()) != null) {
//            sb.append(line).append("\n");
//        }
//        br.close();
//
//        String productJSON = sb.toString();
//
//        ObjectMapper om = new ObjectMapper();
//
//        Product product = om.readValue(productJSON,Product.class);
//
//        if(product.getAmount()<0 || product.getPrice()<=0 || product.getGroupId()<=0 || product.getMaker()==null || product.getDescription()==null || product.getName()==null){
//            sendResponse(httpExchange,409,"incorrect information");
//        }
//        else {
//            Product productWithId = db.insertProductToDB(product);
//            System.out.println(productWithId);
//            sendResponse(httpExchange,201,"created product with id - " + productWithId.getId());
//        }
//    }
//
//    private static void getProduct(HttpExchange httpExchange) throws IOException {
//        String url = httpExchange.getRequestURI().toString();
//
//        String[] array = url.split("/");
//        Integer productId = Integer.parseInt(array[array.length - 1]);
//
//        System.out.println("id - " + productId);
//        Product getProduct = db.getProductByID(productId);
//
//        if (getProduct != null) {
//            sendResponse(httpExchange, 200,getProduct.toString());
//        } else {
//           sendResponse(httpExchange,404,"no such products");
//        }
//    }


//    private static void sendResponse(HttpExchange httpExchange, int number, String body) throws IOException {
//        byte[] response = body.getBytes();
//        httpExchange.sendResponseHeaders(number, response.length);
//        httpExchange.getResponseBody().write(response);
//        httpExchange.getResponseBody().flush();
//    }

//    private static void login(HttpExchange httpExchange) throws IOException {
//        if (httpExchange.getRequestMethod().equals("POST")) {
//            ObjectMapper om = new ObjectMapper();
//            User user = om.readValue(httpExchange.getRequestBody(), User.class);
//
//            User fromDB = db.getByLogin(user.getLogin());
//
//            if (fromDB != null) {
//
//                if (fromDB.getPassword().equals(DigestUtils.md5Hex(user.getPassword()))) {
//                    Tokens t = new Tokens();
//                    String token = t.createJWT(fromDB.getLogin());
//                    httpExchange.getResponseHeaders()
//                            .set("Authorization", token);
//
//                    sendResponse(httpExchange, 200,"token - " + token + "\n" + "ok" );
//                } else {
//                    sendResponse(httpExchange, 401,"some problems with password");
//                }
//
//            } else {
//                sendResponse(httpExchange, 401,"some problems with user");
//            }
//
//        } else {
//            httpExchange.sendResponseHeaders(405, 0);
//        }
//        httpExchange.close();
//    }

}
