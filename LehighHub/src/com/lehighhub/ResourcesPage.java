package com.lehighhub;

/** Official Lehigh links for academic, campus, financial, and media resources. */
public class ResourcesPage {
    public static String render(String message) {
        return render(message, null);
    }

    public static String render(String message, PageHandler.StudentProfile profile) {
        String content = "<section class=\"page-hero resources-hero\"><div><p class=\"eyebrow\">SUPPORT / STUDENT RESOURCES</p><h1>Lehigh resources</h1><p>Find campus services, academic support, student account tools, and news access.</p></div></section>"
                + HtmlLayout.notice(message)
                + section("CENTRAL HUBS & ACADEMIC SUPPORT", "Campus essentials", "",
                    card("Connect Lehigh Portal", "Open the central hub for campus applications, Google Workspace, CourseSite, and administrative dashboards.", link("Open Connect Lehigh", "https://connect.lehigh.edu/"), link("Student technology resources", "https://library.lehigh.edu/popular-resources-students"), link("Connect Lehigh help", "https://lehigh.atlassian.net/wiki/x/HU2XAQ"))
                    + card("Office of the Registrar", "Access enrollment information, transcripts, major and minor declarations, and degree audit tools.", link("Current student services", "https://registrar.lehigh.edu/current-students"))
                    + card("Academic Success and Writing Center", "Find academic support, writing and research resources, and ways to connect with the center.", link("Explore writing and research support", "https://studentaffairs.lehigh.edu/writing-research-resources"), link("University support directory", "https://studentaffairs.lehigh.edu/content/university-resources"))
                    + card("Lehigh Libraries & Technology Services", "Search library resources, find technology support, and explore campus software and computing options.", link("Popular student resources", "https://library.lehigh.edu/popular-resources-students"), link("University-wide student resources", "https://ed.lehigh.edu/university-wide-student-resources")))
                + section("HEALTH, WELLNESS & CAMPUS SAFETY", "Support on campus", "Connect with campus services and student-life support.",
                    card("Campus Resource Guide", "A directory for physical health, fitness facilities, student-life programs, and campus support.", link("Open the campus guide", "https://studentaffairs.lehigh.edu/student-life-leadership/campus-resource-guide"))
                    + card("Student Affairs Resources", "Find student support amenities, including information about the food pantry and health product center.", link("Explore Student Affairs resources", "https://studentaffairs.lehigh.edu/content/resources-1"))
                    + card("University Resources Overview", "Find contact information and links for campus police, counseling, disability accommodations, and other support.", link("View university resources", "https://studentaffairs.lehigh.edu/content/university-resources"), link("Resources for students", "https://catalog.lehigh.edu/universityresources/resourcesforstudents/")))
                + section("TUITION & CAREER", "Student accounts and careers", "Manage student accounts and find career resources.",
                    card("Bursar Office Banner Access", "Access e-Bill tools for tuition and housing payments, and manage family billing authorizations.", link("Open Banner access information", "https://financeadmin.lehigh.edu/content/banner-access"))
                    + card("University Catalog Student Resources", "Browse the catalog’s student resources, including career counseling and fellowship guidance.", link("Explore student resources", "https://catalog.lehigh.edu/universityresources/resourcesforstudents/")))
                + section("EDUCATION & NEWS", "News access", "Use Lehigh’s guides to access participating publications.",
                    card("The Wall Street Journal", "Activate your Lehigh access, then visit the Wall Street Journal.", link("WSJ access information", "https://library.lehigh.edu/news/lehigh-ranks-15th-celebrate-free-access-wall-street-journal"), link("Open WSJ", "https://www.wsj.com/"))
                    + card("The New York Times", "Review Lehigh’s news access instructions and activate your account.", link("NYT access instructions", "https://libraryguides.lehigh.edu/journalism/news"), link("Activate NYT access", "https://www.nytimes.com/activate-access/ippass"))
                    + card("Washington Post & Financial Media", "Use Lehigh’s library directories to find access information for the Washington Post, The Economist, and Financial Times.", link("Read news through Lehigh Libraries", "https://library.lehigh.edu/find/read-nyt-wsj-wapo-more"), link("Business Resources Directory", "https://libraryguides.lehigh.edu/corebusiness"), link("Library access announcement", "https://library.lehigh.edu/news/lehigh-libraries-bring-you-new-york-times-wall-street-journal-and-washington-post")))
                + "<p class=\"resources-footnote\">Links open official Lehigh University pages or publication websites in a new tab. Access requirements may vary by resource.</p>";
        return HtmlLayout.page("Student resources", "resources", "resources.css", content, profile);
    }

    private static String section(String eyebrow, String title, String description, String cards) {
        return "<section class=\"resource-section\"><div class=\"resource-section-heading\"><div><p class=\"eyebrow\">" + eyebrow + "</p><h2>" + title + "</h2></div>"
                + (description.isEmpty() ? "" : "<p>" + description + "</p>") + "</div><div class=\"resource-grid\">" + cards + "</div></section>";
    }

    private static String card(String title, String description, String... links) {
        StringBuilder html = new StringBuilder("<article class=\"resource-card\"><h3>" + title + "</h3><p>" + description + "</p><div class=\"resource-links\">");
        for (String item : links) html.append(item);
        return html.append("</div></article>").toString();
    }

    private static String link(String label, String url) {
        return "<a class=\"resource-link\" href=\"" + url + "\" target=\"_blank\" rel=\"noopener noreferrer\">" + label + " <span aria-hidden=\"true\">↗</span></a>";
    }
}
