#include <jni.h>
#include "org_yuvViewer_gui_YUVViewer.h"

/**
 * http://msdn.microsoft.com/library/en-us/dnwmt/html/YUVFormats.asp
 * R = clip(( 298 * C           + 409 * E + 128) >> 8)
 * G = clip(( 298 * C - 100 * D - 208 * E + 128) >> 8)
 * B = clip(( 298 * C + 516 * D           + 128) >> 8)
 */
JNIEXPORT jintArray

JNICALL
Java_org_yuvViewer_gui_YUVViewer_calculateFastRGBImage(JNIEnv *env,
                                                       jclass clazz,
                                                       jboolean showY,
                                                       jboolean showU,
                                                       jboolean showV,
                                                       jint width,
                                                       jint height,
                                                       jbyteArray yData,
                                                       jbyteArray uData,
                                                       jbyteArray vData,
                                                       jintArray rgbImage) {
    jint *rgbInt = (*env)->GetIntArrayElements(env, rgbImage, 0);
    jbyte *cyData = (*env)->GetByteArrayElements(env, yData, 0);
    jbyte *cuData = (*env)->GetByteArrayElements(env, uData, 0);
    jbyte *cvData = (*env)->GetByteArrayElements(env, vData, 0);
    int i, j;

    if (showY && showU && showV) {
        for (i = 0; i < height; i += 2) {
            for (j = 0; j < width; j += 2) {
                int pos = j + width * i;
                int cb = (cuData[j / 2 + i / 4 * width] & 255) - 128;
                int cr = (cvData[j / 2 + i / 4 * width] & 255) - 128;
                int r_c = 409 * cr + 128;
                int g_c = -100 * cb - 208 * cr + 128;
                int b_c = 516 * cb + 128;

                int p_idx[4] = {pos, pos + 1, pos + width, pos + width + 1};
                for (int k = 0; k < 4; k++) {
                    int p = p_idx[k];
                    int cy = 298 * ((cyData[p] & 255) - 16);
                    int red = (cy + r_c) >> 8;
                    int green = (cy + g_c) >> 8;
                    int blue = (cy + b_c) >> 8;
                    rgbInt[p] = (blue > 255 ? 255 : blue < 0 ? 0 : blue) |
                                ((green > 255 ? 255 : green < 0 ? 0 : green) << 8) |
                                ((red > 255 ? 255 : red < 0 ? 0 : red) << 16);
                }
            }
        }
    } else if (showY && !showU && !showV) {
        for (i = 0; i < height * width; i++) {
            int y = cyData[i] & 255;
            rgbInt[i] = y | (y << 8) | (y << 16);
        }
    } else if (!showY && showU && !showV) {
        for (i = 0; i < height; i += 2) {
            for (j = 0; j < width; j += 2) {
                int pos = j + width * i;
                int u = cuData[j / 2 + i / 4 * width] & 255;
                int val = u | (u << 8) | (u << 16);
                rgbInt[pos] = rgbInt[pos + 1] = rgbInt[pos + width] = rgbInt[pos + width + 1] = val;
            }
        }
    } else if (!showY && !showU && showV) {
        for (i = 0; i < height; i += 2) {
            for (j = 0; j < width; j += 2) {
                int pos = j + width * i;
                int v = cvData[j / 2 + i / 4 * width] & 255;
                int val = v | (v << 8) | (v << 16);
                rgbInt[pos] = rgbInt[pos + 1] = rgbInt[pos + width] = rgbInt[pos + width + 1] = val;
            }
        }
    } else {
        for (i = 0; i < height; i += 2) {
            for (j = 0; j < width; j += 2) {
                int pos = j + width * i;
                int cb = (showU ? (cuData[j / 2 + i / 4 * width] & 255) : 0) - 128;
                int cr = (showV ? (cvData[j / 2 + i / 4 * width] & 255) : 0) - 128;
                int r_c = 409 * cr + 128;
                int g_c = -100 * cb - 208 * cr + 128;
                int b_c = 516 * cb + 128;

                int p_idx[4] = {pos, pos + 1, pos + width, pos + width + 1};
                for (int k = 0; k < 4; k++) {
                    int p = p_idx[k];
                    int cy = 298 * ((showY ? (cyData[p] & 255) : 0) - 16);
                    int red = (cy + r_c) >> 8;
                    int green = (cy + g_c) >> 8;
                    int blue = (cy + b_c) >> 8;
                    rgbInt[p] = (blue > 255 ? 255 : blue < 0 ? 0 : blue) |
                                ((green > 255 ? 255 : green < 0 ? 0 : green) << 8) |
                                ((red > 255 ? 255 : red < 0 ? 0 : red) << 16);
                }
            }
        }
    }

    (*env)->ReleaseByteArrayElements(env, yData, cyData, 0);
    (*env)->ReleaseByteArrayElements(env, uData, cuData, 0);
    (*env)->ReleaseByteArrayElements(env, vData, cvData, 0);
    (*env)->ReleaseIntArrayElements(env, rgbImage, rgbInt, 0);
    return rgbImage;
}

