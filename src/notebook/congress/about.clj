(ns notebook.congress.about)

(def state-abbrvs
  {"WI" "Wisconsin",
   "SC" "South Carolina",
   "MN" "Minnesota",
   "NV" "Nevada",
   "NM" "New Mexico",
   "NE" "Nebraska",
   "AK" "Alaska",
   "NH" "New Hampshire",
   "ME" "Maine",
   "NY" "New York",
   "TN" "Tennessee",
   "FL" "Florida",
   "IA" "Iowa",
   "GA" "Georgia",
   "IL" "Illinois",
   "RI" "Rhode Island",
   "VA" "Virginia",
   "MI" "Michigan",
   "PA" "Pennsylvania",
   "UT" "Utah",
   "WY" "Wyoming",
   "SD" "South Dakota",
   "MO" "Missouri",
   "KY" "Kentucky",
   "CT" "Connecticut",
   "AR" "Arkansas",
   "ID" "Idaho",
   "DC" "District of Columbia",
   "MA" "Massachusetts",
   "OK" "Oklahoma",
   "AL" "Alabama",
   "VT" "Vermont",
   "MS" "Mississippi",
   "CA" "California",
   "LA" "Louisiana",
   "DE" "Delaware",
   "WA" "Washington",
   "KS" "Kansas",
   "MD" "Maryland",
   "ND" "North Dakota",
   "TX" "Texas",
   "OR" "Oregon",
   "NC" "North Carolina",
   "AZ" "Arizona",
   "IN" "Indiana",
   "WV" "West Virginia",
   "CO" "Colorado",
   "HI" "Hawaii",
   "MT" "Montana",
   "NJ" "New Jersey",
   "OH" "Ohio"})

(def fips
  {9 ["CT" "Connecticut"]
   51 ["VA" "Virginia"]
   50 ["VT" "Vermont"]
   34 ["NJ" "New Jersey"]
   69 ["MP" "Northern Mariana Islands"]
   49 ["UT" "Utah"]
   22 ["LA" "Louisiana"]
   26 ["MI" "Michigan"]
   4 ["AZ" "Arizona"]
   8 ["CO" "Colorado"]
   28 ["MS" "Mississippi"]
   60 ["AS" "America Samoa"]
   68 ["MH" "Marshall Islands"]
   30 ["MT" "Montana"]
   21 ["KY" "Kentucky"]
   33 ["NH" "New Hampshire"]
   20 ["KS" "Kansas"]
   47 ["TN" "Tennessee"]
   19 ["IA" "Iowa"]
   17 ["IL" "Illinois"]
   25 ["MA" "Massachusetts"]
   78 ["VI" "Virgin Islands of the United States"]
   15 ["HI" "Hawaii"]
   42 ["PA" "Pennsylvania"]
   66 ["GU" "Guam"]
   44 ["RI" "Rhode Island"]
   5 ["AR" "Arkansas"]
   48 ["TX" "Texas"]
   53 ["WA" "Washington"]
   18 ["IN" "Indiana"]
   36 ["NY" "New York"]
   12 ["FL" "Florida"]
   13 ["GA" "Georgia"]
   27 ["MN" "Minnesota"]
   24 ["MD" "Maryland"]
   35 ["NM" "New Mexico"]
   6 ["CA" "California"]
   38 ["ND" "North Dakota"]
   70 ["PW" "Palau"]
   39 ["OH" "Ohio"]
   1 ["AL" "Alabama"]
   74 ["UM" "U.S. Minor Outlying Islands"]
   37 ["NC" "North Carolina"]
   46 ["SD" "South Dakota"]
   11 ["DC" "District of Columbia"]
   45 ["SC" "South Carolina"]
   56 ["WY" "Wyoming"]
   32 ["NV" "Nevada"]
   55 ["WI" "Wisconsin"]
   2 ["AK" "Alaska"]
   72 ["PR" "Puerto Rico"]
   54 ["WV" "West Virginia"]
   16 ["ID" "Idaho"]
   41 ["OR" "Oregon"]
   10 ["DE" "Delaware"]
   40 ["OK" "Oklahoma"]
   31 ["NE" "Nebraska"]
   64 ["FM" "Federated States of Micronesia"]
   23 ["ME" "Maine"]
   29 ["MO" "Missouri"]})

(def reverse-fips
  (->> fips
       (map (fn [[fips [abbrv longname]]]
              [abbrv fips]))
       (into {})))