/*
 * @(#)2015年8月20日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.cg.core.sniffer.impl;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.cg.core.Context;
import com.wellsoft.pt.cg.core.source.DyformSource;
import com.wellsoft.pt.cg.core.source.Source;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
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
 * 2015年8月20日.1	zhulh		2015年8月20日		Create
 * </pre>
 * @date 2015年8月20日
 */
public class DyformSniffer extends AbstractSniffer {
    public static final String PARAM_DYFORMS = "dyforms";// 上下文的数据字段
    public static final String DECOLLATOR = ";";// 分隔符号

    private DyFormFacade dyFormApiFacade;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.cg.core.sniffer.Sniffer#sniffer(com.wellsoft.pt.cg.core.Context)
     */
    @Override
    public Source sniffer(Context context) {
        if (dyFormApiFacade == null) {
            dyFormApiFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
        }
        DyformSource dyformSource = new DyformSource();
        String formDefUuid = (String) context.getParam(PARAM_DYFORMS);
        if (!StringUtils.isNotBlank(formDefUuid)) {
            throw new RuntimeException("Do not specify dyform ");
        }
        String[] fs = formDefUuid.split(DECOLLATOR);
        for (String dyform : fs) {
            DyFormFormDefinition dyFormDefinition = dyFormApiFacade.getFormDefinition(dyform);
            dyformSource.add(dyFormDefinition);
        }
        return dyformSource;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.cg.core.sniffer.Sniffer#impact(com.wellsoft.pt.cg.core.Context, com.wellsoft.pt.cg.core.source.Source)
     */
    @Override
    public void impact(Context context, Source source) {
        context.setSource(source);
    }

}
