package com.ccxt.whl.stickercamera.app.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.ccxt.whl.Constant;
import com.ccxt.whl.DemoApplication;
import com.ccxt.whl.R;
import com.ccxt.whl.common.util.DataUtils;
import com.ccxt.whl.common.util.StringUtils;
import com.ccxt.whl.utils.HttpRestClient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.melnykov.fab.FloatingActionButton;
import com.ccxt.whl.stickercamera.app.Utils;
import com.ccxt.whl.stickercamera.app.camera.CameraManager;
import com.ccxt.whl.stickercamera.app.camera.adapter.FeedAdapter;
import com.ccxt.whl.stickercamera.app.model.FeedItem;
import com.ccxt.whl.stickercamera.app.view.FeedContextMenu;
import com.ccxt.whl.stickercamera.app.view.FeedContextMenuManager;
import com.ccxt.whl.stickercamera.base.BaseActivity;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * 主界面
 * Created by sky on 2015/7/20.
 * Weibo: http://weibo.com/2030683111
 * Email: 1132234509@qq.com
 */
public class CameraMainActivity extends BaseActivity implements FeedAdapter.OnFeedItemClickListener,
        FeedContextMenu.OnFeedContextMenuItemClickListener {

    @InjectView(R.id.btnCreate)
    FloatingActionButton fab;

    @InjectView(R.id.rvFeed)
    RecyclerView mRecyclerView;
    private List<FeedItem> feedList;
    // private PictureAdapter mAdapter;

    public static final String ACTION_SHOW_LOADING_ITEM = "action_show_loading_item";

    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;
    @InjectView(R.id.content)
    CoordinatorLayout clContent;

    private FeedAdapter feedAdapter;
    public static int count = 0;
    private boolean pendingIntroAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);
        initView();
        setupFeed();

        if (savedInstanceState == null) {
            pendingIntroAnimation = true;
        } else {
            feedAdapter.updateItems(false);
        }
        //如果没有照片则打开相机
//        String str = DataUtils.getStringPreferences(DemoApplication.getApp(), Constant.FEED_INFO);
        //从数据库中下载，今后要加入limit
        RequestParams params = new RequestParams();
        params.add("check", "250");
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://ngc123.sinaapp.com/index.php/Tag/getImgList.html", params, new BaseJsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                String str = rawJsonResponse;
                if (StringUtils.isNotEmpty(str)) {
                    feedList = JSON.parseArray(str, FeedItem.class);
                    Log.d("5v",str);
                    count = feedList.size();
                }
                if (feedList == null) {
                    CameraManager.getInst().openCamera(CameraMainActivity.this);
                } else {
                    feedAdapter.setList(feedList);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {
                Log.d("5v",rawJsonData+"1");
                toast("网络繁忙，请重试" + statusCode,9);
            }

            @Override
            protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });

    }

   /* @Override
    protected void onResume() {
        //如果没有照片则打开相机
        String str = DataUtils.getStringPreferences(App.getApp(), AppConstants.FEED_INFO);
        if (StringUtils.isNotEmpty(str)) {
            feedList = JSON.parseArray(str, FeedItem.class);
        }
        if (feedList == null) {
            CameraManager.getInst().openCamera(MainActivity.this);
        } else {
            feedAdapter.setList(feedList);
        }
        super.onResume();
    }*/

    public void onEventMainThread(FeedItem feedItem) {
        if (feedList == null) {
            feedList = new ArrayList<FeedItem>();
        }
        //feedList.add(0, feedItem);
        //保存图片信息
        //DataUtils.setStringPreferences(DemoApplication.getApp(), Constant.FEED_INFO, JSON.toJSONString(feedList));
        RequestParams params = new RequestParams();

        params.add("json", JSON.toJSONString(feedItem));
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(
                "http://ngc123.sinaapp.com/index.php/Tag/setFeedJson",
                params,
                new BaseJsonHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        showProgressDialog("图片分享中...");
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                        Log.d("5v", rawJsonResponse);
                        dismissProgressDialog();

                        feedList.add(0, feedItem);
                        feedAdapter.setList(feedList);
                        feedAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {
                        dismissProgressDialog();
                        toast("出错了,请检查网络",3);
                    }

                    @Override
                    protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                        return null;
                    }
                }
        );

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        // titleBar.hideLeftBtn();
        // titleBar.hideRightBtn();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedAdapter = new FeedAdapter(CameraMainActivity.this);
        mRecyclerView.setAdapter(feedAdapter);
        // fab.setOnClickListener(v -> CameraManager.getInst().openCamera(MainActivity.this));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CameraManager.getInst().openCamera(CameraMainActivity.this);
            }
        });
    }

    private void setupFeed() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        mRecyclerView.setLayoutManager(linearLayoutManager);

        feedAdapter = new FeedAdapter(this);
        feedAdapter.setOnFeedItemClickListener(this);
        mRecyclerView.setAdapter(feedAdapter);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                FeedContextMenuManager.getInstance().onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (ACTION_SHOW_LOADING_ITEM.equals(intent.getAction())) {
            showFeedLoadingItemDelayed();
        }
    }

    private void showFeedLoadingItemDelayed() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.smoothScrollToPosition(0);
                feedAdapter.showLoadingView();
            }
        }, 500);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (pendingIntroAnimation) {
            pendingIntroAnimation = false;
            startIntroAnimation();
        }
        return true;
    }

    private void startIntroAnimation() {
        fab.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));

        int actionbarSize = Utils.dpToPx(56);
        getToolbar().setTranslationY(-actionbarSize);
        getIvLogo().setTranslationY(-actionbarSize);
        getInboxMenuItem().getActionView().setTranslationY(-actionbarSize);

        getToolbar().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(300);
        getIvLogo().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(400);
        getInboxMenuItem().getActionView().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startContentAnimation();
                    }
                })
                .start();
    }

    private void startContentAnimation() {
        fab.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(300)
                .setDuration(ANIM_DURATION_FAB)
                .start();
        feedAdapter.updateItems(true);
    }

    @Override
    public void onCommentsClick(View v, int position) {
        final Intent intent = new Intent(this, CommentsActivity.class);
        int[] startingLocation = new int[2];
        v.getLocationOnScreen(startingLocation);
        intent.putExtra(CommentsActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onMoreClick(View v, int itemPosition) {
        FeedContextMenuManager.getInstance().toggleContextMenuFromView(v, itemPosition, this);
    }

    @Override
    public void onProfileClick(View v) {
        int[] startingLocation = new int[2];
        v.getLocationOnScreen(startingLocation);
        startingLocation[0] += v.getWidth() / 2;
        UserProfileActivity.startUserProfileFromLocation(startingLocation, this);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onReportClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onSharePhotoClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onCopyShareUrlClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onCancelClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }


    public void showLikedSnackbar() {
        Snackbar.make(clContent, "Liked!", Snackbar.LENGTH_SHORT).show();
    }

}
