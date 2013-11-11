/***************************************************************************
                          ExtensionFileFilter.java  -  description
                             -------------------
    begin                : Thu Aug 28 18:24:32 CEST 2003
    copyright            : (C) 2003 by Patrick Zoerner
    email                : paddyz@t-online.de
 ***************************************************************************/

/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

package org.yuvViewer.utils;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

/**
 * <p>Custom FileFilter. At the moment it only shows *.yuv files (All Files is default).
 * @author Patrick Zoerner
 * @version 1.0
 */
public class ExtensionFileFilter extends FileFilter
{
    private String fileExtension; 
    public ExtensionFileFilter(String extension)
    {
        fileExtension = new String (extension);
        
    }
    /**
     * <p>Displays Directories and *.yuv files only.</p>
     * @param file the chosen File
     * @return true if the file is a directory or the extention is known to ExtensionUtils
     * @see gui.ExtensionUtils
     */
    public boolean accept(File file)
    {
	if (file.isDirectory())
	    {
		return true;
	    }

	String extension = ExtensionUtils.getExtension(file);
	if (extension != null)
	    {
		if (extension.equals(fileExtension))
		    {
			return true;
		    }
		else
		    {
			return false;
		    }
	    }
	return false;
    }

    /**
     * <p>The description of this filter.</p>
     * @return The String Which is displayed ...
     */
    public String getDescription()
    {
	return fileExtension+ " Files (*."+ fileExtension +")";
    }
}
