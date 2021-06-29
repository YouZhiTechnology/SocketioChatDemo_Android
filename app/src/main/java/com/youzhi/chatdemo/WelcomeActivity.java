package com.youzhi.chatdemo;

import android.content.Intent;

public class WelcomeActivity extends BaseActivity {
    @Override
    protected int getLayout() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void initView() {
        super.initView();
        Intent intent = new Intent();

        if (MyApplication.userSP.readIsLogin()) {
            intent.setClass(this, MainActivity.class);
        } else {
            intent.setClass(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
