package com.youzhi.chatdemo;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.lzy.okgo.OkGo;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.youzhi.chatdemo.utils.QMUIUtils;
import com.youzhi.chatdemo.utils.statusbar.StatusBarUtils;


import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    private Bundle mSavedInstanceState;
    private QMUITipDialog qmuiTipDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mSavedInstanceState = savedInstanceState;
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        super.onCreate(savedInstanceState);
        //禁止屏幕旋转
//        if (!(this instanceof LiveActivity)){
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }

        //状态栏透明化: 侵入式透明status bar
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window window = getWindow();
//            // Translucent status bar
//            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager
//                    .LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
        if (isNeedImmerse()) {
            setStatusBarColor(R.color.colorNull, true);
        }
        setContentView(getLayout());
        //绑定ButterKnife
        ButterKnife.bind(this);
        getLayout();
        initView();
        initData();
        ActivitysManager.getInstance().addActivity(this);
    }
    /*是否需要沉浸*/
    protected boolean isNeedImmerse() {
        return true;
    }

    /**
     * 加载提示Dialog
     *
     * @param resId
     */
    protected void showLoadingView(@StringRes int resId, int showType) {
        try {
            if (qmuiTipDialog == null || !qmuiTipDialog.isShowing()) {
                qmuiTipDialog = QMUIUtils.showDialog(this, getString(resId), showType);
            }
        } catch (Exception e) {

        }

    }

    protected void dismissLoadingView() {
        if (qmuiTipDialog != null && qmuiTipDialog.isShowing()) {
            qmuiTipDialog.dismiss();
            qmuiTipDialog = null;
        }
    }

    /**
     * 添加Fragment
     *
     * @param layoutId
     * @param fragment
     * @param tag      Fragment的标签
     */
    protected void addFragment(int layoutId, Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction().add(layoutId, fragment, tag).commitAllowingStateLoss();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    protected void addFragment(int layoutId, Fragment fragment, String tag, Bundle bundle) {
        if (fragment != null) {

            fragment.setArguments(bundle);

        }
        addFragment(layoutId, fragment, tag);
    }

    protected Bundle getSavedInstanceState() {
        return mSavedInstanceState;
    }

    /**
     * @param colorId          状态栏颜色
     * @param isLight          状态栏文本颜色白或者黑
     * @param fitSystemWindows 如果此值设置我true表示会自动让出状态栏高度
     */
    public void setStatusBarColorWithFitSystem(@ColorRes int colorId, boolean isLight, boolean fitSystemWindows) {
        if (fitSystemWindows) {

            StatusBarUtils.setRootViewFitsSystemWindows(this, true);
        }
        StatusBarUtils.setStatusBar(this, colorId, isLight);
    }

    public void setStatusBarColor(@ColorRes int colorId, boolean isLight) {
        setStatusBarColorWithFitSystem(colorId, isLight, false);
    }

    /*
     获取布局
     */
    protected abstract int getLayout();

    /*
    初始化控件
     */
    protected void initView() {
    }

    /*
    初始化数据
     */
    protected void initData() {
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        OkGo.getInstance().cancelTag(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivitysManager.getInstance().removeActivity(this);
    }
}
