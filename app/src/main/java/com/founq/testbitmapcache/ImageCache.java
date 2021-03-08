package com.founq.testbitmapcache;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.util.LruCache;

import com.founq.testbitmapcache.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by ring on 2021/3/8.
 * 三级缓存
 */
public class ImageCache {

    private static ImageCache instance;
    private Context mContext;

    public static ImageCache getInstance() {
        if (null == instance) {
            synchronized (ImageCache.class) {
                if (null == instance) {
                    instance = new ImageCache();
                }
            }
        }
        return instance;
    }

    //内存缓存
    private LruCache<String, Bitmap> memoryCache;
    //磁盘缓存，第三方库，下载路径：https://github.com/JakeWharton/DiskLruCache
    private DiskLruCache mDiskLruCache;
    BitmapFactory.Options mOptions = new BitmapFactory.Options();

    //复用池
    public static Set<WeakReference<Bitmap>> reusablePool;

    //参数dir就是最好存储的磁盘缓存的路径
    public void init(Context context, String dir) {
        reusablePool = Collections.synchronizedSet(new HashSet<WeakReference<Bitmap>>());
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int memoryClass = activityManager.getMemoryClass();//内存的可用内存

        //取可用内存的八分之一
        memoryCache = new LruCache<String, Bitmap>(memoryClass / 8 * 1024 * 1024) {
            /**
             *
             * @param key
             * @param value
             * @return value占用的内存大小
             */
            @Override
            protected int sizeOf(String key, Bitmap value) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//兼容
                    return value.getAllocationByteCount();//申请的字节数//19以后，图片的大小小于等于原先图片大小即可复用
                }
                return value.getByteCount();//19以前，必须图片大小完全一致才可以复用
            }

            /**
             *
             * @param evicted
             * @param key
             * @param oldValue 被挤出来的数据
             * @param newValue 新添加的数据
             */
            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                //oldValue需要放入到复用池中
                if (oldValue.isMutable()) {
                    reusablePool.add(new WeakReference<Bitmap>(oldValue, getReferenceQueue()));
                } else {
                    oldValue.recycle();
                }
            }
        };

        try {
            //valueCount一般是存文件;appVersion版本号
            mDiskLruCache = DiskLruCache.open(new File(dir), 1, 1, 10 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //引用队列
    ReferenceQueue mReferenceQueue;
    Thread clearReferenceQueue;
    boolean shutDown;

    //优化，做了之后可以兼容不同的android版本
    //用于主动监听GC的API，加快回收
    private ReferenceQueue<Bitmap> getReferenceQueue() {
        if (null == mReferenceQueue) {
            mReferenceQueue = new ReferenceQueue<Bitmap>();
            clearReferenceQueue = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!shutDown) {
                        try {
                            //remove带阻塞功能的
                            Reference<Bitmap> reference = mReferenceQueue.remove();
                            Bitmap bitmap = reference.get();
                            if (null != bitmap && !bitmap.isRecycled()) {
                                //加快回收
                                bitmap.recycle();//有的在java层做，会非常慢，调用了recycle就到了native层，速度就很快
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            clearReferenceQueue.start();
        }
        return mReferenceQueue;
    }

    public void putBitmapToMemory(String key, Bitmap bitmap) {
        memoryCache.put(key, bitmap);
    }

    public Bitmap getBitmapFormMemory(String key) {
        return memoryCache.get(key);
    }

    public void clearMemoryCache() {
        memoryCache.evictAll();
    }

    public Bitmap getReusable(int w, int h, int inSampleSize) {
        //3.0以下不理会
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return null;
        }
        Bitmap reusable = null;
        Iterator<WeakReference<Bitmap>> iterator = reusablePool.iterator();
        while (iterator.hasNext()) {
            Bitmap bitmap = iterator.next().get();
            if (null != bitmap) {
                //可以复用
                if (checkInBitmap(bitmap, w, h, inSampleSize)) {
                    reusable = bitmap;
                    iterator.remove();
                    Log.i("test", "复用池中找到了");
                    break;
                } else {
                    iterator.remove();
                }
            }
        }
        return reusable;
    }

    private boolean checkInBitmap(Bitmap bitmap, int w, int h, int inSampleSize) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return bitmap.getWidth() == w && bitmap.getHeight() == h && inSampleSize == 1;
        }
        if (inSampleSize >= 1) {
            w /= inSampleSize;
            h /= inSampleSize;
        }
        int byteCount = w * h * getPixelsCount(bitmap.getConfig());
        return byteCount <= bitmap.getAllocationByteCount();
    }

    /**
     * 用于获取像素点的不同格式所需要的字节数
     *
     * @param config
     * @return
     */
    private int getPixelsCount(Bitmap.Config config) {
        if (config == Bitmap.Config.ARGB_8888) {
            return 4;
        }
        return 2;
    }

    //磁盘缓存的处理
    //加入磁盘缓存
    public void putBitmapToDisk(String key, Bitmap bitmap) {
        DiskLruCache.Snapshot snapshot = null;
        OutputStream outputStream = null;
        try {
            snapshot = mDiskLruCache.get(key);
            //如果缓存中已经有这个文件，不理他
            if (null == snapshot) {
                //如果没有这个文件，就生成这个文件
                DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                if (null != editor) {
                    outputStream = editor.newOutputStream(0);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
                    editor.commit();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != snapshot) {
                snapshot.close();
            }
            if (null != outputStream) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //从磁盘缓存中取
    public Bitmap getBitmapFromDisk(String key, Bitmap reusable) {
        DiskLruCache.Snapshot snapshot = null;
        Bitmap bitmap = null;
        try {
            snapshot = mDiskLruCache.get(key);
            if (null == snapshot) {
                return null;
            }
            //获取文件输入流，读取bitmap
            InputStream inputStream = snapshot.getInputStream(0);
            //解码个图片，写入
            mOptions.inMutable = true;
            mOptions.inBitmap = reusable;
            bitmap = BitmapFactory.decodeStream(inputStream, null, mOptions);
            if (null != bitmap) {
                memoryCache.put(key, bitmap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != snapshot) {
                snapshot.close();
            }
        }
        return bitmap;
    }
}
