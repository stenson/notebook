mapboxgl.accessToken = 'pk.eyJ1Ijoic3RlbnNvbiIsImEiOiJjaWtiaW40MDEwbTg2dnVrcGlpNTFoMjE3In0.9yCEVzLSavURROrUqM3bcg';

var markers = {
  "type": "FeatureCollection",
  "features": [{
    "type": "Feature",
    "properties": {
      "description": "<div class=\"marker-title\">Carondelet House</div><p>627 S Carondelet St<br/>Los Angeles, CA<br/>90057</p>",
      "title": "Wedding Venue",
      "marker-symbol": "airport",
      "title-anchor": "bottom"
    },
    "geometry": {
      "type": "Point",
      "coordinates": [-118.280375, 34.060705]
    }
  }, {
    "type": "Feature",
    "properties": {
      "description": "<div class=\"marker-title\">The Fig House</div><p>6433 N Figueroa St<br/>Los Angeles<br/>CA 90042</p>",
      "marker-symbol": "star",
      "title": "Rehearsal Dinner",
      "title-anchor": "bottom"
    },
    "geometry": {
      "type": "Point",
      "coordinates": [-118.183901, 34.118905]
    }
  }, {
    "type": "Feature",
    "properties": {
      "description": "<div class=\"marker-title\">Langham Huntington</div><p>1401 S Oak Knoll Ave<br/>Pasadena, CA<br/>91106</p>",
      "title": "The Hotel",
      "marker-symbol": "harbor",
      "title-anchor": "top"
    },
    "geometry": {
      "type": "Point",
      "coordinates": [-118.133352, 34.119810]
    }
  }]
};


var map = new mapboxgl.Map({
  container: 'map', // container id
  //style: 'mapbox://styles/stenson/cikbjaczf006q9um5iewjv7y0',
  //style: 'mapbox://styles/mapbox/light-v8',
  style: "mapbox://styles/stenson/cikbqkt2t006g9fm18v2zxtkz",
  center: [
    -118.208023,
    34.095621
  ],
  pitch: 0.00,
  zoom: 11
});

map.on('style.load', function () {
  map.addSource("markers", {
    "type": "geojson",
    "data": markers
  });

  map.addLayer({
    "id": "markers",
    "interactive": true,
    "type": "symbol",
    "source": "markers",
    "layout": {
      "icon-image": true ? "default_marker" : "interstate_1",
      "icon-size": 1,
      "text-field": "{title}",
      "text-font": ["Elementa Pro Bold Italic", "Arial Unicode MS Bold"],
      "text-offset": [0.5, -2],
      //"text-anchor": "{title-anchor}",
      "text-size": "18",
      "text-rotate": -20
    },
    "paint": {
      "text-color": "#BC332F",
      "text-halo-color": "white",
      "text-halo-width": "2",
      "text-halo-blur": "0.3"
    }
  });
});

map.on('click', function (e) {
  map.featuresAt(e.point, {layer: 'markers', radius: 10, includeGeometry: true}, function (err, features) {
    if (err || !features.length)
      return;

    var feature = features[0];

    new mapboxgl.Popup()
      .setLngLat(feature.geometry.coordinates)
      .setHTML(feature.properties.description)
      .addTo(map);
  });
});

// Use the same approach as above to indicate that the symbols are clickable
// by changing the cursor style to 'pointer'.
map.on('mousemove', function (e) {
  map.featuresAt(e.point, {layer: 'markers', radius: 10}, function (err, features) {
    map.getCanvas().style.cursor = (!err && features.length) ? 'pointer' : '';
  });
});