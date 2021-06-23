package Server;

import DBConnection.ProductGroup;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import DBConnection.Product;
import DBConnection.ProductDB;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class HandlerOneGood implements HttpHandler {

    private static HttpServer server;
    private static ProductDB db;

    HandlerOneGood(ProductDB db){
        super();
        this.db = db;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        OutputStream os = httpExchange.getResponseBody();
        String method = httpExchange.getRequestMethod();

        if(method.equals("OPTIONS")) {
            byte[] bytes = "Options".getBytes();
            httpExchange.sendResponseHeaders(201, bytes.length);
            os.write(bytes);
        }
        else if (method.equals("GET")) {
            getProduct(httpExchange,os);
        }
        else if(httpExchange.getRequestMethod().equals("PUT")){
            putProduct(httpExchange, os);
        }
        else if(httpExchange.getRequestMethod().equals("POST")){
            postProduct(httpExchange, os);
        }
        else if (httpExchange.getRequestMethod().equals("DELETE")){
            deleteProduct(httpExchange,os);
        }
        else {
            httpExchange.sendResponseHeaders(405, 0);
        }
        os.close();
        httpExchange.close();
    }

    private static void postProduct(HttpExchange httpExchange, OutputStream os) throws IOException {

        JSONObject jsonObj = MyHttpServer.getJsonFromQuery(httpExchange.getRequestURI().getQuery());

        String productName = db.getProductByID(jsonObj.getInt("id")).getName();

        if(jsonObj.has("name")){
            db.updateProductName(productName, jsonObj.getString("name"));
        }

        if(jsonObj.has("price")) {
            if (jsonObj.getDouble("price") < 0) {
                sendResponse(httpExchange,409,"incorrect information");
            } else {
                db.updateProductPrice(productName, jsonObj.getDouble("price"));
            }
        }

        if(jsonObj.has("amount")) {
            if (jsonObj.getDouble("amount") < 0) {
                sendResponse(httpExchange,409,"incorrect information");
            } else {
                db.updateProductAmount(productName, jsonObj.getDouble("amount"));
              }
        }

        if(jsonObj.has("description")){
            db.updateProductDescription(productName,jsonObj.getString("description"));
        }

        if(jsonObj.has("maker")){
            db.updateProductMaker(productName,jsonObj.getString("maker"));
             }

        if(jsonObj.has("groupId")) {
            int groupId = db.getGroupId( jsonObj.getString("groupId"));
            db.updateProductGroup(productName, groupId);

        }


        byte[] bytes = "product update".getBytes();

        httpExchange.sendResponseHeaders(204, bytes.length);
        os.write(bytes);
    }


    private static void putProduct(HttpExchange httpExchange, OutputStream os) throws IOException {
        JSONObject jsonObj = MyHttpServer.getJsonFromQuery(httpExchange.getRequestURI().getQuery());
        ObjectMapper om = new ObjectMapper();


        int groupId = db.getGroupId( jsonObj.getString("groupId"));

        JSONObject newJSON = new JSONObject();
        newJSON.put("name",jsonObj.getString("name"));
        newJSON.put("groupId",groupId);
        newJSON.put("description",jsonObj.getString("description"));
        newJSON.put("maker",jsonObj.getString("maker"));
        newJSON.put("price",jsonObj.getString("price"));
        newJSON.put("amount",jsonObj.getString("amount"));

        System.out.println(newJSON);

        Product fromreqeust = om.readValue(newJSON.toString(),Product.class);


        System.out.println(fromreqeust.toString());
        MyHttpServer.db.insertProductToDB(fromreqeust);

        byte[] bytes = "product created".getBytes();

        httpExchange.sendResponseHeaders(201, bytes.length);
        os.write(bytes);
    }

    private static void deleteProduct(HttpExchange httpExchange, OutputStream os) throws IOException {
        String url = httpExchange.getRequestURI().toString();

        String[] array = url.split("/");
        Integer productId = Integer.parseInt(array[array.length - 1]);

        System.out.println("id - " + productId);
        Product getProduct = db.getProductByID(productId);

        if(getProduct!=null){
            db.deleteProductByName(getProduct.getName());

            System.out.println(db.showAllProducts());
            httpExchange.sendResponseHeaders(204, 0);
        }

        else {
            byte[] bytes = "not found".getBytes();
            httpExchange.sendResponseHeaders(404, bytes.length);
            os.write(bytes);
        }
    }

    public static int getProdID(HttpExchange httpExchange)
    {
        String str = httpExchange.getRequestURI().toString().substring(10);
        String res = "";
        for(int i=0; i<str.length(); i++) {
            if(str.charAt(i)=='?') break;
            res += str.charAt(i);
        }
        return Integer.parseInt(res);
    }

    public static boolean allIsCorrect(HttpExchange httpExchange){
        try {
            String query = httpExchange.getRequestURI().getQuery();
            JSONObject jsonObj = MyHttpServer.getJsonFromQuery(query);
            String prodname = jsonObj.getString("productname");
            String description = jsonObj.getString("description");
            int price = jsonObj.getInt("price");
            int amount = jsonObj.getInt("amount");
            int groupid = jsonObj.getInt("groupid");
            if(prodname.length() < 1 || description.length() < 1
                    || price<0 || amount<0 || groupid<0)
                return false;
        } catch (Exception e){
            return false;
        }
        return true;
    }

    private static void getProduct(HttpExchange httpExchange, OutputStream os) throws IOException {
        String url = httpExchange.getRequestURI().toString();

        String[] array = url.split("/");
        Integer productId = Integer.parseInt(array[array.length - 1]);

        Product product = db.getProductByID(productId);

        String res = "{";
        res+="'prodname':'"+product.getName()+"',";
        res+="'description':'"+product.getDescription()+"',";
        res+="'price':'"+product.getPrice()+"',";
        res+="'amount':'"+product.getAmount()+"',";
        res+="'groupName':'"+db.getGroupByID(product.getGroupId()).getName()+"',";
        res+="'producer':'"+product.getMaker()+"'}";

        byte[] bytes = res.getBytes();
        httpExchange.sendResponseHeaders(201, bytes.length);
        os.write(bytes);
    }

    public static boolean allIsCorrect(JSONObject jsonObj){
        if(jsonObj.has("price")){
            int price= jsonObj.getInt("price");
            if(price<0) return false;
        }
        if(jsonObj.has("amount")){
            int amount= jsonObj.getInt("amount");
            if(amount<0) return false;
        }
        if(jsonObj.has("groupId")){
            int groupid= jsonObj.getInt("groupId");
            if(!db.hasGroup(groupid))
                return false;
        }
        return true;
    }

    private static void sendResponse(HttpExchange httpExchange, int number, String body) throws IOException {
        byte[] response = body.getBytes();
        httpExchange.sendResponseHeaders(number, response.length);
        httpExchange.getResponseBody().write(response);
        httpExchange.getResponseBody().flush();
    }
}