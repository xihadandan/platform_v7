/*
 * @(#)2015-10-13 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.rule.engine.expression.wf;

import com.wellsoft.pt.rule.engine.Param;
import com.wellsoft.pt.rule.engine.expression.Expression;
import com.wellsoft.pt.rule.engine.suport.CommandName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-10-13.1	zhulh		2015-10-13		Create
 * </pre>
 * @date 2015-10-13
 */
public class OrganizationOfExpression extends WorkFlowExpression {

    private String expression;
    private Expression paramExpression;

    public OrganizationOfExpression(String expression) {
        this.expression = expression;
        paramExpression = parseOptionOf(expression, CommandName.OrganizationOf);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.rule.engine.expression.Expression#evaluate(com.wellsoft.pt.rule.engine.Param)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object evaluate(Param param) {
        List<String> userIds = new ArrayList<String>(0);

        Collection<String> rawUserIds = (Collection<String>) paramExpression.evaluate(param);
        if (rawUserIds.isEmpty()) {
            return userIds;
        }

        // zyguo 组织切到到多组织了，所以该异常应该是不用了，找不到调用的地方，所以这里就不检查了，直接返回
        return rawUserIds;

        // OrgApiFacade orgApiFacade =
        // ApplicationContextHolder.getBean(OrgApiFacade.class);
        // return orgApiFacade.filterUserIdsByOrgId(rawUserIds, getOrgId());
    }

}
