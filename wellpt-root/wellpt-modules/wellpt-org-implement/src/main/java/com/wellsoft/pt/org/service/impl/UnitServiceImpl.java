/*
 * @(#)2013-2-18 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service.impl;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.KeyValuePair;
import com.wellsoft.pt.basicdata.datadict.dto.CdDataDictionaryItemDto;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.cache.config.CacheName;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.org.bean.UnitBean;
import com.wellsoft.pt.org.bean.UnitMemberBean;
import com.wellsoft.pt.org.dao.UnitDao;
import com.wellsoft.pt.org.dao.UnitMemberDao;
import com.wellsoft.pt.org.entity.Unit;
import com.wellsoft.pt.org.entity.UnitMember;
import com.wellsoft.pt.org.entity.User;
import com.wellsoft.pt.org.service.UnitService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
 * 2013-2-18.1	zhulh		2013-2-18		Create
 * </pre>
 * @date 2013-2-18
 */
@Service
@Transactional
public class UnitServiceImpl extends BaseServiceImpl implements UnitService {
    private static final String UNIT_ID_PATTERN = "O0000000000";

    @Autowired
    private UnitDao unitDao;

    @Autowired
    private UnitMemberDao unitMemberDao;

    @Autowired
    private BasicDataApiFacade basicDataApiFacade;

    @Autowired
    private OrgApiFacade orgApiFacade;

