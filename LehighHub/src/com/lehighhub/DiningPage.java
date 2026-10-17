package com.lehighhub;

/** Dining-dollar and meal-plan pace planner. */
public class DiningPage {
    public static String render(String message) {
        return render(message, null);
    }

    public static String render(String message, PageHandler.StudentProfile profile) {
        String content = """
                <section class="page-hero dining-hero"><div><p class="eyebrow">FOOD / MEAL PLAN</p><h1>Plan your meals<br><em>with a clear pace.</em></h1><p>See how Dining Dollars, meal swipes, and weekly Meal Exchanges can work together across your plan.</p></div></section>
                NOTICE_HERE
                <section class="dining-layout">
                  <div class="budget-card">
                    <div class="card-heading"><span class="step-number">01</span><div><p class="eyebrow">YOUR PLAN</p><h2>Meal plan balances</h2></div></div>
                    <p class="card-copy">Add your current balances and the dates you are planning for. The summary balances your three meal slots across all available resources.</p>
                    <form id="dining-form" method="post" action="/dining">
                      <label for="balance">Dining Dollars <span>DD</span></label><div class="input-with-prefix"><span>$</span><input id="balance" name="balance" type="number" min="0" step="0.01" placeholder="350.00" required></div>
                      <label for="current-day">Current day</label><input id="current-day" name="currentDay" type="date" required>
                      <label for="end-date">End date</label><input id="end-date" name="endDate" type="date" required>
                      <p class="fine-print date-range-summary" id="date-range-summary">Choose both dates to see the planning window.</p>
                      <p class="date-error" id="date-error" role="alert" hidden>Choose an end date after the current day.</p>
                      <label for="swipes">Meal swipes available <span>ONE SWIPE = ONE MEAL</span></label><input id="swipes" name="swipes" type="number" min="0" step="1" placeholder="250" required>
                      <label for="exchanges">Meal Exchanges each week <span>REFRESH WEEKLY</span></label><input id="exchanges" name="exchanges" type="number" min="5" max="10" step="1" value="7" required>
                      <p class="fine-print">Each swipe or Meal Exchange covers one meal. Meal Exchanges refresh weekly; unused exchanges may not carry over.</p>
                    </form>
                  </div>
                  <div class="dining-side">
                    <section class="dining-stats" aria-live="polite">
                      <div class="stats-heading"><div><p class="eyebrow">PACE SUMMARY</p><h2>A balanced daily plan</h2></div><span class="live-indicator">LIVE</span></div>
                      <div class="primary-stat"><span>Dining Dollars budget / day</span><strong id="daily-total">—</strong><small id="daily-caption">Enter your balances to map the meal coverage</small></div>
                      <div class="stat-grid">
                        <div class="stat-cell"><span>Swipe pool</span><strong id="swipe-meals">—</strong></div>
                        <div class="stat-cell"><span>Swipe pace</span><strong id="swipe-pace">—</strong></div>
                        <div class="stat-cell"><span>Meal Exchanges / week</span><strong id="exchange-total">7</strong></div>
                        <div class="stat-cell"><span>Meals planned / day</span><strong>3</strong></div>
                      </div>
                      <div class="meal-plan">
                        <div class="meal-plan-heading"><div><p class="eyebrow">THREE MEALS, THREE RESOURCES</p><h3>Suggested daily coverage</h3></div><span>Per day</span></div>
                        <div class="meal-row"><div class="meal-label"><span>Swipe meals</span></div><div class="meal-track"><span id="swipe-bar"></span></div><strong id="swipe-daily">—</strong></div>
                        <div class="meal-row"><div class="meal-label"><span>Exchange meals</span></div><div class="meal-track"><span id="exchange-bar"></span></div><strong id="exchange-daily">—</strong></div>
                        <div class="meal-row"><div class="meal-label"><span>DD paid meals</span></div><div class="meal-track"><span id="dollar-bar"></span></div><strong id="dollar-meal">—</strong></div>
                        <p class="meal-footnote" id="coverage-note">Enter balances to see how many meals each resource can cover.</p>
                      </div>
                    </section>
                  </div>
                </section>
                <script>
                (function(){
                  const form=document.getElementById('dining-form');
                  const balance=document.getElementById('balance'),currentDay=document.getElementById('current-day'),endDate=document.getElementById('end-date'),swipes=document.getElementById('swipes'),exchanges=document.getElementById('exchanges'),dateError=document.getElementById('date-error');
                  const set=(id,value)=>document.getElementById(id).textContent=value;
                  const money=value=>'$'+value.toFixed(2);
                  const localDateString=date=>date.getFullYear()+'-'+String(date.getMonth()+1).padStart(2,'0')+'-'+String(date.getDate()).padStart(2,'0');
                  const today=new Date();currentDay.value=localDateString(today);
                  const defaultEnd=new Date(today);defaultEnd.setDate(defaultEnd.getDate()+30);endDate.value=localDateString(defaultEnd);
                  function update(){
                    dateError.hidden=true;
                    if(balance.value===''||swipes.value===''||currentDay.value===''||endDate.value===''||exchanges.value===''){
                      set('date-range-summary','Choose both dates to see the planning window.');
                      set('daily-total','—');set('daily-caption','Enter your balances to map the meal coverage');set('swipe-meals','—');set('swipe-pace','—');set('exchange-total',exchanges.value||'—');set('swipe-daily','—');set('exchange-daily','—');set('dollar-meal','—');set('coverage-note','Enter balances to see how many meals each resource can cover.');
                      ['swipe-bar','exchange-bar','dollar-bar'].forEach(id=>document.getElementById(id).style.width='0%');return;
                    }
                    const d=Math.round((new Date(endDate.value)-new Date(currentDay.value))/86400000);
                    if(d<1){
                      set('date-range-summary','Planning window must be at least 1 day.');
                      dateError.hidden=false;
                      set('daily-total','—');set('daily-caption','Choose a valid planning window');set('swipe-meals','—');set('swipe-pace','—');set('exchange-total',String(exchanges.value));set('swipe-daily','—');set('exchange-daily','—');set('dollar-meal','—');set('coverage-note','Set the end date to at least one day after the current day.');
                      ['swipe-bar','exchange-bar','dollar-bar'].forEach(id=>document.getElementById(id).style.width='0%');return;
                    }
                    set('date-range-summary',d+' day'+(d===1?'':'s')+' in your planning window.');
                    const dd=Number(balance.value),swipePool=Math.floor(Number(swipes.value)),exchangeWeek=Number(exchanges.value);
                    const swipePerDay=Math.min(3,swipePool/d),exchangePerDay=Math.min(exchangeWeek/7,Math.max(0,3-swipePerDay)),dollarMeals=Math.max(0,3-swipePerDay-exchangePerDay);
                    const dailyDollars=dd/d,perPaidMeal=dollarMeals?dailyDollars/dollarMeals:0;
                    set('daily-total',money(dailyDollars));set('daily-caption','About '+money(perPaidMeal)+' per Dining Dollar paid meal');
                    set('swipe-meals',String(swipePool));set('swipe-pace',swipePerDay.toFixed(2).replace(/0+$/,'').replace(/\\.$/,'')+' / day');set('exchange-total',String(exchangeWeek));
                    set('swipe-daily',swipePerDay.toFixed(2)+' meals');set('exchange-daily',exchangePerDay.toFixed(2)+' meals');set('dollar-meal',dollarMeals.toFixed(2)+' meals · '+money(perPaidMeal));
                    const scale=3;document.getElementById('swipe-bar').style.width=(swipePerDay/scale*100)+'%';document.getElementById('exchange-bar').style.width=(exchangePerDay/scale*100)+'%';document.getElementById('dollar-bar').style.width=(dollarMeals/scale*100)+'%';
                    const swipeDays=swipePool?Math.floor(swipePool/swipePerDay):0;
                    set('coverage-note','At this pace, swipes cover about '+swipeDays+' days; '+exchangeWeek+' weekly exchanges average '+(exchangeWeek/7).toFixed(2)+' meals per day.');
                  }
                  [balance,currentDay,endDate,swipes,exchanges].forEach(field=>field.addEventListener('input',update));
                  form.addEventListener('submit',event=>event.preventDefault());update();
                })();
                </script>
                """.replace("NOTICE_HERE", HtmlLayout.notice(message));
        return HtmlLayout.page("Dining plan", "dining", "dining.css", content, profile);
    }
}
