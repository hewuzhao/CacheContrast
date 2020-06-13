package com.hewuzhao.cachecontrast.disklrucache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * @author hewuzhao
 * @date 2020/6/6
 */
public class DiskLruCacheUtil {

    public static boolean writeObject(OutputStream fos, Object object) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
            oos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeSafely(oos);
        }
        return false;
    }

    public static Object readObject(InputStream inputStream) {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(inputStream);
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeSafely(ois);
        }
        return null;
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
