package Server;

import DBConnection.ProductDB;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import org.json.JSONObject;

public class Auth extends com.sun.net.httpserver.Authenticator {
    private ProductDB dbc;

    Auth() {
        super();
    }

    Auth(ProductDB dbc) {
        super();
        this.dbc = dbc;
    }

    @Override
    public Result authenticate(HttpExchange httpExchange) {
        if (httpExchange.getRequestURI().toString().startsWith("/api/good")) {
            return checkGood(httpExchange);
        } else if(httpExchange.getRequestURI().toString().startsWith("/api/group")){
            return checkGroup(httpExchange);
        } else if(httpExchange.getRequestURI().toString().startsWith("/api/goods")){
            try {
                int prodid = HandlerAllGoods.getGroupID(httpExchange);
                if (!dbc.hasGroup(prodid)) {
                    return new Failure(404);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new Success(new HttpPrincipal("c0nst", "realm"));
    }

    private Result checkGroup(HttpExchange httpExchange) {
        String method = httpExchange.getRequestMethod();
        if (method.equals("PUT")) {
            if (!HandlerOneGroup.allIsCorrect(httpExchange))
                return new Failure(409);
        } else if (method.equals("GET")) {
            try {
                int prodid = HandlerOneGroup.getGroupID(httpExchange);
                if (!dbc.hasGroup(prodid)) {
                    return new Failure(404);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (method.equals("POST")) {
            JSONObject jsonObj = MyHttpServer.getJsonFromQuery(httpExchange.getRequestURI().getQuery());
            if (!jsonObj.has("id"))
                return new Failure(404);
            if (!dbc.hasGroup(jsonObj.getInt("id"))) {
                return new Failure(404);
            }
        } else if (method.equals("DELETE")) {
            try {
                int prodid = HandlerOneGroup.getGroupID(httpExchange);
                if (!dbc.hasGroup(prodid)) {
                    return new Failure(404);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new Success(new HttpPrincipal("c0nst", "realm"));
    }

    private Result checkGood(HttpExchange httpExchange) {
        String method = httpExchange.getRequestMethod();
        if (method.equals("PUT")) {
            if (!HandlerOneGood.allIsCorrect(httpExchange))
                return new Failure(409);
        } else if (method.equals("GET")) {
            try {
                int prodid = HandlerOneGood.getProdID(httpExchange);
                if (!dbc.isProduct(prodid)) {
                    return new Failure(404);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (method.equals("POST")) {
            JSONObject jsonObj = MyHttpServer.getJsonFromQuery(httpExchange.getRequestURI().getQuery());
            if (!jsonObj.has("id"))
                return new Failure(404);
            if (!dbc.isProduct(jsonObj.getInt("id"))) {
                return new Failure(404);
            }
        } else if (method.equals("DELETE")) {
            try {
                int prodid = HandlerOneGood.getProdID(httpExchange);
                if (!dbc.isProduct(prodid)) {
                    return new Failure(404);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new Success(new HttpPrincipal("c0nst", "realm"));
    }
}
