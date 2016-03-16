package com.ccxt.whl.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.method.NumberKeyListener;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ccxt.whl.Constant;
import com.ccxt.whl.DemoApplication;
import com.ccxt.whl.R;
import com.ccxt.whl.utils.CommonUtils;
import com.ccxt.whl.utils.DeviceUuidFactory;
import com.ccxt.whl.utils.HttpRestClient;
import com.ccxt.whl.utils.ImageOptions;
import com.ccxt.whl.utils.JsonToMapList;
import com.ccxt.whl.utils.MyLogger;
import com.ccxt.whl.utils.PreferenceUtils;
import com.easemob.util.PathUtil;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Created by Administrator on 2015/11/11.
 */
public class Setting extends Activity implements View.OnClickListener {

    private static MyLogger Log = MyLogger.yLog();
    /**********************************新增用户信息*****************************************/
    /**
     * 设置用户头像
     */
    private RelativeLayout rl_user_pic;
    /**
     * 设置用户昵称
     */
    private RelativeLayout rl_user_nicheng;
    /**
     * 设置用户性别
     */
    private RelativeLayout rl_user_xingbie;
    /**
     * 设置用户年龄
     */
    private RelativeLayout rl_user_nianling;
    /**
     * 设置用户城市
     */
    private RelativeLayout rl_user_chengshi;
    /**
     * 设置用户职业
     */
    private RelativeLayout rl_user_zhiye;
    /**
     * 设置用户签名
     */
    private RelativeLayout rl_user_qianming;
    /**
     * 设置用户在哪的动态
     */
    private RelativeLayout rl_user_zainadongtai;

    /**
     * 用户头像imageView
     */
    private ImageView iv_user_photo;

    /**
     * 用户昵称
     */
    private TextView tv_user_nicheng;
    /**
     * 用户性别
     */
    private TextView tv_user_xingbie;
    /**
     * 用户年龄
     */
    private TextView tv_user_nianling;
    /**
     * 用户地区
     */
    private TextView tv_user_chengshi;
    /**
     * 用户职业
     */
    private TextView tv_user_zhiye;
    /**
     * 用户签名
     */
    private TextView tv_user_qianming;
    /**
     * 用户在哪动态
     */
    public static TextView tv_user_zainadongtai;

    private String UserPic = null;
    private String UserNickName = null;
    private String UserSex = null;
    private String UserAge = null;
    private String UserArea = null;
    private String UserZaina = null;
    private String UserZhiye = null;
    private String UserQianming = null;

    /*************
     * 新加逻辑变量
     */
    private BaseJsonHttpResponseHandler responseHandler;
    private File cameraFile;//照相机拍照的图片
    //	private File cutFile;//剪切后的图片
    //头像uri
    private static String IMAGE_FILE_LOCATION = null;
    private Uri imageUri = null;
    private static final String IMAGE_FILE_LOCATION_TEAST = "file:///sdcard/lehu/temp.jpg";//temp file
    Uri imageUritest = Uri.parse(IMAGE_FILE_LOCATION_TEAST);//The Uri to store the big bitmap
    //private Uri imageUri = null;//Uri.parse(IMAGE_FILE_LOCATION);//The Uri to store the big bitmap
    public static final int USERPIC_REQUEST_CODE_LOCAL = 101;
    public static final int USERPIC_REQUEST_CODE_LOCAL_19 = 101;
    public static final int USERPIC_REQUEST_CODE_CAMERA = 102;
    public static final int USERPIC_REQUEST_CODE_CUT = 103;

