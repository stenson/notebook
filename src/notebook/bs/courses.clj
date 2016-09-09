(ns notebook.bs.courses)

(def courses
  [(list {:course "TPC at Tampa Bay",
          :location "Tampa, FL",
          :ll (-82.45843 27.94752),
          :coauthor "w/Bobby Weed",
          :year "1990"}
         {:course "Golf Club of Jacksonville",
          :location "Jacksonville, FL",
          :ll (-81.65565 30.33218),
          :coauthor "w/Bobby Weed",
          :year "1990"}
         {:course "Golf Club of Miami(Exec.)",
          :location "Miami, FL",
          :ll (-80.13005 25.79065),
          :coauthor "w/Bobby Weed",
          :year "1990"}
         {:course "TPC at Summerlin",
          :location "Las Vegas, NV",
          :ll (-115.13722 36.17497),
          :coauthor "w/Bobby Weed",
          :year "1991"}
         {:course "TPC at River Highlands",
          :location "Cromwell, CT",
          :ll (-72.64537 41.5951),
          :coauthor "w/Bobby Weed",
          :year "1991"}
         {:course "Sand River Golf &CC",
          :location "Shenzhen, China",
          :ll (114.0683 22.54554),
          :coauthor "w/Gary Player",
          :year "1994"}
         {:course "Great Lake Golf &CC",
          :location "Rayong, Thailand",
          :ll (101.25798 12.68095),
          :coauthor "w/Nick Faldo",
          :year "1995"}
         {:course "Wuhan International",
          :location "Wuhan, China",
          :ll (114.26667 30.58333),
          :coauthor "w/Nick Faldo",
          :year "1996"}
         {:course "Jababeka Golf & CC",
          :location "Jakarta, Indonesia",
          :ll (106.84513 -6.21462),
          :coauthor "w/Nick Faldo",
          :year "1996"}
         {:course "Ocean Dunes Golf Resort",
          :location "Phan Tiet, Vietnam",
          :ll (108.10208 10.92889),
          :coauthor "w/Nick Faldo",
          :year "1996"}
         {:course "Bintan Lagoon",
          :location "Bintan, Indonesia",
          :ll (104.61944 0.95),
          :coauthor "w/Ian Baker-Finch",
          :year "1997"}
         {:course "Riviera Golf Resort",
          :location "Manila, Philippines",
          :ll (120.9822 14.6042),
          :coauthor "w/Bernhard Langer",
          :year "1997"}
         {:course "Riviera Golf Resort",
          :location "Manila, Philippines",
          :ll (120.9822 14.6042),
          :coauthor "w/Freddie Couples",
          :year "1997"}
         {:course "Royal Orchid CC",
          :location "Shunde, China",
          :ll (113.2503 22.84067),
          :coauthor "w/Nick Faldo",
          :year "1998"}
         {:course "Eagle Ridge Golf &CC",
          :location "Manila, Philippines",
          :ll (120.9822 14.6042),
          :coauthor "w/Nick Faldo",
          :year "1999"}
         {:course "Wack Wack West",
          :location "Manila, Philippines",
          :ll (120.9822 14.6042),
          :year "1999"}
         {:course "Pantai Lagenda Golf Resort",
          :location "Kuantan, Malaysia",
          :ll (103.326 3.8077),
          :coauthor "w/Colin Montgomerie",
          :year "1999"}
         {:course "Eagle Ridge Golf &CC",
          :location "Manila, Philippines",
          :ll (120.9822 14.6042),
          :coauthor "w/Isao Aoki",
          :year "2000"}
         {:course "Hang Gang Golf Club",
          :location "Shenzhen, China",
          :ll (114.0683 22.54554),
          :coauthor "w/Ian Woosnam",
          :year "2001"}
         {:course "Grandview Golf Resort",
          :location "Muskoka, Canada",
          :ll (-79.30713 44.97732),
          :coauthor "w/Mark O’Meara",
          :year "2002"}
         {:course "Zhuhai Golden Gulf Golf",
          :location "Zhuhai, China",
          :ll (113.56778 22.27694),
          :coauthor "w/Colin Montgomerie",
          :year "2003"}
         {:course "Meishi Mayflower",
          :location "Haikou, China",
          :ll (110.34167 20.04583),
          :coauthor "w/Colin Montgomerie",
          :year "2003"}
         {:course "Raon Golf Club",
          :location "Jeju, Korea",
          :ll (),
          :coauthor "w/Colin Montgomerie",
          :year "2003"}
         {:course "Moonah Links",
          :location "Melbourne, Aust.",
          :ll (),
          :coauthor "w/Ross Perrett",
          :year "2004"}
         {:course "The Rock",
          :location "Muskoka, Canada",
          :ll (-79.30713 44.97732),
          :coauthor "w/Nick Faldo",
          :year "2004"}
         {:course "Ile Aux Cerfs",
          :location "Mauritius",
          :ll (57.58333 -20.3),
          :coauthor "w/Bernhard Langer",
          :year "2004"}
         {:course "Torquay Sands",
          :location "Torquay, Australia",
          :ll (144.32639 -38.33085),
          :coauthor "w/Stuart Appleby",
          :year "2004"}
         {:course "Hong Hua Golf Club",
          :location "Beijing, China",
          :ll (116.39723 39.9075),
          :coauthor "w/Nick Faldo",
          :year "2004"}
         {:course "Capitol Precision",
          :location "Manila, Philippines",
          :ll (120.9822 14.6042),
          :year "2004"}
         {:course "Kunming Lakeview",
          :location "Kunming, China",
          :ll (102.71833 25.03889),
          :coauthor "w/Nick Faldo",
          :year "2004"}
         {:course "Golden Elephant Golf Club",
          :location "Lang Fang, China",
          :ll (116.69472 39.50972),
          :coauthor "w/Jesper Parnevik",
          :year "2004"}
         {:course "Tuhaye Ranch",
          :location "Park City, Utah",
          :ll (-111.49797 40.64606),
          :coauthor "w/Mark O’Meara",
          :year "2005"}
         {:course "Beijing CBD",
          :location "Beijing, China",
          :ll (116.39723 39.9075),
          :year "2005"}
         {:course "Golden Elephant Golf Club",
          :location "Lang Fang, China",
          :ll (116.69472 39.50972),
          :year "2005"}
         {:course "East Sea Golf Club",
          :location "Shanghai, China",
          :ll (121.45806 31.22222),
          :coauthor "w/Colin Montgomerie",
          :year "2006"}
         {:course "Emirates Wadi Course Dubai, UAE",
          :location "w/Nick Faldo",
          :ll (),
          :year "2006"}
         {:course "West Donghai Golf Club",
          :location "Nanshan, China",
          :ll (113.92978 22.53334),
          :coauthor "w/Colin Montgomerie",
          :year "2006"}
         {:course "Tam Dao Golf Club",
          :location "Hanoi, Vietnam",
          :ll (105.84117 21.0245),
          :year "2006"}
         {:course "Natadola Golf Resort",
          :location "Natadola, Fiji",
          :ll (177.32066 -18.10788),
          :coauthor "w/Vijay Singh",
          :year "2007"}
         {:course "Regal Riviera Golf Club",
          :location "Tianjin, China",
          :ll (117.17667 39.14222),
          :coauthor "w/Vijay Singh",
          :year "2007"}
         {:course "Ma Shan Zai Golf Resort",
          :location "Nanshan, China",
          :ll (113.92978 22.53334),
          :coauthor "w/Ian Woosnam",
          :year "2008"}
         {:course "Angkor Golf Resort",
          :location "Siem Reap, Cambodia",
          :ll (103.86056 13.36179),
          :coauthor "w/Nick Faldo",
          :year "2009"}
         {:course "South Forbes Golf Club",
          :location "Manila, Philippines",
          :ll (120.9822 14.6042),
          :year "2009"}
         {:course "Montgomerie Links",
          :location "Danang, Vietnam",
          :ll (108.22083 16.06778),
          :coauthor "w/Colin Montgomerie",
          :year "2009"}
         {:course "Nanshan Longkou",
          :location "Longkou, China",
          :ll (120.50832 37.60417),
          :year "2009"}
         {:course "Topwin Golf &CC",
          :location "Beijing, China",
          :ll (116.39723 39.9075),
          :coauthor "w/Ian Woosnam",
          :year "2010"}
         {:course "Red Flag Valley – Dragon",
          :location "Dalian, China",
          :ll (121.60222 38.91222),
          :year "2010"}
         {:course "Cua Lo Golf Resort",
          :location "Vinh, Vietnam",
          :ll (105.9722 10.25369),
          :year "2010"}
         {:course "Red Flag Valley – Unicorn",
          :location "Dalian, China",
          :ll (121.60222 38.91222),
          :year "2011"}
         {:course "Imperial Springs Resort (27)",
          :location "Guangzhou, China",
          :ll (113.25 23.11667),
          :coauthor "w/Colin Montgomerie",
          :year "2011"}
         {:course "Golden Bay Golf Resort",
          :location "Tae An, Korea",
          :ll (),
          :coauthor "w/Annika Sorenstam",
          :year "2011"}
         {:course "Dragon Lake Golf Club (27)",
          :location "Guangzhou, China",
          :ll (113.25 23.11667),
          :year "2012"}
         {:course "Reyford Golf Club",
          :location "Daegu, Korea",
          :ll (),
          :coauthor "w/Vijay Singh",
          :year "2012"}
         {:course "Vinpearl Resort",
          :location "Nha Trang, Vietnam",
          :ll (109.19432 12.24507),
          :year "2012"}
         {:course "Hai Tang Bay", :location "Sanya, China", :ll (109.505 18.24306), :year "2012"}
         {:course "Bao Ting Golf Course",
          :location "Sanya, China",
          :ll (109.505 18.24306),
          :year "2012"}
         {:course "Qinhuangdao Golf Course",
          :location "Qinhuangdao, China",
          :ll (119.58833 39.93167),
          :coauthor "w/Sergio Garcia",
          :year "2013"}
         {:course "Pine Rock Golf Club",
          :location "Bei Dai He, China",
          :ll (114.49417 37.06306),
          :year "2013"}
         {:course "Tianjin Watertown (36)",
          :location "Tianjin, China",
          :ll (117.17667 39.14222),
          :year "2013"}
         {:course "Moon Bay Golf Resort", :location "Gunaglu, China", :ll (), :year "2014"}
         {:course "Akbulak Golf & CC",
          :location "Almaty, Kazakhstan",
          :ll (76.92861 43.25667),
          :coauthor "w/Colin Montgomerie",
          :year "2015"}
         {:course "Phu Quoc Golf Resort(27)",
          :location "Phu Quoc Island, Vietnam",
          :ll (104.0 10.2),
          :year "2015"}
         {:course "Bana Hills Golf Club",
          :location "Danang, Vietnam",
          :ll (108.22083 16.06778),
          :coauthor "w/Luke Donald",
          :year "2016"})
   (list {:course "Remodels/Renovations/Restorations",
          :location nil, :ll (10.45 5.8),
          :year nil}
         {:course "CC of Miami – (36 holes)",
          :location "Miami, FL",
          :ll (-80.13005 25.79065),
          :coauthor "Jones – Renovation w/Weed",
          :year "1990"}
         {:course "Dalat Palace",
          :location "Dalat, Vietnam",
          :ll (108.44193 11.94646),
          :coauthor "Renovation",
          :year "1996"}
         {:course "Shaker Heights CC",
          :location "Shaker Heights, OH",
          :ll (-81.53707 41.47394),
          :coauthor "Ross – Restoration",
          :year "1998"}
         {:course "Portland CC",
          :location "Portland, ME",
          :ll (-70.25533 43.66147),
          :coauthor "Ross – Restoration",
          :year "1999"}
         {:course "Rhode Island CC",
          :location "Barrington, RI",
          :ll (-71.30866 41.74066),
          :coauthor "Ross – Restoration",
          :year "2000"}
         {:course "The Country Club",
          :location "Pepper Pike, OH",
          :ll (-81.46373 41.47839),
          :coauthor "Flynn",
          :year "- Restoration"}
         {:course "Ananti Golf Club",
          :location "Seoul, Korea",
          :ll (126.9803 37.5731),
          :coauthor "Remodel",
          :year "2011"}
         {:course "Kahkwa Club",
          :location "Erie, PA",
          :ll (-80.08506 42.12922),
          :coauthor "Ross – Restoration",
          :year "2011"}
         {:course "Kunming Country Club",
          :location "Kunming, China",
          :ll (102.71833 25.03889),
          :coauthor "Remodel",
          :year "2012"})
   (list {:course "Geleximco Golf Club (36)",
          :location "Hanoi, Vietnam",
          :ll (105.84117 21.0245),
          :year "2017"}
         {:course "Vu Yen Golf Course (36)",
          :location "Hai Phong, Vietnam",
          :ll (106.68345 20.86481),
          :year "2017"}
         {:course "Laguna National (Renovate)",
          :location "Singapore",
          :ll (103.85007 1.28967),
          :year "2017"}
         {:course "Cau Duong Golf Club",
          :location "Hanoi, Vietnam",
          :ll (105.84117 21.0245),
          :coauthor "w/Nick Faldo",
          :year "2018"}
         {:course "Cau Duong Golf Club",
          :location "Hanoi, Vietnam",
          :ll (105.84117 21.0245),
          :year "2018"}
         {:course "Quy Nhon Golf Club",
          :location "Quy Nhon, Vietnam",
          :ll (109.22367 13.77648),
          :year "2018"})])