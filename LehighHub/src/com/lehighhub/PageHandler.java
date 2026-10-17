package com.lehighhub;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.net.URLDecoder;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/** Routes page requests and keeps demo profiles in server memory. */
public class PageHandler implements HttpHandler {
    private static final String SESSION_COOKIE = "LEHIGH_HUB_DEMO";
    private static final int MAX_FORM_BYTES = 32 * 1024;
    private static final int MAX_PHOTO_BYTES = 2 * 1024 * 1024;
    private static final Path PHOTO_FOLDER = Paths.get("data", "profile-pictures");
    private static final Set<String> ALLOWED_INTERESTS = Set.of(
            "arts", "outdoors", "fitness", "food", "music", "technology", "service", "gaming", "career");

    // This map is a tiny stand-in for user storage during this local demo only.
    private final Map<String, StudentProfile> profiles = new ConcurrentHashMap<>();
    private final List<ClubPosting> clubPostings = new CopyOnWriteArrayList<>();
    private static final DateTimeFormatter CLUB_TIME_LABEL = DateTimeFormatter.ofPattern("h:mm a");

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String sessionId = sessionId(exchange);
        StudentProfile profile = profiles.get(sessionId);

        if (path.startsWith("/css/")) {
            serveCss(exchange, path.substring(5));
            return;
        }
        if ("/profile-picture".equals(path) && "GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            serveProfilePicture(exchange, sessionId, profile);
            return;
        }

        String method = exchange.getRequestMethod();
        if ("POST".equalsIgnoreCase(method)) {
            if ("/clubs".equals(path)) {
                handleClubPosting(exchange, profile);
                return;
            }
            if ("/signup".equals(path)) {
                handleSignup(exchange, sessionId);
                return;
            }
            if ("/profile".equals(path)) {
                handleProfileUpdate(exchange, sessionId, profile);
                return;
            }
            if ("/profile-picture".equals(path)) {
                handlePhotoUpload(exchange, sessionId, profile);
                return;
            }
            if ("/profile-picture/remove".equals(path)) {
                removeProfilePicture(exchange, sessionId, profile);
                return;
            }
        }

        String message = "";
        Map<String, String> fields = new HashMap<>();
        if ("POST".equalsIgnoreCase(method)) {
            fields = parseForm(exchange);
            message = submittedMessage(path, fields);
        }

