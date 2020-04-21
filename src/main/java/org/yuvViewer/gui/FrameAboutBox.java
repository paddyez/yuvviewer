package org.yuvViewer.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.net.URL;

/**
 * <p>The JDialog showing information about the program</p>
 * <p>Title: YUV viewer</p>
 * <p>Description: Versatile YUV viewing utility</p>
 * <p>Copyright: © 2002</p>
 * <p>Company: </p>
 *
 * @author Patrick-Emil Zörner
 * @version 1.0
 */
public class FrameAboutBox extends JDialog implements ActionListener {
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
    private static final String copyright = "Copyright © 2002";
    private static final String comments = "Versatile YUV viewing utility";

    public FrameAboutBox(MainFrame parent) {
        super(parent);
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>Component initialization</p>
     */
    private void jbInit() {
        URL imageResource = null;
        try {
            imageResource = FrameAboutBox.class.getResource("movie.jpg");
            if(imageResource == null) {
                imageResource = FrameAboutBox.class.getResource("/img/movie.jpg");
            }
        } catch (NullPointerException npe) {
            System.err.println(npe.getMessage());
        }
        if(imageResource != null) {
            imageLabel.setIcon(new ImageIcon(imageResource));
        }
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
    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            cancel();
        }
        super.processWindowEvent(e);
    }

    /**
     * <p>Close the dialog</p>
     */
    void cancel() {
        dispose();
    }

    /**
     * <p>Close the dialog on a button event</p>
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonOK) {
            cancel();
        }
    }
}
