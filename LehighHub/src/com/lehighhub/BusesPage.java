package com.lehighhub;

import java.util.List;
import java.util.Locale;

/** Route board populated from the supplied transit routes, stop catalog, and sequences. */
public class BusesPage {
    private static final List<Stop> LOOP_STOPS = List.of(
            stop("S01", "Whitaker Lab", "Asa Packer", 40.6062, -75.3785, true, "Loop"),
            stop("S02", "Williams Hall", "Asa Packer", 40.6069, -75.3770, true, "Loop"),
            stop("S03", "Drown Hall", "Asa Packer", 40.6074, -75.3781, false, "Loop"),
            stop("S04", "Taylor College", "Asa Packer", 40.6061, -75.3794, false, "Loop"),
            stop("S05", "Alumni Memorial Building", "Asa Packer", 40.6081, -75.3778, true, "Loop"),
            stop("S06", "S.T.E.P.S.", "Asa Packer", 40.6090, -75.3762, true, "Loop"),
            stop("S07", "Farrington Square", "Asa Packer", 40.6098, -75.3751, true, "Loop"));
    private static final Stop JORDAN = stop("S08", "Jordan Hall", "Mountaintop", 40.5985, -75.3621, false, "Southbound");
    private static final Stop BUILDING_C = stop("S09", "Building C", "Mountaintop", 40.5971, -75.3635, false, "Southbound");
    private static final Stop IACOCCA_NB = stop("S10", "Iacocca Hall NB", "Mountaintop", 40.5958, -75.3610, true, "Northbound");
    private static final Stop IACOCCA_SB = stop("S11", "Iacocca Hall SB", "Mountaintop", 40.5957, -75.3611, true, "Southbound");
    private static final Stop ATLSS = stop("S12", "ATLSS / Imbt Lab", "Mountaintop", 40.5960, -75.3648, false, "Northbound");
    private static final Stop CUNDEY = stop("S13", "Cundey Varsity House", "Goodman", 40.5841, -75.3562, true, "Northbound");
    private static final Stop GOODMAN = stop("S14", "Goodman Campus Circle", "Goodman", 40.5815, -75.3551, true, "Northbound");
    private static final Stop SAUCON = stop("S15", "Saucon Village", "Saucon Valley", 40.5782, -75.3491, true, "Loop");
    private static final List<Stop> CONNECTOR_STOPS = List.of(
            withDirection(LOOP_STOPS.get(0), "Loop"), withDirection(LOOP_STOPS.get(1), "Loop"),
            withDirection(LOOP_STOPS.get(2), "Loop"), withDirection(LOOP_STOPS.get(3), "Loop"),
            withDirection(JORDAN, "Loop"), withDirection(IACOCCA_NB, "Northbound"),
            withDirection(GOODMAN, "Loop"), withDirection(SAUCON, "Loop"));
    private static final List<Route> ROUTES = List.of(
            new Route("R01", "Campus Connector", "CC", "Mon-Fri", "07:30", "00:00", 30,
                    "Continuous loop between all four major campuses and Saucon Village.", CONNECTOR_STOPS),
            new Route("R02", "Campus Connector Weekend", "CC", "Sat-Sun", "10:00", "00:00", 30,
                    "Weekend loop serving all campuses and Saucon Village.", CONNECTOR_STOPS),
            new Route("R03", "Mountaintop Connector", "MC", "Mon-Fri", "06:45", "20:15", 15,
                    "High-frequency link between Asa Packer and Mountaintop campus.", List.of(
                            withDirection(LOOP_STOPS.get(0), "Northbound"), withDirection(LOOP_STOPS.get(1), "Northbound"),
                            withDirection(LOOP_STOPS.get(2), "Northbound"), withDirection(LOOP_STOPS.get(3), "Northbound"), JORDAN,
                            BUILDING_C, IACOCCA_SB, ATLSS)),
            new Route("R04", "Goodman Connector", "GC", "Mon-Fri", "06:50", "20:20", 30,
                    "Connects Asa Packer and Goodman campus athletic facilities.", List.of(
                            withDirection(GOODMAN, "Northbound"), withDirection(CUNDEY, "Northbound"),
                            withDirection(LOOP_STOPS.get(0), "Northbound"), withDirection(LOOP_STOPS.get(4), "Southbound"),
                            withDirection(LOOP_STOPS.get(5), "Southbound"), withDirection(LOOP_STOPS.get(6), "Southbound"))),
            new Route("R05", "Packer Express", "PE", "Mon-Fri", "06:30", "02:20", 10,
                    "Exclusively serves the academic and housing loops on Asa Packer campus.", LOOP_STOPS),
            new Route("R06", "Packer Express Weekend", "PE", "Sat-Sun", "10:00", "02:20", 15,
                    "Weekend academic and housing loop on Asa Packer campus.", LOOP_STOPS));

