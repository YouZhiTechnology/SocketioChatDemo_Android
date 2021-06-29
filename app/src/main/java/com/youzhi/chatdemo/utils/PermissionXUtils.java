package com.youzhi.chatdemo.utils;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.ForwardToSettingsCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;
import com.permissionx.guolindev.request.ForwardScope;
import com.youzhi.chatdemo.R;


import java.util.List;

/**
 * @author gaoyuheng
 * @description:
 * @date :2020/8/31 11:49
 */
public class PermissionXUtils {


    private static final int REQUEST_CODE_WRITE_SETTINGS = 0x1666;

    public static void request(FragmentActivity activity, RequestCallback requestCallback, String... permissions) {
        PermissionX.init(activity).permissions(permissions).onExplainRequestReason(new ExplainReasonCallback() {
            @Override
            public void onExplainReason(ExplainScope scope, List<String> deniedList) {

                if (deniedList != null && deniedList.size() > 0) {
                    String message = "即将申请的权限是程序必须依赖的权限";
                    String currentPermission = deniedList.get(0);
                    if (Manifest.permission.SEND_SMS.equals(currentPermission)) {
                        message = "您需要去应用程序设置当中手动开启发送短息权限";
                    } else if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(currentPermission) || Manifest.permission.READ_EXTERNAL_STORAGE.equals(currentPermission)) {
                        message = "保存图片，头像上传等功能需要您开启读写手机存储权限";
                    } else if (Manifest.permission.CALL_PHONE.equals(currentPermission)) {
                        message = "您需要去应用程序设置当中手动开启打电话权限";
                    } else if (Manifest.permission.RECORD_AUDIO.equals(currentPermission)) {
                        message = "您需要去应用程序设置当中手动开启录音权限";
                    } else if (Manifest.permission.CAMERA.equals(currentPermission)) {
                        message = "您需要去应用程序设置当中手动开启相机权限";
                    }
                    openAppDetails(activity, message);
                }
//                scope.showRequestReasonDialog(deniedList, "即将申请的权限是程序必须依赖的权限", "确定", "取消");
//                activity.shouldShowRequestPermissionRationale();

            }
        }).onForwardToSettings(new ForwardToSettingsCallback() {
            @Override
            public void onForwardToSettings(ForwardScope scope, List<String> deniedList) {
//                scope.showForwardToSettingsDialog(deniedList, "您需要去应用程序设置当中手动开启权限", "确定", "取消");
                if (deniedList != null && deniedList.size() > 0) {
                    String message = "您需要去应用程序设置当中手动开启权限";
                    String currentPermission = deniedList.get(0);
                    if (Manifest.permission.SEND_SMS.equals(currentPermission)) {
                        message = "您需要去应用程序设置当中手动开启发送短息权限";
                    } else if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(currentPermission) || Manifest.permission.READ_EXTERNAL_STORAGE.equals(currentPermission)) {
                        message = "您需要去应用程序设置当中手动开启读写手机存储权限";
                    } else if (Manifest.permission.CALL_PHONE.equals(currentPermission)) {
                        message = "您需要去应用程序设置当中手动开启打电话权限";
                    } else if (Manifest.permission.RECORD_AUDIO.equals(currentPermission)) {
                        message = "您需要去应用程序设置当中手动开启录音权限";
                    } else if (Manifest.permission.CAMERA.equals(currentPermission)) {
                        message = "您需要去应用程序设置当中手动开启相机权限";
                    }
                    openAppDetails(activity, message);
                }


            }
        }).request(requestCallback);
    }

    /**
     * 申请权限
     */
    private void requestWriteSettings(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //大于等于23 请求权限
            if (!Settings.System.canWrite(activity.getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);
            }
        } else {
            //小于23直接设置
        }
    }

    /**
     * 打开 APP 的详情设置
     */
    private static void openAppDetails(Activity activity, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.myDialogStyle);
        builder.setMessage(message);

        builder.setPositiveButton("去手动授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                activity.startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    public static void request(FragmentActivity activity, RequestCallback requestCallback, boolean isShowDialog, String... permissions) {
        PermissionX.init(activity).permissions(permissions).onExplainRequestReason(new ExplainReasonCallback() {
            @Override
            public void onExplainReason(ExplainScope scope, List<String> deniedList) {
                if (isShowDialog) {
                    scope.showRequestReasonDialog(deniedList, "即将申请的权限是程序必须依赖的权限", "确定", "取消");

                }

            }
        }).onForwardToSettings(new ForwardToSettingsCallback() {
            @Override
            public void onForwardToSettings(ForwardScope scope, List<String> deniedList) {
                if (isShowDialog) {
                    scope.showForwardToSettingsDialog(deniedList, "您需要去应用程序设置当中手动开启权限", "确定", "取消");
                }
            }
        }).request(requestCallback);
    }


}
