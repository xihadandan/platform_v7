/*
 * @(#)2016年7月7日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.repository.support;

import java.io.IOException;
import java.io.InputStream;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年7月7日.1	zhongzh		2016年7月7日		Create
 * </pre>
 * @date 2016年7月7日
 */
public class CheckedInputStream extends java.util.zip.CheckedInputStream {

    private boolean closeStream;

    /**
     * 如何描述该构造方法
     *
     * @param in
     * @param cksum
     */
    public CheckedInputStream(InputStream in) {
        super(in, new MD5Digest());
        this.closeStream = false;
    }

    /**
     * 如何描述该构造方法
     *
     * @param in
     * @param cksum
     * @param closeStream
     */
    public CheckedInputStream(InputStream in, boolean closeStream) {
        super(in, new MD5Digest());
        this.closeStream = closeStream;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see java.io.FilterInputStream#close()
     */
    @Override
    public void close() throws IOException {
        if (closeStream) {
            super.close();// 在外部记得要关闭
        }
    }

}
