package com.lehighhub;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

/** Starts the small Lehigh Hub web server using the JDK's built-in HttpServer. */
public class LehighHubApplication {
    public static void main(String[] args) throws IOException {
        // Bind only to this computer so the demo is not exposed to a network.
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8080), 0);
        server.createContext("/", new PageHandler());
        server.start();
        System.out.println("Lehigh Hub is running at http://localhost:8080");
        System.out.println("Stop the server with Control+C.");
    }
}