        String page;
        if ("/signup".equals(path)) {
            if (profile != null) {
                redirect(exchange, "/profile");
                return;
            }
            page = SignupPage.render("");
        } else if ("/profile".equals(path)) {
            if (profile == null) {
                redirect(exchange, "/signup");
                return;
            }
            if ("welcome=1".equals(exchange.getRequestURI().getRawQuery())) {
                message = "Welcome to Lehigh Hub, " + profile.getName() + "! Your demo profile is ready.";
            } else if ("saved=1".equals(exchange.getRequestURI().getRawQuery())) {
                message = "Your profile details have been updated.";
            } else if ("photo=uploaded".equals(exchange.getRequestURI().getRawQuery())) {
                message = "Your profile picture was added to this computer's local demo.";
            } else if ("photo=removed".equals(exchange.getRequestURI().getRawQuery())) {
                message = "Your profile picture was removed.";
            }
            page = ProfilePage.render(message, profile);
        } else if ("/clubs".equals(path)) {
            String query = exchange.getRequestURI().getRawQuery();
            boolean posted = "posted=1".equals(query);
            message = posted ? "Your meeting was posted." : "";
            page = ClubsPage.render(message, profile, clubPostings, false, "");
        }
        else if ("/dining".equals(path)) page = DiningPage.render(message, profile);
        else if ("/buses".equals(path)) page = BusesPage.render(message, profile);
        else if ("/resources".equals(path)) page = ResourcesPage.render(message, profile);
        else if ("/".equals(path)) page = homePage(profile);
        else {
            send(exchange, 404, notFoundPage(profile), "text/html; charset=utf-8");
            return;
        }
        send(exchange, 200, page, "text/html; charset=utf-8");
    }

    private void handleSignup(HttpExchange exchange, String oldSessionId) throws IOException {
        Map<String, String> fields = parseForm(exchange);
        String password = fields.remove("password"); // Deliberately never stored or compared.
        String name = safeField(fields, "name");
        String username = safeField(fields, "username");
        List<String> interests = selectedInterests(fields);

        String problem = validateBasics(name, username);
        if (problem.isEmpty() && (password == null || password.length() < 8)) {
            problem = "For this demo form, enter at least 8 characters in the password field.";
        }
        if (problem.isEmpty() && usernameTaken(username, oldSessionId)) {
            problem = "That username is already in use in this demo. Try another one.";
        }
        if (!problem.isEmpty()) {
            send(exchange, 400, SignupPage.render(problem), "text/html; charset=utf-8");
            return;
        }

        // Issue a fresh session ID when a profile is created. The password is discarded.
        String newSessionId = UUID.randomUUID().toString();
        StudentProfile profile = new StudentProfile(name, username, "", "", "", interests, "");
        profiles.put(newSessionId, profile);
        exchange.getResponseHeaders().add("Set-Cookie", sessionCookie(newSessionId));
        redirect(exchange, "/profile?welcome=1");
    }

    private void handleClubPosting(HttpExchange exchange, StudentProfile profile) throws IOException {
        Map<String, String> fields = parseForm(exchange);
        String title = safeField(fields, "title");
        String rawTime = safeField(fields, "time");
        String location = safeField(fields, "location");
        String access = safeField(fields, "access");
        String type = safeField(fields, "type");
        if (ClubPostModeration.containsBlockedLanguage(title, location)) {
            String problem = "Profanity and slurs are not allowed in club posts. Please revise the title or location.";
            send(exchange, 400, ClubsPage.render(problem, profile, clubPostings, true, problem), "text/html; charset=utf-8");
            return;
        }
        LocalTime time;
        try {
            time = LocalTime.parse(rawTime);
        } catch (RuntimeException exception) {
            send(exchange, 400, ClubsPage.render("Enter a valid meeting time.", profile, clubPostings, true, ""), "text/html; charset=utf-8");
            return;
        }
        if (title.isEmpty() || title.length() > 60 || location.isEmpty() || location.length() > 80
                || !("open".equals(access) || "closed".equals(access))
                || !("club".equals(type) || "hangout".equals(type))) {
            send(exchange, 400, ClubsPage.render("Enter a title, location, meeting access, and post type.", profile, clubPostings, true, ""), "text/html; charset=utf-8");
            return;
        }

        List<String> topics = selectedClubTopics(fields);
        synchronized (clubPostings) {
            if (overlapsExistingClub(time, location)) {
                send(exchange, 409, ClubsPage.render("Cannot overlap with another posting", profile, clubPostings, true, "Cannot overlap with another posting"), "text/html; charset=utf-8");
                return;
            }
            clubPostings.add(new ClubPosting(title, time, location, topics, "open".equals(access), type));
        }
        redirect(exchange, "/clubs?posted=1");
    }

    private List<String> selectedClubTopics(Map<String, String> fields) {
        Set<String> allowed = Set.of("arts", "outdoors", "fitness", "food", "music", "technology", "service", "gaming", "career");
        List<String> selected = new ArrayList<>();
        for (String topic : fields.getOrDefault("topic", "").split("\n")) {
            String value = topic.trim();
            if (allowed.contains(value) && !selected.contains(value)) selected.add(value);
        }
        return selected;
    }

    private boolean overlapsExistingClub(LocalTime time, String location) {
        String normalizedLocation = normalizeLocation(location);
        for (ClubPosting posting : clubPostings) {
            if (posting.getTime().equals(time) && normalizeLocation(posting.getLocation()).equals(normalizedLocation)) return true;
        }
        // The four sample listings are recurring meetings already on this demo board.
        return (time.equals(LocalTime.of(18, 30)) && normalizedLocation.equals(normalizeLocation("UC Room 202")))
                || (time.equals(LocalTime.of(10, 0)) && normalizedLocation.equals(normalizeLocation("Alumni Building steps")))
                || (time.equals(LocalTime.of(19, 0)) && normalizedLocation.equals(normalizeLocation("Packard Lab 112")))
                || (time.equals(LocalTime.of(16, 30)) && normalizedLocation.equals(normalizeLocation("Community Garden")));
    }

    private String normalizeLocation(String location) {
        return location.trim().replaceAll("\\s+", " ").toLowerCase();
    }

    private void handleProfileUpdate(HttpExchange exchange, String sessionId, StudentProfile current) throws IOException {
        if (current == null) {
            redirect(exchange, "/signup");
            return;
        }
        Map<String, String> fields = parseForm(exchange);
        String name = safeField(fields, "name");
        String username = safeField(fields, "username");
        String residenceHall = safeField(fields, "residenceHall");
        String graduationYear = safeField(fields, "graduationYear");
        String major = safeField(fields, "major");
        String problem = validateBasics(name, username);
        if (problem.isEmpty() && !graduationYear.isEmpty()) {
            try {
                int year = Integer.parseInt(graduationYear);
                if (year < 2020 || year > 2100) throw new NumberFormatException();
            } catch (NumberFormatException exception) {
                problem = "Enter a graduation year between 2020 and 2100.";
            }
        }
        if (problem.isEmpty() && usernameTaken(username, sessionId)) {
            problem = "That username is already in use in this demo. Try another one.";
        }
        if (!problem.isEmpty()) {
            send(exchange, 400, ProfilePage.render(problem, current), "text/html; charset=utf-8");
            return;
        }

        profiles.put(sessionId, new StudentProfile(name, username, residenceHall, graduationYear,
                major, selectedInterests(fields), current.getPictureFileName()));
        redirect(exchange, "/profile?saved=1");
    }

    private void handlePhotoUpload(HttpExchange exchange, String sessionId, StudentProfile profile) throws IOException {
        if (profile == null) {
            redirect(exchange, "/signup");
            return;
        }
        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        String boundary = multipartBoundary(contentType);
        if (boundary.isEmpty()) {
            send(exchange, 400, ProfilePage.render("Choose an image file to upload.", profile), "text/html; charset=utf-8");
            return;
        }
        byte[] body = exchange.getRequestBody().readNBytes(MAX_PHOTO_BYTES + 64 * 1024 + 1);
        if (body.length > MAX_PHOTO_BYTES + 64 * 1024) {
            send(exchange, 413, ProfilePage.render("That image is too large. Choose a file under 2 MB.", profile), "text/html; charset=utf-8");
            return;
        }

        PhotoPart photo = findPhotoPart(body, boundary);
        String extension = photo == null ? "" : validImageExtension(photo.bytes);
        if (photo == null || extension.isEmpty() || photo.bytes.length > MAX_PHOTO_BYTES) {
            send(exchange, 400, ProfilePage.render("Choose a valid PNG, JPG, GIF, or WebP image under 2 MB.", profile), "text/html; charset=utf-8");
            return;
        }

        Files.createDirectories(PHOTO_FOLDER);
        String fileName = sessionId + "." + extension;
        // Remove an older image format so the profile has just one current photo.
        removeStoredPicture(sessionId, profile.getPictureFileName());
        Files.write(PHOTO_FOLDER.resolve(fileName), photo.bytes);
        profiles.put(sessionId, profile.withPicture(fileName));
        redirect(exchange, "/profile?photo=uploaded");
    }

    private void removeProfilePicture(HttpExchange exchange, String sessionId, StudentProfile profile) throws IOException {
        if (profile == null) {
            redirect(exchange, "/signup");
            return;
        }
        removeStoredPicture(sessionId, profile.getPictureFileName());
        profiles.put(sessionId, profile.withPicture(""));
        redirect(exchange, "/profile?photo=removed");
    }

    private void serveProfilePicture(HttpExchange exchange, String sessionId, StudentProfile profile) throws IOException {
        if (profile == null || !profile.hasProfilePicture()) {
            send(exchange, 404, "No profile image", "text/plain; charset=utf-8");
            return;
        }
        Path image = PHOTO_FOLDER.resolve(profile.getPictureFileName()).normalize();
        if (!image.getParent().equals(PHOTO_FOLDER.normalize()) || !Files.isRegularFile(image)) {
            send(exchange, 404, "No profile image", "text/plain; charset=utf-8");
            return;
        }
        String extension = profile.getPictureFileName().substring(profile.getPictureFileName().lastIndexOf('.') + 1);
        String mime = "png".equals(extension) ? "image/png" : "jpg".equals(extension) ? "image/jpeg"
                : "gif".equals(extension) ? "image/gif" : "image/webp";
        exchange.getResponseHeaders().set("Cache-Control", "no-store");
        byte[] bytes = Files.readAllBytes(image);
        exchange.getResponseHeaders().set("Content-Type", mime);
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }

    private void removeStoredPicture(String sessionId, String fileName) throws IOException {
        if (fileName == null || fileName.isEmpty()) return;
        Path image = PHOTO_FOLDER.resolve(fileName).normalize();
        if (image.getParent().equals(PHOTO_FOLDER.normalize()) && fileName.startsWith(sessionId + ".")) {
            Files.deleteIfExists(image);
        }
    }

    private String validateBasics(String name, String username) {
        if (name.isEmpty() || name.length() > 60) return "Enter your name (up to 60 characters).";
        if (!username.matches("[A-Za-z0-9_]{3,20}")) {
            return "Use a username with 3–20 letters, numbers, or underscores.";
        }
        return "";
    }

    private boolean usernameTaken(String username, String currentSessionId) {
        for (Map.Entry<String, StudentProfile> entry : profiles.entrySet()) {
            if (!entry.getKey().equals(currentSessionId)
                    && entry.getValue().getUsername().equalsIgnoreCase(username)) return true;
        }
        return false;
    }

    private List<String> selectedInterests(Map<String, String> fields) {
        String packed = fields.getOrDefault("interest", "");
        List<String> selected = new ArrayList<>();
        for (String item : packed.split("\n")) {
            String interest = item.trim();
            if (ALLOWED_INTERESTS.contains(interest) && !selected.contains(interest)) selected.add(interest);
        }
        return selected;
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
        byte[] body = exchange.getRequestBody().readNBytes(MAX_FORM_BYTES + 1);
        if (body.length > MAX_FORM_BYTES) return new HashMap<>();
        String encoded = new String(body, StandardCharsets.UTF_8);
        Map<String, String> fields = new HashMap<>();
        for (String pair : encoded.split("&")) {
            if (pair.isEmpty()) continue;
            String[] parts = pair.split("=", 2);
            String key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
            String value = parts.length > 1 ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8) : "";
            fields.merge(key, value, (earlier, later) -> earlier + "\n" + later);
        }
        return fields;
    }

    private String sessionId(HttpExchange exchange) {
        String cookieHeader = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookieHeader != null) {
            for (String piece : cookieHeader.split(";")) {
                String[] pair = piece.trim().split("=", 2);
                if (pair.length == 2 && SESSION_COOKIE.equals(pair[0])) {
                    try {
                        return UUID.fromString(pair[1]).toString();
                    } catch (IllegalArgumentException ignored) {
                        // An invalid cookie is replaced with a new random demo session.
                    }
                }
            }
        }
        String newSessionId = UUID.randomUUID().toString();
        exchange.getResponseHeaders().add("Set-Cookie", sessionCookie(newSessionId));
        return newSessionId;
    }

    private String sessionCookie(String sessionId) {
        return SESSION_COOKIE + "=" + sessionId + "; Path=/; HttpOnly; SameSite=Lax";
    }

    private String multipartBoundary(String contentType) {
        if (contentType == null || !contentType.toLowerCase().startsWith("multipart/form-data")) return "";
        for (String part : contentType.split(";")) {
            String trimmed = part.trim();
            if (trimmed.startsWith("boundary=")) return trimmed.substring("boundary=".length()).replace("\"", "");
        }
        return "";
    }

    private PhotoPart findPhotoPart(byte[] body, String boundary) {
        byte[] marker = ("--" + boundary).getBytes(StandardCharsets.US_ASCII);
        byte[] headerEnd = "\r\n\r\n".getBytes(StandardCharsets.US_ASCII);
        byte[] nextPart = ("\r\n--" + boundary).getBytes(StandardCharsets.US_ASCII);
        int markerAt = findBytes(body, marker, 0);
        while (markerAt >= 0) {
            int headerStart = markerAt + marker.length + 2;
            int headerEndAt = findBytes(body, headerEnd, headerStart);
            if (headerEndAt < 0) return null;
            String headers = new String(body, headerStart, headerEndAt - headerStart, StandardCharsets.ISO_8859_1);
            int contentStart = headerEndAt + headerEnd.length;
            int contentEnd = findBytes(body, nextPart, contentStart);
            if (contentEnd < 0) return null;
            if (headers.contains("name=\"photo\"") && headers.contains("filename=")) {
                return new PhotoPart(Arrays.copyOfRange(body, contentStart, contentEnd));
            }
            markerAt = findBytes(body, marker, contentEnd + 2);
        }
        return null;
    }

    private int findBytes(byte[] source, byte[] target, int start) {
        outer: for (int i = Math.max(0, start); i <= source.length - target.length; i++) {
            for (int j = 0; j < target.length; j++) if (source[i + j] != target[j]) continue outer;
            return i;
        }
        return -1;
    }

    private String validImageExtension(byte[] bytes) {
        if (bytes.length >= 8 && (bytes[0] & 255) == 137 && bytes[1] == 80 && bytes[2] == 78 && bytes[3] == 71) return "png";
        if (bytes.length >= 3 && (bytes[0] & 255) == 255 && (bytes[1] & 255) == 216 && (bytes[2] & 255) == 255) return "jpg";
        if (bytes.length >= 6) {
            String gif = new String(bytes, 0, 6, StandardCharsets.US_ASCII);
            if ("GIF87a".equals(gif) || "GIF89a".equals(gif)) return "gif";
        }
        if (bytes.length >= 12 && "RIFF".equals(new String(bytes, 0, 4, StandardCharsets.US_ASCII))
                && "WEBP".equals(new String(bytes, 8, 4, StandardCharsets.US_ASCII))) return "webp";
        return "";
    }

    private String homePage(StudentProfile profile) {
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
        return HtmlLayout.page("Home", "home", "", content, profile);
    }

    private String notFoundPage(StudentProfile profile) {
        return HtmlLayout.page("Not found", "home", "", "<section class=\"page-hero\"><p class=\"eyebrow\">404 · LOST ON THE MOUNTAIN</p><h1>That page isn't here.</h1><a class=\"button button-dark\" href=\"/\">Back to home</a></section>", profile);
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
        Path path = Paths.get("public", "css", css);
        if (!Files.exists(path)) {
            send(exchange, 404, "Stylesheet not found. Start the server from the LehighHub folder.", "text/plain; charset=utf-8");
            return;
        }
        byte[] contents = Files.readAllBytes(path);
        exchange.getResponseHeaders().set("Content-Type", "text/css; charset=utf-8");
        exchange.sendResponseHeaders(200, contents.length);
        exchange.getResponseBody().write(contents);
        exchange.close();
    }

    private void redirect(HttpExchange exchange, String location) throws IOException {
        exchange.getResponseHeaders().set("Location", location);
        exchange.sendResponseHeaders(303, -1);
        exchange.close();
    }

    private void send(HttpExchange exchange, int status, String body, String contentType) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("Cache-Control", "no-store");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }

    private static final class PhotoPart {
        private final byte[] bytes;
        private PhotoPart(byte[] bytes) { this.bytes = bytes; }
    }

    /** A bulletin meeting kept in memory for this local demo. */
    public static final class ClubPosting {
        private final String title;
        private final LocalTime time;
        private final String location;
        private final List<String> topics;
        private final boolean open;
        private final String type;

        private ClubPosting(String title, LocalTime time, String location, List<String> topics, boolean open, String type) {
            this.title = title;
            this.time = time;
            this.location = location;
            this.topics = List.copyOf(topics);
            this.open = open;
            this.type = type;
        }

        public String getTitle() { return title; }
        public LocalTime getTime() { return time; }
        public String getTimeLabel() { return time.format(CLUB_TIME_LABEL); }
        public String getLocation() { return location; }
        public List<String> getTopics() { return topics; }
        public boolean isOpen() { return open; }
        public String getType() { return type; }
    }

    /** Profile data is intentionally in memory only and resets when the demo server restarts. */
    public static final class StudentProfile {
        private final String name;
        private final String username;
        private final String residenceHall;
        private final String graduationYear;
        private final String major;
        private final List<String> interests;
        private final String pictureFileName;

        public StudentProfile(String name, String username, String residenceHall, String graduationYear,
                String major, List<String> interests, String pictureFileName) {
            this.name = name;
            this.username = username;
            this.residenceHall = residenceHall;
            this.graduationYear = graduationYear;
            this.major = major;
            this.interests = List.copyOf(interests);
            this.pictureFileName = pictureFileName;
        }

        public String getName() { return name; }
        public String getUsername() { return username; }
        public String getResidenceHall() { return residenceHall; }
        public String getGraduationYear() { return graduationYear; }
        public String getMajor() { return major; }
        public List<String> getInterests() { return interests; }
        public String getPictureFileName() { return pictureFileName; }
        public boolean hasProfilePicture() { return pictureFileName != null && !pictureFileName.isEmpty(); }

        public StudentProfile withPicture(String fileName) {
            return new StudentProfile(name, username, residenceHall, graduationYear, major, interests, fileName);
        }
    }
}
