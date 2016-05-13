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

unsigned char table[256];
bool tableInitFlag = false;

int posterize_intensity(int intensity, int poster_level){
	int bin_size = 256 / poster_level;
	return (intensity / bin_size) * (255 / (poster_level - 1));
}

void initTable()
{
    if(tableInitFlag){
        return;
    }
    for(int color = 0; color < 256; ++color){
        table[color] = color / 128 * 128;
    }
    tableInitFlag = true;
}

JNIEXPORT void JNICALL Java_org_opencv_template_convert_ImageConverter_nativeConvert
(JNIEnv * jenv, jclass, jlong imagePtr)
{
    LOGD("Java_org_opencv_template_convert_ImageConverter_nativeConvert");

    initTable();

    cv::Mat image = *((Mat*)imagePtr);

    for(int y = 0; y < image.rows; ++y){
    	for(int x = 0; x < image.cols; ++x){
            image.data[ y * image.step + x * image.elemSize() + 0 ] = table[ image.data[ y * image.step + x * image.elemSize() + 0 ] ];
            image.data[ y * image.step + x * image.elemSize() + 1 ] = table[ image.data[ y * image.step + x * image.elemSize() + 1 ] ];
            image.data[ y * image.step + x * image.elemSize() + 2 ] = table[ image.data[ y * image.step + x * image.elemSize() + 2 ] ];
    	}
    }
}
