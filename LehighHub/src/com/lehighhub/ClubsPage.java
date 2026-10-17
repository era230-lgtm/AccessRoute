package com.lehighhub;

import java.util.List;
import java.util.Map;

/** Club discovery and a local-only bulletin board. */
public class ClubsPage {
    private static final List<String> SAMPLE_IDS = List.of(
            "sample-makers", "sample-trails", "sample-design", "sample-garden");

    public static String render(String message) {
        return render(message, null, List.of(), false, "", Map.of(), "");
    }

    public static String render(String message, PageHandler.StudentProfile profile) {
        return render(message, profile, List.of(), false, "", Map.of(), "");
    }

    public static String render(String message, PageHandler.StudentProfile profile,
            List<PageHandler.ClubPosting> postings, boolean reopenPostDialog, String popupError) {
        return render(message, profile, postings, reopenPostDialog, popupError, Map.of(), "");
    }

    public static String render(String message, PageHandler.StudentProfile profile,
            List<PageHandler.ClubPosting> postings, boolean reopenPostDialog, String popupError,
            Map<String, ? extends List<PageHandler.RsvpEntry>> rsvps, String meetingToOpen) {
        StringBuilder content = new StringBuilder();
        content.append("<section class=\"page-hero clubs-hero\"><div><p class=\"eyebrow\">COMMUNITY / CLUB BULLETIN</p><h1>Find your <em>people.</em></h1><p>Meet the groups making things happen around campus. These listings are examples for the prototype.</p></div></section>")
                .append(HtmlLayout.notice(message))
                .append("<div class=\"page-toolbar\"><div><span class=\"eyebrow\">SAMPLE CLUBS</span><h2>Something for every side of you.</h2></div><div class=\"club-toolbar-actions\"><span class=\"count-pill\">")
                .append(String.format("%02d", 4 + postings.size())).append(" MEETINGS</span>");
        if (profile == null) {
            content.append("<a class=\"post-button\" href=\"/signup\"><span aria-hidden=\"true\">＋</span> Create profile to post</a>");
        } else {
            content.append("<button class=\"post-button\" type=\"button\" data-open-post><span aria-hidden=\"true\">＋</span> Post</button>");
        }
        content.append("</div></div>")
                .append("<section class=\"club-filters\" aria-label=\"Filter club postings\"><label>Topic<select id=\"filter-topic\"><option value=\"all\">All topics</option><option value=\"arts\">Arts</option><option value=\"outdoors\">Outdoors</option><option value=\"fitness\">Fitness</option><option value=\"food\">Food</option><option value=\"music\">Music</option><option value=\"technology\">Technology</option><option value=\"service\">Service</option><option value=\"gaming\">Gaming</option><option value=\"career\">Career</option></select></label><label>Openness<select id=\"filter-access\"><option value=\"all\">Open and closed</option><option value=\"open\">Open</option><option value=\"closed\">Closed</option></select></label><label>Post type<select id=\"filter-kind\"><option value=\"all\">Clubs and hangouts</option><option value=\"club\">Club</option><option value=\"hangout\">Hangout</option></select></label><span class=\"filter-results\" id=\"filter-results\" aria-live=\"polite\"></span></section>")
                .append("<section class=\"club-grid\" id=\"club-grid\">");

        appendMeetingCard(content, "sample-makers", "arts", "open", "club", "ARTS &amp; CULTURE", "01",
                "Mountain Makers", "Creative meetups, open studio nights, and low-pressure ways to try something new.",
                "Thursdays · 6:30 PM", "UC Room 202", "LU Link sample", "", List.of("arts"), "club-art-one");
        appendMeetingCard(content, "sample-trails", "outdoors", "open", "club", "OUTDOORS", "02",
                "Lehigh Trail Collective", "Weekend walks and trail days for anyone who wants a breath of fresh air.",
                "Saturdays · 10:00 AM", "Alumni Building steps", "LU Link sample", "", List.of("outdoors"), "club-art-two");
        appendMeetingCard(content, "sample-design", "technology,service", "open", "club", "TECH &amp; DESIGN", "03",
                "Design for Good", "Students teaming up to sketch, prototype, and share ideas for community projects.",
                "Tuesdays · 7:00 PM", "Packard Lab 112", "LU Link sample", "", List.of("technology", "service"), "club-art-three");
        appendMeetingCard(content, "sample-garden", "food,outdoors", "open", "club", "FOOD &amp; GARDEN", "04",
                "Campus Garden Crew", "Grow, learn, and share what is in season at our friendly garden work sessions.",
                "Wednesdays · 4:30 PM", "Community Garden", "LU Link sample", "", List.of("food", "outdoors"), "club-art-four");

        for (PageHandler.ClubPosting posting : postings) {
            String label = posting.getType().equals("hangout") ? "HANGOUT"
                    : posting.getTopics().isEmpty() ? "CLUB MEETING" : posting.getTopics().get(0).toUpperCase();
            appendMeetingCard(content, posting.getId(), String.join(",", posting.getTopics()),
                    posting.isOpen() ? "open" : "closed", posting.getType(), label, "✳", posting.getTitle(),
                    "Community meeting posted to LU Link.", posting.getTimeLabel(), posting.getLocation(),
                    posting.getPosterName(), posting.getPosterUsername(), posting.getTopics(), "club-art-posted");
        }
        content.append("</section>");

        for (String id : SAMPLE_IDS) appendAttendeeTemplate(content, id, rsvps, profile);
        for (PageHandler.ClubPosting posting : postings) appendAttendeeTemplate(content, posting.getId(), rsvps, profile);

        appendMeetingDialog(content, profile);
        appendPostDialog(content);
        content.append("<script>(function(){const postDialog=document.getElementById('post-dialog'),openPost=document.querySelector('[data-open-post]');if(openPost)openPost.addEventListener('click',()=>{postDialog.showModal();if(window.raiseCustomCursor)window.raiseCustomCursor();});document.querySelector('[data-close-post]').addEventListener('click',()=>postDialog.close());postDialog.addEventListener('click',event=>{if(event.target===postDialog)postDialog.close();});const topic=document.getElementById('filter-topic'),access=document.getElementById('filter-access'),kind=document.getElementById('filter-kind'),cards=Array.from(document.querySelectorAll('.club-card')),results=document.getElementById('filter-results');function filterCards(){let visible=0;cards.forEach(card=>{const topics=(card.dataset.topics||'').split(',');const show=(topic.value==='all'||topics.includes(topic.value))&&(access.value==='all'||card.dataset.access===access.value)&&(kind.value==='all'||card.dataset.kind===kind.value);card.hidden=!show;if(show)visible++;});results.textContent=visible+' '+(visible===1?'meeting':'meetings');}[topic,access,kind].forEach(filter=>filter.addEventListener('change',filterCards));filterCards();")
                .append("const meetingDialog=document.getElementById('meeting-dialog'),meetingCards=Array.from(document.querySelectorAll('[data-meeting-id]')),attendeeList=document.getElementById('attendee-list'),rsvpId=document.getElementById('rsvp-meeting-id'),rsvpButton=document.getElementById('rsvp-button');function openMeeting(card){document.getElementById('meeting-title').textContent=card.dataset.title;document.getElementById('meeting-poster').textContent=card.dataset.poster+(card.dataset.username?' (@'+card.dataset.username+')':'');document.getElementById('meeting-time').textContent=card.dataset.time;document.getElementById('meeting-location').textContent=card.dataset.location;document.getElementById('meeting-access').textContent=card.dataset.access==='open'?'Open meeting':'Closed meeting';document.getElementById('meeting-kind').textContent=card.dataset.kind==='hangout'?'Hangout':'Club';const topics=card.dataset.topics.split(',').filter(Boolean);document.getElementById('meeting-topics').innerHTML=topics.length?topics.map(t=>'<span class=\"club-topic-chip topic-'+t+'\">'+t.charAt(0).toUpperCase()+t.slice(1)+'</span>').join(''):'<span class=\"club-topic-chip topic-general\">General</span>';const id=card.dataset.meetingId;if(rsvpId)rsvpId.value=id;attendeeList.replaceChildren();const template=document.querySelector('template[data-rsvps-for=\"'+id+'\"]');if(template)attendeeList.append(template.content.cloneNode(true));const going=template&&template.dataset.currentGoing==='true';if(rsvpButton){rsvpButton.disabled=!!going;rsvpButton.textContent=going?'You’re going':'RSVP';}meetingDialog.showModal();if(window.raiseCustomCursor)window.raiseCustomCursor();}meetingCards.forEach(card=>{card.addEventListener('click',()=>openMeeting(card));card.addEventListener('keydown',event=>{if(event.key==='Enter'||event.key===' '){event.preventDefault();openMeeting(card);}});});document.querySelector('[data-close-meeting]').addEventListener('click',()=>meetingDialog.close());meetingDialog.addEventListener('click',event=>{if(event.target===meetingDialog)meetingDialog.close();});");
        if (reopenPostDialog) content.append("postDialog.showModal();");
        if (meetingToOpen != null && !meetingToOpen.isEmpty()) {
            content.append("const restoreMeeting=meetingCards.find(item=>item.dataset.meetingId===")
                    .append(jsString(meetingToOpen)).append(");if(restoreMeeting)openMeeting(restoreMeeting);");
        }
        if (!popupError.isEmpty()) content.append("window.alert(").append(jsString(popupError)).append(");");
        content.append("})();</script>");
        return HtmlLayout.page("Club bulletin", "clubs", "clubs.css", content.toString(), profile);
    }

