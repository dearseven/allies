package cc.m2u.allisee.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by apple on 2016/12/18.
 */

public class BitmapHelper {

    /**
     * 2  * 根据View(主要是ImageView)的宽和高来计算Bitmap缩放比例。默认不缩放
     * 3  *
     * 4  * @param options
     * 5  * @param width
     * 6  * @param height
     * 7

    public static int computeScale(BitmapFactory.Options options, int viewWidth, int viewHeight) {
        int inSampleSize = 1; // 默认缩放比例1 即不缩放
        if (viewWidth == 0 || viewHeight == 0) { // 如果宽高都为0 即不存在 返回1
            return inSampleSize;
        }
        int bitmapWidth = options.outWidth;// 获得图片的宽和高
        int bitmapHeight = options.outHeight;

        // 假如Bitmap的宽度或高度大于我们设定图片的View的宽高，则计算缩放比例
        if (bitmapWidth > viewWidth || bitmapHeight > viewWidth) {
            int widthScale = Math.round((float) bitmapWidth / (float) viewWidth);
            int heightScale = Math.round((float) bitmapHeight / (float) viewWidth);

            // 为了保证图片不缩放变形，我们取宽高比例最小的那个
            inSampleSize = widthScale < heightScale ? widthScale : heightScale;
        }
        return inSampleSize;
    }  */


    /**
     * 获取缩放图片
     *
     * @param path
     * @return
     */
    public static Bitmap loadBitmap(String path) {
        InputStream inputStream = null;
        File f = null;
        int n = 1;//缩放比例
        if (!path.isEmpty()) {
            try {
                f = new File(path);
                inputStream = new FileInputStream(f);
                //kb
                long fKB = f.length() / 1024;
                if (fKB <= 64) {
                    n = 1;
                } else if (fKB <= 128) {
                    n = 2;
                } else if (fKB <= 256) {
                    n = 4;
                } else if (fKB <= 512) {
                    n = 6;
                } else if (fKB <= 1024) {
                    n = 8;
                } else if (fKB <= 2048) {
                    n = 20;
                } else if (fKB <= 4096) {
                    n = 40;
                } else {
                    n = 80;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565; // 减小画面的精细度，每个像素占内存减少
        opts.inPurgeable = true; // 可以被回收
        opts.inInputShareable = true;
        //opts.inJustDecodeBounds = true;

        if (n != 0) {
            opts.inSampleSize = n;
        }
        Bitmap bmp = BitmapFactory.decodeStream(inputStream, null, opts);
        return bmp;
    }
}
