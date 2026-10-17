package com.lehighhub;

/** Demonstration bus route board with static, clearly labeled sample times. */
public class BusesPage {
    public static String render(String message) {
        String content = "<section class=\"page-hero buses-hero\"><div><p class=\"eyebrow\">GETTING AROUND / BUS BOARD</p><h1>Hop on.<br><em>Head out.</em></h1><p>Pick a route and get oriented. The stops and times here are fictional demo data.</p></div><div class=\"route-doodle\"><span class=\"route-circle\">L</span><span class=\"route-path\"></span><span class=\"route-pin\">●</span><span class=\"route-label label-one\">CAMPUS</span><span class=\"route-label label-two\">DOWNTOWN</span></div></section>"
                + "<div class=\"sample-banner\"><span class=\"sample-dot\"></span><strong>DEMO SCHEDULE</strong><span>Not live tracking. Confirm real service through official transit channels.</span></div>"
                + HtmlLayout.notice(message)
                + "<section class=\"bus-board\"><div class=\"board-top\"><div><p class=\"eyebrow\">CHOOSE YOUR RIDE</p><h2>Sample route board</h2></div><form method=\"post\" action=\"/buses\" class=\"route-select\"><label for=\"route\">Route</label><select id=\"route\" name=\"route\"><option>Campus Loop</option><option>South Side Shuttle</option><option>Downtown Connector</option></select><button class=\"button button-dark\" type=\"submit\">Show route</button></form></div>"
                + "<div class=\"bus-route-name\"><span class=\"route-badge\">L1</span><div><h3>Campus Loop</h3><p>Sample route · circular campus connection</p></div><span class=\"sample-tag\">DEMO ONLY</span></div>"
                + "<div class=\"stop-list\"><div class=\"stop-row\"><span class=\"stop-marker\">01</span><div><b>University Center</b><small>Starting point · Main entrance</small></div><time>8:10 AM</time></div><div class=\"stop-row\"><span class=\"stop-marker\">02</span><div><b>Mountaintop Campus</b><small>Engineering buildings</small></div><time>8:22 AM</time></div><div class=\"stop-row\"><span class=\"stop-marker\">03</span><div><b>Farrington Square</b><small>Residence and dining area</small></div><time>8:34 AM</time></div><div class=\"stop-row\"><span class=\"stop-marker\">04</span><div><b>University Center</b><small>Loop complete</small></div><time>8:46 AM</time></div></div><div class=\"board-foot\"><span>Sample weekday schedule</span><span>Times shown for visual demo only <b>✳</b></span></div></section>"
                + "<section class=\"bus-tip\"><span>↗</span><p><strong>Plan a little breathing room.</strong> Walk times and service can change. Check current campus transportation updates before heading out.</p></section>";
        return HtmlLayout.page("Bus tracker", "buses", "buses.css", content);
    }
}
