package org.yuvViewer.utils;
import java.io.File;
import java.awt.Dimension;
/**
 * <p>Provides methods</p>
 *
 * @author Patrick-Emil Zörner
 * @version 1.0
 */
public class ExtensionUtils implements YUVDeclaration {
    private ExtensionUtils() {
    }
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
		if (ext.equalsIgnoreCase(YUVNames.SQCIF.name())) {
            return true;
        } else if (ext.equalsIgnoreCase(YUVNames.QCIF.name())) {
            return true;
        } else if (ext.equalsIgnoreCase(YUVNames.SIF.name())) {
            return true;
        } else if (ext.equalsIgnoreCase(YUVNames.CIF.name())) {
            return true;
        } else if (ext.equalsIgnoreCase(YUVNames.CIF4.name())) {
            return true;
        } else if (ext.equalsIgnoreCase(YUVNames.TV.name())) {
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
		if (ext.equalsIgnoreCase(YUVNames.SQCIF.name())) {
            return SQCIF_DIMENSION;
        } else if (ext.equalsIgnoreCase(YUVNames.QCIF.name())) {
            return QCIF_DIMENSION;
        } else if (ext.equalsIgnoreCase(YUVNames.SIF.name())) {
            return SIF_DIMENSION;
        } else if (ext.equalsIgnoreCase(YUVNames.CIF.name())) {
            return CIF_DIMENSION;
        } else if (ext.equalsIgnoreCase(YUVNames.CIF4.name())) {
            return CIF4_DIMENSION;
        } else if (ext.equalsIgnoreCase(YUVNames.TV.name())) {
            return TV_DIMENSION;
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
            name = s.substring(0, i) + ".yuv";
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
            baseName = pathName.substring(0, i);
        }
        return baseName;
    }
    public static File getDirectoryPath(File f) {
        File parent = f.getParentFile();
        if (parent != null) {
            return parent;
        }
        return new File(".");
    }
    public static File[] getFilesInDirectory(File f) {
        File dirFile = ExtensionUtils.getDirectoryPath(f);//new File(strDir);
        return dirFile.listFiles();
    }
}