    public static String render(String message) {
        return render(message, null, "R01");
    }

    public static String render(String message, PageHandler.StudentProfile profile) {
        return render(message, profile, "R01");
    }

    public static String render(String message, PageHandler.StudentProfile profile, String requestedRouteId) {
        Route selected = findRoute(requestedRouteId);
        StringBuilder content = new StringBuilder();
        content.append("<section class=\"page-hero buses-hero\"><div><p class=\"eyebrow\">GETTING AROUND / BUS BOARD</p><h1>Rethink the ride.<br><em>Find your route.</em></h1><p>Check which routes are operating now, follow their stop order, or view stops by their supplied coordinates.</p></div></section>")
                .append("<div class=\"sample-banner\"><span class=\"sample-dot\"></span><strong>ROUTE SCHEDULES</strong><span>Active status uses your device’s local day and time. Frequencies are not live arrival times.</span></div>")
                .append(HtmlLayout.notice(message))
                .append("<section class=\"bus-board\"><div class=\"board-top\"><div><p class=\"eyebrow\">PLAN YOUR RIDE</p><h2>Routes near you</h2></div><div class=\"route-controls\"><label class=\"active-filter\"><input id=\"active-only\" type=\"checkbox\" checked><span>Active routes only</span></label><label class=\"route-select\" for=\"route\">Route<select id=\"route\" name=\"route\">");
        for (Route route : ROUTES) {
            content.append("<option value=\"").append(route.id).append("\" data-days=\"").append(route.operatingDays)
                    .append("\" data-start=\"").append(route.startTime).append("\" data-end=\"").append(route.endTime).append("\"")
                    .append(route.id.equals(selected.id) ? " selected" : "").append(">")
                    .append(HtmlLayout.escape(route.name)).append("</option>");
        }
        content.append("</select></label></div></div><p class=\"active-summary\" id=\"active-summary\" aria-live=\"polite\">Checking local route schedules…</p><div class=\"route-panels\">");
        for (Route route : ROUTES) content.append(routePanel(route, selected));
        content.append("</div><p class=\"schedule-note\">Route availability and frequency are based on the supplied schedule. Exact arrival times and live vehicle locations are not included.</p></section>")
                .append("<section class=\"bus-tip\"><span>↗</span><p><strong>Plan a little breathing room.</strong> A base frequency does not guarantee an arrival time. Confirm current transportation updates before heading out.</p></section>")
                .append(scheduleScript());
        return HtmlLayout.page("Bus routes", "buses", "buses.css", content.toString(), profile);
    }

