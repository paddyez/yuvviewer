/***************************************************************************
                          YUVDeclaration.java  -  description
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

package org.yuvViewer.utils;

import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

public interface YUVDeclaration
{
    public final String sqcif  = "SQCIF";
    public final String qcif   = "QCIF";
    public final String sif    = "SIF";
    public final String cif    = "CIF";
    public final String cif4   = "CIF4";
    public final String tv     = "TV";
    public final String hd1    = "HD1";
    public final String hd2    = "HD2";
    public final String custom = "Custom";

    public final JLabel sqcifLabel = new JLabel("128x96");
    public final JLabel qcifLabel  = new JLabel("176x144");
    public final JLabel sifLabel   = new JLabel("352x240");
    public final JLabel cifLabel   = new JLabel("352x288");
    public final JLabel cif4Label  = new JLabel("704x576");
    public final JLabel tvLabel    = new JLabel("720x576");
    public final JLabel hd1label   = new JLabel("1280x720");
    public final JLabel hd2Label   = new JLabel("1920x1080");
    public final JLabel xLabel     = new JLabel("x:");
    public final JLabel yLabel     = new JLabel("y:");
    public final JLabel yuvLabel   = new JLabel("yuv");
    public final JLabel yOnlyLabel = new JLabel("y-only");

    public final JRadioButton sqcifButton  = new JRadioButton(sqcif);
    public final JRadioButton qcifButton   = new JRadioButton(qcif);
    public final JRadioButton sifButton    = new JRadioButton(sif);
    public final JRadioButton cifButton    = new JRadioButton(cif);
    public final JRadioButton cif4Button   = new JRadioButton(cif4);
    public final JRadioButton tvButton     = new JRadioButton(tv);
    public final JRadioButton hd1Button    = new JRadioButton(hd1);
    public final JRadioButton hd2Button    = new JRadioButton(hd2);
    public final JRadioButton customButton = new JRadioButton(custom);
    public final JRadioButton yuvButton    = new JRadioButton("YUV");
    public final JRadioButton yOnlyButton  = new JRadioButton("Y-Only");

    public static final Dimension SQCIF = new Dimension(128, 96);
    public static final Dimension QCIF  = new Dimension(176, 144);
    public static final Dimension SIF   = new Dimension(352, 240);
    public static final Dimension CIF   = new Dimension(352, 288);
    public static final Dimension CIF4  = new Dimension(704, 576);
    public static final Dimension TV    = new Dimension(720, 576);
    public static final Dimension HD1   = new Dimension(1280, 720);
    public static final Dimension HD2   = new Dimension(1920, 1080);

    public static final int ccYUV = 0;
    public static final int ccY   = 1;
}
