/*
 * @(#)2016年7月9日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.mongodb.gridfs;

import com.mongodb.MongoException;
import com.mongodb.util.JSON;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年7月9日.1	zhongzh		2016年7月9日		Create
 * </pre>
 * @date 2016年7月9日
 */
public class WellLoaclFSDBFile extends GridFSDBFile {

    private LogicFileInfo logicFileInfo;

    /**
     * 如何描述该构造方法
     *
     * @param logicFileInfo
     */
    public WellLoaclFSDBFile(LogicFileInfo logicFileInfo) {
        this.logicFileInfo = logicFileInfo;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.mongodb.gridfs.GridFSDBFile#getInputStream()
     */
    @Override
    public InputStream getInputStream() {
        try {
            return new FileInputStream(logicFileInfo.getPhysicalFileId());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.mongodb.gridfs.GridFSDBFile#writeTo(java.io.OutputStream)
     */
    @Override
    public long writeTo(OutputStream out) throws IOException {
        InputStream input = getInputStream();
        try {
            return IOUtils.copyLarge(input, out);
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.mongodb.gridfs.GridFSFile#save()
     */
    @Override
    public void save() {
        throw new UnsupportedOperationException();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.mongodb.gridfs.GridFSFile#validate()
     */
    @Override
    public void validate() {
        String _md5 = getMD5();
        if (_md5 == null) {
            throw new MongoException("no _md5 stored");
        }

    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.mongodb.gridfs.GridFSFile#numChunks()
     */
    @Override
    public int numChunks() {
        throw new UnsupportedOperationException();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.mongodb.gridfs.GridFSFile#getId()
     */
    @Override
    public Object getId() {
        return logicFileInfo.getPhysicalFileId();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.mongodb.gridfs.GridFSFile#getFilename()
     */
    @Override
    public String getFilename() {
        return logicFileInfo.getFileName();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.mongodb.gridfs.GridFSFile#getContentType()
     */
    @Override
    public String getContentType() {
        return logicFileInfo.getContentType();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.mongodb.gridfs.GridFSFile#getLength()
     */
    @Override
    public long getLength() {
        return logicFileInfo.getFileSize();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.mongodb.gridfs.GridFSFile#getChunkSize()
     */
    @Override
    public long getChunkSize() {
        throw new UnsupportedOperationException();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.mongodb.gridfs.GridFSFile#getUploadDate()
     */
    @Override
    public Date getUploadDate() {
        return logicFileInfo.getModifyTime();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.mongodb.gridfs.GridFSFile#getMD5()
     */
    @Override
    public String getMD5() {
        throw new UnsupportedOperationException();// TODO IMP
        // return logicFileInfo.getFileMd5();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.mongodb.gridfs.GridFSFile#put(java.lang.String, java.lang.Object)
     */
    @Override
    public Object put(String key, Object v) {
        if (key == null)
            throw new RuntimeException("key should never be null");
        else if (key.equals("_id"))
            logicFileInfo.setPhysicalFileId(v == null ? null : v.toString());
        else if (key.equals("filename"))
            logicFileInfo.setFileName(v == null ? null : v.toString());
        else if (key.equals("contentType"))
            logicFileInfo.setContentType((String) v);
        else if (key.equals("length"))
            logicFileInfo.setFileSize(v == null ? null : ((Number) v).longValue());
        else if (key.equals("md5"))
            throw new UnsupportedOperationException(); // TODO IMP
        else
            this.extra.put(key, v);
        return v;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.mongodb.gridfs.GridFSFile#get(java.lang.String)
     */
    @Override
    public Object get(String key) {
        if (key == null)
            throw new RuntimeException("key should never be null");
        else if (key.equals("_id"))
            return logicFileInfo.getPhysicalFileId();
        else if (key.equals("filename"))
            return logicFileInfo.getFileName();
        else if (key.equals("contentType"))
            return logicFileInfo.getContentType();
        else if (key.equals("length"))
            return logicFileInfo.getFileSize();
        else if (key.equals("md5"))
            throw new UnsupportedOperationException();// TODO IMP
        return this.extra.get(key);

    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.mongodb.gridfs.GridFSFile#toString()
     */
    @Override
    public String toString() {
        return JSON.serialize(logicFileInfo);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.mongodb.gridfs.GridFSFile#setGridFS(com.mongodb.gridfs.GridFS)
     */
    @Override
    protected void setGridFS(GridFS fs) {
        throw new UnsupportedOperationException();
    }

    /**
     * @return the logicFileInfo
     */
    public LogicFileInfo getLogicFileInfo() {
        return logicFileInfo;
    }

    /**
     * @param logicFileInfo 要设置的logicFileInfo
     */
    public void setLogicFileInfo(LogicFileInfo logicFileInfo) {
        this.logicFileInfo = logicFileInfo;
    }

}
