#                                                                 -*- ruby -*-

require 'rubygems'
require 'rake/clean'
require 'git'
require 'tmpdir'
require 'jekyll-helpers'
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

task :preview => :run
task :server => :run

desc "Format the blog, then fire up a local HTTP server."
task :run => [:css, :talks, 'jekyll_helpers:watch_less'] do |t|
  watch_talks
  sh "jekyll server --watch"
end

task :talks => TALKS_HTML

file TALKS_HTML => [TALKS_YAML, TALKS_TEMPLATE, 'Rakefile'] do
  puts "Rebuilding #{TALKS_HTML}"
  convert_talks
end

task :css => 'jekyll_helpers:css'

task :gen_deploy => [:generate, :deploy]

desc "Deploy the site to the VPS."
task :deploy do
  sh 'wwwpush', 'phase'
end

#############################################################################
# Functions used within tasks
#############################################################################

# ---------------------------------------------------------------------------
# Mustache template handling, for talks
# ---------------------------------------------------------------------------

require 'mustache'
class Talk
  attr_reader :title, :speaker, :date, :meeting_link, :slides, :video, :code,
              :meeting_id, :resources, :id

  @@next_id = 0

  def initialize(title, speaker, date_str, meeting_link, slides, video, code,
                 resources)
    @id           = @@next_id
    @@next_id    += 1
    @title        = title
    @speaker      = speaker
    @date         = Date.parse(date_str)
    @meeting_link = meeting_link
    @meeting_id   = meeting_link.sub(%r|^https?://.*events/|, "").gsub("/", "")
    @slides       = slides
    @video        = video
    @code         = code
    @resources    = resources
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
                 h['video'], h['code'], h['resources'])
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
