#include <jni.h>
#include "com_mediatek_factorymode_OpticalActivity.h"

#include <stdio.h>
#include <stdlib.h>	
#include <string.h>
#include <fcntl.h>
#include <linux/fs.h>
#include <linux/sensors_io.h>

//#include <fcntl.h>
#include <errno.h>
//#include <unistd.h>
//#include <sys/ioctl.h>
#include <cutils/log.h>

#define LOGD(fmt, arg...) ALOGD(fmt, ##arg)
#define SPRD_DBG LOGD

#define MTK_ALSPS_DEVICE "/dev/als_ps"

#define ALSPS							0X84
#define ALSPS_SET_PS_MODE					_IOW(ALSPS, 0x01, int)
#define ALSPS_GET_PS_MODE					_IOR(ALSPS, 0x02, int)
#define ALSPS_GET_PS_DATA					_IOR(ALSPS, 0x03, int)
#define ALSPS_GET_PS_RAW_DATA				_IOR(ALSPS, 0x04, int)
#define ALSPS_SET_ALS_MODE					_IOW(ALSPS, 0x05, int)
#define ALSPS_GET_ALS_MODE					_IOR(ALSPS, 0x06, int)
#define ALSPS_GET_ALS_DATA					_IOR(ALSPS, 0x07, int)
#define ALSPS_GET_ALS_RAW_DATA           	_IOR(ALSPS, 0x08, int)


//读取环境光数据CDATA
JNIEXPORT jint JNICALL Java_com_mediatek_factorymode_OpticalActivity_getCdata(){
	int fd = -1;
	int als_data;
	
	LOGD("get Cdata");
	
	SPRD_DBG("Enter getCdata success");

	fd = open(MTK_ALSPS_DEVICE, O_RDWR);
	
	if(fd < 0)
	{
		SPRD_DBG("%s, open hardware device fail\n", __FUNCTION__);
		return 0;
	}

	if (ioctl(fd, ALSPS_GET_ALS_RAW_DATA, &als_data)) {
		SPRD_DBG("ALS_RAW_DATA ioctl fail: %s", strerror(errno));
		close(MTK_ALSPS_DEVICE);
		return 0;
	}

	close(MTK_ALSPS_DEVICE);
	return als_data;
}


