package fbw;

import org.wings.LowLevelEventListener;
import org.wings.SBorderLayout;
import org.wings.SDimension;
import org.wings.SLabel;
import org.wings.SPanel;
import org.wings.STextField;
import org.wings.event.SRenderEvent;
import org.wings.event.SRenderListener;
import org.wings.script.JavaScriptListener;
import org.wings.session.SessionManager;

/**
 * @author leon
 */
public class Task1 extends SPanel {
    private final SearchField searchField;
    private final SLabel contentLabel;

    public Task1() { 
        SBorderLayout borderLayout = new SBorderLayout();
        setLayout(borderLayout);
        borderLayout.setVgap(10);
        // Our search field
        searchField = new SearchField();
        searchField.setName("searchField");
        searchField.setPreferredSize(SDimension.FULLWIDTH);
        add(searchField, SBorderLayout.NORTH);
        // This label updates with the search field.
        contentLabel = new SLabel();
        contentLabel.setText("You've searched for: " + searchField.getText());
        add(contentLabel, SBorderLayout.CENTER);
    }
    
    
    
    private class SearchField extends STextField implements LowLevelEventListener {

        public SearchField() {
            addRenderListener(new SRenderListener() {

                @Override
                public void startRendering(SRenderEvent renderEvent) {
                }

                @Override
                public void doneRendering(SRenderEvent renderEvent) {
                    // Every time the text field is rendered (for example a page refresh) we need to include set it up on the client. This
                    // is done via a javascript listener
                    SessionManager.getSession().getScriptManager().addScriptListener(new JavaScriptListener(null, null, "initSearchField('searchField');"));
                }
            });
        }

        /**
         * The asynchronous "search events" triggered by key-up events in the browser may send
         * an already outdated epoch. With epoch check enabled, this would trigger a complete frame 
         * reload. Thus, we're disabling epoch check.
         * @return 
         */
        @Override
        public boolean isEpochCheckEnabled() {
            return false;
        }
        
        @Override
        public void processLowLevelEvent(String action, String[] values) {
            // Don't call super. We don't want the text the be updated. If it's updated
            // on the server too, this update is sent to the client where the contents of the text field may
            // have already changed.
            contentLabel.setText(values[0].toUpperCase());
        }
        
        
    }
    
}
