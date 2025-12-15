/*
 * @(#)2013-10-12 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.log.web;

import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.PropertyFilter;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.log.service.ElasticSearchLogService;
import com.wellsoft.pt.log.support.ElasticSearchSysLogParams;
import com.wellsoft.pt.log.support.LogRecord;
import com.wellsoft.pt.log.support.SystemLogAppender;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
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
 * 2013-10-12.1	zhulh		2013-10-12		Create
 * </pre>
 * @date 2013-10-12
 */
@Controller
@RequestMapping("/log/system")
public class SystemLogController extends BaseController {

    @Resource
    ElasticSearchLogService elasticSearchLogService;

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
     * 打开系统日志页面
     *
     * @return
     */
    @RequestMapping(value = "")
    public String systemLog(Model model) {
        model.addAttribute("beginTime", DateFormatUtils.format(new Date(), "yyyy-MM-dd 00:00:00"));
        model.addAttribute("endTime", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        return forward("/log/system_log");
    }

    @RequestMapping(value = "/get")
    @ResponseBody
    public List<LogRecord> getSystemLog(@RequestParam("fromLineNumber") long fromLineNumber) {
        return SystemLogAppender.getLogRecords(fromLineNumber);
    }

    @RequestMapping(value = "/querySysLog", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JqGridQueryData querySysLog(JqGridQueryInfo jqGridQueryInfo,
                                       HttpServletRequest request) {
        JqGridQueryData jqGridQueryData = new JqGridQueryData();
        try {
            ElasticSearchSysLogParams params = new ElasticSearchSysLogParams();
            String queryPrefix = getQueryPrefix(request);
            List<PropertyFilter> propertyFilters = PropertyFilter.buildFromHttpRequest(request,
                    queryPrefix, false);
            for (PropertyFilter pf : propertyFilters) {
                if (pf.getPropertyName().equalsIgnoreCase("message")) {
                    params.setMessage(pf.getMatchValue().toString());
                }

                if (pf.getPropertyName().equalsIgnoreCase("loglevel")) {
                    params.setLogLevel(pf.getMatchValue().toString());
                }

                if (pf.getPropertyName().equalsIgnoreCase("beginTime")) {
                    params.setBeginTime(new Date(Long.parseLong(pf.getMatchValue().toString())));
                }
                if (pf.getPropertyName().equalsIgnoreCase("endTime")) {
                    params.setEndTime(new Date(Long.parseLong(pf.getMatchValue().toString())));
                }

            }
            params.setPage(
                    new PagingInfo(jqGridQueryInfo.getPage() - 1, jqGridQueryInfo.getRows()));
            QueryData queryData = elasticSearchLogService.querySysLogs(params);

            jqGridQueryData.setDataList(queryData.getDataList());
            jqGridQueryData.setCurrentPage(queryData.getPagingInfo().getCurrentPage() + 1);
            jqGridQueryData.setTotalPages(queryData.getPagingInfo().getTotalPages());
            jqGridQueryData.setTotalRows(queryData.getPagingInfo().getTotalCount());
        } catch (NoNodeAvailableException noNodeExcept) {
            logger.error("日志全文服务节点异常：", noNodeExcept);
        } catch (Exception e) {
            logger.error("查询日志信息异常：", e);
        }
        return jqGridQueryData;
    }


}
