package com.lehighhub;

import java.time.Year;

/** Signup screen for the local demo profile. No password is saved or checked. */
public final class SignupPage {
    private SignupPage() { }

    public static String render(String message) {
        int currentYear = Year.now().getValue();
        int latestYear = currentYear + 4;
        String content = "<section class=\"account-hero\"><p class=\"eyebrow\">YOUR LU LINK</p>"
                + "<h1>Create a demo profile</h1><p>Add your interests and campus details to your profile.</p></section>"
                + HtmlLayout.notice(message)
                + "<section class=\"account-card signup-card\"><div class=\"account-card-heading\"><p class=\"eyebrow signup-title\">CREATE A PROFILE</p></div>"
                + "<div class=\"demo-privacy-note\"><strong>Current students</strong><span>Enter an expected graduation year from " + currentYear + " through " + latestYear + ". This local demo does not verify enrollment.</span></div>"
                + "<form method=\"post\" action=\"/signup\" class=\"account-form\"><div class=\"form-two-column\"><div><label for=\"signup-name\">Name</label><input id=\"signup-name\" name=\"name\" maxlength=\"60\" autocomplete=\"name\" required placeholder=\"Your name\"></div>"
                + "<div><label for=\"signup-username\">Username</label><input id=\"signup-username\" name=\"username\" minlength=\"3\" maxlength=\"20\" pattern=\"[A-Za-z0-9_]+\" autocomplete=\"username\" required placeholder=\"e.g. mountainhawk\"><small>3–20 letters, numbers, or underscores. Profanity, slurs, and sexual terms aren’t allowed.</small></div></div>"
                + "<label for=\"signup-grad-year\">Expected graduation year</label><input id=\"signup-grad-year\" name=\"graduationYear\" type=\"number\" min=\"" + currentYear + "\" max=\"" + latestYear + "\" step=\"1\" required placeholder=\"e.g. " + (currentYear + 2) + "\"><small>Reminder: the eligible range is " + currentYear + "–" + latestYear + ".</small>"
                + "<label for=\"signup-password\">Password <span class=\"field-hint\">demo field only</span></label><input id=\"signup-password\" name=\"password\" type=\"password\" minlength=\"8\" autocomplete=\"new-password\" required placeholder=\"At least 8 characters\">"
                + "<fieldset class=\"interest-fieldset\"><legend>Interests <span>Choose a few that sound like you</span></legend><div class=\"interest-options\">"
                + interest("Arts & culture", "arts") + interest("Outdoors", "outdoors") + interest("Fitness", "fitness")
                + interest("Food", "food") + interest("Music", "music") + interest("Technology", "technology")
                + interest("Service", "service") + interest("Gaming", "gaming") + interest("Career growth", "career")
                + "</div></fieldset><button class=\"button button-dark\" type=\"submit\">Create my profile <span aria-hidden=\"true\">↗</span></button></form></section>"
                + "<p class=\"account-back-link\"><a href=\"/\">← Back to the hub</a></p>";
        return HtmlLayout.page("Sign up", "", "", content);
    }

    private static String interest(String label, String value) {
        return "<label class=\"interest-choice\"><input type=\"checkbox\" name=\"interest\" value=\""
                + value + "\"><span>" + label + "</span></label>";
    }
}
