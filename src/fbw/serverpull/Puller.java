package fbw.serverpull;

import java.io.IOException;
import org.wings.SComponent;
import org.wings.event.SRenderEvent;
import org.wings.event.SRenderListener;
import org.wings.io.Device;
import org.wings.plaf.css.AbstractComponentCG;
import org.wings.script.JavaScriptListener;
import org.wings.session.SessionManager;

/**
 * A component which once added to the component hierarchy will execute pull requests.
 * There should always be only one puller per frame.
 * @author leon
 */
public class Puller extends SComponent {
    
    private long interval = 1000;

    public Puller() {
        setCG(new AbstractComponentCG() {

            @Override
            public void writeInternal(Device device, SComponent component) throws IOException {
                // Write an invisible span
                device.print("<span id='" + component.getName() + "' style='display:none'></span>");
            }
        });
        addRenderListener(new SRenderListener() {

            @Override
            public void startRendering(SRenderEvent renderEvent) {
            }

            @Override
            public void doneRendering(SRenderEvent renderEvent) {
                // When rendered (first time the frame is shown, frame reload, interval change, enabled state change) we will include the 
                // corresponding javascript
                SessionManager.getSession().getScriptManager().addScriptListener(new JavaScriptListener(null, null, "wingS.serverpull.updatePull(" 
                        + Boolean.toString(isEnabled()) + ", '" + getName() + "', " + interval + ")"));
            }
        });
    }
    
    public void setInterval(long interval) {
        if (this.interval == interval) {
            return;
        }
        this.interval = interval;
        reload();
    }

    public long getInterval() {
        return interval;
    }

}
