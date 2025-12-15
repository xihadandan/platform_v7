package com.wellsoft.context.util.file;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Description: DES加密解密工具类
 *
 * @author Asus
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年12月25日.1	Asus		2015年12月25日		Create
 * </pre>
 * @date 2015年12月25日
 */
public class DESUtils {
    private static final Logger LOG = LoggerFactory.getLogger(DESUtils.class);

    public static void main(String[] args) {
        DESUtils desUtil = new DESUtils();
        desUtil.savePriveKey("d:\\luceneData\\file\\keyen.txt");
        System.out.println("密钥已生成");
        desUtil.encryptionFile("d:\\luceneData\\test1.txt", "d:\\luceneData\\file\\", "d:\\luceneData\\file\\keyen.txt");
        System.out.println("文件已加密");
        desUtil.decryptionFile("d:\\luceneData\\file\\test1.txt", "d:\\luceneData\\file\\test1.txt",
                "d:\\luceneData\\file\\keyen.txt");
        System.out.println("文件已解码");

        // desUtil.savePriveKey("d:\\luceneData\\file\\key.doc");
        // System.out.println("密钥生成");
        // desUtil.encryptionFile("d:\\luceneData\\test.doc","d:\\luceneData\\file\\test.doc",
        // "d:\\luceneData\\file\\key.doc");
        // System.out.println("文件已加密");
        // desUtil.decryptionFile("d:\\luceneData\\file\\test.doc","d:\\luceneData\\test.doc",
        // "d:\\luceneData\\file\\key.doc");
        // System.out.println("文件已解密�");
    }

    /**
     * 保存Key文件
     *
     * @param keyFile 文件路径
     */
    public void savePriveKey(String keyFile) {
        FileOutputStream fos = null;
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("DES");
            SecureRandom sr = new SecureRandom();
            keyGen.init(sr);
            SecretKey key = keyGen.generateKey();
            byte[] rawKeyData = key.getEncoded();
            fos = new FileOutputStream(keyFile);
            fos.write(rawKeyData);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 文件加密
     *
     * @param fileName   文件名
     * @param folderName 文件存放路径
     * @param keyFile    加密Key文件
     */
    public void encryptionFile(String fileName, String folderName, String keyFile) {
        SecureRandom sr = new SecureRandom();
        SecretKey key = getPrivetKey(keyFile);
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            File file = new File(fileName);
            if (file.isFile()) {
                if (!folderName.endsWith("\\"))
                    folderName += "\\";
                Cipher cipher = Cipher.getInstance("DES");
                cipher.init(Cipher.ENCRYPT_MODE, key, sr);
                fis = new FileInputStream(file);
                byte[] data = new byte[fis.available()];
                if (fis.read(data) < 0) {
                    // read error
                    LOG.error(new StringBuilder("file[").append(fileName).append("] read no data").toString());
                }
                byte[] encryptedData = cipher.doFinal(data);
                String path = folderName + file.getName();
                fos = new FileOutputStream(path);
                fos.write(encryptedData);
            } else {
                throw new Exception(fileName + " is not a file or it is not exist!");
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(fos);
        }
    }

    /**
     * 文件解密
     *
     * @param inFile  调用文件名
     * @param outFile 文件存放路径
     * @param keyFile Key文件
     */
    public void decryptionFile(String inFile, String outFile, String keyFile) {
        SecretKey key = getPrivetKey(keyFile);
        SecureRandom sr = new SecureRandom();
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key, sr);
            fis = new FileInputStream(new File(inFile));
            byte[] encryptedData = new byte[fis.available()];
            if (fis.read(encryptedData) < 0) {
                // read error
                LOG.error(new StringBuilder("file[").append(inFile).append("] read no data").toString());
            }
            byte[] decryptedData = cipher.doFinal(encryptedData);
            fos = new FileOutputStream(new File(outFile));
            fos.write(decryptedData);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(fos);
        }
    }

    /**
     * 产出Key文件
     *
     * @param keyFile 文件路径
     * @return
     */
    private SecretKey getPrivetKey(String keyFile) {
        FileInputStream fis = null;
        SecretKey key = null;
        try {
            fis = new FileInputStream(new File(keyFile));
            byte[] rawKeyData = new byte[fis.available()];
            if (fis.read(rawKeyData) < 0) {
                // read error
                LOG.error(new StringBuilder("file[").append(keyFile).append("] read no data").toString());
            }
            DESKeySpec dks = new DESKeySpec(rawKeyData);
            key = SecretKeyFactory.getInstance("DES").generateSecret(dks);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(fis);
        }
        return key;
    }
}
