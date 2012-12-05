#                                                                 -*- ruby -*-

require 'rubygems'
require 'rake/clean'
require 'git'
require 'tmpdir'

# ---------------------------------------------------------------------------
# Functions used outside tasks
# ---------------------------------------------------------------------------

def tmpdir(prefix)
  File.join(Dir::tmpdir, Dir::Tmpname.make_tmpname(prefix, 'dir'))
end

def git_temp
  tmpdir('git-phase')
end

# ---------------------------------------------------------------------------
# Constants
# ---------------------------------------------------------------------------

TWITTER_BOOTSTRAP_MASTER = 'https://github.com/twitter/bootstrap'
FONT_AWESOME_MASTER = 'https://github.com/FortAwesome/Font-Awesome'

BOOTSTRAP_URL = ENV['BOOTSTRAP_URL'] || TWITTER_BOOTSTRAP_MASTER
FONT_AWESOME_URL = ENV['FONT_AWESOME_URL'] || FONT_AWESOME_MASTER

_bootstrap_git_temp = nil
def bootstrap_git_temp
  _bootstrap_git_temp ||= git_temp
end

def local_bootstrap?
  ! ENV['BOOTSTRAP_SOURCE'].nil?
end

def bootstrap_source
  ENV['BOOTSTRAP_SOURCE'] || bootstrap_git_temp
end

BOOTSTRAP_SUBDIR = 'bootstrap'

FONT_AWESOME_SUBDIR = 'font-awesome'

BOOTSTRAP_CUSTOM_LESS = 'bootstrap/less/custom.less'
BOOTSTRAP_LESS = 'bootstrap/less/bootstrap.less'

CLEAN << ['css', '_site']

# ---------------------------------------------------------------------------
# Main tasks
# ---------------------------------------------------------------------------

task :default => :jekyll

desc "Format the site."
task :jekyll => :css do |t|
  sh 'jekyll'
end

task :preview => :run
task :server => :run

desc "Format the blog, then fire up a local HTTP server."
task :run => :css do |t|
  sh "jekyll", "--server"
end

# ---------------------------------------------------------------------------
# CSS/SASS
# ---------------------------------------------------------------------------

# Generate CSS files from LESS files

CSS_DIR = 'css'

directory 'stylesheets'

