package Server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONArray;
import DBConnection.ProductDB;
import DBConnection.ProductGroup;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class HandlerAllGroups implements HttpHandler {

    private static HttpServer server;
    private static ProductDB db;

    HandlerAllGroups(ProductDB db){
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
