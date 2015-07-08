LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
OPENCV_LIB_TYPE := STATIC
OPENCV_INSTALL_MODULES := off
OPENCV_CAMERA_MODULES := off

include /home/xdien/android/OpenCV-android-sdk/sdk/native/jni/OpenCV.mk

LOCAL_MODULE := jniEffects
LOCAL_CFLAGS :=  -mfloat-abi=softfp -fexceptions -D_DEBUG -frtti
LOCAL_LDLIBS += -ldl -llog\
        -lz -l/home/xdien/android/OpenCV-android-sdk/sdk/native/3rdparty/libs/armeabi-v7a/libtbb.a

LOCAL_SRC_FILES := JniEffects.cpp

LOCAL_C_INCLUDES += /home/xdien/AndroidStudioProjects/Sua-anh/app/jni
LOCAL_C_INCLUDES += /home/xdien/AndroidStudioProjects/Sua-anh/app/src/arm/jni
LOCAL_C_INCLUDES += /home/xdien/AndroidStudioProjects/Sua-anh/app/src/debug/jni
LOCAL_C_INCLUDES += /home/xdien/AndroidStudioProjects/Sua-anh/app/src/armDebug/jni

LOCAL_STATIC_LIBRARIES := stdc++
LOCAL_STATIC_LIBRARIES += libopencv_contrib libopencv_legacy libopencv_ml libopencv_stitching libopencv_nonfree libopencv_objdetect \
    libopencv_videostab libopencv_calib3d libopencv_photo libopencv_video \
    libopencv_features2d libopencv_highgui libopencv_androidcamera libopencv_flann \
    libopencv_imgproc libopencv_ts libopencv_core
include $(LOCAL_SHARED_LIBRARY)

# Foo Secondary lib:

# Clear vars
include $(CLEAR_VARS)
# Include OpenCV
# Target Build
LOCAL_ARM_MODE := arm
# Library Name
LOCAL_MODULE := Test
# Set All SRC_FILES Together
#SRC_FILES := $(wildcard $(LOCAL_PATH)/FooMain/src/*.cpp)
#SRC_FILES := $(SRC_FILES:$(LOCAL_PATH)/%=%)
LOCAL_SRC_FILES = $(SRC_FILES)
LOCAL_SHARED_LIBRARIES := libFooSecondary
#LOCAL_LDLIBS := -L$(LOCAL_PATH)/../libs/armeabi
#LOCAL_LDLIBS += -lz -lm -ldl -lGLESv2 -lEGL -llog -lFooSecondary
#LOCAL_CFLAGS := -O2 -mno-thumb -Wno-write-strings
LOCAL_CPPFLAGS := -O2 -mno-thumb -Wno-write-strings
# Compile as Shared Library
include $(BUILD_SHARED_LIBRARY)