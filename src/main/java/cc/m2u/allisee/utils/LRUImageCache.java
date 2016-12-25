package cc.m2u.allisee.utils;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

import cc.m2u.allisee.MyLog;

/**
 * Created by apple on 2016/12/25.
 */

public class LRUImageCache {
    int MAXMEMONRY = (int) (Runtime.getRuntime().maxMemory() / 1024);
    private LruCache<String, Bitmap> mMemoryCache;

    public LRUImageCache() {

        if (mMemoryCache == null)
            mMemoryCache = new LruCache<String, Bitmap>(
                    MAXMEMONRY / 8) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    // 重写此方法来衡量每张图片的大小，默认返回图片数量。
                    return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
                }

                @Override
                protected void entryRemoved(boolean evicted, String key,
                                            Bitmap oldValue, Bitmap newValue) {

                }
            };
    }

    public void clearCache() {
        if (mMemoryCache != null) {
            if (mMemoryCache.size() > 0) {
                mMemoryCache.evictAll();
            }
            mMemoryCache = null;
        }
    }

    public synchronized void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (mMemoryCache.get(key) == null) {
            if (key != null && bitmap != null)
                mMemoryCache.put(key, bitmap);
        } else {
            MyLog.i(LRUImageCache.class, "the res is aready exits");
        }
    }

    public synchronized Bitmap getBitmapFromMemCache(String key) {
        Bitmap bm = mMemoryCache.get(key);
        if (key != null) {
            MyLog.i(LRUImageCache.class, "hit cache!");
            return bm;
        }
        MyLog.i(LRUImageCache.class, "not hit cache!");
        return null;
    }

    /**
     * 移除缓存
     *
     * @param key
     */
    public synchronized void removeImageCache(String key) {
        if (key != null) {
            if (mMemoryCache != null) {
                Bitmap bm = mMemoryCache.remove(key);
                if (bm != null)
                    bm.recycle();
            }
        }
    }
}
