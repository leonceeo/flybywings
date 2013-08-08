package fbw;

import org.wings.SBorderLayout;
import org.wings.SButton;
import org.wings.SFrame;
import org.wings.SPanel;

/**
 * @author leon
 */
public class Example {

    public Example() {
        SFrame frame = new SFrame();
        frame.setTitle("Pretty Simple Wings Examples");
        SPanel panel = new SPanel(new SBorderLayout());
        panel.add(new SButton("Test"), SBorderLayout.CENTER);
        frame.setContentPane(panel);
        frame.show();
    }
    
}
