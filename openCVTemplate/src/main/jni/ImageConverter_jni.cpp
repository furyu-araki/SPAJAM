#include <ImageConverter_jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/objdetect.hpp>

#include <string>
#include <vector>

#include <android/log.h>

#define LOG_TAG "ImageConverter"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))

using namespace std;
using namespace cv;


JNIEXPORT void JNICALL Java_org_opencv_template_convert_ImageConverter_nativeConvert
(JNIEnv * jenv, jclass, jlong imagePtr)
{
    LOGD("Java_org_opencv_template_convert_ImageConverter_nativeConvert");

    try
    {
        cv::Mat image = *((Mat*)imagePtr);
    }
    catch (...)
    {
        LOGD("nativeConvert caught unknown exception");
    }

}
