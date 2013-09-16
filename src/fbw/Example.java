package fbw;

import org.wings.SBorderLayout;
import org.wings.SDimension;
import org.wings.SFrame;
import org.wings.STabbedPane;
import org.wings.header.Script;
import org.wings.resource.DefaultURLResource;

/**
 * @author leon, stephan
 */
public class Example {

    public Example() {
        final SFrame frame = new SFrame("Fly By Wings");
        frame.addHeader(new Script("text/javascript", new DefaultURLResource("../js/jquery.js")));
        frame.addHeader(new Script("text/javascript", new DefaultURLResource("../js/jquery.atmosphere.js")));
        frame.addHeader(new Script("text/javascript", new DefaultURLResource("../js/fbw.search.js")));
        frame.addHeader(new Script("text/javascript", new DefaultURLResource("../js/fbw.serverpush.js")));

        STabbedPane examplesPanel = new STabbedPane();
        examplesPanel.setPreferredSize(SDimension.FULLAREA);
        examplesPanel.addTab("Search Field", new SearchPanel());
        examplesPanel.addTab("Async Component Generation", new AsyncComponentGenerationPanel());
        examplesPanel.addTab("Server Push", new ServerPushPanel());

        frame.getContentPane().setLayout(new SBorderLayout());
        frame.getContentPane().add(examplesPanel, SBorderLayout.CENTER);
        frame.show();
    }
}
