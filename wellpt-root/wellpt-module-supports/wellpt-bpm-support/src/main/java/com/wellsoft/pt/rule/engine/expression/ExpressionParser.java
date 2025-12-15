/*
 * @(#)2015-6-25 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.rule.engine.expression;

import com.wellsoft.pt.rule.engine.expression.wf.*;
import com.wellsoft.pt.rule.engine.suport.CommandName;
import com.wellsoft.pt.rule.engine.suport.Operator;
import org.apache.commons.lang.StringUtils;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-25.1	zhulh		2015-6-25		Create
 * </pre>
 * @date 2015-6-25
 */
public class ExpressionParser {

    public Expression parse(String expression) {
        String tmp = expression.trim();
        if (tmp.indexOf(Operator.NAME_ASSIGN) != -1) {
            String[] tokens = StringUtils.split(tmp, Operator.NAME_ASSIGN);
            return new AssignExpression(tokens[0], parse(tokens[1]));
        }
        if (tmp.indexOf(Operator.NAME_UNION) != -1 || tmp.indexOf(Operator.NAME_INTERSECTION) != -1) {
            return new SetOperationListExpression(tmp);
        }
        if (tmp.startsWith(CommandName.Unit)) {
            return new UnitExpression(tmp);
        }
        if (tmp.startsWith(CommandName.FormField)) {
            return new FormFieldExpression(tmp);
        }
        if (tmp.startsWith(CommandName.TaskHistory)) {
            return new TaskHistoryExpression(tmp);
        }
        if (tmp.startsWith(CommandName.Option)) {
            return new OptionExpression(tmp);
        }
        if (tmp.startsWith(CommandName.Interface)) {
            return new InterfaceExpression(tmp);
        }
        if (tmp.startsWith(CommandName.OrganizationOf)) {
            return new OrganizationOfExpression(tmp);
        }
        if (tmp.startsWith(CommandName.LeaderOf)) {
            return new LeaderOfExpression(tmp);
        }
        if (tmp.startsWith(CommandName.DeptLeaderOf)) {
            return new DeptLeaderOfExpression(tmp);
        }
        if (tmp.startsWith(CommandName.BranchedLeaderOf)) {
            return new BranchedLeaderOfExpression(tmp);
        }
        if (tmp.startsWith(CommandName.AllLeaderOf)) {
            return new AllLeaderOfExpression(tmp);
        }
        if (tmp.startsWith(CommandName.DeptOf)) {
            return new DeptOfExpression(tmp);
        }
        if (tmp.startsWith(CommandName.ParentDeptOf)) {
            return new ParentDeptOfExpression(tmp);
        }
        if (tmp.startsWith(CommandName.RootDeptOf)) {
            return new RootDeptOfExpression(tmp);
        }
        if (tmp.startsWith(CommandName.SameDeptOf)) {
            return new SameDeptOfExpression(tmp);
        }
        if (tmp.startsWith(CommandName.SameRootDeptOf)) {
            return new SameRootDeptOfExpression(tmp);
        }
        if (tmp.startsWith(CommandName.SameRootDeptOf)) {
            return new SameRootDeptOfExpression(tmp);
        }
        if (tmp.startsWith(CommandName.LeftBracket)) {
            return new BracketExpression(tmp);
        }
        return new VariableExpression(tmp);
    }

    /**
     * @param expression
     * @param methodName
     * @return
     */
    public Expression parseParam(String expression, String methodName) {
        String tmp = expression;
        tmp = tmp.substring(methodName.length() + 1, tmp.length() - 1);
        return parse(tmp);
    }

}
