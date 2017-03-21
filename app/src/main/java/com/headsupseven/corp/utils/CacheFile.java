package com.headsupseven.corp.utils;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.iainconnor.objectcache.BuildConfig;
import com.iainconnor.objectcache.CacheManager;
import com.iainconnor.objectcache.DiskCache;

import java.io.File;
import java.lang.reflect.Type;

/**
 * Created by Siam on 9/25/2016.
 */
public class CacheFile {


    public static CacheManager Cache(Context context) {
        String cachePath = context.getCacheDir().getPath();
        DiskCache diskCache = null;
        File cacheFile = new File(cachePath + File.separator + BuildConfig.PACKAGE_NAME);
        try {
             diskCache = new DiskCache(cacheFile, BuildConfig.VERSION_CODE, 1024 * 1024 * 10);

        } catch (Exception e) {

        }
        CacheManager cacheManager = CacheManager.getInstance(diskCache);
        return cacheManager;
    }
    public  static String getCache(String key,Context mContext) {
        String resposne = "";
        Type myObjectType = new TypeToken<String>() {
        }.getType();
        Object myObject = CacheFile.Cache(mContext).get(key, String.class, myObjectType);
        if (myObject != null) {
            resposne = myObject.toString();
        } else {
            resposne = "";
        }
        return resposne;
    }

}
