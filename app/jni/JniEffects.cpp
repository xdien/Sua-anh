#include <iostream>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <jni.h>
#include <android/log.h>
#include <opencv/cv.h>
#include <string>
#include <sstream>

using namespace cv;
using namespace std;
string IntToString (int a)
{
    ostringstream temp;
    temp<<a;
    return temp.str();
}
void hello()
    {
      __android_log_print(ANDROID_LOG_DEBUG,"goi tu ben trong","hello addtext");
    }
/*chuyen doi jstring sang std::string*/
std::string jstring2str(JNIEnv* env, jstring jstr)
{
    char*   rtn   =   NULL;
    jclass   clsstring   =   env->FindClass("java/lang/String");
    jstring   strencode   =   env->NewStringUTF("GB2312");
    jmethodID   mid   =   env->GetMethodID(clsstring,   "getBytes",   "(Ljava/lang/String;)[B");
    jbyteArray   barr=   (jbyteArray)env->CallObjectMethod(jstr,mid,strencode);
    jsize   alen   =   env->GetArrayLength(barr);
    jbyte*   ba   =   env->GetByteArrayElements(barr,JNI_FALSE);
    if(alen   >   0)
    {
        rtn   =   (char*)malloc(alen+1);
        memcpy(rtn,ba,alen);
        rtn[alen]=0;
    }
    env->ReleaseByteArrayElements(barr,ba,0);
    std::string stemp(rtn);
    free(rtn);
    return   stemp;
}
static void setLabel(JNIEnv *env, jclass clazz, Mat&im,  string label, Point ors)
           {
               int fontface = cv::FONT_HERSHEY_SIMPLEX;
               double scale = 1;
               int thickness = 2;
               int baseline = 1;
               Size text = getTextSize(label, fontface, scale, thickness, &baseline);
               //rectangle(im, ors + Point(0, baseline), ors + Point(text.width, -text.height), CV_RGB(244,0,0), CV_AA);
               putText(im, label, ors, fontface, scale, CV_RGB(255,0,255), thickness, 8);
          }

namespace com_crazy_xdien_imageedit_sliding_process_jniMatEffects{
    static void JNICALL Smoothing(JNIEnv *env, jclass clazz, jlong addrMatSrc, jlong addrMatDst)
    {
        //__android_log_print(ANDROID_LOG_DEBUG,"jniSmoothing.c","nhat");
        Mat& src  = *(Mat*)addrMatSrc;
        Mat& dst = *(Mat*)addrMatDst;
        Mat m = Mat::zeros(100,400,CV_8UC3);
        putText(m,"Hello jni",Point(20,20),FONT_HERSHEY_SCRIPT_SIMPLEX,1,Scalar(8,255,0),2);
        cvtColor(m, dst, CV_BGR2GRAY,0);
    }
    void JNICALL getStringFromNative(JNIEnv *env, jclass clazz)
    {
        __android_log_print(ANDROID_LOG_DEBUG,"cai sdgi day fff.s..","getStringFromNative");
    }
    static void JNICALL equalizeIntensity(JNIEnv *env, jclass clazz, jlong addrMatSrc, jlong addrMatDst)
    {
      Mat& inputImage  = *(Mat*)addrMatSrc;
      Mat& ra = *(Mat*)addrMatDst;
        if(inputImage.channels() >= 3)
        {
            Mat ycrcb;

            cvtColor(inputImage,ycrcb,CV_BGR2YCrCb);

            vector<Mat> channels;
            split(ycrcb,channels);

            equalizeHist(channels[0],channels[0]);

            Mat result;
            merge(channels,ycrcb);

            cvtColor(ycrcb,result,CV_YCrCb2BGR);
            ra = result.clone();
        }
    }
    static void JNICALL rgbEqualizeHist(JNIEnv *env, jclass clazz, jlong addrMatSrc, jlong addrMatDst)
    {
      Mat& inputImage  = *(Mat*)addrMatSrc;
            Mat& ra = *(Mat*)addrMatDst;
            IplImage img = inputImage;
        //Load image file
      //Create space for outputs rgb and its separate channels, r, g and b
      IplImage* img0 = cvCreateImage(cvGetSize(&img), IPL_DEPTH_8U, 3);    //rgb
      IplImage* r = cvCreateImage(cvGetSize(&img), IPL_DEPTH_8U, 1);    //r
      IplImage* g = cvCreateImage(cvGetSize(&img), IPL_DEPTH_8U, 1);    //g
      IplImage* b = cvCreateImage(cvGetSize(&img), IPL_DEPTH_8U, 1);    //b
      cvSplit(&img, b, g, r, NULL);       //OpenCV likes it in BGR format

      cvEqualizeHist(r, r );    //equalise r
      cvEqualizeHist( g, g );    //equalise g
      cvEqualizeHist( b, b );    //equalise b
      cvMerge(b, g, r, NULL, img0);
      ra = Mat(img0).clone();

      //delete(img0);
            __android_log_print(ANDROID_LOG_DEBUG,"giai phong vung nho..","ok");
    }
    void JNICALL calculator_HSV(JNIEnv *env, jclass clazz, jlong addrMatSrc, jlong addrMatDst, jint type_HSV, jint value)
    {
          Mat& img  = *(Mat*)addrMatSrc;
          Mat& ra = *(Mat*)addrMatDst;
          ra = img.clone();
          Mat hsv;
          __android_log_print(ANDROID_LOG_DEBUG,"nhat","calulator");
          if(type_HSV >=0&& type_HSV <=2)
        {
         cvtColor( img, hsv, CV_BGR2HSV );

            for (int i=0; i < hsv.rows; i++)
            {
                  for(int j=0; j < hsv.cols; j++)
                  {
                        // You need to check this, but I think index 1 is for saturation, but it might be 0 or 2
                        hsv.at<cv::Vec3b>(i,j)[type_HSV] += value;
                        // or:
                        //hsv.at<cv::Vec3b>(i,j)[idx] += 20;// adds_constant_value;
                  }
            }
         }
         else{
            __android_log_print(ANDROID_LOG_DEBUG, "Chu y:","gia tri HSV tu 0-2, Da bo qua ham nay");
         }
         cvtColor(hsv, ra, CV_HSV2BGR);
    }
    static void JNICALL img_smoothing(JNIEnv *env, jclass clazz, jlong addrMatSrc, jlong addrMatDst, jint MAX_KERNEL_LENGTH)
    {
          Mat& src  = *(Mat*)addrMatSrc;
          Mat& dst = *(Mat*)addrMatDst;
        //for ( int i = 1; i < MAX_KERNEL_LENGTH; i = i + 2 )
                //{
                    GaussianBlur( src, dst, Size( 10,10 ), 0, 0 );
                    //medianBlur ( src, dst, i );
                //}
    }

