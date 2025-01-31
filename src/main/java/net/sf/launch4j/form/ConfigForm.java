package net.sf.launch4j.form;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.*;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

public abstract class ConfigForm extends JPanel {
    protected final JTextArea _logTextArea = new JTextArea();
    protected final JTabbedPane _tab = new JTabbedPane();

    /**
     * Default constructor
     */
    public ConfigForm() {
        initializePanel();
    }

    /**
     * Adds fill components to empty cells in the first row and first column of the grid.
     * This ensures that the grid spacing will be the same as shown in the designer.
     * @param cols an array of column indices in the first row where fill components should be added.
     * @param rows an array of row indices in the first column where fill components should be added.
     */
    void addFillComponents(Container panel, int[] cols, int[] rows) {
        Dimension filler = new Dimension(10, 10);

        boolean filled_cell_11 = false;
        CellConstraints cc = new CellConstraints();
        if (cols.length > 0 && rows.length > 0) {
            if (cols[0] == 1 && rows[0] == 1) {
                /* add a rigid area  */
                panel.add(Box.createRigidArea(filler), cc.xy(1, 1));
                filled_cell_11 = true;
            }
        }

        for (int col : cols) {
            if (col == 1 && filled_cell_11) {
                continue;
            }
            panel.add(Box.createRigidArea(filler), cc.xy(col, 1));
        }

        for (int row : rows) {
            if (row == 1 && filled_cell_11) {
                continue;
            }
            panel.add(Box.createRigidArea(filler), cc.xy(1, row));
        }
    }

    /**
     * Helper method to load an image file from the CLASSPATH
     * @param imageName the package and name of the file to load relative to the CLASSPATH
     * @return an ImageIcon instance with the specified image file
     * @throws IllegalArgumentException if the image resource cannot be loaded.
     */
    public ImageIcon loadImage(String imageName) {
        try {
            ClassLoader classloader = getClass().getClassLoader();
            java.net.URL url = classloader.getResource(imageName);
            if (url != null) {
                return new ImageIcon(url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Unable to load image: " + imageName);
    }

    public JPanel createPanel() {
        JPanel jpanel1 = new JPanel();
        FormLayout formlayout1 = new FormLayout(
                "FILL:7DLU:NONE,FILL:DEFAULT:GROW(1.0),FILL:7DLU:NONE",
                "CENTER:3DLU:NONE,FILL:DEFAULT:NONE,CENTER:3DLU:NONE,CENTER:DEFAULT:NONE,CENTER:3DLU:NONE,FILL:DEFAULT:GROW(1.0),CENTER:9DLU:NONE");
        CellConstraints cc = new CellConstraints();
        jpanel1.setLayout(formlayout1);

        _logTextArea.setName("logTextArea");
        JScrollPane jscrollpane1 = new JScrollPane();
        jscrollpane1.setViewportView(_logTextArea);
        jscrollpane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jscrollpane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jpanel1.add(jscrollpane1, cc.xy(2, 6));

        _tab.setName("tab");
        jpanel1.add(_tab, cc.xywh(1, 2, 3, 1));

        addFillComponents(jpanel1, new int[] {1, 2, 3}, new int[] {1, 3, 4, 5, 6, 7});
        return jpanel1;
    }

    /**
     * Initializer
     */
    protected void initializePanel() {
        setLayout(new BorderLayout());
        add(createPanel(), BorderLayout.CENTER);
    }
}
