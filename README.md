Jekyll content for *scala-phase.org*, the web site of the Philly Area
Scala Enthusiasts (PHASE).

## Main content

The main content is in `index-template.html`. Do _not_ edit `index.html`.
It's generated.

If you change `index-template.html`, you _must_ [rebuild](#building).

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

## Building

To build, you need `ruby` (2.6), `rake`, and `bundle`. Run `bundle install`,
to get the required gems. Then, run:

```
rake deploy
```

to build and push the site.

If you don't want to install the prequisites locally, you can use
[this Docker image](https://github.com/bmc/docker/tree/master/staticsite),
which handles it all for you.