    private static void appendMeetingCard(StringBuilder content, String id, String topics, String access,
            String kind, String header, String number, String title, String description, String time,
            String location, String poster, String username, List<String> topicList, String artwork) {
        content.append("<article class=\"club-card meeting-card\" role=\"button\" tabindex=\"0\" aria-label=\"View meeting details for ")
                .append(HtmlLayout.escape(title)).append("\" data-meeting-id=\"").append(HtmlLayout.escape(id))
                .append("\" data-title=\"").append(HtmlLayout.escape(title)).append("\" data-time=\"")
                .append(HtmlLayout.escape(time)).append("\" data-location=\"").append(HtmlLayout.escape(location))
                .append("\" data-poster=\"").append(HtmlLayout.escape(poster)).append("\" data-username=\"")
                .append(HtmlLayout.escape(username)).append("\" data-topics=\"").append(HtmlLayout.escape(topics))
                .append("\" data-access=\"").append(HtmlLayout.escape(access)).append("\" data-kind=\"")
                .append(HtmlLayout.escape(kind)).append("\"><div class=\"club-card-top ").append(artwork)
                .append("\"><span>").append(header).append("</span><b>").append(number)
                .append("</b></div><div class=\"club-card-body\"><h3>").append(HtmlLayout.escape(title))
                .append("</h3><p>").append(HtmlLayout.escape(description)).append("</p>")
                .append(topicChips(topicList));
        if ("LU Link sample".equals(poster)) content.append(sampleByline());
        else content.append(posterByline(poster, username));
        content.append("<div class=\"club-meta\"><span>◷ ").append(HtmlLayout.escape(time))
                .append("</span><span>▦ ").append(HtmlLayout.escape(location)).append("</span><span class=\"meeting-access ")
                .append("open".equals(access) ? "open\">Open" : "closed\">Closed")
                .append("</span><span class=\"meeting-kind\">").append("hangout".equals(kind) ? "Hangout" : "Club")
                .append("</span></div><span class=\"meeting-view-hint\">View details &amp; RSVP ↗</span></div></article>");
    }

