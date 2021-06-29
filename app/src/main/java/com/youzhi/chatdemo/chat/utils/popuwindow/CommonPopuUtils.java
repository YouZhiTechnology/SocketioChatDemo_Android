package com.youzhi.chatdemo.chat.utils.popuwindow;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.RecyclerView;

import com.youzhi.chatdemo.MyApplication;
import com.youzhi.chatdemo.R;


public class CommonPopuUtils {


    @SuppressLint("WrongConstant")
    public static CommonPopupWindow showPopForViewOnItem(Activity context, int layoutId, View showAtView, CommonPopupWindow.ViewInterface listener) {

        CommonPopupWindow popupWindow = new CommonPopupWindow.Builder(context)
                .setView(layoutId)
                .setWidthAndHeight(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//                .setAnimationStyle(R.style.popup_top_anim)
                .setViewOnclickListener(listener)
                .setBackGroundLevel(1)
                .setOutsideTouchable(true)
                .create();
        popupWindow.setFocusable(true);
//        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        /**
         * 先调用下measure,否则拿不到宽和高
         */
        popupWindow.getContentView().measure(0, 0);
        int mPopupWindowHeight = popupWindow.getContentView().getMeasuredHeight();
        int mPopupWindowWidth = popupWindow.getContentView().getMeasuredWidth();
        setHeightWidth();
        Rect location = locateView(showAtView);
        if (location != null) {
            int x;
            //view中心点X坐标
            int xMiddle = location.left + showAtView.getWidth() / 2;
            x = xMiddle - mPopupWindowWidth / 2;//让view得中线点与弹窗得中心点对其
            int y;
            //view中心点Y坐标
            int yMiddle = location.top + showAtView.getHeight() / 2;
            if (yMiddle > mDeviceHeight / 2) {
                //在上面
                y = yMiddle - showAtView.getHeight() / 2 - mPopupWindowHeight - 10;
            } else {
                //在下面
                y = yMiddle + showAtView.getHeight() / 2 + 10;
            }
            popupWindow.showAtLocation(showAtView, Gravity.NO_GRAVITY, x, y);
        }
        return popupWindow;


    }


    private static Rect locateView(View v) {
        if (v == null) return null;
        int[] loc_int = new int[2];
        try {
            v.getLocationOnScreen(loc_int);
        } catch (NullPointerException npe) {
            //Happens when the view doesn't exist on screen anymore.
            return null;
        }
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1];
        location.right = location.left + v.getWidth();
        location.bottom = location.top + v.getHeight();
        return location;
    }


    private static int mDeviceWidth, mDeviceHeight;

    @SuppressLint("WrongConstant")
    public static CommonPopupWindow showPop(Activity context, int height, int layoutId, int gravity, CommonPopupWindow.ViewInterface listener) {


        return showPopForBackground(context, true, 0.5f, height, layoutId, gravity, listener);
    }

    @SuppressLint("WrongConstant")
    public static CommonPopupWindow showPopOutsideFalse(Activity context, int height, int layoutId, int gravity, CommonPopupWindow.ViewInterface listener) {

        return showPopForBackground(context, false, 0.5f, height, layoutId, gravity, listener);
    }

    @SuppressLint("WrongConstant")
    public static CommonPopupWindow showPopForBackground(Activity context, int height, int layoutId, int gravity, CommonPopupWindow.ViewInterface listener) {


        return showPopForBackground(context, true, 0.5f, height, layoutId, gravity, listener);
    }

    @SuppressLint("WrongConstant")
    public static CommonPopupWindow showPopForTransparent(Activity context, boolean isTouchable, int height, int layoutId, int gravity, CommonPopupWindow.ViewInterface listener) {


        return showPopForBackground(context, true, 1, height, layoutId, gravity, listener);
    }

    @SuppressLint("WrongConstant")
    public static CommonPopupWindow showPopForBackground(Activity context, boolean isTouchable, float backGroundLevel, int height, int layoutId, int gravity, CommonPopupWindow.ViewInterface listener) {

        CommonPopupWindow popupWindow = new CommonPopupWindow.Builder(context)
                .setView(layoutId)
                .setWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT, height)
                .setAnimationStyle(R.style.popup_bottom_anim)
                .setViewOnclickListener(listener)
                .setBackGroundLevel(backGroundLevel)
                .setOutsideTouchable(isTouchable)
                .create();
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        popupWindow.showAtLocation(context.getWindow().getDecorView().findViewById(android.R.id.content), gravity, 0, 0);


//        popupWindow.showAsDropDown(view);
//        popupWindow.setOnDismissListener(this);
        return popupWindow;
    }

    public static void setHeightWidth() {
        WindowManager wm = (WindowManager) MyApplication.getContext().getSystemService(Context.WINDOW_SERVICE);
        //API 13才允许使用新方法
        Point outSize = new Point();
        wm.getDefaultDisplay().getSize(outSize);
        if (outSize.x != 0) {
            mDeviceWidth = outSize.x;
        }
        if (outSize.y != 0) {
            mDeviceHeight = outSize.y;
        }
    }
}
