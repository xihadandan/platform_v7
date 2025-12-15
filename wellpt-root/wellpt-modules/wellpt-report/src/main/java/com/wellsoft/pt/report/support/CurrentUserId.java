/*
 * @(#)2014-3-7 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.report.support;

//import com.fr.script.AbstractFunction;

import com.wellsoft.pt.security.util.SpringSecurityUtils;

/**
 * Description: 报表获取当前登录用户ID自定义函数
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-3-7.1	zhouyq		2014-3-7		Create
 * </pre>
 * @date 2014-3-7
 */
@Deprecated
public class CurrentUserId /*extends AbstractFunction*/ {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 获取当前登录用户ID
     * <p>
     * (non-Javadoc)
     *
     * @see com.fr.script.AbstractFunction#run(java.lang.Object[])
     */
    //@Override
    public Object run(Object[] args) {
        String currentUserId = SpringSecurityUtils.getCurrentUserId();
        return currentUserId;
    }
}