    private static void appendAttendeeTemplate(StringBuilder content, String id,
            Map<String, ? extends List<PageHandler.RsvpEntry>> rsvps, PageHandler.StudentProfile currentProfile) {
        List<PageHandler.RsvpEntry> attendees = rsvps.containsKey(id) ? rsvps.get(id) : List.of();
        boolean currentGoing = currentProfile != null && attendees.stream()
                .anyMatch(attendee -> attendee.getUsername().equalsIgnoreCase(currentProfile.getUsername()));
        content.append("<template data-rsvps-for=\"").append(HtmlLayout.escape(id)).append("\" data-current-going=\"")
                .append(currentGoing).append("\">");
        if (attendees.isEmpty()) {
            content.append("<p class=\"attendee-empty\">No one has RSVP’d yet. Be the first to join.</p>");
        } else {
            for (PageHandler.RsvpEntry attendee : attendees) appendAttendee(content, attendee);
        }
        content.append("</template>");
    }

    private static void appendAttendee(StringBuilder content, PageHandler.RsvpEntry attendee) {
        content.append("<article class=\"attendee-row\"><span class=\"club-avatar\" aria-hidden=\"true\">")
                .append(HtmlLayout.escape(initials(attendee.getName()))).append("</span><div class=\"attendee-name\"><b>")
                .append(HtmlLayout.escape(attendee.getName())).append("</b><small>@")
                .append(HtmlLayout.escape(attendee.getUsername())).append("</small></div><div class=\"attendee-profile-facts\"><span><b>Interests</b> ")
                .append(HtmlLayout.escape(formatInterests(attendee.getInterests()))).append("</span><span><b>Residence</b> ")
                .append(HtmlLayout.escape(attendee.getResidenceHall().isEmpty() ? "Not shared" : attendee.getResidenceHall()))
                .append("</span><span><b>Class</b> ").append(HtmlLayout.escape(attendee.getGraduationYear().isEmpty()
                        ? "Not shared" : attendee.getGraduationYear())).append("</span></div></article>");
    }

