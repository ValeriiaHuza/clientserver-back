package Server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.codec.digest.DigestUtils;
import DBConnection.ProductDB;

import java.io.IOException;

public class HandlerLogin implements HttpHandler {

    private static HttpServer server;
    private static ProductDB db;

    HandlerLogin(ProductDB db){
        super();
        this.db = db;
    }


    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if (httpExchange.getRequestMethod().equals("POST")) {
            ObjectMapper om = new ObjectMapper();
            User user = om.readValue(httpExchange.getRequestBody(), User.class);

            User fromDB = db.getByLogin(user.getLogin());

            if (fromDB != null) {

                if (fromDB.getPassword().equals(DigestUtils.md5Hex(user.getPassword()))) {
                    Tokens t = new Tokens();
                    String token = t.createJWT(fromDB.getLogin());
                    httpExchange.getResponseHeaders()
                            .set("Authorization", token);

                    sendResponse(httpExchange, 200,"token - " + token + "\n" + "ok" );
                } else {
                    sendResponse(httpExchange, 401,"some problems with password");
                }

            } else {
                sendResponse(httpExchange, 401,"some problems with user");
            }

        } else {
            httpExchange.sendResponseHeaders(405, 0);
        }
        httpExchange.close();
    }

    private static void sendResponse(HttpExchange httpExchange, int number, String body) throws IOException {
        byte[] response = body.getBytes();
        httpExchange.sendResponseHeaders(number, response.length);
        httpExchange.getResponseBody().write(response);
        httpExchange.getResponseBody().flush();
    }
}
