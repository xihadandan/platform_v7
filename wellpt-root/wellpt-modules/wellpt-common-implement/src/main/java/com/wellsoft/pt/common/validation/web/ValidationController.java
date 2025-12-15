/*
 * @(#)2016年2月1日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.validation.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.common.validation.support.FieldValidationUtils;
import com.wellsoft.pt.jpa.util.ClassUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
 * 2016年2月1日.1	zhulh		2016年2月1日		Create
 * </pre>
 * @date 2016年2月1日
 */
@Controller
@RequestMapping(value = "/common/validation")
public class ValidationController extends BaseController {

    /**
     * @param beanName
     * @return
     */
    @RequestMapping(value = "/metadata")
    @ResponseBody
    public Map<String, Object> getMetadata(@RequestParam(value = "beanName") String beanName) {
        Class<?> cls = ClassUtils.getEntityClasses().get(beanName);
        try {
            if (cls == null) {
                cls = Class.forName(beanName);
            }
            Map<String, Object> metaData = FieldValidationUtils.getJSMetaData(cls.newInstance());
            return metaData;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

}
