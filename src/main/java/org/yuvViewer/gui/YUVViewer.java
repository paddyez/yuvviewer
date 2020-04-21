package org.yuvViewer.gui;

import org.yuvViewer.utils.YUVDeclaration;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class YUVViewer extends Window implements MouseMotionListener, MouseListener {
    static {
        try {
            System.loadLibrary("calc");
        } catch (UnsatisfiedLinkError ule) {
            System.err.println(ule.getMessage());
        }
    }

    public static native int[] calculateFastRGBImage(boolean showY,
                                                     boolean showU,
                                                     boolean showV,
                                                     int width,
                                                     int height,
                                                     byte[] yData,
                                                     byte[] uData,
                                                     byte[] vData,
                                                     int[] rgbImage
    );

    public static native int[] calculateFastColoredRGBImage(int width,
                                                            int height,
                                                            byte[] yData,
                                                            byte[] uData,
                                                            byte[] vData,
                                                            int[] rgbImage
    );

    public static native int[] calculateRGBImage(boolean showY,
                                                 boolean showU,
                                                 boolean showV,
                                                 int width,
                                                 int height,
                                                 byte[] yData,
                                                 byte[] uData,
                                                 byte[] vData,
                                                 int[] rgbImage
    );

    public static native int[] resizeRGBImage(int scale,
                                              int width,
                                              int height,
                                              int[] scaledRGBImage,
                                              int[] rgbImage
    );

    MainFrame frame;
    File yuvFile;
    Dimension size;
    int colorspace;
    Point startPoint;
    Point lastLocation;
    transient RandomAccessFile randomAccessFile;

    byte[] yData;
    byte[] uData;
    byte[] vData;

    boolean showY = true;
    boolean showU = true;
    boolean showV = true;

    int[] rgbImage = null;
    int[] scaledRGBImage = null;
    transient BufferedImage bufferedImage;
    transient BufferedImage scaledImage = null;
    int scale = 1;
    int frameNumber;

    public YUVViewer(MainFrame frame, File yuvFile, Dimension size, int cc) {
        super(frame);

        this.frame = frame;
        this.yuvFile = yuvFile;
        this.size = size;
        this.colorspace = cc;
        if (!((size.width * size.height) % 2 == 0)) {
            System.err.println("Something fishy with the Dimension");
        }
        bufferedImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
        prepare();
        setSize(new Dimension(size.width * scale, size.height * scale));
        setLocationRelativeTo(frame);
        setLayout(null);
        addMouseListener(this);
        addMouseMotionListener(this);
        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        if (scale > 1) {
            g.drawImage(scaledImage, 0, 0, Color.BLACK, this);
        } else {
            g.drawImage(bufferedImage, 0, 0, Color.BLACK, this);
        }
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    void prepare() {
        frameNumber = 0;
        assert yuvFile.exists();
        int yLength = size.width * size.height;
        int uvLength = (size.width * size.height) / 4;
        yData = new byte[yLength];
        uData = new byte[uvLength];
        vData = new byte[uvLength];
        try {
            randomAccessFile = new RandomAccessFile(yuvFile, "r");
        } catch (FileNotFoundException | SecurityException fnfe) {
            System.err.println(fnfe.getMessage());
        }
        if (readData()) {
            System.out.println("End of file!");
        }
        rgbImage = new int[size.width * size.height];

        if (colorspace == YUVDeclaration.ccY) {
            setU(false);
            setV(false);
            frame.enableYUVCheckboxes(showY, showU, showV);
        }

        fillColors();
    }

    boolean readData() {
        try {
            int k, l, m;
            l = m = 0;

            k = randomAccessFile.read(yData);
            if (colorspace == YUVDeclaration.ccYUV) {
                l = randomAccessFile.read(uData);
                m = randomAccessFile.read(vData);
            }
            if (k == -1 || l == -1 || m == -1) {
                return true;
            }
        } catch (IOException | IndexOutOfBoundsException ioe) {
            System.err.println(ioe);
        }
        frameNumber++;
        frame.setFrameNumber(frameNumber);
        return false;
    }

    boolean readLastData() {
        frameNumber--;
        frameNumber = Math.max(frameNumber, 1);
        frame.setFrameNumber(frameNumber);
        int dataLength;
        if (colorspace == YUVDeclaration.ccYUV) {
            dataLength = size.width * size.height + size.width * size.height / 2;
        } else {
            dataLength = size.width * size.height;
        }
        try {
            int k, l, m;
            l = m = 0;
            randomAccessFile.seek(dataLength * (frameNumber - 1));
            k = randomAccessFile.read(yData);
            if (colorspace == YUVDeclaration.ccYUV) {
                l = randomAccessFile.read(uData);
                m = randomAccessFile.read(vData);
            }
            if (k == -1 || l == -1 || m == -1) {
                return true;
            }
        } catch (IOException | IndexOutOfBoundsException ioe) {
            System.err.println(ioe);
        }
        return false;
    }

    public class Play extends Thread {
        boolean threadSuspended = false;
        boolean endOfFile = false;

        public void setSuspended(boolean suspended) {
            threadSuspended = suspended;
            fillColors();
            repaint();
        }

        boolean getSuspended() {
            return threadSuspended;
        }

        boolean getEndOfFile() {
            return endOfFile;
        }

        @Override
        public void run() {
            while (!readData()) {
                fillFastColors();
                repaint();
                try {
                    if (threadSuspended) {
                        synchronized (this) {
                            while (threadSuspended)
                                wait();
                        }
                    }
                } catch (InterruptedException ie) {
                    System.err.println(ie.getMessage());
                }
            }
            endOfFile = true;
            threadSuspended = true;
        }
    }

    /**
     * Call native method with simple algorithem
     */
    void fillFastColors() {
        if (showY && showU && showV) {
            rgbImage = YUVViewer.calculateFastColoredRGBImage(size.width,
                    size.height,
                    yData,
                    uData,
                    vData,
                    rgbImage
            );
        } else {
            rgbImage = YUVViewer.calculateFastRGBImage(showY,
                    showU,
                    showV,
                    size.width,
                    size.height,
                    yData,
                    uData,
                    vData,
                    rgbImage
            );
        }

        if (scale > 1) {
            scaledRGBImage = YUVViewer.resizeRGBImage(scale,
                    size.width,
                    size.height,
                    scaledRGBImage,
                    rgbImage);
            scaledImage.setRGB(0, 0, size.width * scale, size.height * scale, scaledRGBImage, 0, size.width * scale);
        } else {
            bufferedImage.setRGB(0, 0, size.width, size.height, rgbImage, 0, size.width);
        }
    }

    /**
     * Call native method with exact algorithem
     */
    void fillColors() {
        rgbImage = YUVViewer.calculateRGBImage(showY,
                showU,
                showV,
                size.width,
                size.height,
                yData,
                uData,
                vData,
                rgbImage
        );
        if (scale > 1) {
            scaledRGBImage = YUVViewer.resizeRGBImage(scale,
                    size.width,
                    size.height,
                    scaledRGBImage,
                    rgbImage);
            scaledImage.setRGB(0, 0, size.width * scale, size.height * scale, scaledRGBImage, 0, size.width * scale);
        } else {
            bufferedImage.setRGB(0, 0, size.width, size.height, rgbImage, 0, size.width);
        }
    }

    public void setScale(int s) {
        scale = s;
        setSize(new Dimension(size.width * scale, size.height * scale));
        scaledRGBImage = null;
        scaledImage = null;
        scaledRGBImage = new int[size.width * scale * size.height * scale];
        if (scale > 1) {
            scaledRGBImage = YUVViewer.resizeRGBImage(scale,
                    size.width,
                    size.height,
                    scaledRGBImage,
                    rgbImage);
            scaledImage = new BufferedImage(size.width * scale, size.height * scale, BufferedImage.TYPE_INT_RGB);
            scaledImage.setRGB(0, 0, size.width * scale, size.height * scale, scaledRGBImage, 0, size.width * scale);
        } else {
            bufferedImage.setRGB(0, 0, size.width, size.height, rgbImage, 0, size.width);
        }
        repaint();
    }

    public void setY(boolean enabled) {
        showY = enabled;
        fillColors();
        repaint();
    }

    public void setU(boolean enabled) {
        showU = enabled;
        fillColors();
        repaint();
    }

    public void setV(boolean enabled) {
        showV = enabled;
        fillColors();
        repaint();
    }

    @Override
    public void dispose() {
        super.dispose();
        rgbImage = null;
        scaledRGBImage = null;
        bufferedImage = null;
        scaledImage = null;
    }

    public void locationUpdate(Point difference) {
        setLocation(difference.x + lastLocation.x, difference.y + lastLocation.y);
        Toolkit.getDefaultToolkit().sync();
        repaint();

    }

    public void updateLastLocation() {
        lastLocation = getLocation();
    }


    /****************************************************************
     * Handle mouse clicks and drags in the control panel
     ****************************************************************/
    public void mouseDragged(MouseEvent mouseEvent) {
        lastLocation = getLocation();
        setLocation(lastLocation.x - startPoint.x + mouseEvent.getPoint().x, lastLocation.y - startPoint.y + mouseEvent.getPoint().y);
        Toolkit.getDefaultToolkit().sync();
        repaint();
    }

    public void mousePressed(MouseEvent mouseEvent) {
        startPoint = mouseEvent.getPoint();
    }

    public void mouseReleased(MouseEvent mouseEvent) {
        //	System.out.println("released");
    }

    public void mouseEntered(MouseEvent mouseEvent) {
        //	System.out.println("entered");
    }

    public void mouseExited(MouseEvent mouseEvent) {
        //	System.out.println("exited");
    }

    public void mouseMoved(MouseEvent mouseEvent) {
        //	System.out.println("moved");
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        //	System.out.println("clicked");
    }
}