    private static String routePanel(Route route, Route selected) {
        StringBuilder html = new StringBuilder("<article class=\"route-panel\" data-route-id=\"").append(route.id)
                .append("\" data-loop=\"").append(isLoopRoute(route) ? "true" : "false").append("\"")
                .append(route.id.equals(selected.id) ? "" : " hidden").append("><div class=\"bus-route-name\"><span class=\"route-badge\">")
                .append(route.code).append("</span><div><h3>").append(HtmlLayout.escape(route.name)).append("</h3><p>")
                .append(HtmlLayout.escape(route.description)).append("</p></div><span class=\"route-status\" data-status-for=\"").append(route.id).append("\">Checking…</span></div>")
                .append("<div class=\"route-facts\"><span><b>Days</b> ").append(route.operatingDays).append("</span><span><b>Hours</b> ")
                .append(route.startTime).append("–").append(route.endTime).append(route.endTime.compareTo(route.startTime) < 0 ? " next day" : "")
                .append("</span><span><b>Frequency</b> Every ").append(route.frequencyMinutes).append(" min</span><span><b>Stops</b> ").append(route.stops.size()).append("</span></div>")
                .append("<div class=\"stop-view-toggle\" role=\"group\" aria-label=\"Route view\"><button type=\"button\" class=\"view-button active\" data-view=\"map\" aria-pressed=\"true\">Map</button><button type=\"button\" class=\"view-button\" data-view=\"list\" aria-pressed=\"false\">Stops</button></div>")
                .append("<div class=\"stop-list-view\" hidden><ol class=\"stop-list\">");
        for (int i = 0; i < route.stops.size(); i++) {
            Stop stop = route.stops.get(i);
            html.append("<li class=\"stop-row\" data-stop-id=\"").append(stop.id).append("\" data-latitude=\"")
                    .append(String.format(Locale.ROOT, "%.6f", stop.latitude)).append("\" data-longitude=\"")
                    .append(String.format(Locale.ROOT, "%.6f", stop.longitude)).append("\"><span class=\"stop-marker\">").append(String.format(Locale.ROOT, "%02d", i + 1))
                    .append("</span><div><b>").append(HtmlLayout.escape(stop.name)).append("</b><small>").append(HtmlLayout.escape(stop.campus))
                    .append(" · ID ").append(stop.id).append(" · ").append(HtmlLayout.escape(stop.direction)).append(stop.shelter ? " · Shelter" : " · No shelter")
                    .append("</small></div><span class=\"stop-coordinates\">").append(String.format(Locale.ROOT, "%.4f, %.4f", stop.latitude, stop.longitude)).append("</span></li>");
        }
        html.append("</ol></div><div class=\"map-view\"><p class=\"map-caption\">Drag to pan, use the controls to zoom, or select a numbered pin for stop details. The line follows stop order; it is not a live bus path.</p><div class=\"map-canvas\" id=\"map-")
                .append(route.id).append("\" role=\"region\" aria-label=\"Interactive stop map for ").append(HtmlLayout.escape(route.name))
                .append("\"><p class=\"map-loading\">Loading map…</p></div><a class=\"google-maps-link\" href=\"")
                .append(googleMapsArea(route)).append("\" target=\"_blank\" rel=\"noopener noreferrer\">Open this area in Google Maps <span aria-hidden=\"true\">↗</span></a><span class=\"map-link-note\">Google Maps opens separately; numbered stop pins stay on this map.</span>");
        return html.append("</div><div class=\"board-foot\"><span>Every ").append(route.frequencyMinutes)
                .append(" minutes during scheduled hours</span><span>See the route’s current operating status above <b>✳</b></span></div></article>").toString();
    }

