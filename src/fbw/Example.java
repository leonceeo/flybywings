package fbw;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.wings.SBorderLayout;
import org.wings.SButton;
import org.wings.SFlowLayout;
import org.wings.SFrame;
import org.wings.SPanel;

/**
 * @author leon
 */
public class Example {

    public Example() {
        final SFrame frame = new SFrame();
        frame.setTitle("Pretty Simple Wings Examples");
        SPanel panel = new SPanel(new SFlowLayout());
        SButton btn = new SButton("Change frame title");
        btn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setTitle("A new title");
            }
        });
        panel.add(btn);
        frame.getContentPane().add(panel, SBorderLayout.CENTER);
        frame.show();
    }
    
}
