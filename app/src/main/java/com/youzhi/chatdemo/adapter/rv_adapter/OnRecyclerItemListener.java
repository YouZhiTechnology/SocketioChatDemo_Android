package com.youzhi.chatdemo.adapter.rv_adapter;

import android.view.View;

import com.youzhi.chatdemo.adapter.RecyclerBaseAdapter;


/**
 * Created by 高玉恒 on 2017/4/10 0010.
 */

public interface OnRecyclerItemListener<T> {

    public void onItemClick(RecyclerBaseAdapter recyclerBaseAdapter, View v, T data);


}
