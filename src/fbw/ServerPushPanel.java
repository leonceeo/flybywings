package fbw;

import fbw.serverpush.Pushable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.SpinnerNumberModel;
import org.wings.SButton;
import org.wings.SContainer;
import org.wings.SFlowDownLayout;
import org.wings.SFlowLayout;
import org.wings.SLabel;
import org.wings.SPanel;
import org.wings.SSpinner;
import org.wings.STextField;

/**
 * @author stephan
 */
public class ServerPushPanel extends SPanel {

    private final STextField updateTextField;
    private final SButton updateButton;
    private final SSpinner pushSpinner;
    private final SButton pushButton;
    private final SLabel label;
    private ScheduledExecutorService scheduler;
    private Pushable pushable;

    public ServerPushPanel() {
        pushable = new UpdateLabelText().preventEpochUpdates();

        updateTextField = new STextField("Yippie ya yeah!");
        updateButton = new SButton("Update label");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OnUpdateButtonClicked();
            }
        });

        pushSpinner = new SSpinner(new SpinnerNumberModel(1000, 100, 10000, 100));
        pushButton = new SButton("Start pushing");
        pushButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OnPushButtonClicked();
            }
        });

        label = new SLabel("Please click one of the buttons above!");

        SContainer updateContainer = new SContainer(new SFlowLayout());
        updateContainer.add(updateTextField);
        updateContainer.add(updateButton);

        SContainer pushContainer = new SContainer(new SFlowLayout());
        pushContainer.add(pushSpinner);
        pushContainer.add(pushButton);

        setLayout(new SFlowDownLayout());
        add(updateContainer);
        add(pushContainer);
        add(label);
    }

    private void OnUpdateButtonClicked() {
        label.setText("Label has been changed by client: " + updateTextField.getText());
    }

    private void OnPushButtonClicked() {
        if (scheduler == null) {
            pushSpinner.setEnabled(false);
            pushButton.setText("Stop pushing");
            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(pushable, 100, (int) pushSpinner.getValue(), TimeUnit.MILLISECONDS);
        } else {
            pushSpinner.setEnabled(true);
            pushButton.setText("Start pushing");
            scheduler.shutdown();
            scheduler = null;
        }
    }

    private class UpdateLabelText extends Pushable {

        @Override
        protected void push() {
            label.setText("Label has been changed by server: " + System.currentTimeMillis());
        }
    }
}
