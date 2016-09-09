
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
