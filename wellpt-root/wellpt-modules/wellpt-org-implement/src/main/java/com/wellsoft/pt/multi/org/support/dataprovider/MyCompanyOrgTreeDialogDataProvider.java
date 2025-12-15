/*
 * @(#)2017年12月18日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.support.dataprovider;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.multi.org.bean.OrgTreeDialogParams;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeDto;
import com.wellsoft.pt.multi.org.entity.MultiOrgVersion;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgVersionFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
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
 * 2017年12月18日.1	zyguo		2017年12月18日		Create
 * </pre>
 * @date 2017年12月18日
 */
@Component
public class MyCompanyOrgTreeDialogDataProvider extends AbstractOrgTreeDialogServiceImpl implements
        OrgTreeDialogDataProvider {
    public static final String TYPE = "MyCompany";
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 4702994745463524516L;
    @Autowired
    private MultiOrgVersionFacade multiOrgVersionService;
    @Autowired
    private MultiOrgService multiOrgService;

    @Override
    public String getType() {
        return TYPE;
    }

    /**
     * 1，先算出我的当前组织版本
     * 1, 然后算出有包含我的组织版本的所有版本信息，然后找到源头
     */
    private List<OrgTreeNodeDto> queryMyCompanyNodeListOfCurrentVersion(String unitId) {
        // 我的归属单位
        List<MultiOrgVersion> verList = this.multiOrgVersionService.queryCurrentActiveVersionListOfSystemUnit(unitId);
        if (CollectionUtils.isEmpty(verList)) {
            return null;
        }
        List<OrgTreeNodeDto> nodeList = new ArrayList<OrgTreeNodeDto>();
        for (MultiOrgVersion ver : verList) {
            List<OrgTreeNodeDto> list = this.multiOrgService.computeMyCompanyListByVersionId(ver);
            if (!CollectionUtils.isEmpty(list)) {
                nodeList.addAll(list);
            }
        }
        return removeRepeatObjFormList(nodeList);

    }

    @Override
    public List<TreeNode> provideData(OrgTreeDialogParams p) {
        // 是否需要包含用户数据，默认不需要
        int isNeedUser = p.getIsNeedUser(); // 是否返回用户数据
        String orgVersionId = p.getOrgVersionId();
        List<TreeNode> treeNodeList = new ArrayList<TreeNode>();
        // 如果没有指定用户，则用当前登录用户ID
        String userId = p.getUserId();
        if (StringUtils.isBlank(userId)) {
            userId = SpringSecurityUtils.getCurrentUserId();
        }

        List<OrgTreeNodeDto> myCompanyList = new ArrayList<OrgTreeNodeDto>();
        // 如果有指定版本，则采用指定版本的职位列表，没有指定版本，则采用当前单位的最新版本
        if (StringUtils.isBlank(orgVersionId)) {
            // 如果有指定单位，则返回指定单位的，如果没有则返回用户的当前单位
            String unitId = p.getUnitId();
            if (StringUtils.isBlank(unitId)) {
                unitId = SpringSecurityUtils.getCurrentUserUnitId();
            }
            myCompanyList = this.queryMyCompanyNodeListOfCurrentVersion(unitId);
        } else {
            MultiOrgVersion ver = this.multiOrgVersionService.getOrgVersionById(orgVersionId);
            if (ver != null) {
                myCompanyList = this.multiOrgService.computeMyCompanyListByVersionId(ver);
            }
        }
        treeNodeList = createOrgTreeFromNodeList(myCompanyList, isNeedUser, false);
        return treeNodeList;
    }
}
