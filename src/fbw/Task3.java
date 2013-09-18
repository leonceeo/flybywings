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
 * This demo shows how to use the new server push infrastructure for wingS.
 *
 * The actual scenario is the following: the text of "label" can be modified by
 * either a client initiated request or by an active server push. If the user
 * clicks the "updateButton", the text of "label" is set to the text of
 * "updateTextField" using the traditional wingS request-response-cycle. In case
 * the user clicks the "pushButton", a separate thread is started (or stopped)
 * on the server side which pushes partial text updates for "label" to the
 * client. This is done by means of the newly introduced Pushable pattern and a
 * scheduler executing the Pushable at a fixed rate which is defined by the
 * value of "pushSpinner" in milliseconds. First and foremost this demo is a
 * proof of concept that partial component updates can actively be pushed to the
 * client. Additionally it shows that concurrent state changes (for one and the
 * same component) can be triggered by client and server initiated requests or
 * pushes without interfering each other (means synchronization works as well).
 *
 * Since under the hood new server push infrastructure for wingS utilizes the
 * Atmosphere framework, you have to make sure you're using a compatible browser
 * and web server to run the demo. Please see this document for further details:
 *
 * https://github.com/Atmosphere/atmosphere/wiki/Supported-WebServers-and-Browsers
 *
 * @author Stephan Schuster
 */
public class Task3 extends SPanel {

    private final STextField updateTextField;
    private final SButton updateButton;
    private final SSpinner pushSpinner;
    private final SButton pushButton;
    private final SLabel label;
    private ScheduledExecutorService scheduler;
    private Pushable pushable;

    public Task3() {
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
