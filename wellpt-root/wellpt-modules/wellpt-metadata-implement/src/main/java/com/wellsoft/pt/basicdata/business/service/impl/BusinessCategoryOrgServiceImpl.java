/*
 * @(#)2019-02-14 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.business.service.impl;

import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.basicdata.business.dao.BusinessCategoryOrgDao;
import com.wellsoft.pt.basicdata.business.dto.BusinessCategoryOrgDto;
import com.wellsoft.pt.basicdata.business.entity.BusinessCategoryEntity;
import com.wellsoft.pt.basicdata.business.entity.BusinessCategoryOrgEntity;
import com.wellsoft.pt.basicdata.business.service.BusinessCategoryOrgService;
import com.wellsoft.pt.common.generator.service.IdGeneratorService;
import com.wellsoft.pt.dyform.implement.definition.service.FormDefinitionService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserAccountFacadeService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 数据库表BUSINESS_CATEGORY_ORG的service服务接口实现类
 *
 * @author leo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019-02-14.1	leo		2019-02-14		Create
 * </pre>
 * @date 2019-02-14
 */
@Service
public class BusinessCategoryOrgServiceImpl extends
        AbstractJpaServiceImpl<BusinessCategoryOrgEntity, BusinessCategoryOrgDao, String> implements
        BusinessCategoryOrgService {
    public static final String DEFAULT_PWD = "0"; // 默认密码
    private static final String EXTERNAL_ID_PATTERN = IdPrefix.EXTERNAL.getValue() + "0000000000";
    private static final String CATEGORY_ID_PATTERN = IdPrefix.CATEGORY.getValue() + "0000000000";
    @Autowired
    private IdGeneratorService idGeneratorService;
    @Autowired
    private FormDefinitionService formDefinitionService;
    @Autowired
    private MultiOrgUserAccountFacadeService multiOrgUserAccountFacadeService;

    @Transactional
    // 可写事务，处理数据（下一版本回去掉可写事务）
    public TreeNode findAsTree(String categoryId) {
        List<TreeNode> nodes = new ArrayList<TreeNode>();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("businessCategoryUuid", categoryId);
        paramMap.put("parentUuid", TreeNode.ROOT_ID);
        List<BusinessCategoryOrgEntity> list = dao
                .listByHQL(
                        "from BusinessCategoryOrgEntity where businessCategoryUuid = :businessCategoryUuid and parentUuid = :parentUuid order by code asc ",
                        paramMap);
        for (BusinessCategoryOrgEntity po : list) {

            TreeNode node = new TreeNode();
            node.setId(po.getUuid());
            if (BusinessCategoryOrgEntity.TYPE_1.equals(po.getType())) {
                node.setName(po.getName());
            } else {
                node.setName(po.getName());
            }
            if (StringUtils.isBlank(po.getId())) { // 处理旧数据
                po.setId(this.generateId(po.getType()));
                this.dao.save(po);
            }
            nodes.add(node);
            findAsRecursive(node);
        }

        TreeNode node = new TreeNode();
        node.setId(TreeNode.ROOT_ID);
        node.setName("组织结构");
        node.getChildren().addAll(nodes);
        return node;
    }

    private String generateId(String type) {
        if (BusinessCategoryOrgEntity.TYPE_1.equals(type)) {
            return idGeneratorService.generate(BusinessCategoryOrgEntity.class, EXTERNAL_ID_PATTERN);
        } else if (BusinessCategoryOrgEntity.TYPE_2.equals(type)) {
            return idGeneratorService.generate(BusinessCategoryEntity.class, CATEGORY_ID_PATTERN);
        }
        return null;
    }

    private void findAsRecursive(TreeNode parent) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("parentUuid", parent.getId());
        List<BusinessCategoryOrgEntity> childs = this.dao.listByHQL(
                "from BusinessCategoryOrgEntity where parentUuid = :parentUuid order by code asc ", paramMap);
        for (BusinessCategoryOrgEntity child : childs) {
            TreeNode node = new TreeNode();
            node.setId(child.getUuid());
            if (BusinessCategoryOrgEntity.TYPE_1.equals(child.getType())) {
                node.setName(child.getName());
            } else {
                node.setName(child.getName());
            }
            if (StringUtils.isBlank(child.getId())) { // 处理旧数据
                child.setId(this.generateId(child.getType()));
                this.dao.save(child);
            }
            parent.getChildren().add(node);
            findAsRecursive(node);
        }
    }

    @Override
    public BusinessCategoryOrgDto get(String uuid) {
        BusinessCategoryOrgEntity po = this.dao.getOne(uuid);
        if (po == null) {
            return null;
        }
        BusinessCategoryOrgDto dto = new BusinessCategoryOrgDto();
        BeanUtils.copyProperties(po, dto);

        if (TreeNode.ROOT_ID.equals(po.getParentUuid())) {
            dto.setParentName("");
        } else {
            BusinessCategoryOrgEntity parent = this.dao.getOne(po.getParentUuid());
            if (BusinessCategoryOrgEntity.TYPE_1.equals(parent.getType())) {
                dto.setParentName(parent.getDeptValue());
            } else {
                dto.setParentName(parent.getName());
            }
        }
        return dto;
    }

    @Override
    @Transactional
    public String save(BusinessCategoryOrgDto dto) {
        BusinessCategoryOrgEntity po = null;
        if (StringUtils.isBlank(dto.getUuid())) {
            po = new BusinessCategoryOrgEntity();
            BeanUtils.copyProperties(dto, po, IdEntity.BASE_FIELDS);
            po.setId(this.generateId(po.getType()));
        } else {
            po = dao.getOne(dto.getUuid());
            BeanUtils.copyProperties(dto, po, IdEntity.BASE_FIELDS);
        }
        if (BusinessCategoryOrgEntity.TYPE_1.equals(dto.getType())) {
            List<MultiOrgUserAccount> accounts = multiOrgUserAccountFacadeService.queryAllAdminIdsBySystemUnitId(dto.getUnit());
            if (accounts.size() < 1) {
                throw new RuntimeException("该单位尚无管理员，请先添加单位管理员");
            }
            po.setManageUser(accounts.get(0).getId());
            po.setManageUserValue(accounts.get(0).getUserName());
        }
        this.dao.save(po);
        return po.getUuid();
    }

    @Override
    @Transactional
    public void deleteById(String uuid) {
        List<BusinessCategoryOrgEntity> list = new ArrayList<BusinessCategoryOrgEntity>();
        findAsRecursive(list, dao.getOne(uuid));

        for (BusinessCategoryOrgEntity po : list) {
            dao.delete(po);

        }
    }

    private void findAsRecursive(List<BusinessCategoryOrgEntity> list, BusinessCategoryOrgEntity po) {
        list.add(po);
        List<BusinessCategoryOrgEntity> childs = dao.listByFieldEqValue("parentUuid", po.getUuid());
        for (BusinessCategoryOrgEntity child : childs) {
            findAsRecursive(list, child);
        }
    }

    @Override
    public JqGridQueryData queryByManage(JqGridQueryInfo queryInfo, String value) {

        PagingInfo page = new PagingInfo(queryInfo.getPage(), queryInfo.getRows(), true);

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("unitId", SpringSecurityUtils.getCurrentUserUnitId());
        params.put("value", value);
        List<QueryItem> datas = this.dao.listQueryItemByNameSQLQuery("queryByManage", params, page);

        List<BusinessCategoryOrgDto> list = new ArrayList<BusinessCategoryOrgDto>();

        for (QueryItem data : datas) {
            BusinessCategoryOrgDto dto = new BusinessCategoryOrgDto();
            dto.setUuid(data.getString("uuid"));
            dto.setName(data.getString("name"));
            dto.setBusinessCategoryUuid(data.getString("businessCategoryUuid"));
            dto.setManageDeptValue(data.getString("deptValue"));
            dto.setManageUserValue(data.getString("manageUserValue"));
            list.add(dto);
        }
        JqGridQueryData queryData = new JqGridQueryData();
        queryData.setCurrentPage(queryInfo.getPage());
        queryData.setDataList(list);
        queryData.setRepeatitems(false);
        queryData.setTotalPages(page.getTotalPages());
        queryData.setTotalRows(page.getTotalCount());
        return queryData;
    }

    @Override
    public List<BusinessCategoryOrgEntity> listByParentUuid(String uuid) {
        return this.dao.listByFieldEqValue("parentUuid", uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.business.service.BusinessCategoryOrgService#getByCategoryUuidAndDeptId(java.lang.String, java.lang.String)
     */
    @Override
    public BusinessCategoryOrgEntity getByCategoryUuidAndDeptId(String categoryUuid, String deptId) {
        BusinessCategoryOrgEntity entity = new BusinessCategoryOrgEntity();
        entity.setBusinessCategoryUuid(categoryUuid);
        entity.setDept(deptId);
        List<BusinessCategoryOrgEntity> entities = this.dao.listByEntity(entity);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }
        return null;
    }

    @Override
    public BusinessCategoryOrgEntity getBusinessById(String id) {
        return this.dao.getOneByFieldEq("id", id);
    }

    @Override
    @Transactional
    public String updateOldId() {
        List<BusinessCategoryOrgEntity> baklist = this.dao.listBySQL("select * from BUSINESS_CATEGORY_ORG_BAK where type='2' and id like 'E%' ", null);
        Set<String> idSet = new HashSet<>();
        for (BusinessCategoryOrgEntity bak : baklist) {
            idSet.add(bak.getId());
        }
        if (idSet.size() == 0) {
            return null;
        }
        Map<String, Collection<String>> errMap = formDefinitionService.updateBusinessCategoryOrgEntityId(idSet);
        List<BusinessCategoryOrgEntity> list = this.dao.listBySQL("select * from BUSINESS_CATEGORY_ORG  where type='2' and id like 'E%' ", null);
        for (BusinessCategoryOrgEntity categoryOrgEntity : list) {
            categoryOrgEntity.setId(categoryOrgEntity.getId().replaceFirst(IdPrefix.EXTERNAL.getValue(), IdPrefix.CATEGORY.getValue()));
            this.dao.update(categoryOrgEntity);
        }
        if (errMap.size() > 0) {
            return "读取数据表或字段错误，请检查表或字段是否存在：" + JsonUtils.object2Gson(errMap);
        }
        return null;
    }

    @Override
    public List<BusinessCategoryOrgEntity> getBusinessByIds(List<String> ids) {
        return this.dao.listByFieldInValues("id", ids);
    }

    public List<BusinessCategoryOrgEntity> listByUuid(String categoryUuid) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", SpringSecurityUtils.getCurrentUserId());
        paramMap.put("type", BusinessCategoryOrgEntity.TYPE_1);
        paramMap.put("categoryUuid", categoryUuid);
        String sql = "select * from BUSINESS_CATEGORY_ORG where uuid in (" +
                "  select business_category_org_uuid from BUSINESS_ROLE_ORG_USER where INSTR(users, :userId) > 0 and business_category_org_uuid in (" +
                "    select o.uuid from BUSINESS_CATEGORY_ORG o where o.type = :type and o.business_category_uuid = :categoryUuid" +
                "  )" +
                ") order by code ";
        return this.dao.listBySQL(sql, paramMap);
    }
}
