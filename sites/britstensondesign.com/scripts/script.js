
var $carousel = $(".carousel");
//var flkty = $carousel.data('flickity');
//var $imgs = $('.carousel-cell img');

$carousel.on('select.flickity', function(e) {
  var $selected = $carousel.find(".is-selected");
  var colors = $selected.data("colors");
  $("#header-outer").animate({"border-top-color": colors[0]}, {duration:300});
  $(".logo").animate({"color": colors[0]}, {duration:300});
  //$("body").animate({"background-color": colors[1]}, {duration:300});
});

mapboxgl.accessToken = 'pk.eyJ1Ijoic3RlbnNvbiIsImEiOiJjaWtiaW40MDEwbTg2dnVrcGlpNTFoMjE3In0.9yCEVzLSavURROrUqM3bcg';

var map = new mapboxgl.Map({
  container: "map",
  style: "mapbox://styles/stenson/ciswdd2ry002z2wo0zsiay7i8",
  center: [-175.4296875, 23.241346102386135],
  zoom: 1,
  minZoom: 1,
  maxZoom: 9
});

map.on("click", function(e) {
  var features = map.queryRenderedFeatures(e.point, { layers: ['New Courses', 'Renovations', 'Under Construction'] });
  if (!features.length) {
    return;
  }

  var feature = features[0];

  var popup = new mapboxgl.Popup()
    .setLngLat(map.unproject(e.point))
    .setHTML([
      "<h1>" + feature.properties.course + "</h1>",
      "<h2>" + feature.properties.location + "</h2>",
      feature.properties.coauthor ? "<h3><em>with</em> " + feature.properties.coauthor + "</h3>" : "",
      "<h4>" + feature.properties.year + "</h4>",
      "</div>"
    ].join("\n"))
    .addTo(map);
});

map.scrollZoom.disable();
map.addControl(new mapboxgl.Navigation());