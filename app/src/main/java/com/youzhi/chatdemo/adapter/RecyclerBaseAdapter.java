package com.youzhi.chatdemo.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.youzhi.chatdemo.adapter.rv_adapter.OnRecyclerItemListener;
import com.youzhi.chatdemo.adapter.rv_adapter.OnRecyclerItemLongListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gyh on 2017/4/10 0010.
 * 仅供RecyclerView使用的Adapter 内部对使用SwipeMenu作为侧滑删除等控件做了兼容
 */

public abstract class RecyclerBaseAdapter<D, H extends RecyclerBaseHolder> extends RecyclerView.Adapter<H> implements IAdapter<H>, View.OnClickListener, View.OnLongClickListener {
    private int width_grid;
    private List<D> datas = new ArrayList<>();
    private OnRecyclerItemListener<D> mOnItemClickListener;
    private OnRecyclerItemLongListener<D> onRecyclerItemLongListener;
    private View VIEW_HEADER;
    private View VIEW_FOOTER;

    private Bundle mBundle;
    private OnHolderNotifyRefreshListener mOnHolderNotifyRefreshListener;
    private boolean isSwipeMenu;

    //Type
    protected int TYPE_HEADER = -1;
    protected int TYPE_FOOTER = -2;
    public OnDeletedListener mOnDeletedListener;

    public OnOperationListener<D> onOperationListener;

    public void addHeaderView(View headerView) {

        if (!haveHeaderView()) {
            //避免出现宽度自适应
            if (headerView.getLayoutParams() == null) {
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                headerView.setLayoutParams(params);
            }

            VIEW_HEADER = headerView;
            notifyItemInserted(0);

        }

    }

    public void addFooterView(View footerView) {

        if (!haveFooterView()) {
            if (footerView.getLayoutParams() == null) {
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                footerView.setLayoutParams(params);
            }
            VIEW_FOOTER = footerView;
            notifyItemInserted(getItemCount() - 1);
        }
    }


    public void setOnOperationListener(OnOperationListener<D> onOperationListener) {
        this.onOperationListener = onOperationListener;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {   // 布局是GridLayoutManager所管理
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) manager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    // 如果是Header、Footer的对象则占据spanCount的位置，否则就只占用1个位置
                    return (isHeaderView(position) || isFooterView(position)) ? gridLayoutManager.getSpanCount() : 1;
                }
            });
        }
    }

    public boolean isSwipeMenu() {
        return isSwipeMenu;
    }

    public boolean haveHeaderView() {
        return VIEW_HEADER != null;
    }

    public boolean haveFooterView() {
        return VIEW_FOOTER != null;
    }

    protected boolean isHeaderView(int position) {
        return haveHeaderView() && position == 0;
    }

    protected boolean isFooterView(int position) {
        return haveFooterView() && position == getItemCount() - 1;
    }

    /**
     * @param swipeMenu 是否有侧滑菜单
     */
    public void setSwipeMenu(boolean swipeMenu) {
        isSwipeMenu = swipeMenu;
    }

    public void setOnHolderNotifyRefreshListener(OnHolderNotifyRefreshListener onHolderNotifyRefreshListener) {
        mOnHolderNotifyRefreshListener = onHolderNotifyRefreshListener;
    }

    public void setOnDeletedListener(OnDeletedListener onDeletedListener) {
        mOnDeletedListener = onDeletedListener;


    }

    public void remove(D elem) {
        datas.remove(elem);
        notifyDataSetChanged();

    }

    public void removeAll() {
        datas.clear();
        notifyDataSetChanged();

    }

    public interface OnHolderNotifyRefreshListener {
        void onHolderNotifyRefresh(Object data);
    }

    public void holderNotifyRefresh(Object data) {
        if (mOnHolderNotifyRefreshListener != null) {
            mOnHolderNotifyRefreshListener.onHolderNotifyRefresh(data);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (onRecyclerItemLongListener != null) {
            onRecyclerItemLongListener.onItemLongClick(this,v, (D) v.getTag());
        }
        return false;
    }

    public interface OnDeletedListener {

        void onDeleted(int id, int position);

    }

    //处理条目里一系列操作返回给住界面得交互监听
    public interface OnOperationListener<D> {

        void onOperation(int operationType, D data);
    }

    @Override
    public H onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_HEADER) {
            return createViewHolder(VIEW_HEADER, parent.getContext(), viewType);
        }
        if (viewType == TYPE_FOOTER) {
            return createViewHolder(VIEW_FOOTER, parent.getContext(), viewType);
        }
        View itemView;
        if (getLayoutId(viewType) > 0) {
            //TODO: 发现使用View.inflater布局显示异常，因此推荐使用LayoutInflater
            itemView =
                    LayoutInflater.from(parent.getContext()).inflate(getLayoutId(viewType), parent, false);

        } else {

            itemView = getLayoutView(parent.getContext());
            if (itemView == null) {
                throw new RuntimeException("您的View都没有，拿什么显示？");
            }
        }
        /*针对侧滑菜单列表条目点击事件冲突的解决方式*/
        if (isSwipeMenu) {
            ViewGroup viewGroup = (ViewGroup) itemView;
            if (viewGroup.getChildCount() > 0) {
                View childAt = viewGroup.getChildAt(0);
                childAt.setOnClickListener(this);
                childAt.setOnLongClickListener(this);
            }
        } else {
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }


        return createViewHolder(itemView, parent.getContext(), viewType);
    }


    public List<D> getDatas() {
        return datas;

    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderView(position)) {
            return TYPE_HEADER;
        } else if (isFooterView(position)) {
            return TYPE_FOOTER;
        } else {
            return super.getItemViewType(position);
        }


    }


    @Override
    public void onBindViewHolder(H holder, int position) {
        if (!isHeaderView(position) && !isFooterView(position)) {
            if (haveHeaderView()) position--;
            holder.setData(datas.get(position));
            holder.bindHolder(position);
        }

    }

    //    @Override
