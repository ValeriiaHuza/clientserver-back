package practice5;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import practice4.ProductDB;

import java.io.IOException;

public class HandlerGoods implements HttpHandler {

    private static HttpServer server;
    private static ProductDB db;

    HandlerGoods(ProductDB db){
        super();
        this.db = db;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

    }
}
