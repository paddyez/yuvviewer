package org.yuvViewer.utils;

import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

public interface YUVDeclaration
{
    String sqcif  = "SQCIF";
    String qcif   = "QCIF";
    String sif    = "SIF";
    String cif    = "CIF";
    String cif4   = "CIF4";
    String tv     = "TV";
    String hd1    = "HD1";
    String hd2    = "HD2";
    String custom = "Custom";

    JLabel sqcifLabel = new JLabel("128x96");
    JLabel qcifLabel  = new JLabel("176x144");
    JLabel sifLabel   = new JLabel("352x240");
    JLabel cifLabel   = new JLabel("352x288");
    JLabel cif4Label  = new JLabel("704x576");
    JLabel tvLabel    = new JLabel("720x576");
    JLabel hd1label   = new JLabel("1280x720");
    JLabel hd2Label   = new JLabel("1920x1080");
    JLabel xLabel     = new JLabel("x:");
    JLabel yLabel     = new JLabel("y:");
    JLabel yuvLabel   = new JLabel("yuv");
    JLabel yOnlyLabel = new JLabel("y-only");

    JRadioButton sqcifButton  = new JRadioButton(sqcif);
    JRadioButton qcifButton   = new JRadioButton(qcif);
    JRadioButton sifButton    = new JRadioButton(sif);
    JRadioButton cifButton    = new JRadioButton(cif);
    JRadioButton cif4Button   = new JRadioButton(cif4);
    JRadioButton tvButton     = new JRadioButton(tv);
    JRadioButton hd1Button    = new JRadioButton(hd1);
    JRadioButton hd2Button    = new JRadioButton(hd2);
    JRadioButton customButton = new JRadioButton(custom);
    JRadioButton yuvButton    = new JRadioButton("YUV");
    JRadioButton yOnlyButton  = new JRadioButton("Y-Only");

    Dimension SQCIF = new Dimension(128, 96);
    Dimension QCIF  = new Dimension(176, 144);
    Dimension SIF   = new Dimension(352, 240);
    Dimension CIF   = new Dimension(352, 288);
    Dimension CIF4  = new Dimension(704, 576);
    Dimension TV    = new Dimension(720, 576);
    Dimension HD1   = new Dimension(1280, 720);
    Dimension HD2   = new Dimension(1920, 1080);

    int ccYUV = 0;
    int ccY   = 1;
}
