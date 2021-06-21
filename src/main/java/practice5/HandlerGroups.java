package practice5;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONArray;
import org.json.JSONObject;
import practice4.ProductDB;
import practice4.ProductGroup;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class HandlerGroups implements HttpHandler {

    private static HttpServer server;
    private static ProductDB db;

    HandlerGroups(ProductDB db){
        super();
        this.db = db;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        OutputStream os = httpExchange.getResponseBody();
        String method = httpExchange.getRequestMethod();

        System.out.println(method);

        if (method.equals("GET")){
            ArrayList<ProductGroup> ar = MyHttpServer.db.showAllGroups();
            JSONArray res = new JSONArray();

            for ( ProductGroup i : ar){
                res.put(i.getName()+"#" + i.getDescription()+"#"+i.getId());
            }

            System.out.println(res);
            byte[] bytes = res.toString().getBytes();
            httpExchange.sendResponseHeaders(201, bytes.length);
            os.write(bytes);
        }
        os.close();
    }
}
