/*
 * @(#)6 Mar 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.support.treecomponent;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.basicdata.treecomponent.facade.support.*;
import com.wellsoft.pt.multi.org.bean.OrgTreeDialogParams;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgTreeDialogService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgVersionFacade;
import com.wellsoft.pt.multi.org.support.dataprovider.MyCompanyOrgTreeDialogDataProvider;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 6 Mar 2017.1	zyguo		6 Mar 2017		Create
 * </pre>
 * @date 6 Mar 2017
 */
@Component
public class MyCompanyTreeComponentDataProvider extends AbstractTreeComponentDataProvider implements
        TreeComponentDataProvider {

    @Autowired
    private MultiOrgTreeDialogService multiOrgTreeDialogService;
    @Autowired
    private MultiOrgVersionFacade multiOrgVersionService;

    @Override
    public List<TreeType> getNodeTypes() {
        List<TreeType> types = new ArrayList<TreeType>();
        types.add(new TreeType(IdPrefix.ORG.getValue(), "组织"));
        types.add(new TreeType(IdPrefix.BUSINESS_UNIT.getValue(), "业务单位"));
        types.add(new TreeType(IdPrefix.DEPARTMENT.getValue(), "部门"));
        types.add(new TreeType(IdPrefix.JOB.getValue(), "职位"));
        types.add(new TreeType(IdPrefix.USER.getValue(), "用户"));
        return types;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeComponentDataProvider#getName()
     */
    @Override
    public String getName() {
        return "多组织-我的集团";
    }

    // 同步加载数据
    @Override
    public List<TreeNode> loadAllTreeData(TreeComponentRequestParam requestParam) {
        OrgTreeDialogParams p = new OrgTreeDialogParams();
        String unitId = SpringSecurityUtils.getCurrentUserUnitId();
        if (StringUtils.isBlank(unitId)) {
            throw new RuntimeException("多组织-我的单位，unitId不能为空");
        }
        p.setUnitId(unitId);
        p.setIsNeedUser(isNeedUser(requestParam.getTreeNodeConfigs()));
        String dataFilter = requestParam.getDataFilter();

        if (StringUtils.isNotBlank(dataFilter)) {
            HashMap<String, String> json = JsonUtils.json2Object(dataFilter, HashMap.class);
            // 同时配置了orgVersionId 和 functionType的话，则以orgVersionId为准
            String orgVersionId = json.get("orgVersionId");
            if (StringUtils.isNotBlank(orgVersionId)) {
                p.setOrgVersionId(orgVersionId);
            } else {
                String functionType = json.get("functionType");
                if (StringUtils.isNotBlank(functionType)) {
                    orgVersionId = this.multiOrgVersionService.getCurrentActiveVersionByFunctionType(unitId,
                            functionType);
                    p.setOrgVersionId(orgVersionId);
                }
            }
        }
        return this.multiOrgTreeDialogService.queryUnitTreeDialogDataByType(MyCompanyOrgTreeDialogDataProvider.TYPE, p);
    }

    private Integer isNeedUser(ArrayList<TreeNodeConfig> noteTypeInfos) {
        return MyUnitTreeComponentDataProvider.isNeedUser(noteTypeInfos);
    }

    // 数据数量的使用说明
    @Override
    public String getFilterHint() {
        return "支持参数{functionType:指定的组织类型, orgVersionId:指定组织版本}，指定functionType则取该类型的当前启用版本,指定orgVersionId则取该指定版本的数据，orgVersionId的优先级高于functionType";
    }

}