    @Autowired
    private com.wellsoft.pt.common.generator.service.IdGeneratorService idGeneratorService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UnitService#getById(java.lang.String)
     */
    @Override
    @Cacheable(value = CacheName.DEFAULT)
    public Unit getById(String unitId) {
        Unit target = new Unit();
        Unit unit = unitDao.findUniqueBy("id", unitId);
        if (unit == null) {
            return unit;
        }
        BeanUtils.copyProperties(unit, target);
        return target;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UnitService#getBean(java.lang.String)
     */
    @Override
    public UnitBean getBean(String uuid) {
        UnitBean bean = new UnitBean();
        Unit unit = this.unitDao.get(uuid);
        BeanUtils.copyProperties(unit, bean);

        // 1、获取父节点
        Unit parent = unit.getParent();
        if (parent != null) {
            bean.setParentUuid(parent.getUuid());
        }

        // 2、获取组织单元成员-用户
        Set<String> userIdSet = new HashSet<String>();
        Map<String, String> userCache = bean.getUserCache();
        Set<UnitMemberBean> memberBeans = new LinkedHashSet<UnitMemberBean>();
        Set<UnitMember> members = unit.getMembers();
        for (UnitMember child : members) {
            UnitMemberBean memberBean = new UnitMemberBean();
            BeanUtils.copyProperties(child, memberBean);
            memberBean.setUnitUuid(unit.getUuid());
            memberBean.setUnitName(unit.getName());
            memberBeans.add(memberBean);

            // 获取用户成员的用户
            if (UnitMember.MEMBER_TYPE_USER.equals(memberBean.getMemberType())) {
                String[] userIds = StringUtils.split(memberBean.getMember(), Separator.SEMICOLON.getValue());
                for (String userId : userIds) {
                    userIdSet.add(userId);
                }
            } else {
                userCache.put(memberBean.getMember(), memberBean.getMember());
            }
        }
        bean.setMemberBeans(memberBeans);

        // 用户ID、用户名放入缓存
        List<MultiOrgUserAccount> users = orgApiFacade.queryUserAccountListByIds(Arrays.asList(userIdSet
                .toArray(new String[0])));
        for (MultiOrgUserAccount user : users) {
            userCache.put(user.getId(), user.getUserName());
        }
        bean.setUserCache(userCache);

        return bean;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UnitService#saveBean(com.wellsoft.pt.org.bean.UnitBean)
     */
    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public void saveBean(UnitBean bean) {
        Unit unit = new Unit();
        if (StringUtils.isNotBlank(bean.getUuid())) {
            unit = this.unitDao.get(bean.getUuid());
        } else {
            // bean.setId(IdPrefix.UNIT.getValue() + UUID.randomUUID());
            String id = idGeneratorService.generate(Unit.class, UNIT_ID_PATTERN);
            String tenantId = SpringSecurityUtils.getCurrentTenantId();
            id = id.substring(0, 1) + tenantId.substring(1, tenantId.length()) + id.substring(4, 11);
            bean.setId(id);
            bean.setTenantId(tenantId);
        }
        BeanUtils.copyProperties(bean, unit);

        // 1、设置父节点
        if (StringUtils.isNotBlank(bean.getParentUuid())) {
            unit.setParent(this.unitDao.get(bean.getParentUuid()));
        }

        // 2、设置组织单元成员用户及邮件地址
        Set<UnitMemberBean> memberBeans = bean.getChangedMemberBeans();
        for (UnitMemberBean memberBean : memberBeans) {
            if (StringUtils.isNotBlank(memberBean.getUuid())) {
                UnitMember member = this.unitMemberDao.get(memberBean.getUuid());
                BeanUtils.copyProperties(memberBean, member);
                this.unitMemberDao.save(member);
            } else {
                UnitMember member = new UnitMember();
                BeanUtils.copyProperties(memberBean, member);
                member.setUuid(null);
                member.setUnit(unit);
                this.unitMemberDao.save(member);
            }
        }

        this.unitDao.save(unit);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UnitService#remove(java.lang.String)
     */
    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public void remove(String uuid) {
        this.unitDao.delete(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UnitService#getAsTree(java.lang.String)
     */
    @Override
    public TreeNode getAsTree(String excludeUuid) {
        TreeNode treeNode = new TreeNode();
        if (TreeNode.ROOT_ID.equals(excludeUuid)) {
            treeNode.setId(TreeNode.ROOT_ID);
            treeNode.setName("组织单元");
        }
        List<Unit> units = this.unitDao.getTopLevel();
        buildTree(treeNode, units, excludeUuid);
        return treeNode;
    }

    /**
     * @param treeNode
     * @param groups
     * @param excludeUuid
     */
    private void buildTree(TreeNode treeNode, List<Unit> units, String excludeUuid) {
        List<TreeNode> children = new ArrayList<TreeNode>();
        for (Unit unit : units) {
            if (unit.getUuid().equals(excludeUuid)) {
                continue;
            }
            if (StringUtils.isBlank(unit.getType())) {
                continue;
            }
            TreeNode child = new TreeNode();
            child.setId(unit.getUuid());
            child.setName(unit.getType().equals("1") ? unit.getCategory() : unit.getName());
            child.setData(unit.getType());

            children.add(child);
            if (unit.getChildren().size() != 0) {
                buildTree(child, unit.getChildren(), excludeUuid);
            }
        }
        treeNode.setChildren(children);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UnitService#getCurrentUserUnits()
     */
    @Override
    @Cacheable(value = CacheName.DEFAULT)
    public List<Unit> getUserUnits(String userUuid) {
        List<Unit> units = unitMemberDao.getUnitByUser(userUuid);
        return BeanUtils.convertCollection(units, Unit.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UnitService#getBusinessTypes(java.lang.String)
     */
    @Override
    public KeyValuePair getBusinessTypes(String bussinessType) {
        KeyValuePair keyValuePair = new KeyValuePair();
        List<CdDataDictionaryItemDto> dataDictionaries = basicDataApiFacade.getDataDictionariesByType(bussinessType);
        for (CdDataDictionaryItemDto dataDictionary : dataDictionaries) {
            keyValuePair.put(dataDictionary.getValue(), dataDictionary.getLabel());
        }
        return keyValuePair;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UnitService#getLeafUnits(java.lang.String)
     */
    @Override
    public List<Unit> getLeafUnits(String id) {
        List<Unit> units = new ArrayList<Unit>();
        Unit unit = this.unitDao.getById(id);
        traverseAndAddLeaf(units, unit.getChildren());
        return units;
    }

    /**
     * @param unit
     * @param children
     */
    private void traverseAndAddLeaf(List<Unit> units, List<Unit> children) {
        for (Unit child : children) {
            if (child.getChildren().size() == 0) {
                units.add(child);
            } else {
                traverseAndAddLeaf(units, child.getChildren());
            }
        }
    }

    List<User> getByTenantId(String tenantId) {
        String hql = "from User u where u.tenantId = :tenantId";
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("tenantId", tenantId);
        return this.dao.query(hql, map);
    }
}
