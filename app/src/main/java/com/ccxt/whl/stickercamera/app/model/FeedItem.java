package com.ccxt.whl.stickercamera.app.model;

import android.net.Uri;
import android.util.Log;

import com.ccxt.whl.DemoApplication;
import com.ccxt.whl.common.util.FileUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * 图片Module
 * Created by sky on 15/7/18.
 */
public class FeedItem {

    //本地地址
    private String imgPath;
    private List<TagItem> tagList;
    private String textContext;
    private int imgId;

    //图片名
    private String imgName;

    //增加用户访问控制
    private String uid = "0";

    public FeedItem() {

    }

    public FeedItem(List<TagItem> tagList, String imgName) {
        this.imgName = imgName;
        this.tagList = tagList;
    }

    public List<TagItem> getTagList() {
        return tagList;
    }

    public void setTagList(List<TagItem> tagList) {
        this.tagList = tagList;
    }

    public String getImgPath() {
        imgPath = FileUtils.getInst().getPhotoSavedPath() + "/" +imgName;
        Log.d("5v", imgPath);
        File file = new File(FileUtils.getInst().getPhotoSavedPath() + "/",imgName);
        if(!file.exists()) {
            // 从网络上获取图片
            Log.d("5v", imgPath);
            String path = "http://ngc123-uploads.stor.sinaapp.com/" + imgName;
            AsyncHttpClient client = new AsyncHttpClient();
            client.setURLEncodingEnabled(false);
            client.get(path, new FileAsyncHttpResponseHandler(file) {
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, File response) {

                }

            });
        }
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public void setTextContext(String textContext) {
        this.textContext = textContext;
    }

    public String getTextContext() {
        return textContext;
    }

    //增加访问控制
    public void setUid(String uid) { this.uid = DemoApplication.getInstance().getUser(); }

    public String getUid() {return this.uid;}

    public void setImgId(int imgId) { this.imgId = imgId; }
    public int getImgId() { return this.imgId; }

    public void setImgName(String imgName) {this.imgName = imgName; }
    public String getImgName() {return this.imgName;}
}
