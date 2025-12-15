/*
 * @(#)10/21/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.utils;

import com.google.common.collect.Maps;
import com.wellsoft.pt.biz.service.impl.BizProcessItemInstanceServiceImpl;
import com.wellsoft.pt.jpa.template.TemplateEngine;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 10/21/22.1	zhulh		10/21/22		Create
 * </pre>
 * @date 10/21/22
 */
public class ProcessTitleUtils {

    private static Logger LOG = LoggerFactory.getLogger(ProcessTitleUtils.class);

    private ProcessTitleUtils() {
    }

    /**
     * @param processName
     * @return
     */
    public static String generateProcessInstanceTitle(String processName) {
        return generateTitle(processName);
    }

    /**
     * @param processNodeName
     * @return
     */
    public static String generateProcessNodeInstanceTitle(String processNodeName) {
        return generateTitle(processNodeName);
    }

    /**
     * @param itemName
     * @return
     */
    public static String generateItemInstanceTitle(String itemName) {
        return generateTitle(itemName);
    }

    /**
     * @param name
     * @return
     */
    private static String generateTitle(String name) {
        String titleExpression = "${名称}_${发起人姓名}-${发起人所在部门名称}_${年}-${月}-${日}";
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        // 变量
        Map<Object, Object> variables = Maps.newHashMap();
        variables.put("名称", name);
        variables.put("发起人姓名", userDetails.getUserName());
        variables.put("发起人所在部门名称", userDetails.getMainDepartmentName());
        TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
        templateEngine.addDefaultConstant(variables);
        String title = StringUtils.EMPTY;
        try {
            title = templateEngine.process(titleExpression, variables);
        } catch (Exception e) {
            LOG.error("生成标题出错！", e);
        }
        return title;
    }

}
