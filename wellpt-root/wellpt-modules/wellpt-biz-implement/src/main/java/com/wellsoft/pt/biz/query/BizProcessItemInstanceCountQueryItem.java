/*
 * @(#)10/18/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.query;

import com.wellsoft.context.jdbc.support.BaseQueryItem;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 10/18/22.1	zhulh		10/18/22		Create
 * </pre>
 * @date 10/18/22
 */
public class BizProcessItemInstanceCountQueryItem implements BaseQueryItem {

    private String processNodeInstUuid;

    private long count;

    /**
     * @return the processNodeInstUuid
     */
    public String getProcessNodeInstUuid() {
        return processNodeInstUuid;
    }

    /**
     * @param processNodeInstUuid 要设置的processNodeInstUuid
     */
    public void setProcessNodeInstUuid(String processNodeInstUuid) {
        this.processNodeInstUuid = processNodeInstUuid;
    }

    /**
     * @return the count
     */
    public long getCount() {
        return count;
    }

    /**
     * @param count 要设置的count
     */
    public void setCount(long count) {
        this.count = count;
    }
}
