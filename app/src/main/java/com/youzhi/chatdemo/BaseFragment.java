package com.youzhi.chatdemo;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.lzy.okgo.OkGo;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.youzhi.chatdemo.utils.QMUIUtils;


import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {
    protected Context context;
    protected Unbinder unbinder;
    private View view;
    private QMUITipDialog qmuiTipDialog;
    protected boolean isFirstLoad = true;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public void resetStatus() {
        isFirstLoad = true;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(getLayout(), container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    /**
     * 加载提示Dialog
     *
     * @param resId
     */
    protected void showLoadingView(@StringRes int resId, int showType) {
        if (getActivity() == null) {
            return;
        }
        try {

            if (qmuiTipDialog == null || !qmuiTipDialog.isShowing()) {
                qmuiTipDialog = QMUIUtils.showDialog(getActivity(), getString(resId), showType);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
        isFirstLoad = true;
        if (unbinder != null) {
            unbinder.unbind();
        }

    }

    public abstract int getLayout();

    public abstract void initView(View view);

    public abstract void initData();

    @Override
    public void onStop() {
        super.onStop();
//        OkGo.getInstance().cancelTag(getActivity());

    }


}