JNIEXPORT jintArray

JNICALL
Java_org_yuvViewer_gui_YUVViewer_calculateFastColoredRGBImage(JNIEnv *env,
                                                              jclass clazz,
                                                              jint width,
                                                              jint height,
                                                              jbyteArray yData,
                                                              jbyteArray uData,
                                                              jbyteArray vData,
                                                              jintArray rgbImage) {
    jint *rgbInt = (*env)->GetIntArrayElements(env, rgbImage, 0);
    jbyte *cyData = (*env)->GetByteArrayElements(env, yData, 0);
    jbyte *cuData = (*env)->GetByteArrayElements(env, uData, 0);
    jbyte *cvData = (*env)->GetByteArrayElements(env, vData, 0);
    int i, j;
    int red, green, blue;

    for (i = 0; i < height; i += 2) {
        for (j = 0; j < width; j += 2) {
            int pos = j + width * i;
            int cy = cyData[pos] & 255;
            int cb = cuData[j / 2 + i / 4 * width] & 255;
            int cr = cvData[j / 2 + i / 4 * width] & 255;
            cy -= 16;
            cb -= 128;
            cr -= 128;
            red = (298 * cy + 409 * cr + 128) >> 8;
            green = (298 * cy - 100 * cb - 208 * cr + 128) >> 8;
            blue = (298 * cy + 516 * cb + 128) >> 8;

            //clipping
            red = red > 255 ? 255 : red < 0 ? 0 : red;
            green = green > 255 ? 255 : green < 0 ? 0 : green;
            blue = blue > 255 ? 255 : blue < 0 ? 0 : blue;
            rgbInt[pos] = blue | (green << 8) | (red << 16);

            cy = cyData[pos + 1] & 255;
            cy -= 16;
            red = (298 * cy + 409 * cr + 128) >> 8;
            green = (298 * cy - 100 * cb - 208 * cr + 128) >> 8;
            blue = (298 * cy + 516 * cb + 128) >> 8;

            //clipping
            red = red > 255 ? 255 : red < 0 ? 0 : red;
            green = green > 255 ? 255 : green < 0 ? 0 : green;
            blue = blue > 255 ? 255 : blue < 0 ? 0 : blue;
            rgbInt[pos + 1] = blue | (green << 8) | (red << 16);

            cy = cyData[pos + width] & 255;
            cy -= 16;
            red = (298 * cy + 409 * cr + 128) >> 8;
            green = (298 * cy - 100 * cb - 208 * cr + 128) >> 8;
            blue = (298 * cy + 516 * cb + 128) >> 8;
            //clipping
            red = red > 255 ? 255 : red < 0 ? 0 : red;
            green = green > 255 ? 255 : green < 0 ? 0 : green;
            blue = blue > 255 ? 255 : blue < 0 ? 0 : blue;
            rgbInt[pos + width] = blue | (green << 8) | (red << 16);

            cy = cyData[pos + width + 1] & 255;
            cy -= 16;
            red = (298 * cy + 409 * cr + 128) >> 8;
            green = (298 * cy - 100 * cb - 208 * cr + 128) >> 8;
            blue = (298 * cy + 516 * cb + 128) >> 8;
            //clipping
            red = red > 255 ? 255 : red < 0 ? 0 : red;
            green = green > 255 ? 255 : green < 0 ? 0 : green;
            blue = blue > 255 ? 255 : blue < 0 ? 0 : blue;
            rgbInt[pos + width + 1] = blue | (green << 8) | (red << 16);
        }
    }
    (*env)->ReleaseByteArrayElements(env, yData, cyData, 0);
    (*env)->ReleaseByteArrayElements(env, uData, cuData, 0);
    (*env)->ReleaseByteArrayElements(env, vData, cvData, 0);
    (*env)->ReleaseIntArrayElements(env, rgbImage, rgbInt, 0);
    return rgbImage;
}

