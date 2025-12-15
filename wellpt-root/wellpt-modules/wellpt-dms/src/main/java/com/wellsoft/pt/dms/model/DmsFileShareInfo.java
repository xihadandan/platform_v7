/*
 * @(#)Feb 7, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.model;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Feb 7, 2018.1	zhulh		Feb 7, 2018		Create
 * </pre>
 * @date Feb 7, 2018
 */
public class DmsFileShareInfo extends DmsFile {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 4289543314675467551L;

    // 分享UUID
    private String shareUuid;

    /**
     * @return the shareUuid
     */
    public String getShareUuid() {
        return shareUuid;
    }

    /**
     * @param shareUuid 要设置的shareUuid
     */
    public void setShareUuid(String shareUuid) {
        this.shareUuid = shareUuid;
    }

}
