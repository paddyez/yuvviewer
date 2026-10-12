package org.yuvViewer.gui;

import java.awt.Toolkit;
import java.io.File;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JFileChooser;

public class FileSelectionChooser extends JFileChooser {
    public FileSelectionChooser() {
        addPropertyChangeListener(event -> {
            String property = event.getPropertyName();
            File file = getSelectedFile();
            if (DIRECTORY_CHANGED_PROPERTY.equals(property) && file != null
                    && !Objects.equals(file.getAbsoluteFile().getParentFile(), getCurrentDirectory())) {
                setSelectedFile(null);
            }
            if (SELECTED_FILE_CHANGED_PROPERTY.equals(property)
                    || DIRECTORY_CHANGED_PROPERTY.equals(property)
                    || FILE_FILTER_CHANGED_PROPERTY.equals(property)
                    || DIALOG_TYPE_CHANGED_PROPERTY.equals(property)
                    || "UI".equals(property)) {
                updateApproveButton();
            }
        });
        updateApproveButton();
    }

    private boolean hasSelectedFile() {
        File file = getSelectedFile();
        return file != null && file.isFile();
    }

    private void updateApproveButton() {
        JButton button = getUI().getDefaultButton(this);
        if (button != null) {
            button.setEnabled(hasSelectedFile());
        }
    }

    @Override
    public void approveSelection() {
        if (hasSelectedFile()) {
            super.approveSelection();
        } else {
            updateApproveButton();
            Toolkit.getDefaultToolkit().beep();
        }
    }
}