/**
 * http://msdn.microsoft.com/library/en-us/dnwmt/html/YUVFormats.asp
 * R = clip( round( 1.164383 * C                   + 1.596027 * E  ) )
 * G = clip( round( 1.164383 * C - (0.391762 * D) - (0.812968 * E) ) )
 * B = clip( round( 1.164383 * C +  2.017232 * D                   ) )
 */
JNIEXPORT jintArray

JNICALL
Java_org_yuvViewer_gui_YUVViewer_calculateRGBImage(JNIEnv *env,
                                                   jclass clazz,
                                                   jboolean showY,
                                                   jboolean showU,
                                                   jboolean showV,
                                                   jint width,
                                                   jint height,
                                                   jbyteArray yData,
                                                   jbyteArray uData,
                                                   jbyteArray vData,
                                                   jintArray rgbImage) {
    jint *rgbInt = (*env)->GetIntArrayElements(env, rgbImage, 0);
    jbyte *cyData = (*env)->GetByteArrayElements(env, yData, 0);
    jbyte *cuData = (*env)->GetByteArrayElements(env, uData, 0);
    jbyte *cvData = (*env)->GetByteArrayElements(env, vData, 0);
    int i, j;

    if (showY && showU && showV) {
        for (i = 0; i < height; i += 2) {
            for (j = 0; j < width; j += 2) {
                int pos = j + width * i;
                int cb = (cuData[j / 2 + i / 4 * width] & 255) - 128;
                int cr = (cvData[j / 2 + i / 4 * width] & 255) - 128;
                double r_c = 1.596027 * cr;
                double g_c = -0.391762 * cb - 0.812968 * cr;
                double b_c = 2.017232 * cb;

                int p_idx[4] = {pos, pos + 1, pos + width, pos + width + 1};
                for (int k = 0; k < 4; k++) {
                    int p = p_idx[k];
                    double cy = 1.164383 * ((cyData[p] & 255) - 16);
                    int red = (int) (cy + r_c);
                    int green = (int) (cy + g_c);
                    int blue = (int) (cy + b_c);
                    rgbInt[p] = (blue > 255 ? 255 : blue < 0 ? 0 : blue) |
                                ((green > 255 ? 255 : green < 0 ? 0 : green) << 8) |
                                ((red > 255 ? 255 : red < 0 ? 0 : red) << 16);
                }
            }
        }
    } else if (showY && !showU && !showV) {
        for (i = 0; i < height * width; i++) {
            int y = cyData[i] & 255;
            rgbInt[i] = y | (y << 8) | (y << 16);
        }
    } else if (!showY && showU && !showV) {
        for (i = 0; i < height; i += 2) {
            for (j = 0; j < width; j += 2) {
                int pos = j + width * i;
                int u = cuData[j / 2 + i / 4 * width] & 255;
                int val = u | (u << 8) | (u << 16);
                rgbInt[pos] = rgbInt[pos + 1] = rgbInt[pos + width] = rgbInt[pos + width + 1] = val;
            }
        }
    } else if (!showY && !showU && showV) {
        for (i = 0; i < height; i += 2) {
            for (j = 0; j < width; j += 2) {
                int pos = j + width * i;
                int v = cvData[j / 2 + i / 4 * width] & 255;
                int val = v | (v << 8) | (v << 16);
                rgbInt[pos] = rgbInt[pos + 1] = rgbInt[pos + width] = rgbInt[pos + width + 1] = val;
            }
        }
    } else {
        for (i = 0; i < height; i += 2) {
            for (j = 0; j < width; j += 2) {
                int pos = j + width * i;
                int cb = (showU ? (cuData[j / 2 + i / 4 * width] & 255) : 0) - 128;
                int cr = (showV ? (cvData[j / 2 + i / 4 * width] & 255) : 0) - 128;
                double r_c = 1.596027 * cr;
                double g_c = -0.391762 * cb - 0.812968 * cr;
                double b_c = 2.017232 * cb;

                int p_idx[4] = {pos, pos + 1, pos + width, pos + width + 1};
                for (int k = 0; k < 4; k++) {
                    int p = p_idx[k];
                    double cy = 1.164383 * ((showY ? (cyData[p] & 255) : 0) - 16);
                    int red = (int) (cy + r_c);
                    int green = (int) (cy + g_c);
                    int blue = (int) (cy + b_c);
                    rgbInt[p] = (blue > 255 ? 255 : blue < 0 ? 0 : blue) |
                                ((green > 255 ? 255 : green < 0 ? 0 : green) << 8) |
                                ((red > 255 ? 255 : red < 0 ? 0 : red) << 16);
                }
            }
        }
    }

    (*env)->ReleaseByteArrayElements(env, yData, cyData, 0);
    (*env)->ReleaseByteArrayElements(env, uData, cuData, 0);
    (*env)->ReleaseByteArrayElements(env, vData, cvData, 0);
    (*env)->ReleaseIntArrayElements(env, rgbImage, rgbInt, 0);
    return rgbImage;
}

