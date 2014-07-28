#                                                                 -*- ruby -*-

require 'rubygems'
require 'rake/clean'
require 'git'
require 'tmpdir'
require 'fssm'

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

FONT_AWESOME_MASTER = 'https://github.com/FortAwesome/Font-Awesome'
FONT_AWESOME_URL    = ENV['FONT_AWESOME_URL'] || FONT_AWESOME_MASTER
FONT_AWESOME_SUBDIR = 'font-awesome'

LESS_DIR            = 'less'
LESS_FILES          = FileList["#{LESS_DIR}/*.less"].select do |f|
                        f !~ %r|^less/_|
                      end
TEMPLATES_DIR       = '_templates'
TALKS_YAML          = 'talks.yml'
TALKS_TEMPLATE      = '_templates/talks.mustache'
TALKS_HTML          = '_includes/talks.html'

CLEAN << ['css', '_site']

# ---------------------------------------------------------------------------
# Main tasks
# ---------------------------------------------------------------------------

task :default => :jekyll

desc "Format the site."
task :jekyll => [:css, :talks] do |t|
  sh 'jekyll build'
end

task :preview => :run
task :server => :run

desc "Format the blog, then fire up a local HTTP server."
task :run => [:css, :talks] do |t|
  watch_less_files
  watch_talks
  sh "jekyll server --watch"
end

task :talks => TALKS_HTML

file TALKS_HTML => [TALKS_YAML, TALKS_TEMPLATE, 'Rakefile'] do
  puts "Rebuilding #{TALKS_HTML}"
  convert_talks
end

# ---------------------------------------------------------------------------
# CSS/SASS
# ---------------------------------------------------------------------------

class Lesser
  include FileUtils

  def initialize
  end

  def convert(lessfile)
    css = css_for_less lessfile
    puts "Converting #{lessfile} to #{css}"
    lessc_full lessfile, css
  end

  private

  def lessc_full(input, output)
    sh 'lessc', input, output
  end

  def lessc_min
    sh 'lessc', '--compress', input, output
  end

  def css_for_less(less)
    dir = File.dirname(File.dirname(File.absolute_path(less)))
    base = File.basename(less, ".less")
    "#{dir}/css/#{base}.css"
  end
end

# Generate CSS files from LESS files

CSS_DIR = 'css'

directory 'stylesheets'

CSS_OUTPUT_FILES = LESS_FILES.map do |f|
  f.gsub(/^#{LESS_DIR}/, CSS_DIR).gsub(/\.less$/, '.css')
end

# Figure out the name of the LESS file necessary make a CSS file.
def css_to_less
  Proc.new {|task| task.sub(/^#{CSS_DIR}/, LESS_DIR).sub(/\.css$/, '.less')}
end

lessc = Lesser.new

rule %r{^#{CSS_DIR}/[^_].*\.css$} => [css_to_less, 'Rakefile'] + LESS_FILES do |t|
  mkdir_p CSS_DIR
  puts("#{t.source} -> #{t.name}")
  Dir.chdir('less') do
    less_input = File.basename(t.source)
    css_output = File.join('..', t.name)
    lessc.convert less_input
  end
end

desc "Run SASS to produce the stylesheets."
task :css => CSS_OUTPUT_FILES

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

#############################################################################
# Functions used within tasks
#############################################################################

# ---------------------------------------------------------------------------
# Mustache template handling, for talks
# ---------------------------------------------------------------------------

require 'mustache'
class Talk
  attr_reader :title, :speaker, :date, :meetup, :slides, :video, :code,
              :meetup_id

  def initialize(title, speaker, date_str, meetup, slides, video, code)
    @title      = title
    @speaker    = speaker
    @date       = Date.parse(date_str)
    @meetup     = meetup
    @meetup_id  = meetup.split('/').last
    @slides     = slides
    @video      = video
    @code       = code
  end
end

class Talks < Mustache
  attr_reader :talks
  def initialize(talks)
    @talks = talks
  end
end

class TalkRenderer
  def initialize(yaml_path, template_path, html_out)
    @yaml     = yaml_path
    @template = template_path
    @html_out = html_out
  end

  def render
    require 'yaml'
    require 'mustache'

    # Mustache is just simpler than Liquid.

    talks = Talks.new(load_yaml(@yaml).sort { |t1, t2| t2.date <=> t1.date })
    render_template @template, talks, @html_out
  end

  private

  def render_template(template, talks, html_out)
    File.open(html_out, 'w') do |f|
      template = File.open(template).read
      f.write(talks.render(template))
    end
  end

  def load_yaml(yaml)
    File.open yaml do |f|
      data = YAML.load f
      data['talks'].map do |h|
        slides = if h['slides']
                   OpenStruct.new(link:    h['slides']['link'],
                                  comment: h['slides']['comment'])
                 else
                   nil
                 end

        Talk.new(h['title'], h['speaker'], h['date'], h['meetup_link'], slides,
                 h['video'], h['code'])
      end
    end
  end
end

def convert_talks
  TalkRenderer.new(TALKS_YAML, TALKS_TEMPLATE, TALKS_HTML).render
end

def watch_talks
  def watch_templates
    fork do
      FSSM.monitor(TEMPLATES_DIR) do
        glob '*.mustache'

        update { |base, relative| convert_talks }
        create { |base, relative| convert_talks }
        delete { |base, relative| convert_talks }
      end
    end
  end

  def watch_yaml
    fork do
      FSSM.monitor(".", TALKS_YAML) do
        update { |base, relative| convert_talks }
        create { |base, relative| convert_talks }
        delete { |base, relative| convert_talks }
      end
    end
  end

  watch_templates
  watch_yaml
end

# ---------------------------------------------------------------------------
# LESS (CSS) handling
# ---------------------------------------------------------------------------

def rebuild_less_files
  lessc = Lesser.new
  Dir.glob("#{LESS_DIR}/[^_]*.less").each do |less|
    lessc.convert less
  end
rescue Exception => ex
  puts ex.message
end

def watch_less_files
  fork do
    FSSM.monitor(LESS_DIR) do
      update do |base, relative|
        # For now, it doesn't matter what changed. Update them all.
        rebuild_less_files
      end
      create do |base, relative|
        rebuild_less_files
      end
      delete do |base, relative|
        rebuild_less_files
      end
    end
  end
end

# ---------------------------------------------------------------------------
# Misc.
# ---------------------------------------------------------------------------

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
