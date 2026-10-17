package com.lehighhub;

/** Lehigh-themed email gate shown before the local demo hub. */
public final class AccessPage {
    private AccessPage() { }

    public static String render(String message, String email) {
        String safeEmail = HtmlLayout.escape(email);
        return "<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"><title>Welcome · Lehigh Hub</title>"
                + "<link rel=\"stylesheet\" href=\"/css/shared.css\"><link rel=\"stylesheet\" href=\"/css/access.css\"><link rel=\"stylesheet\" href=\"/css/controls.css\"></head><body>"
                + "<main class=\"access-shell\"><section class=\"access-story\"><a class=\"access-brand\" href=\"/\"><span class=\"access-brand-mark\">H</span><span>Lehigh <b>Hub</b></span></a>"
                + "<div class=\"access-story-copy\"><p class=\"access-eyebrow\">YOUR CAMPUS, CONNECTED</p><h1>All of Lehigh,<br><em>one place.</em></h1>"
                + "<p class=\"access-description\">Find your people, plan your day, and get to the campus resources you need—all in one welcoming hub.</p>"
                + "<div class=\"access-highlights\"><strong>Campus life, made easier.</strong><div><span>✳ &nbsp;Clubs &amp; meetups</span><span>◒ &nbsp;Dining plans</span><span>↗ &nbsp;Bus routes</span><span>＋ &nbsp;Student resources</span></div></div></div>"
                + "<p class=\"access-footer\">For the Lehigh community</p></section><section class=\"access-form-panel\"><div class=\"access-form-card\">"
                + "<p class=\"access-form-eyebrow\">WELCOME TO THE MOUNTAIN</p><h2>Get started</h2><p class=\"access-form-intro\">Enter your Lehigh email to continue to Lehigh Hub.</p>"
                + (message == null || message.isEmpty() ? "" : "<p class=\"access-error\" role=\"alert\">" + HtmlLayout.escape(message) + "</p>")
                + "<form method=\"post\" action=\"/access\"><label for=\"lehigh-email\">Lehigh email</label>"
                + "<input id=\"lehigh-email\" name=\"email\" type=\"email\" autocomplete=\"email\" required maxlength=\"254\" pattern=\"[A-Za-z0-9._%+\\-]+@lehigh\\.edu\" title=\"Use your @lehigh.edu email address\" placeholder=\"you@lehigh.edu\" value=\""
                + safeEmail + "\"><button type=\"submit\">Continue to Lehigh Hub <span aria-hidden=\"true\">↗</span></button></form>"
                + "<p class=\"access-note\">This local prototype checks that your address ends in <b>@lehigh.edu</b>. It does not verify inbox ownership or use Lehigh single sign-on.</p>"
                + "<p class=\"access-terms\">By continuing, you agree to use this student-built demo respectfully.</p></div></section></main></body></html>";
    }
}
