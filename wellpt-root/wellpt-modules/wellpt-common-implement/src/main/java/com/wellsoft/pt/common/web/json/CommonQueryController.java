/*
 * @(#)2013-2-1 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.web.json;

import com.wellsoft.context.jdbc.support.PropertyFilter;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.service.CommonQueryService;
import com.wellsoft.context.web.controller.BaseController;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
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
 * 2013-2-1.1	zhulh		2013-2-1		Create
 * </pre>
 * @date 2013-2-1
 */
@Controller
@RequestMapping("/common/query")
public class CommonQueryController extends BaseController {
    @Autowired
    private CommonQueryService commonQueryService;

    // key的格式为OPERATOR_FIELDNAME
    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public QueryData query(QueryInfo queryInfo, HttpServletRequest request) {
        String queryPrefix = getQueryPrefix(request);
        List<PropertyFilter> propertyFilters = PropertyFilter.buildFromHttpRequest(request, queryPrefix);
        queryInfo.setPropertyFilters(propertyFilters);
        return commonQueryService.query(queryInfo);
    }

    /**
     * @param request
     * @return
     */
    private String getQueryPrefix(HttpServletRequest request) {
        String queryPrefix = request.getParameter("queryPrefix");
        if (StringUtils.isBlank(queryPrefix)) {
            queryPrefix = "filter";
        }
        return queryPrefix;
    }

}
