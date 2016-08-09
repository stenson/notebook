
mapboxgl.accessToken = 'pk.eyJ1Ijoic3RlbnNvbiIsImEiOiJjaWtiaW40MDEwbTg2dnVrcGlpNTFoMjE3In0.9yCEVzLSavURROrUqM3bcg';

var map = new mapboxgl.Map({
  container: "map",
  style: "mapbox://styles/stenson/cirntjgi70001g0nczvkd442q",
  center: [-100.04, 38.907],
  zoom: 3.3,
  minZoom: 2,
  maxZoom: 9
});

map.on("load", function() {

  map.addSource("members", {
    type: "geojson",
    data: "/_districts.json"
  });

  map.addLayer({
    id: "members-layer",
    type: "fill",
    source: "members",
    paint: {
      'fill-color': {
        property: 'party-int',
        stops: [
          [0, "indianred"],
          [1, "royalblue"]
        ]
      }
    },
    filter: //[
      //[">", "distance", 2000],
      ["==", "birth-score", 4]
    //]
  }, "countries");

  //map.addLayer({
  //  id: "members-lines",
  //  type: "line",
  //  source: "members",
  //  minzoom: 5,
  //  paint: {
  //    'line-color': 'rgba(255, 255, 255, 0.45)',
  //    'line-width': 0.3
  //  }
  //}, "countries");
  //
  //map.addLayer({
  //  id: "members-highlight",
  //  type: "fill",
  //  source: "members",
  //  paint: {
  //    'fill-color': 'rgba(200, 100, 240, 0.1)',
  //    'fill-outline-color': 'rgba(200, 100, 240, 1)'
  //  },
  //  filter: [["==", "name", ""]]
  //}, "countries");

  map.on("click", function(e) {
    var features = map.queryRenderedFeatures(e.point, { layers: ['members-layer'] });
    if (!features.length) {
      return;
    }

    var feature = features[0];

    var popup = new mapboxgl.Popup()
      .setLngLat(map.unproject(e.point))
      .setHTML([
        "<h1>" + feature.properties.slug + "</h1>",
        "<h2>" + feature.properties.name + "</h2>",
        "<div class='details'>",
        "<h3><span>From</span> " + feature.properties["birth-place"] + "</h3>",
        "<h3><span>To</span> " + feature.properties["hometown"] + "</h3>",
        "</div>"
      ].join("\n"))
      .addTo(map);
  });
});