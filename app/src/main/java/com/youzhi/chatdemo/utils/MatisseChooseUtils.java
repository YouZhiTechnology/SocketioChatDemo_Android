package com.youzhi.chatdemo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;


import com.youzhi.chatdemo.R;
import com.youzhi.chatdemo.chat.utils.FileSizeUtil;
import com.youzhi.chatdemo.chat.utils.MediaFileUtils;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import com.zhihu.matisse.internal.entity.IncapableCause;
import com.zhihu.matisse.internal.entity.Item;
import com.zhihu.matisse.internal.utils.PhotoMetadataUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author gaoyuheng
 * @description:
 * @date :2020/8/31 11:49
 */
public class MatisseChooseUtils {

    public static void startPic(Activity context, int requestCode, int maxSelect) {
        Matisse.from(context)
                .choose(MimeType.ofImage())
                //是否只显示选择的类型的缩略图，就不会把所有图片视频都放在一起，而是需要什么展示什么
                .showSingleMediaType(true)
                //这两行要连用 是否在选择图片中展示照相 和适配安卓7.0 FileProvider
                .capture(true)
                .captureStrategy(new CaptureStrategy(true, context.getPackageName() + ".provider"))
                //有序选择图片 123456...
                .countable(true)
                //最大选择数量为9
                .maxSelectable(maxSelect)

                //选择方向
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

                //界面中缩略图的质量
                .thumbnailScale(0.8f)
                //黑色主题
                .theme(R.style.Matisse_Dracula)
                //Glide加载方式
                .imageEngine(new GlideEngine())
                //请求码
                .forResult(requestCode);

    }

    public static void startPicAndVideo(Activity context, int requestCode, int maxSelect) {
        Matisse.from(context)
                .choose(MimeType.ofAll())
                //是否只显示选择的类型的缩略图，就不会把所有图片视频都放在一起，而是需要什么展示什么
                .showSingleMediaType(true)
                //这两行要连用 是否在选择图片中展示照相 和适配安卓7.0 FileProvider
                .capture(true)
                .captureStrategy(new CaptureStrategy(true, context.getPackageName() + ".provider"))
                //有序选择图片 123456...
                .countable(true)
                //最大选择数量为9
                .maxSelectable(maxSelect)

                //选择方向
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                //添加过滤
                .addFilter(new PathFilter())
                //界面中缩略图的质量
                .thumbnailScale(0.8f)
                //黑色主题
                .theme(R.style.Matisse_Dracula)
                //Glide加载方式
                .imageEngine(new GlideEngine())
                //请求码
                .forResult(requestCode);

    }


    public static class PathFilter extends Filter {


        @Override
        protected Set<MimeType> constraintTypes() {
            return new HashSet<MimeType>() {{
                add(MimeType.JPEG);
                add(MimeType.PNG);
                add(MimeType.MP4);
                add(MimeType.MKV);
                add(MimeType.AVI);
            }};
        }

        @Override
        public IncapableCause filter(Context context, Item item) {
            if (!needFiltering(context, item)) {
                return null;
            }

            String path = PhotoMetadataUtils.getPath(context.getContentResolver(), item.getContentUri());
                if (MediaFileUtils.isVideoFileType(path)&& FileSizeUtil.getFileOrFilesSize(path,FileSizeUtil.SIZETYPE_MB)>50){
                    return new IncapableCause(IncapableCause.DIALOG, "视频大小不得超过50MB");
                }
            return null;
        }

    }
}
