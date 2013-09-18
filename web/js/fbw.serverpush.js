/**
 * This file encapsulates the client side JavaScript stuff that is needed for
 * the new server push infrastructure of wingS. For more details please visit:
 * 
 * https://github.com/Atmosphere/atmosphere/wiki/Understanding-JavaScript-functions
 * https://github.com/Atmosphere/atmosphere/wiki/jQuery.atmosphere.js-atmosphere.js-API
 */

// Create a dedicated namespace for the server push stuff
wingS.namespace("serverpush");

// This method opens the "persistent communication channel" to
// the server which enables the latter to push updates on demand.
wingS.serverpush.connect = function() {
    
    // Create a request object with the according properties
    var request = {
        url: document.location.href.substring(0, document.location.href.lastIndexOf("/")) + '/serverpush',
        contentType: "text/xml",
        transport: 'websocket',
        fallbackTransport: 'long-polling',
        trackMessageLength: false,
        shared: false,
        logLevel: 'debug'
    };

    request.onOpen = function(response) {
        console.debug("Server Push: onOpen()");
    };

    request.onTransportFailure = function(errorMsg, request) {
        console.debug("Server Push: onTransportFailure()");
        if (window.EventSource) {
            request.fallbackTransport = "sse";
        }
    };

    request.onMessage = function(response) {
        console.debug("Server Push: onMessage()");
        var exception = null;
        // Parse the response body as an XML fragment
        var responseXML = $($.parseXML(response.responseBody));
        // Iterate over each <update> node, ...
        responseXML.find("update").each(function() {
            // ... extract the actual update code
            var update = $(this).text();
            console.debug("--> Executing update: ", update);
            try {
                // ... and try to evaluate it.
                window.eval(update);
            }
            catch (e) {
                if (exception === null) {
                    exception = {
                        message: e.message,
                        detail: update
                    };
                }
            }
        });
        // Some basic exception handling
        if (exception !== null) {
            wingS.dialog.showExceptionDialog(exception);
        }
    };

    request.onClose = function(response) {
        console.debug("Server Push: onClose()");
    };

    // Store our communication channels for later usage
    wingS.serverpush.socket = $.atmosphere;
    wingS.serverpush.subsocket = wingS.serverpush.socket.subscribe(request);
    
    console.debug("Server Push: connected");
};

// This method closes the "persistent communication channel" to
// the server and prevents the latter from pushing any updates.
wingS.serverpush.disconnect = function() {
    if (wingS.serverpush.socket) {
        wingS.serverpush.socket.unsubscribe();
        console.debug("Server Push: disconnected");
    }
};

// Connect on document loaded
$(document).ready(function() {
    wingS.serverpush.connect();
});