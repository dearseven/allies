package cc.m2u.allisee.beans;

import android.graphics.Bitmap;

/**
 * 文件描述
 * Created by wx on 2016/12/18.
 */

public class FileDes {
    public String filePath;
    public String fileName;
    public String thumbPath;
    public Bitmap thumb;
    /**
     * 初始的时候为默认的类型
     */
    public FILE_TYPE fileType = FILE_TYPE.default_type;
    public int id;


    public enum FILE_TYPE {
        default_type, sys_img, sys_video, wx_img, wx_video, qq_img, qq_video, qq_video_thumb, wx_video_thumb
    }
}
