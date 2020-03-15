/***************************************************************************
                          MainFrame.java  -  description
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

package org.yuvViewer.gui;

import java.awt.*;
import java.io.File;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * <p>The JDialog showing information about the program</p>
 * <p>Title: Reference Decoder MPEG-4</p>
 * <p>Description: Reference Decoder MPEG-4</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: sci-worx</p>
 * @author Patrick Zoerner
 * @version 1.0
 */
public class FrameAboutBox extends JDialog implements ActionListener
{
    //    private final String separator=File.separator;
    private static final JPanel panel1 = new JPanel();
    private static final JPanel panel2 = new JPanel();
    private static final JPanel insetsPanel1 = new JPanel();
    private static final JPanel insetsPanel2 = new JPanel();
    private static final JPanel insetsPanel3 = new JPanel();
    private static final JButton buttonOK = new JButton();
    private static final JLabel imageLabel = new JLabel();
    private static final JLabel label1 = new JLabel();
    private static final JLabel label2 = new JLabel();
    private static final JLabel label3 = new JLabel();
    private static final JLabel label4 = new JLabel();
    private static final BorderLayout borderLayout1 = new BorderLayout();
    private static final BorderLayout borderLayout2 = new BorderLayout();
    private static final FlowLayout flowLayout1 = new FlowLayout();
    private static final GridLayout gridLayout1 = new GridLayout();
    private static final String product = "YUV Viewer";
    private static final String version = "1.0";
    private static final String copyright = "Copyright (c) 2003";
    private static final String comments = "Versatile YUV viewing utility";

    public FrameAboutBox(MainFrame parent)
    {
	super(parent);
	enableEvents(AWTEvent.WINDOW_EVENT_MASK);
	try
	    {
		jbInit();
	    }
	catch(Exception e)
	    {
		e.printStackTrace();
	    }
    }
    /**
     * <p>Component initialization</p>
     */
    private void jbInit() throws Exception
    {
	imageLabel.setIcon(new ImageIcon(FrameAboutBox.class.getResource("movie.jpg")));
	this.setTitle("About");
	panel1.setLayout(borderLayout1);
	panel2.setLayout(borderLayout2);
	insetsPanel1.setLayout(flowLayout1);
	insetsPanel2.setLayout(flowLayout1);
	insetsPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	gridLayout1.setRows(4);
	gridLayout1.setColumns(1);
	label1.setText(product);
	label2.setText(version);
	label3.setText(copyright);
	label4.setText(comments);
	insetsPanel3.setLayout(gridLayout1);
	insetsPanel3.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 10));
	buttonOK.setText("Ok");
	buttonOK.addActionListener(this);
	insetsPanel2.add(imageLabel, null);
	panel2.add(insetsPanel2, BorderLayout.WEST);
	this.getContentPane().add(panel1, null);
	insetsPanel3.add(label1, null);
	insetsPanel3.add(label2, null);
	insetsPanel3.add(label3, null);
	insetsPanel3.add(label4, null);
	panel2.add(insetsPanel3, BorderLayout.CENTER);
	insetsPanel1.add(buttonOK, null);
	panel1.add(insetsPanel1, BorderLayout.SOUTH);
	panel1.add(panel2, BorderLayout.NORTH);
    }
    /**
     * <p>Overridden so we can exit when window is closed</p>
     */
    protected void processWindowEvent(WindowEvent e)
    {
	if (e.getID() == WindowEvent.WINDOW_CLOSING)
	    {
		cancel();
	    }
	super.processWindowEvent(e);
    }
    /**
     * <p>Close the dialog</p>
     */
    void cancel()
    {
	dispose();
    }
    /**
     * <p>Close the dialog on a button event</p>
     */
    public void actionPerformed(ActionEvent e)
    {
	if (e.getSource() == buttonOK)
	    {
		cancel();
	    }
    }
}
