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
 * <p>WTFPL – Do What the Fuck You Want to Public License</p>
 * <p>Company: </p>
 *
 * @author Patrick-Emil Zörner
 * @version 1.0
 */
public class FrameAboutBox extends JDialog implements ActionListener {
    private final JPanel panel1 = new JPanel();
    private final JPanel panel2 = new JPanel();
    private final JPanel insetsPanel1 = new JPanel();
    private final JPanel insetsPanel2 = new JPanel();
    private final JPanel insetsPanel3 = new JPanel();
    private final JButton buttonOK = new JButton();
    private final JLabel imageLabel = new JLabel();
    private final JLabel licenseImageLabel = new JLabel();
    private final JLabel label1 = new JLabel();
    private final JLabel label2 = new JLabel();
    private final JLabel label3 = new JLabel();
    private final JLabel label4 = new JLabel();
    private final BorderLayout borderLayout1 = new BorderLayout();
    private final BorderLayout borderLayout2 = new BorderLayout();
    private final FlowLayout flowLayout1 = new FlowLayout();
    private final GridLayout gridLayout1 = new GridLayout();
    private static final String PRODUCT = "YUV Viewer";
    private static final String VERSION = "1.0";
    private static final String COPYRIGHT = "WTFPL – Do What the Fuck You Want to Public License";
    private static final String COMMENTS = "Versatile YUV viewing utility";

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
        URL licenseImageResource = null;
        try {
            imageResource = FrameAboutBox.class.getResource("/img/movie.jpg");
            licenseImageResource = FrameAboutBox.class.getResource("/img/wtfpl-badge-1.png");
            System.out.println("Trying to load image resource: movie.jpg" + (imageResource == null ? " (not found)" : " (found)"));
        } catch (NullPointerException npe) {
            System.err.println(npe.getMessage());
        }
        if(imageResource != null) {
            imageLabel.setIcon(new ImageIcon(imageResource));
        }
        if(licenseImageResource != null) {
            licenseImageLabel.setIcon(new ImageIcon(licenseImageResource));
        }
        this.setTitle("About");
        panel1.setLayout(borderLayout1);
        panel2.setLayout(borderLayout2);
        insetsPanel1.setLayout(flowLayout1);
        insetsPanel2.setLayout(new BoxLayout(insetsPanel2, BoxLayout.Y_AXIS));
        insetsPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gridLayout1.setRows(4);
        gridLayout1.setColumns(1);
        label1.setText(PRODUCT);
        label2.setText(VERSION);
        label3.setText(COPYRIGHT);
        label4.setText(COMMENTS);
        insetsPanel3.setLayout(gridLayout1);
        insetsPanel3.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 10));
        buttonOK.setText("Ok");
        buttonOK.addActionListener(this);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        licenseImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        insetsPanel2.add(imageLabel);
        insetsPanel2.add(Box.createVerticalStrut(25));
        insetsPanel2.add(licenseImageLabel);
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
