package fbw.serverpush;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.atmosphere.cpr.BroadcasterFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author stephan
 */
public class AtmosphereSessionListener implements HttpSessionListener {

    private static final Logger LOG = LoggerFactory.getLogger(AtmosphereSessionListener.class);

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        String broadcasterId = event.getSession().getId();
        LOG.debug("Creating broadcaster: {}", broadcasterId);
        BroadcasterFactory.getDefault().lookup(broadcasterId, true);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        String broadcasterId = event.getSession().getId();
        LOG.debug("Removing broadcaster: {}", broadcasterId);
        BroadcasterFactory.getDefault().remove(broadcasterId);
    }
}
