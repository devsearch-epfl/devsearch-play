
SyntaxHighlighter.all()


$(function(){

    // Side bar
    $('.button-collapse').sideNav();

    // Spinning wheel on search button
    $('#search-btn').click(function(){
        $('#search-btn').hide()
        $('#search-spin').show()
    })

    // Toggle search
    $('#toggle-filters').children("a").click(function()
    {
        if($(".language-filter:first").is(":hidden")) {
            $(".language-filter").show("slow")
            $('#toggle-filters').children("a").text("Hide")
        } else {
            $(".language-filter").hide("slow")
            $('#toggle-filters').children("a").text("Advanced search")
        }

        return false;
    });

}); // end of document ready

