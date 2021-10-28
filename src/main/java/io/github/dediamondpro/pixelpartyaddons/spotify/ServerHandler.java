package io.github.dediamondpro.pixelpartyaddons.spotify;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class ServerHandler {
    private static boolean isInitialized = false;
    private static HttpServer httpServer;

    static {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(7853), 0);
            httpServer.createContext("/callback", ServerHandler::handleCallback);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void init() {
        if (!isInitialized) {
            httpServer.start();
            isInitialized = true;
            System.out.println("Started callback server.");
        }
    }

    private static void handleCallback(HttpExchange httpExchange) throws IOException {
        if (httpExchange.getRequestMethod().equals("GET")) {
            String code = httpExchange.getRequestURI().getQuery().split("code=")[1];
            String title = "Success!";
            String message = "You can now close this and go back to minecraft.";
            if (code.equals("")) {
                System.out.println("An error has occurred, code is empty.");
                title = "An error has occurred.";
                message = "Please try again later.";
            }
            String response = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<body>\n" +
                    "<h1>" + title + "</h1>\n" +
                    "<p>" + message + "</p>\n" +
                    "</body>\n" +
                    "</html>";
            httpExchange.sendResponseHeaders(200, response.length());

            OutputStream out = httpExchange.getResponseBody();
            out.write(response.getBytes());
            out.close();

            System.out.println("Received callback " + code);
            Authenticator.getTokens(code);
        }
    }
}
