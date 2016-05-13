package org.opencv.template.convert;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;

public class ImageConverter
{

    public static void convert(Mat image) {
        nativeConvert(image.getNativeObjAddr());
    }

    private static native void nativeConvert(long inputImage);

}
