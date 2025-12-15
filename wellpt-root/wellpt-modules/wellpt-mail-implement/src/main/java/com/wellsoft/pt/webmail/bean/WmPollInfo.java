/*
 * @(#)2016年6月7日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.bean;

import com.wellsoft.context.jdbc.support.BaseItem;

/**
 * Description: 邮件统计信息
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年6月7日.1	zhulh		2016年6月7日		Create
 * </pre>
 * @date 2016年6月7日
 */
public class WmPollInfo extends BaseItem {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -1722871141242486998L;

    private Integer status;
    private Long count;

    /**
     * @return the status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @param status 要设置的status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return the count
     */
    public Long getCount() {
        return count;
    }

    /**
     * @param count 要设置的count
     */
    public void setCount(Long count) {
        this.count = count;
    }

}
