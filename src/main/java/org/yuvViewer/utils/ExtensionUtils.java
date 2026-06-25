package org.yuvViewer.utils;
import java.awt.Dimension;
import java.io.File;
/**
 * <p>Provides methods</p>
 *
 * @author Patrick-Emil Zörner
 * @version 1.0
 */
public class ExtensionUtils {
    private static final java.util.Map<String, Dimension> EXTENSION_DIMENSIONS;
    static {
        EXTENSION_DIMENSIONS = new java.util.HashMap<>();
        for (YUVDeclaration.YUVNames name : YUVDeclaration.YUVNames.values()) {
            Dimension dim = name.getDimension();
            if (dim != null) {
                EXTENSION_DIMENSIONS.put(name.name().toLowerCase(), dim);
            }
        }
    }

    private ExtensionUtils() {
    }

    /**
     * @param file the chosen file
     * @return the file extension as String, or null if none found
     */
    public static String getExtension(File file) {
        String s = file.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1) {
            return s.substring(i + 1).toLowerCase();
        }
        return null;
    }

    /**
     * @param file the chosen file
     * @return True if the chosen file extension is a known YUV format
     */
    public static boolean approveSelection(File file) {
        String ext = getExtension(file);
        if (ext == null) return false;
        return ext.equals("yuv") || EXTENSION_DIMENSIONS.containsKey(ext);
    }

    /**
     * @param file the chosen file
     * @return the Dimension for the given file extension, or null for custom/yuv
     */
    public static Dimension getDimension(File file) {
        String ext = getExtension(file);
        if (ext == null) return null;
        return EXTENSION_DIMENSIONS.get(ext);
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