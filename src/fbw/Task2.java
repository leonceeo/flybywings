package fbw;

import fbw.serverpull.Puller;
import fbw.serverpush.Pushable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import org.wings.SBorderLayout;
import org.wings.SButtonGroup;
import org.wings.SFlowLayout;
import org.wings.SPanel;
import org.wings.SRadioButton;
import org.wings.STable;
import org.wings.SToggleButton;

/**
 * @author leon 
 */
public class Task2 extends SPanel {
    
    private static final String[] COLS = new String[] { "When", "Where" };
    
    private ExecutorService generatorThreadExecutor = Executors.newFixedThreadPool(1);
    private ScheduledExecutorService pushExecutor;
    
    private STable dataTable;
    private Puller puller;
    private TableModelGenerator tableModelGenerator = new TableModelGenerator();
    private TablePushable tablePushable = new TablePushable();
    private int pushPullInterval = 1000;
    private TableModel generatedTableModel;
    private boolean pullEnabled;
    
    public Task2() {
        setLayout(new SBorderLayout());
        SButtonGroup buttonGroup = new SButtonGroup();
        SRadioButton pullButton = new SRadioButton("Get data by polling");
        SRadioButton pushButton = new SRadioButton("Use server push to update data");
        buttonGroup.add(pullButton);
        buttonGroup.add(pushButton);
        pushButton.setSelected(true);
        pushButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setPullEnabled(false);
            }
        });
        pullButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setPullEnabled(true);
            }
        });
        SPanel buttonPanel = new SPanel(new SFlowLayout(SFlowLayout.HORIZONTAL, 10, 0));        // Add
        final String startText = "Start background generation thread";
        final String stopText = "Stop background generation thread";
        final SToggleButton toggleButton = new SToggleButton(startText);
        toggleButton.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if (toggleButton.isSelected()) {
                    toggleButton.setText(stopText);
                    startThread();
                } else {
                    toggleButton.setText(startText);
                    stopThread();
                }
            }
        });
        buttonPanel.add(pullButton);
        buttonPanel.add(pushButton);
        buttonPanel.add(toggleButton);
        // Also add the puller, which is an invisible component. We'll configure it with an interval of 1 second
        // Take note, that the puller should only be added once per frame. In a real word application, it probably should
        // be added in a central place
        puller = new Puller();
        puller.setInterval(pushPullInterval);
        // Disable the puller, we'll dynamically enable it when needed
        puller.setEnabled(false);
        buttonPanel.add(puller);
        add(buttonPanel, SBorderLayout.NORTH);
        dataTable = new STable();
        dataTable.setModel(new EmptyTableModel());
        add(dataTable, SBorderLayout.CENTER);
        // Start with server push
        setPullEnabled(false);
    }

    private void startThread() {
        tableModelGenerator.enabled = true;
        generatorThreadExecutor.submit(tableModelGenerator);
    }
    
    private void setPullEnabled(boolean enabled) {
        if (enabled) {
            if (pushExecutor != null && !pushExecutor.isShutdown()) {
                pushExecutor.shutdown();
            }
            puller.setEnabled(true);
        } else {
            // Disable the puller
            puller.setEnabled(false);
            // Create the push executor
            pushExecutor = Executors.newSingleThreadScheduledExecutor();
            // Execute a push on interval hearbeat
            pushExecutor.scheduleAtFixedRate(tablePushable, 100, pushPullInterval, TimeUnit.MILLISECONDS);
        }
    }
    
    private void stopThread() {
        tableModelGenerator.enabled = false;
    }
    
    private static class EmptyTableModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return 0;
        }

        @Override
        public int getColumnCount() {
            return COLS.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return null;
        }

        @Override
        public String getColumnName(int column) {
            return COLS[column];
        }
    }
    
    private static class GeneratedTableModel extends AbstractTableModel {

        private String when = new Date().toString();
        
        private int rowCount;

        public GeneratedTableModel(int rowCount) {
            this.rowCount = rowCount;
        }
        
        @Override
        public int getRowCount() {
            return rowCount;
        }

        @Override
        public int getColumnCount() {
            return COLS.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return when;
                case 1:
                default:
                    return "Place " + rowIndex;
            }
        }

        @Override
        public String getColumnName(int column) {
            return COLS[column];
        }
        
    }
    
    private class TablePushable extends Pushable {

        @Override
        protected void push() {
            if (generatedTableModel != null) {
                dataTable.setModel(generatedTableModel);
                generatedTableModel = null;
            }
        }
    }
    
    private class TableModelGenerator implements Runnable {
        private Random random = new Random();
        
        private boolean enabled = false;
        
        @Override
        public void run() {
            while (enabled) {
                // Sleep for a random amount between 1 and 3 seconds
                long sleepTime = random.nextInt(3) * 1000 + 1 * 1000;
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Task2.class.getName()).log(Level.SEVERE, null, ex);
                }
                // Generate between 4 and 25 rows
                int rowCount = random.nextInt(22) + 4;
                
                generatedTableModel = new GeneratedTableModel(rowCount);
                
                if (pushExecutor == null || pushExecutor.isShutdown()) {
                    // Only set the model if we're running in pull mode. Push will be handled
                    // by the Pushable
                   dataTable.setModel(generatedTableModel);
                   generatedTableModel = null;
                }
            }
        }
    }
    
}
