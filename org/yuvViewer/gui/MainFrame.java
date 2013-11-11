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

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.yuvViewer.utils.*;

/**
 * @author Patrick Zoerner
 * @version 1.0
 */
public class MainFrame extends JFrame implements ActionListener
{
    private UIManager.LookAndFeelInfo[] lookAndFeelInfo = UIManager.getInstalledLookAndFeels();
    private UIManager.LookAndFeelInfo info;
    private final String Title="YUV Viewer";
    private final SettingsDialog settingsDialog=new SettingsDialog(this);
    private JPanel contentPane;
    private BorderLayout borderLayout1 = new BorderLayout();
    private JMenuBar jMenuBar1 = new JMenuBar();
    private JMenu jMenuFile = new JMenu();
    private JMenu jMenuOptions = new JMenu();
    private JMenuItem jMenuFileOpen = new JMenuItem();
    private JMenuItem jMenuFileExit = new JMenuItem();
    private JMenu jMenuTools = new JMenu();
    private JRadioButtonMenuItem jMenuScale1 = new JRadioButtonMenuItem();
    private JRadioButtonMenuItem jMenuScale2 = new JRadioButtonMenuItem();
    private JRadioButtonMenuItem jMenuScale4 = new JRadioButtonMenuItem();
    private JRadioButtonMenuItem jMenuScale8 = new JRadioButtonMenuItem();
    ButtonGroup buttonGroup = new ButtonGroup();
    private JCheckBoxMenuItem jCheckBoxMenuItemShowY = new JCheckBoxMenuItem("Show Y", true);
    private JCheckBoxMenuItem jCheckBoxMenuItemShowU = new JCheckBoxMenuItem("Show U", true);
    private JCheckBoxMenuItem jCheckBoxMenuItemShowV = new JCheckBoxMenuItem("Show V", true);
    private ButtonGroup lookAndFellGroup = new ButtonGroup();
    private JRadioButtonMenuItem jRadioButtonMenueItem;
    private JMenu jMenuHelp = new JMenu();
    private JMenuItem jMenuHelpAbout = new JMenuItem();
    private final FrameAboutBox dlg = new FrameAboutBox(this);

    private final JDesktopPane jDesktop = new JDesktopPane();
    private final JInternalFrame jInternalFrame=new JInternalFrame("Control", 
								   false, //resizable
								   false, //closable
								   false, //maximizable
								   true);
    private final JButton jButtonStepBack=new JButton("||<");
    private final JButton jButtonPlay=new JButton(">");
    private final JButton jButtonStepForeward=new JButton(">||");
    private final JButton jButtonPause=new JButton("||");
    private final JButton jButtonRewind=new JButton("<<");
    private final JLabel jFrameText=new JLabel("Frame #:");
    private final WholeNumberTextField frameText=new WholeNumberTextField(3);

    private YUVViewer yuvViewer=null;
    private File yuvFile;
    private Dimension yuvDimension;
    private int colorSpace;

    YUVViewer.Play play;
    
    private boolean firstTime=true;

