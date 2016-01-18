(ns notebook.zs
  (:require [clojure.string :as string]
            [puget.printer :refer [cprint]]
            [notebook.gdoc :as gdoc]
            [hiccup.page :refer [html5]]
            [notebook.html :as html]
            [notebook.hiero :refer [<-inline as-txt]]))

;(def essay "14ANhea-S4bz9GOOOPIwCqfhKyGFpWQ2oQHtW1K-kHuQ")
;(def map "http://www.bigmapblog.com/2013/birdseye-view-of-los-angeles/")

; Notable Events of June 25
; Recording of Sundays at the Village Vanguard
; Birthdays
; Deathdays
; International year of the pulse (http://www.fao.org/pulses-2016/en/)

(def essay
  ["___"
   "### Past to Present"
   "In the spring of 2007, in the waning days
    of Columbia’s academic year, elated moods
    filled freshman dorms: final exams were almost
    over; it was a Friday. Jeff Schwartz had been
    invited to a party on the 11th floor of Carman,
    and he asked me if I wanted to join. _Eh_.
    Come on, man. _Ok_. We rode up to 11 with
    a crowd of partygoers, then walked down the
    hall, where sat a girl. The volume of chatter
    seemed to decrease around me, and I was aware
    of only three things: my breathing (increasing),
    her beauty (arresting), and a question: would
    she be going to this party? (Could I be so
    lucky?)"
   "She was going, reluctantly. The party turned
    out to be in her dorm room, though she had
    not been aware of any party planning. Oh well.
    By this time the party had started, I was
    busy auditioning and re-auditioning opening
    lines in my head. Finally I decided on one,
    and got my chance. I stepped up to the free-throw
    line."
   " “Are you in 21st-century art history?”"
   " “No.”"
   "The crowd went silent. I knew she _had_ been
    in the class at some point. She was in my
    discussion section, three months ago. I learned
    nothing about art in that class. I busied
    myself with snuck glances. A week after that,
    even though the TA leading the section was
    terrible, I returned, only to see if she’d
    be there. She wasn’t. And a third time. No
    luck."
   "“Oh I think I saw you in my discussion section
   — did you drop that class?”"
   " “Yeah.”"
   "I remember almost nothing else about that
    conversation. But ever since that first question,
    asked almost nine years ago, Diana and I have
    never really stopped talking."
   "At first we talked about music, then we talked
    about everything else; time flew. A year went
    by. Would I want to come visit her extended
    family in China over the summer? Yes! So we
    did (and I’m forever grateful for the chance
    to meet her grey-eyed grandfather.) Two more
    years, then we were sitting next to each other
    at graduation, then we were in Paris. Diana
    moved to the west coast first, but six months
    of long distance passed quickly. Next thing
    I knew, I had a one-way ticket, then a California
    driver’s license, then an abiding love for
    the smell of eucalyptus lining Junipero Serra
    Drive."
   "The Bay Area was home for 3 years. We continued
    to talk about everything: food, our life together,
    grad school for Diana. Probably she’d go to
    UC Berkeley, we loved it in the Bay. But she
    might as well apply to Harvard — why not?
    — though we would _never_ move to Boston.
    Unthinkable. Just curious if she’d get in."
   "On the cross-country drive to Boston, we
    drove America for the first time, and she
    began to meet my extended family, scattered
    everywhere in the 50 states. Two years after
    that (after meeting all new friends, and surviving
    the worst winter in the history of New England),
    we planned a cross-country drive to Los Angeles,
    and we drove America again. In New Orleans
    we dined, in old-world style, on old school
    food: turtle soup, a Sazerac. It was June
    25, 2015, twelve months to the day before
    our marriage, and we celebrated our -1 year
    anniversary in an astounding place that —
    even though neither of us had any connection
    to it — still felt oddly like home."
   "In fact, now that we’ve lived so many places
    together, nothing really defines “home” for
    me more than Diana’s presence. (If we wake
    up in the same bed, isn’t that home?)"
   "We are descended from peripatetic people.
    50 years ago, when my own mother was competing
    in a duckpin bowling league in Massachusetts,
    8000 miles away, in Shantou, Diana’s father
    was tying live beetles to strings. (An inexpensive
    fan, necessary in Shantou’s oppressive humidity.)
    30 years ago, my father (a Yankee) was playing
    banjo in northern Virginia, and Diana’s mother
    was teaching English in Zhanjiang. Two families,
    always moving, never quite settled. Virginia,
    Maryland, Florida, Ohio; Guangdong, Oregon,
    Maryland, New Jersey."
   "Then two college students crossed paths in
   New York. A link formed in the universe."
   "Of course, now that Alfonso — our timid terrier
    — has joined the family, maybe the definition
    of home is starting to change a little. Just
    yesterday, over clear Rhode Island clam chowder
    (in West Hollywood), we did some quick mental
    math. If Alfie leads a healthy life, we’ll
    be in our early 40s when he’s an old man.
    Who knows how big our family will be then?"
   "Uh oh. Diana seems to have caught me teary-eyed.
    What am I doing? Nothing, just writing. What
    is she doing? Researching vacuums. There’s
    a lot of dog hair on the ground, which is
    a new problem, though an excellent one to
    have."])

(def deets
  ["___"
   "### Future"
   "So for now, Los Angeles is home (the background
    of this site is the upward view from our driveway),
    and we’ve decided to get married. Yes, we’ve
    been engaged for over a year now, and we’ve
    been cohabitating for over 5 years, and dating
    for almost 9. But there’s no time like the
    present!"
   "So, here are the deets:"
   "- _The day_ —  June 25, 2016"
   "- _The venue_ — The Carondelet House, Los\nAngeles, CA"
   "On the 24th, Brit & Kate will be hosting
    a Rehearsal Dinner at the Fig House in Los
    Angeles. Given that over 90% of all you wedding
    guests are non-Angelenos, you’re also all
    invited to that soiree — a more casual affair,
    complete with an accordion & clarinet duo,
    arranged in honor of the music once heard
    at Brit & Kate’s own wedding (not their idea;
    Rob’s idea)."])

(html/refresh
  "zhengstenson.com"
  "Zheng Stenson Wedding"
  {:styles ["klim" "style"]
   :scripts ["hyphenator"]}
  [:div#container
   [:div#text-outer
    [:div#text-inner
     [:h1 "Diana &amp; Rob"]
     [:div#dh
      [:a {:href "https://en.wikipedia.org/wiki/Double_Happiness_(calligraphy)"
           :target "_blank"}
       [:img {:src "dh.png"}]]]
     [:img.sz {:src "sz-512.png" :width 32}]
     [:div.content
      (<-inline essay)]]]])