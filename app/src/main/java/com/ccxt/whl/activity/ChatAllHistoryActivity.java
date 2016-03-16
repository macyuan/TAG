package com.ccxt.whl.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ccxt.whl.DemoApplication;
import com.ccxt.whl.R;
import com.ccxt.whl.adapter.ChatAllHistoryAdapter;
import com.ccxt.whl.db.InviteMessgeDao;
import com.ccxt.whl.domain.User;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContact;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/11/12.
 */
public class ChatAllHistoryActivity extends Activity {

    private InputMethodManager inputMethodManager;
    private ListView listView;
    private Map<String, User> contactList;
    private ChatAllHistoryAdapter adapter;
    private EditText query;
    private ImageButton clearSearch;
    public RelativeLayout errorItem;
    public TextView errorText;
    private boolean hidden;
    private List<EMGroup> groups;
    public Context mContext = ChatAllHistoryActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_conversation_history);
        init();
    }


    public void init() {
        inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        errorItem = (RelativeLayout) findViewById(R.id.rl_error_item);
        errorText = (TextView) errorItem.findViewById(R.id.tv_connect_errormsg);
        // contact list
        contactList = DemoApplication.getInstance().getContactList();
        listView = (ListView) findViewById(R.id.list);
        adapter = new ChatAllHistoryAdapter(mContext, 1, loadConversationsWithRecentChat());

        groups = EMGroupManager.getInstance().getAllGroups();

        // 设置adapter
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMConversation conversation = adapter.getItem(position);
                String username = conversation.getUserName();
                if (username.equals(DemoApplication.getInstance().getUser()))
                    Toast.makeText(mContext, "不能和自己聊天", 0).show();
                else {
                    // 进入聊天页面
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    EMContact emContact = null;
                    groups = EMGroupManager.getInstance().getAllGroups();
                    for (EMGroup group : groups) {
                        if (group.getGroupId().equals(username)) {
                            emContact = group;
                            break;
                        }
                    }
                    if (emContact != null && emContact instanceof EMGroup) {
                        // it is group chat
                        intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                        intent.putExtra("groupId", ((EMGroup) emContact).getGroupId());
                    } else {
                        // it is single chat
                        intent.putExtra("userId", username);
                    }
                    startActivity(intent);
                }
            }
        });
        // 注册上下文菜单
        registerForContextMenu(listView);

        listView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 隐藏软键盘
                if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
                    if (getCurrentFocus() != null)
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });
        // 搜索框
        query = (EditText) findViewById(R.id.query);
        // 搜索框中清除button
        clearSearch = (ImageButton) findViewById(R.id.search_clear);
        query.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                adapter.getFilter().filter(s);
                if (s.length() > 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                } else {
                    clearSearch.setVisibility(View.INVISIBLE);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query.getText().clear();

            }
        });
    }

    /**
     * 获取所有会话
     *
     * @param context
     * @return
     */
    private List<EMConversation> loadConversationsWithRecentChat() {
        // 获取所有会话，包括陌生人
        Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
        List<EMConversation> conversationList = new ArrayList<EMConversation>();
        //过滤掉messages seize为0的conversation
        for (EMConversation conversation : conversations.values()) {
            if (conversation.getAllMessages().size() != 0)
                conversationList.add(conversation);
        }
        // 排序
        sortConversationByLastChatTime(conversationList);
        return conversationList;
    }

    /**
     * 根据最后一条消息的时间排序
     *
     * @param usernames
     */
    private void sortConversationByLastChatTime(List<EMConversation> conversationList) {
        Collections.sort(conversationList, new Comparator<EMConversation>() {
            @Override
            public int compare(final EMConversation con1, final EMConversation con2) {

                EMMessage con2LastMessage = con2.getLastMessage();
                EMMessage con1LastMessage = con1.getLastMessage();
                if (con2LastMessage.getMsgTime() == con1LastMessage.getMsgTime()) {
                    return 0;
                } else if (con2LastMessage.getMsgTime() > con1LastMessage.getMsgTime()) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // if(((AdapterContextMenuInfo)menuInfo).position > 0){ m,
        getMenuInflater().inflate(R.menu.delete_message, menu);
        // }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_message) {
            EMConversation tobeDeleteCons = adapter.getItem(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position);
            // 删除此会话
            EMChatManager.getInstance().deleteConversation(tobeDeleteCons.getUserName(), tobeDeleteCons.isGroup());
            InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(mContext);
            inviteMessgeDao.deleteMessage(tobeDeleteCons.getUserName());
            adapter.remove(tobeDeleteCons);
            adapter.notifyDataSetChanged();

            // 更新消息未读数
            // ((MainActivity) getActivity()).updateUnreadLabel();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden) {
            refresh();
        }
    }

    /**
     * 刷新页面
     */
    public void refresh() {
        adapter = new ChatAllHistoryAdapter(mContext, R.layout.row_chat_history, loadConversationsWithRecentChat());
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
