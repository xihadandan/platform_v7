package com.wellsoft.context.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

/**
 * Description: UUID生成器
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年12月24日.1	Asus		2015年12月24日		Create
 * </pre>
 */
public class UUIDGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(UUIDGenerator.class);
    private static final int IP;
    private static final int JVM = (int) (System.currentTimeMillis() >>> 8);
    private static short counter = (short) 0;

    static {
        int ipadd;
        try {
            ipadd = toInt(InetAddress.getLocalHost().getAddress());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            ipadd = 0;
        }
        IP = ipadd;
    }

    /**
     * Unique across JVMs on this machine (unless they load this class
     * in the same quater second - very unlikely)
     */
    private static int getJVM() {
        return JVM;
    }

    /**
     * Unique in a millisecond for this JVM instance (unless there
     * are > Short.MAX_VALUE instances created in a millisecond)
     */
    private static short getCount() {
        synchronized (UUIDGenerator.class) {
            if (counter >= Short.MAX_VALUE) {
                counter = -1;
            }
            return ++counter;
        }
    }

    /**
     * Unique in a local network
     */
    private static int getIP() {
        return IP;
    }

    /**
     * Unique down to millisecond
     */
    private static short getHiTime() {
        return (short) (System.currentTimeMillis() >>> 32);
    }

    private static int getLoTime() {
        return (int) System.currentTimeMillis();
    }

    /**
     * 生成UUID
     *
     * @param obj
     * @return UUID
     */
    public static String generate(Object obj) {
        StringBuilder buffer = new StringBuilder(36).append(getIP()).append(getJVM()).append(getHiTime())
                .append(getLoTime()).append(getCount());// thread safe
        if (buffer.charAt(0) == '-') {
            buffer.deleteCharAt(0);
        }
        return buffer.toString();
    }

    /**
     * bytes[] to Int
     *
     * @param bytes
     * @return Int
     */
    public static int toInt(byte[] bytes) {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            result = (result << 8) - Byte.MIN_VALUE + (int) bytes[i];
        }
        return result;
    }
}