//    public int getItemCount() {
//
//        return datas == null ? 0 : datas.size();
//    }
    @Override
    public int getItemCount() {
        int count = (datas == null ? 0 : datas.size());
        if (VIEW_FOOTER != null) {
            count++;
        }

        if (VIEW_HEADER != null) {
            count++;
        }
        return count;
    }

    public void add(D elem) {
        datas.add(elem);
        onSetData();
        if (haveFooterView()) {
            notifyItemInserted(getItemCount() - 2);
        } else {
            notifyItemInserted(getItemCount() - 1);
        }

//        notifyDataSetChanged();
    }

    public void onSetData() {

    }

    public void addAt(int location, D elem) {
        datas.add(location, elem);
        onSetData();
        if (haveHeaderView()) {
            notifyItemInserted(location + 1);
        } else {
            notifyItemInserted(location);
        }

    }

    public View getLayoutView(Context context) {
        return null;
    }

    public final void addDatas(List<D> datas) {
        int insertIndex = this.datas.size();
        if (datas != null && datas.size() > 0) {
            if (this.datas.addAll(datas)) {
                onSetData();
                if (haveFooterView()) {
                    notifyItemRangeInserted(getItemCount() - 2, datas.size());
                } else {
                    notifyItemRangeInserted(getItemCount() - 1, datas.size());
                }
            }


        }

    }

    public int getSize() {
        return datas == null ? 0 : datas.size();
    }

    public final void addDatas(D[] datas) {
        if (datas != null && datas.length > 0) {
            for (int i = 0; i < datas.length; i++) {
                this.datas.add(datas[i]);
            }
        }
        onSetData();
        notifyDataSetChanged();
    }

    public void refreshData(D[] datas) {

        if (this.datas.size() > 0) {
            this.datas.clear();
        }
        if (datas != null) {
            for (int i = 0; i < datas.length; i++) {
                this.datas.add(datas[i]);
            }
        }
        onSetData();
        notifyDataSetChanged();
    }

    public void refreshData(List<D> datas) {

        if (this.datas.size() > 0) {
            this.datas.clear();
        }
        this.datas.addAll(datas == null ? new ArrayList<D>() : datas);

        onSetData();
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnRecyclerItemListener<D> onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;

    }

    public void setOnLongItemClickListener(OnRecyclerItemLongListener<D> onRecyclerItemLongListener) {
        this.onRecyclerItemLongListener = onRecyclerItemLongListener;

    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            /*针对侧滑删除菜单的判断*/
            if (isSwipeMenu) {
                mOnItemClickListener.onItemClick(this, (ViewGroup) v.getParent(), (D) ((ViewGroup) v.getParent()).getTag());
            } else {
                mOnItemClickListener.onItemClick(this, v, (D) v.getTag());
            }

        }
    }

    /**
     * 如果子类要使用Adapter来传递数据请重写该方法
     *
     * @return
     */
    public Bundle getArguments() {

        return mBundle;
    }

    public void setArguments(Bundle arguments) {

        this.mBundle = arguments;
    }

    protected static class HeadOrFootHolder extends RecyclerBaseHolder {


        public HeadOrFootHolder(View itemView, Context context, RecyclerView.Adapter adapter) {
            super(itemView, context, adapter);
        }

        @Override
        public void bindHolder(int position) {

        }
    }
}