LESS_DIR = 'less'
LESS_FILES = FileList["#{LESS_DIR}/*.less"]
CSS_OUTPUT_FILES = LESS_FILES.map do |f|
  f.gsub(/^#{LESS_DIR}/, CSS_DIR).gsub(/\.less$/, '.css')
end

# Figure out the name of the LESS file necessary make a CSS file.
def css_to_less
  Proc.new {|task| task.sub(/^#{CSS_DIR}/, LESS_DIR).sub(/\.css$/, '.less')}
end

rule %r{^#{CSS_DIR}/.*\.css$} => [css_to_less, 'Rakefile'] + LESS_FILES do |t|
  mkdir_p CSS_DIR
  puts("#{t.source} -> #{t.name}")
  Dir.chdir('less') do
    less_input = File.basename(t.source)
    css_output = File.join('..', t.name)
    lessc_full less_input, css_output
  end
end

desc "Run SASS to produce the stylesheets."
task :css => CSS_OUTPUT_FILES


desc "Rebuild the Twitter Bootstrap and Sass Twitter Bootstrap stuff."
task :bootstrap => [
  :bootstrap_git,
  :bootstrap_js,
  :bootstrap_css,
  :bootstrap_clean
]

desc "Generate bootstrap.min.css"
task :bootstrap_css => [:bootstrap_css_copy, :bootstrap_css_edit, :bootstrap_css_compile]

task :bootstrap_css_copy do
  puts "Copying LESS files"
  mkdir_p "bootstrap/less"
  Dir.glob(File.join(bootstrap_source, 'less', '*.less')).each do |source|
    target = File.join('bootstrap/less', File.basename(source))
    cp source, target if different?(source, target)
  end

  mkdir_p 'bootstrap/img'
  Dir.glob(File.join(bootstrap_source, 'img', '*')).each do |image|
    target = File.join('bootstrap/img', File.basename(image))
    cp image, target if different?(image, target)
  end
end

task :bootstrap_css_edit do
  temp = "temp.less"
  begin
    puts "Editing Bootstrap LESS file(s)."
    Dir.glob(File.join("bootstrap", "less", "*.less")).each do |less|

      t = File.open(temp, "w")
      t.write(File.open(less).read.each_line.map do |line|
        r = %r{"\.\./img\/}
        line = line.sub(r, '"/bootstrap/img/') if line =~ r
        line
      end.join(""))
      t.close
      cp temp, less if different?(temp, less)
    end
  ensure
    rm_f temp
  end
end

task :bootstrap_css_compile do
  puts "Compiling #{BOOTSTRAP_CUSTOM_LESS}"
  mkdir_p "bootstrap/css"
  lessc_min BOOTSTRAP_CUSTOM_LESS, 'bootstrap/css/bootstrap.min.css'
  lessc_full BOOTSTRAP_CUSTOM_LESS, 'bootstrap/css/bootstrap.css'
end

task :bootstrap_js do
  require 'uglifier'
  require 'erb'

  template = ERB.new %q{
  <!-- AUTOMATICALLY GENERATED. DO NOT EDIT. -->
  <% paths.each do |path| %>
  <script type="text/javascript" src="/bootstrap/js/<%= path %>"></script>
  <% end %>
  }

  mkdir_p 'bootstrap/js'
  paths = []
  minifier = Uglifier.new
  Dir.glob(File.join(bootstrap_source, 'js', '*.js')).each do |source|
    base = File.basename(source).sub(/^(.*)\.js$/, '\1.min.js')
    paths << base
    target = File.join('bootstrap/js', base)
    if different?(source, target)
      File.open(target, 'w') do |out|
        out.write minifier.compile(File.read(source))
      end
    end
  end

  mkdir_p '_includes'
  File.open('_includes/bootstrap.js.html', 'w') do |f|
    f.write template.result(binding)
  end
end

# Do not invoke this directly. It will fail.
task :bootstrap_clean do
  rm_r bootstrap_git_temp unless local_bootstrap?
end

task :bootstrap_git do
  unless local_bootstrap?
    git_clone BOOTSTRAP_URL, bootstrap_git_temp, BOOTSTRAP_SUBDIR
  end
end

desc "Update Font-Awesome"
task :font_awesome do
  temp = git_temp
  begin
    rm_r FONT_AWESOME_SUBDIR
    mkdir FONT_AWESOME_SUBDIR
    git_clone FONT_AWESOME_URL, temp, "."
    font_dir = FONT_AWESOME_SUBDIR
    mkdir_p font_dir
    css_dir = File.join(FONT_AWESOME_SUBDIR, "css")
    mkdir_p css_dir
    cp_r File.join(temp, "font"), font_dir
    cp File.join(temp, "css", "font-awesome.css"), css_dir
  ensure
    rm_r temp
  end
end

# ---------------------------------------------------------------------------
# Functions used within tasks
# ---------------------------------------------------------------------------

def lessc_full(input, output)
  sh 'lessc', input, output
end
def lessc_min(input, output)
  sh 'lessc', '--compress', input, output
end

def git_clone(url, parent_dir, child_dir)
  FileUtils.mkdir_p parent_dir
  Dir.chdir(parent_dir) do
    puts "In #{parent_dir}\n    Cloning #{url}"
    Git.clone(url, child_dir)
  end
end

def different?(path1, path2)
  require 'digest/md5'

  if File.exist?(path1) && File.exist?(path2)
    path1_md5 = Digest::MD5.hexdigest(File.read path1)
    path2_md5 = Digest::MD5.hexdigest(File.read path2)
    (path2_md5 != path1_md5)
  else
     true
  end
end