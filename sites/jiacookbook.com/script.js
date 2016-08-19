
function rand255() {
  return Math.floor(Math.random() * 255);
}

function randomColor() {
  return "rgb("+rand255()+","+rand255()+","+rand255()+")";
}

$(function() {
  var d = 6000;
  var change = function() {
    $("body").animate({"background-color": randomColor()}, {duration:d});
    $("h1").animate({"color": randomColor()}, {duration: d});
    $("h3").animate({"color": randomColor()}, {duration: d});
  };

  setInterval(change, d);
  change();
});