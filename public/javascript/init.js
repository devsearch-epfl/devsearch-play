
SyntaxHighlighter.all()


$(function(){

    // Side bar
    $('.button-collapse').sideNav();

    // Spinning wheel on search button
    $('#search-btn').click(function(){
        $('#search-btn').hide();
        $('#search-spin').show()
    });

    function showFilters(){
        var toggle = $('#toggle-filters').children("a");
        var filters=  $(".language-filter");
        if($(".language-filter:first").is(":hidden")) {
            filters.show("slow");
            toggle.text("Clear");
        } else {
            filters.hide("slow");
            filters.children("input").prop( "checked", false );
            toggle.text("Advanced search");
        }

        return false;
    }

    function showMoreInfo(){
        var toggle = $('#toggle-more-info').children("a");
        var filters=  $(".more-info");
        if($(".more-info:first").is(":hidden")) {
            filters.show("slow");
            toggle.text("Clear");
        } else {
            filters.hide("slow");
            filters.children("input").prop( "checked", false );
            toggle.text("More info");
        }
        return false;
    }

    function showFeatures(){
        var toggle = $('#toggle-feature').children("a");
        var filters=  $(".show-feature");
        if($(".more-info:first").is(":hidden")) {
            filters.show("slow");
            toggle.text("Clear");
        } else {
            filters.hide("slow");
            filters.children("input").prop( "checked", false );
            toggle.text("More info");
        }
        return false;
    }

    // Show the filters
    $('#toggle-filters').children("a").click(showFilters);
    $('#toggle-more-info').children("a").click(showMoreInfo);
    $('#toggle-feature').children("a").click(showFeatures);

    // filters should be visible if a chechbox is selected on page load
    if($(".language-filter").children("input:checked").length > 0){
        showFilters();
    }

}); // end of document ready

