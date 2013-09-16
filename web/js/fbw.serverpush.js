wingS.namespace("serverpush");

wingS.serverpush.connect = function() {

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
        var responseXML = $($.parseXML(response.responseBody));
        responseXML.find("update").each(function() {
            var update = $(this).text();
            console.debug("--> Executing update: ", update);
            try {
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
        if (exception !== null) {
            wingS.dialog.showExceptionDialog(exception);
        }
    };

    request.onClose = function(response) {
        console.debug("Server Push: onClose()");
    };

    wingS.serverpush.socket = $.atmosphere;
    wingS.serverpush.subsocket = wingS.serverpush.socket.subscribe(request);
    
    console.debug("Server Push: connected");
};

wingS.serverpush.disconnect = function() {
    if (wingS.serverpush.socket) {
        wingS.serverpush.socket.unsubscribe();
        console.debug("Server Push: disconnected");
    }
};

$(document).ready(function() {
    wingS.serverpush.connect();
});