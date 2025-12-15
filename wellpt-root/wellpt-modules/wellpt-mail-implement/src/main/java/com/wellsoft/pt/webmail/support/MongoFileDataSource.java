/*
 * @(#)2016年6月4日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.support;

import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;

import javax.activation.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Description: Mongo文件数据源
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年6月4日.1	zhulh		2016年6月4日		Create
 * </pre>
 * @date 2016年6月4日
 */
public class MongoFileDataSource implements DataSource {

    private MongoFileEntity fileEntity;

    /**
     * @param fileEntity
     */
    public MongoFileDataSource(MongoFileEntity fileEntity) {
        this.fileEntity = fileEntity;
    }

    /**
     * (non-Javadoc)
     *
     * @see javax.activation.DataSource#getContentType()
     */
    @Override
    public String getContentType() {
        return fileEntity.getContentType();
    }

    /**
     * (non-Javadoc)
     *
     * @see javax.activation.DataSource#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return fileEntity.getInputstream();
    }

    /**
     * (non-Javadoc)
     *
     * @see javax.activation.DataSource#getName()
     */
    @Override
    public String getName() {
        return fileEntity.getFileName();
    }

    /**
     * (non-Javadoc)
     *
     * @see javax.activation.DataSource#getOutputStream()
     */
    @Override
    public OutputStream getOutputStream() throws IOException {
        return null;
    }

}
