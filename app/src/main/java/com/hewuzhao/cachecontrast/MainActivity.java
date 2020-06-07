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
import android.widget.ScrollView;
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

    private static final long TIME_INTERVAL = 10;

    private TextView mResultSave;
    private TextView mResultGet;
    private TextView mResultSaveTip;
    private TextView mResultGetTip;
    private ScrollView mResultSaveScrollView;
    private ScrollView mResultGetScrollView;

    private HandlerThread mHandlerThread;
    private Handler mThreadHandler;

    private BytesBuffer mDataBuffer;
    private BlobCache.LookupRequest mLookupRequest;
    private ByteBuffer mPixelsBuffer;
    private BytesBuffer mWidthBuffer;
    private BytesBuffer mHeightBuffer;
    private Map<String, byte[]> mKeyMap;

    private List<Integer> mBigDrawableList = Arrays.asList(
            R.drawable.big_00,
            R.drawable.big_01,
            R.drawable.big_02,
            R.drawable.big_03,
            R.drawable.big_04,
            R.drawable.big_05,
            R.drawable.big_06,
            R.drawable.big_07,
            R.drawable.big_08,
            R.drawable.big_09,
            R.drawable.big_10,
            R.drawable.big_11,
            R.drawable.big_12,
            R.drawable.big_13,
            R.drawable.big_14,
            R.drawable.big_15,
            R.drawable.big_16,
            R.drawable.big_17,
            R.drawable.big_18,
            R.drawable.big_19,
            R.drawable.big_00,
            R.drawable.big_01,
            R.drawable.big_02,
            R.drawable.big_03,
            R.drawable.big_04,
            R.drawable.big_05,
            R.drawable.big_06,
            R.drawable.big_07,
            R.drawable.big_08,
            R.drawable.big_09,
            R.drawable.big_10,
            R.drawable.big_11,
            R.drawable.big_12,
            R.drawable.big_13,
            R.drawable.big_14,
            R.drawable.big_15,
            R.drawable.big_16,
            R.drawable.big_17,
            R.drawable.big_18,
            R.drawable.big_19,
            R.drawable.big_00,
            R.drawable.big_01,
            R.drawable.big_02,
            R.drawable.big_03,
            R.drawable.big_04,
            R.drawable.big_05,
            R.drawable.big_06,
            R.drawable.big_07,
            R.drawable.big_08,
            R.drawable.big_09,
            R.drawable.big_10,
            R.drawable.big_11,
            R.drawable.big_12,
            R.drawable.big_13,
            R.drawable.big_14,
            R.drawable.big_15,
            R.drawable.big_16,
            R.drawable.big_17,
            R.drawable.big_18,
            R.drawable.big_19
    );

    private List<Integer> mSmallDrawableList = Arrays.asList(
            R.drawable.small_00,
            R.drawable.small_01,
            R.drawable.small_02,
            R.drawable.small_03,
            R.drawable.small_04,
            R.drawable.small_05,
            R.drawable.small_06,
            R.drawable.small_07,
            R.drawable.small_08,
            R.drawable.small_09,
            R.drawable.small_10,
            R.drawable.small_11,
            R.drawable.small_12,
            R.drawable.small_13,
            R.drawable.small_14,
            R.drawable.small_15,
            R.drawable.small_16,
            R.drawable.small_17,
            R.drawable.small_18,
            R.drawable.small_19,
            R.drawable.small_00,
            R.drawable.small_01,
            R.drawable.small_02,
            R.drawable.small_03,
            R.drawable.small_04,
            R.drawable.small_05,
            R.drawable.small_06,
            R.drawable.small_07,
            R.drawable.small_08,
            R.drawable.small_09,
            R.drawable.small_10,
            R.drawable.small_11,
            R.drawable.small_12,
            R.drawable.small_13,
            R.drawable.small_14,
            R.drawable.small_15,
            R.drawable.small_16,
            R.drawable.small_17,
            R.drawable.small_18,
            R.drawable.small_19,
            R.drawable.small_00,
            R.drawable.small_01,
            R.drawable.small_02,
            R.drawable.small_03,
            R.drawable.small_04,
            R.drawable.small_05,
            R.drawable.small_06,
            R.drawable.small_07,
            R.drawable.small_08,
            R.drawable.small_09,
            R.drawable.small_10,
            R.drawable.small_11,
            R.drawable.small_12,
            R.drawable.small_13,
            R.drawable.small_14,
            R.drawable.small_15,
            R.drawable.small_16,
            R.drawable.small_17,
            R.drawable.small_18,
            R.drawable.small_19,
            R.drawable.small_00,
            R.drawable.small_01,
            R.drawable.small_02,
            R.drawable.small_03,
            R.drawable.small_04,
            R.drawable.small_05,
            R.drawable.small_06,
            R.drawable.small_07,
            R.drawable.small_08,
            R.drawable.small_09,
            R.drawable.small_10,
            R.drawable.small_11,
            R.drawable.small_12,
            R.drawable.small_13,
            R.drawable.small_14,
            R.drawable.small_15,
            R.drawable.small_16,
            R.drawable.small_17,
            R.drawable.small_18,
            R.drawable.small_19
    );

    private List<Integer> mCurrentList;
    private String mCurrentBlobName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResultSave = findViewById(R.id.result_save);
        mResultGet = findViewById(R.id.result_get);
        mResultSaveTip = findViewById(R.id.result_save_tip);
        mResultGetTip = findViewById(R.id.result_get_tip);
        mResultSaveScrollView = findViewById(R.id.result_save_scroll);
        mResultGetScrollView = findViewById(R.id.result_get_scroll);

        startThread();
        mCurrentList = mBigDrawableList;
        mCurrentBlobName = "BIG";

        findViewById(R.id.disklrucache_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResultSaveTip.setText("DiskLruCache Save:");
                diskCacheSave();
            }
        });
        findViewById(R.id.disklrucache_get).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResultGetTip.setText("DiskLruCache Get:");
                diskCacheGet();
            }
        });

        findViewById(R.id.blobcache_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResultSaveTip.setText("BlobCache Save:");
                blobCacheSave();
            }
        });
        findViewById(R.id.blobcache_get).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResultGetTip.setText("BlobCache Get:");
                blobCacheGet();
            }
        });

        findViewById(R.id.android_get).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResultGetTip.setText("Android Get:");
                androidGet();
            }
        });

        findViewById(R.id.change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentList == mBigDrawableList) {
                    mCurrentList = mSmallDrawableList;
                    mCurrentBlobName = "SMALL";
                } else {
                    mCurrentList = mBigDrawableList;
                    mCurrentBlobName = "BIG";
                }
            }
        });
    }

    private void startThread() {
        mHandlerThread = new HandlerThread("cache_thread");
        mHandlerThread.start();
        mThreadHandler = new Handler(mHandlerThread.getLooper());
    }

    private void diskCacheSave() {
        final List<Integer> list = new ArrayList<>(mCurrentList);
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
                    long t1 = System.currentTimeMillis();
                    DiskLruCacheManager.getInstance().putBitmap(finalName, bitmap);
                    long now = System.currentTimeMillis();
                    final long time = now - t1;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mResultSave.setText(mResultSave.getText().toString() + finalName + " cost time=" + time + "\n");
                            mResultSaveScrollView.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                }

                mThreadHandler.postDelayed(this, TIME_INTERVAL);
            }
        });
    }

    private void diskCacheGet() {
        final List<Integer> list = new ArrayList<>(mCurrentList);
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
                long t1 = System.currentTimeMillis();
                Bitmap bitmap = DiskLruCacheManager.getInstance().getBitmap(finalName);
                long now = System.currentTimeMillis();
                final long t = now - t1;
                if (bitmap == null) {
                    Log.e("test5", "DiskLruCache, get bitmap is null.");
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mResultGet.setText(mResultGet.getText().toString() + finalName + " cost time=" + t + "\n");
                            mResultGetScrollView.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                }

                mThreadHandler.postDelayed(this, TIME_INTERVAL);
            }
        });
    }

    private void blobCacheSave() {
        final List<Integer> list = new ArrayList<>(mCurrentList);
        final BlobCache blobCache = BlobCacheManager.getInstance().getBlobCache(mCurrentBlobName, 100, 1024 * 1024 * 400, 1);
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (list.size() == 0) {
                    return;
                }

                Resources res = getResources();
                int id = list.remove(0);
                Drawable drawable = res.getDrawable(id);
                if (drawable instanceof BitmapDrawable) {
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                    String name = res.getResourceName(id);
                    name = name.split("\\/")[1];
                    final String finalName = name;
                    long t1 = System.currentTimeMillis();
                    BlobCacheUtil.saveImageByBlobCache(bitmap, finalName, blobCache);
                    long now = System.currentTimeMillis();
                    final long time = now - t1;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mResultSave.setText(mResultSave.getText().toString() + finalName + " cost time=" + time + "\n");
                            mResultSaveScrollView.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                }

                mThreadHandler.postDelayed(this, TIME_INTERVAL);
            }
        });
    }

    private void blobCacheGet() {
        final List<Integer> list = new ArrayList<>(mCurrentList);
        final BlobCache blobCache = BlobCacheManager.getInstance().getBlobCache(mCurrentBlobName, 100, 1024 * 1024 * 400, 1);
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (list.size() == 0) {
                    return;
                }

                Resources res = getResources();
                int id = list.remove(0);
                String name = res.getResourceName(id);
                name = name.split("\\/")[1];
                final String finalName = name;

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

                long t1 = System.currentTimeMillis();

                BytesBuffer bytesBuffer = BlobCacheUtil.getCacheDataByName(blobCache, finalName, mDataBuffer, key, mLookupRequest);
                if (bytesBuffer != null && bytesBuffer.data != null) {
                    mDataBuffer = bytesBuffer;
                    if (mPixelsBuffer == null || mPixelsBuffer.capacity() != bytesBuffer.data.length) {
                        mPixelsBuffer = ByteBuffer.allocate(bytesBuffer.data.length);
                    }
                    Bitmap bitmap = BlobCacheUtil.getCacheBitmapByData(bytesBuffer, mPixelsBuffer, null, mWidthBuffer, mHeightBuffer);

                    long now = System.currentTimeMillis();
                    final long time = now - t1;
                    if (bitmap == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mResultGet.setText(mResultGet.getText().toString() + finalName + " bitmap is null cost time=" + time + "\n");
                                mResultGetScrollView.fullScroll(View.FOCUS_DOWN);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mResultGet.setText(mResultGet.getText().toString() + finalName + " cost time=" + time + "\n");
                                mResultGetScrollView.fullScroll(View.FOCUS_DOWN);
                            }
                        });
                    }
                }

                mThreadHandler.postDelayed(this, TIME_INTERVAL);
            }
        });
    }

    private void androidGet() {
        final List<Integer> list = new ArrayList<>(mCurrentList);
        final BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        decodeOptions.inMutable = true;
        decodeOptions.inDensity = Bitmap.DENSITY_NONE;
        decodeOptions.inScaled = false;
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
                long t1 = System.currentTimeMillis();

                Bitmap bitmap = BitmapFactory.decodeResource(res, id, decodeOptions);
                long now = System.currentTimeMillis();
                final long time = now - t1;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mResultGet.setText(mResultGet.getText().toString() + finalName + " cost time=" + time + "\n");
                        mResultGetScrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
                mThreadHandler.postDelayed(this, TIME_INTERVAL);
            }
        });
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