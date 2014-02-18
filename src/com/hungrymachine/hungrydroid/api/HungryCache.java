package com.hungrymachine.hungrydroid.api;

import com.hungrymachine.hungrydroid.utils.HungryLogger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import com.github.droidfu.cachefu.*;

/**
 * Extends AbstractCache as a key/value store for String values.
 * Overrides AbstractCache's default time limit-based cache invalidation and instead
 *
 * @author davesims
 */
public class HungryCache extends AbstractCache<String, String> {

    private static final String name = "HungryCache";
    private static final int INITIAL_CAPACITY = 100;
    private static final int MAX_CONCURRENT_THREADS = 2;
    private static final int EXPIRATION_IN_MINUTES = 60; // will never last this long

    private static class SingletonHolder {
        public static HungryCache instance = new HungryCache();
    }

    public static HungryCache instance() {
        return SingletonHolder.instance;
    }

    private HungryCache() {
        super(name, INITIAL_CAPACITY, EXPIRATION_IN_MINUTES, MAX_CONCURRENT_THREADS);
    }

    @Override
    public String getFileNameForKey(String key) {
        return CacheHelper.getFileNameFromUrl(key);
    }

    @Override
    protected synchronized String readValueFromDisk(File file) throws IOException {
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        long fileSize = file.length();

        if (fileSize > Integer.MAX_VALUE) {
            throw new IOException("Cannot read files larger than " + Integer.MAX_VALUE + " bytes");
        }

        byte[] data = new byte[(int) fileSize];
        inputStream.read(data, 0, (int) fileSize);
        inputStream.close();
        return new String(data);
    }

    @Override
    protected synchronized void writeValueToDisk(File file, String data) throws IOException {
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
        outputStream.write(data.getBytes());
        outputStream.close();
    }

    /**
     * Removes all data from the MEM and DISK caches.
     */
    public static void clearCache() {
        instance().clear();
    }

    /**
     * Return a cached String value from the cache for the given key.
     *
     * @param key
     * @return
     */
    public static String getCachedValue(String key) {
        return instance().get(key);
    }

    /**
     * Return a cached String value from the cache for the given key, if it exists, ignoring validation rules.
     *
     * @param key
     * @return
     */
    public static String getValueWithoutValidation(String key) {
//        TODO: implement getting value without validation
//        return instance().getValueIgnoreValidation(key);
      return null;
    }

}