    public MainFrame()
    {
	super();
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
    void jbInit()
    {
	contentPane = (JPanel)this.getContentPane();
	contentPane.setLayout(borderLayout1);

	this.setTitle(Title);
	//Menu File
	jMenuFile.setText("File");
	jMenuFile.setMnemonic(KeyEvent.VK_F);
	getAccessibleContext().setAccessibleDescription("File");
	jMenuTools.setText("Tools");
	jMenuFile.setMnemonic(KeyEvent.VK_T);
	getAccessibleContext().setAccessibleDescription("Tools");
	jMenuFileOpen.setText("Open");
        jMenuFileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
	jMenuFileOpen.addActionListener(this);
	jMenuFileExit.setText("Exit");
	jMenuFileExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
	jMenuFileExit.addActionListener(this);
	jMenuScale1.setText("Scale 1:1");
	jMenuScale1.addActionListener(this);
	jMenuScale2.setText("Scale 1:2");
	jMenuScale2.addActionListener(this);
	jMenuScale4.setText("Scale 1:4");
	jMenuScale4.addActionListener(this);
	jMenuScale8.setText("Scale 1:8");
	jMenuScale8.addActionListener(this);
	buttonGroup.add(jMenuScale1);
	jMenuScale1.setSelected(true);
	buttonGroup.add(jMenuScale2);
	buttonGroup.add(jMenuScale4);
	buttonGroup.add(jMenuScale8);
	jCheckBoxMenuItemShowY.addActionListener(this);
	jCheckBoxMenuItemShowU.addActionListener(this);
	jCheckBoxMenuItemShowV.addActionListener(this);
	//Menu Options
	jMenuOptions.setText("Options");
	jMenuOptions.setMnemonic(KeyEvent.VK_O);
	setupRadioButtons();
	//Menu Help
	jMenuHelp.setText("Help");
	jMenuHelp.setMnemonic(KeyEvent.VK_H);
	jMenuHelpAbout.setText("About");
	jMenuHelpAbout.setMnemonic(KeyEvent.VK_A);
	jMenuHelpAbout.addActionListener(this);

	jMenuFile.add(jMenuFileOpen);
	jMenuFile.add(jMenuFileExit);

	jMenuTools.add(jMenuScale1);
	jMenuTools.add(jMenuScale2);
	jMenuTools.add(jMenuScale4);
	jMenuTools.add(jMenuScale8);
	jMenuTools.addSeparator();
	jMenuTools.add(jCheckBoxMenuItemShowY);
	jMenuTools.add(jCheckBoxMenuItemShowU);
	jMenuTools.add(jCheckBoxMenuItemShowV);

	jMenuHelp.add(jMenuHelpAbout);

	jMenuBar1.add(jMenuFile);
	jMenuBar1.add(jMenuTools);
	jMenuBar1.add(jMenuOptions);
	jMenuBar1.add(jMenuHelp);
	this.setJMenuBar(jMenuBar1);
	contentPane.add(jDesktop, BorderLayout.CENTER);

	setupInternalFrame();
    }

    void setupRadioButtons()
    {
    	for(int i = 0; i < lookAndFeelInfo.length; i++)
	    {
		jRadioButtonMenueItem = new JRadioButtonMenuItem(lookAndFeelInfo[i].getName());
		jMenuOptions.add(jRadioButtonMenueItem);

		jRadioButtonMenueItem.setSelected(UIManager.getLookAndFeel().getName().equals(lookAndFeelInfo[i].getName()));
		jRadioButtonMenueItem.putClientProperty("UIKey", lookAndFeelInfo[i]);
		jRadioButtonMenueItem.addItemListener(new ItemListener()
		    {
			public void itemStateChanged(ItemEvent itemEvent)
			{
			    JRadioButtonMenuItem jRadioButtonMenueItem2 = (JRadioButtonMenuItem)itemEvent.getSource();
			    if(jRadioButtonMenueItem2.isSelected())
				{
				    info = (UIManager.LookAndFeelInfo)jRadioButtonMenueItem2.getClientProperty("UIKey");
				    
				    try
					{
					    UIManager.setLookAndFeel(info.getClassName());
					    SwingUtilities.updateComponentTreeUI(MainFrame.this);
					    SwingUtilities.updateComponentTreeUI(jInternalFrame.getContentPane()); 					
					}
				    catch (Exception e)
					{
					    System.err.println("unable to set UI " + e.getMessage());
					}
				}
			}
		    });
		lookAndFellGroup.add(jRadioButtonMenueItem);
	    }
    }

    void setupInternalFrame()
    {
	jInternalFrame.getContentPane().setLayout(new FlowLayout());
	jButtonStepBack.setActionCommand("StepBack");
	jButtonStepBack.setToolTipText("step one frame back");
	jButtonPlay.setActionCommand("Play");
	jButtonPlay.setToolTipText("play");
	jButtonStepForeward.setActionCommand("StepForeward");
	jButtonStepForeward.setToolTipText("step one frame foreward");
	jButtonPause.setActionCommand("Pause");
	jButtonPause.setToolTipText("plause");
	jButtonRewind.setActionCommand("Rewind");
	jButtonRewind.setToolTipText("rewind");
	jButtonStepBack.addActionListener(this);
	jButtonPlay.addActionListener(this);
	jButtonStepForeward.addActionListener(this);
	jButtonPause.addActionListener(this);
	jButtonRewind.addActionListener(this);
	jInternalFrame.getContentPane().add(jButtonRewind);	
	jInternalFrame.getContentPane().add(jButtonStepBack);
	jInternalFrame.getContentPane().add(jButtonPlay);
	jInternalFrame.getContentPane().add(jButtonStepForeward);
	jInternalFrame.getContentPane().add(jButtonPause);
	jInternalFrame.getContentPane().add(jFrameText);
	jInternalFrame.getContentPane().add(frameText);
	frameText.setEditable(false);
	jInternalFrame.setSize(jInternalFrame.getPreferredSize());
	jDesktop.add(jInternalFrame);
    }
    
    void startupViewer()
    {
	if(firstTime)
	    {
		jInternalFrame.setVisible(true);
		firstTime=false;
	    }
	else
	    {
		if(play!=null)
		    {
			play.setSuspended(true);
			if(play.getSuspended()==true)
			    {
				play=null;
			    }
		    }
		jDesktop.remove(yuvViewer);
		yuvViewer.dispose();		
		yuvViewer=null;
		jMenuScale1.setSelected(true);
		jCheckBoxMenuItemShowY.setState(true);
		jCheckBoxMenuItemShowU.setState(true);
		jCheckBoxMenuItemShowV.setState(true);
	    }
	yuvViewer=new YUVViewer(this,yuvFile,yuvDimension,colorSpace);
    }


    public void jMenuFileOpen()
    {
	JFileChooser jFileChooser=new JFileChooser();
	ExtensionFileFilter extensionFileFilter=new ExtensionFileFilter("yuv");
	jFileChooser.addChoosableFileFilter(extensionFileFilter);
	jFileChooser.addChoosableFileFilter(new ExtensionFileFilter("sqcif"));
	jFileChooser.addChoosableFileFilter(new ExtensionFileFilter("qcif"));
	jFileChooser.addChoosableFileFilter(new ExtensionFileFilter("sif"));
	jFileChooser.addChoosableFileFilter(new ExtensionFileFilter("cif"));
	jFileChooser.addChoosableFileFilter(new ExtensionFileFilter("cif4"));
	jFileChooser.addChoosableFileFilter(new ExtensionFileFilter("tv"));
	
	jFileChooser.setFileFilter(extensionFileFilter);
	int returnVal = jFileChooser.showDialog(MainFrame.this,"Open");
	jFileChooser.setApproveButtonToolTipText("Select an 4:2:0 *.yuv File");
	if (returnVal == JFileChooser.APPROVE_OPTION)
	    {					
                yuvFile=jFileChooser.getSelectedFile();
		if(ExtensionUtils.approveSelection(yuvFile))
		    {
			yuvDimension=ExtensionUtils.getDimension(yuvFile);
			if(yuvDimension==null)
			    {
				Dimension jDialogSettings = settingsDialog.getPreferredSize();
				Dimension frmSize = getSize();
				Point loc = getLocation();
				settingsDialog.setLocation((frmSize.width-jDialogSettings.width) / 2 + loc.x, (frmSize.height-jDialogSettings.height) / 2 + loc.y);
				settingsDialog.setResizable(false);
				settingsDialog.setModal(true);
				settingsDialog.pack();
				settingsDialog.setVisible(true);
			    }
			startupViewer();
                    }	
		else
		    {			
			System.out.println("I do not approve your selection");
		    }
	    }

	else
	    {
		System.out.println("Selection cancelled by user");
	    }
    }

    public void setYUVDimension(Dimension dimension)
    {
	yuvDimension=dimension;
    }
    public void setColorSpace(int cc) {
	colorSpace = cc;
    }

    public void setFrameNumber(int frameNumber)
    {
	Integer Int=new Integer(frameNumber);
	frameText.setText(Int.toString());
    }
    /**
     * <p>Help | About action performed. 
     * Displays FrameAboutBox</p>
     */
    public void jMenuHelpAbout()
    {
	Dimension dlgSize = dlg.getPreferredSize();
	Dimension frmSize = getSize();
	Point loc = getLocation();
	dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
	dlg.setResizable(false);
	dlg.setModal(true);
	dlg.pack();
	dlg.setVisible(true);
    }

    public void jMenuFileExit()
    {
	System.exit(0);
    }
    public void actionPerformed(ActionEvent ae)
    {
	String command = ae.getActionCommand();
	if(command.equals("Exit"))
	    {
		jMenuFileExit();
	    }
	else if(command.equals("Open"))
	    {
	      jMenuFileOpen();
	    }
	else if(command.equals("Scale 1:1"))
	    {
		if(yuvViewer!=null)
		    {
			yuvViewer.setScale(1);
		    }
	    }
	else if(command.equals("Scale 1:2"))
	    {
		if(yuvViewer!=null)
		    {
			if(play!=null)
			    {
				play.setSuspended(true);
				if(play.getSuspended()==true)
				    {
					play=null;
				    }
			    }
			yuvViewer.setScale(2);
		    }
		else
		    {
			jMenuScale1.setSelected(true);
		    }
	    }
	else if(command.equals("Scale 1:4"))
	    {
		if(yuvViewer!=null)
		    {
			if(play!=null)
			    {
				play.setSuspended(true);
				if(play.getSuspended()==true)
				    {
					play=null;
				    }
			    }
			yuvViewer.setScale(4);
		    }
		else
		    {
			jMenuScale1.setSelected(true);
		    }
	    }
	else if(command.equals("Scale 1:8"))
	    {
		if(yuvViewer!=null)
		    {
			if(play!=null)
			    {
				play.setSuspended(true);
				if(play.getSuspended()==true)
				    {
					play=null;
				    }
			    }
			yuvViewer.setScale(8);
		    }
		else
		    {
			jMenuScale1.setSelected(true);
		    }
	    }
	else if(command.equals("Show Y"))
	    {
		if(yuvViewer!=null)
		    {
			yuvViewer.setY(jCheckBoxMenuItemShowY.getState());
		    }
		else
		    {
			jCheckBoxMenuItemShowY.setState(true);
		    }
	    }
	else if(command.equals("Show U"))
	    {
		if(yuvViewer!=null)
		    {
			yuvViewer.setU(jCheckBoxMenuItemShowU.getState());
		    }
		else
		    {
			jCheckBoxMenuItemShowU.setState(true);
		    }
	    }
	else if(command.equals("Show V"))
	    {
		if(yuvViewer!=null)
		    {
			yuvViewer.setV(jCheckBoxMenuItemShowV.getState());
		    }
		else
		    {
			jCheckBoxMenuItemShowV.setState(true);
		    }
	    }
      	else if(command.equals("StepBack"))
	    {
		if(!yuvViewer.readLastData())
		    {
			yuvViewer.fillColors();
			yuvViewer.repaint();
		    }
	    }
	else if(command.equals("Play"))
	    {
		if(play==null)
		    {
			play=yuvViewer.new Play();
			if(play.getSuspended()==true)
			    {
				play.setSuspended(false);
			    }
			else 
			    {
				play.start();
			    }
		    }
		else if(play.getEndOfFile())
		    {
			play=null;
			yuvViewer.prepare();
			play=yuvViewer.new Play();
			play.start();
		    }
	    }
	else if(command.equals("StepForeward"))
	    {
		if(!yuvViewer.readData())		    
		    {
			yuvViewer.fillColors();
			yuvViewer.repaint();
		    }
	    }
	else if(command.equals("Pause"))
	    {
		if(play==null)                                       
		    {
			play=yuvViewer.new Play();
			play.start();
		    }
		else
		    {
			play.setSuspended(true);
			if(play.getSuspended()==true)
			    {
				play=null;
			    }
		    }
	    }
	else if(command.equals("Rewind"))
	    {
		yuvViewer.prepare();
		yuvViewer.fillColors();
		yuvViewer.repaint();
	    }
	else if(command.equals("About"))
	    {
		jMenuHelpAbout();
	    }
	else
	    {
		System.err.println("Command not found");
	    }
    }
    protected void processWindowEvent(WindowEvent we)
    {
	super.processWindowEvent(we);
	if (we.getID() == WindowEvent.WINDOW_CLOSING)
	    {
		jMenuFileExit();
	    }
    }
    public void enableYUVCheckboxes(boolean y, boolean u, boolean v)
    {
	jCheckBoxMenuItemShowY.setEnabled(y);
	jCheckBoxMenuItemShowU.setEnabled(u);
	jCheckBoxMenuItemShowV.setEnabled(v);
    }
}
