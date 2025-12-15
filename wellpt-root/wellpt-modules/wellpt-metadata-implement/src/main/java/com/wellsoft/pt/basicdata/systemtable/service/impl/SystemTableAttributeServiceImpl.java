package com.wellsoft.pt.basicdata.systemtable.service.impl;

import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.systemtable.dao.SystemTableAttributeDao;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTable;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTableAttribute;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTableRelationship;
import com.wellsoft.pt.basicdata.systemtable.service.SystemTableAttributeService;
import com.wellsoft.pt.basicdata.systemtable.service.SystemTableRelationshipService;
import com.wellsoft.pt.basicdata.systemtable.service.SystemTableService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Description: 系统表结构服务层实现类
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-21.1	zhouyq		2013-3-21		Create
 * </pre>
 * @date 2013-3-21
 */
@Service
public class SystemTableAttributeServiceImpl extends
        AbstractJpaServiceImpl<SystemTableAttribute, SystemTableAttributeDao, String> implements
        SystemTableAttributeService {

    @Autowired
    private SystemTableRelationshipService systemTableRelationshipService;

    @Autowired
    private SystemTableService systemTableService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.service.SystemTableService#query(com.wellsoft.pt.common.component.jqgrid.JqGridQueryInfo)
     */
    @Override
    public JqGridQueryData query(JqGridQueryInfo queryInfo) {
        PagingInfo pageData = new PagingInfo(queryInfo.getPage(), queryInfo.getRows(), true);
        List<SystemTableAttribute> systemTableAttributes = this.dao.listAllByOrderPage(pageData, null);
        List<SystemTableAttribute> jqUsers = new ArrayList<SystemTableAttribute>();
        for (SystemTableAttribute systemTableAttribute : systemTableAttributes) {
            SystemTableAttribute jqSystemTableAttribute = new SystemTableAttribute();
            BeanUtils.copyProperties(systemTableAttribute, jqSystemTableAttribute);
            jqUsers.add(jqSystemTableAttribute);
        }
        JqGridQueryData queryData = new JqGridQueryData();
        queryData.setCurrentPage(queryInfo.getPage());
        queryData.setDataList(jqUsers);
        queryData.setRepeatitems(false);
        queryData.setTotalPages(pageData.getTotalPages());
        queryData.setTotalRows(pageData.getTotalCount());
        return queryData;
    }

    /**
     * 根据表的uuid返回表的所有字段的集合
     *
     * @param tableUuid
     * @return
     */
    @Override
    public List<SystemTableAttribute> getSystemTableColumns(String tableUuid) {
        SystemTable systemTable = systemTableService.getOne(tableUuid);
        List<SystemTableAttribute> systemTableAttributeList = dao.listByFieldEqValue("systemTable", systemTable);
        return systemTableAttributeList;
    }

    /**
     * 根据表的uuid返回主表及从表属性的集合
     *
     * @param tableUuid
     * @return
     */
    @Override
    public List<SystemTableAttribute> getAttributesByrelationship(String tableUuid) {
        SystemTable systemTable = systemTableService.getOne(tableUuid);// 获得系统表
        List<SystemTableAttribute> allSystemTableAttributeList = new ArrayList<SystemTableAttribute>();//
        List<SystemTableAttribute> systemTableAttributeList = getSystemTableColumns(tableUuid);// 获得本身表所有属性
        for (SystemTableAttribute systemTableAttribute : systemTableAttributeList) {
            allSystemTableAttributeList.add(systemTableAttribute);
        }
        // 获得关联表属性
        List<SystemTableRelationship> systemTableRelationshipList = systemTableRelationshipService
                .queryBySystemTableUuid(systemTable.getUuid());
        for (SystemTableRelationship systemTableRelationship : systemTableRelationshipList) {
            String secondaryTableName = systemTableRelationship.getSecondaryTableName();// 从表名
            SystemTable secondaryTable = systemTableService.getByFullEntityName(secondaryTableName);
            String secondaryTableUuid = secondaryTable.getUuid();
            List<SystemTableAttribute> secondarySystemTableAttributeList = getSystemTableColumns(secondaryTableUuid);// 获得本身表所有属性
            for (SystemTableAttribute systemTableAttribute : secondarySystemTableAttributeList) {
                allSystemTableAttributeList.add(systemTableAttribute);
            }
        }
        return allSystemTableAttributeList;

    }

    /**
     * 根据表的uuid返回主表及从表属性的集合(返回TreeNode)
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.systemtable.service.SystemTableAttributeService#getAttributesTreeNodeByrelationship(java.lang.String)
     */
    @Override
    public List<TreeNode> getAttributesTreeNodeByrelationship(String s, String tableUuid) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        List<SystemTableAttribute> allSystemTableAttributeList = getAttributesByrelationship(tableUuid);
        for (Iterator iterator2 = allSystemTableAttributeList.iterator(); iterator2.hasNext(); ) {
            TreeNode treeNode = new TreeNode();
            treeNode.setId("-1");
            SystemTableAttribute fd = (SystemTableAttribute) iterator2.next();
            SystemTableAttribute fdNew = new SystemTableAttribute();
            BeanUtils.copyProperties(fd, fdNew);
            if (fdNew.getChineseName() == null || fdNew.getChineseName().equals("")) {
                treeNode.setName(fdNew.getAttributeName());
            } else {
                treeNode.setName(fdNew.getChineseName());
            }
            treeNode.setId(fdNew.getAttributeName());
            treeNode.setData(QueryItem.getKey(fdNew.getColumnAliases()));
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.systemtable.service.SystemTableAttributeService#getAttributesByrelationship2(java.lang.String)
     */
    @Override
    public List<SystemTableRelationship> getAttributesByrelationship2(String tableUuid) {
        SystemTable systemTable = systemTableService.getOne(tableUuid);// 获得系统表
        List<SystemTableAttribute> allSystemTableAttributeList = new ArrayList<SystemTableAttribute>();//
        List<SystemTableAttribute> systemTableAttributeList = getSystemTableColumns(tableUuid);// 获得本身表所有属性
        for (SystemTableAttribute systemTableAttribute : systemTableAttributeList) {
            allSystemTableAttributeList.add(systemTableAttribute);
        }
        // 获得关联表属性
        List<SystemTableRelationship> systemTableRelationshipList = systemTableRelationshipService
                .queryBySystemTableUuid(systemTable.getUuid());
        return systemTableRelationshipList;
    }

    /**
     * 获得系统表
     *
     * @param tableUuid
     * @return
     */
    public SystemTable getTable(String tableUuid) {
        return systemTableService.getOne(tableUuid);// 获得系统表
    }

    @Override
    public List<SystemTableAttribute> getBySystemTableUuid(String uuid) {
        return this.dao.listBySQL("select * from cd_system_table_entity_attr where system_table_uuid='" + uuid + "'",
                null);
    }

}
