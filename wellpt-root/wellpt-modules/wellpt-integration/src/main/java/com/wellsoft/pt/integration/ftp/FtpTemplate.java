package com.wellsoft.pt.integration.ftp;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class FtpTemplate {

    public FtpClientFactory fc = new FtpClientFactory();
    protected Logger logger = LoggerFactory.getLogger(getClass());

    public int write(String filename, InputStream is) {
        FTPClient ftp = fc.getWrite();
        try {
            ftp.storeFile(filename, is);
        } catch (IOException ex) {
            logger.info("error while writing [" + filename + "]", ex);
            return -1;
        } finally {
            IOUtils.closeQuietly(is);// no
        }
        return 1;
    }

    public int write(String filename, byte[] content) {
        return write(filename, new ByteArrayInputStream(content));
    }

    public int wirte(String filename, String content) throws UnsupportedEncodingException {
        if (content == null) {
            content = "";
        }
        return write(filename, content.getBytes("UTF-8"));
    }

    public OutputStream read(String filename) {
        OutputStream os = new ByteArrayOutputStream();
        FTPClient ftp = fc.getRead();
        try {
            ftp.retrieveFile(filename, os);
            // os.flush(); // no flush
        } catch (IOException ex) {
            logger.info("error while reading [" + filename + "]", ex);
            return null;
        } finally {
            IOUtils.closeQuietly(os);// no
        }
        return os;
    }

    public byte[] readByte(String filename) {
        ByteArrayOutputStream os = (ByteArrayOutputStream) read(filename);
        return os.toByteArray();
    }

    public String readString(String filename) throws UnsupportedEncodingException {
        ByteArrayOutputStream os = (ByteArrayOutputStream) read(filename);
        return os.toString("UTF-8");
    }
}
