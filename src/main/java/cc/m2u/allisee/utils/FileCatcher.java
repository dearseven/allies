package cc.m2u.allisee.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cc.m2u.allisee.beans.FileDes;

/**
 * 读取系统所有的视频，图片位置，以及qq和微信的
 * Created by wx on 2016/12/18.
 */

public class FileCatcher {

    public List<FileDes> files = new ArrayList<FileDes>();

    public List<FileDes> getAllFiles(Context context, boolean si, boolean sv, boolean qq, boolean wx) {
        if (si)
            getSysImg(context);
        if (sv)
            getSysVideo(context);
        if (qq)
            getQQFile(context);
        if (wx)
            getWXFile(context);
        return files;
    }

    private void getQQFile(Context context) {
        File sdc = Environment.getExternalStorageDirectory();
        Log.i("allisee", sdc.getPath());
        String path = sdc.getPath() + "/tencent/";
        File folder = new File(path);
        if (folder.exists()) {
            Log.i("allisee", "找到路径");
        } else {
            return;
        }
        File fs[] = folder.listFiles();
        for (File _fs : fs) {
            if (_fs.getName().length() > 0) {
                Log.i("allisee", _fs.getPath());
                toolGetWXFile(_fs);
            }
        }
    }

    private void getWXFile(Context context) {
        File sdc = Environment.getExternalStorageDirectory();
        Log.i("allisee", sdc.getPath());
        String path = sdc.getPath() + "/tencent/MicroMsg/";
        File folder = new File(path);
        if (folder.exists()) {
            Log.i("allisee", "找到路径");
        } else {
            return;
        }
        File fs[] = folder.listFiles();
        for (File _fs : fs) {
            if (_fs.getName().length() > 30) {
                Log.i("allisee", _fs.getPath());
                toolGetWXFile(_fs);
            }
        }

    }

    private void toolGetWXFile(File f) {
        File fs[] = f.listFiles();
        for (File img : fs) {
            if (img.isDirectory() && img.getName().contains("package")) {
                continue;
            }
            if (img.isDirectory()) {
                toolGetWXFile(img);
            } else {
                if (img.getName().contains(".jpg") || img.getName().contains(".JPE") ||
                        img.getName().contains(".JPGE") || img.getName().contains(".jpge") ||
                        img.getName().contains(".png") || img.getName().contains(".PNG") ||
                        img.getName().contains(".gif") || img.getName().contains(".GIF")) {
                    Log.i("allisee", "图片:" + img.getPath());
                    FileDes fd = new FileDes();
                    fd.id = 0;
                    fd.filePath = img.getPath();
                    fd.fileName = fd.filePath.substring(fd.filePath.lastIndexOf("/") + 1); //文件名
                    fd.fileType = FileDes.FILE_TYPE.wx_img;
                    files.add(fd);
                }
            }
        }

    }

    /**
     * 读取系统所有的视频
     *
     * @param context
     */
    private void getSysVideo(Context context) {
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = null;
        try {
            cursor = resolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
            if (cursor == null) return;
            while (cursor.moveToNext()) {
                FileDes fd = new FileDes();
                fd.id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                fd.filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                fd.fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                fd.fileType = FileDes.FILE_TYPE.sys_video;
                //获取略缩图
                Bitmap thumbnail = MediaStore.Video.Thumbnails.getThumbnail(resolver, fd.id, MediaStore.Video.Thumbnails.MICRO_KIND, null);
                fd.thumb = thumbnail;
                files.add(fd);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }
    }

    /**
     * 通过系统读取所有的图片
     *
     * @param context
     */
    private void getSysImg(Context context) {
        ContentResolver resolver = context.getContentResolver();
        Cursor cs = null;
        try {
            cs = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
            if (cs == null)
                return;
            while (cs.moveToNext()) {
                FileDes fd = new FileDes();
                fd.id = cs.getInt(cs.getColumnIndex(MediaStore.Images.Media._ID));
                fd.filePath = cs.getString(cs.getColumnIndex(MediaStore.Images.Media.DATA));
                fd.fileName = cs.getString(cs.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)); //文件名
                fd.fileType = FileDes.FILE_TYPE.sys_img;
                files.add(fd);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (cs != null && !cs.isClosed()) {
                cs.close();
                cs = null;
            }
        }

    }
}