    private ProgressDialog pd;
    //新增设备唯一id
    private static String uid = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_setting);
        init();
    }

    public void init() {

        DeviceUuidFactory uuid = new DeviceUuidFactory(Setting.this);
        uid = uuid.getDeviceUuid().toString();
        pd = new ProgressDialog(Setting.this);
        pd.setMessage("正在提交请求...");
        /***************用户信息设置**************/
        rl_user_pic = (RelativeLayout) findViewById(R.id.rl_user_pic);
        rl_user_nicheng = (RelativeLayout) findViewById(R.id.rl_user_nicheng);
        rl_user_xingbie = (RelativeLayout) findViewById(R.id.rl_user_xingbie);
        rl_user_nianling = (RelativeLayout) findViewById(R.id.rl_user_nianling);
        rl_user_chengshi = (RelativeLayout) findViewById(R.id.rl_user_chengshi);
        rl_user_zainadongtai = (RelativeLayout) findViewById(R.id.rl_user_zainadongtai);
        //---------
        rl_user_zhiye = (RelativeLayout) findViewById(R.id.rl_user_zhiye);
        rl_user_qianming = (RelativeLayout) findViewById(R.id.rl_user_qianming);

        //实例化用户信息组件
        iv_user_photo = (ImageView) findViewById(R.id.iv_user_photo);
        tv_user_nicheng = (TextView) findViewById(R.id.tv_user_nicheng);
        tv_user_xingbie = (TextView) findViewById(R.id.tv_user_xingbie);
        tv_user_nianling = (TextView) findViewById(R.id.tv_user_nianling);
        tv_user_chengshi = (TextView) findViewById(R.id.tv_user_chengshi);
        tv_user_zainadongtai = (TextView) findViewById(R.id.tv_user_zainadongtai);
        //--------
        tv_user_zhiye = (TextView) findViewById(R.id.tv_user_zhiye);
        tv_user_qianming = (TextView) findViewById(R.id.tv_user_qianming);

        //获取用户信息
        UserPic = PreferenceUtils.getInstance(Setting.this).getSettingUserPic();
        UserNickName = PreferenceUtils.getInstance(Setting.this).getSettingUserNickName();
        UserSex = PreferenceUtils.getInstance(Setting.this).getSettingUserSex();
        UserAge = PreferenceUtils.getInstance(Setting.this).getSettingUserAge();
        UserArea = PreferenceUtils.getInstance(Setting.this).getSettingUserArea();
        UserZaina = PreferenceUtils.getInstance(Setting.this).getSettingUserZaina();
        //-----
        UserZhiye = PreferenceUtils.getInstance(Setting.this).getSettingUserZhiye();
        UserQianming = PreferenceUtils.getInstance(Setting.this).getSettingUserQianming();

        //设置用户信息
        //iv_user_photo
        ImageLoader.getInstance().displayImage(UserPic, iv_user_photo, ImageOptions.getOptions());
        tv_user_nicheng.setText(UserNickName);
        tv_user_xingbie.setText(UserSex);
        tv_user_nianling.setText(UserAge);
        tv_user_chengshi.setText(UserArea);
        tv_user_zhiye.setText(UserZhiye);
        tv_user_qianming.setText(UserQianming);

        if (CommonUtils.isNullOrEmpty(UserZaina)) {
            UserZaina = "暂时获取不到您的位置";
        }
        tv_user_zainadongtai.setText(UserZaina);

        /**********设置***********/
        rl_user_pic.setOnClickListener(this);
        rl_user_nicheng.setOnClickListener(this);
        rl_user_xingbie.setOnClickListener(this);
        rl_user_nianling.setOnClickListener(this);
        rl_user_chengshi.setOnClickListener(this);
        rl_user_zainadongtai.setOnClickListener(this);
        rl_user_zhiye.setOnClickListener(this);
        rl_user_qianming.setOnClickListener(this);

        /*************************http请求处理***********************/
        responseHandler = new BaseJsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  String rawJsonResponse, Object response) {
                // TODO Auto-generated method stub
                Log.d("setting_qes" + rawJsonResponse);
                pd.dismiss();
                if (CommonUtils.isNullOrEmpty(rawJsonResponse)) {
                    Toast.makeText(Setting.this, "您的网络不稳定,请检查网络！", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> lm = JsonToMapList.getMap(rawJsonResponse);
                if (lm.get("status").toString() != null && lm.get("status").toString().equals("yes")) {
                    Toast.makeText(Setting.this, "更新成功", Toast.LENGTH_SHORT).show();
                    Log.d("message==" + lm.get("message").toString());
                    if (!CommonUtils.isNullOrEmpty(lm.get("result").toString())) {
                        Map<String, Object> lmres = JsonToMapList.getMap(lm.get("result").toString());
                        String nickname = lmres.get("nickname").toString();
                        String age = lmres.get("age").toString();
                        String sex = lmres.get("sex").toString();
                        String headurl = lmres.get("headurl").toString();
                        String zhiye = lmres.get("zhiye").toString();
                        String qianming = lmres.get("qianming").toString();
                        if (!CommonUtils.isNullOrEmpty(nickname)) {
                            tv_user_nicheng.setText(nickname);
                            PreferenceUtils.getInstance(Setting.this).setSettingUserNickName(nickname);
                        } else if (!CommonUtils.isNullOrEmpty(age)) {
                            tv_user_nianling.setText(age);
                            PreferenceUtils.getInstance(Setting.this).setSettingUserAge(age);
                        } else if (!CommonUtils.isNullOrEmpty(sex)) {
                            tv_user_xingbie.setText(sex);
                            PreferenceUtils.getInstance(Setting.this).setSettingUserSex(age);
                        } else if (!CommonUtils.isNullOrEmpty(headurl)) {
                            //tv_user_xingbie.setText(sex);
                            //更新头像
                            ImageLoader.getInstance().displayImage(headurl, iv_user_photo, ImageOptions.getOptions());
                            //ImageLoader.getInstance().displayImage(headurl, iv_user_photo);
                            //ImageLoader.getInstance().notify();
                            PreferenceUtils.getInstance(Setting.this).setSettingUserPic(headurl);
                        } else if (!CommonUtils.isNullOrEmpty(zhiye)) {//更新职业
                            tv_user_zhiye.setText(zhiye);
                            PreferenceUtils.getInstance(Setting.this).setSettingUserZhiye(zhiye);
                        } else if (!CommonUtils.isNullOrEmpty(qianming)) {//更新签名
                            tv_user_qianming.setText(qianming);
                            PreferenceUtils.getInstance(Setting.this).setSettingUserQianming(qianming);
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
                Toast.makeText(Setting.this, "网络请求失败,请检查网络！", Toast.LENGTH_SHORT).show();
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
            case R.id.rl_user_pic:
                //startActivity(new Intent(getActivity(), BlacklistActivity.class));
                //更改头像图片
                change_headurl();
                break;
            case R.id.rl_user_nicheng:
                //startActivity(new Intent(getActivity(), BlacklistActivity.class));
                //修改昵称
                change_nickname("" + tv_user_nicheng.getText().toString().trim());
                break;
            case R.id.rl_user_xingbie:
                //修改性别
                change_sex();
                break;
            case R.id.rl_user_nianling:
                //修改年龄
                change_age("" + tv_user_nianling.getText().toString().trim());
            case R.id.rl_user_chengshi:
                //修改城市
                break;
            case R.id.rl_user_zhiye:
                //修改职业
                change_zhiye();
                break;
            case R.id.rl_user_qianming:
                //修改签名
                change_qianming("" + tv_user_qianming.getText().toString().trim());
                break;
            case R.id.rl_user_zainadongtai:
                //修改在哪动态
                break;
            default:
                break;
        }
    }

    /**
     * 更改职业
     */
    private void change_zhiye() {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(Setting.this);
        String[] strarr = {"计算机/互联网/通信", "生产/工艺/制造", "商业/服务业/个体经营"
                , "金融/银行/投资/保险", "文化/广告/传媒", "娱乐/艺术/表演", "医疗/护理/制药"
                , "律师/法务", "教育/培训", "公务员/事业单位", "学生", "无"};
        builder.setItems(strarr, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

                String zhiye = "无";

                switch (arg1) {
                    case 0:
                        zhiye = "计算机/互联网/通信";
                        break;
                    case 1:
                        zhiye = "生产/工艺/制造";
                        break;
                    case 2:
                        zhiye = "商业/服务业/个体经营";
                        break;
                    case 3:
                        zhiye = "金融/银行/投资/保险";
                        break;
                    case 4:
                        zhiye = "文化/广告/传媒";
                        break;
                    case 5:
                        zhiye = "娱乐/艺术/表演";
                        break;
                    case 6:
                        zhiye = "医疗/护理/制药";
                        break;
                    case 7:
                        zhiye = "律师/法务";
                        break;
                    case 8:
                        zhiye = "教育/培训";
                        break;
                    case 9:
                        zhiye = "公务员/事业单位";
                        break;
                    case 10:
                        zhiye = "学生";
                        break;
                    default:
                        zhiye = "无";
                        break;
                }

                RequestParams params = new RequestParams();
                params.add("user", DemoApplication.getInstance().getUser());
                params.add("zhiye", zhiye);
                params.add("param", "zhiye");
                params.add("uid", uid);

                HttpRestClient.get(Constant.UPDATE_USER_URL, params, responseHandler);
                pd.show();
            }
        });
        builder.show();
    }

    /**
     * 更改签名
     */
    private void change_qianming(String qianming) {
        // TODO Auto-generated method stub
        final EditText texta = new EditText(Setting.this);
        texta.setText(qianming);
        new AlertDialog.Builder(Setting.this)
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

    /**
     * 更改年龄
     */
    public void change_age(String age) {

        final EditText texta = new EditText(Setting.this);
        texta.setText(age);
        //设置EditText输入类型
        texta.setKeyListener(new NumberKeyListener() {
            public int getInputType() {
                return InputType.TYPE_CLASS_PHONE;
            }

            @Override
            protected char[] getAcceptedChars() {
                // TODO Auto-generated method stub
                char[] numbers = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
                return numbers;
            }
        });
        new AlertDialog.Builder(Setting.this)
                .setTitle("请输入您的年龄")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(texta)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String age = texta.getEditableText().toString();
                        RequestParams params = new RequestParams();

                        params.add("user", DemoApplication.getInstance().getUser());
                        params.add("age", age);
                        params.add("param", "age");
                        params.add("uid", uid);
                        HttpRestClient.get(Constant.UPDATE_USER_URL, params, responseHandler);
                        pd.show();
                        dialog.dismiss();
                        //设置你的操作事项
                    }
                })
                .setNegativeButton("取消", null)
                .show();
        //return true;
    }

    /**
     * 更改昵称
     */
    public void change_nickname(String nickname) {

        final EditText texta = new EditText(Setting.this);
        texta.setText(nickname);
        new AlertDialog.Builder(Setting.this)
                .setTitle("请输入您的昵称")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(texta)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String nickname = texta.getEditableText().toString();
                        RequestParams params = new RequestParams();

                        params.add("user", DemoApplication.getInstance().getUser());
                        params.add("nickname", nickname);
                        params.add("param", "nickname");
                        params.add("uid", uid);
                        HttpRestClient.get(Constant.UPDATE_USER_URL, params, responseHandler);
                        pd.show();
                        dialog.dismiss();
                        //设置你的操作事项
                    }
                })
                .setNegativeButton("取消", null)
                .show();
        //return true;
    }

    /**
     * 更改性别
     */
    public void change_sex() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Setting.this);
        String[] strarr = {"男", "女"};
        builder.setItems(strarr, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                String sex = "2";
                // TODO 自动生成的方法存根
                if (arg1 == 0) {//男
                    sex = "1";
                } else {//女
                    sex = "2";
                }
                RequestParams params = new RequestParams();
                params.add("user", DemoApplication.getInstance().getUser());
                params.add("sex", sex);
                params.add("param", "sex");
                params.add("uid", uid);
                HttpRestClient.get(Constant.UPDATE_USER_URL, params, responseHandler);
                pd.show();
            }
        });
        builder.show();
    }

    /**
     * 更改头像
     */
    public void change_headurl() {
        /*//如果挂在SDcard
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			//在SDcard文件TestSyncListView下创建文件
			IMAGE_FILE_LOCATION = Environment.getExternalStorageDirectory()+Constant.CACHE_DIR_IMAGE+"/temp.jpg";
			imageUri = Uri.parse(IMAGE_FILE_LOCATION);
			//File cutFile = new File(Constant.CACHE_DIR_IMAGE,"temp.jpg");
			//cutFile.getParentFile().mkdirs();
			File dir = new File(IMAGE_FILE_LOCATION);
	        if (!dir.exists()) {
	              try {
	                  //在指定的文件夹中创建文件
	                  dir.createNewFile();
	            } catch (Exception e) {
	            	//println(e);
	            }
	        }

		}else{
			Toast.makeText(getActivity(), "SD卡不存在，不能更改头像", 0).show();
			return;
		}*/
        /*创建缓存图片文件-要用于照相和本地图片选择缓存*/
        if (!CommonUtils.isExitsSdcard()) {
            Toast.makeText(Setting.this, "SD卡不存在，不能更改头像", Toast.LENGTH_SHORT).show();
            return;
        }
        cameraFile = new File(PathUtil.getInstance().getImagePath(), DemoApplication.getInstance().getUser()
                + System.currentTimeMillis() + ".jpg");
        if (cameraFile == null && !cameraFile.exists()) {//如果文件存在就不在创建
            cameraFile.getParentFile().mkdirs();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(Setting.this);
        String[] strarr = {"选择拍照", "选择本地相册"};
        builder.setItems(strarr, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO 自动生成的方法存根
                if (arg1 == 0) {//选择拍照
                    selectPicFromCamera();
                } else {//选择本地相册
                    selectPicFromLocal();
                }
            }
        });
        builder.show();
    }

    /**
     * 照相获取图片
     */
    public void selectPicFromCamera() {

//		cameraFile = new File(PathUtil.getInstance().getImagePath(), DemoApplication.getInstance().getUserName()

        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                USERPIC_REQUEST_CODE_CAMERA);
    }

    /**
     * 从图库获取图片
     */
    public void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

            //Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
            /*intent.setType("image/*");
            intent.putExtra("crop", "true");
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", 600);
			intent.putExtra("outputY", 600);
			intent.putExtra("scale", true);
			intent.putExtra("return-data", false);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUritest);
			intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
			intent.putExtra("noFaceDetection", true); // no face detection
			 */
            //startActivityForResult(intent, CHOOSE_BIG_PICTURE);
        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            //startActivityForResult(intent, USERPIC_REQUEST_CODE_LOCAL_19);
        }
        startActivityForResult(intent, USERPIC_REQUEST_CODE_LOCAL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == USERPIC_REQUEST_CODE_CAMERA) { // 获取照片
            if (cameraFile != null && cameraFile.exists()) {
                Log.d("cameraFile" + cameraFile.getAbsolutePath());
                //改成返回到指定的uri imageUri = Uri.fromFile(cameraFile);
                cropImageUri(Uri.fromFile(cameraFile), 300, 300, USERPIC_REQUEST_CODE_CUT);

            }
        } else if (requestCode == USERPIC_REQUEST_CODE_LOCAL) { // 获取本地图片
            if (data != null) {
                Uri selectedImage = data.getData();
                if (selectedImage != null) {
                    cropImageUri(selectedImage, 300, 300, USERPIC_REQUEST_CODE_CUT);
                    //Log.d("log","selectedImage"+selectedImage);

                }
            }
        } else if (requestCode == USERPIC_REQUEST_CODE_CUT) {//裁剪图片
            // 从剪切图片返回的数据
            if (data != null) {
                Bitmap bitmap = data.getParcelableExtra("data");
                iv_user_photo.setImageBitmap(bitmap);

                File file = saveJPGE_After(bitmap, cameraFile); //获取截取图片后的数据

                RequestParams params = new RequestParams();
                if (file.exists()) {
                    try {
                        params.put("headurl", file, "image/jpeg");
                        params.put("user", DemoApplication.getInstance().getUser());
                        params.put("param", "headurl");
                        params.add("uid", uid);
                        HttpRestClient.post(Constant.UPDATE_USER_URL, params, responseHandler);
                        pd.show();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    Toast toast = Toast.makeText(Setting.this, "无法获取图片，请检查SD卡是否存在", Toast.LENGTH_SHORT);
                }

            } else {
                // Log.e(TAG, "CHOOSE_SMALL_PICTURE: data = " + data);

            }
            // Log.d("log","imageUribundle==>"+imageUri);
            //iv_user_photo.setImageURI(imageUri);


            //params.put(key, file, )

            //**获取返回图片的数据用于返回data是使用
            //Bitmap bitmap = data.getParcelableExtra("data");
            // Bitmap bitmap = data.getExtras().getParcelable("data");

	                /*压缩图片
                    // ByteArrayOutputStream out = new ByteArrayOutputStream();
	                // bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
	                */

            //this.iv_image.setImageBitmap(bitmap);
                   /* }else{
	     				Toast toast = Toast.makeText(getActivity(), "无法获取图片，请检查SD卡是否存在", Toast.LENGTH_SHORT);

	     			} */

	            /*  try {
	                // 将临时文件删除
	               // tempFile.delete();
	            } catch (Exception e) {
	                e.printStackTrace();
	            }  */

				/* RequestParams params = new RequestParams();
				 final String contentType = RequestParams.APPLICATION_OCTET_STREAM;*/
            // params.put(key, file, contentType)
            //HttpRestClient.post(url, params, responseHandler)
        }
    }

    //uri 转bitmap
    private Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(Setting.this.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    /**
     * 根据uri获取图片地址
     *
     * @param selectedImage
     */
    private File Uritofile(Uri selectedImage) {
        File file = null;
        Cursor cursor = Setting.this.getContentResolver().query(selectedImage, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex("_data");
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
                Toast toast = Toast.makeText(Setting.this, "找不到图片", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return null;
            }
            file = new File(picturePath);
            //sendPicture(picturePath);
        } else {
            file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(Setting.this, "找不到图片", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return null;
            }

        }

        return file;

    }

    /**
     * 根据图库图片uri获取图片
     *
     * @param selectedImage
     */
    private void sendPicByUri(Uri selectedImage) {
        // String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = Setting.this.getContentResolver().query(selectedImage, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex("_data");
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
                Toast toast = Toast.makeText(Setting.this, "找不到图片", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            //copyFile(picturePath,imageUri.getPath());
            cropImageUri(selectedImage, 200, 200, USERPIC_REQUEST_CODE_CUT);
            //sendPicture(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(Setting.this, "找不到图片", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;

            }
            //copyFile(selectedImage.getPath(),imageUri.getPath());
            cropImageUri(selectedImage, 200, 200, USERPIC_REQUEST_CODE_CUT);
            //sendPicture(file.getAbsolutePath());
        }

    }

    /**
     * 根据图库图片uri获取图片
     *
     * @param selectedImage
     */
    private String getpathfromUri(Uri selectedImage) {
        // String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = Setting.this.getContentResolver().query(selectedImage, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex("_data");
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
                Toast toast = Toast.makeText(Setting.this, "找不到图片", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return null;
            }
            return picturePath;
            //sendPicture(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(Setting.this, "找不到图片", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return null;

            }
            return file.getAbsolutePath();
        }

    }

    /**
     * 裁剪图片
     *
     * @param uri
     * @param outputX
     * @param outputY
     * @param requestCode
     */
    private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode) {

		 /*Intent intent = new Intent("com.android.camera.action.CROP");
		 intent.setDataAndType(uri, "image/*");
		 intent.putExtra("crop", "true");
		 //aspectX aspectY宽高比例
		 intent.putExtra("aspectX", 1);
		 intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		 intent.putExtra("outputX", outputX);
		 intent.putExtra("outputY", outputY);
		 intent.putExtra("scale", true);
		 intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		 intent.putExtra("return-data", false);
		 intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		 intent.putExtra("noFaceDetection", true); // no face detection
		 startActivityForResult(intent, requestCode);*/


        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, requestCode);
    }

    /**
     * 保存Bitmap为文件
     *
     * @param baseBitmap
     */
    public void save(Bitmap baseBitmap) {
        try {
			  /*			  BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(cutFile));
			   baseBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
	           bos.flush();
	           bos.close();*/
            OutputStream stream = new FileOutputStream(Uritofile(imageUri));
            baseBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.close();
		  /*
		   // 模拟一个广播，通知系统sdcard被挂载
		   Intent intent = new Intent();
		   intent.setAction(Intent.ACTION_MEDIA_MOUNTED);
		   intent.setData(Uri.fromFile(Environment
		     .getExternalStorageDirectory()));
		   sendBroadcast(intent);*/
            Toast.makeText(Setting.this, "保存图片成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(Setting.this, "保存图片失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * bitmap 转 file
     * 保存图片为JPEG
     *
     * @param bitmap
     * @param cameraFile2
     */
    public File saveJPGE_After(Bitmap bitmap, File cameraFile2) {
        //File file = new File(cameraFile2);
        try {
            FileOutputStream out = new FileOutputStream(cameraFile2);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cameraFile2;
    }


    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    //Log.d("log",bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            Log.d("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

}

