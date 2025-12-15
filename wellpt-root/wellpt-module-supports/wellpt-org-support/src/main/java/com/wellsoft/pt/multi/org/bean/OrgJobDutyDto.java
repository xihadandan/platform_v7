/*
 * @(#)2017年11月24日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.bean;

import com.wellsoft.context.jdbc.support.BaseQueryItem;

/**
 * Description: 如何描述该类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年11月24日.1	zyguo		2017年11月24日		Create
 * </pre>
 * @date 2017年11月24日
 */
public class OrgJobDutyDto extends OrgTreeNodeDto implements BaseQueryItem {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -1399944020822163150L;
    private String dutyId;

    /**
     * @return the dutyId
     */
    public String getDutyId() {
        return dutyId;
    }

    /**
     * @param dutyId 要设置的dutyId
     */
    public void setDutyId(String dutyId) {
        this.dutyId = dutyId;
    }
}
