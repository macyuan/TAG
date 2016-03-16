package com.ccxt.whl.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ccxt.whl.Constant;
import com.ccxt.whl.DemoApplication;
import com.ccxt.whl.R;
import com.ccxt.whl.gushi.User_gushi_Activity;
import com.ccxt.whl.utils.CommonUtils;
import com.ccxt.whl.utils.DeviceUuidFactory;
import com.ccxt.whl.utils.HttpRestClient;
import com.ccxt.whl.utils.ImageOptions;
import com.ccxt.whl.utils.JsonToMapList;
import com.ccxt.whl.utils.MyLogger;
import com.ccxt.whl.utils.PreferenceUtils;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;

import java.util.Map;

/**
 * Created by Administrator on 2015/11/11.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {
    private static MyLogger Log = MyLogger.yLog();
    //个人设置
    RelativeLayout person_setting;
    //个性签名
    RelativeLayout one;
    //我的消息
    RelativeLayout two;
    RelativeLayout three1;
    //新消息提醒设置子选项
    LinearLayout three_setting;
    //接收新消息通知
    RelativeLayout rl_switch_notification;
    //选择按钮
    ImageView iv_switch_open_notification;
    ImageView iv_switch_close_notification;
    //声音
    RelativeLayout rl_switch_sound;
    ImageView iv_switch_open_sound;
    ImageView iv_switch_close_sound;
    //震动
    RelativeLayout rl_switch_vibrate;
    ImageView iv_switch_open_vibrate;
    ImageView iv_switch_close_vibrate;

    RelativeLayout four1;
    // 聊天设置子选项
    LinearLayout four_setting;
    //使用扬声器播放声音
    RelativeLayout rl_switch_speaker;
    ImageView iv_switch_open_speaker;
    ImageView iv_switch_close_speaker;
    //通讯录黑名单
    LinearLayout ll_black_list;

    RelativeLayout five;
    /**
     * 声音和震动中间的那条线
     */
    private TextView textview1, textview2;

    private LinearLayout blacklistContainer;

    /**
     * 诊断
     */
    private LinearLayout llDiagnose;
    /**
     * 管理故事
     */
    private LinearLayout wo_gushi;

    /**
     * 退出按钮
     */
    private Button logoutBtn;

    private Button exit;

    private EMChatOptions chatOptions;
    //昵称
    TextView tv_user_nicheng;
    //头像
    ImageView iv_user_photo;
    //心情
    TextView change_xinqing;
    //新增设备唯一id
    private static String uid = null;
    private ProgressDialog pd;
    private BaseJsonHttpResponseHandler responseHandler;
    private String UserPic = null;
    private String UserNickName = null;
    private String UserQianming = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversation_settings, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DeviceUuidFactory uuid = new DeviceUuidFactory(getActivity());
        pd = new ProgressDialog(getActivity());
        pd.setMessage("正在提交请求...");
        uid = uuid.getDeviceUuid().toString();
        person_setting = (RelativeLayout) getView().findViewById(R.id.person_setting);
        three1 = (RelativeLayout) getView().findViewById(R.id.three1);
        four1 = (RelativeLayout) getView().findViewById(R.id.four1);
        five = (RelativeLayout) getView().findViewById(R.id.five);
        one = (RelativeLayout) getView().findViewById(R.id.one);
        two = (RelativeLayout) getView().findViewById(R.id.two);
        //新消息提醒设置子选项
        three_setting = (LinearLayout) getView().findViewById(R.id.three_setting);
        //接收新消息通知
        rl_switch_notification = (RelativeLayout) getView().findViewById(R.id.rl_switch_notification);
        iv_switch_open_notification = (ImageView) getView().findViewById(R.id.iv_switch_open_notification);
        iv_switch_close_notification = (ImageView) getView().findViewById(R.id.iv_switch_close_notification);
        //声音
        rl_switch_sound = (RelativeLayout) getView().findViewById(R.id.rl_switch_sound);
        iv_switch_open_sound = (ImageView) getView().findViewById(R.id.iv_switch_open_sound);
        iv_switch_close_sound = (ImageView) getView().findViewById(R.id.iv_switch_close_sound);
        //震动
        rl_switch_vibrate = (RelativeLayout) getView().findViewById(R.id.rl_switch_vibrate);
        iv_switch_open_vibrate = (ImageView) getView().findViewById(R.id.iv_switch_open_vibrate);
        iv_switch_close_vibrate = (ImageView) getView().findViewById(R.id.iv_switch_close_vibrate);

        // 聊天设置子选项
        four_setting = (LinearLayout) getView().findViewById(R.id.four_setting);
        //使用扬声器播放声音
        rl_switch_speaker = (RelativeLayout) getView().findViewById(R.id.rl_switch_speaker);
        iv_switch_open_speaker = (ImageView) getView().findViewById(R.id.iv_switch_open_speaker);
        iv_switch_close_speaker = (ImageView) getView().findViewById(R.id.iv_switch_close_speaker);
        //通讯录黑名单
        ll_black_list = (LinearLayout) getView().findViewById(R.id.ll_black_list);
        logoutBtn = (Button) getView().findViewById(R.id.btn_logout);
        exit = (Button) getView().findViewById(R.id.btn_exit);
        //昵称
        tv_user_nicheng = (TextView) getView().findViewById(R.id.tv_user_nicheng);
        //头像
        iv_user_photo = (ImageView) getView().findViewById(R.id.iv_user_photo);
        //心情
        change_xinqing = (TextView) getView().findViewById(R.id.change_xinqing);

        //获取设置用户昵称和头像和心情
        UserPic = PreferenceUtils.getInstance(getActivity()).getSettingUserPic();
        UserNickName = PreferenceUtils.getInstance(getActivity()).getSettingUserNickName();
        UserQianming = PreferenceUtils.getInstance(getActivity()).getSettingUserQianming();
        ImageLoader.getInstance().displayImage(UserPic, iv_user_photo, ImageOptions.getOptions());
        tv_user_nicheng.setText(UserNickName);
        change_xinqing.setText(UserQianming);

        if (!TextUtils.isEmpty(EMChatManager.getInstance().getCurrentUser())) {
            //logoutBtn.setText(getString(R.string.button_logout) + "(" + EMChatManager.getInstance().getCurrentUser() + ")");
            logoutBtn.setText(getString(R.string.button_logout));

        }

        textview1 = (TextView) getView().findViewById(R.id.textview1);
        textview2 = (TextView) getView().findViewById(R.id.textview2);

        blacklistContainer = (LinearLayout) getView().findViewById(R.id.ll_black_list);
        wo_gushi = (LinearLayout) getView().findViewById(R.id.wo_gushi);

        /***********设置**********/

        blacklistContainer.setOnClickListener(this);
        three1.setOnClickListener(this);
        four1.setOnClickListener(this);
        one.setOnClickListener(this);
        two.setOnClickListener(this);
        rl_switch_notification.setOnClickListener(this);
        rl_switch_sound.setOnClickListener(this);
        rl_switch_vibrate.setOnClickListener(this);
        rl_switch_speaker.setOnClickListener(this);
        five.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        exit.setOnClickListener(this);

        wo_gushi.setOnClickListener(this);
        person_setting.setOnClickListener(this);

        chatOptions = EMChatManager.getInstance().getChatOptions();
        if (chatOptions.getNotificationEnable()) {
            iv_switch_open_notification.setVisibility(View.VISIBLE);
            iv_switch_close_notification.setVisibility(View.INVISIBLE);
        } else {
            iv_switch_open_notification.setVisibility(View.INVISIBLE);
            iv_switch_close_notification.setVisibility(View.VISIBLE);
        }
        if (chatOptions.getNoticedBySound()) {
            iv_switch_open_sound.setVisibility(View.VISIBLE);
            iv_switch_close_sound.setVisibility(View.INVISIBLE);
        } else {
            iv_switch_open_sound.setVisibility(View.INVISIBLE);
            iv_switch_close_sound.setVisibility(View.VISIBLE);
        }
        if (chatOptions.getNoticedByVibrate()) {
            iv_switch_open_vibrate.setVisibility(View.VISIBLE);
            iv_switch_close_vibrate.setVisibility(View.INVISIBLE);
        } else {
            iv_switch_open_vibrate.setVisibility(View.INVISIBLE);
            iv_switch_close_vibrate.setVisibility(View.VISIBLE);
        }

        if (chatOptions.getUseSpeaker()) {
            iv_switch_open_speaker.setVisibility(View.VISIBLE);
            iv_switch_close_speaker.setVisibility(View.INVISIBLE);
        } else {
            iv_switch_open_speaker.setVisibility(View.INVISIBLE);
            iv_switch_close_speaker.setVisibility(View.VISIBLE);
        }

        /*************************http请求处理***********************/
        responseHandler = new BaseJsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  String rawJsonResponse, Object response) {
                // TODO Auto-generated method stub
                Log.d("setting_qes" + rawJsonResponse);
                pd.dismiss();
                if (CommonUtils.isNullOrEmpty(rawJsonResponse)) {
                    Toast.makeText(getActivity(), "您的网络不稳定,请检查网络！", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> lm = JsonToMapList.getMap(rawJsonResponse);
                if (lm.get("status").toString() != null && lm.get("status").toString().equals("yes")) {
                    Toast.makeText(getActivity(), "更新成功", Toast.LENGTH_SHORT).show();
                    Log.d("message==" + lm.get("message").toString());
                    if (!CommonUtils.isNullOrEmpty(lm.get("result").toString())) {
                        Map<String, Object> lmres = JsonToMapList.getMap(lm.get("result").toString());

                        String qianming = lmres.get("qianming").toString();
                        if (!CommonUtils.isNullOrEmpty(qianming)) {//更新签名
                            change_xinqing.setText(qianming);
                            PreferenceUtils.getInstance(getActivity()).setSettingUserQianming(qianming);
                        }

                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, String rawJsonData,
                                  Object errorResponse) {
                // TODO Auto-generated method stub
                pd.dismiss();
                Toast.makeText(getActivity(), "网络请求失败,请检查网络！", Toast.LENGTH_SHORT).show();
                return;
            }

            @Override
            protected Object parseResponse(String rawJsonData,
                                           boolean isFailure) throws Throwable {
                // TODO Auto-generated method stub
                return null;
            }

        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.person_setting:
                Intent intent = new Intent(getActivity(), Setting.class);
                startActivity(intent);
                break;
            case R.id.three1:
                if (three_setting.getVisibility() == View.GONE) {
                    three_setting.setVisibility(View.VISIBLE);
                } else {
                    three_setting.setVisibility(View.GONE);
                }
                break;
            case R.id.one:
                //个性签名
                //修改签名
                change_qianming("" + change_xinqing.getText().toString().trim());
                break;
            case R.id.two:
                //我的消息
                Intent intent1 = new Intent(getActivity(), ChatAllHistoryActivity.class);
                startActivity(intent1);
                break;
            case R.id.four1:
                if (four_setting.getVisibility() == View.GONE) {
                    four_setting.setVisibility(View.VISIBLE);
                } else {
                    four_setting.setVisibility(View.GONE);
                }
                break;
            case R.id.rl_switch_notification:
                if (iv_switch_open_notification.getVisibility() == View.VISIBLE) {
                    iv_switch_open_notification.setVisibility(View.INVISIBLE);
                    iv_switch_close_notification.setVisibility(View.VISIBLE);
                    rl_switch_sound.setVisibility(View.GONE);
                    rl_switch_vibrate.setVisibility(View.GONE);
                    textview1.setVisibility(View.GONE);
                    textview2.setVisibility(View.GONE);
                    chatOptions.setNotificationEnable(false);
                    EMChatManager.getInstance().setChatOptions(chatOptions);

                    PreferenceUtils.getInstance(getActivity()).setSettingMsgNotification(false);
                } else {
                    iv_switch_open_notification.setVisibility(View.VISIBLE);
                    iv_switch_close_notification.setVisibility(View.INVISIBLE);
                    rl_switch_sound.setVisibility(View.VISIBLE);
                    rl_switch_vibrate.setVisibility(View.VISIBLE);
                    textview1.setVisibility(View.VISIBLE);
                    textview2.setVisibility(View.VISIBLE);
                    chatOptions.setNotificationEnable(true);
                    EMChatManager.getInstance().setChatOptions(chatOptions);
                    PreferenceUtils.getInstance(getActivity()).setSettingMsgNotification(true);
                }
                break;
            case R.id.rl_switch_sound:
                if (iv_switch_open_sound.getVisibility() == View.VISIBLE) {
                    iv_switch_open_sound.setVisibility(View.INVISIBLE);
                    iv_switch_close_sound.setVisibility(View.VISIBLE);
                    chatOptions.setNoticeBySound(false);
                    EMChatManager.getInstance().setChatOptions(chatOptions);
                    PreferenceUtils.getInstance(getActivity()).setSettingMsgSound(false);
                } else {
                    iv_switch_open_sound.setVisibility(View.VISIBLE);
                    iv_switch_close_sound.setVisibility(View.INVISIBLE);
                    chatOptions.setNoticeBySound(true);
                    EMChatManager.getInstance().setChatOptions(chatOptions);
                    PreferenceUtils.getInstance(getActivity()).setSettingMsgSound(true);
                }
                break;
            case R.id.rl_switch_vibrate:
                if (iv_switch_open_vibrate.getVisibility() == View.VISIBLE) {
                    iv_switch_open_vibrate.setVisibility(View.INVISIBLE);
                    iv_switch_close_vibrate.setVisibility(View.VISIBLE);
                    chatOptions.setNoticedByVibrate(false);
                    EMChatManager.getInstance().setChatOptions(chatOptions);
                    PreferenceUtils.getInstance(getActivity()).setSettingMsgVibrate(false);
                } else {
                    iv_switch_open_vibrate.setVisibility(View.VISIBLE);
                    iv_switch_close_vibrate.setVisibility(View.INVISIBLE);
                    chatOptions.setNoticedByVibrate(true);
                    EMChatManager.getInstance().setChatOptions(chatOptions);
                    PreferenceUtils.getInstance(getActivity()).setSettingMsgVibrate(true);
                }
                break;
            case R.id.rl_switch_speaker:
                if (iv_switch_open_speaker.getVisibility() == View.VISIBLE) {
                    iv_switch_open_speaker.setVisibility(View.INVISIBLE);
                    iv_switch_close_speaker.setVisibility(View.VISIBLE);
                    chatOptions.setUseSpeaker(false);
                    EMChatManager.getInstance().setChatOptions(chatOptions);
                    PreferenceUtils.getInstance(getActivity()).setSettingMsgSpeaker(false);
                } else {
                    iv_switch_open_speaker.setVisibility(View.VISIBLE);
                    iv_switch_close_speaker.setVisibility(View.INVISIBLE);
                    chatOptions.setUseSpeaker(true);
                    EMChatManager.getInstance().setChatOptions(chatOptions);
                    PreferenceUtils.getInstance(getActivity()).setSettingMsgVibrate(true);
                }
                break;
            case R.id.btn_logout:
                DemoApplication.getInstance().logout();
                // 重新显示登陆页面
                ((MainActivity) getActivity()).finish();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case R.id.btn_exit:
                //DemoApplication.getInstance().logout();
                // 重新显示登陆页面
                //((MainActivity) getActivity()).finish();
                android.os.Process.killProcess(android.os.Process.myPid()); //获取PID
                System.exit(0); //常规java、c#的标准退出法，返回值为0代表正常退出
                break;

            case R.id.ll_black_list:
                startActivity(new Intent(getActivity(), BlacklistActivity.class));
                break;
            case R.id.five:
                startActivity(new Intent(getActivity(), DiagnoseActivity.class));
                break;
            case R.id.wo_gushi:
                startActivity(new Intent(getActivity(), User_gushi_Activity.class).putExtra("userId"
                        , DemoApplication.getInstance().getUser()));
                break;
            default:
                break;
        }
    }

    /**
     * 更改签名
     */
    private void change_qianming(String qianming) {
        // TODO Auto-generated method stub
        final EditText texta = new EditText(getActivity());
        texta.setText(qianming);
        new android.app.AlertDialog.Builder(getActivity())
                .setTitle("请输入您的签名")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(texta)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String nickname = texta.getEditableText().toString();
                        RequestParams params = new RequestParams();

                        params.add("user", DemoApplication.getInstance().getUser());
                        params.add("qianming", nickname);
                        params.add("param", "qianming");
                        params.add("uid", uid);
                        HttpRestClient.get(Constant.UPDATE_USER_URL, params, responseHandler);
                        pd.show();
                        dialog.dismiss();
                        //设置你的操作事项
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
