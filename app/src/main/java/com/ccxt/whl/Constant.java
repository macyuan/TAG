/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ccxt.whl;

import android.os.Environment;

public class Constant {
	public static final String CACHE_DIR = "";
	public static final String CACHE_DIR_IMAGE = "/image";
	public static final String NEW_FRIENDS_USERNAME = "item_new_friends";
	public static final String GROUP_USERNAME = "item_groups";
	public static final String KEFU = "kefu";
	public static final String CHAT_ROOM = "item_chatroom";
	public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
	public static final String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";
	public static final String ACCOUNT_REMOVED = "account_removed";
	public static final String CHAT_ROBOT = "item_robots";
	public static final String MESSAGE_ATTR_ROBOT_MSGTYPE = "msgtype";

	public static final String VERSION = "v="+DemoApplication.getInstance().getVersionName()+"&";
	//http连接
	public static final String BASE_URL = "http://115.28.27.144/";

	//社会化登录url
	public static final String S_LOGIN_URL = "login.php?action=s&"+VERSION;
	//普通登录url
	public static final String LOGIN_URL = "login.php?action=p&"+VERSION;
	//普通注册url
	public static final String REGISTER_URL = "register.php?action=r&"+VERSION;
	//普通注册回执url
	public static final String REGISTER_URL_HUIZHI = "register.php?action=h&"+VERSION;
	//更新个人资料url
	public static final String UPDATE_USER_URL = "register.php?action=u&"+VERSION;
	//请求在哪数据
	public static final String ZAINA_URL = "zaina.php?action=n&"+VERSION;
	//请求对话人的头像和昵称
	public static final String USER_URL_C = "userinfo.php?action=c&"+VERSION;
	//根据邮箱请求联系人的唯一id和昵称
	public static final String USER_URL_E = "userinfo.php?action=e&"+VERSION;
	//根据请求人的唯一id请求用户的详细资料
	public static final String USER_URL_I = "userinfo.php?action=i&"+VERSION;
	//发布个人故事
	public static final String GUSHI_PUBLISH = "gushi.php?action=p&"+VERSION;
	//请求最新故事
	public static final String GUSHI_NEW = "gushi.php?action=t&"+VERSION;
	//请求个人故事
	public static final String GUSHI_USER = "gushi.php?action=u&"+VERSION;
	//删除个人故事
	public static final String GUSHI_DEL = "gushi.php?action=d&"+VERSION;
	//拉黑举报接口
    public static final String USER_BLACK = "userinfo.php?action=d&"+VERSION;
  //获取更新信息接口
    public static final String CHECK_UPDATE = "update.php?action=c&"+VERSION;

	public static final String APP_DIR                    = Environment.getExternalStorageDirectory() + "/StickerCamera";
	public static final String APP_TEMP                   = APP_DIR + "/temp";
	public static final String APP_IMAGE                  = APP_DIR + "/image";

	public static final int    POST_TYPE_POI              = 1;
	public static final int    POST_TYPE_TAG              = 0;
	public static final int    POST_TYPE_DEFAULT		  = 0;


	public static final float  DEFAULT_PIXEL              = 1242;                           //按iphone6设置
	public static final String PARAM_MAX_SIZE             = "PARAM_MAX_SIZE";
	public static final String PARAM_EDIT_TEXT            = "PARAM_EDIT_TEXT";
	public static final int    ACTION_EDIT_LABEL          = 8080;
	public static final int    ACTION_EDIT_LABEL_POI      = 9090;

	public static final String FEED_INFO                  = "FEED_INFO";


	public static final int REQUEST_CROP = 6709;
	public static final int REQUEST_PICK = 9162;
	public static final int RESULT_ERROR = 404;
}
