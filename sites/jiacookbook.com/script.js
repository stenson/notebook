
function rand255() {
  return Math.floor(Math.random() * 255);
}

function randomColor(a) {
  return "rgba("+rand255()+","+rand255()+","+rand255()+","+a+")";
}

$(function() {
  var d = 6000;
  var change = function() {
    var text = randomColor(1.0);
    $("#color").animate({"background-color": randomColor(0.5)}, {duration:d});
    $("h1").animate({"color": text}, {duration: d});
      //.animate({"border-color": randomColor}, {duration: d});
    $("h2").animate({"color": text}, {duration: d});
  };

  var changing = setInterval(change, d);
  setTimeout(function() {
    clearInterval(changing);
  }, 1000 * 30);
  //change();
});