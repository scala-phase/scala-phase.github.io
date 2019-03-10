Jekyll content for *scala-phase.org*, the web site of the Philly Area
Scala Enthusiasts (PHASE).

## Main content

The main content is in `index.html`. **BUT:** Do _not_ edit the section
between `<!-- @start talks@ -->` and `<!-- @end talks@ -->`. That section
is updated by the [build](#building) process.

## Adding talks

The talks are specified in `talks.yml`. The `talks` element is a list
of objects with the following fields:

- `title` (required): The talk title
- `speaker` (required): The speaker
- `date` (required): The date of the talk, in `yyyy-mm-dd` format.
- `meeting_link` (required): The URL of the talk's Meetup page.
- `video` (optional): URL of the video, if there is one.
- `slides` (optional): Contains two sub-fields:
    - `link`: URL of the presentation, if available.
    - `comment` (optional): One-line comment about slides (e.g., "PDF", "HTML", 
      etc.)
- `code` (optional): URL of code (e.g., GitHub repo) associated with the talk,
  if available.

Once you've updated `talks.yml`, you _must_ [rebuild](#building).

## Tweaking CSS

**Don't** edit the `assets/css/main.css` that was unpacked with the
[Pixelarity](https://pixelarity.com) theme. Instead, override styles by
editing the `css/custom.css` file.

## Building

To build, you need `ruby` (2.6), `rake`, and `bundle`. Run `bundle install`,
to get the rest of the required gems. Then, run:

```
rake build
```

to build the site with Jekyll. You _cannot_ just run `jekyll build`, because
the `Rakefile` contains some preprocessing tasks that must run before `jekyll`
is invoked.

After building, check in any changed files (such as the `index.html` file). 

## Publishing

Before publishing, verify that everything looks good. Run

```
rake serve
```

to build the site and run `jekyll` in server mode. Then, connect your browser
to `http://localhost:4000` and give the site the once-over.

The site is hosted on [GitHub Pages](https://pages.github.com/). To publish, 
just push your changes to the GitHub repo (or issue a PR from your fork, and 
one of the maintainers will do it.)



