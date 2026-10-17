package com.lehighhub;

/** Sample directory of free student resources, organized by category. */
public class ResourcesPage {
    public static String render(String message) {
        return render(message, null);
    }

    public static String render(String message, PageHandler.StudentProfile profile) {
        String content = "<section class=\"page-hero resources-hero\"><div><p class=\"eyebrow\">SUPPORT / STUDENT RESOURCES</p><h1>Good things are<br><em>within reach.</em></h1><p>Explore some starting points for support, learning, and campus life. Sample listings only.</p></div><div class=\"resource-art\"><span class=\"resource-orbit orbit-one\"></span><span class=\"resource-orbit orbit-two\"></span><span class=\"resource-core\">+</span><b>YOU<br>GOT<br>THIS</b></div></section>"
                + HtmlLayout.notice(message)
                + "<section class=\"resource-toolbar\"><div><p class=\"eyebrow\">FREE STUDENT RESOURCES</p><h2>A place to start.</h2></div><form method=\"post\" action=\"/resources\"><label for=\"category\">Browse by</label><select id=\"category\" name=\"category\"><option>All resources</option><option>Academic</option><option>Wellbeing</option><option>Community</option></select><button type=\"submit\" class=\"filter-button\">Filter <span>↗</span></button></form></section>"
                + "<section class=\"resource-grid\"><article class=\"resource-card\"><span class=\"resource-icon icon-academic\">A</span><span class=\"resource-type\">ACADEMIC</span><h3>Peer Learning Studio</h3><p>Imagine drop-in study tables, collaborative problem solving, and a friendly study reset.</p><div class=\"resource-card-foot\"><span>Sample listing</span><a href=\"#official-note\" aria-label=\"More information about Peer Learning Studio\">↗</a></div></article>"
                + "<article class=\"resource-card\"><span class=\"resource-icon icon-wellbeing\">♡</span><span class=\"resource-type\">WELLBEING</span><h3>Student Wellbeing Desk</h3><p>A sample starting point for finding campus wellbeing information and support options.</p><div class=\"resource-card-foot\"><span>Sample listing</span><a href=\"#official-note\" aria-label=\"More information about Student Wellbeing Desk\">↗</a></div></article>"
                + "<article class=\"resource-card\"><span class=\"resource-icon icon-community\">✳</span><span class=\"resource-type\">COMMUNITY</span><h3>Community Connections</h3><p>Picture a welcoming place to learn about student groups, events, and ways to get involved.</p><div class=\"resource-card-foot\"><span>Sample listing</span><a href=\"#official-note\" aria-label=\"More information about Community Connections\">↗</a></div></article>"
                + "<article class=\"resource-card\"><span class=\"resource-icon icon-career\">↗</span><span class=\"resource-type\">CAREER</span><h3>Career Exploration Corner</h3><p>A mock collection of tools for exploring interests, building skills, and preparing for next steps.</p><div class=\"resource-card-foot\"><span>Sample listing</span><a href=\"#official-note\" aria-label=\"More information about Career Exploration Corner\">↗</a></div></article></section>"
                + "<section class=\"resource-note\" id=\"official-note\"><span>i</span><p><strong>Need current details?</strong> These are fictional examples, not actual Lehigh programs. Visit official Lehigh University channels for accurate eligibility, hours, and availability.</p></section>";
        return HtmlLayout.page("Student resources", "resources", "resources.css", content, profile);
    }
}
