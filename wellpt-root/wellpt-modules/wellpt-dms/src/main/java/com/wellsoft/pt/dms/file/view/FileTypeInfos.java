/*
 * @(#)2018年9月26日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.view;

import com.wellsoft.context.base.BaseObject;

import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年9月26日.1	zhulh		2018年9月26日		Create
 * </pre>
 * @date 2018年9月26日
 */
public class FileTypeInfos extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -2404940696985268091L;

    private Map<String, String> typeNameMap;

    private Map<String, Boolean> downloadableMap;

    /**
     * @return the typeNameMap
     */
    public Map<String, String> getTypeNameMap() {
        return typeNameMap;
    }

    /**
     * @param typeNameMap 要设置的typeNameMap
     */
    public void setTypeNameMap(Map<String, String> typeNameMap) {
        this.typeNameMap = typeNameMap;
    }

    /**
     * @return the downloadableMap
     */
    public Map<String, Boolean> getDownloadableMap() {
        return downloadableMap;
    }

    /**
     * @param downloadableMap 要设置的downloadableMap
     */
    public void setDownloadableMap(Map<String, Boolean> downloadableMap) {
        this.downloadableMap = downloadableMap;
    }

}