/**
 * Scaling the Image 1:2,1:4,1:8
 */
JNIEXPORT jintArray

JNICALL
Java_org_yuvViewer_gui_YUVViewer_resizeRGBImage(JNIEnv *env,
                                                jclass clazz,
                                                jint scale,
                                                jint width,
                                                jint height,
                                                jintArray scaledRGBImage,
                                                jintArray rgbImage) {
    jint *scaledImage = (*env)->GetIntArrayElements(env, scaledRGBImage, 0);
    jint *image = (*env)->GetIntArrayElements(env, rgbImage, 0);
    for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
            int t = image[j + i * width];
            switch (scale) {
                case 1:
                    scaledImage[(scale * j + 0) + (scale * i + 0) * scale * width] = t;
                    break;
                case 2:
                    scaledImage[(scale * j + 0) + (scale * i + 0) * scale * width] = t;
                    scaledImage[(scale * j + 0) + (scale * i + 1) * scale * width] = t;
                    scaledImage[(scale * j + 1) + (scale * i + 0) * scale * width] = t;
                    scaledImage[(scale * j + 1) + (scale * i + 1) * scale * width] = t;
                    break;
                case 4:
                    for (int k = 0; k < 4; k++) {
                        for (int l = 0; l < 4; l++) {
                            scaledImage[(scale * j + k) + (scale * i + l) * scale * width] = t;
                        }
                    }

                    break;
                default:
                    for (int k = 0; k < scale; k++) {
                        for (int l = 0; l < scale; l++) {
                            scaledImage[(scale * j + k) + (scale * i + l) * scale * width] = t;
                        }
                    }
            }
        }
    }
    (*env)->ReleaseIntArrayElements(env, scaledRGBImage, scaledImage, 0);
    (*env)->ReleaseIntArrayElements(env, rgbImage, image, 0);
    return scaledRGBImage;
}
