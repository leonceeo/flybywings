var search = new Object();
search.currentValue = '';
search.submittedValue = '';

$.fn.setCursorPosition = function(pos) {
  this.each(function(index, elem) {
    if (elem.setSelectionRange) {
      elem.setSelectionRange(pos, pos);
    } else if (elem.createTextRange) {
      var range = elem.createTextRange();
      range.collapse(true);
      range.moveEnd('character', pos);
      range.moveStart('character', pos);
      range.select();
    }
  });
  return this;
};

function initSearchField(name) {
    var fieldSelector = '#' + name;
    $(fieldSelector).on("keyup", function(event) {
        search.currentValue = $(fieldSelector).val();
        setTimeout(function() {
            maybeHandleSearchEvent(search.currentValue, name);
        }, 300);
    });
    if ($(fieldSelector).val() !== search.currentValue) {
        $(fieldSelector).val(search.currentValue);
    }
    $(fieldSelector).focus().setCursorPosition($(fieldSelector).val().length);
}

function maybeHandleSearchEvent(oldValue, name) {
    if (search.currentValue === oldValue && search.currentValue !== search.submittedValue) {
        search.submittedValue = search.currentValue;
        wingS.request.followLink(name, true, name, search.currentValue, null);
    }
}