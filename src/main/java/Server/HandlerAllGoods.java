package Server;

import DBConnection.ProductGroup;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import DBConnection.Product;
import DBConnection.ProductDB;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.OutputStream;
import java.util.ArrayList;

public class HandlerAllGoods implements HttpHandler {

    private Connection connection;
    private String product = "product";
    private String group = "groupTable";
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

        if (method.equals("GET")) {
            getProducts(httpExchange, os);
        }
        else {
            httpExchange.sendResponseHeaders(405, 0);
        }
        httpExchange.close();
    }

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
        String res = "";
        for(int i=0; i<str.length(); i++) {
            if(str.charAt(i)=='?') break;
            res += str.charAt(i);
        }
        return Integer.parseInt(res);
    }

    public static int getGroupID(HttpExchange httpExchange){
        String str = httpExchange.getRequestURI().toString().substring(14);
        String res = "";
        for(int i=0; i<str.length(); i++) {
            if(str.charAt(i)=='?') break;
            res += str.charAt(i);
        }
        return Integer.parseInt(res);
    }
    /////////////////////////////////////////////////////////////////////////////////

}