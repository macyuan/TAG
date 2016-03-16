package com.ccxt.whl.stickercamera.app.camera.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ccxt.whl.Constant;
import com.ccxt.whl.DemoApplication;
import com.ccxt.whl.common.util.FileUtils;
import com.ccxt.whl.common.util.ImageUtils;
import com.ccxt.whl.common.util.StringUtils;
import com.ccxt.whl.common.util.TimeUtils;
import com.ccxt.whl.customview.LabelSelector;
import com.ccxt.whl.customview.LabelView;
import com.ccxt.whl.customview.MyHighlightView;
import com.ccxt.whl.customview.MyImageViewDrawableOverlay;
import com.ccxt.whl.R;
import com.ccxt.whl.stickercamera.app.camera.CameraBaseActivity;
import com.ccxt.whl.stickercamera.app.camera.CameraManager;
import com.ccxt.whl.stickercamera.app.camera.EffectService;
import com.ccxt.whl.stickercamera.app.camera.adapter.FilterAdapter;
import com.ccxt.whl.stickercamera.app.camera.adapter.StickerToolAdapter;
import com.ccxt.whl.stickercamera.app.camera.effect.FilterEffect;
import com.ccxt.whl.stickercamera.app.camera.util.EffectUtil;
import com.ccxt.whl.stickercamera.app.camera.util.GPUImageFilterTools;
import com.ccxt.whl.stickercamera.app.model.Addon;
import com.ccxt.whl.stickercamera.app.model.FeedItem;
import com.ccxt.whl.stickercamera.app.model.TagItem;
import com.ccxt.whl.stickercamera.app.ui.EditTextActivity;
import com.ccxt.whl.stickercamera.app.ui.PublishActivity;
import com.ccxt.whl.utils.CommonUtils;
import com.ccxt.whl.utils.JsonToMapList;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.sephiroth.android.library.widget.HListView;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

/**
 * 图片处理界面
 * Created by sky on 2015/7/8.
 * Weibo: http://weibo.com/2030683111
 * Email: 1132234509@qq.com
 */
public class PhotoProcessActivity extends CameraBaseActivity {

    //滤镜图片
    @InjectView(R.id.gpuimage)
    GPUImageView mGPUImageView;
    //绘图区域
    @InjectView(R.id.drawing_view_container)
    ViewGroup drawArea;
    //底部按钮
    @InjectView(R.id.sticker_btn)
    TextView stickerBtn;
    @InjectView(R.id.filter_btn)
    TextView filterBtn;
    @InjectView(R.id.text_btn)
    TextView labelBtn;
    //工具区
    @InjectView(R.id.list_tools)
    HListView bottomToolBar;
    @InjectView(R.id.toolbar_area)
    ViewGroup toolArea;
    private MyImageViewDrawableOverlay mImageView;
    private LabelSelector labelSelector;

    //当前选择底部按钮
    private TextView currentBtn;
    //当前图片
    private Bitmap currentBitmap;
    //用于预览的小图片
    private Bitmap smallImageBackgroud;
    //小白点标签
    private LabelView emptyLabelView;

    private List<LabelView> labels = new ArrayList<LabelView>();

