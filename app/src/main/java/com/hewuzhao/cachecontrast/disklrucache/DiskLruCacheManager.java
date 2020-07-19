package com.hewuzhao.cachecontrast.disklrucache;

import android.graphics.Bitmap;

import com.hewuzhao.cachecontrast.MyApplication;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author hewuzhao
 * @date 2020/6/6
 */
public class DiskLruCacheManager {

    private DiskLruCache mDiskLruCache;

    private DiskLruCacheManager() {
        try {
            mDiskLruCache = DiskLruCache.open(MyApplication.sApplication.getCacheDir(), 1, 1, 1024 * 1024 * 100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class Inner {
        private static DiskLruCacheManager INSTANCE = new DiskLruCacheManager();
    }

    public static DiskLruCacheManager getInstance() {
        return Inner.INSTANCE;
    }

    public void putBitmap(String key, Bitmap bitmap) {
        try {
            DiskLruCache.Editor editor = mDiskLruCache.edit(key);
            if (editor != null) {
                OutputStream os = editor.newOutputStream(0);
                if (DiskLruCacheUtil.writeObject(os, DiskLruCacheUtil.bitmap2Bytes(bitmap))) {
                    editor.commit();
                } else {
                    editor.abort();
                }
                mDiskLruCache.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getBitmap(String key) {
        try {
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);

            if (snapshot != null) {
                InputStream inputStream = snapshot.getInputStream(0);
                byte[] bytes = DiskLruCacheUtil.readObject(inputStream);
                return DiskLruCacheUtil.bytes2Bitmap(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
