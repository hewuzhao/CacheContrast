package com.hewuzhao.cachecontrast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.hewuzhao.cachecontrast.blobcache.BlobCache;
import com.hewuzhao.cachecontrast.blobcache.BlobCacheManager;
import com.hewuzhao.cachecontrast.blobcache.BlobCacheUtil;
import com.hewuzhao.cachecontrast.blobcache.BytesBuffer;
import com.hewuzhao.cachecontrast.disklrucache.DiskLruCacheManager;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hewuzhao
 * @date 2020-06-07
 */
public class MainActivity extends AppCompatActivity {

    private static final long TIME_INTERVAL = 60;

    private TextView mResultDisk;
    private TextView mResultBlob;
    private TextView mResultAndroid;

    private HandlerThread mHandlerThread;
    private Handler mThreadHandler;

    private BytesBuffer mDataBuffer;
    private BlobCache.LookupRequest mLookupRequest;
    private ByteBuffer mPixelsBuffer;
    private BytesBuffer mWidthBuffer;
    private BytesBuffer mHeightBuffer;
    private Map<String, byte[]> mKeyMap;

    private List<Integer> mMb1_1DrawableList = Arrays.asList(
            R.drawable.mb_1_1,
            R.drawable.mb_1_1,
            R.drawable.mb_1_1,
            R.drawable.mb_1_1,
            R.drawable.mb_1_1,
            R.drawable.mb_1_1,
            R.drawable.mb_1_1,
            R.drawable.mb_1_1,
            R.drawable.mb_1_1,
            R.drawable.mb_1_1
    );

    private List<Integer> mKb506DrawableList = Arrays.asList(
            R.drawable.kb_506,
            R.drawable.kb_506,
            R.drawable.kb_506,
            R.drawable.kb_506,
            R.drawable.kb_506,
            R.drawable.kb_506,
            R.drawable.kb_506,
            R.drawable.kb_506,
            R.drawable.kb_506,
            R.drawable.kb_506
    );

    private List<Integer> mKb100DrawableList = Arrays.asList(
            R.drawable.kb_100,
            R.drawable.kb_100,
            R.drawable.kb_100,
            R.drawable.kb_100,
            R.drawable.kb_100,
            R.drawable.kb_100,
            R.drawable.kb_100,
            R.drawable.kb_100,
            R.drawable.kb_100,
            R.drawable.kb_100
    );

    private List<Integer> mCurrentList;
    private String mCurrentBlobName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResultDisk = findViewById(R.id.result_disk);
        mResultBlob = findViewById(R.id.result_blob);
        mResultAndroid = findViewById(R.id.result_android);