    private static String formatInterests(List<String> interests) {
        if (interests.isEmpty()) return "Not shared";
        return String.join(", ", interests.stream()
                .map(value -> value.substring(0, 1).toUpperCase() + value.substring(1)).toList());
    }

    private static void appendMeetingDialog(StringBuilder content, PageHandler.StudentProfile profile) {
        content.append("<dialog class=\"meeting-dialog\" id=\"meeting-dialog\" aria-labelledby=\"meeting-title\"><button class=\"dialog-close meeting-dialog-close\" type=\"button\" aria-label=\"Close meeting details\" data-close-meeting>×</button>")
                .append("<header class=\"meeting-dialog-header\"><p class=\"eyebrow\">MEETING DETAILS</p><h2 id=\"meeting-title\"></h2><div class=\"meeting-essential\"><span><small>Posted by</small><b id=\"meeting-poster\"></b></span><span><small>When</small><b id=\"meeting-time\"></b></span><span><small>Where</small><b id=\"meeting-location\"></b></span></div><div class=\"meeting-detail-badges\"><span id=\"meeting-access\"></span><span id=\"meeting-kind\"></span><div id=\"meeting-topics\" class=\"club-topic-list\"></div></div></header>")
                .append("<section class=\"attendee-section\"><div class=\"attendee-heading\"><div><p class=\"eyebrow\">COMMUNITY</p><h3>Who's going?</h3></div><span class=\"attendee-note\">RSVPs from LU Link profiles</span></div><div class=\"attendee-list\" id=\"attendee-list\"></div></section>");
        if (profile == null) {
            content.append("<a class=\"rsvp-button\" href=\"/signup\">Create a profile to RSVP ↗</a>");
        } else {
            content.append("<form id=\"rsvp-form\" method=\"post\" action=\"/clubs/rsvp\"><input id=\"rsvp-meeting-id\" type=\"hidden\" name=\"meetingId\"><button id=\"rsvp-button\" class=\"rsvp-button\" type=\"submit\">RSVP</button></form>");
        }
        content.append("</dialog>");
    }

