/*
 * @(#)2016年7月5日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.repository.support;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Description: MD5校验,可自动拓展缓存大小、防止OutputStream的断流现象
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
public class CheckedBufferOutputStream extends BufferedOutputStream {

    public static final int DEFAULT_CHECKED_SIZE = 8 * 1024 * 1024; // 8M,单个项目确认后尽量不要变

    public static final int DEFAULT_BUFFER_SIZE = 8 * 1024; // 8K

    /**
     * The current position in the out. This is the index of the next
     * character to be write to the <code>out</code> OutputStream.
     * <p>
     *
     * @see java.io.FilterOutputStream#out
     */
    protected int pos;

    /**
     * MD5校验的字节数
     */
    protected int checkedSize;

    private MessageDigest _messageDigester = null;

    private String _md5 = null;

    /**
     * 如何描述该构造方法
     *
     * @param out
     * @throws NoSuchAlgorithmException
     */
    public CheckedBufferOutputStream(OutputStream out) throws NoSuchAlgorithmException {
        this(out, DEFAULT_CHECKED_SIZE);
    }

    /**
     * 如何描述该构造方法
     *
     * @param out
     * @param checkedSize
     * @throws NoSuchAlgorithmException
     */
    public CheckedBufferOutputStream(OutputStream out, int checkedSize) throws NoSuchAlgorithmException {
        super(out, DEFAULT_BUFFER_SIZE);
        if (checkedSize <= 0) {
            throw new IllegalArgumentException("Checked size <= 0");
        }
        this.pos = 0;
        this.checkedSize = checkedSize;
        _messageDigester = MessageDigest.getInstance("MD5");
        _messageDigester.reset(); // MD5 reset
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
        String fileOutput = "D:\\text-out.txt";
        InputStream input = null;
        CheckedBufferOutputStream output = null;
        try {
            long start = System.currentTimeMillis();
            input = new CheckedBufferInputStream(new FileInputStream(fileName));
            String md5 = ((CheckedBufferInputStream) input).getMd5();
            long end = System.currentTimeMillis();
            System.out.println("File[" + fileName + "]MD5:" + md5 + " space----:" + (end - start));// logger
            output = new CheckedBufferOutputStream(new FileOutputStream(fileOutput));
            byte[] b = new byte[1024];
            int size = -1;
            while ((size = input.read(b, 0, b.length)) > -1) {
                output.write(b, 0, size);
            }
            output.flush();
            System.out.println("File[" + fileOutput + "]MD5:" + output.getMd5());// logger
            String md51 = output.getMd5();
            String md52 = output.getMd5();
            IOUtils.closeQuietly(input);
            input = new FileInputStream(fileOutput);
            System.out.println(StringUtils.abbreviate(IOUtils.toString(input), 255));// logger
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
        }
    }

    /**
     * Flush the internal buffer
     */
    private void flushBuffer() throws IOException {
        if (count > 0) {
            out.write(buf, 0, count);
            updateDigest(buf, 0, count);// update Digest
            count = 0;
        }
    }

    /**
     * update the internal digest
     */
    private void updateDigest(byte[] b, int off, int len) throws IOException {
        if (pos < checkedSize) {
            int asize = Math.min(checkedSize - pos, len);
            _messageDigester.update(b, 0, asize);// MD5 update
            pos += len;
        }
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see java.io.BufferedOutputStream#write(int)
     */
    @Override
    public synchronized void write(int b) throws IOException {
        if (count >= buf.length) {
            flushBuffer();
        }
        buf[count++] = (byte) b;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see java.io.BufferedOutputStream#write(byte[], int, int)
     */
    @Override
    public synchronized void write(byte[] b, int off, int len) throws IOException {
        if (len >= buf.length) {
			/* If the request length exceeds the size of the output buffer,
			       flush the output buffer and then write the data directly.
			       In this way buffered streams will cascade harmlessly. */
            flushBuffer();
            out.write(b, off, len);
            updateDigest(b, off, len); // update Digest
            return;
        }
        if (len > buf.length - count) {
            flushBuffer();
        }
        System.arraycopy(b, off, buf, count, len);
        count += len;
    }

    /**
     * Closes this output stream and releases any system resources
     * associated with the stream.
     * <p>
     * The <code>close</code> method of <code>CheckedOutputStream</code>
     * calls its <code>flush</code> method, and then calls the
     * <code>close</code> method of its underlying output stream.
     *
     * @throws IOException if an I/O error occurs.
     * @see java.io.CheckedBufferOutputStream#flush()
     * @see java.io.CheckedBufferOutputStream#out
     */
    public void close() throws IOException {
        super.close();
        _md5 = null;
        _messageDigester = null;
    }

    /**
     * Flushes this buffered output stream. This forces any buffered
     * output bytes to be written out to the underlying output stream.
     *
     * @throws IOException if an I/O error occurs.
     * @see java.io.FilterOutputStream#out
     */
    public synchronized void flush() throws IOException {
        flushBuffer();
        out.flush();
        _md5 = CheckedBufferOutputStream.toHex(_messageDigester.digest());// // The digest is reset after this call is made.
    }

    /**
     * 返回MD5签名,写完数据且flush之后、close之前调
     *
     * @return 流的MD5签名, 如果流大于8M, 则返回输流的前8M签名
     */
    public String getMd5() {
        return _md5;// 返回MD5
    }
}