    private static String scheduleScript() {
        return """
                <script>
                (function(){
                  const routeSelect=document.getElementById('route');
                  const activeOnly=document.getElementById('active-only');
                  const activeSummary=document.getElementById('active-summary');
                  const panels=[...document.querySelectorAll('.route-panel')];
                  let leafletPromise;
                  const dayMatches=(schedule,day)=>schedule==='Mon-Fri'?day>=1&&day<=5:schedule==='Sat-Sun'?day===0||day===6:false;
                  const minutes=time=>{const [hour,minute]=time.split(':').map(Number);return hour*60+minute;};
                  function operating(option,now){
                    const day=now.getDay(),minute=now.getHours()*60+now.getMinutes();
                    const start=minutes(option.dataset.start),end=minutes(option.dataset.end);
                    if(end<start){
                      const yesterday=new Date(now);yesterday.setDate(yesterday.getDate()-1);
                      return (dayMatches(option.dataset.days,day)&&minute>=start)||(dayMatches(option.dataset.days,yesterday.getDay())&&minute<end);
                    }
                    return dayMatches(option.dataset.days,day)&&minute>=start&&minute<end;
                  }
                  function update(){
                    const now=new Date(),options=[...routeSelect.options];
                    const active=options.filter(option=>operating(option,now));
                    options.forEach(option=>{
                      option.hidden=activeOnly.checked&&!operating(option,now)&&option.value!==routeSelect.value;
                      option.textContent=option.textContent.replace(/ · (Active now|Not operating now)$/,'')+(operating(option,now)?' · Active now':' · Not operating now');
                    });
                    if(activeOnly.checked&&active.length&& !operating(routeSelect.selectedOptions[0],now))routeSelect.value=active[0].value;
                    panels.forEach(panel=>{
                      panel.hidden=panel.dataset.routeId!==routeSelect.value;
                      if(!panel.hidden&&!panel.querySelector('.map-view').hidden){
                        const mapContainer=panel.querySelector('.map-canvas');
                        if(mapContainer.dataset.initialized!=='true')initializeMap(panel).catch(()=>{});
                      }
                    });
                    panels.forEach(panel=>{
                      const status=panel.querySelector('.route-status'),option=options.find(item=>item.value===panel.dataset.routeId),isActive=operating(option,now);
                      status.textContent=isActive?'Operating now':'Not operating now';status.classList.toggle('is-active',isActive);
                    });
                    const localTime=now.toLocaleTimeString([], {hour:'numeric',minute:'2-digit'});
                    activeSummary.textContent=active.length?active.length+' route'+(active.length===1?'':'s')+' operating now · '+localTime+' local time.': 'No routes are operating now · '+localTime+' local time. Turn off “Active routes only” to browse all schedules.';
                  }
                  function loadLeaflet(){
                    if(window.L)return Promise.resolve();
                    if(leafletPromise)return leafletPromise;
                    leafletPromise=new Promise((resolve,reject)=>{
                      const css=document.createElement('link');css.rel='stylesheet';css.href='https://unpkg.com/leaflet@1.9.4/dist/leaflet.css';
                      css.integrity='sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY=';css.crossOrigin='';
                      const fail=()=>{leafletPromise=null;reject(new Error('Map library failed to load'));};
                      css.onerror=fail;css.onload=()=>{
                        const script=document.createElement('script');script.src='https://unpkg.com/leaflet@1.9.4/dist/leaflet.js';
                        script.integrity='sha256-20nQCchB9co0qIjJZRGuk2/Z9VM+kNiyxNV1lvTlZBo=';script.crossOrigin='';
                        script.onload=resolve;script.onerror=fail;document.head.appendChild(script);
                      };
                      document.head.appendChild(css);
                    });
                    return leafletPromise;
                  }
                  function escapeHtml(value){return value.replace(/[&<>"']/g,char=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[char]));}
                  function initializeMap(panel){
                    const container=panel.querySelector('.map-canvas');
                    if(container.dataset.initialized==='true'){
                      setTimeout(()=>container._routeMap.invalidateSize(),100);return Promise.resolve();
                    }
                    return loadLeaflet().then(()=>{
                      container.replaceChildren();
                      const stops=[...panel.querySelectorAll('.stop-row')].map((row,index)=>({
                        id:row.dataset.stopId,latitude:Number(row.dataset.latitude),longitude:Number(row.dataset.longitude),
                        name:row.querySelector('b').textContent,details:row.querySelector('small').textContent,sequence:index+1
                      }));
                      const map=L.map(container,{scrollWheelZoom:false});container._routeMap=map;
                      L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png',{maxZoom:19,attribution:'&copy; <a href="https://www.openstreetmap.org/copyright" target="_blank" rel="noopener noreferrer">OpenStreetMap</a> contributors'}).addTo(map);
                      const coordinates=stops.map(stop=>[stop.latitude,stop.longitude]);
                      const lineCoordinates=panel.dataset.loop?coordinates.concat([coordinates[0]]):coordinates;
                      L.polyline(lineCoordinates,{color:'#79583a',weight:4,opacity:.82,dashArray:'7 7'}).addTo(map);
                      stops.forEach(stop=>{
                        const icon=L.divIcon({className:'lehigh-map-pin-wrap',html:'<span class="lehigh-map-pin"><span>'+stop.sequence+'</span></span>',iconSize:[30,36],iconAnchor:[15,34]});
                        const googleStop='https://www.google.com/maps/search/?api=1&query='+stop.latitude.toFixed(6)+'%2C'+stop.longitude.toFixed(6);
                        L.marker([stop.latitude,stop.longitude],{icon,title:stop.name,alt:'Stop '+stop.sequence+': '+stop.name})
                          .addTo(map).bindPopup('<strong>'+escapeHtml(stop.name)+'</strong><br>'+escapeHtml(stop.details)+'<br><small>'+stop.latitude.toFixed(4)+', '+stop.longitude.toFixed(4)+'</small><br><a target="_blank" rel="noopener noreferrer" href="'+googleStop+'">Open this stop in Google Maps</a>');
                      });
                      map.fitBounds(L.latLngBounds(coordinates).pad(.15));container.dataset.initialized='true';
                      setTimeout(()=>map.invalidateSize(),100);
                    }).catch(()=>{container.innerHTML='<p class="map-error">The map could not load. Check your internet connection and try again.</p>';throw new Error('Map could not be displayed');});
                  }
                  routeSelect.addEventListener('change',update);
                  activeOnly.addEventListener('change',update);
                  document.querySelectorAll('.route-panel').forEach(panel=>{
                    panel.querySelectorAll('.view-button').forEach(button=>button.addEventListener('click',()=>{
                      const map=button.dataset.view==='map';
                      panel.querySelector('.stop-list-view').hidden=map;
                      panel.querySelector('.map-view').hidden=!map;
                      panel.querySelectorAll('.view-button').forEach(view=>{const selected=view===button;view.classList.toggle('active',selected);view.setAttribute('aria-pressed',String(selected));});
                      if(map)initializeMap(panel).catch(()=>{});
                    }));
                  });
                  update();setInterval(update,60000);
                })();
                </script>
                """;
    }

