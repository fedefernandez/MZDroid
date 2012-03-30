package com.projectsexception.mzdroid.util;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

public class ImageLoader extends Thread {
    
    private static Object sLock = new Object();
    private static Queue<ImageView> queue = new LinkedList<ImageView>();
    private static ImageLoader imageLoader = new ImageLoader();
    private Map<String, SoftReference<Bitmap>> cache = new HashMap<String, SoftReference<Bitmap>>();
    
    private Context ctx;
    
    public void setContext(Context ctx) {
        this.ctx = ctx;
    }
    
    //Used to display bitmap in the UI thread
    static class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        ImageView imageView;
        public BitmapDisplayer(Bitmap b, ImageView i) {
            bitmap = b;
            imageView = i;
        }
        public void run() {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
    
    public static void requestPhoto(Context ctx, ImageView imageView) {
        synchronized (sLock) {
            queue.add(imageView);
            sLock.notifyAll();
            imageLoader.setContext(ctx);
            if (imageLoader.getState()==Thread.State.NEW) {
                imageLoader.start();
            }
        }
    }
    
    public static void stopThread() {
        imageLoader.interrupt();
        
    }
    
    private static boolean hasMore() {
        synchronized (sLock) {
            return !queue.isEmpty();
        }
    }
    
    private static ImageView getNext() {
        synchronized (sLock) {            
            return queue.poll();
        }
    }
    
    private static void addBitmapToView(Bitmap bitmap, ImageView imageView) {
        if (bitmap != null) {
            BitmapDisplayer bd = new BitmapDisplayer(bitmap, imageView);
            Activity a = (Activity) imageView.getContext();
            a.runOnUiThread(bd);
        }
    }

    @Override
    public void run() {
        ImageView photoToLoad;        
        while (true) {
            try {
                if (!hasMore()) {
                    synchronized (sLock) {
                        sLock.wait();
                    }
                }
                if (hasMore()) {
                    photoToLoad = getNext();
                    String url = (String) photoToLoad.getTag();
                    Bitmap bitmap = null;
                    if (cache.containsKey(url) && cache.get(url).get() != null) {
                        bitmap = cache.get(url).get();
                    } else {
                        bitmap = loadBitmap(ctx, url);
                    }
                    cache.put(url, new SoftReference<Bitmap>(bitmap));                    
                    addBitmapToView(bitmap, photoToLoad);
                }
                if(Thread.interrupted()) {
                    break;
                }
            } catch (InterruptedException e) {
                Log.w("aContags", "end ImageLoader task");
            }
        }        
    }
    
    public Bitmap loadBitmap(Context context, String URL) {
        CustomLog.debug("ImageLoader", "loadImageFromURL " + URL);
        Bitmap bitmap = null;
        try {
            URL url = new URL(URL);
            URLConnection conn = url.openConnection();
            if (conn.getClass().isAssignableFrom(HttpURLConnection.class)) {
                HttpURLConnection httpConn = (HttpURLConnection) conn;
                httpConn.setRequestMethod("GET");
                httpConn.connect();
    
                if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpConn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(inputStream);
                }
            } else {
                conn.connect();
                InputStream inputStream = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            }
        } catch (Exception ex) {
            CustomLog.error("ImageLoader", ex.getMessage());
        }
        return bitmap;
    }

}
