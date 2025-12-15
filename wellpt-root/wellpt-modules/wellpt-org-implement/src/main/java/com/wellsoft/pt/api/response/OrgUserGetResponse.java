/*
 * @(#)2015-7-8 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.response;

import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.org.bean.UserBean;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-7-8.1	zhulh		2015-7-8		Create
 * </pre>
 * @date 2015-7-8
 */
public class OrgUserGetResponse extends WellptResponse {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -7313662149117292036L;

    private UserBean data;

    /**
     * @return the data
     */
    public UserBean getData() {
        return data;
    }

    /**
     * @param data 要设置的data
     */
    public void setData(UserBean data) {
        this.data = data;
    }

}
