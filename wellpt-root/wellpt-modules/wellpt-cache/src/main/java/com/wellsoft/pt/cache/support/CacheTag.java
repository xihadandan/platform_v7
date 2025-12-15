/*
 * @(#)2019年9月21日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.cache.support;

import com.wellsoft.pt.cache.FrontCacheUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年9月21日.1	zhongzh		2019年9月21日		Create
 * </pre>
 * @date 2019年9月21日
 */
public class CacheTag extends TagSupport {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private String module;

    /**
     * @return the module
     */
    public String getModule() {
        return module;
    }

    /**
     * @param module 要设置的module
     */
    public void setModule(String module) {
        this.module = module;
    }

    @Override
    public int doStartTag() throws JspException {
        try {
            pageContext.getOut().write(String.valueOf(FrontCacheUtils.get(module)));
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return SKIP_BODY;
    }

}
