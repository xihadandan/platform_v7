/*
 * @(#)2016年6月23日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.facade.service.impl;

import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.facade.service.AppThemeMgr;
import com.wellsoft.pt.app.theme.Theme;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
 * 2016年6月23日.1	zhulh		2016年6月23日		Create
 * </pre>
 * @date 2016年6月23日
 */
@Service
public class AppThemeMgrImpl implements AppThemeMgr {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.component.select2.Select2QueryApi#loadSelectData(com.wellsoft.pt.common.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData loadSelectData(Select2QueryInfo select2QueryInfo) {
        return new Select2QueryData(AppContextHolder.getContext().getAllThemes(), "id", "name");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.component.select2.Select2QueryApi#loadSelectDataByIds(com.wellsoft.pt.common.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData loadSelectDataByIds(Select2QueryInfo select2QueryInfo) {
        String[] ids = select2QueryInfo.getIds();
        List<Select2DataBean> beans = new ArrayList<Select2DataBean>();
        for (String id : ids) {
            Theme theme = AppContextHolder.getContext().getTheme(id);
            if (theme == null) {
                continue;
            }
            Select2DataBean bean = new Select2DataBean(theme.getId(), theme.getName());
            beans.add(bean);
        }
        return new Select2QueryData(beans);
    }

}
