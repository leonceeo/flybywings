package fbw;

import org.wings.SBorderLayout;
import org.wings.SDimension;
import org.wings.SFrame;
import org.wings.STabbedPane;
import org.wings.header.JavaScriptHeader;

/**
 * This is the main class of our demo application which shows the exemplary
 * solutions to the problems mentioned by Clemens Ott in our conference call on
 * the 6th of August 2013. In detail, our tasks have been the following:
 *
 * TASK 1: Search field focusing problem ... [TODO Leon]
 *
 * TASK 2: Asynchronous form generation ... [TODO Leon]
 *
 * TASK 3: How to retrieve partial component updates in wingS in preparation of
 * a future server push integration scenario.
 *
 * To implement server push in wingS was indeed desired but actually not part of
 * our task due to it's complex nature and our limited amount of time. However,
 * during development we thought that just showing you how to retrieve partial
 * component updates won't take you any further. That's why we decided to invest
 * more time here and to integrate the requested partial component updates in a
 * real server push infrastructure for wingS based on the Atmosphere framework.
 * Our server push solution explicitly doesn't modify any sources of the wingS
 * framework itself. On the one hand this guarantees that you're current
 * application works as it has done before. On the other hand this decouples you
 * from an otherwise hand-made wingS build.
 *
 * We hope that our effort finds your agreement.
 *
 * TASK 4: Summarize the possible alternatives for choosing a Java web framework
 * as a future replacement of wingS and briefly mention how to deal with the IXP
 * platform's demand for a state of the art server push infrastructure.
 *
 * @author Leon Chiver, Stephan Schuster
 */
public class Example {

    public Example() {
        final SFrame frame = new SFrame("Fly By Wings");

        frame.addHeader(new JavaScriptHeader("../js/jquery.js"));
        frame.addHeader(new JavaScriptHeader("../js/jquery.atmosphere.js"));
        frame.addHeader(new JavaScriptHeader("../js/fbw.search.js"));
        frame.addHeader(new JavaScriptHeader("../js/fbw.serverpush.js"));

        STabbedPane examplesPanel = new STabbedPane();
        examplesPanel.setPreferredSize(SDimension.FULLAREA);
        examplesPanel.addTab("Task 1: Search field focusing problem", new Task1());
        examplesPanel.addTab("Task 2: Asynchronous form generation", new Task2());
        examplesPanel.addTab("Task 3: Partial updates via server push", new Task3());
        examplesPanel.addTab("Task 4: Choosing an alternative for wingS", new Task4());

        frame.getContentPane().setLayout(new SBorderLayout());
        frame.getContentPane().add(examplesPanel, SBorderLayout.CENTER);
        frame.show();
    }
}
