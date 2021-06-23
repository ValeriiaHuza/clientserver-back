package Server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;
import DBConnection.ProductDB;
import DBConnection.ProductGroup;

import java.io.IOException;
import java.io.OutputStream;

public class HandlerOneGroup implements HttpHandler {

    private static HttpServer server;
    private static ProductDB db;

    HandlerOneGroup(ProductDB db){
        super();
        this.db = db;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        OutputStream os = httpExchange.getResponseBody();
        String method = httpExchange.getRequestMethod();
        System.out.println(method);
        if(method.equals("OPTIONS")){
            byte[] bytes = "Options".getBytes();
            httpExchange.sendResponseHeaders(201, bytes.length);
            os.write(bytes);
        }
        else if(method.equals("PUT")) {
            JSONObject jsonObj = MyHttpServer.getJsonFromQuery(httpExchange.getRequestURI().getQuery());
            ObjectMapper om = new ObjectMapper();

            ProductGroup fromreqeust = om.readValue(jsonObj.toString(),ProductGroup.class);

            System.out.println(fromreqeust.toString());
            MyHttpServer.db.insertGroupToDB(fromreqeust);

            byte[] bytes = "group created".getBytes();

            httpExchange.sendResponseHeaders(201, bytes.length);
            os.write(bytes);
        }
        else if(method.equals("GET")){
            String[] array = httpExchange.getRequestURI().getPath().split("/");
            ProductGroup obj = db.getGroupByID(Integer.parseInt(array[array.length-1]));
            String str = "{'groupname':'"+obj.getName()+"','description':'"+obj.getDescription()+"'}";
            System.out.println(str);
            byte[] bytes = str.getBytes();
            httpExchange.sendResponseHeaders(201, bytes.length);
            os.write(bytes);
        }
        else if(method.equals("DELETE")){
            String url = httpExchange.getRequestURI().toString();

        String[] array = url.split("/");
        Integer groupId = Integer.parseInt(array[array.length - 1]);


        ProductGroup getGroup = db.getGroupByID(groupId);

        if(getGroup!=null){
            db.deleteGroupByName(getGroup.getName());

            System.out.println(db.showAllProducts());
            httpExchange.sendResponseHeaders(204, 0);
        }
        else {
            byte[] bytes = "not found".getBytes();
            httpExchange.sendResponseHeaders(404, bytes.length);
            os.write(bytes);
        }
        }
        os.close();
    }
}
