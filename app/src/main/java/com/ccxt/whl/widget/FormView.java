package com.ccxt.whl.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.ccxt.whl.R;


/**
 * Created by kobe.gong on 2015/7/17.
 */
public class FormView extends LinearLayout {

    private EditText edit1, edit2;

    public FormView(Context context) {
        super(context);
        loadView();
    }

    public FormView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadView();
    }

    public FormView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadView();
    }

    private void loadView(){
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.form_view, this);
        edit1 = (EditText) findViewById(R.id.username);
        edit2 = (EditText) findViewById(R.id.password);
    }

    @Override
    public void setFocusable(boolean focusable) {
        super.setFocusable(focusable);
        edit1.setFocusable(focusable);
        edit2.setFocusable(focusable);
    }
}
