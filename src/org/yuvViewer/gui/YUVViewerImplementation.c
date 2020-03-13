/***************************************************************************
                          YUVViewerImplementation.c  -  description
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

#include <jni.h>
#include "org_yuvViewer_gui_YUVViewer.h"

/**
 * http://msdn.microsoft.com/library/en-us/dnwmt/html/YUVFormats.asp
 * R = clip(( 298 * C           + 409 * E + 128) >> 8)
 * G = clip(( 298 * C - 100 * D - 208 * E + 128) >> 8)
 * B = clip(( 298 * C + 516 * D           + 128) >> 8)
 */
JNIEXPORT jintArray JNICALL
Java_org_yuvViewer_gui_YUVViewer_calculateFastRGBImage(JNIEnv *env, 
						       jobject obj, 
						       jboolean showY,
						       jboolean showU,
						       jboolean showV,
						       jint width, 
						       jint height, 
						       jbyteArray yData, 
						       jbyteArray uData, 
						       jbyteArray vData,
						       jintArray rgbImage
						       )
{
  jint *rgbInt = (*env)->GetIntArrayElements(env, rgbImage, 0);
  jbyte *cyData = (*env)->GetByteArrayElements(env, yData, 0);
  jbyte *cuData = (*env)->GetByteArrayElements(env, uData, 0);
  jbyte *cvData = (*env)->GetByteArrayElements(env, vData, 0);
  int i, j;
  int red, green, blue;

  for(i=0;i<height;i+=2)
    {
      for(j=0;j<width;j+=2)
	{
	  int pos=j+width*i;
	  int cy=cyData[pos]&255;
	  int cb=cuData[j/2+i/4*width]&255;
	  int cr=cvData[j/2+i/4*width]&255;
	  cy = showY? cy: 0;
	  cb = showU? cb: 0;
	  cr = showV? cr: 0;
	  cy -=16;
	  cb -=128;
	  cr -=128;
	  red=(298*cy + 409*cr+128) >> 8;
	  green=(298*cy - 100*cb - 208*cr + 128) >> 8;
	  blue=(298*cy + 516*cb + 128) >> 8;
      
	  //clipping
	  red = red > 255? 255 : red < 0? 0 : red;
	  green = green > 255? 255 : green < 0? 0 : green;
	  blue = blue > 255? 255 : blue < 0? 0 : blue;
	  if(showY &! showU &! showV)
	    {
	      red = green = blue = cy+16;
	    }
	  else if( !showY && showU &! showV)
	    {
	      red = green = blue = cb + 128;
	    }
	  else if ( !showY &! showU && showV)
	    {
	      red = green = blue = cr + 128;
	    }
	  rgbInt[pos]=blue | ( green << 8) | ( red << 16);
	  
	  cy=cyData[pos+1]&255;
	  cy = showY? cy: 0;
	  cy -=16;
	  red=(298*cy + 409*cr+128) >> 8;
	  green=(298*cy - 100*cb - 208*cr + 128) >> 8;
	  blue=(298*cy + 516*cb + 128) >> 8;

	  //clipping
	  red = red > 255? 255 : red < 0? 0 : red;
	  green = green > 255? 255 : green < 0? 0 : green;
	  blue = blue > 255? 255 : blue < 0? 0 : blue;
	  if(showY &! showU &! showV)
	    {
	      red = green = blue = cy+16;
	    }
	  else if( !showY && showU &! showV)
	    {
	      red = green = blue = cb + 128;
	    }
	  else if ( !showY &! showU && showV)
	    {
	      red = green = blue = cr + 128;
	    }
	  rgbInt[pos+1]=blue | ( green << 8) | ( red << 16);

	  cy=cyData[pos+width]&255;
	  cy = showY? cy: 0;
	  cy -=16;
	  red=(298*cy + 409*cr+128) >> 8;
	  green=(298*cy - 100*cb - 208*cr + 128) >> 8;
	  blue=(298*cy + 516*cb + 128) >> 8;
	  //clipping
	  red = red > 255? 255 : red < 0? 0 : red;
	  green = green > 255? 255 : green < 0? 0 : green;
	  blue = blue > 255? 255 : blue < 0? 0 : blue;
	  if(showY &! showU &! showV)
	    {
	      red = green = blue = cy+16;
	    }
	  else if( !showY && showU &! showV)
	    {
	      red = green = blue = cb + 128;
	    }
	  else if ( !showY &! showU && showV)
	    {
	      red = green = blue = cr + 128;
	    }
	  rgbInt[pos+width]=blue | ( green << 8) | ( red << 16);

	  cy=cyData[pos+width+1]&255;
	  cy = showY? cy: 0;
	  cy -=16;
	  red=(298*cy + 409*cr+128) >> 8;
	  green=(298*cy - 100*cb - 208*cr + 128) >> 8;
	  blue=(298*cy + 516*cb + 128) >> 8;
	  //clipping
	  red = red > 255? 255 : red < 0? 0 : red;
	  green = green > 255? 255 : green < 0? 0 : green;
	  blue = blue > 255? 255 : blue < 0? 0 : blue;
	  if(showY &! showU &! showV)
	    {
	      red = green = blue = cy+16;
	    }
	  else if( !showY && showU &! showV)
	    {
	      red = green = blue = cb + 128;
	    }
	  else if ( !showY &! showU && showV)
	    {
	      red = green = blue = cr + 128;
	    }
	  rgbInt[pos+width+1]=blue | ( green << 8) | ( red << 16);
	}
    }
  (*env)->ReleaseByteArrayElements(env, yData, cyData, 0);
  (*env)->ReleaseByteArrayElements(env, uData, cuData, 0);
  (*env)->ReleaseByteArrayElements(env, vData, cvData, 0);
  (*env)->ReleaseIntArrayElements(env, rgbImage, rgbInt, 0);
  return rgbImage;  
}

