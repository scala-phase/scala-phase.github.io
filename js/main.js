(function($) {

  $(document).ready(function() {
    // Animated scrolling, with easing.
    // See http://stackoverflow.com/questions/24267628
    // and http://jqueryui.com/resources/demos/effect/easing.html

    function scrollTo(targetSelector) {
      var target = $(targetSelector);
      var topOffset = target.offset().top;
      $('html, body').animate(
        {scrollTop: topOffset},
        1000,
        'easeInOutQuint'
      );
    };

    // Wire all .navlink items to animated scroll.

    $(".navlink").click(function(e) {
      e.preventDefault();
      var href = $(this).attr('href');
      scrollTo(href);
      window.location.hash = href.replace('#', '');
    });

    function resizeSections() {
      $(".section").css('height', window.innerHeight);
      /* $(".section").css('width', window.innerWidth); */
    };

    resizeSections();

    $(".resources-link").click(function(e) {
      var id = $(this).data("resources-id")
      var closed = ($(this).data("state") === "closed");
      var elem = $("#" + id);
      if (closed) {
        $(this).find(".pointer-closed").hide();
        $(this).find(".pointer-open").show();
        $(this).data("state", "open");
        elem.show();
      }
      else {
        $(this).find(".pointer-open").hide();
        $(this).find(".pointer-closed").show();
        $(this).data("state", "closed");
        elem.hide();
      }
    });

  });

}(jQuery));
