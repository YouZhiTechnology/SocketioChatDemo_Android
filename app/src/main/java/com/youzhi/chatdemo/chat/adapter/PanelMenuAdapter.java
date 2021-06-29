package com.youzhi.chatdemo.chat.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.youzhi.chatdemo.R;
import com.youzhi.chatdemo.adapter.RecyclerBaseAdapter;
import com.youzhi.chatdemo.adapter.RecyclerBaseHolder;
import com.youzhi.chatdemo.chat.bean.MenuInfo;

public class PanelMenuAdapter extends RecyclerBaseAdapter {


    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_chat_menu;
    }

    @Override
    public Object createViewHolder(View itemView, Context context, int viewType) {
        return new PanelMenuHolder(itemView, context, this);
    }

    private static class PanelMenuHolder extends RecyclerBaseHolder<MenuInfo> {

        private final TextView tv_menu;

        public PanelMenuHolder(View itemView, Context context, RecyclerView.Adapter adapter) {
            super(itemView, context, adapter);
            tv_menu = itemView.findViewById(R.id.tv_menu);
        }

        @Override
        public void bindHolder(int position) {
            tv_menu.setText(mData.getName());
            tv_menu.setCompoundDrawablesWithIntrinsicBounds(0, mData.getIcon(), 0, 0);
        }

    }
}
