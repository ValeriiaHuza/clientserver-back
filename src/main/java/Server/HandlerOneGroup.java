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
        if(method.equals("OPTIONS")){
            byte[] bytes = "Options".getBytes();
            httpExchange.sendResponseHeaders(201, bytes.length);
            os.write(bytes);
        }
        else if(method.equals("PUT")) {
            JSONObject jsonObj = MyHttpServer.getJsonFromQuery(httpExchange.getRequestURI().getQuery());
            ObjectMapper om = new ObjectMapper();

            ProductGroup fromreqeust = om.readValue(jsonObj.toString(),ProductGroup.class);
            MyHttpServer.db.insertGroupToDB(fromreqeust);

            byte[] bytes = "group created".getBytes();

            httpExchange.sendResponseHeaders(201, bytes.length);
            os.write(bytes);
        }
        else if(method.equals("GET")){
            String[] array = httpExchange.getRequestURI().getPath().split("/");
            ProductGroup obj = db.getGroupByID(Integer.parseInt(array[array.length-1]));
            String str = "{'groupname':'"+obj.getName()+"','description':'"+obj.getDescription()+"'}";
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
        else if(method.equals("POST")){
            JSONObject jsonObj = MyHttpServer.getJsonFromQuery(httpExchange.getRequestURI().getQuery());
            String groupName = db.getGroupByID(jsonObj.getInt("id")).getName();
            if(jsonObj.has("groupName")){
                db.updateGroupName(groupName,jsonObj.getString("groupName") );
            }

            if (jsonObj.has("description")){
                db.updateGroupDescription(groupName,jsonObj.getString("description"));
            }

            httpExchange.sendResponseHeaders(204,-1);
        }
        os.close();
    }

    public static int getGroupID(HttpExchange httpExchange){
        String str = httpExchange.getRequestURI().toString().substring(11);
        System.out.println(">>>>>>>>httpExchange.getRequestURI().toString()>>>>>>>."+httpExchange.getRequestURI().toString());
        //str.replaceFirst("/", "");
        System.out.println(">>>>>>>>>> getGroupID() int handlerOneGroup >>>>>>>>>>>>>" + str); ////////////check
        String res = "";
        for(int i=0; i<str.length(); i++) {
            if(str.charAt(i)=='?') break;
            res += str.charAt(i);
        }
        return Integer.parseInt(str);
    }

    public static boolean allIsCorrect(HttpExchange httpExchange){
        try {
            String query = httpExchange.getRequestURI().getQuery();
            JSONObject jsonObj = MyHttpServer.getJsonFromQuery(query);
            String groupname = jsonObj.getString("groupname");
            String description = jsonObj.getString("description");
        } catch (Exception e){
            return false;
        }
        return true;
    }
}