    static void JNICALL addMask(JNIEnv *env, jclass clazz, jlong addrMatSrc, jlong addrMatDst, jint topLeftX, jint topLeftY, jint width, jint height, jint type)
    {
        Mat& image = *(Mat*)addrMatSrc;
        Mat& dst = *(Mat*)addrMatDst;
        dst = image.clone();
        if(type == 4)
        {
            Mat roi = dst(Rect(topLeftX, topLeftY, width, height));
            Mat color(roi.size(), CV_8UC4, Scalar(0, 125, 125));//bitmap
            //setLabel(roi, jstring2str(env,text) ,Point(50,20));//padding
            double alpha = 0.3;
            __android_log_print(ANDROID_LOG_DEBUG,"MORPH_NULL ","color: %d x %d anh roi %dx%d", color.cols,color.rows,roi.cols,roi.rows);
            addWeighted(color, alpha, roi, 1.0 - alpha , 0.0, roi);
        }
        if(type == 3)
        {
              int erosion_size =6;
            Mat roi = dst(Rect(topLeftX, topLeftY, width, height));
            Mat color(roi.size(), CV_8UC4, Scalar(0, 125, 125));//bitmap
            Mat element = getStructuringElement( MORPH_RECT, Size( 2*erosion_size + 1, 2*erosion_size+1 ), Point( erosion_size, erosion_size ) );
              /// Apply the erosion operation
              erode( roi, roi, element );
            double alpha = 0.3;
            __android_log_print(ANDROID_LOG_DEBUG,"MORPH_RECT ","color: %d x %d anh roi %dx%d", color.cols,color.rows,roi.cols,roi.rows);
            addWeighted(color, alpha, roi, 1.0 - alpha , 0.0, roi);
        }
    }

    static JNINativeMethod methodTable[] = {
      {"Smoothing", "(JJ)V", (void *) Smoothing},
      {"getStringFromNative", "()V", (void *) getStringFromNative},
      {"equalizeIntensity", "(JJ)V", (void *) equalizeIntensity},
      {"rgbEqualizeHist", "(JJ)V", (void *) rgbEqualizeHist},
      {"calculator_HSV", "(JJII)V", (void *) calculator_HSV},
      {"img_smoothing","(JJI)V", (void *) img_smoothing},
      {"addMask", "(JJIIIII)V", (void *) addMask}//(JJLjava/lang/String;IIII)V
    };
}

using namespace com_crazy_xdien_imageedit_sliding_process_jniMatEffects;

extern "C" jint JNI_OnLoad(JavaVM* vm, void* reserved)
           {
               JNIEnv* env;
               if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
                   return -1;
               }
               else{
               jclass clazz = env->FindClass("com/crazy/xdien/imageedit/sliding/process/jniMatEffects");
                    if(clazz){
                    env->RegisterNatives(clazz,methodTable,sizeof(methodTable)/sizeof(methodTable[0]));
                    env->DeleteLocalRef(clazz);
                     return JNI_VERSION_1_6;
                    } else {
                        return -1;
                    }
               }
           }
