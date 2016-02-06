$(function(){
    var $w = $(window);
    $("#lefthand").height($w.height());
    
    // highlight the sidebar link appropriately
    var path = window.location.pathname;
    if(path !== "/") {
        $("#lefthand a").each(function(){
            var $this = $(this);
            if(path === $this.attr("href")) {
                $this.addClass("current");
            }
        });
    }
    
    $("a.iphoto").mouseover(function(){
      $("a.photo").each(function(){
        clearTimeout($(this).data("changer"));
      });
      var $link = $(this),
        current = $link.find("img").attr("src").slice(1),
        photos = $link.data("photos").split(";"),
        position = 0;
      for(var i = 0; i < photos.length; i += 1) {
        if(photos[i] == current) {
          current = i;
          break;
        }
      }
      $link.data("changer",setInterval(function(){
        (current+1 >= photos.length) ? current = 0 : current += 1;
        $link.find("img").attr("src","/"+photos[current]);
      },700));
    });
    
    $("a.iphoto").mouseout(function(){
      clearTimeout($(this).data("changer"));
    });
});