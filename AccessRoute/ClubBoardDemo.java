package com.lehighhub;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClubBoardDemo {
    private static final List<Event> events = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        // Clearly labeled examples: these are not live Lehigh University events.
        events.add(new Event("Board Game Night", LocalDate.now().plusDays(2).toString(),
                "Common Room", "Games", "Bring a game or join a table.", "games friends"));
        events.add(new Event("Casual Campus Walk", LocalDate.now().plusDays(3).toString(),
                "UC Front Steps", "Outdoors", "A relaxed walk and conversation.", "outdoors walking friends"));
        events.add(new Event("Study Together", LocalDate.now().plusDays(1).toString(),
                "Library Lobby", "Study", "Bring your work and study alongside others.", "study academics"));

        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 8080), 0);
        server.createContext("/", ClubBoardDemo::handleRequest);
        server.start();
        System.out.println("Club Board Demo running at http://localhost:8080");
        System.out.println("Press Ctrl+C to stop the server.");
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            send(exchange, 405, page("<h1>Method not supported</h1>"));
            return;
        }

        Map<String, String> query = parseQuery(exchange.getRequestURI().getRawQuery());
        String action = query.getOrDefault("action", "");
        if ("post".equals(action)) {
            String title = clean(query.get("title"));
            String date = clean(query.get("date"));
            String location = clean(query.get("location"));
            String category = clean(query.get("category"));
            String description = clean(query.get("description"));
            String tags = clean(query.get("tags"));

            if (!title.isBlank() && !date.isBlank() && !location.isBlank()
                    && !category.isBlank() && !description.isBlank()) {
                try {
                    LocalDate.parse(date); // Check that the date is valid.
                    events.add(0, new Event(title, date, location, category, description, tags));
                } catch (Exception ignored) {
                    // Invalid dates are not added; the page will show normally.
                }
            }
        }

        String categoryFilter = query.getOrDefault("categoryFilter", "All");
        String interests = query.getOrDefault("interests", "").toLowerCase();
        StringBuilder cards = new StringBuilder();
        int shown = 0;

        for (Event event : events) {
            if (!"All".equals(categoryFilter) && !event.category.equalsIgnoreCase(categoryFilter)) {
                continue;
            }
            boolean matches = !interests.isBlank()
                    && (containsAny(event.tags, interests) || containsAny(event.title + " " + event.description, interests));
            shown++;
            cards.append("<article class='event-card'>")
                .append("<div class='card-top'><span class='tag'>").append(escape(event.category))
                .append("</span><span class='sample'>DEMO EVENT</span></div>")
                .append("<h3>").append(escape(event.title)).append("</h3>")
                .append("<p>").append(escape(event.description)).append("</p>")
                .append("<div class='event-meta'><span>📅 ").append(escape(event.date))
                .append("</span><span>📍 ").append(escape(event.location)).append("</span></div>")
                .append("<div class='tags-line'>Interests: ").append(escape(event.tags.isBlank() ? "not specified" : event.tags))
                .append("</div>");
            if (matches) cards.append("<div class='match'>Matches your interests</div>");
            cards.append("</article>");
        }

        if (shown == 0) cards.append("<div class='empty'>No events match that filter. Try another category.</div>");

        String html = """
            <header class="hero">
              <div class="eyebrow">LU LINK · COMMUNITY</div>
              <h1>Find your people.</h1>
              <p>Low-pressure hangouts, campus clubs, and things to do together.</p>
              <div class="demo-note">⚠ Demonstration only — all events below are sample data, not live Lehigh University listings.</div>
            </header>
            <main>
              <section class="panel">
                <div class="section-heading"><div><div class="eyebrow dark">DISCOVER</div><h2>What's happening?</h2></div></div>
                <form method="get" action="/">
                  <label for="interests">Your interests</label>
                  <input id="interests" name="interests" value="%s" placeholder="e.g. games, outdoors, music">
                  <div class="field-row">
                    <div class="field-grow"><label for="categoryFilter">Content filter</label>
                      <select id="categoryFilter" name="categoryFilter">
                        %s
                      </select>
                    </div>
                    <button type="submit">Find hangouts</button>
                  </div>
                  <p class="hint">Tip: enter words like “games” or “study” to spot matching events.</p>
                </form>
              </section>
              <section class="events-section">
                <div class="section-heading"><div><div class="eyebrow dark">THE BULLETIN</div><h2>Upcoming hangouts</h2></div><span class="count">%d listed</span></div>
                <div class="event-grid">%s</div>
              </section>
              <section class="panel create-panel">
                <div class="eyebrow dark">BRING PEOPLE TOGETHER</div><h2>Post a hangout</h2>
                <p class="subtext">Share a simple plan and let people with similar interests find it.</p>
                <form method="get" action="/">
                  <input type="hidden" name="action" value="post">
                  <div class="form-grid">
                    <div><label for="title">Event title</label><input id="title" name="title" required maxlength="80" placeholder="e.g. Pick-up basketball"></div>
                    <div><label for="date">Date</label><input id="date" name="date" type="date" required></div>
                    <div><label for="location">Location</label><input id="location" name="location" required maxlength="100" placeholder="e.g. Campus courts"></div>
                    <div><label for="category">Category</label>
                      <select id="category" name="category"><option>Social</option><option>Games</option><option>Outdoors</option><option>Study</option><option>Sports</option><option>Arts</option><option>Food</option><option>Other</option></select>
                    </div>
                    <div class="full"><label for="description">What's the plan?</label><textarea id="description" name="description" required maxlength="400" rows="3" placeholder="Tell people what to expect..."></textarea></div>
                    <div class="full"><label for="tags">Interest tags</label><input id="tags" name="tags" placeholder="e.g. friends, music, outdoors"></div>
                  </div>
                  <button type="submit">Publish hangout</button>
                </form>
              </section>
              <footer>LU Link · A student community prototype</footer>
            </main>
            """.formatted(escape(interests), categoryOptions(categoryFilter), shown, cards);

        send(exchange, 200, page(html));
    }

    private static String categoryOptions(String selected) {
        String[] categories = {"All", "Social", "Games", "Outdoors", "Study", "Sports", "Arts", "Food", "Other"};
        StringBuilder result = new StringBuilder();
        for (String category : categories) {
            result.append("<option value=\"").append(escape(category)).append("\"")
                  .append(category.equalsIgnoreCase(selected) ? " selected" : "")
                  .append(">").append(escape(category)).append("</option>");
        }
        return result.toString();
    }

    private static boolean containsAny(String text, String interests) {
        String lowerText = text.toLowerCase();
        for (String interest : interests.split("[, ]+")) {
            if (!interest.isBlank() && lowerText.contains(interest)) return true;
        }
        return false;
    }

    private static Map<String, String> parseQuery(String rawQuery) {
        Map<String, String> values = new LinkedHashMap<>();
        if (rawQuery == null || rawQuery.isBlank()) return values;
        for (String pair : rawQuery.split("&")) {
            String[] parts = pair.split("=", 2);
            String key = decode(parts[0]);
            String value = parts.length > 1 ? decode(parts[1]) : "";
            values.put(key, value);
        }
        return values;
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private static String clean(String value) {
        return value == null ? "" : value.trim();
    }

    // Escape user-entered text before placing it in HTML.
    private static String escape(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }

    private static String page(String body) {
        return """
            <!doctype html><html lang="en"><head><meta charset="utf-8">
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <title>LU Link · Club Board</title>
            <style>
              *{box-sizing:border-box}body{margin:0;background:#f4f6fa;color:#19233a;font-family:-apple-system,BlinkMacSystemFont,"Segoe UI",sans-serif}
              .hero{padding:46px max(22px,calc((100% - 1050px)/2)) 36px;background:linear-gradient(130deg,#142d55,#254f85);color:white}
              .eyebrow{font-size:11px;letter-spacing:2px;font-weight:800;color:#c6d9f4}.eyebrow.dark{color:#59749c}
              h1{font-size:clamp(34px,6vw,54px);letter-spacing:-1.8px;margin:12px 0 8px}.hero p{font-size:17px;color:#e1eaf7;margin:0 0 20px}
              .demo-note{display:inline-block;padding:10px 13px;background:#ffffff18;border:1px solid #ffffff45;border-radius:9px;font-size:12px;color:#fff}
              main{max-width:1050px;margin:28px auto;padding:0 22px}.panel{background:white;border:1px solid #e1e7f0;border-radius:16px;padding:24px;margin-bottom:30px;box-shadow:0 8px 26px #172b4d08}
              .section-heading{display:flex;justify-content:space-between;align-items:center;margin-bottom:18px}h2{font-size:24px;letter-spacing:-.6px;margin:5px 0 0}h3{font-size:19px;margin:12px 0 8px;letter-spacing:-.3px}
              label{display:block;font-size:12px;font-weight:750;color:#3c4c66;margin:0 0 7px}input,select,textarea{width:100%;font:inherit;font-size:14px;border:1px solid #d8e0ec;border-radius:9px;padding:11px 12px;background:#fff;color:#19233a;outline:none}
              input:focus,select:focus,textarea:focus{border-color:#4779b8;box-shadow:0 0 0 3px #4779b81c}.field-row{display:flex;gap:12px;align-items:end;margin-top:14px}.field-grow{flex:1}
              button{border:0;border-radius:9px;background:#1f477b;color:#fff;font-weight:750;padding:12px 17px;cursor:pointer;font-size:14px}button:hover{background:#17365f}.hint,.subtext{font-size:12px;color:#718097;margin:12px 0 0}.count{font-size:12px;color:#66758d;background:#eaf0f8;padding:7px 10px;border-radius:20px}
              .event-grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(250px,1fr));gap:14px}.event-card{background:#fff;border:1px solid #e0e7f0;border-radius:14px;padding:18px;box-shadow:0 5px 18px #172b4d06}.card-top{display:flex;justify-content:space-between;gap:8px;align-items:center}.tag{font-size:11px;font-weight:800;color:#254d7f;background:#eaf2fc;padding:6px 9px;border-radius:20px}.sample{font-size:9px;font-weight:800;letter-spacing:1px;color:#8b6a2d}.event-card p{font-size:13px;line-height:1.55;color:#627087;min-height:40px}.event-meta{display:grid;gap:7px;font-size:12px;color:#40526e;margin:16px 0}.tags-line{font-size:11px;color:#718097;border-top:1px solid #edf0f5;padding-top:12px}.match{margin-top:12px;background:#e9f7ef;color:#21623c;font-size:11px;font-weight:800;padding:8px;border-radius:7px}.empty{padding:22px;background:#fff;border-radius:12px;color:#65738a}
              .create-panel{margin-top:34px}.create-panel h2{margin-top:7px}.form-grid{display:grid;grid-template-columns:1fr 1fr;gap:16px;margin:20px 0}.full{grid-column:1/-1}.create-panel button{margin-top:2px}footer{text-align:center;padding:20px;color:#8290a5;font-size:12px}
              @media(max-width:560px){.hero{padding-top:34px}.panel{padding:18px}.field-row{align-items:stretch;flex-direction:column}.form-grid{grid-template-columns:1fr}.full{grid-column:auto}.section-heading{align-items:flex-start;gap:12px}}
            </style></head><body>""" + body + "</body></html>";
    }

    private static void send(HttpExchange exchange, int status, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }

    private static class Event {
        final String title, date, location, category, description, tags;
        Event(String title, String date, String location, String category, String description, String tags) {
            this.title = title; this.date = date; this.location = location;
            this.category = category; this.description = description; this.tags = tags;
        }
    }
}
