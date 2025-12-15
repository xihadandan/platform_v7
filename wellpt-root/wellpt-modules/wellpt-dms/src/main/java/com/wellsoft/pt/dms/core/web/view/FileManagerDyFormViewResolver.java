/*
 * @(#)Jan 23, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.web.view;

import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.pt.dms.config.support.Configuration;
import com.wellsoft.pt.dms.config.support.FileStore;
import com.wellsoft.pt.dms.config.support.Store;
import com.wellsoft.pt.dms.core.context.ActionContext;
import com.wellsoft.pt.dms.core.web.View;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

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
 * Jan 23, 2018.1	zhulh		Jan 23, 2018		Create
 * </pre>
 * @date Jan 23, 2018
 */
@Component
public class FileManagerDyFormViewResolver extends DyFormViewResolver {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.web.view.DyFormViewResolver#resolveView(com.wellsoft.pt.dms.core.context.ActionContext, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public View resolveView(ActionContext actionContext, HttpServletRequest request) {
        Configuration configuration = actionContext.getConfiguration();
        Store store = configuration.getStore(actionContext, request);
        if (store == null) {
            throw new BusinessException("文件夹定义的夹文件数据定义没有配置，请配置后再试！");
        }
        String dataType = store.getDataType();
        if (store instanceof FileStore && isSupportTypeOfDyForm(dataType)) {
            String folderUuid = request.getParameter("fd_id");
            String fileUuid = request.getParameter("doc_id");
            String formUuid = request.getParameter("doc_def_uuid");
            if (StringUtils.isBlank(formUuid)) {
                formUuid = store.getFormUuid();
            }
            return new FileManagerDyFormView(folderUuid, fileUuid, formUuid, getIdValue(request));
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.web.view.AbstractViewResolver#getOrder()
     */
    @Override
    public int getOrder() {
        return 10;
    }

}
