package com.youzhi.chatdemo.utils;

import android.content.Context;
import android.content.res.ColorStateList;

import androidx.annotation.ColorRes;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;

/**
 * gaoyuheng
 * Qmui工具类
 */
public class QMUIUtils {
    /**
     * 不显示任何icon
     */
    public static final int ICON_TYPE_NOTHING = 0;
    /**
     * 显示 Loading 图标
     */
    public static final int ICON_TYPE_LOADING = 1;
    /**
     * 显示成功图标
     */
    public static final int ICON_TYPE_SUCCESS = 2;
    /**
     * 显示失败图标
     */
    public static final int ICON_TYPE_FAIL = 3;
    /**
     * 显示信息图标
     */
    public static final int ICON_TYPE_INFO = 4;

    public static QMUITipDialog showDialog(Context context, String text, int iconType) {

        QMUITipDialog qmuiTipDialog = new QMUITipDialog.Builder(context).setTipWord(text).setIconType(iconType).create();
        qmuiTipDialog.show();
        return qmuiTipDialog;

    }
    /*设置圆角button背景如果直接setBackgroundColor圆角会失效*/
    public static void setBgColorForQMUIRB(QMUIRoundButton qmuiRoundButton, @ColorRes int colorId) {
        QMUIRoundButtonDrawable qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable) qmuiRoundButton.getBackground();
        ColorStateList colorStateList = ColorStateList.valueOf(qmuiRoundButton.getResources().getColor(colorId));
        qmuiRoundButtonDrawable.setBgData(colorStateList);
    }
}
