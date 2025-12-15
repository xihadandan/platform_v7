/*
 * @(#)Jan 5, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.facade.service.impl;

import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.dms.entity.DmsFolderEntity;
import com.wellsoft.pt.dms.entity.DmsRoleEntity;
import com.wellsoft.pt.dms.file.facade.service.FileManagerComponentService;
import com.wellsoft.pt.dms.file.view.FileDataView;
import com.wellsoft.pt.dms.file.view.FileManagerThumbnailViewWithBootstrapTableDataView;
import com.wellsoft.pt.dms.service.DmsFolderService;
import com.wellsoft.pt.dms.service.DmsRoleService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Jan 5, 2018.1	zhulh		Jan 5, 2018		Create
 * </pre>
 * @date Jan 5, 2018
 */
@Service
@Transactional(readOnly = true)
public class FileManagerComponentServiceImpl extends BaseServiceImpl implements FileManagerComponentService {

    @Autowired
    private DmsFolderService dmsFolderService;

    @Autowired(required = false)
    private List<FileDataView> dataViews;

    @Autowired
    private DmsRoleService dmsRoleService;

    public static void buildChildNodes(TreeNode node, Map<String, List<DmsFolderEntity>> ddMap) {
        String key = node.getId();
        List<DmsFolderEntity> ddList = ddMap.get(key);
        if (ddList == null) {
            return;
        }
        for (DmsFolderEntity dd : ddList) {
            TreeNode child = new TreeNode();
            child.setId(dd.getUuid());
            child.setName(dd.getName());
            child.setData(dd.getUuid());
            node.getChildren().add(child);
            buildChildNodes(child, ddMap);
        }
    }

    /**
     * 列出文件库
     *
     * @return
     */
    @Override
    public List<DmsFolderEntity> listFileLibrary() {
        return dmsFolderService.listFileLibrary();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.facade.service.FileManagerComponentService#getFolderTree()
     */
    @Override
    public TreeNode getFolderTree() {
        TreeNode treeNode = new TreeNode();
        treeNode.setName("文件库");
        treeNode.setId(UUID.randomUUID().toString());
        treeNode.setNocheck(true);

        List<DmsFolderEntity> topList = new ArrayList<DmsFolderEntity>();
        Map<String, List<DmsFolderEntity>> ddMap = new HashMap<String, List<DmsFolderEntity>>();
        DmsFolderEntity example = new DmsFolderEntity();
        example.setStatus("1");
        List<DmsFolderEntity> allList = dmsFolderService.findByExample(example);
        for (DmsFolderEntity dd : allList) {
            String parentUuid = dd.getParentUuid();
            if (StringUtils.isNotBlank(parentUuid)) {
                if (!ddMap.containsKey(parentUuid)) {
                    ddMap.put(parentUuid, new ArrayList<DmsFolderEntity>());
                }
                ddMap.get(parentUuid).add(dd);
            } else {
                topList.add(dd);
            }
        }

        for (DmsFolderEntity dd : topList) {
            TreeNode node = new TreeNode();
            node.setId(dd.getUuid());
            node.setName(dd.getName());
            node.setData(dd.getUuid());

            // 生成子结点
            buildChildNodes(node, ddMap);
            treeNode.getChildren().add(node);
        }
        return treeNode;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.facade.service.FileManagerComponentService#getDataViewSelectData(com.wellsoft.pt.common.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData getDataViewSelectData(Select2QueryInfo queryInfo) {
        Select2QueryData select2Data = new Select2QueryData();
        OrderComparator.sort(this.dataViews);
        for (FileDataView dataView : this.dataViews) {
            select2Data.addResultData(new Select2DataBean(dataView.getType(), dataView.getName()));
        }
        return select2Data;
    }

    @Override
    public Select2QueryData getDmsRoleSelectData(Select2QueryInfo queryInfo) {
        DmsRoleEntity example = new DmsRoleEntity();
        example.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        List<DmsRoleEntity> dmsRoleEntities = dmsRoleService.findByExample(example);
        return new Select2QueryData(dmsRoleEntities, "uuid", "name");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.facade.service.FileManagerComponentService#getDefaultDataViewSelectData(com.wellsoft.pt.common.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData getDefaultDataViewSelectData(Select2QueryInfo queryInfo) {
        Select2QueryData select2Data = new Select2QueryData();
        OrderComparator.sort(this.dataViews);
        for (FileDataView dataView : this.dataViews) {
            if (dataView instanceof FileManagerThumbnailViewWithBootstrapTableDataView) {
                continue;
            }
            select2Data.addResultData(new Select2DataBean(dataView.getType(), dataView.getName()));
        }
        return select2Data;
    }

}
