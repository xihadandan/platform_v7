/*
 * @(#)2019年5月15日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.maven.plugin.cipher;

import java.io.File;

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
public class CompiledClassFile {

    // class所在目录
    private File directory;

    // class目录的文件名
    private String name;

    // class文件
    private File classFile;

    // class文件所代表的类名
    private String className;

    /**
     * @param directory
     * @param name
     */
    public CompiledClassFile(File directory, String name) {
        super();
        this.directory = directory;
        this.name = name;
        classFile = new File(directory, name);
        parseClassName();
    }

    /**
     *
     */
    private void parseClassName() {
        String fileSeparator = System.getProperty("file.separator");
        String tmpName = StringUtils.replace(getName(), fileSeparator, ".");
        className = StringUtils.replace(tmpName, ".class", "");
    }

    /**
     * @return the directory
     */
    public File getDirectory() {
        return directory;
    }

    /**
     * @param directory 要设置的directory
     */
    public void setDirectory(File directory) {
        this.directory = directory;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    public File getClassFile() {
        return classFile;
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

}
