#include <jni.h>
#include <smmintrin.h>
#include "org_yuvViewer_gui_YUVViewer.h"

#define CLIP(x) ((x) > 255 ? 255 : ((x) < 0 ? 0 : (x)))
#define PACK_RGB(r, g, b) (CLIP(b) | (CLIP(g) << 8) | (CLIP(r) << 16))

static inline void yuv_to_rgb_sse_8px(const jbyte *pY, __m128i u16, __m128i v16, jint *pRGB) {
    __m128i zero = _mm_setzero_si128();
    __m128i v128 = _mm_set1_epi16(128);
    __m128i v16_16 = _mm_set1_epi16(16);
    __m128i v298 = _mm_set1_epi16(298);
    __m128i v409 = _mm_set1_epi16(409);
    __m128i v100 = _mm_set1_epi16(-100);
    __m128i v208 = _mm_set1_epi16(-208);
    __m128i v516 = _mm_set1_epi16(516);

    __m128i y_raw = _mm_loadl_epi64((const __m128i *) pY);
    __m128i y16 = _mm_mullo_epi16(_mm_sub_epi16(_mm_unpacklo_epi8(y_raw, zero), v16_16), v298);

    __m128i r16 = _mm_srai_epi16(_mm_add_epi16(y16, _mm_add_epi16(_mm_mullo_epi16(v16, v409), v128)), 8);
    __m128i g16 = _mm_srai_epi16(_mm_add_epi16(y16, _mm_add_epi16(_mm_add_epi16(_mm_mullo_epi16(u16, v100), _mm_mullo_epi16(v16, v208)), v128)), 8);
    __m128i b16 = _mm_srai_epi16(_mm_add_epi16(y16, _mm_add_epi16(_mm_mullo_epi16(u16, v516), v128)), 8);

    __m128i r8 = _mm_packus_epi16(r16, zero);
    __m128i g8 = _mm_packus_epi16(g16, zero);
    __m128i b8 = _mm_packus_epi16(b16, zero);

    __m128i bg = _mm_unpacklo_epi8(b8, g8);
    __m128i rz = _mm_unpacklo_epi8(r8, zero);

    __m128i bgrz_lo = _mm_unpacklo_epi16(bg, rz);
    __m128i bgrz_hi = _mm_unpackhi_epi16(bg, rz);

    _mm_storeu_si128((__m128i *) pRGB, bgrz_lo);
    _mm_storeu_si128((__m128i *) (pRGB + 4), bgrz_hi);
}

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
        int width2 = width / 2;
        __m128i zero = _mm_setzero_si128();
        __m128i v128 = _mm_set1_epi16(128);
        for (i = 0; i < height; i += 2) {
            jbyte *pY1 = cyData + i * width;
            jbyte *pY2 = pY1 + width;
            jint *pRGB1 = rgbInt + i * width;
            jint *pRGB2 = pRGB1 + width;
            jbyte *pU = cuData + (i / 2) * width2;
            jbyte *pV = cvData + (i / 2) * width2;

            j = 0;
            for (; j <= width2 - 4; j += 4) {
                __m128i u_raw = _mm_cvtsi32_si128(*(const int *) pU); pU += 4;
                __m128i v_raw = _mm_cvtsi32_si128(*(const int *) pV); pV += 4;
                __m128i u8 = _mm_unpacklo_epi8(u_raw, u_raw);
                __m128i v8 = _mm_unpacklo_epi8(v_raw, v_raw);
                __m128i u16 = _mm_sub_epi16(_mm_unpacklo_epi8(u8, zero), v128);
                __m128i v16 = _mm_sub_epi16(_mm_unpacklo_epi8(v8, zero), v128);
                yuv_to_rgb_sse_8px(pY1, u16, v16, pRGB1); pY1 += 8; pRGB1 += 8;
                yuv_to_rgb_sse_8px(pY2, u16, v16, pRGB2); pY2 += 8; pRGB2 += 8;
            }

            for (; j < width2; j++) {
                int cb = (*pU++ & 255) - 128;
                int cr = (*pV++ & 255) - 128;
                int r_c = 409 * cr + 128;
                int g_c = -100 * cb - 208 * cr + 128;
                int b_c = 516 * cb + 128;

                int cy;
                cy = 298 * ((*pY1++ & 255) - 16);
                *pRGB1++ = PACK_RGB((cy + r_c) >> 8, (cy + g_c) >> 8, (cy + b_c) >> 8);
                cy = 298 * ((*pY1++ & 255) - 16);
                *pRGB1++ = PACK_RGB((cy + r_c) >> 8, (cy + g_c) >> 8, (cy + b_c) >> 8);
                cy = 298 * ((*pY2++ & 255) - 16);
                *pRGB2++ = PACK_RGB((cy + r_c) >> 8, (cy + g_c) >> 8, (cy + b_c) >> 8);
                cy = 298 * ((*pY2++ & 255) - 16);
                *pRGB2++ = PACK_RGB((cy + r_c) >> 8, (cy + g_c) >> 8, (cy + b_c) >> 8);
            }
        }
    } else if (showY && !showU && !showV) {
        jbyte *pY = cyData;
        jint *pRGB = rgbInt;
        int total = height * width;
        i = 0;
        __m128i mask = _mm_setr_epi8(0, 0, 0, -1, 1, 1, 1, -1, 2, 2, 2, -1, 3, 3, 3, -1);
        for (; i <= total - 4; i += 4) {
            __m128i y = _mm_cvtsi32_si128(*(const int *) pY); pY += 4;
            __m128i rgb = _mm_shuffle_epi8(y, mask);
            _mm_storeu_si128((__m128i *) pRGB, rgb); pRGB += 4;
        }
        for (; i < total; i++) {
            int y = *pY++ & 255;
            *pRGB++ = y | (y << 8) | (y << 16);
        }
    } else if (!showY && (showU ^ showV)) {
        jbyte *chromaData = showU ? cuData : cvData;
        int width2 = width / 2;
        __m128i mask = _mm_setr_epi8(0, 0, 0, -1, 0, 0, 0, -1, 1, 1, 1, -1, 1, 1, 1, -1);
        for (i = 0; i < height; i += 2) {
            jbyte *pC = chromaData + (i / 2) * width2;
            jint *pRGB1 = rgbInt + i * width;
            jint *pRGB2 = pRGB1 + width;
            j = 0;
            for (; j <= width2 - 2; j += 2) {
                short val = *(const short *) pC; pC += 2;
                __m128i c = _mm_cvtsi32_si128(val);
                __m128i rgb = _mm_shuffle_epi8(c, mask);
                _mm_storeu_si128((__m128i *) pRGB1, rgb); pRGB1 += 4;
                _mm_storeu_si128((__m128i *) pRGB2, rgb); pRGB2 += 4;
            }
            for (; j < width2; j++) {
                int val = *pC++ & 255;
                int rgbVal = val | (val << 8) | (val << 16);
                *pRGB1++ = rgbVal;
                *pRGB1++ = rgbVal;
                *pRGB2++ = rgbVal;
                *pRGB2++ = rgbVal;
            }
        }
    } else {
        int width2 = width / 2;
        for (i = 0; i < height; i += 2) {
            jbyte *pY1 = cyData + i * width;
            jbyte *pY2 = pY1 + width;
            jint *pRGB1 = rgbInt + i * width;
            jint *pRGB2 = pRGB1 + width;
            jbyte *pU = cuData + (i / 2) * width2;
            jbyte *pV = cvData + (i / 2) * width2;

            for (j = 0; j < width2; j++) {
                int cb = (showU ? (*pU & 255) : 0) - 128; pU++;
                int cr = (showV ? (*pV & 255) : 0) - 128; pV++;
                int r_c = 409 * cr + 128;
                int g_c = -100 * cb - 208 * cr + 128;
                int b_c = 516 * cb + 128;

                int cy;
                cy = 298 * ((showY ? (*pY1 & 255) : 0) - 16); pY1++;
                *pRGB1++ = PACK_RGB((cy + r_c) >> 8, (cy + g_c) >> 8, (cy + b_c) >> 8);
                cy = 298 * ((showY ? (*pY1 & 255) : 0) - 16); pY1++;
                *pRGB1++ = PACK_RGB((cy + r_c) >> 8, (cy + g_c) >> 8, (cy + b_c) >> 8);
                cy = 298 * ((showY ? (*pY2 & 255) : 0) - 16); pY2++;
                *pRGB2++ = PACK_RGB((cy + r_c) >> 8, (cy + g_c) >> 8, (cy + b_c) >> 8);
                cy = 298 * ((showY ? (*pY2 & 255) : 0) - 16); pY2++;
                *pRGB2++ = PACK_RGB((cy + r_c) >> 8, (cy + g_c) >> 8, (cy + b_c) >> 8);
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

    int width2 = width / 2;
    __m128i zero = _mm_setzero_si128();
    __m128i v128 = _mm_set1_epi16(128);
    for (i = 0; i < height; i += 2) {
        jbyte *pY1 = cyData + i * width;
        jbyte *pY2 = pY1 + width;
        jint *pRGB1 = rgbInt + i * width;
        jint *pRGB2 = pRGB1 + width;
        jbyte *pU = cuData + (i / 2) * width2;
        jbyte *pV = cvData + (i / 2) * width2;

        j = 0;
        for (; j <= width2 - 4; j += 4) {
            __m128i u_raw = _mm_cvtsi32_si128(*(const int *) pU); pU += 4;
            __m128i v_raw = _mm_cvtsi32_si128(*(const int *) pV); pV += 4;
            __m128i u8 = _mm_unpacklo_epi8(u_raw, u_raw);
            __m128i v8 = _mm_unpacklo_epi8(v_raw, v_raw);
            __m128i u16 = _mm_sub_epi16(_mm_unpacklo_epi8(u8, zero), v128);
            __m128i v16 = _mm_sub_epi16(_mm_unpacklo_epi8(v8, zero), v128);
            yuv_to_rgb_sse_8px(pY1, u16, v16, pRGB1); pY1 += 8; pRGB1 += 8;
            yuv_to_rgb_sse_8px(pY2, u16, v16, pRGB2); pY2 += 8; pRGB2 += 8;
        }

        for (; j < width2; j++) {
            int cb = (*pU++ & 255) - 128;
            int cr = (*pV++ & 255) - 128;
            int r_c = 409 * cr + 128;
            int g_c = -100 * cb - 208 * cr + 128;
            int b_c = 516 * cb + 128;

            int cy;
            cy = 298 * ((*pY1++ & 255) - 16);
            *pRGB1++ = PACK_RGB((cy + r_c) >> 8, (cy + g_c) >> 8, (cy + b_c) >> 8);
            cy = 298 * ((*pY1++ & 255) - 16);
            *pRGB1++ = PACK_RGB((cy + r_c) >> 8, (cy + g_c) >> 8, (cy + b_c) >> 8);
            cy = 298 * ((*pY2++ & 255) - 16);
            *pRGB2++ = PACK_RGB((cy + r_c) >> 8, (cy + g_c) >> 8, (cy + b_c) >> 8);
            cy = 298 * ((*pY2++ & 255) - 16);
            *pRGB2++ = PACK_RGB((cy + r_c) >> 8, (cy + g_c) >> 8, (cy + b_c) >> 8);
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
        int width2 = width / 2;
        __m128i zero = _mm_setzero_si128();
        __m128i v128 = _mm_set1_epi16(128);
        for (i = 0; i < height; i += 2) {
            jbyte *pY1 = cyData + i * width;
            jbyte *pY2 = pY1 + width;
            jint *pRGB1 = rgbInt + i * width;
            jint *pRGB2 = pRGB1 + width;
            jbyte *pU = cuData + (i / 2) * width2;
            jbyte *pV = cvData + (i / 2) * width2;

            j = 0;
            for (; j <= width2 - 4; j += 4) {
                __m128i u_raw = _mm_cvtsi32_si128(*(const int *) pU); pU += 4;
                __m128i v_raw = _mm_cvtsi32_si128(*(const int *) pV); pV += 4;
                __m128i u8 = _mm_unpacklo_epi8(u_raw, u_raw);
                __m128i v8 = _mm_unpacklo_epi8(v_raw, v_raw);
                __m128i u16 = _mm_sub_epi16(_mm_unpacklo_epi8(u8, zero), v128);
                __m128i v16 = _mm_sub_epi16(_mm_unpacklo_epi8(v8, zero), v128);
                yuv_to_rgb_sse_8px(pY1, u16, v16, pRGB1); pY1 += 8; pRGB1 += 8;
                yuv_to_rgb_sse_8px(pY2, u16, v16, pRGB2); pY2 += 8; pRGB2 += 8;
            }

            for (; j < width2; j++) {
                int cb = (*pU++ & 255) - 128;
                int cr = (*pV++ & 255) - 128;
                double r_c = 1.596027 * cr;
                double g_c = -0.391762 * cb - 0.812968 * cr;
                double b_c = 2.017232 * cb;

                double cy;
                cy = 1.164383 * ((*pY1++ & 255) - 16);
                *pRGB1++ = PACK_RGB((int) (cy + r_c), (int) (cy + g_c), (int) (cy + b_c));
                cy = 1.164383 * ((*pY1++ & 255) - 16);
                *pRGB1++ = PACK_RGB((int) (cy + r_c), (int) (cy + g_c), (int) (cy + b_c));
                cy = 1.164383 * ((*pY2++ & 255) - 16);
                *pRGB2++ = PACK_RGB((int) (cy + r_c), (int) (cy + g_c), (int) (cy + b_c));
                cy = 1.164383 * ((*pY2++ & 255) - 16);
                *pRGB2++ = PACK_RGB((int) (cy + r_c), (int) (cy + g_c), (int) (cy + b_c));
            }
        }
    } else if (showY && !showU && !showV) {
        jbyte *pY = cyData;
        jint *pRGB = rgbInt;
        int total = height * width;
        i = 0;
        __m128i mask = _mm_setr_epi8(0, 0, 0, -1, 1, 1, 1, -1, 2, 2, 2, -1, 3, 3, 3, -1);
        for (; i <= total - 4; i += 4) {
            __m128i y = _mm_cvtsi32_si128(*(const int *) pY); pY += 4;
            __m128i rgb = _mm_shuffle_epi8(y, mask);
            _mm_storeu_si128((__m128i *) pRGB, rgb); pRGB += 4;
        }
        for (; i < total; i++) {
            int y = *pY++ & 255;
            *pRGB++ = y | (y << 8) | (y << 16);
        }
    } else if (!showY && (showU ^ showV)) {
        jbyte *chromaData = showU ? cuData : cvData;
        int width2 = width / 2;
        __m128i mask = _mm_setr_epi8(0, 0, 0, -1, 0, 0, 0, -1, 1, 1, 1, -1, 1, 1, 1, -1);
        for (i = 0; i < height; i += 2) {
            jbyte *pC = chromaData + (i / 2) * width2;
            jint *pRGB1 = rgbInt + i * width;
            jint *pRGB2 = pRGB1 + width;
            j = 0;
            for (; j <= width2 - 2; j += 2) {
                short val = *(const short *) pC; pC += 2;
                __m128i c = _mm_cvtsi32_si128(val);
                __m128i rgb = _mm_shuffle_epi8(c, mask);
                _mm_storeu_si128((__m128i *) pRGB1, rgb); pRGB1 += 4;
                _mm_storeu_si128((__m128i *) pRGB2, rgb); pRGB2 += 4;
            }
            for (; j < width2; j++) {
                int val = *pC++ & 255;
                int rgbVal = val | (val << 8) | (val << 16);
                *pRGB1++ = rgbVal;
                *pRGB1++ = rgbVal;
                *pRGB2++ = rgbVal;
                *pRGB2++ = rgbVal;
            }
        }
    } else {
        int width2 = width / 2;
        for (i = 0; i < height; i += 2) {
            jbyte *pY1 = cyData + i * width;
            jbyte *pY2 = pY1 + width;
            jint *pRGB1 = rgbInt + i * width;
            jint *pRGB2 = pRGB1 + width;
            jbyte *pU = cuData + (i / 2) * width2;
            jbyte *pV = cvData + (i / 2) * width2;

            for (j = 0; j < width2; j++) {
                int cb = (showU ? (*pU & 255) : 0) - 128; pU++;
                int cr = (showV ? (*pV & 255) : 0) - 128; pV++;
                double r_c = 1.596027 * cr;
                double g_c = -0.391762 * cb - 0.812968 * cr;
                double b_c = 2.017232 * cb;

                double cy;
                cy = 1.164383 * ((showY ? (*pY1 & 255) : 0) - 16); pY1++;
                *pRGB1++ = PACK_RGB((int) (cy + r_c), (int) (cy + g_c), (int) (cy + b_c));
                cy = 1.164383 * ((showY ? (*pY1 & 255) : 0) - 16); pY1++;
                *pRGB1++ = PACK_RGB((int) (cy + r_c), (int) (cy + g_c), (int) (cy + b_c));
                cy = 1.164383 * ((showY ? (*pY2 & 255) : 0) - 16); pY2++;
                *pRGB2++ = PACK_RGB((int) (cy + r_c), (int) (cy + g_c), (int) (cy + b_c));
                cy = 1.164383 * ((showY ? (*pY2 & 255) : 0) - 16); pY2++;
                *pRGB2++ = PACK_RGB((int) (cy + r_c), (int) (cy + g_c), (int) (cy + b_c));
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
    int swidth = scale * width;
    for (int i = 0; i < height; i++) {
        jint *pSrcLine = image + i * width;
        jint *pDstLine = scaledImage + i * scale * swidth;
        for (int j = 0; j < width; j++) {
            int t = *pSrcLine++;
            jint *pDst = pDstLine + j * scale;
            switch (scale) {
                case 1:
                    *pDst = t;
                    break;
                case 2:
                    pDst[0] = t;
                    pDst[swidth] = t;
                    pDst[1] = t;
                    pDst[1 + swidth] = t;
                    break;
                case 4:
                    for (int l = 0; l < 4; l++) {
                        jint *pRow = pDst + l * swidth;
                        pRow[0] = pRow[1] = pRow[2] = pRow[3] = t;
                    }
                    break;
                default:
                    for (int l = 0; l < scale; l++) {
                        jint *pRow = pDst + l * swidth;
                        for (int k = 0; k < scale; k++) {
                            pRow[k] = t;
                        }
                    }
            }
        }
    }
    (*env)->ReleaseIntArrayElements(env, scaledRGBImage, scaledImage, 0);
    (*env)->ReleaseIntArrayElements(env, rgbImage, image, 0);
    return scaledRGBImage;
}