    //标签区域
    private View commonLabelArea;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_process);
        ButterKnife.inject(this);
        EffectUtil.clear();
        initView();
        initEvent();
        initStickerToolBar();

        ImageUtils.asyncLoadImage(this, getIntent().getData(), new ImageUtils.LoadImageCallback() {
            @Override
            public void callback(Bitmap result) {
                currentBitmap = result;
                mGPUImageView.setImage(currentBitmap);
            }
        });

        ImageUtils.asyncLoadSmallImage(this, getIntent().getData(), new ImageUtils.LoadImageCallback() {
            @Override
            public void callback(Bitmap result) {
                smallImageBackgroud = result;
            }
        });
    }

    private void initView() {
        //添加贴纸水印的画布
        View overlay = LayoutInflater.from(PhotoProcessActivity.this).inflate(
                R.layout.view_drawable_overlay, null);
        mImageView = (MyImageViewDrawableOverlay) overlay.findViewById(R.id.drawable_overlay);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(DemoApplication.getApp().getScreenWidth(),
                DemoApplication.getApp().getScreenWidth());
        mImageView.setLayoutParams(params);
        overlay.setLayoutParams(params);
        drawArea.addView(overlay);
        //添加标签选择器
        RelativeLayout.LayoutParams rparams = new RelativeLayout.LayoutParams(DemoApplication.getApp().getScreenWidth(), DemoApplication.getApp().getScreenWidth());
        labelSelector = new LabelSelector(this);
        labelSelector.setLayoutParams(rparams);
        drawArea.addView(labelSelector);
        labelSelector.hide();

        //初始化滤镜图片
        mGPUImageView.setLayoutParams(rparams);


        //初始化空白标签
        emptyLabelView = new LabelView(this);
        emptyLabelView.setEmpty();
        EffectUtil.addLabelEditable(mImageView, drawArea, emptyLabelView,
                mImageView.getWidth() / 2, mImageView.getWidth() / 2);
        emptyLabelView.setVisibility(View.INVISIBLE);

        //初始化推荐标签栏
        commonLabelArea = LayoutInflater.from(PhotoProcessActivity.this).inflate(
                R.layout.view_label_bottom, null);
        commonLabelArea.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        toolArea.addView(commonLabelArea);
        commonLabelArea.setVisibility(View.GONE);
    }

    private void initEvent() {
        stickerBtn.setOnClickListener(v -> {
            if (!setCurrentBtn(stickerBtn)) {
                return;
            }
            bottomToolBar.setVisibility(View.VISIBLE);
            labelSelector.hide();
            emptyLabelView.setVisibility(View.GONE);
            commonLabelArea.setVisibility(View.GONE);
            initStickerToolBar();
        });

        filterBtn.setOnClickListener(v -> {
            if (!setCurrentBtn(filterBtn)) {
                return;
            }
            bottomToolBar.setVisibility(View.VISIBLE);
            labelSelector.hide();
            emptyLabelView.setVisibility(View.INVISIBLE);
            commonLabelArea.setVisibility(View.GONE);
            initFilterToolBar();
        });
        labelBtn.setOnClickListener(v -> {
            if (!setCurrentBtn(labelBtn)) {
                return;
            }
            bottomToolBar.setVisibility(View.GONE);
            labelSelector.showToTop();
            commonLabelArea.setVisibility(View.VISIBLE);

        });
        labelSelector.setTxtClicked(v -> {
            EditTextActivity.openTextEdit(PhotoProcessActivity.this, "", 8, Constant.ACTION_EDIT_LABEL);
        });
        labelSelector.setAddrClicked(v -> {
            EditTextActivity.openTextEdit(PhotoProcessActivity.this, "", 8, Constant.ACTION_EDIT_LABEL_POI);

        });
        mImageView.setOnDrawableEventListener(wpEditListener);
        mImageView.setSingleTapListener(() -> {
            emptyLabelView.updateLocation((int) mImageView.getmLastMotionScrollX(),
                    (int) mImageView.getmLastMotionScrollY());
            emptyLabelView.setVisibility(View.VISIBLE);

            labelSelector.showToTop();
            drawArea.postInvalidate();
        });
        labelSelector.setOnClickListener(v -> {
            labelSelector.hide();
            emptyLabelView.updateLocation((int) labelSelector.getmLastTouchX(),
                    (int) labelSelector.getmLastTouchY());
            emptyLabelView.setVisibility(View.VISIBLE);
        });


        titleBar.setRightBtnOnclickListener(v -> {
            savePicture();
        });
    }

    //保存图片
    private void savePicture() {
        //加滤镜
        final Bitmap newBitmap = Bitmap.createBitmap(mImageView.getWidth(), mImageView.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newBitmap);
        RectF dst = new RectF(0, 0, mImageView.getWidth(), mImageView.getHeight());
        try {
            cv.drawBitmap(mGPUImageView.capture(), null, dst, null);
        } catch (InterruptedException e) {
            e.printStackTrace();
            cv.drawBitmap(currentBitmap, null, dst, null);
        }
        //加贴纸水印
        EffectUtil.applyOnSave(cv, mImageView);



//        new SavePicToFileTask().execute(newBitmap);

        //异步上传图片
        RequestParams params = new RequestParams();
        params.add("uid", DemoApplication.getInstance().getUser());
        try{
            String fileName = ImageUtils.saveToFile(FileUtils.getInst().getPhotoSavedPath() + "/tmp", false, newBitmap);
            File file = new File(fileName);
            params.put("file",file);

        }catch (Exception e){
            Toast.makeText(PhotoProcessActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
            return;
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://ngc123.sinaapp.com/index.php/Tag/uploadImg", params, new BaseJsonHttpResponseHandler() {


            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog("图片处理中...");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  String rawJsonResponse, Object response) {
                Log.d("5v", rawJsonResponse);
                dismissProgressDialog();

                if (CommonUtils.isNullOrEmpty(rawJsonResponse)) {
                    Toast.makeText(PhotoProcessActivity.this, "您的网络不稳定,请检查网络！", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> lm = JsonToMapList.getMap(rawJsonResponse);
                if (lm.get("status").toString() != null &&
                        lm.get("status").toString().equals("ok")) {

                    Map<String, Object> lmres = JsonToMapList.getMap(lm.get("result").toString());

                    Log.d("5v", lm.get("result").toString());

                    if (lmres.get("imgName") == null) {
                        Toast.makeText(PhotoProcessActivity.this, "您的网络差，请稍后重试 ！", Toast.LENGTH_SHORT).show();
                        dismissProgressDialog();
                        return;
                    }
//
                    //将照片信息保存至sharedPreference
                    //保存标签信息
                    List<TagItem> tagInfoList = new ArrayList<TagItem>();
                    for (LabelView label : labels) {
                        tagInfoList.add(label.getTagInfo());
                    }

                    //将图片信息通过EventBus发送到MainActivity
                    FeedItem feedItem = new FeedItem(tagInfoList, lmres.get("imgName").toString());
                    try{
                        feedItem.setImgId(Integer.parseInt(lmres.get("id").toString()));
                    }catch (Exception e){

                    }

                    //EventBus.getDefault().post(feedItem);

                    // Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));
                    PublishActivity.openWithPhotoUri(
                            PhotoProcessActivity.this,
                            getIntent().getData(),
                            feedItem
                    );
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, String rawJsonData,
                                  Object errorResponse) {
                // TODO Auto-generated method stub
                Toast.makeText(PhotoProcessActivity.this, "请求失败,请检查网络！", Toast.LENGTH_SHORT).show();
                dismissProgressDialog();
                return;
            }

            @Override
            protected Object parseResponse(String rawJsonData, boolean isFailure)
                    throws Throwable {
                // TODO Auto-generated method stub
                return null;
            }
        });
    }

//    private class SavePicToFileTask extends AsyncTask<Bitmap, Void, String> {
//        Bitmap bitmap;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            showProgressDialog("图片处理中...");
//        }
//
//        @Override
//        protected String doInBackground(Bitmap... params) {
//            String fileName = null;
//            try {
//                bitmap = params[0];
//
//                String picName = TimeUtils.dtFormat(new Date(), "yyyyMMddHHmmss");
//                fileName = ImageUtils.saveToFile(FileUtils.getInst().getPhotoSavedPath() + "/" + picName, false, bitmap);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                toast("图片处理错误，请退出相机并重试", Toast.LENGTH_LONG);
//            }
//            return fileName;
//        }
//
//        @Override
//        protected void onPostExecute(String fileName) {
//            super.onPostExecute(fileName);
//            dismissProgressDialog();
//            if (StringUtils.isEmpty(fileName)) {
//                return;
//            }
//
//            //将照片信息保存至sharedPreference
//            //保存标签信息
//            List<TagItem> tagInfoList = new ArrayList<TagItem>();
//            for (LabelView label : labels) {
//                tagInfoList.add(label.getTagInfo());
//            }
//
//            //将图片信息通过EventBus发送到MainActivity
//            FeedItem feedItem = new FeedItem(tagInfoList, fileName);
//            //EventBus.getDefault().post(feedItem);
//
//           // Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));
//            PublishActivity.openWithPhotoUri(PhotoProcessActivity.this, getIntent().getData(), feedItem);
//            //CameraManager.getInst().close();
//        }
//    }


    public void tagClick(View v) {
        TextView textView = (TextView) v;
        TagItem tagItem = new TagItem(Constant.POST_TYPE_TAG, textView.getText().toString());
        String[] str = textView.getText().toString().split(":");
        tagItem.setClick(str[1]);
        addLabel(tagItem);
    }

    private MyImageViewDrawableOverlay.OnDrawableEventListener wpEditListener = new MyImageViewDrawableOverlay.OnDrawableEventListener() {
        @Override
        public void onMove(MyHighlightView view) {
        }

        @Override
        public void onFocusChange(MyHighlightView newFocus, MyHighlightView oldFocus) {
        }

        @Override
        public void onDown(MyHighlightView view) {

        }

        @Override
        public void onClick(MyHighlightView view) {
            labelSelector.hide();
        }

        @Override
        public void onClick(final LabelView label) {
            if (label.equals(emptyLabelView)) {
                return;
            }
            alert("温馨提示", "是否需要删除该标签！", "确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EffectUtil.removeLabelEditable(mImageView, drawArea, label);
                    labels.remove(label);
                }
            }, "取消", null);
        }
    };

    private boolean setCurrentBtn(TextView btn) {
        if (currentBtn == null) {
            currentBtn = btn;
        } else if (currentBtn.equals(btn)) {
            return false;
        } else {
            currentBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
        Drawable myImage = getResources().getDrawable(R.drawable.select_icon);
        btn.setCompoundDrawablesWithIntrinsicBounds(null, null, null, myImage);
        currentBtn = btn;
        return true;
    }


    //初始化贴图
    private void initStickerToolBar() {

        bottomToolBar.setAdapter(new StickerToolAdapter(PhotoProcessActivity.this, EffectUtil.addonList));
        bottomToolBar.setOnItemClickListener(new it.sephiroth.android.library.widget.AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(it.sephiroth.android.library.widget.AdapterView<?> arg0,
                                    View arg1, int arg2, long arg3) {
                labelSelector.hide();
                Addon sticker = EffectUtil.addonList.get(arg2);
                EffectUtil.addStickerImage(mImageView, PhotoProcessActivity.this, sticker,
                        new EffectUtil.StickerCallback() {
                            @Override
                            public void onRemoveSticker(Addon sticker) {
                                labelSelector.hide();
                            }
                        });
            }
        });
        setCurrentBtn(stickerBtn);
    }


    //初始化滤镜
    private void initFilterToolBar() {
        final List<FilterEffect> filters = EffectService.getInst().getLocalFilters();
        final FilterAdapter adapter = new FilterAdapter(PhotoProcessActivity.this, filters, smallImageBackgroud);
        bottomToolBar.setAdapter(adapter);
        bottomToolBar.setOnItemClickListener(new it.sephiroth.android.library.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(it.sephiroth.android.library.widget.AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                labelSelector.hide();
                if (adapter.getSelectFilter() != arg2) {
                    adapter.setSelectFilter(arg2);
                    GPUImageFilter filter = GPUImageFilterTools.createFilterForType(
                            PhotoProcessActivity.this, filters.get(arg2).getType());
                    mGPUImageView.setFilter(filter);
                    GPUImageFilterTools.FilterAdjuster mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(filter);
                    //可调节颜色的滤镜
                    if (mFilterAdjuster.canAdjust()) {
                        //mFilterAdjuster.adjust(100); 给可调节的滤镜选一个合适的值
                    }
                }
            }
        });
    }

    //添加标签
    private void addLabel(TagItem tagItem) {
        labelSelector.hide();
        emptyLabelView.setVisibility(View.INVISIBLE);
        if (labels.size() >= 5) {
            alert("温馨提示", "您只能添加5个标签！", "确定", null, null, null, true);
        } else {
            int left = emptyLabelView.getLeft();
            int top = emptyLabelView.getTop();
            if (labels.size() == 0 && left == 0 && top == 0) {
                left = mImageView.getWidth() / 2 - 10;
                top = mImageView.getWidth() / 2;
            }
            LabelView label = new LabelView(PhotoProcessActivity.this);
            label.init(tagItem);
            EffectUtil.addLabelEditable(mImageView, drawArea, label, left, top);
            labels.add(label);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        labelSelector.hide();
        super.onActivityResult(requestCode, resultCode, data);
        if (Constant.ACTION_EDIT_LABEL == requestCode && data != null) {
            String text = data.getStringExtra(Constant.PARAM_EDIT_TEXT);
            if (StringUtils.isNotEmpty(text)) {
                TagItem tagItem = new TagItem(Constant.POST_TYPE_TAG, text);
                addLabel(tagItem);
            }
        } else if (Constant.ACTION_EDIT_LABEL_POI == requestCode && data != null) {
            String text = data.getStringExtra(Constant.PARAM_EDIT_TEXT);
            if (StringUtils.isNotEmpty(text)) {
                TagItem tagItem = new TagItem(Constant.POST_TYPE_POI, text);
                addLabel(tagItem);
            }
        }
    }
}
