package org.yuvViewer.utils;

import java.io.File;
import java.awt.Dimension;

/**
 * <p>Provides methods</p>
 *
 * @author Patrick Zoerner
 * @version 1.0
 */
public class ExtensionUtils implements YUVDeclaration {
    /**
     * @param file the chosen file
     * @return the file extention as String
     */
    public static String getExtension(File file) {
        String ext = null;
        String s = file.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    /**
     * @param file the chosen file
     * @return True if the chosen file extension is a *.yuv file
     */
    public static boolean approveSelection(File file) {
        String ext = null;
        String s = file.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
		assert ext != null;
		if (ext.equals(sqcif.toLowerCase())) {
            return true;
        } else if (ext.equals(qcif.toLowerCase())) {
            return true;
        } else if (ext.equals(sif.toLowerCase())) {
            return true;
        } else if (ext.equals(cif.toLowerCase())) {
            return true;
        } else if (ext.equals(cif4.toLowerCase())) {
            return true;
        } else if (ext.equals(tv.toLowerCase())) {
            return true;
        } else return ext.equals("yuv");
	}

    public static Dimension getDimension(File file) {
        String ext = null;
        String s = file.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
		assert ext != null;
		if (ext.equals(sqcif.toLowerCase())) {
            return SQCIF;
        } else if (ext.equals(qcif.toLowerCase())) {
            return QCIF;
        } else if (ext.equals(sif.toLowerCase())) {
            return SIF;
        } else if (ext.equals(cif.toLowerCase())) {
            return CIF;
        } else if (ext.equals(cif4.toLowerCase())) {
            return CIF4;
        } else if (ext.equals(tv.toLowerCase())) {
            return TV;
        }
        return null;
    }

    /**
     * @param file the chosen File
     * @return filename
     */
    public static String getNameEnding(File file) {
        String name = null;
        String s = file.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1) {
            name = s.substring(0, file.getName().length() - 4) + ".yuv";
        }
        return name;
    }

    /**
     * @param file the chosen File
     * @return The basename.
     */
    public static String getBaseName(File file) {
        String baseName = null;
        String pathName = file.getPath();
        int i = pathName.lastIndexOf('.');
        if (i > 0 && i < pathName.length() - 1) {
            baseName = pathName.substring(0, (pathName.length() - 4));
        }
        return baseName;
    }

    public static File getDirectoryPath(File f) {
        String strDir = f.getPath();
        strDir = strDir.substring(0, strDir.lastIndexOf(File.separator));
        return new File(strDir);

    }

    public static File[] getFilesInDirectory(File f) {
        File dirFile = ExtensionUtils.getDirectoryPath(f);//new File(strDir);
        return dirFile.listFiles();
    }

}
