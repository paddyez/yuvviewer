package org.yuvViewer.gui;

import org.yuvViewer.utils.WholeNumberTextField;
import org.yuvViewer.utils.YUVDeclaration;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

public class SettingsDialog extends JDialog implements ActionListener, YUVDeclaration {
    MainFrame parent;
    private final JTabbedPane tabbedPaneSettings = new JTabbedPane();
    private final JButton buttonOK = new JButton();
    private final JButton buttonCancel = new JButton();
    private final WholeNumberTextField xText = new WholeNumberTextField(3);
    private final WholeNumberTextField yText = new WholeNumberTextField(3);
    private final ButtonGroup group = new ButtonGroup();
    private final ButtonGroup ccGroup = new ButtonGroup();
    private final JPanel radioPanel = new JPanel();
    private final JPanel customPanel = new JPanel();
    private final JPanel buttonPanel = new JPanel();
    private final JPanel ccPanel = new JPanel();
    private final GridBagLayout gBL = new GridBagLayout();
    private final GridBagConstraints gBC = new GridBagConstraints();
    private static final String ERR_VIDEOSIZE = "Check Video size";
    private static final String POPUP_CONFIRM_HEADLINE = "Info";
    private static final String POPUP_ERROR_HEADLINE = "Error";

    private Dimension dimension;
    private int colorspace = ccYUV;

    public SettingsDialog(MainFrame parent) {
        super(parent);
        this.parent = parent;
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //private static int USR_MSG_PROJECT_EXISTS=0;    
    private boolean confirmMessage(String confirmMsg) {
        int ret;
        ret = JOptionPane.showConfirmDialog(this, confirmMsg, POPUP_CONFIRM_HEADLINE, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		return ret == JOptionPane.OK_OPTION;
    }

    private void errorMessage(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage, POPUP_ERROR_HEADLINE, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * <p>Component initialization</p>
     */
    private void jbInit() {
        this.setTitle("Settings only 4:2:0 enabled!");

        sqcifButton.addActionListener(this);
        qcifButton.setMnemonic(KeyEvent.VK_Q);
        qcifButton.addActionListener(this);
        qcifButton.setSelected(true);
        sifButton.setMnemonic(KeyEvent.VK_S);
        sifButton.addActionListener(this);
        cifButton.setMnemonic(KeyEvent.VK_C);
        cifButton.addActionListener(this);
        cif4Button.addActionListener(this);
        tvButton.setMnemonic(KeyEvent.VK_T);
        tvButton.addActionListener(this);
        hd1Button.addActionListener(this);
        hd2Button.addActionListener(this);
        group.add(sqcifButton);
        group.add(qcifButton);
        group.add(sifButton);
        group.add(cifButton);
        group.add(cif4Button);
        group.add(tvButton);
        group.add(hd1Button);
        group.add(hd2Button);
        group.add(customButton);
        customButton.setMnemonic(KeyEvent.VK_U);
        customButton.addActionListener(this);
        customPanel.setLayout(new FlowLayout());
        xText.setEditable(false);
        yText.setEditable(false);
        customPanel.add(xLabel, null);
        customPanel.add(xText, null);
        customPanel.add(yLabel, null);
        customPanel.add(yText, null);
        radioPanel.setLayout(new GridLayout(0, 2));
        radioPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        radioPanel.add(sqcifButton);
        radioPanel.add(sqcifLabel);
        radioPanel.add(qcifButton);
        radioPanel.add(qcifLabel);
        radioPanel.add(sifButton);
        radioPanel.add(sifLabel);
        radioPanel.add(cifButton);
        radioPanel.add(cifLabel);
        radioPanel.add(cif4Button);
        radioPanel.add(cif4Label);
        radioPanel.add(tvButton);
        radioPanel.add(tvLabel);
        radioPanel.add(hd1Button);
        radioPanel.add(hd1label);
        radioPanel.add(hd2Button);
        radioPanel.add(hd2Label);
        radioPanel.add(customButton);
        radioPanel.add(customPanel);
        TitledBorder titledV = BorderFactory.createTitledBorder("Video Size");
        radioPanel.setBorder(titledV);
        tabbedPaneSettings.add("Video Size", radioPanel);

        ccPanel.setBorder(BorderFactory.createTitledBorder("Color space"));
        ccGroup.add(yuvButton);
        yuvButton.setSelected(true);
        ccGroup.add(yOnlyButton);
        ccPanel.add(yuvButton);
        ccPanel.add(yOnlyButton);
        tabbedPaneSettings.add("Color", ccPanel);

        buttonOK.setText("Ok");
        buttonCancel.setText("Cancel");
        buttonOK.addActionListener(this);
        buttonCancel.addActionListener(this);
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(buttonOK, null);
        buttonPanel.add(buttonCancel, null);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabbedPaneSettings, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    void setConstraints(int x, int y, int top, int left, int bottom, int right, int anchor) {
        gBC.gridx = x;
        gBC.gridy = y;
        gBC.insets = new Insets(top, left, bottom, right);
        gBC.anchor = anchor;
    }

    /**
     * <p>Overridden so we can exit when window is closed</p>
     */
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
        if (e.getSource() == buttonCancel) {
            cancel();
        } else if (e.getSource() == buttonOK) {
            if (validateSettings()) {
                parent.setYUVDimension(dimension);
                parent.setColorSpace(colorspace);
                cancel();
            } else {
                System.out.println("Settings not valid!");
            }
        } else if (e.getSource() == sqcifButton) {
            yText.setEditable(false);
            xText.setEditable(false);
        } else if (e.getSource() == qcifButton) {
            yText.setEditable(false);
            xText.setEditable(false);
        } else if (e.getSource() == sifButton) {
            yText.setEditable(false);
            xText.setEditable(false);
        } else if (e.getSource() == cifButton) {
            yText.setEditable(false);
            xText.setEditable(false);
        } else if (e.getSource() == cif4Button) {
            yText.setEditable(false);
            xText.setEditable(false);
        } else if (e.getSource() == tvButton) {
            yText.setEditable(false);
            xText.setEditable(false);
        } else if (e.getSource() == customButton) {
            yText.setEditable(true);
            xText.setEditable(true);
        } else if (e.getSource() == hd1Button) {
            yText.setEditable(false);
            xText.setEditable(false);
        } else if (e.getSource() == hd2Button) {
            yText.setEditable(false);
            xText.setEditable(false);
        }
    }

    private boolean validateSettings() {
        if (sqcifButton.isSelected()) {
            dimension = SQCIF;
        } else if (qcifButton.isSelected()) {
            dimension = QCIF;
        } else if (sifButton.isSelected()) {
            dimension = SIF;
        } else if (cifButton.isSelected()) {
            dimension = CIF;
        } else if (cif4Button.isSelected()) {
            dimension = CIF4;
        } else if (tvButton.isSelected()) {
            dimension = TV;
        } else if (hd1Button.isSelected()) {
            dimension = HD1;
        } else if (hd2Button.isSelected()) {
            dimension = HD2;
        } else if (customButton.isSelected()) {
            int xSize = xText.getValue();
            int ySize = yText.getValue();
            if (xSize < 8 || ySize < 8) {
                errorMessage(ERR_VIDEOSIZE);
                return false;
            } else {
                dimension = new Dimension(xSize, ySize);
            }
        }
        // set color space
        if (yuvButton.isSelected()) {
            colorspace = ccYUV;
        } else if (yOnlyButton.isSelected()) {
            colorspace = ccY;
        }

        return true;
    }
}
