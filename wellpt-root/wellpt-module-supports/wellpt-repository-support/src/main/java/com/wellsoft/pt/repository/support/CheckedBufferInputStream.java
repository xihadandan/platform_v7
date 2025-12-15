/*
 * @(#)2016年7月5日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.repository.support;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Description: MD5校验,可自动拓展缓存大小、防止InputStream的断流现象
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年7月5日.1	zhongzh		2016年7月5日		Create
 * </pre>
 * @date 2016年7月5日
 */
public class CheckedBufferInputStream extends BufferedInputStream {

    public static final int DEFAULT_CHECKED_SIZE = 8 * 1024 * 1024; // 8M,单个项目确认后尽量不要变

    public static final int DEFAULT_BUFFER_SIZE = 8 * 1024; // 8K

    private String _md5 = null;

    /**
     * Creates a <code>CheckedInputStream</code>
     * and saves its  argument, the input stream
     * <code>in</code>, for later use. An internal
     * buffer array is created and  stored in <code>buf</code>.
     *
     * @param in the underlying input stream.
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public CheckedBufferInputStream(InputStream in) throws IOException, NoSuchAlgorithmException {
        this(in, DEFAULT_CHECKED_SIZE);
    }

    /**
     * Creates a <code>CheckedInputStream</code>
     * with the specified buffer size,
     * and saves its  argument, the input stream
     * <code>in</code>, for later use.  An internal
     * buffer array of length  <code>size</code>
     * is created and stored in <code>buf</code>.
     *
     * @param in          the underlying input stream.
     * @param checkedSize the checked size.
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws IllegalArgumentException if size <= 0.
     */
    private CheckedBufferInputStream(InputStream in, int checkedSize) throws IOException, NoSuchAlgorithmException {
        super(in, DEFAULT_BUFFER_SIZE);
        if (checkedSize <= 0) {
            throw new IllegalArgumentException("Checked size <= 0");
        }
        MessageDigest _messageDigester = MessageDigest.getInstance("MD5");
        _messageDigester.reset(); // MD5 reset

        super.mark(checkedSize);// readlimit：自动拓展缓存最大值
        long skip = -1;
        // (缓存没有超过最大值 && 还有字节可读)--->读到没有可读字节
        while ((skip = super.skip(checkedSize - count)) > 0) { // skip==0 没有可读字节,count < checkedSize &&
            // System.out.println("count[" + count + "]pos[" + pos + "]skip[" + skip + "]"); // logger
        }
        int asize = count > checkedSize ? checkedSize : count; // 实际(action)须交验字节数大小
        _messageDigester.update(buf, 0, asize);
        _md5 = CheckedBufferInputStream.toHex(_messageDigester.digest()); // The digest is reset after this call is made.
        _messageDigester = null;
        // System.out.println("before reset:count[" + count + "]pos[" + pos + "]markpos[" + markpos + "]");// logger
        super.reset();
        // System.out.println("after reset:count[" + count + "]pos[" + pos + "]markpos[" + markpos + "]");// logger
    }

    public static String toHex(byte b[]) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            String s = Integer.toHexString(0xff & b[i]);

            if (s.length() < 2) {
                sb.append("0");
            }
            sb.append(s);
        }
        return sb.toString();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        String fileName = "D:\\text.txt";
        InputStream input = null;
        byte[] data = new byte[]{'A', 'B', 'C'};
        OutputStream output = null;
        int suffix = 5;
        // 0:518886cb70c1c119c99ad1bbb5865e7a
        // 1:518886cb70c1c119c99ad1bbb5865e7a
        // 2:abca94ef4a03d818092ca12a5e7e2a05
        // 3:83eecae2653e3c56a7fab1b8cfaed969
        // 4:ee01441540d0a968435cc39884f19631
        // 5:cb365b899921cfe003e3d91d54c27a5a
        // ...
        try {
            output = new CheckedBufferOutputStream(new FileOutputStream(fileName));
            for (int i = 0; i < DEFAULT_CHECKED_SIZE - suffix; i++) {
                output.write('A');
            }
            output.write(data, 0, 3);
            output.flush();
            System.out.println("File[" + fileName + "]MD5:" + ((CheckedBufferOutputStream) output).getMd5());// logger
        } finally {
            IOUtils.closeQuietly(output);
        }

        try {
            long start = System.currentTimeMillis();
            input = new CheckedBufferInputStream(new FileInputStream(fileName));
            System.out.println(StringUtils.abbreviate(IOUtils.toString(input), 255));// logger
            long end = System.currentTimeMillis();
            String md5 = ((CheckedBufferInputStream) input).getMd5();
            String md51 = ((CheckedBufferInputStream) input).getMd5();
            String md52 = ((CheckedBufferInputStream) input).getMd5();
            System.out.println("File[" + fileName + "]MD5:" + md5 + " space----:" + (end - start));// logger
            System.out.println("File[" + fileName + "]MD51:" + md51 + " space----:" + (end - start));// logger
            IOUtils.closeQuietly(input);

            input = new FileInputStream(fileName);
            int avail = Math.min(input.available(), DEFAULT_CHECKED_SIZE);
            byte[] b = new byte[avail];
            if (input.read(b, 0, avail) < 0) {
                System.err.println("error");
            }
            System.out.println("Common Digest:" + DigestUtils.md5Hex(b));
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    /**
     * Closes this input stream and releases any system resources
     * associated with the stream.
     * Once the stream has been closed, further read(), available(), reset(),
     * or skip() invocations will throw an IOException.
     * Closing a previously closed stream has no effect.
     *
     * @throws IOException if an I/O error occurs.
     */
    public void close() throws IOException {
        super.close();
        _md5 = null;
    }

    /**
     * 返回MD5签名,构造函数后、close之前调
     *
     * @return 流的MD5签名, 如果流大于8M, 则返回输流的前8M签名
     */
    public String getMd5() {
        return _md5;// 返回MD5
    }
}
