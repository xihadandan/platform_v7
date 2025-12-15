/*
 * @(#)2015年11月12日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.unit.support;

import com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService;
import org.dom4j.Document;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年11月12日.1	zhulh		2015年11月12日		Create
 * </pre>
 * @date 2015年11月12日
 */
public abstract class UnitTreeDataProviderDelegateService extends UnitTreeTypeDelegateService {

    /**
     * 如何描述该方法
     *
     * @return
     */
    public abstract String getType();

    public abstract Document searchXml(String optionType, String all, String login, String searchValue,
                                       String filterCondition);

    /**
     * 获取前台扩展参数filterCondition
     *
     * @return
     */
    protected String getFilterCondition() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        return request.getParameter("filterCondition");
    }

}
