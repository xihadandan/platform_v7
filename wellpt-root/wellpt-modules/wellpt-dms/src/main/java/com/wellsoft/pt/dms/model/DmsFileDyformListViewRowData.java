/*
 * @(#)2018年9月20日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.model;

import com.wellsoft.pt.dms.ext.support.ListViewRowData;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年9月20日.1	zhulh		2018年9月20日		Create
 * </pre>
 * @date 2018年9月20日
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DmsFileDyformListViewRowData extends ListViewRowData {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 6894176103578705656L;

    // 夹数据UUID
    private String folderUuid;

    /**
     * @return the folderUuid
     */
    public String getFolderUuid() {
        return folderUuid;
    }

    /**
     * @param folderUuid 要设置的folderUuid
     */
    public void setFolderUuid(String folderUuid) {
        this.folderUuid = folderUuid;
    }

}
