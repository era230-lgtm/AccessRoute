package com.lehighhub;

import java.util.List;

/** Club discovery and a local-only bulletin board. */
public class ClubsPage {
    public static String render(String message) {
        return render(message, null, List.of(), false, "");
    }

    public static String render(String message, PageHandler.StudentProfile profile) {
        return render(message, profile, List.of(), false, "");
    }

    public static String render(String message, PageHandler.StudentProfile profile,
            List<PageHandler.ClubPosting> postings, boolean reopenDialog, String popupError) {
        StringBuilder content = new StringBuilder();
        content.append("<section class=\"page-hero clubs-hero\"><div><p class=\"eyebrow\">COMMUNITY / CLUB BULLETIN</p><h1>Find your <em>people.</em></h1><p>Meet the groups making things happen around campus. These listings are examples for the prototype.</p></div><div class=\"hero-stamp\">JOIN<br>IN<span>✳</span></div></section>")
                .append(HtmlLayout.notice(message))
                .append("<div class=\"page-toolbar\"><div><span class=\"eyebrow\">SAMPLE CLUBS</span><h2>Something for every side of you.</h2></div><div class=\"club-toolbar-actions\"><span class=\"count-pill\">")
                .append(String.format("%02d", 4 + postings.size())).append(" MEETINGS</span><button class=\"post-button\" type=\"button\" data-open-post><span aria-hidden=\"true\">＋</span> Post</button></div></div>")
                .append("<section class=\"club-filters\" aria-label=\"Filter club postings\"><label>Topic<select id=\"filter-topic\"><option value=\"all\">All topics</option><option value=\"arts\">Arts</option><option value=\"outdoors\">Outdoors</option><option value=\"fitness\">Fitness</option><option value=\"food\">Food</option><option value=\"music\">Music</option><option value=\"technology\">Technology</option><option value=\"service\">Service</option><option value=\"gaming\">Gaming</option><option value=\"career\">Career</option></select></label><label>Openness<select id=\"filter-access\"><option value=\"all\">Open and closed</option><option value=\"open\">Open</option><option value=\"closed\">Closed</option></select></label><label>Post type<select id=\"filter-kind\"><option value=\"all\">Clubs and hangouts</option><option value=\"club\">Club</option><option value=\"hangout\">Hangout</option></select></label><span class=\"filter-results\" id=\"filter-results\" aria-live=\"polite\"></span></section>")
                .append("<section class=\"club-grid\" id=\"club-grid\"><article class=\"club-card\" data-topics=\"arts\" data-access=\"open\" data-kind=\"club\"><div class=\"club-card-top club-art-one\"><span>ARTS &amp; CULTURE</span><b>01</b></div><div class=\"club-card-body\"><h3>Mountain Makers</h3><p>Creative meetups, open studio nights, and low-pressure ways to try something new.</p><div class=\"club-meta\"><span>◷ Thursdays · 6:30 PM</span><span>▦ UC Room 202</span><span class=\"meeting-access open\">Open</span></div></div></article>")
                .append("<article class=\"club-card\" data-topics=\"outdoors\" data-access=\"open\" data-kind=\"club\"><div class=\"club-card-top club-art-two\"><span>OUTDOORS</span><b>02</b></div><div class=\"club-card-body\"><h3>Lehigh Trail Collective</h3><p>Weekend walks and trail days for anyone who wants a breath of fresh air.</p><div class=\"club-meta\"><span>◷ Saturdays · 10:00 AM</span><span>▦ Alumni Building steps</span><span class=\"meeting-access open\">Open</span></div></div></article>")
                .append("<article class=\"club-card\" data-topics=\"technology,service\" data-access=\"open\" data-kind=\"club\"><div class=\"club-card-top club-art-three\"><span>TECH &amp; DESIGN</span><b>03</b></div><div class=\"club-card-body\"><h3>Design for Good</h3><p>Students teaming up to sketch, prototype, and share ideas for community projects.</p><div class=\"club-meta\"><span>◷ Tuesdays · 7:00 PM</span><span>▦ Packard Lab 112</span><span class=\"meeting-access open\">Open</span></div></div></article>")
                .append("<article class=\"club-card\" data-topics=\"food,outdoors\" data-access=\"open\" data-kind=\"club\"><div class=\"club-card-top club-art-four\"><span>FOOD &amp; GARDEN</span><b>04</b></div><div class=\"club-card-body\"><h3>Campus Garden Crew</h3><p>Grow, learn, and share what is in season at our friendly garden work sessions.</p><div class=\"club-meta\"><span>◷ Wednesdays · 4:30 PM</span><span>▦ Community Garden</span><span class=\"meeting-access open\">Open</span></div></div></article>");
        for (PageHandler.ClubPosting posting : postings) {
            content.append("<article class=\"club-card posted-club\" data-topics=\"")
                    .append(HtmlLayout.escape(String.join(",", posting.getTopics())))
                    .append("\" data-access=\"").append(posting.isOpen() ? "open" : "closed")
                    .append("\" data-kind=\"").append(posting.getType()).append("\"><div class=\"club-card-top club-art-posted\"><span>")
                    .append(posting.getType().equals("hangout") ? "HANGOUT" : (posting.getTopics().isEmpty() ? "CLUB MEETING" : HtmlLayout.escape(posting.getTopics().get(0).toUpperCase())))
                    .append("</span><b>✳</b></div><div class=\"club-card-body\"><h3>")
                    .append(HtmlLayout.escape(posting.getTitle())).append("</h3><p>");
            if (!posting.getTopics().isEmpty()) content.append("Topics: ").append(HtmlLayout.escape(String.join(", ", posting.getTopics()))).append(". ");
            content.append("</p><div class=\"club-meta\"><span>◷ ").append(HtmlLayout.escape(posting.getTimeLabel()))
                    .append("</span><span>▦ ").append(HtmlLayout.escape(posting.getLocation())).append("</span><span class=\"meeting-access ")
                    .append(posting.isOpen() ? "open\">Open" : "closed\">Closed").append("</span><span class=\"meeting-kind\">")
                    .append(posting.getType().equals("hangout") ? "Hangout" : "Club").append("</span></div></div></article>");
        }
        content.append("</section>")
                .append("<dialog class=\"post-dialog\" id=\"post-dialog\" aria-labelledby=\"post-dialog-title\"><div class=\"post-dialog-heading\"><div><p class=\"eyebrow\">CLUB BULLETIN</p><h2 id=\"post-dialog-title\">Create a meeting post</h2></div><button class=\"dialog-close\" type=\"button\" aria-label=\"Close post window\" data-close-post>×</button></div>")
                .append("<form method=\"post\" action=\"/clubs\" class=\"post-dialog-form\"><label for=\"post-title\">Post title</label><input id=\"post-title\" name=\"title\" required maxlength=\"60\" placeholder=\"e.g. Campus Garden Crew\"><label for=\"post-time\">Meeting time</label><input id=\"post-time\" name=\"time\" type=\"time\" required><label for=\"post-location\">Location</label><input id=\"post-location\" name=\"location\" required maxlength=\"80\" placeholder=\"Building and room\">")
                .append("<fieldset class=\"post-topic-fieldset\"><legend>Topics <span>Select all that apply</span></legend><div class=\"post-topic-options\">");
        String[][] topics = {{"arts", "Arts"}, {"outdoors", "Outdoors"}, {"fitness", "Fitness"}, {"food", "Food"}, {"music", "Music"}, {"technology", "Technology"}, {"service", "Service"}, {"gaming", "Gaming"}, {"career", "Career"}};
        for (String[] topic : topics) {
            content.append("<label class=\"post-topic-choice\"><input type=\"checkbox\" name=\"topic\" value=\"")
                    .append(topic[0]).append("\"><span>").append(topic[1]).append("</span></label>");
        }
        content.append("</div></fieldset><fieldset class=\"post-access-fieldset\"><legend>Meeting access</legend><div class=\"post-access-options\"><label><input type=\"radio\" name=\"access\" value=\"open\" checked><span>Open</span></label><label><input type=\"radio\" name=\"access\" value=\"closed\"><span>Closed</span></label></div></fieldset><fieldset class=\"post-access-fieldset\"><legend>Post type</legend><div class=\"post-access-options\"><label><input type=\"radio\" name=\"type\" value=\"club\" checked><span>Club</span></label><label><input type=\"radio\" name=\"type\" value=\"hangout\"><span>Hangout</span></label></div></fieldset><button class=\"button button-dark confirm-post-button\" type=\"submit\">Confirm posting <span aria-hidden=\"true\">↗</span></button></form></dialog>")
                .append("<script>(function(){const dialog=document.getElementById('post-dialog');document.querySelector('[data-open-post]').addEventListener('click',()=>dialog.showModal());document.querySelector('[data-close-post]').addEventListener('click',()=>dialog.close());dialog.addEventListener('click',event=>{if(event.target===dialog)dialog.close();});const topic=document.getElementById('filter-topic'),access=document.getElementById('filter-access'),kind=document.getElementById('filter-kind'),cards=Array.from(document.querySelectorAll('.club-card')),results=document.getElementById('filter-results');function filterCards(){let visible=0;cards.forEach(card=>{const topics=(card.dataset.topics||'').split(',');const show=(topic.value==='all'||topics.includes(topic.value))&&(access.value==='all'||card.dataset.access===access.value)&&(kind.value==='all'||card.dataset.kind===kind.value);card.hidden=!show;if(show)visible++;});results.textContent=visible+' '+(visible===1?'meeting':'meetings');} [topic,access,kind].forEach(filter=>filter.addEventListener('change',filterCards));filterCards();");
        if (reopenDialog) content.append("dialog.showModal();");
        if (!popupError.isEmpty()) content.append("window.alert('").append(popupError).append("');");
        content.append("})();</script>");
        return HtmlLayout.page("Club bulletin", "clubs", "clubs.css", content.toString(), profile);
    }
}
