/*
 * @(#)2019年5月15日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.maven.plugin.cipher;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtPrimitiveType;
import javassist.NotFoundException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年5月15日.1	zhulh		2019年5月15日		Create
 * </pre>
 * @date 2019年5月15日
 */
@Mojo(name = "cipher", defaultPhase = LifecyclePhase.PROCESS_CLASSES, requiresDependencyResolution = ResolutionScope.COMPILE)
public class CipherMojo extends AbstractCipherMojo {

    @Parameter(defaultValue = "${project.build.sourceEncoding}")
    private String encoding;

    @Parameter(defaultValue = "${project.artifactId}")
    private String artifactId;

    // 加密数据信息的存储的文件目录
    private String dbDir = "META-INF/resources/WEB-INF";

    /**
     * A list of inclusion filters for the encrypt.
     */
    @Parameter
    private Set<String> includes = new HashSet<String>();

    /**
     * A list of exclusion filters for the compiler.
     */
    @Parameter
    private Set<String> excludes = new HashSet<String>();

    /**
     * Project classpath.
     */
    @Parameter(defaultValue = "${project.compileClasspathElements}", readonly = true, required = true)
    private List<String> classpathElements;

    /**
     * The output directory into which to cipher compiled classes.
     */
    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true, readonly = true)
    private File outputDirectory;

    /**
     * @return
     * @throws Exception
     */
    private String[] getDefaultIncludes() {
        return new String[]{"**/*.class"};
    }

    /**
     * (non-Javadoc)
     *
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        // getLog().info("artifactId " + artifactId);
        getLog().info("Using \'" + encoding + "\' encoding to encrypt classe files");
        getLog().info("Encrypt classe file in output directory " + outputDirectory);
        try {
            processDir(outputDirectory);
        } catch (Exception e) {
            getLog().error(e);
            throw new MojoFailureException(e.getMessage());
        }
    }

    /**
     * @param classesDir
     * @throws Exception
     */
    private void processDir(File classesDir) throws Exception {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(classesDir);
        if (excludes.isEmpty()) {
            scanner.addDefaultExcludes();
        } else {
            scanner.setExcludes(excludes.toArray(new String[]{}));
        }
        if (includes.isEmpty()) {
            scanner.setIncludes(getDefaultIncludes());
        } else {
            scanner.setIncludes(includes.toArray(new String[]{}));
        }
        scanner.scan();

        // 已经存在加密文件，不再进行加密处理
        if (existsEncryptFiles()) {
            getLog().error("Encrypt file exists, remove the target classes folder(or use mvn clean) and try again.");
            System.exit(0);
            return;
        }

        DataOutputStream mOutput = new DataOutputStream(new FileOutputStream(getMetadataFile()));
        DataOutputStream dOutput = new DataOutputStream(new FileOutputStream(getDataFile()));
        mOutput.write(getKeyEncoded(getArtifactId()));
        //		writeBytes(getKeyEncoded(), new File(outputDirectory, "key.data").getAbsolutePath());
        for (String name : scanner.getIncludedFiles()) {
            CompiledClassFile compiledClassFile = new CompiledClassFile(classesDir, name);
            getLog().info("Encrypt class file " + compiledClassFile.getName());
            // 加密文件
            processFile(compiledClassFile, mOutput, dOutput);
        }
        IOUtil.close(mOutput);
        IOUtil.close(dOutput);
    }


    /**
     * @return
     */
    boolean existsEncryptFiles() {
        File dbDirectory = new File(outputDirectory, dbDir);
        if (!dbDirectory.exists()) {
            return false;
        }
        File mdbFile = new File(dbDirectory, artifactId + ".mdb");
        File cdbFile = new File(dbDirectory, artifactId + ".cdb");
        return mdbFile.exists() && cdbFile.exists();
    }

    /**
     * @return
     */
    private File getMetadataFile() {
        File dbDirectory = new File(outputDirectory, dbDir);
        if (!dbDirectory.exists()) {
            dbDirectory.mkdirs();
        }
        return new File(dbDirectory, artifactId + ".mdb");
    }

    /**
     * @return
     */
    private File getDataFile() {
        File dbDirectory = new File(outputDirectory, dbDir);
        if (!dbDirectory.exists()) {
            dbDirectory.mkdirs();
        }
        return new File(dbDirectory, artifactId + ".cdb");
    }

    /**
     * @param compiledClassFile
     */
    private void processFile(CompiledClassFile compiledClassFile, DataOutputStream mOutput, DataOutputStream dOutput) {
        try {
            byte[] result = encrypt(compiledClassFile);
            String className = compiledClassFile.getClassName();
            String fileId = DigestUtils.md5Hex(className);
            mOutput.write(fileId.getBytes());
            mOutput.writeInt(result.length);
            dOutput.write(result);
            // getLog().info("offset " + offset + ", byte lenth " + result.length);
            // 重写class内容，清空方法体
            rewriteClassFile(compiledClassFile);
        } catch (Exception e) {
            getLog().error(e);
            throw new RuntimeException("文件加密失败!");
        }
    }

    /**
     * @param sourceFile
     * @throws NotFoundException
     */
    private void rewriteClassFile(CompiledClassFile compiledClassFile) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        if (classpathElements != null) {
            for (String classpathElement : classpathElements) {
                pool.appendClassPath(classpathElement);
            }
        }
        CtClass cc = pool.getCtClass(compiledClassFile.getClassName());
        if (cc.isAnnotation() || cc.isAnnotation() || cc.isArray() || cc.isEnum() || cc.isFrozen() || cc.isInterface()) {
            return;
        }
        // 清空方法
        CtMethod[] ctMethods = cc.getDeclaredMethods();
        for (CtMethod ctMethod : ctMethods) {
            CtClass ctClass = ctMethod.getReturnType();
            if (CtPrimitiveType.class.isAssignableFrom(ctClass.getClass())) {
                CtPrimitiveType ctPrimitiveType = (CtPrimitiveType) ctClass;
                String privitiveTypeName = ctPrimitiveType.getName();
                if (StringUtils.equals(privitiveTypeName, "void")) {
                    ctMethod.setBody("{}");
                } else if (StringUtils.equals(privitiveTypeName, "boolean")) {
                    ctMethod.setBody("{return false;}");
                } else if (StringUtils.equals(privitiveTypeName, "byte")) {
                    ctMethod.setBody("{return 0x0;}");
                } else if (StringUtils.equals(privitiveTypeName, "short")) {
                    ctMethod.setBody("{return (short)0;}");
                } else if (StringUtils.equals(privitiveTypeName, "char")) {
                    ctMethod.setBody("{return (char)0;}");
                } else if (StringUtils.equals(privitiveTypeName, "long")) {
                    ctMethod.setBody("{return 0L;}");
                } else if (StringUtils.equals(privitiveTypeName, "float")) {
                    ctMethod.setBody("{return 0F;}");
                } else if (StringUtils.equals(privitiveTypeName, "double")) {
                    ctMethod.setBody("{return 0D;}");
                } else {
                    ctMethod.setBody("{return 0;}");
                }
            } else {
                ctMethod.setBody("{return null;}");
            }
        }
        // 保存生成的字节码
        cc.writeFile(outputDirectory.getAbsolutePath());
    }

    /**
     * @param sourceFile
     * @return
     * @throws Exception
     */
    private byte[] encrypt(CompiledClassFile compiledClassFile) {
        FileInputStream input = null;
        try {
            // 密钥
            byte[] keyEncoded = getKeyEncoded(getArtifactId());
            // getLog().info("AES key length " + keyEncoded.length);
            // 创建密码器
            Cipher cipher = Cipher.getInstance(ENCRYPTED_KEY_CIPHER_ALGORITHM);

            // 初始化为加密模式的密码器
            SecretKeySpec key = new SecretKeySpec(keyEncoded, ENCRYPTED_KEY_ALGORITHM);

            // 初始化向量
            int ivLen = cipher.getBlockSize();
            byte[] ivBytes = new byte[ivLen];
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);

            // 加密内容
            input = new FileInputStream(compiledClassFile.getClassFile());
            byte[] byteContent = IOUtil.toByteArray(input);

            // 加密
            byte[] result = cipher.doFinal(byteContent);

            return result;
        } catch (Exception e) {
            getLog().error(e.getMessage(), e);
        } finally {
            IOUtil.close(input);
        }
        return null;
    }

    /**
     * @return the artifactId
     */
    public String getArtifactId() {
        return artifactId;
    }

}
