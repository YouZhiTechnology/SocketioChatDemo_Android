package com.youzhi.chatdemo;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;



import java.util.List;
import java.util.Stack;

/*
 *  全局Activity的管理类
 * */
public class ActivitysManager {
    private static Stack<Activity> activityStack;
    private static volatile ActivitysManager instance;


    public boolean hasActivity() {
        if (activityStack == null || activityStack.size() <= 0) {
            return false;
        } else {
            return true;
        }

    }

    public static ActivitysManager getInstance() {
        if (instance == null) {
            instance = new ActivitysManager();
        }
        return instance;
    }

    public void startActivity(Class<?> cls) {
        Intent intent = new Intent(MyApplication.getContext(), cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.getContext().startActivity(intent);
    }

    public void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(MyApplication.getContext(), cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.getContext().startActivity(intent);
    }

    public Activity isInStack(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                return activity;
            }
        }
        return null;
    }

    private Activity isInStack(String actName) {
        for (Activity activity : activityStack) {
            if (activity.getClass().getSimpleName().equals(actName)) {
                return activity;
            }
        }
        return null;
    }

    public void addActivity(Activity act) {
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        activityStack.add(act);
    }

    public Activity currentActivity() {
        Activity activity = null;
        if (activityStack != null) {
            try {
                activity = activityStack.lastElement();
            } catch (Exception e) {

            }

        }

        return activity;
    }

    public void removeActivity(Activity act) {
        if (null != isInStack(act.getClass())) {
            activityStack.remove(act);
        }
    }

    public void finishActivity(String actName) {
        Activity activity = isInStack(actName);
        if (null != activity) {
            activity.finish();
        }
    }

    public void finishAllActivity() {
        for (int i = 0; i < activityStack.size(); i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

//    public void afinishLoginOtherAllActivity() {
//        for (int i = 0; i < activityStack.size(); i++) {
//            if (null != activityStack.get(i) && !(activityStack.get(i) instanceof LoginActivity)) {
//                activityStack.get(i).finish();
//            }
//        }
//        activityStack.clear();
//    }

    //MainActivity之外出栈
    public void finishOtherActivity() {
        for (Activity activity : activityStack) {
            if (!activity.getClass().getSimpleName().equals("MainActivity")) {
                activity.finish();
            }
        }
    }

    /**
     * 退出应用.
     */
    public void appExit() {
        finishAllActivity();
        System.exit(0);
    }

    public Fragment getCurrentFragment(FragmentActivity fragmentActivity) {
        FragmentManager supportFragmentManager = fragmentActivity.getSupportFragmentManager();
        List<Fragment> fragments = supportFragmentManager.getFragments();
        if (fragments == null || fragments.size() <= 0) {
            return null;
        }
        for (int i = 0; i < fragments.size(); i++) {
            Fragment fragment = fragments.get(i);
            if (fragment.isVisible()) {
                return fragment;
            }
        }
        return null;
    }
}
