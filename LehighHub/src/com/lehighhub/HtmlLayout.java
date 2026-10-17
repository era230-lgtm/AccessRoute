package com.lehighhub;

/** Shared page frame, navigation, and small HTML helpers used by every page. */
public final class HtmlLayout {
    private HtmlLayout() { }

    public static String page(String title, String activePage, String pageCss, String content) {
        return page(title, activePage, pageCss, content, null);
    }

    /** Adds the shared account controls when a visitor's in-memory demo profile exists. */
    public static String page(String title, String activePage, String pageCss, String content,
            PageHandler.StudentProfile profile) {
        StringBuilder html = new StringBuilder();
        html.append("<!doctype html><html lang=\"en\"><head>");
        html.append("<meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
        html.append("<title>").append(escape(title)).append(" · LU Link</title>");
        html.append("<link rel=\"stylesheet\" href=\"/css/shared.css\">");
        if (pageCss != null && !pageCss.isEmpty()) {
            html.append("<link rel=\"stylesheet\" href=\"/css/").append(escape(pageCss)).append("\">");
        }
        html.append("<link rel=\"stylesheet\" href=\"/css/controls.css\">");
        html.append("</head><body><a class=\"skip-link\" href=\"#main-content\">Skip to content</a>");
        html.append("<header class=\"site-header\"><div class=\"header-inner\">");
        html.append("<a class=\"brand\" href=\"/\" aria-label=\"LU Link home\"><span class=\"brand-mark\">LU</span><span><b>Link</b></span></a>");
        html.append("<nav class=\"main-nav\" aria-label=\"Main navigation\">");
        navLink(html, "/", "Home", activePage.equals("home"));
        navLink(html, "/clubs", "Clubs", activePage.equals("clubs"));
        navLink(html, "/dining", "Dining", activePage.equals("dining"));
        navLink(html, "/buses", "Buses", activePage.equals("buses"));
        navLink(html, "/resources", "Resources", activePage.equals("resources"));
        html.append("</nav><div class=\"account-actions\">");
        if (profile == null) {
            html.append("<a class=\"signup-link\" href=\"/signup\">Sign up <span aria-hidden=\"true\">↗</span></a>");
        } else {
            html.append("<a class=\"account-username\" href=\"/profile\" aria-label=\"Open profile for ")
                    .append(escape(profile.getUsername())).append("\">")
                    .append(escape(profile.getUsername())).append("</a>");
        }
        html.append("<a class=\"profile-button\" href=\"").append(profile == null ? "/signup" : "/profile")
                .append("\" aria-label=\"").append(profile == null ? "Sign up to create your profile" : "Open your profile")
                .append("\">");
        if (profile != null && profile.hasProfilePicture()) {
            html.append("<img src=\"/profile-picture\" alt=\"\">");
        } else {
            html.append("<span class=\"profile-icon\" aria-hidden=\"true\"><i></i></span>");
        }
        html.append("</a></div></div></header>");
        html.append("<main id=\"main-content\" class=\"page-wrap\">").append(content).append("</main>");
        html.append("<footer class=\"site-footer\"><div><span>LU LINK</span><span>Built for the Mountain Hawk community</span></div>");
        html.append("<p>Hackathon prototype · Sample information only · Not an official university service</p></footer>");
        html.append("<div class=\"custom-cursor\" popover=\"manual\" aria-hidden=\"true\"></div>");
        html.append("<script>(function(){if(!window.matchMedia('(hover: hover) and (pointer: fine)').matches)return;const cursor=document.querySelector('.custom-cursor');document.documentElement.classList.add('custom-cursor-enabled');if(cursor.showPopover)cursor.showPopover();window.raiseCustomCursor=function(){if(!cursor.showPopover)return;if(cursor.matches(':popover-open'))cursor.hidePopover();cursor.showPopover();};let visible=false;function move(event){cursor.style.left=event.clientX+'px';cursor.style.top=event.clientY+'px';if(!visible){cursor.classList.add('is-visible');visible=true;}const target=event.target instanceof Element?event.target:null;cursor.classList.toggle('is-ring',!!(target&&target.closest('a,button,select,input:not([type=checkbox]):not([type=radio]),textarea,input[type=checkbox],input[type=radio],label.interest-choice,.post-topic-choice,.post-access-options label,[role=button]')));}document.addEventListener('pointermove',move);document.addEventListener('pointerleave',()=>cursor.classList.remove('is-visible'));document.addEventListener('pointerenter',()=>cursor.classList.add('is-visible'));})();</script>");
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
