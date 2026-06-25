package org.yuvViewer.utils;

import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

public interface YUVDeclaration {
    public enum YUVNames {
        SQCIF,
        QCIF,
        SIF,
        CIF,
        CIF4,
        TV,
        HD1,
        HD2,
        CUSTOM;
    }

    Dimension SQCIF_DIMENSION = new Dimension(128, 96);
    Dimension QCIF_DIMENSION = new Dimension(176, 144);
    Dimension SIF_DIMENSION = new Dimension(352, 240);
    Dimension CIF_DIMENSION = new Dimension(352, 288);
    Dimension CIF4_DIMENSION = new Dimension(704, 576);
    Dimension TV_DIMENSION = new Dimension(720, 576);
    Dimension HD1_DIMENSION = new Dimension(1280, 720);
    Dimension HD2_DIMENSION = new Dimension(1920, 1080);

    int ccYUV = 0;
    int ccY = 1;
}
