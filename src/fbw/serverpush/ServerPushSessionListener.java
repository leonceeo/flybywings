package fbw.serverpush;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.atmosphere.cpr.BroadcasterFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The AtmosphereSessionListener implements the HttpSessionListener interface
 * and therefore needs to be referenced in the web.xml as listener. Its one and
 * only task is to remove the broadcaster associated with destroyed sessions.
 *
 * @author Stephan Schuster
 */
public class ServerPushSessionListener implements HttpSessionListener {

    private static final Logger LOG = LoggerFactory.getLogger(ServerPushSessionListener.class);

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        // Nothing to do here. We'll create the broadcaster for
        // this session on demand. That is, when a client opens
        // a server push channel: see AtmosphereHandler.onOpen()
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        // Remove the broadcaster associated with the destroyed
        // session from the application. The broadcaster has been
        // created in AtmosphereHandler.onOpen() and been stored
        // in BroadcasterFactory under the according session ID.
        String broadcasterId = event.getSession().getId();
        LOG.debug("Removing broadcaster: {}", broadcasterId);
        BroadcasterFactory.getDefault().remove(broadcasterId);
    }
}
