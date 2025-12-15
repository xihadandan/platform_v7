package com.wellsoft.pt.mt.dbmigrate;

import org.apache.commons.io.FileUtils;
import org.mozilla.universalchardet.CharsetListener;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;

/*
 * @(#)2016年3月22日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */

/**
 * Description: 将目录的所有SQL文件编码转换为UTF-8
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月22日.1	zhongzh		2016年3月22日		Create
 * </pre>
 * @date 2016年3月22日
 */
public class CharsetUtils {

    public final static String DEFAULT_ANSI_CHARSET = "GBK";

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            CharsetUtils.err("usage: CharsetUtils {dbTypeDir} [Charset]\nexample:CharsetUtils xxxx.txt GBK");
            return;
        }
        String dir = args[0];
        Charset toCharset = null;
        if (args.length > 1 && args[1] != null) {
            try {
                toCharset = Charset.forName(args[1]);
            } catch (Exception cause) {
                // ignore
                toCharset = Charset.forName("UTF-8");
                CharsetUtils.err("error load Charset[" + args[1] + "]", cause);
            }
        }
        CharsetUtils.log("process dir:" + dir);
        File file = new File(dir);
        if (file.exists() == false) {
            CharsetUtils.err("File[" + dir + "] Not Exist");
            return;
        }
        CharsetUtils.log("defaultCharset : " + toCharset);
        CharsetUtils.log("");
        CharsetUtils.log("");
        CharsetUtils.processDir(file, toCharset);
    }

    public static void processDir(File dir, Charset toCharset) throws IOException {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".sql") || name.endsWith(".tpl") || (new File(name)).isDirectory();
                }
            });
            if (files == null || files.length <= 0) {
                return;
            }
            for (File file : files) {
                processDir(file, toCharset);
            }
        } else if (dir.isFile()) {
            CharsetUtils.processFile(dir, toCharset);
            // Txt2Charset.log(dir.getName() + ":" + IOUtils.toString(new FileInputStream(dir), "GBK"));
        }
    }

    public static void processFile(File file, Charset toCharset) throws IOException {
        if (file.exists() == false || file.isFile() == false) {
            return;
        }
        String detectCharset = detectCharset(file);
        if (detectCharset.equals(toCharset.name())) {
            CharsetUtils.log("File[" + file.getPath() + "]ignore Process");
            return;
        }
        File bakFile = new File(file.getPath() + ".~bak");
        // Txt2Charset.log(tmpFile.getPath());
        FileUtils.copyFile(file, bakFile);
        try {
            FileUtils.deleteQuietly(file);
            FileUtils.writeStringToFile(file, FileUtils.readFileToString(bakFile, detectCharset), toCharset, false);
            CharsetUtils.log("File[" + file.getName() + "]detectCharset[" + detectCharset + "] done");
        } catch (Exception cause) {
            CharsetUtils.err("File[" + file.getName() + "]process Fail : " + cause.getMessage(), cause);
            if (bakFile.exists()) {
                FileUtils.copyFile(bakFile, file);// 回滚
            }
            return;// ignore throwable
        } finally {
            FileUtils.deleteQuietly(bakFile);
        }
    }

    public static String detectCharset(File file) throws IOException {
        UniversalDetector detector = new UniversalDetector(new CharsetListener() {
            public void report(String name) {
                // Txt2Charset.log("detecting charset = " + name);
            }
        });

        byte[] buf = new byte[4096];
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            int nread;
            while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
            detector.dataEnd();
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
        // 在简体中文Windows操作系统中，ANSI 编码代表 GBK 编码；在繁体中文Windows操作系统中，ANSI编码代表Big5；在日文Windows操作系统中，ANSI 编码代表 Shift_JIS 编码。
        return detector.getDetectedCharset() == null ? DEFAULT_ANSI_CHARSET : detector.getDetectedCharset();
    }

    public static void log(String message, Object... obj) {
        System.out.println(message);
    }

    public static void err(String message, Object... obj) {
        System.err.println(message);
    }
}
