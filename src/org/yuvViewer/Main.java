package org.yuvViewer;

import javax.swing.UIManager;
import java.awt.*;

import org.yuvViewer.gui.*;

class Main {
    MainFrame mainFrame;

	public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Main();
    }

    Main() {
        mainFrame = new MainFrame();
        mainFrame.validate();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        mainFrame.setSize(new Dimension(screenSize.width - 100, screenSize.height - 100));
        Dimension frameSize = mainFrame.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        mainFrame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        mainFrame.setVisible(true);
    }
}
