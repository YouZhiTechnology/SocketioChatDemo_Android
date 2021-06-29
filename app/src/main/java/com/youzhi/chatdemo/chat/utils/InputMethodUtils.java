package com.youzhi.chatdemo.chat.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;




public class InputMethodUtils {


    /**
     * 隐藏输入法
     *
     * @param currentFocusView 当前焦点view
     */
    public static void hideKeyboard(View currentFocusView) {
        if (currentFocusView != null) {
            IBinder token = currentFocusView.getWindowToken();
            if (token != null) {
                InputMethodManager im = (InputMethodManager) currentFocusView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(token, 0);
            }
        }
    }

    /**
     * 开关输入法
     *
     * @param currentFocusView 当前焦点view
     */
    public static void toggleSoftInput(View currentFocusView) {
        InputMethodManager imm = (InputMethodManager) currentFocusView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(currentFocusView, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }



    public static void updateSoftInputMethod(Activity activity, int softInputMode) {
        if (!activity.isFinishing()) {
            WindowManager.LayoutParams params = activity.getWindow().getAttributes();
            if (params.softInputMode != softInputMode) {
                params.softInputMode = softInputMode;
                activity.getWindow().setAttributes(params);
            }
        }
    }
}
