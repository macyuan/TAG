package com.ccxt.whl.common.util;

import com.ccxt.whl.DemoApplication;

public class DistanceUtil {

    public static int getCameraAlbumWidth() {
        return (DemoApplication.getApp().getScreenWidth() - DemoApplication.getApp().dp2px(10)) / 4 - DemoApplication.getApp().dp2px(4);
    }
    
    // 相机照片列表高度计算 
    public static int getCameraPhotoAreaHeight() {
        return getCameraPhotoWidth() + DemoApplication.getApp().dp2px(4);
    }
    
    public static int getCameraPhotoWidth() {
        return DemoApplication.getApp().getScreenWidth() / 4 - DemoApplication.getApp().dp2px(2);
    }

    //活动标签页grid图片高度
    public static int getActivityHeight() {
        return (DemoApplication.getApp().getScreenWidth() - DemoApplication.getApp().dp2px(24)) / 3;
    }
}
