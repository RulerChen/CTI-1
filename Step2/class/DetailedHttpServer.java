import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

public class DetailedHttpServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", DetailedHttpServer::handleRequest);
        server.start();
        System.out.println("Server started on port 8080");
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {
        String remoteAddress = exchange.getRemoteAddress().toString();
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        System.out.println("-------- New Request --------");
        System.out.println("Time: " + timestamp);
        System.out.println("Remote Address: " + remoteAddress);
        System.out.println("Method: " + method);
        System.out.println("Path: " + path);
        System.out.println("-----------------------------");

        try {
            byte[] response = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("Exploit.class"));
            exchange.getResponseHeaders().set("Content-Type", "application/octet-stream");
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
            System.out.println("File sent successfully.");
        } catch (IOException e) {
            String errorMessage = "File not found or error reading file.";
            exchange.sendResponseHeaders(404, errorMessage.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(errorMessage.getBytes());
            }
            System.out.println("Error: " + errorMessage);
        }
        System.out.println("Request handled.");
    }
}