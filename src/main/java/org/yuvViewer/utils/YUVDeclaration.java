package org.yuvViewer.utils;

import java.awt.Dimension;

public final class YUVDeclaration {
    private YUVDeclaration() {}

    public enum YUVNames {
        SQCIF(128, 96),
        QCIF(176, 144),
        SIF(352, 240),
        CIF(352, 288),
        CIF4(704, 576),
        TV(720, 576),
        HD1(1280, 720),
        HD2(1920, 1080),
        CUSTOM(0, 0);

        private final Dimension dimension;

        YUVNames(int width, int height) {
            this.dimension = new Dimension(width, height);
        }

        /** @return the standard Dimension for this format, or null for CUSTOM */
        public Dimension getDimension() {
            return this == CUSTOM ? null : dimension;
        }
    }

    public static final int CC_YUV = 0;
    public static final int CC_Y   = 1;
}
