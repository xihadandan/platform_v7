/*
 * @(#)2015-8-13 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.cg.core.generator.model;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.cg.core.Context;
import com.wellsoft.pt.cg.core.Type;
import com.wellsoft.pt.cg.core.service.NavigationGeneratorService;
import com.wellsoft.pt.cg.core.source.TableSource;

import java.util.List;
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
 * 2015-8-13.1	zhulh		2015-8-13		Create
 * </pre>
 * @date 2015-8-13
 */
public class NavigationByTableModel extends AbstractModel {
    private static final int MODELCODE = Type.OUTPUTTYPE_NAVIGATION;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.cg.core.generator.Model#getCode()
     */
    @Override
    public int getCode() {
        return MODELCODE;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.cg.core.generator.Model#work(com.wellsoft.pt.cg.core.Context)
     */
    @Override
    public void work(Context context) {
        TableSource source = (TableSource) context.getSource();

        for (Map.Entry<String, List<TableSource.Column>> entry : source.getTables().entrySet()) {
            String tableName = entry.getKey();
            ApplicationContextHolder.getBean(NavigationGeneratorService.class).generate(context, tableName);
        }
    }

}
