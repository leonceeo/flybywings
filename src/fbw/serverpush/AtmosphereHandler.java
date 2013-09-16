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
 * @author stephan
 */
@AtmosphereHandlerService(
        path = "/example/serverpush/*",
        interceptors = {AtmosphereResourceLifecycleInterceptor.class, HeartbeatInterceptor.class})
public class AtmosphereHandler extends OnMessage<Pushable.Message> {

    private static final Logger LOG = LoggerFactory.getLogger(AtmosphereHandler.class);

    @Override
    public void onOpen(AtmosphereResource resource) throws IOException {
        String broadcasterId = resource.getRequest().getSession(false).getId();
        LOG.debug("Attaching broadcaster: {}", broadcasterId);
        Broadcaster broadcaster = BroadcasterFactory.getDefault().lookup(broadcasterId);
        resource.setBroadcaster(broadcaster);
    }

    @Override
    public void onMessage(AtmosphereResponse response, Pushable.Message message) throws IOException {
        final String contentType = message.getContentType();
        final String content = message.getContent();
        LOG.debug("Pushing update message: {}\n{}", contentType, content);
        response.setContentType(contentType);
        response.write(content);
    }
}
