package com.hewuzhao.cachecontrast.disklrucache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author hewuzhao
 * @date 2020/6/6
 */
public class DiskLruCacheUtil {

    public static boolean writeObject(OutputStream fos, byte[] values) {
        try {
            fos.write(values);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static byte[] readObject(InputStream in) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int count = -1;
        try {
            while((count = in.read(data, 0, 4096)) != -1) {
                outStream.write(data, 0, count);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        data = null;
        return outStream.toByteArray();
    }

    private static void closeSafely(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static byte[] bitmap2Bytes(Bitmap bm) {
        if (bm == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap bytes2Bitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
