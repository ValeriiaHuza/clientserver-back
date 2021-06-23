package Server;

import DBConnection.ProductGroup;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import DBConnection.Product;
import DBConnection.ProductDB;

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

    private static void sendResponse(HttpExchange httpExchange, int number, String body) throws IOException {
        byte[] response = body.getBytes();
        httpExchange.sendResponseHeaders(number, response.length);
        httpExchange.getResponseBody().write(response);
        httpExchange.getResponseBody().flush();
    }
}