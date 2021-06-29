package com.youzhi.chatdemo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.youzhi.chatdemo.bean.BaseBean;
import com.youzhi.chatdemo.constant.AppConst;
import com.youzhi.chatdemo.utils.FastJsonUtils;
import com.youzhi.chatdemo.utils.MyOkGo;
import com.youzhi.chatdemo.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements MyOkGo.NetResultCallback {

    @BindView(R.id.login_btn)
    Button loginBtn;
    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_code)
    EditText etCode;

    @Override
    protected int getLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
//        etCode.setText("m53vyg4j");
    }


    @OnClick(R.id.login_btn)
    public void onViewClicked() {
        login();


    }

    private void login() {
        String account = etAccount.getText().toString();
        if (TextUtils.isEmpty(account)) {
            ToastUtil.showShort("请输入账号");
            return;
        }
        String code = etCode.getText().toString();
        if (TextUtils.isEmpty(code)) {
            ToastUtil.showShort("请输入激活码");
            return;
        }
        Map<String, String> stringStringMap = new HashMap<>();
        stringStringMap.put("siteId", account);
        stringStringMap.put("companyId", code);
        MyOkGo.send(MyOkGo.getPostRequest(AppConst.LOGIN_CHECK, this)
                        .upJson(FastJsonUtils.toJSONString(stringStringMap))
                , this, new BaseBean());
    }

    @Override
    public void onSuccess(BaseBean baseBean) {
        MyApplication.userSP.saveUid(baseBean.getmUserId());
        MyApplication.userSP.saveAccount(etAccount.getText().toString());
        MyApplication.userSP.saveCode(etCode.getText().toString());
        MyApplication.userSP.saveIsLogin(true);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onError(int status, String message, String url) {

    }


}
