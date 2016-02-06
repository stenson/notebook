require "rubygems"
require "nokogiri"
require "httparty"
require "tilt"
require "erb"
require "pp"

# so tilt recognizes the filenames properly
Tilt.register "rhtml", Tilt::ERBTemplate

class FlickrPipe
  include HTTParty
  base_uri "http://api.flickr.com/services/rest/"
  
  @@basics = {
    :api_key => "330e0c098f9e263fe2be15e8fbaa007f"
  }
  
  def fetch(options)
    Nokogiri::XML(self.class.get("", build_options(options)).body)
  end
  
  def build_options(more_options)
    options = {}
    options.merge!(@@basics)
    options.merge!(more_options)
    { :query => options }
  end
  
  def all_sets_for(user_id)
    pieces = []
    
    sets = fetch({
      :method => "flickr.photosets.getList",
      :user_id => user_id
    })
    
    sets.css("photosets photoset").each do |set_info|
      title = set_info.css("title").text
      if !title.match(/^\+\+\+/)
        puts title
        pieces.push(Piece.new(set_info["id"], set_info.css("title").text))
      end
    end
    
    pieces
  end
  
  def all_collections_for(user_id, piece_hash)
    resp = fetch({
      :method => "flickr.collections.getTree",
      :user_id => user_id
    })
    
    collections = []
    
    resp.css("collections collection").each do |c|
      pieces = c.css("set").map do |set|
        piece_hash[set["id"]]
      end
      collections.push(Collection.new(c["id"], c["title"], c["iconsmall"], pieces))
    end
    
    collections
  end
  
  def all_photos_in_set(set_id)
    puts "fetching #{set_id}"
    photos = []
    
    set = fetch({
      :method => "flickr.photosets.getPhotos",
      :photoset_id => set_id
    })
    
    set.css("photoset photo").each do |photo|
      photos.push(FlickrPhoto.new(photo["id"]))
    end
    
    photos
  end
  
  def data_for_photo(photo_id)
  	sleep 1
    fetch({
      :method => "flickr.photos.getInfo",
      :photo_id => photo_id
    })
  end
  
end