    public static String routeName(String routeId) {
        return findRoute(routeId).name;
    }

    private static Route findRoute(String routeId) {
        if (routeId != null) {
            for (Route route : ROUTES) {
                if (route.id.equalsIgnoreCase(routeId.trim()) || route.code.equalsIgnoreCase(routeId.trim())) return route;
            }
        }
        return ROUTES.get(0);
    }

    private static Stop stop(String id, String name, String campus, double latitude, double longitude, boolean shelter, String direction) {
        return new Stop(id, name, campus, latitude, longitude, shelter, direction);
    }

    private static Stop withDirection(Stop stop, String direction) {
        return new Stop(stop.id, stop.name, stop.campus, stop.latitude, stop.longitude, stop.shelter, direction);
    }

    private static boolean isLoopRoute(Route route) {
        return route.id.equals("R01") || route.id.equals("R02") || route.id.equals("R05") || route.id.equals("R06");
    }

    private static String googleMapsArea(Route route) {
        double minLatitude = Double.POSITIVE_INFINITY, maxLatitude = Double.NEGATIVE_INFINITY;
        double minLongitude = Double.POSITIVE_INFINITY, maxLongitude = Double.NEGATIVE_INFINITY;
        for (Stop stop : route.stops) {
            minLatitude = Math.min(minLatitude, stop.latitude);
            maxLatitude = Math.max(maxLatitude, stop.latitude);
            minLongitude = Math.min(minLongitude, stop.longitude);
            maxLongitude = Math.max(maxLongitude, stop.longitude);
        }
        return String.format(Locale.ROOT, "https://www.google.com/maps/@?api=1&map_action=map&center=%.6f%%2C%.6f&zoom=14",
                (minLatitude + maxLatitude) / 2, (minLongitude + maxLongitude) / 2);
    }

    private static final class Route {
        private final String id;
        private final String name;
        private final String code;
        private final String operatingDays;
        private final String startTime;
        private final String endTime;
        private final int frequencyMinutes;
        private final String description;
        private final List<Stop> stops;

        private Route(String id, String name, String code, String operatingDays, String startTime, String endTime,
                      int frequencyMinutes, String description, List<Stop> stops) {
            this.id = id;
            this.name = name;
            this.code = code;
            this.operatingDays = operatingDays;
            this.startTime = startTime;
            this.endTime = endTime;
            this.frequencyMinutes = frequencyMinutes;
            this.description = description;
            this.stops = stops;
        }
    }

    private static final class Stop {
        private final String id;
        private final String name;
        private final String campus;
        private final double latitude;
        private final double longitude;
        private final boolean shelter;
        private final String direction;

        private Stop(String id, String name, String campus, double latitude, double longitude, boolean shelter, String direction) {
            this.id = id;
            this.name = name;
            this.campus = campus;
            this.latitude = latitude;
            this.longitude = longitude;
            this.shelter = shelter;
            this.direction = direction;
        }
    }
}
