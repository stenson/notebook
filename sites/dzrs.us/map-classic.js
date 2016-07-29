var markers = {
  "type": "FeatureCollection",
  "features": [{
    "type": "Feature",
    "properties": {
      "description": "<div class=\"marker-title\">Fig House</div><p>Rehearsal Dinner Venue</p>",
      "marker-color": "#f86767",
      "marker-size": "large",
      "marker-symbol": "star",
      "title": "Rehearsal Dinner"
    },
    "geometry": {
      "type": "Point",
      "coordinates": [-118.183901, 34.118905]
    }
  }, {
    "type": "Feature",
    "properties": {
      "description": "<div class=\"marker-title\">Carondelet House</div><p>Wedding Venue</p>",
      "title": "Wedding Venue",
      "marker-color": "#f86767",
      "marker-size": "large",
      "marker-symbol": "star"
    },
    "geometry": {
      "type": "Point",
      "coordinates": [-118.280375, 34.060705]
    }
  }, {
    "type": "Feature",
    "properties": {
      "description": "<div class=\"marker-title\">Langham Huntington</div><p>Hotel</p>",
      "title": "Hotel",
      "marker-color": "#f86767",
      "marker-size": "large",
      "marker-symbol": "star"
    },
    "geometry": {
      "type": "Point",
      "coordinates": [-118.133352, 34.119810]
    }
  }]
};

/*
 var map = new mapboxgl.Map({
 container: 'map', // container id
 //style: 'mapbox://styles/stenson/cikbjaczf006q9um5iewjv7y0',
 style: 'mapbox://styles/mapbox/emerald-v8',
 center: [
 -118.208023,
 34.084611
 ],
 //pitch: 10.00,
 zoom: 11
 });
 */

L.mapbox.accessToken = 'pk.eyJ1Ijoic3RlbnNvbiIsImEiOiJjaWtiaW40MDEwbTg2dnVrcGlpNTFoMjE3In0.9yCEVzLSavURROrUqM3bcg';
var map = L.mapbox.map('map', 'mapbox.streets')
  .setView([34.084611, -118.208023], 11);

var myLayer = L.mapbox.featureLayer().addTo(map);
myLayer.setGeoJSON(markers);
myLayer.on('mouseover', function(e) {
  e.layer.openPopup();
});
myLayer.on('mouseout', function(e) {
  e.layer.closePopup();
});