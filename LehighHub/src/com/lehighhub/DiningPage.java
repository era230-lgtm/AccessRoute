package com.lehighhub;

/** A simple calculator based on sample dining-dollar values entered by the visitor. */
public class DiningPage {
    public static String render(String message) {
        String content = "<section class=\"page-hero dining-hero\"><div><p class=\"eyebrow\">FOOD / SPENDING PLAN</p><h1>Make your dollars<br><em>go further.</em></h1><p>A simple pace check for your dining budget. Try the calculator with sample numbers.</p></div><div class=\"dining-illustration\"><span class=\"plate plate-back\"></span><span class=\"plate plate-front\">LH</span><i class=\"spark spark-one\">✳</i><i class=\"spark spark-two\">✦</i></div></section>"
                + "<div class=\"sample-banner\"><span class=\"sample-dot\"></span><strong>SAMPLE TOOL</strong><span>Enter a pretend balance; this page is not connected to a student account.</span></div>"
                + HtmlLayout.notice(message)
                + "<section class=\"dining-layout\"><div class=\"budget-card\"><div class=\"card-heading\"><span class=\"step-number\">01</span><div><p class=\"eyebrow\">QUICK CALCULATOR</p><h2>Your daily target</h2></div></div><p class=\"card-copy\">Split a sample balance across the days you want it to last.</p><form method=\"post\" action=\"/dining\"><label for=\"balance\">Sample balance <span>USD</span></label><div class=\"input-with-prefix\"><span>$</span><input id=\"balance\" name=\"balance\" type=\"number\" min=\"0\" step=\"0.01\" placeholder=\"350.00\" required></div><label for=\"days\">Days to plan for</label><input id=\"days\" name=\"days\" type=\"number\" min=\"1\" max=\"365\" placeholder=\"30\" required><button class=\"button button-dark full-button\" type=\"submit\">Calculate my pace <span>↗</span></button></form><p class=\"fine-print\">For planning inspiration only. Actual plan terms may differ.</p></div>"
                + "<div class=\"dining-side\"><div class=\"tip-card\"><span class=\"tip-icon\">✳</span><p class=\"eyebrow\">A LITTLE RHYTHM HELPS</p><h3>Think weekly, not just daily.</h3><p>A flexible weekly target can make room for busy days, shared meals, and the occasional treat.</p><div class=\"tip-line\"><span>PLAN</span><b>+</b><span>ENJOY</span><b>+</b><span>ADJUST</span></div></div><div class=\"sample-math\"><span class=\"eyebrow\">EXAMPLE ONLY</span><div class=\"math-row\"><span>$350 sample balance</span><b>÷</b><span>14 days</span><b>=</b><strong>$25/day</strong></div></div></div></section>";
        return HtmlLayout.page("Dining budget", "dining", "dining.css", content);
    }
}
