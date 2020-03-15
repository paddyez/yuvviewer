/***************************************************************************
                          WholeNumberTextField.java  -  description
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

import javax.swing.*; 
import javax.swing.text.*; 

import java.awt.Toolkit;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class WholeNumberTextField extends JTextField 
{
    private Toolkit toolkit;
    private NumberFormat integerFormatter;

    public WholeNumberTextField(int value, int columns) 
    {
        super(columns);
        toolkit = Toolkit.getDefaultToolkit();
        integerFormatter = NumberFormat.getNumberInstance(Locale.US);
        integerFormatter.setParseIntegerOnly(true);
        setValue(value);
    }
    public WholeNumberTextField(int columns) 
    {
        super(columns);
        toolkit = Toolkit.getDefaultToolkit();
        integerFormatter = NumberFormat.getNumberInstance(Locale.US);
        integerFormatter.setParseIntegerOnly(true);
    }

    public int getValue() 
    {
        int retVal = 0;
        try 
	    {
		retVal = integerFormatter.parse(getText()).intValue();
	    }
	catch (ParseException e) 
	    {
		// This should never happen because insertString allows
		// only properly formatted data to get in the field.
		toolkit.beep();
	    }
        return retVal;
    }

    public void setValue(int value) 
    {
        setText(integerFormatter.format(value));
    }

    protected Document createDefaultModel() 
    {
        return new WholeNumberDocument();
    }

    protected class WholeNumberDocument extends PlainDocument 
    {
        public void insertString(int offs, String str,AttributeSet a) 
	    throws BadLocationException 
	{
            char[] source = str.toCharArray();
            char[] result = new char[source.length];
            int j = 0;
	    
            for (int i = 0; i < result.length; i++) 
		{
		    if (Character.isDigit(source[i]))
			result[j++] = source[i];
		    else 
			{
			    toolkit.beep();
			    //			    System.err.println("insertString: " + source[i]);
			}
		}
            super.insertString(offs, new String(result, 0, j), a);
        }
    }
}
