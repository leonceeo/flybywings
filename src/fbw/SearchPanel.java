package fbw;

import java.util.Locale;
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
public class SearchPanel extends SPanel {
    private final SearchField searchField;
    private final SLabel contentLabel;

    public SearchPanel() { 
        SBorderLayout borderLayout = new SBorderLayout();
        setLayout(borderLayout);
        borderLayout.setVgap(10);
        searchField = new SearchField();
        searchField.setName("searchField");
        searchField.setPreferredSize(SDimension.FULLWIDTH);
        add(searchField, SBorderLayout.NORTH);
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
                    SessionManager.getSession().getScriptManager().addScriptListener(new JavaScriptListener(null, null, "initSearchField();"));
                }
            });
        }

        @Override
        public boolean isEpochCheckEnabled() {
            return false;
        }
        
        @Override
        public void processLowLevelEvent(String action, String[] values) {
            contentLabel.setText(values[0].toUpperCase());
        }
        
        
    }
    
}
