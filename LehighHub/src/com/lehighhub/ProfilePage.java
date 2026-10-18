package com.lehighhub;

import java.time.Year;

/** Editable, in-memory demo profile page. */
public final class ProfilePage {
    private ProfilePage() { }

    public static String render(String message, PageHandler.StudentProfile profile) {
        int currentYear = Year.now().getValue();
        int latestYear = currentYear + 4;
        String content = "<section class=\"profile-intro\"><div><p class=\"eyebrow\">PROFILE</p><h1>Your profile</h1><p>Update the details and interests on your demo profile.</p></div><span class=\"profile-demo-tag\">LOCAL DEMO PROFILE</span></section>"
                + HtmlLayout.notice(message)
                + "<section class=\"profile-layout\"><aside class=\"profile-aside\"><div class=\"profile-avatar-large\">"
                + (profile.hasProfilePicture() ? "<img src=\"/profile-picture\" alt=\"Profile picture for " + HtmlLayout.escape(profile.getName()) + "\">" : "<span class=\"profile-icon\" aria-hidden=\"true\"><i></i></span>")
                + "</div><p class=\"eyebrow\">PROFILE PHOTO</p><h2>Profile photo</h2><p>Choose a JPG, PNG, GIF, or WebP image. It is saved on this computer for the demo.</p>"
                + "<form class=\"photo-form\" method=\"post\" action=\"/profile-picture\" enctype=\"multipart/form-data\"><label for=\"profile-photo\">Choose an image</label><input id=\"profile-photo\" name=\"photo\" type=\"file\" accept=\"image/png,image/jpeg,image/gif,image/webp\" required onchange=\"if(this.files.length)this.form.requestSubmit()\"><small>Maximum file size: 2 MB.</small></form>"
                + (profile.hasProfilePicture() ? "<form method=\"post\" action=\"/profile-picture/remove\"><button class=\"text-button\" type=\"submit\">Remove current photo</button></form>" : "")
                + "</aside><div class=\"profile-edit-card\"><div class=\"account-card-heading\"><div><p class=\"eyebrow\">YOUR DETAILS</p><h2>Edit your profile</h2></div></div>"
                + "<form class=\"account-form\" method=\"post\" action=\"/profile\"><div class=\"form-two-column\"><div><label for=\"profile-name\">Name</label><input id=\"profile-name\" name=\"name\" value=\"" + value(profile.getName()) + "\" maxlength=\"60\" required></div>"
                + "<div><label for=\"profile-username\">Username</label><input id=\"profile-username\" name=\"username\" value=\"" + value(profile.getUsername()) + "\" minlength=\"3\" maxlength=\"20\" pattern=\"[A-Za-z0-9_]+\" required><small>Profanity, slurs, and sexual terms aren’t allowed.</small></div></div>"
                + "<div class=\"form-two-column\"><div><label for=\"profile-hall\">Residence hall</label><input id=\"profile-hall\" name=\"residenceHall\" value=\"" + value(profile.getResidenceHall()) + "\" maxlength=\"60\" placeholder=\"Optional\"></div>"
                + "<div><label for=\"profile-year\">Expected graduation year</label><input id=\"profile-year\" name=\"graduationYear\" value=\"" + value(profile.getGraduationYear()) + "\" type=\"number\" min=\"" + currentYear + "\" max=\"" + latestYear + "\" step=\"1\" required placeholder=\"e.g. " + (currentYear + 2) + "\"><small>Reminder: current students should graduate from " + currentYear + " through " + latestYear + ".</small></div></div>"
                + "<label for=\"profile-major\">Major or program</label><input id=\"profile-major\" name=\"major\" value=\"" + value(profile.getMajor()) + "\" maxlength=\"80\" placeholder=\"Optional\">"
                + "<fieldset class=\"interest-fieldset\"><legend>Interests <span>Click to update your selections</span></legend><div class=\"interest-options\">"
                + interest("Arts & culture", "arts", profile) + interest("Outdoors", "outdoors", profile) + interest("Fitness", "fitness", profile)
                + interest("Food", "food", profile) + interest("Music", "music", profile) + interest("Technology", "technology", profile)
                + interest("Service", "service", profile) + interest("Gaming", "gaming", profile) + interest("Career growth", "career", profile)
                + "</div></fieldset><button class=\"button button-dark\" type=\"submit\">Save changes <span aria-hidden=\"true\">↗</span></button></form></div>"
                + "<p class=\"account-back-link\"><a href=\"/\">← Back to the hub</a></p></section>";
        return HtmlLayout.page("Your profile", "", "", content, profile);
    }

    private static String interest(String label, String value, PageHandler.StudentProfile profile) {
        boolean checked = profile.getInterests().contains(value);
        return "<label class=\"interest-choice\"><input type=\"checkbox\" name=\"interest\" value=\""
                + value + "\"" + (checked ? " checked" : "") + "><span>" + label + "</span></label>";
    }

    private static String value(String value) {
        return HtmlLayout.escape(value);
    }
}
