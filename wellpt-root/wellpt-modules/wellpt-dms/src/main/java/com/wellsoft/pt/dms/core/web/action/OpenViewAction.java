/*
 * @(#)Feb 28, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.web.action;

import com.wellsoft.pt.dms.core.annotation.Action;
import com.wellsoft.pt.dms.core.annotation.ActionConfig;
import com.wellsoft.pt.dms.core.context.ActionContext;
import com.wellsoft.pt.dms.core.web.ActionProxy;
import com.wellsoft.pt.dms.core.web.View;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
 * Feb 28, 2017.1	zhulh		Feb 28, 2017		Create
 * </pre>
 * @date Feb 28, 2017
 */
@Action("数据列表")
public class OpenViewAction extends OpenViewActionSupport {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 210780583445764373L;

    @ActionConfig(name = "打开详情", id = "open_view")
    public String open(String pkValue, HttpServletRequest request, HttpServletResponse response,
                       Model model) {
        View view = resolveView(request);
        try {
            ActionContext actionContext = getActionContext();
            List<ActionProxy> actions = actionContext.getActions();
            actionContext.setActions(actions);
            view.loadActionData(model, actionContext, request, response);
            mergeOutputModel(model, actionContext, request, response);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return view.getViewName();
    }

}