        startThread();
        mCurrentList = mMb1_1DrawableList;
        mCurrentBlobName = "1.1_mb";

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResultDisk.setText("DiskLruCache Save Cost Time:\n");
                mResultBlob.setText("BlobCache Save Cost Time:\n");
                mResultAndroid.setText("");
                save();
            }
        });
        findViewById(R.id.get).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResultDisk.setText("DiskLruCache Get Cost Time:\n");
                mResultBlob.setText("BlobCache Get Cost Time:\n");
                mResultAndroid.setText("Android Get Cost Time:\n");
                get();
            }
        });

        findViewById(R.id.kb100).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentList = mKb100DrawableList;
                mCurrentBlobName = "100_kb";
            }
        });

        findViewById(R.id.kb506).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentList = mKb506DrawableList;
                mCurrentBlobName = "506_kb";
            }
        });

        findViewById(R.id.mb1_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentList = mMb1_1DrawableList;
                mCurrentBlobName = "1.1_mb";
            }
        });
    }

    private void startThread() {
        mHandlerThread = new HandlerThread("cache_thread");
        mHandlerThread.start();
        mThreadHandler = new Handler(mHandlerThread.getLooper());
    }

    private void save() {
        final List<Integer> list = new ArrayList<>(mCurrentList);
        final BlobCache blobCache = BlobCacheManager.getInstance().getBlobCache(mCurrentBlobName, 100, 1024 * 1024 * 400, 1);
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (list.size() == 0) {
                    return;
                }
                int id = list.remove(0);
                Resources res = getResources();
                Drawable drawable = res.getDrawable(id);
                if (drawable instanceof BitmapDrawable) {
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                    String name = res.getResourceName(id);
                    name = name.split("\\/")[1];
                    final String finalName = name;
                    final String size = byteToMB(bitmap.getByteCount());



                    // Disk
                    long t1 = System.currentTimeMillis();
                    DiskLruCacheManager.getInstance().putBitmap(finalName, bitmap);
                    long now = System.currentTimeMillis();
                    final long time = now - t1;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mResultDisk.setText(mResultDisk.getText().toString() + "\n" + finalName + ": " + time);
                        }
                    });


                    // Blob
                    t1 = System.currentTimeMillis();
                    BlobCacheUtil.saveImageByBlobCache(bitmap, finalName, blobCache);
                    now = System.currentTimeMillis();
                    final long time2 = now - t1;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mResultBlob.setText(mResultBlob.getText().toString() + "\n" + finalName + ": " + time2);
                        }
                    });
                }


                mThreadHandler.postDelayed(this, TIME_INTERVAL);
            }
        });
    }

    private void get() {
        final List<Integer> list = new ArrayList<>(mCurrentList);
        final BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        decodeOptions.inMutable = true;
        decodeOptions.inDensity = Bitmap.DENSITY_NONE;
        decodeOptions.inScaled = false;
        final BlobCache blobCache = BlobCacheManager.getInstance().getBlobCache(mCurrentBlobName, 100, 1024 * 1024 * 400, 1);
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (list.size() == 0) {
                    return;
                }
                int id = list.remove(0);
                Resources res = getResources();
                String name = res.getResourceName(id);
                name = name.split("\\/")[1];
                final String finalName = name;
                String size;

                // Disk
                long t1 = System.currentTimeMillis();
                Bitmap bitmap = DiskLruCacheManager.getInstance().getBitmap(finalName);
                long now = System.currentTimeMillis();
                final long t = now - t1;

                if (bitmap == null) {
                    Log.e("test5", "DiskLruCache, get bitmap is null.");
                } else {
                    size = byteToMB(bitmap.getByteCount());
                    final String finalSize = size;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mResultDisk.setText(mResultDisk.getText().toString() + "\n" + finalName + ": " + t);
                        }
                    });
                }

                // Blob
                if (mDataBuffer == null) {
                    mDataBuffer = new BytesBuffer();
                }
                if (mLookupRequest == null) {
                    mLookupRequest = new BlobCache.LookupRequest();
                }
                if (mWidthBuffer == null) {
                    mWidthBuffer = new BytesBuffer(4);
                }
                if (mHeightBuffer == null) {
                    mHeightBuffer = new BytesBuffer(4);
                }

                if (mKeyMap == null) {
                    mKeyMap = new ConcurrentHashMap<>();
                }
                byte[] key = mKeyMap.get(finalName);
                if (key == null) {
                    key = BlobCacheUtil.getBytes(finalName);
                    mKeyMap.put(finalName, key);
                }

                t1 = System.currentTimeMillis();

                BytesBuffer bytesBuffer = BlobCacheUtil.getCacheDataByName(blobCache, finalName, mDataBuffer, key, mLookupRequest);
                if (bytesBuffer != null && bytesBuffer.data != null) {
                    mDataBuffer = bytesBuffer;
                    if (mPixelsBuffer == null || mPixelsBuffer.capacity() != bytesBuffer.data.length) {
                        mPixelsBuffer = ByteBuffer.allocate(bytesBuffer.data.length);
                    }
                    bitmap = BlobCacheUtil.getCacheBitmapByData(bytesBuffer, mPixelsBuffer, null, mWidthBuffer, mHeightBuffer);

                    now = System.currentTimeMillis();
                    final long time = now - t1;

                    if (bitmap == null) {

                    } else {
                        size = byteToMB(bitmap.getByteCount());
                        final String finalSize2 = size;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mResultBlob.setText(mResultBlob.getText().toString() + "\n" + finalName + ": " + time);
                            }
                        });
                    }
                }


                // Android
                t1 = System.currentTimeMillis();

                bitmap = BitmapFactory.decodeResource(res, id, decodeOptions);
                now = System.currentTimeMillis();
                final long time = now - t1;

                if (bitmap == null) {

                } else {
                    size = byteToMB(bitmap.getByteCount());
                    final String finalSize1 = size;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mResultAndroid.setText(mResultAndroid.getText().toString() + "\n" + finalName + ": " + time);
                        }
                    });
                }

                mThreadHandler.postDelayed(this, TIME_INTERVAL);
            }
        });
    }

    //将字节数转化为MB
    private String byteToMB(long size){
        long kb = 1024;
        long mb = kb*1024;
        long gb = mb*1024;
        if (size >= gb){
            return String.format("%.1f GB",(float)size/gb);
        }else if (size >= mb){
            float f = (float) size/mb;
            return String.format(f > 100 ?"%.0f MB":"%.1f MB",f);
        }else if (size > kb){
            float f = (float) size / kb;
            return String.format(f>100?"%.0f KB":"%.1f KB",f);
        }else {
            return String.format("%d B",size);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mThreadHandler != null) {
            mThreadHandler.removeCallbacksAndMessages(null);
            mThreadHandler = null;
        }

        if (mHandlerThread != null) {
            mHandlerThread.quit();
            mHandlerThread = null;
        }
    }
}