// Namespace for serverpush
wingS.namespace("serverpull");

wingS.serverpull.updatePull = function(enabled, name, interval) {
    wingS.serverpull.enabled = enabled;
    if (!enabled && wingS.serverpull.timeout !== 0) {
        clearTimeout(wingS.serverpull.timeout);
    } else if (enabled) {
        if (wingS.serverpull.timeout !== 0) {
            clearTimeout(wingS.serverpull.timeout)
        }
        var timeout = setTimeout(function() {
            wingS.serverpull.pull(name, interval);
        }, interval);
        wingS.serverpull.timeout = timeout;
    }
};

wingS.serverpull.pull = function(name, interval) {
    console.debug("Pulling....");
    wingS.request.followLink(name, true, name, null, null);
    if (wingS.serverpull.enabled) {
        var timeout = setTimeout(function() {
            wingS.serverpull.pull(name, interval);
        }, interval);
        wingS.serverpull.timeout = timeout;
    }
};

$(document).ready(function() {
    wingS.serverpull.timeout = 0;
    wingS.serverpull.enabled = false;
});