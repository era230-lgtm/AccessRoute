package com.lehighhub;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/** Routes browser requests to the home page and the four demo pages. */
public class PageHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.startsWith("/css/")) {
            serveCss(exchange, path.substring(5));
            return;
        }

        String method = exchange.getRequestMethod();
        String message = "";
        Map<String, String> fields = new HashMap<>();
        if ("POST".equalsIgnoreCase(method)) {
            fields = parseForm(exchange);
            message = submittedMessage(path, fields);
        }

        String page;
        if ("/clubs".equals(path)) page = ClubsPage.render(message);
        else if ("/dining".equals(path)) page = DiningPage.render(message);
        else if ("/buses".equals(path)) page = BusesPage.render(message);
        else if ("/resources".equals(path)) page = ResourcesPage.render(message);
        else if ("/".equals(path)) page = homePage();
        else page = notFoundPage();

        send(exchange, 200, page, "text/html; charset=utf-8");
    }

    private String submittedMessage(String path, Map<String, String> fields) {
        if ("/clubs".equals(path)) {
            String club = safeField(fields, "club");
            if (club.isEmpty()) return "Add a club name to preview your bulletin post.";
            String update = safeField(fields, "update");
            if (update.isEmpty()) update = "No update text added.";
            return "Preview for “" + club + "”: " + update + " — this demo does not publish posts.";
        }
        if ("/dining".equals(path)) {
            try {
                double balance = Double.parseDouble(safeField(fields, "balance"));
                int days = Integer.parseInt(safeField(fields, "days"));
                if (balance < 0 || days < 1 || days > 365) throw new NumberFormatException();
                return String.format("Sample budget: $%.2f per day for %d days.", balance / days, days);
            } catch (NumberFormatException exception) {
                return "Enter a non-negative balance and a number of days from 1 to 365.";
            }
        }
        if ("/buses".equals(path)) return "Showing the sample “" + safeField(fields, "route") + "” route below.";
        if ("/resources".equals(path)) return "Showing sample resources for “" + safeField(fields, "category") + "” below.";
        return "";
    }

    private String safeField(Map<String, String> fields, String key) {
        return fields.getOrDefault(key, "").trim();
    }

    private Map<String, String> parseForm(HttpExchange exchange) throws IOException {
        byte[] body = exchange.getRequestBody().readAllBytes();
        String encoded = new String(body, StandardCharsets.UTF_8);
        Map<String, String> fields = new HashMap<>();
        for (String pair : encoded.split("&")) {
            String[] parts = pair.split("=", 2);
            String key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
            String value = parts.length > 1 ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8) : "";
            fields.put(key, value);
        }
        return fields;
    }

    private void serveCss(HttpExchange exchange, String fileName) throws IOException {
        // Only known stylesheets are served; paths from the request are never used as file paths.
        String css;
        switch (fileName) {
            case "shared.css": css = "shared.css"; break;
            case "clubs.css": css = "clubs.css"; break;
            case "dining.css": css = "dining.css"; break;
            case "buses.css": css = "buses.css"; break;
            case "resources.css": css = "resources.css"; break;
            default: send(exchange, 404, "Not found", "text/plain; charset=utf-8"); return;
        }
        java.nio.file.Path path = java.nio.file.Paths.get("public", "css", css);
        if (!java.nio.file.Files.exists(path)) {
            send(exchange, 404, "Stylesheet not found. Start the server from the LehighHub folder.", "text/plain; charset=utf-8");
            return;
        }
        byte[] contents = java.nio.file.Files.readAllBytes(path);
        exchange.getResponseHeaders().set("Content-Type", "text/css; charset=utf-8");
        exchange.sendResponseHeaders(200, contents.length);
        exchange.getResponseBody().write(contents);
        exchange.close();
    }

    private String homePage() {
        String content = "<section class=\"home-hero\"><div class=\"hero-copy\"><p class=\"eyebrow\">YOUR CAMPUS, CONNECTED</p>"
                + "<h1>Make the most<br>of <em>Lehigh.</em></h1><p class=\"hero-intro\">A friendly starting point for finding your people, planning your day, and making campus life a little easier.</p>"
                + "<a class=\"button button-light\" href=\"/clubs\">Explore the hub <span aria-hidden=\"true\">↗</span></a></div>"
                + "<div class=\"hero-art\" aria-label=\"Mountain inspired illustration\"><div class=\"sun\"></div><div class=\"mountain mountain-back\"></div><div class=\"mountain mountain-front\"></div><span class=\"hero-caption\">BETHLEHEM, PA · THE LEHIGH VALLEY</span></div></section>"
                + "<div class=\"sample-banner\"><span class=\"sample-dot\"></span><strong>DEMO MODE</strong><span>Everything below uses clearly labeled sample information.</span></div>"
                + "<section class=\"section-heading\"><div><p class=\"eyebrow\">A FEW GOOD SHORTCUTS</p><h2>Campus life, in one place.</h2></div><p>Built by students, for the everyday stuff.</p></section>"
                + "<section class=\"feature-grid\"><a class=\"feature-card feature-clubs\" href=\"/clubs\"><span class=\"feature-number\">01 / COMMUNITY</span><span class=\"feature-icon\">✳</span><h3>Find your people</h3><p>Browse a sample club board and draft a bulletin post.</p><span class=\"card-arrow\">↗</span></a>"
                + "<a class=\"feature-card feature-dining\" href=\"/dining\"><span class=\"feature-number\">02 / FOOD</span><span class=\"feature-icon\">◒</span><h3>Plan your dining dollars</h3><p>Turn a sample balance into a simple daily target.</p><span class=\"card-arrow\">↗</span></a>"
                + "<a class=\"feature-card feature-buses\" href=\"/buses\"><span class=\"feature-number\">03 / GETTING AROUND</span><span class=\"feature-icon\">↗</span><h3>Find a bus route</h3><p>Check an example campus route and its demo schedule.</p><span class=\"card-arrow\">↗</span></a>"
                + "<a class=\"feature-card feature-resources\" href=\"/resources\"><span class=\"feature-number\">04 / SUPPORT</span><span class=\"feature-icon\">＋</span><h3>Discover resources</h3><p>Explore a curated sample of student support options.</p><span class=\"card-arrow\">↗</span></a></section>"
                + "<section class=\"home-note\"><span class=\"note-mark\">H</span><p><strong>A small hub for big campus energy.</strong><br>Made as a student hackathon prototype. Always check official Lehigh channels for current details.</p></section>";
        return HtmlLayout.page("Home", "home", "", content);
    }

    private String notFoundPage() {
        return HtmlLayout.page("Not found", "home", "", "<section class=\"page-hero\"><p class=\"eyebrow\">404 · LOST ON THE MOUNTAIN</p><h1>That page isn't here.</h1><a class=\"button button-dark\" href=\"/\">Back to home</a></section>");
    }

    private void send(HttpExchange exchange, int status, String body, String contentType) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }
}
