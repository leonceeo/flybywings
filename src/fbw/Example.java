package fbw;

import org.wings.SBorderLayout;
import org.wings.SDimension;
import org.wings.SFrame;
import org.wings.STabbedPane;
import org.wings.URLResource;
import org.wings.header.Script;
import org.wings.resource.DefaultURLResource;

/**
 * @author leon
 */
public class Example { 

    public Example() {
        final SFrame frame = new SFrame();
        frame.addHeader(new Script("text/javascript", new DefaultURLResource("../jquery-1.10.2.min.js")));
        frame.addHeader(new Script("text/javascript", new DefaultURLResource("../fbw.js")));
        frame.addHeader(new Script("text/javascript", new DefaultURLResource("../searchField.js")));
        STabbedPane examplesPanel = new STabbedPane();
        examplesPanel.setPreferredSize(SDimension.FULLAREA);
        frame.getContentPane().setLayout(new SBorderLayout());
        frame.getContentPane().add(examplesPanel, SBorderLayout.CENTER);
        examplesPanel.addTab("Search field", new SearchPanel());
        examplesPanel.addTab("Async component generation", new AsyncComponentGenerationPanel());
        frame.show();
    }
    
}
