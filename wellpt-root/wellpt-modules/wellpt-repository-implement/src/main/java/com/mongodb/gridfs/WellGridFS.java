// GridFS.java

/**
 * Copyright (C) 2008 10gen Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.gridfs;

import com.mongodb.DB;

import java.io.InputStream;

/**
 * Description: 创建WellGridFSInputFile
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年7月6日.1	zhongzh		2016年7月6日		Create
 * </pre>
 * @date 2016年7月6日
 */
public class WellGridFS extends GridFS {

    /**
     * 如何描述该构造方法
     *
     * @param db
     * @param bucket
     */
    public WellGridFS(DB db, String bucket) {
        super(db, bucket);
    }

    /**
     * 如何描述该构造方法
     *
     * @param db
     */
    public WellGridFS(DB db) {
        super(db);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.mongodb.gridfs.GridFS#createFile(java.io.InputStream, java.lang.String)
     */
    @Override
    public GridFSInputFile createFile(InputStream in, String filename) {
        return new WellGridFSInputFile(this, in, filename);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.mongodb.gridfs.GridFS#createFile(java.io.InputStream, java.lang.String, boolean)
     */
    @Override
    public GridFSInputFile createFile(InputStream in, String filename, boolean closeStreamOnPersist) {
        return new WellGridFSInputFile(this, in, filename, closeStreamOnPersist);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.mongodb.gridfs.GridFS#createFile(java.lang.String)
     */
    @Override
    public GridFSInputFile createFile(String filename) {
        return new WellGridFSInputFile(this, filename);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.mongodb.gridfs.GridFS#createFile()
     */
    @Override
    public GridFSInputFile createFile() {
        return new WellGridFSInputFile(this);
    }

}
