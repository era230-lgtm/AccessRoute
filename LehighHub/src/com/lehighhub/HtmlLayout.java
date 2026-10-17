package com.lehighhub;

/** Shared page frame, navigation, and small HTML helpers used by every page. */
public final class HtmlLayout {
    private HtmlLayout() { }

    public static String page(String title, String activePage, String pageCss, String content) {
        StringBuilder html = new StringBuilder();
        html.append("<!doctype html><html lang=\"en\"><head>");
        html.append("<meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
        html.append("<title>").append(escape(title)).append(" · Lehigh Hub</title>");
        html.append("<link rel=\"stylesheet\" href=\"/css/shared.css\">");
        if (pageCss != null && !pageCss.isEmpty()) {
            html.append("<link rel=\"stylesheet\" href=\"/css/").append(escape(pageCss)).append("\">");
        }
        html.append("</head><body><a class=\"skip-link\" href=\"#main-content\">Skip to content</a>");
        html.append("<header class=\"site-header\"><div class=\"header-inner\">");
        html.append("<a class=\"brand\" href=\"/\" aria-label=\"Lehigh Hub home\"><span class=\"brand-mark\">H</span><span>Lehigh <b>Hub</b></span></a>");
        html.append("<nav class=\"main-nav\" aria-label=\"Main navigation\">");
        navLink(html, "/", "Home", activePage.equals("home"));
        navLink(html, "/clubs", "Clubs", activePage.equals("clubs"));
        navLink(html, "/dining", "Dining", activePage.equals("dining"));
        navLink(html, "/buses", "Buses", activePage.equals("buses"));
        navLink(html, "/resources", "Resources", activePage.equals("resources"));
        html.append("</nav><span class=\"term-label\">STUDENT GUIDE <i></i></span></div></header>");
        html.append("<main id=\"main-content\" class=\"page-wrap\">").append(content).append("</main>");
        html.append("<footer class=\"site-footer\"><div><span>LEHIGH HUB</span><span>Built for the Mountain Hawk community</span></div>");
        html.append("<p>Hackathon prototype · Sample information only · Not an official university service</p></footer>");
        html.append("</body></html>");
        return html.toString();
    }

    private static void navLink(StringBuilder html, String href, String label, boolean active) {
        html.append("<a href=\"").append(href).append("\"");
        if (active) html.append(" class=\"active\" aria-current=\"page\"");
        html.append(">").append(label).append("</a>");
    }

    public static String escape(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }

    public static String notice(String message) {
        if (message == null || message.isEmpty()) return "";
        return "<div class=\"form-notice\" role=\"status\">" + escape(message) + "</div>";
    }
}
