package com.wellsoft.pt.integration.support;

import org.apache.commons.io.IOUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;

public class FileUitl {

    /**
     * 将文件转成base64 字符串
     *
     * @param path文件路径
     * @return *
     * @throws Exception
     */

    public static String encodeBase64File(String path) throws Exception {
        File file = new File(path);

        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return new BASE64Encoder().encode(buffer);

    }

    /**
     * 将文件转成base64 字符串
     *
     * @param path文件路径
     * @return *
     * @throws Exception
     */

    public static String encodeBase64File(InputStream is) throws IOException {

        byte[] data = null;
        String content = "";
        // 读取图片字节数组
        int size = 0;
        data = new byte[1000];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while ((size = is.read(data)) != -1) {
            baos.write(data, 0, size);
        }
        try {
            content = new BASE64Encoder().encode(baos.toByteArray());
            is.close();
            baos.close();
        } catch (IOException e) {
            throw e;
        }
        return content;

    }

    /**
     * 将base64字符解码保存文件
     *
     * @param base64Code
     * @param targetPath
     * @throws Exception
     */

    public static void decoderBase64File(String base64Code, String targetPath) throws Exception {
        byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
        FileOutputStream out = new FileOutputStream(targetPath);
        out.write(buffer);
        out.close();

    }

    /**
     * 将base64字符解码转为InputStream
     *
     * @param base64Code
     * @throws Exception
     */

    public static InputStream decoderBase64ToInputStream(String base64Code) throws Exception {
        byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
        InputStream sbs = new ByteArrayInputStream(buffer);
        return sbs;
    }

    /**
     * 将base64字符保存文本文件
     *
     * @param base64Code
     * @param targetPath
     * @throws Exception
     */

    public static void toFile(String base64Code, String targetPath) throws Exception {

        byte[] buffer = base64Code.getBytes();
        FileOutputStream out = new FileOutputStream(targetPath);
        out.write(buffer);
        out.close();
    }

    public static void writeReadMeToFolder(File folder) {
        File readmeFile = new File(folder, "readme.txt");
        FileOutputStream localFileOutputStream = null;
        try {
            if (!readmeFile.exists()) {
                readmeFile.createNewFile();
                localFileOutputStream = new FileOutputStream(readmeFile);
                localFileOutputStream.flush();
            }
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        } finally {
            if (localFileOutputStream != null) {
                IOUtils.closeQuietly(localFileOutputStream);
            }
        }
    }

    public static void main(String[] args) {
        try {
            String base64Code = encodeBase64File("c:/mongodb2.jpg");
            System.out.println(base64Code);
            decoderBase64File(base64Code, "c:/mongodb3.jpg");
            toFile(base64Code, "D:\\three.txt");
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

}
