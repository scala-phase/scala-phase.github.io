#                                                                 -*- ruby -*-

require 'rubygems'
require 'git'
require 'tmpdir'
require 'jekyll-helpers'
require 'fssm'
require 'date'

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

# ---------------------------------------------------------------------------
# Main tasks
# ---------------------------------------------------------------------------

JekyllHelpers::Tasks.new.install do |config|
  config.less_dir      = LESS_DIR
  config.css_dir       = 'stylesheets'
  config.less_compress = true
end

task :default => :jekyll

task :generate => :jekyll

desc "Format the site."
task :jekyll => [:css, :talks] do |t|
  sh 'jekyll build'
end

task :gen => :generate
task :preview => :run
task :server => :run

desc "Format the blog, then fire up a local HTTP server."
task :run => [:css, :talks, 'jekyll_helpers:watch_less'] do |t|
  watch_talks
  sh "jekyll server --watch --host 0.0.0.0"
end

task :talks => TALKS_HTML do
  rm_rf 'talks/slick-2015-03-19'
  git_clone 'https://github.com/bmc/slick-presentation-2015-03-19.git', 'talks', 'slick-2015-03-19'
end

file TALKS_HTML => [TALKS_YAML, TALKS_TEMPLATE, 'Rakefile'] do
  puts "Rebuilding #{TALKS_HTML}"
  convert_talks
end

task :css_clean do
  rm_rf 'stylesheets'
end

task :css => :css_clean do
  sh 'lessc less/style.less stylesheets/style.css'
end

task :gen_deploy => [:generate, :deploy]

desc "Deploy the site to the VPS."
task :deploy do
  RSYNC_ARGS = [
    'rsync',
    '-avuz',
    '--delete',
    '--exclude', '/.git',
    '--exclude', '/tmp',
    '--exclude', '*.iml',
    '--exclude', '/.idea',
    '.', 'yore.ardentex.com:/var/www/scala-phase.org/html'
  ]
  sh *RSYNC_ARGS
end

task :clean do
  rm_rf "_site"
  rm_f "_includes/talks.html"
  rm_rf "stylesheets"
end

#############################################################################
# Functions used within tasks
#############################################################################

# ---------------------------------------------------------------------------
# Mustache template handling, for talks
# ---------------------------------------------------------------------------

require 'mustache'
class Talk
  attr_reader :title, :speaker, :date, :meeting_link, :slides, :video,
              :meeting_id, :id, :code_link, :code_label, :css_class

  @@next_id = 0

  def initialize(title, speaker, date_str, meeting_link, slides, video, code)
    @id           = @@next_id
    @@next_id    += 1
    @css_class    = if (@id % 2) == 0 then "even" else "odd" end
    @title        = title
    @speaker      = speaker
    @date         = ::Date.parse(date_str)
    @meeting_link = meeting_link

    if meeting_link =~ %r|^http.*meetup.com/scala-phase/events/(\d+)/.*$|
      @meeting_id   = "meetup.com/.../#{$1}"
    else
      @meeting_id = meeting_link.sub(%r|https?://|, "")
    end

    @slides       = slides
    @video        = video
    @code_label   = code
    @code_link    = code
    if code
      @code_label = @code_label.sub(%r|^https?://|, "")
    end
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
                   OpenStruct.new(link:      h['slides']['link'],
                                  comment:   h['slides']['comment'])
                 else
                   nil
                 end

        Talk.new(h['title'], h['speaker'], h['date'], h['meeting_link'], slides,
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
