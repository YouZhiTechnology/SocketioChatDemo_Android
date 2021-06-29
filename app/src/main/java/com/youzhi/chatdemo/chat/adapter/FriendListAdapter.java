package com.youzhi.chatdemo.chat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.youzhi.chatdemo.R;
import com.youzhi.chatdemo.adapter.RecyclerBaseAdapter;
import com.youzhi.chatdemo.adapter.RecyclerBaseHolder;
import com.youzhi.chatdemo.chat.bean.ChatMessageInfo;
import com.youzhi.chatdemo.chat.bean.FriendInfo;

public class FriendListAdapter extends RecyclerBaseAdapter {

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_friend_list;
    }

    @Override
    public Object createViewHolder(View itemView, Context context, int viewType) {
        return new FriendListHolder(itemView, context, this);
    }

    public static class FriendListHolder extends RecyclerBaseHolder<FriendInfo> {

        private final ImageView iv_head;
        private final TextView tv_username;
        private final TextView tv_message_type;

        public FriendListHolder(View itemView, Context context, RecyclerView.Adapter adapter) {
            super(itemView, context, adapter);
            iv_head = itemView.findViewById(R.id.iv_head);
            tv_username = itemView.findViewById(R.id.tv_username);
            tv_message_type = itemView.findViewById(R.id.tv_message_type);
        }

        @Override
        public void bindHolder(int position) {
            Glide.with(mContext).load(mData.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).into(iv_head);
            tv_username.setText(mData.getNick());
            try {
                switch (Integer.valueOf(mData.getFileType())) {
                    case ChatMessageInfo.TYPE_ITEM_TEXT_OTHER:
                        tv_message_type.setText(mData.getLastMsg());
                        break;
                    case ChatMessageInfo.TYPE_ITEM_PHOTO_OTHER:
                        tv_message_type.setText("[图片]");
                        break;
                    case ChatMessageInfo.TYPE_ITEM_VOICE_OTHER:
                        tv_message_type.setText("[语音]");
                        break;
                }
            }catch (Exception e){
                tv_message_type.setText("未知");
            }


        }
    }
}
