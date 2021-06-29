package com.youzhi.chatdemo.adapter;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by 高玉恒 on 2017/4/10 0010.
 * 仅供RecyclerView使用的Holder
 */

public abstract class RecyclerBaseHolder<D> extends RecyclerView.ViewHolder {
    protected Context mContext;
    protected RecyclerView.Adapter mAdapter;
    protected D mData;


    public RecyclerBaseHolder(View itemView, Context context, RecyclerView.Adapter adapter) {
        super(itemView);

        this.mContext = context;
        this.mAdapter = adapter;
//        // 通过当前类的泛型父类（BasePresenter）来获取泛型子类（不确定类型）的实例  TODO 注意 如果在继承BaseActivity时没有给定泛型，这里返回的是null
//        mPresenter = TUtil.getT(this, 0);
//        //通过当前类的泛型父类（BaseModel）来获取泛型的实例
//        mModel = TUtil.getT(this, 1);
        this.initPresenter();
    }

    protected void initPresenter() {

    }

    public void setData(D data) {
        this.mData = data;
        this.itemView.setTag(data);
    }

    public abstract void bindHolder(int position);


    public D getData() {

        return this.mData;
    }
}
