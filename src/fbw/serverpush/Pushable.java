package fbw.serverpush;

import java.io.IOException;
import java.util.Set;
import javax.servlet.http.HttpSession;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wings.LowLevelEventListener;
import org.wings.ReloadManager;
import org.wings.SComponent;
import org.wings.SForm;
import org.wings.SFrame;
import org.wings.io.StringBuilderDevice;
import org.wings.resource.UpdateResource;
import org.wings.session.Session;
import org.wings.session.SessionManager;

/**
 * @author stephan
 */
public abstract class Pushable implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(Pushable.class);
    private final Session session;
    private final HttpSession httpSession;
    private boolean preventFormEvents;
    private boolean preventEpochUpdates;

    public Pushable() {
        this(null);
    }

    public Pushable(Session session) {
        if (session == null) {
            session = SessionManager.getSession();
            if (session == null) {
                throw new IllegalStateException("No session available");
            }
        }
        this.session = session;
        this.httpSession = session.getServletRequest().getSession(false);
    }

    public Pushable preventFormEvents() {
        this.preventFormEvents = true;
        return this;
    }

    public Pushable preventEpochUpdates() {
        this.preventEpochUpdates = true;
        return this;
    }

    @Override
    public final void run() {
        try {
            if (SessionManager.getSession() != null) {
                runInClientRequestCycle();
            } else {
                synchronized (httpSession) {
                    try {
                        SessionManager.setSession(session);
                        runInServerPushThread();
                    } finally {
                        SessionManager.removeSession();
                    }
                }
            }
        } catch (Throwable ex) {
            LOG.error("Error while running pushable", ex);
        }
    }

    protected abstract void push();

    private void runInClientRequestCycle() throws Exception {
        push();
    }

    private void runInServerPushThread() throws Exception {
        ReloadManager reloadManager = session.getReloadManager();
        reloadManager.setUpdateMode(true);

        push();

        if (!preventFormEvents) {
            SForm.clearArmedComponents();
            for (SComponent dirtyComponent : reloadManager.getDirtyComponents()) {
                if (dirtyComponent instanceof LowLevelEventListener) {
                    SForm.addArmedComponent((LowLevelEventListener) dirtyComponent);
                }
            }
            SForm.fireEvents();
        }

        reloadManager.notifyCGs();
        if (!preventEpochUpdates) {
            reloadManager.invalidateFrames();
        }

        Message message = getUpdateMessage();
        if (message != null) {
            String broadcasterId = httpSession.getId();
            LOG.debug("Invoking broadcaster: {}", broadcasterId);
            Broadcaster broadcaster = BroadcasterFactory.getDefault().lookup(broadcasterId);
            broadcaster.broadcast(message).get();

            reloadManager.clear();
        }
    }

    private Message getUpdateMessage() {
        UpdateResource updateResource = getUpdateResource();
        if (updateResource != null) {
            try {
                StringBuilderDevice updates = new StringBuilderDevice();
                updateResource.write(updates);
                return new Message(updates.toString(), updateResource.getMimeType());
            } catch (IOException ex) {
                LOG.error("Error while creating update message", ex);
            }
        }
        return null;
    }

    private UpdateResource getUpdateResource() {
        Set<SFrame> frames = session.getFrames();
        if (frames.isEmpty()) {
            return null;
        }
        SFrame frame = frames.iterator().next();
        while (frame.getParent() != null) {
            frame = (SFrame) frame.getParent();
        }
        return (UpdateResource) frame.getDynamicResource(UpdateResource.class);
    }

    public static class Message {

        private final String content;
        private final String contentType;

        public Message(String content, String contentType) {
            this.content = content;
            this.contentType = contentType;
        }

        public String getContent() {
            return content;
        }

        public String getContentType() {
            return contentType;
        }
    }
}
