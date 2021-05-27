package com.asimpson.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.session.MediaSession;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/*LRU strategy implemented to achieve Preloading and Caching. Using the cache, images previously viewed
* are preloaded resulting in a quicker loading time. */

public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;
    private static final int MESSAGE_PRELOAD = 1;
    private static final int CACHE_SIZE = 400; //declaration of the cache size

    private boolean mHasQuit = false;
    private Handler mRequestHandler;
    private ConcurrentMap<T,String> mRequestMap = new ConcurrentHashMap<>();

    private final Handler mResponseHandler;
    private ThumbnailDownloadListener<T> mThumbnailDownloadListener;

    private LruCache<String, Bitmap> memoryCache; //declaring an object from the LruCache class


    public interface ThumbnailDownloadListener<T> {
        void onThumbnailDownloaded(T target, Bitmap thumbnail);
    }
    public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> listener) {
        mThumbnailDownloadListener = listener;
    }

    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
        memoryCache = new LruCache<String, Bitmap>(CACHE_SIZE);
    }

    @Override
    protected void onLooperPrepared() {
        mRequestHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    T target = (T) msg.obj;
                    Log.i(TAG, "Got a request for URL: " + mRequestMap.get(target));
                    handleRequest(target);
                } else if(msg.what == MESSAGE_PRELOAD){
                    String url = (String)msg.obj;
                    preload(url);
                }
            }
        };
    }

    private void preload(final T token) {
        String url = mRequestMap.get(token);
        preload(url);
    }

    private void preload(String url) {
        if(url == null)
            return;
        if (memoryCache.get(url) != null)
            return;
        Bitmap bitmap = getBitmap(url);
        if (bitmap != null)
            memoryCache.put(url, bitmap);
    }

    public void queuePreload(String url) {
        if (memoryCache.get(url) != null)return;

        mRequestHandler
                .obtainMessage(MESSAGE_PRELOAD, url)
                .sendToTarget();
    }

    public Bitmap checkCache(String url) {
        return memoryCache.get(url);
    }

    private Bitmap getBitmap(String url) {
        try {
            byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
            Bitmap bitmapDecode = BitmapFactory
                    .decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            Log.i(TAG, "bitmap created");
            return bitmapDecode;
        } catch (IOException ioe) {
            Log.e(TAG, "Error downloading image", ioe);
        }
        return null;
    }

    private void handleRequest(final T target) {
        try {
            final String url = mRequestMap.get(target);
            if (url == null) {
                return;
            }
            byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory
                    .decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            Log.i(TAG, "Bitmap created");

            mResponseHandler.post(new Runnable() {
                public void run() {
                    if (mRequestMap.get(target) != url ||
                            mHasQuit) {
                        return;
                    }
                    mRequestMap.remove(target);
                    mThumbnailDownloadListener.onThumbnailDownloaded(target, bitmap);
                }
            });
        } catch (IOException ioe) {
            Log.e(TAG, "Error downloading image", ioe);
        }
    }

    @Override
    public boolean quit() {
        mHasQuit = true;
        return super.quit();
    }
    public void queueThumbnail(T target, String url) {
        Log.i(TAG, "Got a URL: " + url);

        if (url == null) {
            mRequestMap.remove(target);
        } else {
            mRequestMap.put(target, url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target)
                    .sendToTarget();
        }
    }

    public void clearQueue() {
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
        mRequestMap.clear();
    }

}
