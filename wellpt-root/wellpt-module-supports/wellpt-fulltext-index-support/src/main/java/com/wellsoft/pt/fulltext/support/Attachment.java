/*
 * @(#)8/1/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.support;

import java.util.HashMap;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 8/1/25.1	    zhulh		8/1/25		    Create
 * </pre>
 * @date 8/1/25
 */
public class Attachment extends HashMap {
    
    /**
     * @return the fileName
     */
    public Object getFileName() {
        return this.get("fileName");
    }

    /**
     * @param fileName 要设置的fileName
     */
    public void setFileName(String fileName) {
        this.put("fileName", fileName);
    }

    /**
     * @return the data
     */
    public Object getData() {
        return this.get("data");
    }

    /**
     * @param data 要设置的data
     */
    public void setData(String data) {
        this.put("data", data);
    }
}
