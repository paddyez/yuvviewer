package org.yuvViewer.utils;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * <p>Custom FileFilter. At the moment it only shows *.yuv files (All Files is default).
 *
 * @author Patrick Zoerner
 * @version 1.0
 */
public class ExtensionFileFilter extends FileFilter {
    private final String fileExtension;

    public ExtensionFileFilter(String extension) {
        fileExtension = extension;
    }

    /**
     * <p>Displays Directories and *.yuv files only.</p>
     *
     * @param file the chosen File
     * @return true if the file is a directory or the extention is known to ExtensionUtils
     * @see org.yuvViewer.utils.ExtensionUtils
     */
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }

        String extension = ExtensionUtils.getExtension(file);
        if (extension != null) {
			return extension.equals(fileExtension);
        }
        return false;
    }

    /**
     * <p>The description of this filter.</p>
     *
     * @return The String Which is displayed ...
     */
    public String getDescription() {
        return fileExtension + " Files (*." + fileExtension + ")";
    }
}
