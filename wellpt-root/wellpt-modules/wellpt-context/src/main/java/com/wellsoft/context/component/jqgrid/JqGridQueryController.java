/*
 * @(#)2013-2-3 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.component.jqgrid;

import com.wellsoft.context.jdbc.support.PagingInfo;
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
import springfox.documentation.annotations.ApiIgnore;

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
 * 2013-2-3.1	zhulh		2013-2-3		Create
 * </pre>
 * @date 2013-2-3
 */
@Controller
@RequestMapping(value = "/common/jqgrid")
public class JqGridQueryController extends BaseController {
    @Autowired
    private CommonQueryService commonQueryService;

    /**
     * 如何描述该方法
     *
     * @param queryData
     * @return
     */
    protected static JqGridQueryData convertToJqGridQueryData(QueryData queryData) {
        JqGridQueryData jqGridQueryData = new JqGridQueryData();
        PagingInfo pagingInfo = queryData.getPagingInfo();
        jqGridQueryData.setDataList(queryData.getDataList());
        jqGridQueryData.setTotalPages(pagingInfo.getTotalPages());
        jqGridQueryData.setCurrentPage(pagingInfo.getCurrentPage());
        jqGridQueryData.setTotalRows(pagingInfo.getTotalCount());
        return jqGridQueryData;
    }

    /**
     * 如何描述该方法
     *
     * @param jqGridQueryInfo
     * @param request
     * @return
     */
    protected static QueryInfo buildQueryInfo(JqGridQueryInfo jqGridQueryInfo, HttpServletRequest request) {
        PagingInfo pagingInfo = new PagingInfo();
        pagingInfo.setPageSize(jqGridQueryInfo.getRows());
        pagingInfo.setCurrentPage(jqGridQueryInfo.getPage());
        pagingInfo.setAutoCount(true);

        QueryInfo queryInfo = new QueryInfo();
        queryInfo.setSfId(jqGridQueryInfo.getSfId());
        queryInfo.setServiceName(jqGridQueryInfo.getServiceName());
        queryInfo.setQueryType(jqGridQueryInfo.getQueryType());
        queryInfo.setPagingInfo(pagingInfo);
        // sort
        if (StringUtils.isNotBlank(jqGridQueryInfo.getSidx())) {
            queryInfo.setOrderBy(jqGridQueryInfo.getSidx() + " " + jqGridQueryInfo.getSord());
        }
        String queryPrefix = getQueryPrefix(request);
        boolean queryOr = getQueryOr(request);
        List<PropertyFilter> propertyFilters = PropertyFilter.buildFromHttpRequest(request, queryPrefix, queryOr);
        queryInfo.setPropertyFilters(propertyFilters);
        return queryInfo;
    }

    /**
     * @param request
     * @return
     */
    protected static String getQueryPrefix(HttpServletRequest request) {
        String queryPrefix = request.getParameter("queryPrefix");
        if (StringUtils.isBlank(queryPrefix)) {
            queryPrefix = "filter";
        }
        return queryPrefix;
    }

    /**
     * 如何描述该方法
     *
     * @param request
     * @return
     */
    protected static boolean getQueryOr(HttpServletRequest request) {
        String queryOr = request.getParameter("queryOr");
        if (StringUtils.isBlank(queryOr)) {
            return false;
        }
        return "true".equalsIgnoreCase(queryOr);
    }

    @ApiIgnore
    @RequestMapping(value = "/query", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JqGridQueryData query(JqGridQueryInfo jqGridQueryInfo, HttpServletRequest request) {
        QueryInfo queryInfo = buildQueryInfo(jqGridQueryInfo, request);

        QueryData queryData = commonQueryService.query(queryInfo);

        JqGridQueryData jqGridQueryData = convertToJqGridQueryData(queryData);

        return jqGridQueryData;
    }
}
