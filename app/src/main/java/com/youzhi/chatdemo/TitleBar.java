package com.youzhi.chatdemo;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;


import butterknife.BindView;
import butterknife.ButterKnife;

public class TitleBar extends LinearLayout {

    @BindView(R.id.iv_title_left)
    ImageView iv_left;
    @BindView(R.id.iv_title_right)
    ImageView iv_right;
    @BindView(R.id.tv_title_content)
    TextView tv_contet;
    @BindView(R.id.tv_title_right)
    TextView tv_title_right;
    @BindView(R.id.v_status_bar)
    View v_status_bar;

    private final TypedArray typedArray;
    private String content;
    private String contentRight;
    private Boolean left_visible;
    private Boolean right_visible;
    private Boolean right_tv_visible;
    private int left_icon;
    private int right_icon;
    private Context context;

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.TitleBar);
        LayoutInflater.from(context).inflate(R.layout.layout_title, this);
        ButterKnife.bind(this);
        initAttrs();
        initView();
    }

    private void initAttrs() {
        content = typedArray.getString(R.styleable.TitleBar_title_content);
        contentRight = typedArray.getString(R.styleable.TitleBar_right_content);
        left_visible = typedArray.getBoolean(R.styleable.TitleBar_left_visible, false);
        right_visible = typedArray.getBoolean(R.styleable.TitleBar_right_visible, false);
        right_tv_visible = typedArray.getBoolean(R.styleable.TitleBar_right_tv_visible, false);
        left_icon = typedArray.getResourceId(R.styleable.TitleBar_left_icon, R.mipmap.ic_back);
        right_icon = typedArray.getResourceId(R.styleable.TitleBar_right_icon, R.mipmap.ic_back);

    }

    public void setRightIconColor(@ColorRes int colorId) {
        iv_right.setColorFilter(getResources().getColor(colorId));
    }

    public void setRightIconId(@DrawableRes int imageResId) {
        iv_right.setImageResource(imageResId);
    }

    public ImageView getRightImage() {
        return iv_right;
    }

    public void setNotStatusBar(){
        v_status_bar.setVisibility(GONE);
    }
    private void initView() {
        tv_contet.setText(content);
        tv_title_right.setText(contentRight);
        if (left_visible)
            iv_left.setVisibility(VISIBLE);
        else
            iv_left.setVisibility(INVISIBLE);

        if (right_visible)
            iv_right.setVisibility(VISIBLE);
        else
            iv_right.setVisibility(INVISIBLE);

        if (right_tv_visible)
            tv_title_right.setVisibility(VISIBLE);
        else
            tv_title_right.setVisibility(INVISIBLE);

        iv_left.setImageResource(left_icon);
        iv_right.setImageResource(right_icon);

        iv_left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) context).finish();
            }
        });
    }

    /*
    左边点击事件
     */
    public void setLeftOnClickListener(OnClickListener leftOnClickListener) {
        iv_left.setOnClickListener(leftOnClickListener);
    }

    /*
    右边点击事件
     */
    public void setRightOnClickListener(OnClickListener rightOnClickListener) {
        iv_right.setOnClickListener(rightOnClickListener);
    }

    /*
    右边文字点击事件
     */
    public void setRightTvOnClickListener(OnClickListener rightOnClickListener) {
        tv_title_right.setOnClickListener(rightOnClickListener);
    }

    /*
    右边文字显示隐藏
     */
    public void setRightVisibility(boolean isBoolean) {
        if (isBoolean)
            tv_title_right.setVisibility(VISIBLE);
        else
            tv_title_right.setVisibility(GONE);
    }
    /*
    右边图标显示隐藏
     */
    public void setRightIconVisibility(boolean isBoolean) {
        if (isBoolean)
            iv_right.setVisibility(VISIBLE);
        else
            iv_right.setVisibility(GONE);
    }
    /*
    右边文字颜色
     */
    public void setRightColor(int color) {
        tv_title_right.setTextColor(color);
    }

    /*
    右边文字内容
     */
    public void setRightContent(String content) {
        tv_title_right.setText(content);
    }


    /*
    设置头部内容
     */
    public void settitleContent(String content) {
        tv_contet.setText(content);
    }

    /*
    获取头部内容
     */
    public String gettitleContent() {
        return tv_contet.getText().toString();
    }
}
