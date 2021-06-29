package com.youzhi.chatdemo.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.FileProvider;


import com.youzhi.chatdemo.MyApplication;
import com.youzhi.chatdemo.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;

/**
 * Created by max on 2017/10/16.
 * 文件工具类
 */

public class FileUtil {

    public final static String projectName = "ranshu";

    //文件基本地址
    public final static String baseFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + projectName;

    //本地存储的图片路径
    public static String getImageFilePath() {
        return baseFilePath + "/image/";//+ Application.getInstance().getSpUtil().getLoginName()+"/";
    }

    public static String getDiskCacheDir(Context context) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();

        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    public static String getDiskFileDir(Context context, String type) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalFilesDir(type).getPath();

        } else {
            cachePath = context.getFilesDir().getPath();
        }
        return cachePath;
    }

    //需要上传的数据文件路径
    public static String getUploadDataFilePath() {
        return baseFilePath + "/upload/";//+ Application.getInstance().getSpUtil().getLoginName()+"/data/";
    }

    //清理图片选择后压缩的地址对应的所有图片
    public static void clearCompressCache() {
        File file = new File(FileUtil.getDiskFileDir(MyApplication.getContext(), MyApplication.getContext().getString(R.string.luban_compress)));
        try {
            if (file.exists()) {
                if (file.isDirectory()) {
                    File[] childrenFiles = file.listFiles();
                    if (childrenFiles != null) {
                        for (File childFile : childrenFiles) {
                            if (!childFile.isDirectory()) {
                                childFile.delete();
                            }
                        }
                    }

                }
            }
        } catch (Exception e) {

        }

    }
    //清理图片选择后压缩的地址对应的所有图片
    public static void clearCacheForType(String diskTypeFolder) {
        File file = new File(FileUtil.getDiskFileDir(MyApplication.getContext(), diskTypeFolder));
        try {
            if (file.exists()) {
                if (file.isDirectory()) {
                    File[] childrenFiles = file.listFiles();
                    if (childrenFiles != null) {
                        for (File childFile : childrenFiles) {
                            if (!childFile.isDirectory()) {
                                childFile.delete();
                            }
                        }
                    }

                }
            }
        } catch (Exception e) {

        }

    }
    public static String getPath(String path) {
        return baseFilePath + "/" + path;
    }

    //需要下载保存的文件路径
    public static String getDownloadFilePath() {
        String path = baseFilePath + "/download/";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public static void deleteFiles(File file) {
        if (file == null) return;

        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    deleteFiles(f);
                }
            } else {
                file.delete();
            }
        }
    }

    public static File getFileByDirectory(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files.length >= 1) {
                    return files[0];
                }
            }
        }

        return null;
    }

    /**
     * 检查文件路径是否存在，否则创建
     *
     * @param file
     */
    public static void checkFilePathExists(File file) {
        if (file != null) {
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
        }
    }

    public static void write(String path, String fileName, String content) throws IOException {
        File fileP = new File(path);
        if (!fileP.exists()) fileP.mkdirs();
        File file = new File(fileP, fileName);
        if (file.exists()) file.delete();
        file.createNewFile();
        Writer w = new FileWriter(file);
        w.write(content);
        w.flush();
        w.close();
    }

    public static boolean isSDCardEnable() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }

