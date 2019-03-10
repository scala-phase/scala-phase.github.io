#                                                                 -*- ruby -*-

require 'rubygems'
require 'git'
require 'tmpdir'
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

LESS_DIR            = 'less'
CUSTOM_LESS         = "#{LESS_DIR}/custom.less"
CUSTOM_CSS_DIR      = "css"
CUSTOM_CSS          = "#{CUSTOM_CSS_DIR}/custom.css"
TALKS_YAML          = 'talks.yml'
TALKS_TEMPLATE      = 'talks.mustache'
TALKS_HTML          = 'talks.html'

# ---------------------------------------------------------------------------
# Main tasks
# ---------------------------------------------------------------------------

task :default => :generate

desc "Format the site."
task :generate => :dist

desc "Generate and deploy the site."
task :gen_deploy => :deploy

task :index => [:talkshtml, :css] do |t|
  puts "Creating index.html"
  File.open("index.html", mode: "w") do |index|
    File.open("index-template.html").each do |line|
      if line.strip == '{{talks}}'
        File.open(TALKS_HTML).each do |t|
          index.puts(t)
        end
      else
        index.puts(line)
      end
    end
  end

  rm_f TALKS_HTML
end

task :dist => [:index] do |t|
  rm_rf 'dist'
  mkdir_p 'dist'
  %w{assets css images talks scala-fundamentals}.each do |dir|
    cp_r dir, 'dist'
  end
  Dir.glob('goo*.html').each do |f|
    cp f, 'dist'
  end
  %w{index.html keybase.txt}.each do |f|
    cp f, 'dist'
  end
end

task :gen => :generate

task :talks => :talkshtml do
  rm_rf 'talks/slick-2015-03-19'
  git_clone 'https://github.com/bmc/slick-presentation-2015-03-19.git', 'talks', 'slick-2015-03-19'
end

task :talkshtml => TALKS_HTML

file TALKS_HTML => [TALKS_YAML, TALKS_TEMPLATE, 'Rakefile'] do
  puts "Rebuilding #{TALKS_HTML}"
  convert_talks
end

task :css_clean do
  rm_f CUSTOM_CSS_DIR
end

task :less => :css
task :css => CUSTOM_CSS

file CUSTOM_CSS => [CUSTOM_LESS] do
  mkdir_p CUSTOM_CSS_DIR
  sh "lessc #{CUSTOM_LESS} #{CUSTOM_CSS}"
end

task :gen_deploy => [:generate, :deploy]

desc "Deploy the site to the VPS."
task :deploy => :dist do
  Dir.chdir 'dist' do
    RSYNC_ARGS = [
        'rsync',
        '-avuz',
        '--delete',
        '--exclude', '/.git',
        '--exclude', '/tmp',
        '--exclude', '*.iml',
        '--exclude', '/.idea',
        '.', 'bmc@yore.ardentex.com:/var/www/scala-phase.org/html'
    ]
    sh *RSYNC_ARGS
  end
end

task :clean do
  rm_rf "dist"
  rm_rf "css"
  rm_rf "talks.html"
  rm_rf "index.html"
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