JNIEXPORT jintArray JNICALL
Java_org_yuvViewer_gui_YUVViewer_calculateFastColoredRGBImage(JNIEnv *env, 
							      jobject obj, 
							      jint width, 
							      jint height, 
							      jbyteArray yData, 
							      jbyteArray uData, 
							      jbyteArray vData,
							      jintArray rgbImage
							      )
{
  jint *rgbInt = (*env)->GetIntArrayElements(env, rgbImage, 0);
  jbyte *cyData = (*env)->GetByteArrayElements(env, yData, 0);
  jbyte *cuData = (*env)->GetByteArrayElements(env, uData, 0);
  jbyte *cvData = (*env)->GetByteArrayElements(env, vData, 0);
  int i, j;
  int red, green, blue;

  for(i=0;i<height;i+=2)
    {
      for(j=0;j<width;j+=2)
	{
	  int pos=j+width*i;
	  int cy=cyData[pos]&255;
	  int cb=cuData[j/2+i/4*width]&255;
	  int cr=cvData[j/2+i/4*width]&255;
	  cy -=16;
	  cb -=128;
	  cr -=128;
	  red=(298*cy + 409*cr+128) >> 8;
	  green=(298*cy - 100*cb - 208*cr + 128) >> 8;
	  blue=(298*cy + 516*cb + 128) >> 8;
      
	  //clipping
	  red = red > 255? 255 : red < 0? 0 : red;
	  green = green > 255? 255 : green < 0? 0 : green;
	  blue = blue > 255? 255 : blue < 0? 0 : blue;
	  rgbInt[pos]=blue | ( green << 8) | ( red << 16);
	  
	  cy=cyData[pos+1]&255;
	  cy -=16;
	  red=(298*cy + 409*cr+128) >> 8;
	  green=(298*cy - 100*cb - 208*cr + 128) >> 8;
	  blue=(298*cy + 516*cb + 128) >> 8;

	  //clipping
	  red = red > 255? 255 : red < 0? 0 : red;
	  green = green > 255? 255 : green < 0? 0 : green;
	  blue = blue > 255? 255 : blue < 0? 0 : blue;
	  rgbInt[pos+1]=blue | ( green << 8) | ( red << 16);

	  cy=cyData[pos+width]&255;
	  cy -=16;
	  red=(298*cy + 409*cr+128) >> 8;
	  green=(298*cy - 100*cb - 208*cr + 128) >> 8;
	  blue=(298*cy + 516*cb + 128) >> 8;
	  //clipping
	  red = red > 255? 255 : red < 0? 0 : red;
	  green = green > 255? 255 : green < 0? 0 : green;
	  blue = blue > 255? 255 : blue < 0? 0 : blue;
	  rgbInt[pos+width]=blue | ( green << 8) | ( red << 16);

	  cy=cyData[pos+width+1]&255;
	  cy -=16;
	  red=(298*cy + 409*cr+128) >> 8;
	  green=(298*cy - 100*cb - 208*cr + 128) >> 8;
	  blue=(298*cy + 516*cb + 128) >> 8;
	  //clipping
	  red = red > 255? 255 : red < 0? 0 : red;
	  green = green > 255? 255 : green < 0? 0 : green;
	  blue = blue > 255? 255 : blue < 0? 0 : blue;
	  rgbInt[pos+width+1]=blue | ( green << 8) | ( red << 16);
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
JNIEXPORT jintArray JNICALL
Java_org_yuvViewer_gui_YUVViewer_calculateRGBImage(JNIEnv *env, 
						   jobject obj, 
						   jboolean showY,
						   jboolean showU,
						   jboolean showV,
						   jint width, 
						   jint height, 
						   jbyteArray yData, 
						   jbyteArray uData, 
						   jbyteArray vData,
						   jintArray rgbImage
						   )
{
  jint *rgbInt = (*env)->GetIntArrayElements(env, rgbImage, 0);
  jbyte *cyData = (*env)->GetByteArrayElements(env, yData, 0);
  jbyte *cuData = (*env)->GetByteArrayElements(env, uData, 0);
  jbyte *cvData = (*env)->GetByteArrayElements(env, vData, 0);
  int i, j;
  int red, green, blue;

  for(i=0;i<height;i+=2)
    {
      for(j=0;j<width;j+=2)
	{
	  int pos=j+width*i;
	  int cy=cyData[pos]&255;
	  int cb=cuData[j/2+i/4*width]&255;
	  int cr=cvData[j/2+i/4*width]&255;
	  cy = showY? cy: 0;
	  cb = showU? cb: 0;
	  cr = showV? cr: 0;
	  cy -=16;
	  cb -=128;
	  cr -=128;
	  red=(int)( 1.164383*cy + 1.596027*cr);
	  green=(int)( 1.164383*cy - 0.391762*cb - 0.812968*cr);
	  blue=(int)(1.164383*cy +  2.017232*cb);
	  //clipping
	  red = red > 255? 255 : red < 0? 0 : red;
	  green = green > 255? 255 : green < 0? 0 : green;
	  blue = blue > 255? 255 : blue < 0? 0 : blue;
	  if(showY &! showU &! showV)
	    {
	      red = green = blue = cy+16;
	    }
	  else if( !showY && showU &! showV)
	    {
	      red = green = blue = cb + 128;
	    }
	  else if ( !showY &! showU && showV)
	    {
	      red = green = blue = cr + 128;
	    }
	  rgbInt[pos]=blue | ( green << 8) | ( red << 16);

	  cy=cyData[pos+1]&255;
	  cy = showY? cy: 0;
	  cy -=16;
	  red=(int)( 1.164383*cy + 1.596027*cr);
	  green=(int)( 1.164383*cy - 0.391762*cb - 0.812968*cr);
	  blue=(int)(1.164383*cy +  2.017232*cb);
	  //clipping
	  red = red > 255? 255 : red < 0? 0 : red;
	  green = green > 255? 255 : green < 0? 0 : green;
	  blue = blue > 255? 255 : blue < 0? 0 : blue;
	  if(showY &! showU &! showV)
	    {
	      red = green = blue = cy+16;
	    }
	  else if( !showY && showU &! showV)
	    {
	      red = green = blue = cb + 128;
	    }
	  else if ( !showY &! showU && showV)
	    {
	      red = green = blue = cr + 128;
	    }
	  rgbInt[pos+1]=blue | ( green << 8) | ( red << 16);

	  cy=cyData[pos+width]&255;
	  cy = showY? cy: 0;
	  cy -=16;
	  red=(int)( 1.164383*cy + 1.596027*cr);
	  green=(int)( 1.164383*cy - 0.391762*cb - 0.812968*cr);
	  blue=(int)(1.164383*cy +  2.017232*cb);
	  //clipping
	  red = red > 255? 255 : red < 0? 0 : red;
	  green = green > 255? 255 : green < 0? 0 : green;
	  blue = blue > 255? 255 : blue < 0? 0 : blue;
	  if(showY &! showU &! showV)
	    {
	      red = green = blue = cy+16;
	    }
	  else if( !showY && showU &! showV)
	    {
	      red = green = blue = cb + 128;
	    }
	  else if ( !showY &! showU && showV)
	    {
	      red = green = blue = cr + 128;
	    }
	  rgbInt[pos+width]=blue | ( green << 8) | ( red << 16);

	  cy=cyData[pos+width+1]&255;
	  cy = showY? cy: 0;
	  cy -=16;
	  red=(int)( 1.164383*cy + 1.596027*cr);
	  green=(int)( 1.164383*cy - 0.391762*cb - 0.812968*cr);
	  blue=(int)(1.164383*cy +  2.017232*cb);
	  //clipping
	  red = red > 255? 255 : red < 0? 0 : red;
	  green = green > 255? 255 : green < 0? 0 : green;
	  blue = blue > 255? 255 : blue < 0? 0 : blue;
	  if(showY &! showU &! showV)
	    {
	      red = green = blue = cy+16;
	    }
	  else if( !showY && showU &! showV)
	    {
	      red = green = blue = cb + 128;
	    }
	  else if ( !showY &! showU && showV)
	    {
	      red = green = blue = cr + 128;
	    }
	  rgbInt[pos+width+1]=blue | ( green << 8) | ( red << 16);
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
JNIEXPORT jintArray JNICALL
Java_org_yuvViewer_gui_YUVViewer_resizeRGBImage(JNIEnv *env, 
						jobject obj,
						jint scale,
						jint width,
						jint height,
						jintArray scaledRGBImage,
						jintArray rgbImage
						)
{
  jint *scaledImage= (*env)->GetIntArrayElements(env, scaledRGBImage, 0);
  jint *image= (*env)->GetIntArrayElements(env, rgbImage, 0);
  for(int i = 0; i < height; i++)
  {
	  for(int j = 0; j < width; j++)
	  {
		  int t = image[j+i*width];
		  switch(scale) 
		  {
		  case  1:  
			  scaledImage[(scale*j+0)+(scale*i+0)*scale*width] = t;
			  break;
		  case  2:  
			  scaledImage[(scale*j+0)+(scale*i+0)*scale*width] = t;
			  scaledImage[(scale*j+0)+(scale*i+1)*scale*width] = t;
			  scaledImage[(scale*j+1)+(scale*i+0)*scale*width] = t;
			  scaledImage[(scale*j+1)+(scale*i+1)*scale*width] = t;
			  break;
		  case  4:  
			  for(int k = 0; k < 4; k++)
			  {
				  for(int l = 0; l < 4; l++)
				  {
					  scaledImage[(scale*j+k)+(scale*i+l)*scale*width] = t;
				  }
			  }

			  break;
		  default: 
			  for(int k = 0; k < scale; k++)
			  {
				  for(int l = 0; l < scale; l++)
				  {
					  scaledImage[(scale*j+k)+(scale*i+l)*scale*width]=t;
				  }
			  }
		  }
	  }
    }
  (*env)->ReleaseIntArrayElements(env, scaledRGBImage, scaledImage, 0);
  (*env)->ReleaseIntArrayElements(env, rgbImage, image, 0);
  return scaledRGBImage;
}
