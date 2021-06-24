package Server;

import DBConnection.ProductGroup;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import DBConnection.Product;
import DBConnection.ProductDB;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class HandlerAllGoods implements HttpHandler {

    private Connection connection;
    private String product = "product";
    private String group = "groupTable";

    double priceAllProducts;

    ////////////////////////////////////////////
    private static HttpServer server;
    private static ProductDB db;

    HandlerAllGoods(ProductDB db){
        super();
        this.db = db;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        OutputStream os = httpExchange.getResponseBody();
        String method = httpExchange.getRequestMethod();

        System.out.println(method);

        String uri = httpExchange.getRequestURI().getPath();
        String[] array = uri.split("/");

        if(method.equals("OPTIONS")) {
            byte[] bytes = "Options".getBytes();
            httpExchange.sendResponseHeaders(201, bytes.length);
            os.write(bytes);
        }
        else if (method.equals("GET") && array.length==3) {
            getProducts(httpExchange, os);
        }
        else if(method.equals("GET") && array.length==4){
            getProductsInGroup(httpExchange, os,Integer.valueOf(array[3]));
        }
        else if(method.equals("PUT")){
            search(httpExchange, os);
        }
        else {
            httpExchange.sendResponseHeaders(405, 0);
        }
        httpExchange.close();
    }

    private void search(HttpExchange httpExchange, OutputStream os) throws IOException {
        String query = "{";
        String[] ar1 = httpExchange.getRequestURI().getQuery().split("&");
        System.out.println("uri array " + Arrays.toString(ar1));
        for(int i=0; i<ar1.length; i++){
            String[] ar2 = ar1[i].split("=");
            query += "'"+ar2[0]+"':'"+ar2[1]+"'";
            if(i!=ar1.length-1) query += ",";
        }
        query += "}";

        System.out.println("query - " + query);

        JSONObject newObject = new JSONObject(query);

        ArrayList<Product> array = new ArrayList<Product>() ;

        if(newObject.has("name")){
            ArrayList<Product> temp = db.getByName(newObject.getString("name")+"%");
            array.addAll(temp);
        }

        if (newObject.has("minPrice") && !newObject.has("maxPrice")){
            ArrayList<Product> temp = db.getByPrice(newObject.getDouble("minPrice"),100000000);
            array.addAll(temp);
        }
        else if (newObject.has("maxPrice") && !newObject.has("minPrice")){
            ArrayList<Product> temp = db.getByPrice(0,newObject.getDouble("maxPrice"));
            array.addAll(temp);
        }
        else if (newObject.has("maxPrice") && newObject.has("minPrice")){
            ArrayList<Product> temp = db.getByPrice(newObject.getDouble("minPrice"),newObject.getDouble("maxPrice"));
            array.addAll(temp);
        }

        //ArrayList<Product> ar = MyHttpServer.db.showAllProductsInGroup(db.getGroupByID(valueOf).getName());

        JSONArray res = new JSONArray();

        for ( Product i : array){
            //res.put(i.getName()+"#"+i.getId());
            res.put(i.getName()+"#"+i.getId());

        }

        System.out.println(res);
        byte[] bytes = res.toString().getBytes();
        httpExchange.sendResponseHeaders(201, bytes.length);
        os.write(bytes);
    }


    private void getProductsInGroup(HttpExchange httpExchange, OutputStream os, Integer valueOf) throws IOException {
        ArrayList<Product> ar = MyHttpServer.db.showAllProductsInGroup(db.getGroupByID(valueOf).getName());

        JSONArray res = new JSONArray();

        for ( Product i : ar){
            //res.put(i.getName()+"#"+i.getId());
            res.put(i.getName()+"#"+i.getId() + "#" + (i.getPrice()*i.getAmount()));

        }

        System.out.println(res);
        byte[] bytes = res.toString().getBytes();
        httpExchange.sendResponseHeaders(201, bytes.length);
        os.write(bytes);
    }

//    private void getPriceOfAllProductsInGroup(HttpExchange httpExchange, OutputStream os, Integer valueOf) throws IOException {
//        ArrayList<Product> ar = MyHttpServer.db.showAllProductsInGroup(db.getGroupByID(valueOf).getName());
//
//        JSONArray res = new JSONArray();
//
//        for ( Product i : ar){
//            res.put(i.getName()+"#"+i.getId() + "#" + (i.getPrice()*i.getAmount()));
//        }
//
//        System.out.println(res);
//        byte[] bytes = res.toString().getBytes();
//        httpExchange.sendResponseHeaders(201, bytes.length);
//        os.write(bytes);
//    }

    private static void getProducts(HttpExchange httpExchange, OutputStream os) throws IOException {
        ArrayList<Product> ar = MyHttpServer.db.showAllProducts();
        JSONArray res = new JSONArray();

        for ( Product i : ar){
            res.put(i.getName()+"#"+i.getId());
        }

        System.out.println(res);
        byte[] bytes = res.toString().getBytes();
        httpExchange.sendResponseHeaders(201, bytes.length);
        os.write(bytes);
    }

    public static int getAllProdID(HttpExchange httpExchange)
    {
        String str = httpExchange.getRequestURI().toString().substring(10);
        str.replaceFirst("/", "");
        System.out.println(">>>>>>>getAllProdID() in handlerAllGoods>>>>>>>>"+str);
        String res = "";
        for(int i=0; i<str.length(); i++) {
            if(str.charAt(i)=='?') break;
            res += str.charAt(i);
        }
        return Integer.parseInt(str);
    }

    public static int getGroupID(HttpExchange httpExchange){
        String str = httpExchange.getRequestURI().toString().substring(9);
        String res = "";
        for(int i=0; i<str.length(); i++) {
            if(str.charAt(i)=='?') break;
            res += str.charAt(i);
        }
        return Integer.parseInt(res);
    }
    /////////////////////////////////////////////////////////////////////////////////

}