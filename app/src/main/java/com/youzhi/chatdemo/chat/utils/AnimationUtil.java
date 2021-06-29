package com.youzhi.chatdemo.chat.utils;

import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author gaoyuheng
 * @description:
 * @date : 5/20/21 5:11 PM.
 */
public class AnimationUtil implements ValueAnimator.AnimatorUpdateListener {

    private View targetView;
    private int endHeight;
    private static int STATUS_LEISURE = 0;//空闲
    private static int STATUS_STARTING = 1;//动画播放状态
    private RecyclerView mRecycler;
    private int ANIMATION_STATUS = STATUS_LEISURE;

    public synchronized static AnimationUtil getInstance() {
        return new AnimationUtil();
    }

    private AnimationUtil() {

    }

    /**
     * 动态改变view的高度动画效果，动画时长300毫秒[android属性动画默认时长]
     * 原理:动画改变view LayoutParams.height的值
     *
     * @param view      要进行高度改变动画的view
     * @param endHeight 动画后的view的高度
     */
    public void changeViewHeightAnimatorStart(final View view, final int startHeight, final int endHeight) {
        if (view != null && startHeight >= 0 && endHeight >= 0 && ANIMATION_STATUS != STATUS_STARTING) {
            targetView = view;
            this.endHeight = startHeight;
            ValueAnimator animator = ValueAnimator.ofInt(startHeight, endHeight);
            animator.addUpdateListener(this);
            animator.start();
            animator.setDuration(200);
            ANIMATION_STATUS = STATUS_STARTING;
        }
    }

    public void bindRecyclerViewLinkage(RecyclerView recyclerView) {
        mRecycler = recyclerView;

    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        ViewGroup.LayoutParams params = targetView.getLayoutParams();
        params.height = (int) animation.getAnimatedValue();
//        Log.e("LOGCAT", "动画高度==" + (int) animation.getAnimatedValue());
        targetView.setLayoutParams(params);
        //这里是让列表也随着动画进行滚动操作，实现联动
        if (mRecycler != null) {
            mRecycler.post(new Runnable() {
                @Override
                public void run() {
                    RecyclerView.LayoutManager layoutManager = mRecycler.getLayoutManager();
                    if (layoutManager instanceof LinearLayoutManager) {
                        ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(mRecycler.getLayoutManager().getItemCount() - 1, Integer.MIN_VALUE);
                    }

                }
            });
        }
        //到达结束值表示动画结束

        if (endHeight == params.height) {
            ANIMATION_STATUS = STATUS_LEISURE;
        }

    }


}
