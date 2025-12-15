/*
 * @(#)2019年5月14日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.servlet;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.util.ObfuscatedString;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.ServletContextResourcePatternResolver;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

/**
 * Description: 密码器类加载器，设置为上下文的类加载器，解密class文件，后续考虑通过Instrumentation或JVMTI替换解密功能
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年5月14日.1	zhulh		2019年5月14日		Create
 * </pre>
 * @date 2019年5月14日
 */
@Order(value = 0)
public class CryptoClassLoaderInitializer implements WebApplicationInitializer {

    // 对称加密密钥对生成算法"AES"
    private static final String ENCRYPTED_KEY_ALGORITHM = new ObfuscatedString(new long[]{0x1259D531FAA434FCL,
            0x565DBBDA88D87DE4L}).toString();
    // 密码器对称加密密钥加密解密算法"AES/CBC/ISO10126Padding"
    private static final String ENCRYPTED_KEY_CIPHER_ALGORITHM = new ObfuscatedString(new long[]{0x5C6A5012DB3B7D55L,
            0x3C19BA3F7BABFEDL, 0xA9EC1F481C7DC06L, 0xB2D2AA8717167007L}).toString();
    // 数据文件后缀".cdb"
    private static final String DATA_FILE_SUFIX = new ObfuscatedString(new long[]{0x78F8C0E929FE96FEL,
            0x5E058216EE556DL}).toString();
    private Logger logger = LoggerFactory.getLogger(CryptoClassLoaderInitializer.class);
    // 加密的元数据位置信息"classpath*:/META-INF/resources/**/*.mdb"
    private String mdbLocationPattern = new ObfuscatedString(new long[]{0x138F9CD068E41229L, 0x5593C38603C16C87L,
            0xDF64E703F4425EDAL, 0x689742091D6DD3F7L, 0x86C34B77505A023L, 0x6A11CBF949E60C7BL}).toString();
    // 加密的数据位置信息"classpath*:/META-INF/resources/**/*.cdb"
    private String cdbLocationPattern = new ObfuscatedString(new long[]{0xCD8278DA14A441EAL, 0x3C8B9996A3BA4D46L,
            0xE73B7578DE554E9FL, 0x3A916BDE405714F6L, 0xEBFF11325EC9F2E7L, 0xA8A0AF4B0F34430CL}).toString();

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.web.WebApplicationInitializer#onStartup(javax.servlet.ServletContext)
     */
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        ResourcePatternResolver resourcePatternResolver = new ServletContextResourcePatternResolver(servletContext);
        try {
            Resource[] mdbResources = resourcePatternResolver.getResources(mdbLocationPattern);
            Resource[] cdbResources = resourcePatternResolver.getResources(cdbLocationPattern);
            if (mdbResources.length > 0 && cdbResources.length > 0) {
                ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
                ClassLoader classLoader = new CryptoClassLoader(currentClassLoader, mdbResources, cdbResources);
                Thread.currentThread().setContextClassLoader(classLoader);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static class CryptoClassLoader extends ClassLoader {

        private final Set<String> includedClasses = Sets.newHashSet();
        private final Set<String> excludedClasses = Sets.newHashSet();
        private Logger logger = LoggerFactory.getLogger(CryptoClassLoader.class);
        private Map<String, CipherFileInfo> cipherFileInfoMap = Maps.newHashMap();
        private Map<String, SecretKeyInfo> keyInfoMap = Maps.newHashMap();

        private Map<String, Resource> dataResourceMap = Maps.newHashMap();

        /**
         * @param parent
         */
        public CryptoClassLoader(ClassLoader parent, Resource[] mdbResources, Resource[] cdbResources) {
            super(parent);
            parseMetadata(mdbResources);
            prepareData(cdbResources);
        }

        /**
         * @param mdbResources
         */
        private void parseMetadata(Resource[] mdbResources) {
            for (Resource resource : mdbResources) {
                try {
                    parseMetadata(resource);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        /**
         * @param resource
         * @throws Exception
         */
        private void parseMetadata(Resource resource) throws Exception {
            String resourceFileName = resource.getFilename();
            String dataFileName = getDataFileName(resourceFileName);
            DataInputStream dataInputStream = new DataInputStream(resource.getInputStream());
            // 读取密钥
            byte[] encoded = readKeyBytes(dataInputStream);
            SecretKeyInfo secretKeyInfo = new SecretKeyInfo();
            secretKeyInfo.setDataFileName(dataFileName);
            secretKeyInfo.setEncoded(encoded);
            keyInfoMap.put(dataFileName, secretKeyInfo);

            // 类索引长度
            int classIndexLengh = DigestUtils.md5Hex(Resource.class.getName()).getBytes().length;
            byte[] classIndexByte = new byte[classIndexLengh];
            // 读取第一个类索引
            int len = dataInputStream.read(classIndexByte);
            int offset = 0;
            while (len > 0) {
                int cipherFileLength = dataInputStream.readInt();
                CipherFileInfo cipherFileInfo = new CipherFileInfo();
                cipherFileInfo.setDataFileName(dataFileName);
                cipherFileInfo.setIndexName(new String(classIndexByte));
                cipherFileInfo.setOffset(offset);
                cipherFileInfo.setLengh(cipherFileLength);
                addToCipherFileInfoMap(cipherFileInfo);

                // 下一类文件信息
                offset += cipherFileLength;
                len = dataInputStream.read(classIndexByte);
            }
            IOUtils.closeQuietly(dataInputStream);
        }

        /**
         * 如何描述该方法
         *
         * @param resourceFileName
         * @return
         */
        private String getDataFileName(String resourceFileName) {
            String dataFileName = StringUtils.substring(resourceFileName, 0, resourceFileName.length() - 4)
                    + DATA_FILE_SUFIX;
            return dataFileName;
        }

        /**
         * @param cdbResources
         */
        private void prepareData(Resource[] cdbResources) {
            for (Resource resource : cdbResources) {
                prepareData(resource);
            }
        }

        /**
         * @param resource
         */
        private void prepareData(Resource resource) {
            dataResourceMap.put(resource.getFilename(), resource);
        }

        /**
         * @param cipherFileInfo
         */
        private void addToCipherFileInfoMap(CipherFileInfo cipherFileInfo) {
            cipherFileInfoMap.put(cipherFileInfo.getIndexName(), cipherFileInfo);
        }

        /**
         * (non-Javadoc)
         *
         * @see java.lang.ClassLoader#loadClass(java.lang.String, boolean)
         */
        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            if (isInclude(name)) {
                return loadClassInternal(name);
            }
            return super.loadClass(name, resolve);
        }

        /**
         * @param name
         * @return
         */
        private Class<?> loadClassInternal(String name) {
            Class<?> c = findLoadedClass(name);
            if (c != null) {
                return c;
            }
            byte[] b = null;
            try {
                b = loadClassData(name);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return defineClass(name, b, 0, b.length);
        }

        /**
         * @param name
         * @return
         */
        private boolean isInclude(String name) {
            if (includedClasses.contains(name)) {
                return true;
            }
            if (excludedClasses.contains(name)) {
                return false;
            }
            String classFileId = DigestUtils.md5Hex(name);
            CipherFileInfo cipherFileInfo = cipherFileInfoMap.get(classFileId);
            if (cipherFileInfo != null) {
                includedClasses.add(name);
            } else {
                excludedClasses.add(name);
            }
            return includedClasses.contains(name);
        }

        /**
         * @param name
         * @return
         * @throws Exception
         */
        private byte[] loadClassData(String name) throws Exception {
            String classFileId = DigestUtils.md5Hex(name);
            CipherFileInfo cipherFileInfo = cipherFileInfoMap.get(classFileId);
            String dataFileName = cipherFileInfo.getDataFileName();
            // 从数据文件中读取相应的数据
            Resource dataResource = dataResourceMap.get(dataFileName);
            InputStream input = dataResource.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(input);
            // 数据偏移量
            int offset = cipherFileInfo.getOffset();
            // 数据长度
            int length = cipherFileInfo.getLengh();
            byte[] dataBytes = new byte[length];
            dataInputStream.skip(offset);
            dataInputStream.readFully(dataBytes);
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(dataInputStream);

            SecretKeyInfo secretKeyInfo = getSecretKeyInfo(dataFileName);
            // AES专用密钥
            SecretKeySpec key = new SecretKeySpec(secretKeyInfo.getEncoded(), ENCRYPTED_KEY_ALGORITHM);
            // 创建密码器
            Cipher cipher = Cipher.getInstance(ENCRYPTED_KEY_CIPHER_ALGORITHM);
            // 初始化向量
            int ivLen = cipher.getBlockSize();
            byte[] ivBytes = new byte[ivLen];
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            // 初始化为加密模式的密码器
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            // 解密
            byte[] result = cipher.doFinal(dataBytes);

            return result;
        }

        /**
         * @param dataResource
         * @return
         * @throws IOException
         */
        private byte[] readKeyBytes(DataInputStream dataInputStream) throws IOException {
            // 数据偏移量
            int offset = 0;
            // 数据长度
            int length = 16;
            byte[] keyBytes = new byte[length];
            dataInputStream.skipBytes(offset);
            dataInputStream.readFully(keyBytes);
            return keyBytes;
        }

        /**
         * @param dataFileName
         * @return
         */
        private SecretKeyInfo getSecretKeyInfo(String dataFileName) {
            return keyInfoMap.get(dataFileName);
        }

    }

    private static class CipherFileInfo extends BaseObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 5848001200111310178L;

        private String dataFileName;
        private String indexName;
        private int offset;
        private int lengh;

        /**
         * @return the dataFileName
         */
        public String getDataFileName() {
            return dataFileName;
        }

        /**
         * @param dataFileName 要设置的dataFileName
         */
        public void setDataFileName(String dataFileName) {
            this.dataFileName = dataFileName;
        }

        /**
         * @return the indexName
         */
        public String getIndexName() {
            return indexName;
        }

        /**
         * @param indexName 要设置的indexName
         */
        public void setIndexName(String indexName) {
            this.indexName = indexName;
        }

        /**
         * @return the offset
         */
        public int getOffset() {
            return offset;
        }

        /**
         * @param offset 要设置的offset
         */
        public void setOffset(int offset) {
            this.offset = offset;
        }

        /**
         * @return the lengh
         */
        public int getLengh() {
            return lengh;
        }

        /**
         * @param lengh 要设置的lengh
         */
        public void setLengh(int lengh) {
            this.lengh = lengh;
        }

    }

    private static class SecretKeyInfo extends BaseObject {
        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -3917929083398494868L;

        private String dataFileName;

        private byte[] encoded;

        /**
         * @return the dataFileName
         */
        public String getDataFileName() {
            return dataFileName;
        }

        /**
         * @param dataFileName 要设置的dataFileName
         */
        public void setDataFileName(String dataFileName) {
            this.dataFileName = dataFileName;
        }

        /**
         * @return the encoded
         */
        public byte[] getEncoded() {
            return encoded;
        }

        /**
         * @param encoded 要设置的encoded
         */
        public void setEncoded(byte[] encoded) {
            this.encoded = encoded;
        }

    }

}
