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

    JLabel sqcifLabel = new JLabel("128x96");
    JLabel qcifLabel = new JLabel("176x144");
    JLabel sifLabel = new JLabel("352x240");
    JLabel cifLabel = new JLabel("352x288");
    JLabel cif4Label = new JLabel("704x576");
    JLabel tvLabel = new JLabel("720x576");
    JLabel hd1label = new JLabel("1280x720");
    JLabel hd2Label = new JLabel("1920x1080");
    JLabel xLabel = new JLabel("x:");
    JLabel yLabel = new JLabel("y:");
    JLabel yuvLabel = new JLabel("yuv");
    JLabel yOnlyLabel = new JLabel("y-only");

    JRadioButton sqcifButton = new JRadioButton(YUVNames.SQCIF.name());
    JRadioButton qcifButton = new JRadioButton(YUVNames.QCIF.name());
    JRadioButton sifButton = new JRadioButton(YUVNames.SIF.name());
    JRadioButton cifButton = new JRadioButton(YUVNames.CIF.name());
    JRadioButton cif4Button = new JRadioButton(YUVNames.CIF4.name());
    JRadioButton tvButton = new JRadioButton(YUVNames.TV.name());
    JRadioButton hd1Button = new JRadioButton(YUVNames.HD1.name());
    JRadioButton hd2Button = new JRadioButton(YUVNames.HD2.name());
    JRadioButton customButton = new JRadioButton(YUVNames.CUSTOM.name());
    JRadioButton yuvButton = new JRadioButton("YUV");
    JRadioButton yOnlyButton = new JRadioButton("Y-Only");

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
