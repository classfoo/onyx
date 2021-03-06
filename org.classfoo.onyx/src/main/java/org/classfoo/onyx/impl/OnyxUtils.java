package org.classfoo.onyx.impl;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * Onyx Utility Class
 * @author ClassFoo
 *
 */
public class OnyxUtils {

    public static final Logger logger = LoggerFactory.getLogger(OnyxUtils.class);

    /**
     * return error information map to front ends
     * @param message
     * @param params
     * @return
     */
    public static Map<String, Object> returnError(String message, Object... params) {
        HashMap<String, Object> error = new HashMap<String, Object>(2);
        error.put("type", "error");
        error.put("message", message);
        logger.error(message);
        return error;
    }

    /**
     * return error information map to front ends
     * @param args
     * @param message
     * @param params
     * @return
     */
    public static Map<String, Object> returnError(Map<String, Object> args, String message, Object... params) {
        HashMap<String, Object> error = new HashMap<String, Object>(args.size() + 2);
        error.putAll(args);
        error.put("type", "error");
        error.put("message", message);
        logger.error(message);
        return error;
    }

    /**
     * 获取压缩后的uuid
     * @return
     */
    public static String getCompressedUUID() {
        UUID uuid = UUID.randomUUID();
        return getCompressedUUID(uuid);
    }

    private static String getCompressedUUID(UUID uuid) {
        byte[] byUuid = new byte[16];
        long least = uuid.getLeastSignificantBits();
        long most = uuid.getMostSignificantBits();
        long2bytes(most, byUuid, 0);
        long2bytes(least, byUuid, 8);
        return Base64.encodeBase64URLSafeString(byUuid);
    }

    private static void long2bytes(long value, byte[] bytes, int offset) {
        for (int i = 7; i > -1; i--) {
            bytes[offset++] = (byte) ((value >> 8 * i) & 0xFF);
        }
    }

    /**
     * create random uuid with given prefix
     * @param prefix
     * @return
     */
    public static String getRandomUUID(String prefix) {
        return prefix + UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * read json object from web request parameter map
     * @param options
     * @param key
     * @param type
     * @return
     */
    public static <T> T readJson(Map<String, Object> options, String key, Class<T> type) {
        try {
            String v = MapUtils.getString(options, key);
            if (String.class.equals(type)) {
                return type.cast(v);
            }
            ObjectMapper om = new ObjectMapper();
            T list = om.readValue(v, type);
            return list;
        }
        catch (Exception e) {
            throw new RuntimeException("parse json failed!", e);
        }
    }

    private static final String[] COLORRGBS = { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "0A", "0B",
            "0C", "0D", "0E", "0F", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "1A", "1B", "1C", "1D",
            "1E", "1F", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "2A", "2B", "2C", "2D", "2E", "2F",
            "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "3A", "3B", "3C", "3D", "3E", "3F", "40", "41",
            "42", "43", "44", "45", "46", "47", "48", "49", "4A", "4B", "4C", "4D", "4E", "4F", "50", "51", "52", "53",
            "54", "55", "56", "57", "58", "59", "5A", "5B", "5C", "5D", "5E", "5F", "60", "61", "62", "63", "64", "65",
            "66", "67", "68", "69", "6A", "6B", "6C", "6D", "6E", "6F", "70", "71", "72", "73", "74", "75", "76", "77",
            "78", "79", "7A", "7B", "7C", "7D", "7E", "7F", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89",
            "8A", "8B", "8C", "8D", "8E", "8F", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "9A", "9B",
            "9C", "9D", "9E", "9F", "A0", "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "AA", "AB", "AC", "AD",
            "AE", "AF", "B0", "B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8", "B9", "BA", "BB", "BC", "BD", "BE", "BF",
            "C0", "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9", "CA", "CB", "CC", "CD", "CE", "CF", "D0", "D1",
            "D2", "D3", "D4", "D5", "D6", "D7", "D8", "D9", "DA", "DB", "DC", "DD", "DE", "DF", "E0", "E1", "E2", "E3",
            "E4", "E5", "E6", "E7", "E8", "E9", "EA", "EB", "EC", "ED", "EE", "EF", "F0", "F1", "F2", "F3", "F4", "F5",
            "F6", "F7", "F8", "F9", "FA", "FB", "FC", "FD", "FE", "FF" };

    /**
     * get random color string for html css
     * @return
     */
    public static final String getRandomColor() {
        Random rand = new Random();
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        Color randomColor = new Color(r, g, b);
        return '#' + int2hex(randomColor.getRed()) + int2hex(randomColor.getGreen()) + int2hex(randomColor.getBlue());
    }

    private static String int2hex(int c) {
        return COLORRGBS[c];
    }

    /**
     * remove blanks in text
     * @param text
     * @return
     */
    public static String removeBlank(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        return text.replaceAll("/s*", "");
    }

    /**
     * Integer.SIZE = 32;
     */
    static int MAX_POWER_OF_TWO = 1 << (Integer.SIZE - 2);

    /**
     * smallest hash map size
     */
    static int SMALLEST_HASHMAP_SIZE = 3;

    /**
     * Guaua like HashMap initialCapacity size，base on default 0.75 loadFactor，in order to avoid rehash
     * @param expectedSize
     * @return
     */
    public static <K, V> HashMap<K, V> newHashMapWithExpectedSize(int expectedSize) {
        return new HashMap<K, V>(capacity(expectedSize));
    }

    static int capacity(int expectedSize) {//expectedSize = 7,return 10
        if (expectedSize < SMALLEST_HASHMAP_SIZE) {
            checkNonnegative(expectedSize);
            return expectedSize + 1;
        }
        if (expectedSize < MAX_POWER_OF_TWO) {
            // This is the calculation used in JDK8 to resize when a putAll  
            // happens; it seems to be the most conservative calculation we  
            // can make.  0.75 is the default load factor.  
            return (int) ((float) expectedSize / 0.75F + 1.0F);
        }
        // any large value
        return Integer.MAX_VALUE;
    }

    private static int checkNonnegative(int expectedSize) {
        if (expectedSize < 0) {
            throw new RuntimeException("expectedSize<0!");
        }
        return expectedSize;
    }

    /**
     * global thread pool
     */
    private static ExecutorService THREADPOOL = null;

    static {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("pool-%d").build();
        THREADPOOL = new ThreadPoolExecutor(4, 8, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(64),
                namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * Run single runnable
     * @param runnable
     */
    public static void run(Runnable runnable) {
        THREADPOOL.execute(runnable);
    }
}