    private static void appendPostDialog(StringBuilder content) {
        content.append("<dialog class=\"post-dialog\" id=\"post-dialog\" aria-labelledby=\"post-dialog-title\"><div class=\"post-dialog-heading\"><div><p class=\"eyebrow\">CLUB BULLETIN</p><h2 id=\"post-dialog-title\">Create a meeting post</h2></div><button class=\"dialog-close\" type=\"button\" aria-label=\"Close post window\" data-close-post>×</button></div>")
                .append("<form method=\"post\" action=\"/clubs\" class=\"post-dialog-form\"><label for=\"post-title\">Post title</label><input id=\"post-title\" name=\"title\" required maxlength=\"60\" placeholder=\"e.g. Campus Garden Crew\"><label for=\"post-time\">Meeting time</label><input id=\"post-time\" name=\"time\" type=\"time\" required><label for=\"post-location\">Location</label><input id=\"post-location\" name=\"location\" required maxlength=\"80\" placeholder=\"Building and room\">")
                .append("<fieldset class=\"post-topic-fieldset\"><legend>Topics <span>Select all that apply</span></legend><div class=\"post-topic-options\">");
        String[][] topics = {{"arts", "Arts"}, {"outdoors", "Outdoors"}, {"fitness", "Fitness"}, {"food", "Food"}, {"music", "Music"}, {"technology", "Technology"}, {"service", "Service"}, {"gaming", "Gaming"}, {"career", "Career"}};
        for (String[] topic : topics) {
            content.append("<label class=\"post-topic-choice\"><input type=\"checkbox\" name=\"topic\" value=\"")
                    .append(topic[0]).append("\"><span>").append(topic[1]).append("</span></label>");
        }
        content.append("</div></fieldset><fieldset class=\"post-access-fieldset\"><legend>Meeting access</legend><div class=\"post-access-options\"><label><input type=\"radio\" name=\"access\" value=\"open\" checked><span>Open</span></label><label><input type=\"radio\" name=\"access\" value=\"closed\"><span>Closed</span></label></div></fieldset><fieldset class=\"post-access-fieldset\"><legend>Post type</legend><div class=\"post-access-options\"><label><input type=\"radio\" name=\"type\" value=\"club\" checked><span>Club</span></label><label><input type=\"radio\" name=\"type\" value=\"hangout\"><span>Hangout</span></label></div></fieldset><button class=\"button button-dark confirm-post-button\" type=\"submit\">Confirm posting <span aria-hidden=\"true\">↗</span></button></form></dialog>");
    }

    private static String topicChips(List<String> topics) {
        StringBuilder chips = new StringBuilder("<div class=\"club-topic-list\" aria-label=\"Meeting topics\">");
        if (topics.isEmpty()) {
            chips.append("<span class=\"club-topic-chip topic-general\">General</span>");
        } else {
            for (String topic : topics) {
                String label = topic.substring(0, 1).toUpperCase() + topic.substring(1);
                chips.append("<span class=\"club-topic-chip topic-").append(HtmlLayout.escape(topic)).append("\">")
                        .append(HtmlLayout.escape(label)).append("</span>");
            }
        }
        return chips.append("</div>").toString();
    }

    private static String sampleByline() {
        return "<div class=\"club-posted-by sample-byline\"><span class=\"club-avatar\" aria-hidden=\"true\">LU</span><span><small>Sample listing</small><b>LU Link</b></span></div>";
    }

    private static String posterByline(String name, String username) {
        return "<div class=\"club-posted-by\"><span class=\"club-avatar\" aria-hidden=\"true\">" + HtmlLayout.escape(initials(name))
                + "</span><span><small>Posted by</small><b>" + HtmlLayout.escape(name) + "</b><em>@"
                + HtmlLayout.escape(username) + "</em></span></div>";
    }

    private static String initials(String name) {
        String[] parts = name.trim().split("\\s+");
        StringBuilder initials = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty() && initials.length() < 2) initials.append(Character.toUpperCase(part.charAt(0)));
        }
        return initials.length() == 0 ? "?" : initials.toString();
    }

    private static String jsString(String value) {
        return "'" + value.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n").replace("\r", "\\r") + "'";
    }
}
