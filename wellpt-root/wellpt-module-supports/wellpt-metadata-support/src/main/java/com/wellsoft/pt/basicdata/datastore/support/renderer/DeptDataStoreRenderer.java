/*
 * @(#)2016年11月1日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.support.renderer;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.pt.basicdata.datastore.support.RendererParam;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.multi.org.entity.MultiOrgTreeNode;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

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
public class DeptDataStoreRenderer extends AbstractSelectiveDatasDataStoreRenderer {
    // private static final String SELECTIVE_NAME_CONFIG_KEY =
    // "ORG_DEPARTMENT_NAME_ID";
    // private static final String SELECTIVE_PATH_CONFIG_KEY =
    // "ORG_DEPARTMENT_PATH_ID";
    private static final String SELECTIVE_ALL_ELE_KEY = "ORG_ALL_ELE";
    private static final String SHOW_TYPE_KEY = "showType";
    @Autowired
    private OrgApiFacade orgApiFacade;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.DataStoreRenderer#getType()
     */
    @Override
    public String getType() {
        return "deptRenderer";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.DataStoreRenderer#getName()
     */
    @Override
    public String getName() {
        return "部门渲染器";
    }

    @Override
    public Object doRenderData(String columnIndex, Object value, Map<String, Object> rowData,
                               RendererParam param) {
        if (value == null || StringUtils.isBlank(value.toString())) {
            return "";
        }
        List<String> names = Lists.newArrayList();
        String[] values = value.toString().split(",|;");

        if (null != param.get(SHOW_TYPE_KEY) && param.get(SHOW_TYPE_KEY).equals("1")) { // 只渲染当前部门
            for (String v : values) {
                // 需要渲染的是是指全路径， 需要转成离的最近的部门ID
                if (v.startsWith(IdPrefix.ORG_VERSION.getValue())) {
                    v = MultiOrgTreeNode.getNearestEleIdByType(v, IdPrefix.DEPARTMENT.getValue());
                } else if (v.startsWith(IdPrefix.USER.getValue())) {
                    v = convertUserId2DeptId(v);
                    if (StringUtils.isBlank(v)) {
                        continue;
                    }
                }
                Object obj = super.doRenderData(columnIndex, v, rowData, param);
                if (obj != null) {
                    names.add(obj.toString());
                }
            }

        } else { // 渲染全路径
            for (String v : values) {
                // 需要渲染的是是指全路径， 需要转成离的最近的部门ID
                if (v.startsWith(IdPrefix.ORG_VERSION.getValue())) {
                    // 从根节点算到最后一个部门节点
                    String lastDept = MultiOrgTreeNode.getNearestEleIdByType(v,
                            IdPrefix.DEPARTMENT.getValue());
                    String[] ids = v.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
                    List<String> path = Lists.newArrayList();
                    // 第一个是版本ID,不需要
                    for (int i = 1; i < ids.length; i++) {
                        if (ids[i].equals(lastDept)) {
                            Object obj = super.doRenderData(columnIndex, ids[i], rowData, param);
                            if (obj != null) {
                                path.add(obj.toString());
                            }
                            // 执行到最后一个了，跳出
                            break;
                        } else {
                            Object obj = super.doRenderData(columnIndex, ids[i], rowData, param);
                            if (obj != null) {
                                path.add(obj.toString());
                            }
                        }
                    }
                    String namePath = StringUtils.join(path, MultiOrgService.PATH_SPLIT_SYSMBOL);
                    names.add(namePath);
                    continue;
                } else if (v.startsWith(IdPrefix.USER.getValue())) {
                    v = convertUserId2DeptId(v);
                    if (StringUtils.isBlank(v)) {
                        continue;
                    }
                }

                Object obj = super.doRenderData(columnIndex, v, rowData, param);
                if (obj != null) {
                    names.add(obj.toString());
                }
            }
        }
        return StringUtils.join(names, ";");
    }

    private String convertUserId2DeptId(String userId) {
        OrgUserVo orgUserVo = orgApiFacade.getUserVoById(userId);
        return orgUserVo.getMainDepartmentName();
    }


    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.AbstractSelectiveDatasDataStoreRenderer#getConfigKey(com.wellsoft.pt.basicdata.datastore.support.RendererParam)
     */
    @Override
    public String getConfigKey(RendererParam param) {
        return SELECTIVE_ALL_ELE_KEY;
        // if ("1".equals(param.getString(SHOW_TYPE_KEY))) {
        // return SELECTIVE_NAME_CONFIG_KEY;
        // }
        // return SELECTIVE_PATH_CONFIG_KEY;
    }
}
