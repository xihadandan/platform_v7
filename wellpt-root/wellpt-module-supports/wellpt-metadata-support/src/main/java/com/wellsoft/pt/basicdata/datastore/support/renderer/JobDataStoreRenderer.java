/*
 * @(#)2016年11月1日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.support.renderer;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.pt.basicdata.datastore.support.RendererParam;
import com.wellsoft.pt.multi.org.entity.MultiOrgTreeNode;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年11月1日.1	xiem		2016年11月1日		Create
 * </pre>
 * @date 2016年11月1日
 */
//@Component
@Deprecated
public class JobDataStoreRenderer extends AbstractSelectiveDatasDataStoreRenderer {
    // private static final String SELECTIVE_CONFIG_KEY = "ORG_JOB_PATH_ID";
    private static final String SELECTIVE_ALL_ELE_KEY = "ORG_ALL_ELE";
    private static final String SHOW_TYPE_KEY = "showType";


    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.DataStoreRenderer#getType()
     */
    @Override
    public String getType() {
        return "jobRenderer";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.DataStoreRenderer#getName()
     */
    @Override
    public String getName() {
        return "职位渲染器";
    }

    @Override
    public Object doRenderData(String columnIndex, Object value, Map<String, Object> rowData, RendererParam param) {
        String showType = param.get(SHOW_TYPE_KEY) == null ? "0" : param.get(SHOW_TYPE_KEY).toString();
        if (value == null || StringUtils.isBlank(value.toString())) {
            return "";
        }

        List<String> names = Lists.newArrayList();
        String[] values = value.toString().split(",|;");
        if ("1".equals(showType)) { // 只渲染当前节点
            for (String v : values) {
                // 需要渲染的是是指全路径， 需要转成离的最近的职位ID
                if (v.startsWith(IdPrefix.ORG_VERSION.getValue())) {
                    v = MultiOrgTreeNode.getNearestEleIdByType(v, IdPrefix.JOB.getValue());
                }
                Object obj = super.doRenderData(columnIndex, v, rowData, param);
                if (obj != null) {
                    names.add(obj.toString());
                }
            }
        } else {
            for (String v : values) {
                if (v.startsWith(IdPrefix.ORG_VERSION.getValue())) {
                    String[] ids = v.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
                    List<String> path = Lists.newArrayList();
                    // 第一个是版本ID,不需要
                    for (int i = ids.length > 2 ? (ids.length - 2) : 1; i < ids.length; i++) {
                        Object obj = super.doRenderData(columnIndex, ids[i], rowData, param);
                        if (obj != null) {
                            path.add(obj.toString());
                        }
                    }
                    String namePath = StringUtils.join(path, MultiOrgService.PATH_SPLIT_SYSMBOL);
                    names.add(namePath);
                } else {
                    Object obj = super.doRenderData(columnIndex, v, rowData, param);
                    if (obj != null) {
                        names.add(obj.toString());
                    }
                }
            }
        }
        return StringUtils.join(names, ";");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.AbstractSelectiveDatasDataStoreRenderer#getConfigKey(com.wellsoft.pt.basicdata.datastore.support.RendererParam)
     */
    @Override
    public String getConfigKey(RendererParam param) {
        return SELECTIVE_ALL_ELE_KEY;
    }
}
