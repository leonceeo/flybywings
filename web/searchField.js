function initSearchField() {
    $('#searchField').on("keyup", function(event) {
        search.currentValue = $('#searchField').val();
        setTimeout(function() {
            maybeHandleSearchEvent(search.currentValue);
        }, 300);
    });
    if ($('#searchField').val() !== search.currentValue) {
        $('#searchField').val(search.currentValue);
    }
    $('#searchField').focus().setCursorPosition($('#searchField').val().length);
}

function maybeHandleSearchEvent(oldValue) {
    if (search.currentValue === oldValue && search.currentValue !== search.submittedValue) {
        search.submittedValue = search.currentValue;
        wingS.request.followLink("searchField", true, "searchField", search.currentValue, null);
    }
}