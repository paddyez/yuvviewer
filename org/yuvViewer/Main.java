/***************************************************************************
                          Main.java  -  description
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

package org.yuvViewer;

import javax.swing.UIManager;
import java.awt.*;

import org.yuvViewer.gui.*;
/**
 * download kunststoff.jar at:
 * http://www.incors.org/index.php3
 */
//import com.incors.plaf.kunststoff.*;

class Main
{
    MainFrame mainFrame;
    private boolean packFrame = false;

    public static void main(String[] args)
    {
	try
	    {
		/**
		 * download kunststoff.jar at:
		 * http://www.incors.org/index.php3
		 */
		//UIManager.setLookAndFeel(new KunststoffLookAndFeel());
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    }
	catch(Exception e)
	    {
		e.printStackTrace();
	    }
	new Main();
    }
    Main()
    {
	mainFrame=new MainFrame();
	if (packFrame)
	    {
		mainFrame.pack();
	    }
	else
	    {
		mainFrame.validate();
	    }
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	mainFrame.setSize(new Dimension(screenSize.width-100,screenSize.height-100));
	Dimension frameSize = mainFrame.getSize();
	if (frameSize.height > screenSize.height)
	    {
		frameSize.height = screenSize.height;
	    }
	if (frameSize.width > screenSize.width)
	    {
		frameSize.width = screenSize.width;
	    }
	mainFrame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
	mainFrame.setVisible(true);
    }
}