class FlickrPhoto
  
  attr_accessor :unique_title, :title, :url_title
  attr_reader :id, :description, :tags, :unix, :urls, :height, :width
  
  def initialize(id)
    puts "processing photo #{id}"
    @id = id
    self.fetch_data
    @tags = []
    copy_to_local_server
  end
  
  def fetch_data
    data = FlickrPipe.new.data_for_photo(@id)
    @description = data.css("description").text
    @title = data.css("title").text.gsub(/[\s]{2,}/," ")
    @url_title = @title.gsub(/[\s]+/,"_").gsub(/#/,"no-")
    @unique_title = @title # for now...
    data.css("photo").each { |p| @unix = p["dateuploaded"] }
    @urls = self.get_urls_from(data)
  end
  
  def critical_stuff
    "#{@urls[:medium]}"
  end
  
  def get_tags_from(data)
    data.css("tags tag").map do |tag|
      tag.text.downcase()
    end
  end
  
  def get_urls_from(data)
    p = data.css("photo")[0]
    base = [
        "http://farm#{p['farm']}.static.flickr.com",
        "#{p['server']}",
        "#{p['id']}_#{p['secret']}"
      ].join "/"
    urls = {}
    { :large=>"z", :medium=>"m" }.each do |name,ext|
      urls[name] = base + "_#{ext}.jpg"
    end
    urls
  end
  
  def copy_to_local_server
    # rewrites the urls array
    @urls.each do |name,url|
      contents = HTTParty.get(url)
      filename = "files/"+url.split("/")[-1]
      File.open("#{filename}","w") { |file| file.write(contents) }
      @urls[name] = filename
    end
  end
  
end

class Piece
  attr_reader :id, :title, :photos, :url_title
  
  def initialize(id, title)
    @id = id
    @title = title
    @url_title = @title.gsub(/[\s]+/,"_").gsub(/#/,"no-")
    @photos = FlickrPipe.new.all_photos_in_set(id)
    add_unique_titles_to_photos
  end

  def add_unique_titles_to_photos
    i = 1
    @photos.each do |p|
      p.title = @title
      p.url_title = "#{@url_title}_view_#{i}"
      i += 1
    end
  end
end

class Collection
  attr_reader :id, :title, :icon, :pieces, :url_title
  
  def initialize(id, title, icon, pieces)
    @id, @title, @icon, @pieces = id, title, icon, pieces
    @url_title = @title.gsub(/[\s]+/,"_").gsub(/#/,"no-")
  end
end

# get all the photos with their text
# create an html page for each unique tag

class Site
  
  attr_reader :photos, :tags, :pages
  
  #@@threehexagons = "52717351@N06"
  @@threehexagons = "54965767@N04"
  
  def initialize
    @pieces = get_pieces
    @collections = get_collections
    @photos = photos_from_pieces
    @tags = {}
    @pages = []
    
    write_pages
    puts "...and done"
  end

  def get_pieces
    clean_directory "files" # get rid of old photos
    FlickrPipe.new.all_sets_for @@threehexagons
  end
  
  def get_collections
    piece_hash = {}
    @pieces.each { |p| piece_hash[p.id] = p }
    FlickrPipe.new.all_collections_for @@threehexagons, piece_hash
  end
  
  def photos_from_pieces
    photos = []
    @pieces.each { |p| p.photos.each { |photo| photos.push(photo) } }
    photos
  end
  
  def build_tags
    tags = {}
    @photos.each do |photo|
      photo.tags.each do |tag|
        if tags.has_key? tag
          tags[tag].push(photo)
        else
          tags[tag] = [].push(photo)
        end
      end
    end
    tags
  end
  
  def write_pages
    # get any static pages
    @incidentals = Dir.entries("incidentals").
      reject { |f| File.directory? f }.
      map { |f| f.gsub(".rhtml","") }
    # clean the old stuff
    ["photos", "pieces"].each { |d| clean_directory "#{d}" }
    
    # now build the individual groups of pages
    build_collection_pages
    build_pieces_pages # /pieces/
    build_photo_pages # /photos/Crop-Circle-1
    build_incidental_pages #/about
    build_index_page # /
  end
  
  def clean_directory(name)
    Dir.foreach(name) do |f|
      File.delete("#{name}/#{f}") if f != "." and f != ".."
    end
  end
  
  def build_pieces_pages
    $pieces = @pieces
    @pieces.each do |piece|
      $piece = piece
      if(piece.photos)
        eval_and_write "piece", "pieces/#{piece.url_title}"
      end
    end
  end
  
  def build_tagged_pages
    $tags = @tags
    @tags.each do |tag,tagged|
      $tagged = tagged
      eval_and_write "tagged", "tagged/#{tag}"
    end
  end
  
  def build_collection_pages
    @collections.each do |c|
      $collection = c
      eval_and_write "collection", "collections/#{c.url_title}"
    end
  end
  
  def build_photo_pages
    @photos.each do |photo|
      $photo = photo
      eval_and_write "photo", "photos/#{photo.url_title}"
    end
  end
  
  def build_incidental_pages
    @incidentals.each do |f|
      write_file(f, eval_template("incidentals/#{f}", true ))
    end
  end
  
  def build_index_page
    $photos = @photos # should just be #frontpage images
    eval_and_write "index", "index"
  end
  
  def eval_and_write(templatename,filename)
    write_file( filename, eval_template( templatename ) )
  end
  
  def eval_template(template,nonstandard = false)
    $class = template.gsub("/","-")
    $incidentals = @incidentals
    $collections = @collections
    inner = nonstandard ? template : "views/#{template}"
    Tilt.new("views/page.rhtml").render { # outer template
      Tilt.new("#{inner}.rhtml").render # inner template
    }
  end
  
  def write_file(name,contents)
    File.open("#{name}.html","w") do |file|
      file.write(contents)
    end
  end
  
end
  
# now build it!
site = Site.new