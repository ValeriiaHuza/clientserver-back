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
//        else if(httpExchange.getRequestMethod().equals("PUT")){
//            putProduct(httpExchange);
//        }
//        else if(httpExchange.getRequestMethod().equals("POST")){
//            postProduct(httpExchange);
//        }
        else if (httpExchange.getRequestMethod().equals("DELETE")){
            deleteProduct(httpExchange,os);
        }
        else {
            httpExchange.sendResponseHeaders(405, 0);
        }
        os.close();
        httpExchange.close();
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