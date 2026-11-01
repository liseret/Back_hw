package app;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Main {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        GameService service = new GameService();
        server.createContext("/", new ApiHandler(service));
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();
        System.out.println("Pokemon Kazik: http://localhost:" + PORT);
    }
}
