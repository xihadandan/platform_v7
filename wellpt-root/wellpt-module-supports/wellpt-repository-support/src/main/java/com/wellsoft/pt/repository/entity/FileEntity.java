/*
 * @(#)2012-11-21 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.repository.entity;

import java.io.InputStream;
import java.util.Calendar;

/**
 * Description: 记录文件相关属性类
 *
 * @author lilin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 *
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-21.1	lilin		2012-11-21		Create
 * </pre>
 * @date 2012-11-21
 */
public class FileEntity {
    //文件名
    private String filename;

    private String fileID;

    //修改时间
    private Calendar edittime;
    //文件大小
    private long size;
    //文件流
    private InputStream file;

    public FileEntity() {
    }

    public FileEntity(String filename, Calendar edittime, long size) {
        super();
        this.filename = filename;
        this.edittime = edittime;
        this.size = size;
        this.file = file;
    }

    /**
     * @return the file
     */
    public InputStream getFile() {
        return file;
    }

    /**
     * @param file 要设置的file
     */
    public void setFile(InputStream file) {
        this.file = file;
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename 要设置的filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * @return the edittime
     */
    public Calendar getEdittime() {
        return edittime;
    }

    /**
     * @param edittime 要设置的edittime
     */
    public void setEdittime(Calendar edittime) {
        this.edittime = edittime;
    }

    /**
     * @return the size
     */
    public long getSize() {
        return size;
    }

    /**
     * @param size 要设置的size
     */
    public void setSize(long size) {
        this.size = size;
    }

    public long getKBSize() {
        return (this.size / 1024);
    }

    public String getFileID() {
        return fileID;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }


}
