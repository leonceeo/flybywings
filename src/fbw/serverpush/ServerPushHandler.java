package fbw.serverpush;

import java.io.IOException;

import org.atmosphere.config.service.AtmosphereHandlerService;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.handler.OnMessage;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.HeartbeatInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ServerPushHandler is our implementation of an AtmosphereHandler which in
 * turn is the central concept of an Atmosphere Framework application. Because
 * our intend is to delegate as much as possible of the connection life cycle to
 * the Atmosphere Framework itself, we do not implement the AtmosphereHandler
 * interface directly but extend from some of the framework's ready-to-use
 * components: OnMessage<T> and AbstractReflectorAtmosphereHandler. While the
 * first simplifies the handling of the suspend/resume/disconnect and broadcast
 * operation, the latter provides an implementation to inspect the framework's
 * event in onStateChange() and to decide if the underlying connection must be
 * resumed. To further reduce the lines of code, we additionally make use of the
 * AtmosphereInterceptor mechanism: The AtmosphereResourceLifecycleInterceptor
 * automatically suspends the intercepted AtmosphereResource and takes care of
 * managing the response's state (flushing, resuming, etc.) when a broadcaster's
 * broadcast() method has been invoked. The HeartbeatInterceptor transparently
 * keeps the suspended connection active by sending some bytes between the
 * client and the server. This helps us in situations where a proxy or firewall
 * doesn't allow a connection to stay inactive for a longer period of time.
 *
 * What remains to do for the ServerPushHandler is pretty simple: we need to
 * create, store and attach a session based broadcaster to the
 * AtmosphereResource in onOpen() and write back the wingS-specific updates in
 * onMessage().
 *
 * In order to fully understand what is going on here, a deeper understanding of
 * the Atmosphere framework is indispensable. Here are some of the main
 * resources:
 *
 * https://github.com/Atmosphere/atmosphere/wiki
 * https://github.com/Atmosphere/atmosphere/wiki/Supported-WebServers-and-Browsers
 * https://github.com/Atmosphere/atmosphere/wiki/Understanding-AtmosphereResource
 * https://github.com/Atmosphere/atmosphere/wiki/Understanding-AtmosphereHandler
 * https://github.com/Atmosphere/atmosphere/wiki/Getting-Started-with-AtmosphereHandler,-WebSocket-and-Long-Polling
 * https://github.com/Atmosphere/atmosphere/wiki/Understanding-Broadcaster
 * https://github.com/Atmosphere/atmosphere/wiki/Understanding-AtmosphereInterceptor
 * https://github.com/Atmosphere/atmosphere/wiki/Understanding-JavaScript-functions
 * https://github.com/Atmosphere/atmosphere/wiki/jQuery.atmosphere.js-atmosphere.js-API
 *
 * http://async-io.org/
 * http://async-io.org/tutorial.html
 *
 * @author Stephan Schuster
 */
@AtmosphereHandlerService(
        path = "/example/serverpush/*", supportSession = true,
        interceptors = {AtmosphereResourceLifecycleInterceptor.class, HeartbeatInterceptor.class})
public class ServerPushHandler extends OnMessage<Pushable.Message> {

    private static final Logger LOG = LoggerFactory.getLogger(ServerPushHandler.class);

    @Override
    public void onOpen(AtmosphereResource resource) throws IOException {
        // Retrieve the ID of the HTTP session created by wingS or Atmosphere
        String broadcasterId = resource.getRequest().getSession(false).getId();
        LOG.debug("Attaching broadcaster: {}", broadcasterId);
        // Try to find the according session broadcaster or create and store it if necessary
        Broadcaster broadcaster = BroadcasterFactory.getDefault().lookup(broadcasterId, true);
        // Associate the broadcaster to the suspended AtmosphereResource for later broadcasts
        resource.setBroadcaster(broadcaster);
    }

    @Override
    public void onMessage(AtmosphereResponse response, Pushable.Message message) throws IOException {
        final String contentType = message.getContentType();
        final String content = message.getContent();
        LOG.debug("Pushing update message: {}\n{}", contentType, content);
        // Push the wingS-specific updates encapsulated in a Pushable.Message
        // back to the client by using the AtmosphereResponse's write() method
        response.setContentType(contentType);
        response.write(content);
    }
}