//    public static String zipUploadData(Map<String,List> map) throws Exception {
//        String dataPath = getUploadDataFilePath();
//        String zipPath = new File(dataPath).getParent()+"/zip/"+ UUID.randomUUID()+".zip";
//
//        File dataFile = new File(dataPath);
//        if(!dataFile.exists()){
//            dataFile.mkdirs();
//        }
//        deleteFiles(dataFile);
//        File zipFile = new File(zipPath);
//        File zipParentFile = zipFile.getParentFile();
//        if(!zipParentFile.exists()){
//            zipParentFile.mkdirs();
//        }else{
//            deleteFiles(zipFile.getParentFile());
//        }
//
//        List<String> fileList = new ArrayList<>();
//        Iterator<String> it = map.keySet().iterator();
//        while(it.hasNext()){
//            String key = it.next();
//            List list = map.get(key);
//            if("file".equals(key)){
//                fileList.addAll(list);
//            }else{
//                JSONArray array = new JSONArray(list);
//                if(array.length()>0){
//                    write(dataPath,key,array.toString());
//                }
//            }
//        }
//        String[] paths = new String[fileList.size()+1];
//        fileList.toArray(paths);
//        paths[paths.length-1]=dataPath;
//        new ZipCompress(zipPath,paths).zip();
//        return zipPath;
//    }

    /**
     * 换算文件大小
     *
     * @param size
     * @return
     */
    public static String formatFileSize(long size) {
        DecimalFormat df = new DecimalFormat("0.##");
        String fileSizeString = "未知大小";
        if (size < 1024) {
            fileSizeString = df.format((double) size) + "B";
        } else if (size < 1048576) {
            fileSizeString = df.format((double) size / 1024) + "Kb";
        } else if (size < 1073741824) {
            fileSizeString = df.format((double) size / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) size / 1073741824) + "G";
        }
        return fileSizeString;
    }

    public static String writeFile(String filepath, String content) throws Exception {
        filepath = baseFilePath + "/" + "错误信息" + "/" + filepath;
        // 创建文件目录
        getAppDirAndMK(filepath);
        File file = new File(filepath);
        writeFile(file, content.getBytes("UTF-8"));
        return filepath;
    }

    public static void writeFile(File file, byte[] data) throws Exception {
        FileOutputStream outStream = new FileOutputStream(file, true);
        outStream.write(data);
        outStream.close();
    }

    public static String getAppDirAndMK(String dir) {
        // 如果包含文件名称 去掉文件名
        if (dir.lastIndexOf(".") > dir.lastIndexOf("/")) {
            dir = dir.contains("/") ? dir.substring(0, dir.lastIndexOf("/")) : "";
        }
        dir += dir.endsWith("/") ? "" : "/";
        if (mkdirs(dir)) {
            return dir;
        } else {
            return "";
        }
    }

    public static boolean mkdirs(String dir) {
        if (existsSD()) {
            File file_dir = new File(dir);
            if (!file_dir.exists()) {
                return file_dir.mkdirs();
            }
            return true;
        }
        return false;
    }

    public static boolean existsSD() {
        boolean exist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if (!exist) {
            Log.d("zzpig", "读取sd卡错误！");
        }
        return exist;
    }

    public static Intent openFileIntent(Context mContext, String filePath) {
        File file = new File(filePath);
        if ((file == null) || !file.exists())
            return null;
        if (file.isDirectory())
            return getDirIntent(mContext, filePath);
        /* 取得扩展名 */
        String end = file
                .getName()
                .substring(file.getName().lastIndexOf(".") + 1,
                        file.getName().length()).toLowerCase();
        /* 依扩展名的类型决定MimeType */
        if (end.equals("pdf")) {
            return getPdfFileIntent(filePath);
        } else {
            return null;
        }
    }

    // Android获取一个用于打开文件夹的intent
    public static Intent getDirIntent(Context mContext, String param) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        File file = new File(param);
        Uri data;
        // 判断版本大于等于7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // "net.csdn.blog.ruancoder.fileprovider"即是在清单文件中配置的authorities
            data = FileProvider.getUriForFile(mContext, MyApplication.getContext().getPackageName() + ".fileprovider", file);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            data = Uri.fromFile(file);
        }
        intent.setDataAndType(data, "*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        return intent;
    }

    // Android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }

    /**
     * 将Bitmap 图片保存到本地路径，并返回路径
     *
     * @param c        //     * @param mType 资源类型，参照  MultimediaContentType 枚举，根据此类型，保存时可自动归类
     * @param fileName 文件名称
     * @param bitmap   图片
     * @return
     */
    public static String saveFile(Context c, String fileName, Bitmap bitmap) {
        return saveFile(c, "", fileName, bitmap);
    }

    public static String saveFile(Context c, String filePath, String fileName, Bitmap bitmap) {
        byte[] bytes = bitmapToBytes(bitmap);
        return saveFile(c, filePath, fileName, bytes);
    }

    public static byte[] bitmapToBytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static String saveFile(Context c, String filePath, String fileName, byte[] bytes) {
        String fileFullName = "";
        FileOutputStream fos = null;
        try {
            String suffix = "";
            if (filePath == null || filePath.trim().length() == 0) {
                filePath = getImageFilePath() + "faceImage";
            }
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            File fullFile = new File(filePath, fileName + suffix);
            fileFullName = fullFile.getPath();
            fos = new FileOutputStream(new File(filePath, fileName + suffix));
            fos.write(bytes);
        } catch (Exception e) {
            fileFullName = "";
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    fileFullName = "";
                }
            }
        }
        return fileFullName;
    }

    private static Application getApplicationByReflect() {
        try {
            @SuppressLint("PrivateApi")
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object thread = activityThread.getMethod("currentActivityThread").invoke(null);
            Object app = activityThread.getMethod("getApplication").invoke(thread);
            if (app == null) {
                throw new NullPointerException("u should init first");
            }
            return (Application) app;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("u should init first");
    }


    /**
     * 转换图片成圆形
     *
     * @param bitmap 传入Bitmap对象
     * @return
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }

    public static String reckon() {
        return reckon(baseFilePath);
    }

    public static String reckon(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return getByte(getTotalSizeOfFilesInDir(file));
        }
        return "0M";
    }

    // 递归方式 计算文件的大小
    private static long getTotalSizeOfFilesInDir(final File file) {
        if (file.isFile())
            return file.length();
        final File[] children = file.listFiles();
        long total = 0;
        if (children != null)
            for (final File child : children)
                total += getTotalSizeOfFilesInDir(child);
        return total;
    }

    private static String getByte(long count) {
        String str = "0";
        long Kb = 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;
        DecimalFormat df = new DecimalFormat("0.##");
        if (count > Gb) {
            str = df.format((double) count / Gb) + "G";
        } else if (count > Mb) {
            str = df.format((double) count / Mb) + "M";
        } else {
            str = df.format((double) count / Kb) + "KB";
        }
//		else
//		{
//			str = df.format((double)count)+  "B";
//		}
        return str;
    }
}
