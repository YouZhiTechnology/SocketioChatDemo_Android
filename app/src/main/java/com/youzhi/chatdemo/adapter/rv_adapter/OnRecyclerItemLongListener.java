package com.youzhi.chatdemo.adapter.rv_adapter;

import android.view.View;

import com.youzhi.chatdemo.adapter.RecyclerBaseAdapter;

/**
 * Created by HuiHeZe on 2017/9/25.
 */

public interface OnRecyclerItemLongListener <T>{

     void onItemLongClick(RecyclerBaseAdapter recyclerBaseAdapter,View v, T data);

}
