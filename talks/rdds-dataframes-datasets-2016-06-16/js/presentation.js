$(document).ready(function() {
  // Find all <pre> <code> blocks, get the contents, and remove leading
  // and trailing blank lines.

  $("pre").children("code").each(function() {
    var html = $(this).html();
    var lines = html.split("\n");
    var html2 = _.filter(lines, function(line) {
      return line.trim().length > 0;
    });

    $(this).html(_.join(html2, "\n"));
  });


  // Initialize Reveal.js
  Reveal.initialize({
    slideNumber:  true,
    progress:     false,
    center:       false,
    history:      true,
    transition:   'slide',
    controls:     false,
    width:        2560, // Coupled to presentation.less
    height:       1600, // Coupled to presentation.less
    // Plugins
    dependencies: [
      {
        src:      'js/highlightjs/highlight.pack.js',
        async:    true,
        callback: function() { hljs.initHighlightingOnLoad(); }
      },
      {
        src:   'js/reveal.js/plugin/zoom-js/zoom.js',
        async: true
      },
      {
        src:   'js/reveal.js/plugin/notes/notes.js',
        async: true
      },
      { src: 'js/reveal.js/plugin/markdown/marked.js',
        condition: function() {
          return !!document.querySelector( '[data-markdown]' );
        }
      },
      { src: 'js/reveal.js/plugin/markdown/markdown.js',
        condition: function() {
          return !!document.querySelector( '[data-markdown]' );
        }
      }
    ]
  });
});
