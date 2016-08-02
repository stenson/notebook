var width = 800;
var height = 500;
var active = d3.select(null);

var projection = d3.geoAlbersUsa()
  .translate([width/2, height/2])
  .scale([1000]);

var path = d3.geoPath()
  .projection(projection);

var svg = d3.select("#container")
  .append("svg")
  .attr("width", width)
  .attr("height", height);

var g = svg.append("g");
  //.style("stroke-width", "1.5px");

svg = g;

svg.append("rect")
  .attr("class", "background")
  .attr("width", width)
  .attr("height", height)
  .on("click", reset);

var div = d3.select("#container")
  .append("div")
  .attr("class", "tooltip")
  .style("opacity", 0);

var active;
var j;

queue()
  .defer(d3.json, "us.json")
  .defer(d3.json, "members2.json")
  // should load places separately?
  .await(ready);

function ready(error, us, members) {
  svg.append("defs").append("path")
    .attr("id", "land")
    .datum(topojson.feature(us, us.objects.land))
    .attr("d", path);

  svg.append("clipPath")
    .attr("id", "clip-land")
    .append("use")
    .attr("xlink:href", "#land");

  svg.append("g")
    .attr("class", "districts")
    .attr("clip-path", "url(#clip-land)")
    .selectAll("path")
    .data(topojson.feature(members, members.objects.members).features)
    .enter().append("path")
    .attr("d", path)
    .style("fill", function(d) {
      if (d.properties["born-there"]) {
        return d3.color("#888");
      } else if (d.properties["born-in-state"]) {
        return d3.color("#bbb");
      } else if (d.properties["born-in-us"]) {
        return d3.color("#ddd");
      } else {
        return d3.color("royalblue");
      }
    })
    .on("mouseover", function(d) {
      console.log(d.properties.slug, d.properties.name);
    })
    .on("click", clicked)
    .append("title")
    .text(function(d) { return d.id; });

  //svg.append("path")
  //  .attr("class", "district-boundaries")
  //  .datum(topojson.mesh(members, members.objects.members,
  //    function(a, b) {
  //      return a !== b && a.properties.state === b.properties.state;
  //    }))
  //  .attr("d", path);

  svg.append("path")
    .attr("class", "state-boundaries")
    .datum(topojson.mesh(us, us.objects.states, function(a, b) { return a !== b; }))
    .attr("d", path);
}

function clicked(d) {
  if (active.node() === this) return reset();
  active.classed("active", false);
  active = d3.select(this).classed("active", true);

  var bounds = path.bounds(d),
    dx = bounds[1][0] - bounds[0][0],
    dy = bounds[1][1] - bounds[0][1],
    x = (bounds[0][0] + bounds[1][0]) / 2,
    y = (bounds[0][1] + bounds[1][1]) / 2,
    scale = .9 / Math.max(dx / width, dy / height),
    translate = [width / 2 - scale * x, height / 2 - scale * y];

  svg.transition()
    .duration(750)
    .style("stroke-width", 1.5 / scale + "px")
    .attr("transform", "translate(" + translate + ")scale(" + scale + ")");
}

function reset() {
  active.classed("active", false);
  active = d3.select(null);

  svg.transition()
    .duration(750)
    .style("stroke-width", "1.5px")
    .attr("transform", "");
}