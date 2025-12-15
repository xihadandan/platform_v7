/*
 * @(#)2012-10-30 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.acl.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.Page;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.jpa.dao.UniversalDao;
import com.wellsoft.pt.jpa.support.QueryItemResultTransformer;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.acl.entity.*;
import com.wellsoft.pt.security.acl.service.*;
import com.wellsoft.pt.security.acl.support.AclPermission;
import com.wellsoft.pt.security.acl.support.QueryInfo;
import com.wellsoft.pt.security.acl.util.AclUtil;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-10-30.1	zhulh		2012-10-30		Create
 * </pre>
 * @date 2012-10-30
 */
@Service
@Transactional(readOnly = true)
public class AclServiceImpl implements AclService {

    private static final String QUERY_ITEM_BASIC_NO_PRINCIPAL = "select acl_object_identity.objectIdIdentity "
            + "from AclObjectIdentity acl_object_identity inner join acl_object_identity.aclSid as acli_sid "
            + "inner join acl_object_identity.aclClass as acl_class	"
            + "inner join acl_object_identity.aclEntries as acl_entry	" + "inner join acl_entry.aclSid as acl_sid "
            + "where o.uuid = acl_object_identity.objectIdIdentity "
            + "and (acl_class.cls = :cls and acl_entry.granting = :granting and acl_entry.mask in (:masks) "
            + " and acl_sid.sid in (:sids)) ";
    /**
     * 基于群组成员的查询
     */
    private static final String QUERY_BASE_MEMBER = "select acl_entry.uuid " + "from AclEntry acl_entry "
            + "where o.uuid = acl_entry.objectIdIdentity and (acl_entry.granting = :granting and acl_entry.mask in (:masks) "
            + "and exists (select id from AclSidMember acl_sid_member where acl_sid_member.aclSid = acl_entry.aclSid "
            + "and acl_sid_member.member = :member and acl_sid_member.moduleId = :moduleId) )";
    /**
     * 基于SID及群组成员的查询
     */
    private static final String QUERY_ALL = "select acl_entry.uuid " + "from AclEntry acl_entry "
            + "where o.uuid = acl_entry.objectIdIdentity "
            + "and (acl_entry.granting = :granting and acl_entry.mask in (:masks) "
            + "and (exists (select id from AclSid all_acl_sid where all_acl_sid = acl_entry.aclSid and ((all_acl_sid.sid = :sid and all_acl_sid.principal = :principal) or all_acl_sid.sid like 'ROLE_%')) "
            + "or exists (select id from AclSidMember acl_sid_member where acl_sid_member.aclSid = acl_entry.aclSid "
            + "and acl_sid_member.member = :member and acl_sid_member.moduleId = :moduleId)) )";
    /**
     * 基于SID及群组成员的查询
     */
    private static final String QUERY_ALL_ITEM = "select acl_entry.uuid " + "from AclEntry acl_entry "
            + "where o.uuid = acl_entry.objectIdIdentity "
            + "and (acl_entry.granting = :granting and acl_entry.mask in (:masks) "
            + "and (exists (select id from AclSid all_acl_sid where all_acl_sid = acl_entry.aclSid and ((all_acl_sid.sid = :sid and all_acl_sid.principal = :principal) or all_acl_sid.sid like 'ROLE_%')) "
            + "or exists (select id from AclSidMember acl_sid_member where acl_sid_member.aclSid = acl_entry.aclSid "
            + "and acl_sid_member.member = :member and acl_sid_member.moduleId = :moduleId)) )";
    /**
     * 查询对实体的权限
     */
    private static final String HAS_PERMISSION_BASIC = "select count(*) from AclEntry acl_entry where acl_entry.mask = :mask	"
            + "and acl_entry.aclSid.uuid = (select acl_sid.uuid from AclSid acl_sid where acl_sid.sid = :sid and acl_sid.principal = :principal)	"
            + "and acl_entry.aclObjectIdentity.uuid = (select acl_object_identity.uuid	"
            + "from AclObjectIdentity acl_object_identity where acl_object_identity.aclClass.cls = :cls	"
            + "and acl_object_identity.objectIdIdentity = :objectIdIdentity)	";
    private static final String HAS_PERMISSION_BASIC_NEW = "select count(*) from AclEntry acl_entry where acl_entry.mask = :mask	"
            + "and acl_entry.aclSid.uuid = (select acl_sid.uuid from AclSid acl_sid where acl_sid.sid = :sid and acl_sid.principal = :principal)	"
            + "and acl_entry.objectIdIdentity = :objectIdIdentity	";
    /**
     * 获取当前实体的权限列表
     */
    private static final String GET_PERMISSION_BASIC = "select acl_entry.mask from AclEntry acl_entry where 	"
            + "acl_entry.aclSid.uuid = (select acl_sid.uuid from AclSid acl_sid where acl_sid.sid = :sid and acl_sid.principal = :principal)	"
            + "and acl_entry.aclObjectIdentity.uuid = (select acl_object_identity.uuid	"
            + "from AclObjectIdentity acl_object_identity where acl_object_identity.aclClass.cls = :cls	"
            + "and acl_object_identity.objectIdIdentity = :objectIdIdentity)	";
    /**
     * 获取对实体有权限访问的SID列表
     */
    private static final String GET_Sid_BASIC2 = "select acl_sid from AclSid acl_sid where exists (select id from AclEntry acl_entry where "
            + " acl_entry.aclSid = acl_sid and exists (select id from AclObjectIdentity aclObjectIdentity "
            + " where aclObjectIdentity = acl_entry.aclObjectIdentity and aclObjectIdentity.objectIdIdentity = :objectIdIdentity "
            + " and exists (select id from AclClass acl_class where acl_class = aclObjectIdentity.aclClass and acl_class.cls = :cls)))";
    /**
     * 获取对实体有指定权限访问的SID列表
     */
    private static final String GET_Sid_WITH_PERMISSION_BASIC2 = "select acl_sid from AclSid acl_sid where exists (select id from AclEntry acl_entry where "
            + " acl_entry.aclSid = acl_sid and acl_entry.mask = :mask and exists (select id from AclObjectIdentity aclObjectIdentity "
            + " where aclObjectIdentity = acl_entry.aclObjectIdentity and aclObjectIdentity.objectIdIdentity = :objectIdIdentity "
            + " and exists (select id from AclClass acl_class where acl_class = aclObjectIdentity.aclClass and acl_class.cls = :cls)))";
    private static final String GET_Sid_WITH_PERMISSION_BASIC2_NEW = "from AclSid t1 where t1.uuid in(select t2.aclSid.uuid from AclEntry t2 where t2.mask = :mask and t2.objectIdIdentity = :objectIdIdentity)";
    /**
     * 获取成员在moduleId对实体具有的所有权限
     */
    private static final String GET_PERMISSION_BY_MODULE_ID = "select distinct acl_entry.mask from AclEntry acl_entry inner join acl_entry.aclObjectIdentity acl_object_identity "
            + "inner join acl_entry.aclSid acl_sid where acl_object_identity.objectIdIdentity = :objectIdIdentity "
            + "and exists (select id from AclSidMember acl_sid_member where acl_sid_member.aclSid = acl_sid "
            + "and acl_sid_member.member = :member  and acl_sid_member.moduleId = :moduleId )";
    @Resource(name = "universalDao")
    protected UniversalDao dao;
    @Resource(name = "nativeDao")
    protected NativeDao nativeDao;
    @Autowired
    private AclSidService aclSidService;

    @Autowired
    private AclClassService aclClassService;

    @Autowired
    private AclObjectIdentityService aclObjectIdentityService;

    @Autowired
    private AclEntryService aclEntryService;

    @Autowired
    private AclSidMemberService aclSidMemberService;

    @Autowired
    private OrgApiFacade orgApiFacade;

    /**
     * 获取实际实体类类名，除去javassist代理类的问题
     *
     * @param entity
     * @return
     */
    private static <ENTITY> String getEntityClassName(ENTITY entity) {
        return ClassUtils.getUserClass(entity).getCanonicalName();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#get(java.lang.Class, java.io.Serializable)
     */
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> ENTITY get(Class<ENTITY> entityClass, Serializable entityUuid) {
        return this.get(entityClass, entityUuid, BasePermission.READ);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#get(java.lang.Class, java.io.Serializable, org.springframework.security.acls.model.Permission)
     */
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> ENTITY get(Class<ENTITY> entityClass, Serializable entityUuid,
                                                Permission permission) {
        return this.get(entityClass, entityUuid, permission, getUsername());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#get(java.lang.Class, java.io.Serializable, org.springframework.security.acls.model.Permission, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> ENTITY get(Class<ENTITY> entityClass, Serializable entityUuid,
                                                Permission permission, String sid) {
        return this.get(entityClass, entityUuid, permission, sid, sid.startsWith(PREFIX_USERNAME));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#get(java.lang.Class, java.io.Serializable, org.springframework.security.acls.model.Permission, java.lang.String, java.lang.Boolean)
     */
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> ENTITY get(Class<ENTITY> entityClass, Serializable entityUuid,
                                                Permission permission, String sid, Boolean principal) {
        // 权限
        List<Permission> permissions = new ArrayList<Permission>();
        permissions.add(permission);

        // 用户或角色
        List<String> sids = new ArrayList<String>();
        sids.add(sid);

        return this.get(entityClass, entityUuid, permissions, sids, principal);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#get(java.lang.Class, java.io.Serializable, java.util.List, java.util.List)
     */
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> ENTITY get(Class<ENTITY> entityClass, Serializable entityUuid,
                                                List<Permission> permissions, List<String> sids) {
        return this.get(entityClass, entityUuid, permissions, sids, null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#get(java.lang.Class, java.io.Serializable, java.util.List, java.util.List, java.lang.Boolean)
     */
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> ENTITY get(Class<ENTITY> entityClass, Serializable entityUuid,
                                                List<Permission> permissions, List<String> sids, Boolean principal) {
        // 增加管理员权限查询条件
        // permissions.add(BasePermission.ADMINISTRATION);

        List<Integer> masks = new ArrayList<Integer>();
        for (Permission permission : permissions) {
            masks.add(permission.getMask());
        }
        String entityName = entityClass.getCanonicalName();
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("cls", entityName);
        params.put("entityName", entityName);
        params.put("objectIdIdentity", entityUuid);
        params.put("masks", masks.toArray());
        params.put("granting", Boolean.TRUE);
        params.put("sids", sids.toArray());
        if (principal != null) {
            params.put("principal", principal);
        }
        List list = this.dao.namedQuery("basicAclEntityDataQuery", params, entityClass);
        return CollectionUtils.isNotEmpty(list) ? (ENTITY) list.get(0) : null;

    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#getAll(java.lang.Class)
     */
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> List<ENTITY> getAll(Class<ENTITY> entityClass) {
        return getAll(entityClass, BasePermission.READ);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#getAll(java.lang.Class, org.springframework.security.acls.model.Permission)
     */
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> List<ENTITY> getAll(Class<ENTITY> entityClass, Permission permission) {
        return getAll(entityClass, BasePermission.READ, getUsername());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#getAll(java.lang.Class, org.springframework.security.acls.model.Permission, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> List<ENTITY> getAll(Class<ENTITY> entityClass, Permission permission, String sid) {
        return getAll(entityClass, BasePermission.READ, sid, sid.startsWith(PREFIX_USERNAME));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#getAll(java.lang.Class, java.lang.String, boolean, org.springframework.security.acls.model.Permission, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> List<ENTITY> getAll(Class<ENTITY> entityClass, String orderByProperty,
                                                         boolean isAsc, Permission permission, String sid) {
        // 权限
        List<Permission> permissions = new ArrayList<Permission>();
        permissions.add(permission);

        // 用户或角色
        List<String> sids = new ArrayList<String>();
        sids.add(sid);

        return getAll(entityClass, orderByProperty, isAsc, permissions, sids, null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#getAll(java.lang.Class, org.springframework.security.acls.model.Permission, java.lang.String, java.lang.Boolean)
     */
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> List<ENTITY> getAll(Class<ENTITY> entityClass, Permission permission, String sid,
                                                         Boolean principal) {
        // 权限
        List<Permission> permissions = new ArrayList<Permission>();
        permissions.add(permission);

        // 用户或角色
        List<String> sids = new ArrayList<String>();
        sids.add(sid);

        return getAll(entityClass, permissions, sids, principal);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#getAll(java.lang.Class, java.util.List, java.util.List)
     */
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> List<ENTITY> getAll(Class<ENTITY> entityClass, List<Permission> permissions,
                                                         List<String> sids) {
        return getAll(entityClass, permissions, sids, null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#getAll(java.lang.Class, java.util.List, java.util.List, java.lang.Boolean)
     */
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> List<ENTITY> getAll(Class<ENTITY> entityClass, List<Permission> permissions,
                                                         List<String> sids, Boolean principal) {
        // 查询信息，不统计全部数量
        QueryInfo<ENTITY> queryInfo = new QueryInfo<ENTITY>();
        queryInfo.getPage().setAutoCount(false);

        this.query(entityClass, queryInfo, permissions, sids, principal);

        return queryInfo.getPage().getResult();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#getAll(java.lang.Class, java.util.List, java.util.List, java.lang.Boolean)
     */
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> List<ENTITY> getAll(Class<ENTITY> entityClass, String orderByProperty,
                                                         boolean isAsc, List<Permission> permissions, List<String> sids, Boolean principal) {
        // 查询信息，不统计全部数量
        QueryInfo<ENTITY> queryInfo = new QueryInfo<ENTITY>();
        queryInfo.getPage().setAutoCount(false);
        queryInfo.addOrderby(orderByProperty, isAsc ? "asc" : "desc");

        this.query(entityClass, queryInfo, permissions, sids, principal);

        return queryInfo.getPage().getResult();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#query(java.lang.Class, com.wellsoft.pt.security.acl.support.QueryInfo)
     */
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> QueryInfo<ENTITY> query(Class<ENTITY> entityClass, QueryInfo<ENTITY> queryInfo) {
        return query(entityClass, queryInfo, BasePermission.READ);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#query(java.lang.Class, com.wellsoft.pt.security.acl.support.QueryInfo, org.springframework.security.acls.model.Permission)
     */
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> QueryInfo<ENTITY> query(Class<ENTITY> entityClass, QueryInfo<ENTITY> queryInfo,
                                                             Permission permission) {
        return query(entityClass, queryInfo, permission, getUsername());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#query(java.lang.Class, com.wellsoft.pt.security.acl.support.QueryInfo, org.springframework.security.acls.model.Permission, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> QueryInfo<ENTITY> query(Class<ENTITY> entityClass, QueryInfo<ENTITY> queryInfo,
                                                             Permission permission, String sid) {
        return query(entityClass, queryInfo, permission, sid, sid.startsWith(PREFIX_USERNAME));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#query(java.lang.Class, com.wellsoft.pt.security.acl.support.QueryInfo, org.springframework.security.acls.model.Permission, java.lang.String, java.lang.Boolean)
     */
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> QueryInfo<ENTITY> query(Class<ENTITY> entityClass, QueryInfo<ENTITY> queryInfo,
                                                             Permission permission, String sid, Boolean principal) {
        // 权限
        List<Permission> permissions = new ArrayList<Permission>();
        permissions.add(permission);

        // 用户或角色
        List<String> sids = new ArrayList<String>();
        sids.add(sid);

        return query(entityClass, queryInfo, permissions, sids, principal);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#query(java.lang.Class, com.wellsoft.pt.security.acl.support.QueryInfo, java.util.List, java.util.List)
     */
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> QueryInfo<ENTITY> query(Class<ENTITY> entityClass, QueryInfo<ENTITY> queryInfo,
                                                             List<Permission> permissions, List<String> sids) {
        return query(entityClass, queryInfo, permissions, sids, null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#query(java.lang.Class, com.wellsoft.pt.security.acl.support.QueryInfo, java.util.List, java.util.List, java.lang.Boolean)
     */
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> QueryInfo<ENTITY> query(Class<ENTITY> entityClass, QueryInfo<ENTITY> queryInfo,
                                                             List<Permission> permissions, List<String> sids, Boolean principal) {
        // 增加管理员权限查询条件
        // permissions.add(BasePermission.ADMINISTRATION);

        List<Integer> masks = new ArrayList<Integer>();
        for (Permission permission : permissions) {
            masks.add(permission.getMask());
        }

        String entityName = entityClass.getCanonicalName();
        String whereHql = AclUtil.buildWhereHql(queryInfo);
        String orderBy = AclUtil.buildOrderby(queryInfo);

        // 分页信息
        Page<ENTITY> page = queryInfo.getPage();

        Map<String, Object> params = Maps.newHashMap();
        if (principal != null) {
            params.put("principal", principal);
        }
        params.put("cls", entityName);
        params.put("masks", masks.toArray());
        params.put("granting", Boolean.TRUE);
        params.put("sids", sids.toArray());
        params.put("entityName", entityName);
        params.put("whereHql", whereHql);
        params.put("orderBy", orderBy);
        params.putAll(queryInfo.getQueryParams());
        PagingInfo pagingInfo = new PagingInfo(page.getFirst() - 1, page.getPageSize(),
                queryInfo.getPage().isAutoCount());
        List list = this.dao.namedQuery("basicAclEntityDataQuery", params, entityClass, pagingInfo);
        page.setResult(list);
        page.setTotalCount(pagingInfo.getTotalCount());

        return queryInfo;
    }



    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> List<QueryItem> queryForItem(Class<ENTITY> entityClass,
                                                                  QueryInfo<ENTITY> queryInfo, Permission permission, String sid) {
        boolean principal = sid.startsWith(PREFIX_USERNAME);
        String entityName = entityClass.getCanonicalName();
        String whereHql = AclUtil.buildWhereHql(queryInfo);
        String orderBy = AclUtil.buildOrderby(queryInfo);
        String selections = queryInfo.getSelectionHql();
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("cls", entityName);
        params.put("entityName", entityName);
        params.put("selections", selections);
        params.put("masks", permission.getMask());
        params.put("granting", Boolean.TRUE);
        params.put("sid", sid);
        params.put("principal", principal);
        params.put("orderBy", orderBy);
        params.put("whereHql", whereHql);
        params.putAll(queryInfo.getQueryParams());
        Page<ENTITY> page = queryInfo.getPage();
        if (page != null) {
            PagingInfo pagingInfo = new PagingInfo(page.getPageNo(), page.getPageSize(), page.isAutoCount());
            return this.dao.namedQuery("basicAclEntityDataQuery", params, QueryItem.class, pagingInfo);
        } else {
            return this.dao.namedQuery("basicAclEntityDataQuery", params, QueryItem.class);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#queryForItem(java.lang.Class, com.wellsoft.pt.security.acl.support.QueryInfo, java.util.List, java.util.List)
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> List<QueryItem> queryForItem(Class<ENTITY> entityClass,
                                                                  QueryInfo<ENTITY> queryInfo, List<Permission> permissions, List<String> sids) {
        List<Integer> masks = new ArrayList<Integer>();
        for (Permission permission : permissions) {
            masks.add(permission.getMask());
        }
        // 查询实休可访问的id
        String hql = QUERY_ITEM_BASIC_NO_PRINCIPAL;

        String entityName = entityClass.getCanonicalName();
        String whereHql = "1 = 1" + AclUtil.buildWhereHql(queryInfo);
        String queryHql = "select " + queryInfo.getSelectionHql() + " from " + entityName + " o where " + whereHql
                + " and exists (" + hql + ")" + AclUtil.buildOrderby(queryInfo);
        Query query = this.dao.getSession().createQuery(queryHql);
        // query.setCacheable(true);
        queryInfo.addQueryParams("cls", entityName);
        queryInfo.addQueryParams("masks", masks.toArray());
        queryInfo.addQueryParams("granting", true);
        queryInfo.addQueryParams("sids", sids.toArray());

        // 设置查询参数
        AclUtil.setQueryParams(query, queryInfo.getQueryParams());
        // 分页信息
        Page<ENTITY> page = queryInfo.getPage();
        if (page.getPageSize() != -1) {
            query.setFirstResult(page.getFirst() - 1);
            query.setMaxResults(page.getPageSize());
        }
        query.setResultTransformer(QueryItemResultTransformer.INSTANCE);
        List<QueryItem> queryItems = query.list();

        // 计算总数
        if (page.isAutoCount()) {
            query = this.dao.getSession().createQuery(
                    "select count(*) from " + entityName + " o where " + whereHql + " and exists (" + hql + ")");
            // query.setCacheable(true);
            AclUtil.setQueryParams(query, queryInfo.getQueryParams());
            page.setTotalCount((Long) query.uniqueResult());
        }
        return queryItems;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#save(com.wellsoft.pt.core.entity.IdEntity)
     */
    @Override
    @Transactional
    public <ENTITY extends IdEntity> void save(ENTITY entity) {
        save(entity, getUsername());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#save(com.wellsoft.pt.core.entity.IdEntity, java.lang.String)
     */
    @Override
    @Transactional
    public <ENTITY extends IdEntity> void save(ENTITY entity, String sid) {
        save(entity, sid, BasePermission.ADMINISTRATION);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#save(com.wellsoft.pt.core.entity.IdEntity, java.lang.String, org.springframework.security.acls.model.Permission)
     */
    @Override
    @Transactional
    public <ENTITY extends IdEntity> void save(ENTITY entity, String sid, Permission permission) {
        // 增加权限
        addPermission(entity, permission, sid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#save(com.wellsoft.pt.core.entity.IdEntity, com.wellsoft.pt.core.entity.IdEntity)
     */
    @Override
    @Transactional
    public <ENTITY extends IdEntity> void save(ENTITY entity, ENTITY parentEntity) {
        save(entity, parentEntity, getUsername());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#save(com.wellsoft.pt.core.entity.IdEntity, com.wellsoft.pt.core.entity.IdEntity, java.lang.String)
     */
    @Override
    @Transactional
    public <ENTITY extends IdEntity> void save(ENTITY entity, ENTITY parentEntity, String sid) {
        save(entity, sid, BasePermission.ADMINISTRATION);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#save(com.wellsoft.pt.core.entity.IdEntity, com.wellsoft.pt.core.entity.IdEntity, java.lang.String, org.springframework.security.acls.model.Permission)
     */
    @Override
    @Transactional
    public <ENTITY extends IdEntity> void save(ENTITY entity, ENTITY parentEntity, String sid, Permission permission) {
        save(entity, sid, permission);

        // 当前父acl
        AclObjectIdentity parentAclObjectIdentity = aclObjectIdentityService
                .findByObjectId(entity.getClass().getCanonicalName(), parentEntity.getUuid());
        // 当前父acl
        AclObjectIdentity aclObjectIdentity = aclObjectIdentityService.findByObjectId(getEntityClassName(entity),
                entity.getUuid());
        aclObjectIdentity.setAclObjectIdentity(parentAclObjectIdentity);
        aclObjectIdentityService.save(aclObjectIdentity);
        // aclObjectIdentityService.flush();
    }

    /**
     * 获取当前的用户名作用ACL中的默认SID
     *
     * @return
     */
    private String getUsername() {
        return SpringSecurityUtils.getCurrentUserId();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#remove(com.wellsoft.pt.core.entity.IdEntity)
     */
    @Override
    @Transactional
    public <ENTITY extends IdEntity> void remove(ENTITY entity) {
        // 删除实体相关的acl权限
        removeAcl(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#removeAll(java.util.Collection)
     */
    @Override
    @Transactional
    public <ENTITY extends IdEntity> void removeAll(Collection<ENTITY> entities) {
        for (ENTITY entity : entities) {
            removeAcl(entity);
        }
    }

    /**
     * 删除实体相关的acl权限
     *
     * @param entity
     */
    private <ENTITY extends IdEntity> void removeAcl(ENTITY entity) {
        // 当前acl
        AclObjectIdentity aclObjectIdentity = aclObjectIdentityService.findByObjectId(getEntityClassName(entity),
                entity.getUuid());
        if (aclObjectIdentity != null) {
            aclObjectIdentityService.delete(aclObjectIdentity);
            // aclObjectIdentityService.flush();
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#removeByPk(java.lang.Class, java.lang.String)
     */
    @Override
    @Transactional
    public <ENTITY extends IdEntity> void removeByPk(Class<ENTITY> entityClass, String entityUuid) {
        removeAcl(entityClass, entityUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#removeAllByPk(java.lang.Class, java.util.Collection)
     */
    @Override
    @Transactional
    public <ENTITY extends IdEntity> void removeAllByPk(Class<ENTITY> entityClass, Collection<String> entityUuids) {
        for (String entityUuid : entityUuids) {
            removeAcl(entityClass, entityUuid);
        }
    }

    /**
     * 删除实体相关的acl权限
     *
     * @param entity
     */
    @Override
    @Transactional
    public <ENTITY extends IdEntity> void removeAcl(Class<ENTITY> entityClass, String entityUuid) {
        // 当前acl
        AclObjectIdentity aclObjectIdentity = aclObjectIdentityService.findByObjectId(entityClass.getCanonicalName(),
                entityUuid);
        if (aclObjectIdentity != null) {
            aclObjectIdentityService.delete(aclObjectIdentity);
            // aclObjectIdentityService.flush();
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#changeOwner(java.lang.Class, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public <ENTITY extends IdEntity> void changeOwner(Class<ENTITY> entityClass, String entityUuid, String sid) {
        // 创建或获取SID
        AclSid aclSid = new AclSid(sid.startsWith(PREFIX_USERNAME), sid);
        aclSid = aclSidService.get(aclSid);
        if (aclSid == null) {
            aclSid = new AclSid(sid.startsWith(PREFIX_USERNAME), sid);
            aclSidService.save(aclSid);
        }
        // 获取当前ACL
        AclObjectIdentity aclObjectIdentity = aclObjectIdentityService.findByObjectId(entityClass.getCanonicalName(),
                entityUuid);
        // 更新ACE的SID
        Set<AclEntry> aclEntries = aclObjectIdentity.getAclEntries();
        for (AclEntry aclEntry : aclEntries) {
            aclEntry.setAclSid(aclSid);
        }
        // 更新ACE的SID
        aclObjectIdentity.setAclSid(aclSid);
        // 保存更新
        aclObjectIdentityService.save(aclObjectIdentity);
        // aclObjectIdentityService.flush();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#changeAcl(java.lang.Class, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public <ENTITY extends IdEntity> void changeAcl(Class<ENTITY> entityClass, String entityUuid,
                                                    String newEntityUuid) {
        if (StringUtils.isBlank(entityUuid) || StringUtils.isBlank(newEntityUuid)) {
            return;
        }
        // 获取当前ACL
        AclObjectIdentity aclObjectIdentity = aclObjectIdentityService.findByObjectId(entityClass.getCanonicalName(),
                entityUuid);
        if (aclObjectIdentity != null) {
            aclObjectIdentity.setObjectIdIdentity(newEntityUuid);
            // 保存更新
            aclObjectIdentityService.save(aclObjectIdentity);

            // 更新冗余的关联表
            Map<String, Object> values = new HashMap<String, Object>();
            values.put("objectIdIdentity", newEntityUuid);
            values.put("aclObjectIdentityUuid", aclObjectIdentity.getUuid());
            this.dao.namedExecute("updateAclObjectIdIdentity", values);
            // this.dao.flush();
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#mergeAcl(java.lang.Class, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public <ENTITY extends IdEntity> void mergeAcl(Class<ENTITY> entityClass, String sourceEntityUuid,
                                                   String targetEntityUuid) {
        // //获取当前ACL
        // String entityClassName = entityClass.getCanonicalName();
        // AclObjectIdentity sourceAclObjectIdentity =
        // aclObjectIdentityService.findByObjectId(entityClassName,
        // sourceEntityUuid);
        // AclObjectIdentity targetAclObjectIdentity =
        // aclObjectIdentityService.findByObjectId(entityClassName,
        // targetEntityUuid);
        // if (sourceAclObjectIdentity != null) {
        // if(targetAclObjectIdentity == null){
        // aclObjectIdentity.setObjectIdIdentity(newEntityUuid);
        // //保存更新
        // aclObjectIdentityService.save(aclObjectIdentity);
        // }else{
        // // 更新冗余的关联表
        // Set<AclEntry> soruceEntries =
        // targetAclObjectIdentity.getAclEntries();
        // Set<AclEntry> targetEntries =
        // targetAclObjectIdentity.getAclEntries();
        // for (AclEntry aclEntry : soruceEntries) {
        // aclEntry.setObjectIdIdentity(newEntityUuid);
        // aclEntryService.save(aclEntry);
        // }
        // }
        // }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#addPermission(com.wellsoft.pt.core.entity.IdEntity, org.springframework.security.acls.model.Permission)
     */
    @Override
    @Transactional
    public <ENTITY extends IdEntity> void addPermission(ENTITY entity, Permission permission) {
        this.addPermission(entity, permission, getUsername());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#addPermission(com.wellsoft.pt.core.entity.IdEntity, org.springframework.security.acls.model.Permission, java.lang.String)
     */
    @Override
    @Transactional
    public <ENTITY extends IdEntity> void addPermission(ENTITY entity, Permission permission, String sid) {
        this.addPermission(entity, permission, sid, sid.startsWith(PREFIX_USERNAME));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#addPermission(com.wellsoft.pt.core.entity.IdEntity, org.springframework.security.acls.model.Permission, java.lang.String, java.lang.Boolean)
     */
    private <ENTITY extends IdEntity> void addPermission(ENTITY entity, Permission permission, String sid,
                                                         Boolean principal) {
        // 增加权限
        // if (!this.hasPermission(entity, permission, sid, principal)) {
        String entityClassName = getEntityClassName(entity);
        AclClass aclClass = aclClassService.getByClasssName(entityClassName);
        if (aclClass == null) {
            aclClass = new AclClass();
            aclClass.setCls(entityClassName);
            aclClassService.save(aclClass);
        }
        String objectIdIdentity = entity.getUuid();
        AclSid aclSid = new AclSid(principal, sid);
        aclSid = aclSidService.get(aclSid);
        if (aclSid == null) {
            aclSid = new AclSid(principal, sid);
            aclSidService.save(aclSid);
        }
        // 当前ACL
        AclObjectIdentity aclObjectIdentity = aclObjectIdentityService.findByObjectId(entityClassName,
                entity.getUuid());
        if (aclObjectIdentity == null) {
            aclObjectIdentity = new AclObjectIdentity(aclClass, objectIdIdentity, aclSid, Boolean.TRUE);
            aclObjectIdentityService.save(aclObjectIdentity);
            // aclObjectIdentityService.flush();
        }

        AclEntry aclEntry = new AclEntry();
        aclEntry.setAclObjectIdentity(aclObjectIdentity);
        aclEntry.setAclSid(aclSid);

        // Integer ace =
        // aclEntryService.getMaxAceByAclObjectIdentity(aclObjectIdentity.getId());
        // int aceOrder = ace == null ? 0 : ace + 1;
        // aclEntry.setAceOrder(aceOrder);
        // aclEntry.setAuditFailure(Boolean.FALSE);
        // aclEntry.setAuditSuccess(Boolean.FALSE);
        aclEntry.setObjectIdIdentity(objectIdIdentity);
        aclEntry.setGranting(Boolean.TRUE);
        aclEntry.setMask(permission.getMask());
        aclEntryService.save(aclEntry);
        // aclEntryService.flush();
        // aclObjectIdentity.getAclEntries().add(aclEntry);
        // aclObjectIdentityService.save(aclObjectIdentity);
        // }
    }

    // /**
    // * (non-Javadoc)
    // * @see
    // com.wellsoft.pt.security.acl.service.AclService#removePermission(com.wellsoft.pt.core.entity.IdEntity,
    // org.springframework.security.acls.model.Permission)
    // */
    // @Override
    // public <ENTITY extends IdEntity> void removePermission(ENTITY entity,
    // Permission permission) {
    // removePermission(entity, permission, getUsername());
    // }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#addPermission(java.lang.Class, java.lang.String, org.springframework.security.acls.model.Permission, java.lang.String)
     */
    @Override
    @Transactional
    public <ENTITY extends IdEntity> void addPermission(Class<ENTITY> entityClass, String entityUuid,
                                                        Permission permission, String sid) {
        // 增加权限
        // if (!this.hasPermission(entity, permission, sid, principal)) {
        String entityClassName = entityClass.getCanonicalName();
        boolean principal = sid.startsWith(PREFIX_USERNAME);
        AclClass aclClass = aclClassService.getByClasssName(entityClassName);
        if (aclClass == null) {
            aclClass = new AclClass();
            aclClass.setCls(entityClassName);
            aclClassService.save(aclClass);
        }
        String objectIdIdentity = entityUuid;
        AclSid aclSid = new AclSid(principal, sid);
        aclSid = aclSidService.get(aclSid);
        if (aclSid == null) {
            aclSid = new AclSid(principal, sid);
            aclSidService.save(aclSid);
        }
        // 当前ACL
        AclObjectIdentity aclObjectIdentity = aclObjectIdentityService.findByObjectId(entityClassName, entityUuid);
        if (aclObjectIdentity == null) {
            aclObjectIdentity = new AclObjectIdentity(aclClass, objectIdIdentity, aclSid, Boolean.TRUE);
            aclObjectIdentityService.save(aclObjectIdentity);
            // aclObjectIdentityService.flush();
        }

        AclEntry aclEntry = new AclEntry();
        aclEntry.setAclObjectIdentity(aclObjectIdentity);
        aclEntry.setAclSid(aclSid);

        // Integer ace =
        // aclEntryService.getMaxAceByAclObjectIdentity(aclObjectIdentity.getId());
        // int aceOrder = ace == null ? 0 : ace + 1;
        // aclEntry.setAceOrder(aceOrder);
        // aclEntry.setAuditFailure(Boolean.FALSE);
        // aclEntry.setAuditSuccess(Boolean.FALSE);
        aclEntry.setObjectIdIdentity(objectIdIdentity);
        aclEntry.setGranting(Boolean.TRUE);
        aclEntry.setMask(permission.getMask());
        aclEntryService.save(aclEntry);
        // aclEntryService.flush();
        // aclObjectIdentity.getAclEntries().add(aclEntry);
        // aclObjectIdentityService.save(aclObjectIdentity);
        // }
    }

    @Override
    @Transactional
    public <ENTITY extends IdEntity> void addPermission(Class<ENTITY> entityClass, String entityUuid, Permission permission, Set<String> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            return;
        }

        // 增加权限
        String entityClassName = entityClass.getCanonicalName();
        List<String> sidList = Lists.newArrayList();
        sidList.addAll(sids);
        AclClass aclClass = aclClassService.getByClasssName(entityClassName);
        if (aclClass == null) {
            aclClass = new AclClass();
            aclClass.setCls(entityClassName);
            aclClassService.save(aclClass);
        }

        List<AclSid> aclSids = aclSidService.listBySids(sidList);
        List<String> existsSids = aclSids.stream().map(aclSid -> aclSid.getSid()).collect(Collectors.toList());
        sidList.removeAll(existsSids);
        if (CollectionUtils.isNotEmpty(sidList)) {
            List<AclSid> toAddSids = Lists.newArrayList();
            for (String asid : sidList) {
                toAddSids.add(new AclSid(asid.startsWith(PREFIX_USERNAME), asid));
            }
            aclSidService.saveAll(toAddSids);
            aclSids.addAll(toAddSids);
        }

        // 当前ACL
        String objectIdIdentity = entityUuid;
        AclObjectIdentity aclObjectIdentity = aclObjectIdentityService.findByObjectId(entityClassName, entityUuid);
        if (aclObjectIdentity == null) {
            aclObjectIdentity = new AclObjectIdentity(aclClass, objectIdIdentity, aclSids.get(0), Boolean.TRUE);
            aclObjectIdentityService.save(aclObjectIdentity);
            // aclObjectIdentityService.flush();
        }

        List<AclEntry> aclEntries = Lists.newArrayList();
        for (AclSid aclSid : aclSids) {
            AclEntry aclEntry = new AclEntry();
            aclEntry.setAclObjectIdentity(aclObjectIdentity);
            aclEntry.setAclSid(aclSid);

            aclEntry.setObjectIdIdentity(objectIdIdentity);
            aclEntry.setGranting(Boolean.TRUE);
            aclEntry.setMask(permission.getMask());
            aclEntries.add(aclEntry);
        }
        aclEntryService.saveAll(aclEntries);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#removePermission(java.lang.Class, java.lang.String, org.springframework.security.acls.model.Permission)
     */
    @Override
    @Transactional
    public <ENTITY extends IdEntity> void removePermission(Class<ENTITY> entityClass, String entityUuid,
                                                           Permission permission) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("mask", permission.getMask());
        params.put("objectIdIdentity", entityUuid);
        this.nativeDao.namedExecute("deleteGrantAclByAclObjIdentityAndMask", params);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#removePermission(com.wellsoft.pt.core.entity.IdEntity, org.springframework.security.acls.model.Permission, java.lang.String)
     */
    @Override
    @Transactional
    public <ENTITY extends IdEntity> void removePermission(ENTITY entity, Permission permission, String sid) {
        removePermission(entity, permission, sid, sid.startsWith(PREFIX_USERNAME));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#removePermission(java.lang.Class, java.lang.String)
     */
    @Override
    @Transactional
    public <ENTITY extends IdEntity> void removePermission(Class<ENTITY> entityClass, String entityUuid) {

        Map<String, Object> params = Maps.newHashMap();
        params.put("objectIdIdentity", entityUuid);
        this.dao.namedExecute("deleteAclEntryByObjectIdIdentity", params);

    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#removePermission(com.wellsoft.pt.core.entity.IdEntity, org.springframework.security.acls.model.Permission, java.lang.String, java.lang.Boolean)
     */
    private <ENTITY extends IdEntity> void removePermission(ENTITY entity, Permission permission, String sid,
                                                            Boolean principal) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("sid", sid);
        params.put("principal", principal);
        params.put("objectIdIdentity", entity.getUuid());
        params.put("mask", permission.getMask());
        params.put("granting", 1);
        this.nativeDao.namedExecute("deleteAclEntryBySidAndObjIdIdentity", params);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#removePermission(java.lang.Class, java.lang.String, org.springframework.security.acls.model.Permission, java.lang.String)
     */
    @Override
    @Transactional
    public <ENTITY extends IdEntity> void removePermission(Class<ENTITY> entityClass, String entityUuid,
                                                           Permission permission, String sid) {

        Map<String, Object> params = Maps.newHashMap();
        params.put("sid", sid);
        params.put("principal", sid.startsWith(PREFIX_USERNAME));
        params.put("objectIdIdentity", entityUuid);
        params.put("mask", permission.getMask());
        params.put("granting", 1);
        this.nativeDao.namedExecute("deleteAclEntryBySidAndObjIdIdentity", params);
    }

    /**
     * @param entityClass
     * @param entityUuid
     * @param permissions
     * @param sids
     * @param <ENTITY>
     */
    @Override
    @Transactional
    public <ENTITY extends IdEntity> void removePermissions(Class<ENTITY> entityClass, String entityUuid, List<Permission> permissions, Collection<String> sids) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("sids", sids);
        params.put("objectIdIdentity", entityUuid);
        params.put("masks", permissions.stream().map(permission -> permission.getMask()).collect(Collectors.toList()));
        params.put("granting", 1);
        this.nativeDao.namedExecute("deleteAclEntryBySidsAndObjIdIdentity", params);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#removePermission(org.springframework.security.acls.model.Permission, java.lang.String)
     */
    @Override
    @Transactional
    public <ENTITY extends IdEntity> void removePermission(Permission permission, String sid) {
        Boolean principal = sid.startsWith(PREFIX_USERNAME);
        Map<String, Object> params = Maps.newHashMap();
        params.put("sid", sid);
        params.put("principal", principal);
        params.put("mask", permission.getMask());
        this.nativeDao.namedExecute("deleteAclEntryBySid", params);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#removePermission(com.wellsoft.pt.core.entity.IdEntity, java.lang.String)
     */
    @Override
    @Transactional
    public <ENTITY extends IdEntity> void removePermission(ENTITY entity, String sid) {
        Boolean principal = sid.startsWith(PREFIX_USERNAME);
        Map<String, Object> params = Maps.newHashMap();
        params.put("sid", sid);
        params.put("principal", principal);
        params.put("objectIdIdentity", entity.getUuid());
        this.nativeDao.namedExecute("deleteAclEntryBySidAndObjIdIdentity", params);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#hasPermission(com.wellsoft.pt.core.entity.IdEntity, org.springframework.security.acls.model.Permission)
     */
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> boolean hasPermission(ENTITY entity, Permission permission) {
        return hasPermission(entity.getClass(), entity.getUuid(), permission, getUsername());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#hasPermission(com.wellsoft.pt.core.entity.IdEntity, org.springframework.security.acls.model.Permission, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> boolean hasPermission(ENTITY entity, Permission permission, String sid) {
        return hasPermission(entity.getClass(), entity.getUuid(), permission, sid, sid.startsWith(PREFIX_USERNAME));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#hasPermission(com.wellsoft.pt.core.entity.IdEntity, org.springframework.security.acls.model.Permission, java.lang.String, java.lang.Boolean)
     */
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> Boolean hasPermission(Class<ENTITY> entityClass, String entityUuid,
                                                           Permission permission, String sid, Boolean principal) {
        // 查询hql
        String hql = HAS_PERMISSION_BASIC_NEW;
        Query query = this.dao.getSession().createQuery(hql);
        query.setParameter("mask", permission.getMask());
        query.setParameter("sid", sid);
        query.setParameter("principal", principal);
        // query.setParameter("cls", entityClass.getCanonicalName());
        query.setParameter("objectIdIdentity", entityUuid);

        Long count = (Long) query.uniqueResult();
        return count > 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#hasPermission(java.lang.Class, java.lang.String, org.springframework.security.acls.model.Permission)
     */
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> boolean hasPermission(Class<ENTITY> entityClass, String entityUuid,
                                                           Permission permission) {
        return this.hasPermission(entityClass, entityUuid, permission, getUsername());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#hasPermission(java.lang.Class, java.lang.String, org.springframework.security.acls.model.Permission, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> boolean hasPermission(Class<ENTITY> entityClass, String entityUuid,
                                                           Permission permission, String sid) {
        return this.hasPermission(entityClass, entityUuid, permission, sid, sid.startsWith(PREFIX_USERNAME));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#getPermission(com.wellsoft.pt.core.entity.IdEntity)
     */
    @Override
    public <ENTITY extends IdEntity> List<Permission> getPermission(ENTITY entity) {
        return getPermission(entity, getUsername());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#getPermission(com.wellsoft.pt.core.entity.IdEntity, java.lang.String)
     */
    @Override
    public <ENTITY extends IdEntity> List<Permission> getPermission(ENTITY entity, String sid) {
        return getPermission(entity, sid, sid.startsWith(PREFIX_USERNAME));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#isGranted(java.lang.String, java.util.List, java.util.List)
     */
    @Override
    public boolean isGranted(String objectIdIdentity, List<Permission> permissions, List<String> sids) {
        List<Integer> masks = new ArrayList<Integer>();
        for (Permission permission : permissions) {
            masks.add(permission.getMask());
        }
        Map<String, Object> params = Maps.newHashMap();
        params.put("masks", masks);
        params.put("sids", sids);
        params.put("objectIdIdentity", objectIdIdentity);
        Number count = (Number) this.nativeDao.findUniqueByNamedQuery("checkAclGranted", params);
        return count.longValue() > 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#isGranted(java.lang.String, java.util.List)
     */
    @Override
    public boolean isGranted(String objectIdIdentity, List<String> sids) {

        Map<String, Object> params = Maps.newHashMap();
        params.put("sids", sids);
        params.put("objectIdIdentity", objectIdIdentity);
        Number count = (Number) this.nativeDao.findUniqueByNamedQuery("checkAclGranted", params);
        return count.longValue() > 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#getPermission(com.wellsoft.pt.core.entity.IdEntity, java.lang.String, java.lang.Boolean)
     */
    @SuppressWarnings("unchecked")
    private <ENTITY extends IdEntity> List<Permission> getPermission(ENTITY entity, String sid, Boolean principal) {
        // 查询hql
        String hql = GET_PERMISSION_BASIC;
        Query query = this.dao.getSession().createQuery(hql);
        query.setParameter("sid", sid);
        query.setParameter("principal", principal);
        query.setParameter("cls", getEntityClassName(entity));
        query.setParameter("objectIdIdentity", entity.getUuid());

        List<Integer> masks = query.list();

        List<Permission> permissions = new ArrayList<Permission>();
        for (Integer mask : masks) {
            permissions.add(new AclPermission(mask));
        }
        return permissions;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#removeSid(java.lang.String)
     */
    @Override
    public void removeSid(String sid) {
        removeSid(sid, sid.startsWith(PREFIX_USERNAME));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#removeSid(java.lang.String, java.lang.Boolean)
     */
    private void removeSid(String sid, Boolean principal) {
        AclSid aclSid = new AclSid(principal, sid);
        aclSid = aclSidService.get(aclSid);// aclSidService.getOrCreate(aclSid);
        if (aclSid != null) {
            aclSidService.delete(aclSid);
            // aclSidService.flush();
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#getSid(com.wellsoft.pt.core.entity.IdEntity)
     */
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> List<AclSid> getSid(ENTITY entity) {
        return getSid(entity, null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#getSid(com.wellsoft.pt.core.entity.IdEntity, org.springframework.security.acls.model.Permission)
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> List<AclSid> getSid(ENTITY entity, Permission permission) {
        return this.getSid((Class<ENTITY>) ClassUtils.getUserClass(entity), entity.getUuid(), permission);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#getSid(java.lang.Class, java.lang.String, org.springframework.security.acls.model.Permission)
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> List<AclSid> getSid(Class<ENTITY> entityClass, String entityUuid,
                                                         Permission permission) {
        // 查询hql
        String hql = permission == null ? GET_Sid_BASIC2 : GET_Sid_WITH_PERMISSION_BASIC2_NEW;
        Query query = this.dao.getSession().createQuery(hql);
        // query.setCacheable(true);
        if (permission == null) {
            query.setParameter("cls", entityClass.getCanonicalName());
        }
        query.setParameter("objectIdIdentity", entityUuid);
        if (permission != null) {
            query.setParameter("mask", permission.getMask());
        }

        return query.list();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#query(java.lang.Class,
     * com.wellsoft.pt.security.acl.support.QueryInfo, java.util.List,
     * java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> QueryInfo<ENTITY> query(Class<ENTITY> entityClass, QueryInfo<ENTITY> queryInfo,
                                                             List<Permission> permissions, String moduleId) {
        return query(entityClass, queryInfo, permissions, getUsername(), moduleId);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#query(java.lang.Class,
     * com.wellsoft.pt.security.acl.support.QueryInfo, java.util.List,
     * java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> QueryInfo<ENTITY> query(Class<ENTITY> entityClass, QueryInfo<ENTITY> queryInfo,
                                                             List<Permission> permissions, String member, String moduleId) {
        List<Integer> masks = new ArrayList<Integer>();
        for (Permission permission : permissions) {
            masks.add(permission.getMask());
        }
        // AclClass aclClass = aclClassService.getOrCreate(new
        // AclClass(entityClass.getCanonicalName()));
        // String clsId = aclClass.getId();
        // 查询实休可访问的id
        String hql = QUERY_BASE_MEMBER;

        String entityName = entityClass.getCanonicalName();
        String whereHql = "1 = 1" + AclUtil.buildWhereHql(queryInfo);
        String queryHql = "from " + entityName + " o where " + whereHql + " and exists (" + hql + ")"
                + AclUtil.buildOrderby(queryInfo);
        Query query = this.dao.getSession().createQuery(queryHql);
        // query.setCacheable(true);
        // queryInfo.addQueryParams("clsId", clsId);
        queryInfo.addQueryParams("masks", masks.toArray());
        queryInfo.addQueryParams("granting", Boolean.TRUE);
        // 成员查询不需要条件principal
        // queryInfo.addQueryParams("principal",
        // member.startsWith(PREFIX_USERNAME));
        queryInfo.addQueryParams("member", member);
        queryInfo.addQueryParams("moduleId", moduleId);

        // 设置查询参数
        AclUtil.setQueryParams(query, queryInfo.getQueryParams());
        // 分页信息
        Page<ENTITY> page = queryInfo.getPage();
        if (page.getPageSize() != -1) {
            query.setFirstResult(page.getFirst() - 1);
            query.setMaxResults(page.getPageSize());
        }
        page.setResult(query.list());

        // 计算总数
        if (page.isAutoCount()) {
            query = this.dao.getSession().createQuery(
                    "select count(*) from " + entityName + " o where " + whereHql + " and exists (" + hql + ")");
            // query.setCacheable(true);
            AclUtil.setQueryParams(query, queryInfo.getQueryParams());
            page.setTotalCount((Long) query.uniqueResult());
        }
        return queryInfo;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#queryForItem(java.lang.Class, com.wellsoft.pt.security.acl.support.QueryInfo, java.util.List, java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> List<QueryItem> queryForItem(Class<ENTITY> entityClass,
                                                                  QueryInfo<ENTITY> queryInfo, List<Permission> permissions, String member, String moduleId) {
        List<Integer> masks = new ArrayList<Integer>();
        for (Permission permission : permissions) {
            masks.add(permission.getMask());
        }
        // AclClass aclClass = aclClassService.getOrCreate(new
        // AclClass(entityClass.getCanonicalName()));
        // String clsId = aclClass.getId();
        // 查询实休可访问的id
        String hql = QUERY_BASE_MEMBER;

        String entityName = entityClass.getCanonicalName();
        String whereHql = "1 = 1" + AclUtil.buildWhereHql(queryInfo);
        String queryHql = "select " + queryInfo.getSelectionHql() + " from " + entityName + " o where " + whereHql
                + " and exists (" + hql + ")" + AclUtil.buildOrderby(queryInfo);
        Query query = this.dao.getSession().createQuery(queryHql);
        // query.setCacheable(true);
        // queryInfo.addQueryParams("clsId", clsId);
        queryInfo.addQueryParams("masks", masks.toArray());
        queryInfo.addQueryParams("granting", Boolean.TRUE);
        // 成员查询不需要条件principal
        // queryInfo.addQueryParams("principal",
        // member.startsWith(PREFIX_USERNAME));
        queryInfo.addQueryParams("member", member);
        queryInfo.addQueryParams("moduleId", moduleId);

        // 设置查询参数
        AclUtil.setQueryParams(query, queryInfo.getQueryParams());
        // 分页信息
        Page<ENTITY> page = queryInfo.getPage();
        if (page.getPageSize() != -1) {
            query.setFirstResult(page.getFirst() - 1);
            query.setMaxResults(page.getPageSize());
        }
        query.setResultTransformer(QueryItemResultTransformer.INSTANCE);
        List<QueryItem> queryItems = query.list();

        // 计算总数
        if (page.isAutoCount()) {
            query = this.dao.getSession().createQuery(
                    "select count(*) from " + entityName + " o where " + whereHql + " and exists (" + hql + ")");
            // query.setCacheable(true);
            AclUtil.setQueryParams(query, queryInfo.getQueryParams());
            page.setTotalCount((Long) query.uniqueResult());
        }
        return queryItems;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#queryAll(java.lang.Class, com.wellsoft.pt.security.acl.support.QueryInfo, java.util.List, java.util.List, java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> QueryInfo<ENTITY> queryAll(Class<ENTITY> entityClass, QueryInfo<ENTITY> queryInfo,
                                                                List<Permission> permissions, String member, String moduleId) {
        List<Integer> masks = new ArrayList<Integer>();
        for (Permission permission : permissions) {
            masks.add(permission.getMask());
        }
        // 查询实休可访问的id
        String hql = QUERY_ALL;

        String entityName = entityClass.getCanonicalName();
        String whereHql = "1 = 1" + AclUtil.buildWhereHql(queryInfo);
        String queryHql = "from " + entityName + " o where " + whereHql + " and exists (" + hql + ")"
                + AclUtil.buildOrderby(queryInfo);
        Query query = this.dao.getSession().createQuery(queryHql);
        // query.setCacheable(true);
        // queryInfo.addQueryParams("cls", entityName);
        queryInfo.addQueryParams("masks", masks.toArray());
        queryInfo.addQueryParams("granting", Boolean.TRUE);
        // 添加当前用户
        queryInfo.addQueryParams("sid", getUsername());
        // queryInfo.addQueryParams("sids", sids.toArray());
        // 成员查询不需要条件principal
        queryInfo.addQueryParams("principal", member.startsWith(PREFIX_USERNAME));
        queryInfo.addQueryParams("member", member);
        queryInfo.addQueryParams("moduleId", moduleId);

        // 设置查询参数
        AclUtil.setQueryParams(query, queryInfo.getQueryParams());
        // 分页信息
        Page<ENTITY> page = queryInfo.getPage();
        if (page.getPageSize() != -1) {
            query.setFirstResult(page.getFirst() - 1);
            query.setMaxResults(page.getPageSize());
        }
        page.setResult(query.list());

        // 计算总数
        if (page.isAutoCount()) {
            query = this.dao.getSession().createQuery(
                    "select count(*) from " + entityName + " o where " + whereHql + " and exists (" + hql + ")");
            // query.setCacheable(true);
            AclUtil.setQueryParams(query, queryInfo.getQueryParams());
            page.setTotalCount((Long) query.uniqueResult());
        }
        return queryInfo;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#queryAllForItem(java.lang.Class, com.wellsoft.pt.security.acl.support.QueryInfo, java.util.List, java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> List<QueryItem> queryAllForItem(Class<ENTITY> entityClass,
                                                                     QueryInfo<ENTITY> queryInfo, List<Permission> permissions, String member, String moduleId) {
        List<Integer> masks = new ArrayList<Integer>();
        for (Permission permission : permissions) {
            masks.add(permission.getMask());
        }
        // 查询实休可访问的id
        String hql = QUERY_ALL_ITEM;

        String entityName = entityClass.getCanonicalName();
        String whereHql = "1 = 1" + AclUtil.buildWhereHql(queryInfo);
        String queryHql = "select " + queryInfo.getSelectionHql() + " from " + entityName + " o where " + whereHql
                + " and exists (" + hql + ")" + AclUtil.buildOrderby(queryInfo);
        Query query = this.dao.getSession().createQuery(queryHql);
        // query.setCacheable(true);
        // queryInfo.addQueryParams("cls", entityName);
        queryInfo.addQueryParams("masks", masks.toArray());
        queryInfo.addQueryParams("granting", Boolean.TRUE);
        // 添加当前用户
        queryInfo.addQueryParams("sid", getUsername());
        // 成员查询不需要条件principal
        queryInfo.addQueryParams("principal", member.startsWith(PREFIX_USERNAME));
        queryInfo.addQueryParams("member", member);
        queryInfo.addQueryParams("moduleId", moduleId);

        // 设置查询参数
        AclUtil.setQueryParams(query, queryInfo.getQueryParams());
        // 分页信息
        Page<ENTITY> page = queryInfo.getPage();
        if (page.getPageSize() != -1) {
            query.setFirstResult(page.getFirst() - 1);
            query.setMaxResults(page.getPageSize());
        }
        query.setResultTransformer(QueryItemResultTransformer.INSTANCE);
        List<QueryItem> queryItems = query.list();

        // 计算总数
        if (page.isAutoCount()) {
            query = this.dao.getSession().createQuery(
                    "select count(*) from " + entityName + " o where " + whereHql + " and exists (" + hql + ")");
            // query.setCacheable(true);
            AclUtil.setQueryParams(query, queryInfo.getQueryParams());
            page.setTotalCount((Long) query.uniqueResult());
        }
        return queryItems;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#getMember(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<AclSidMember> getMember(String sid, String moduleId) {
        return this.aclSidMemberService.getMember(sid, moduleId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#addMember(java.lang.String, java.lang.String)
     */
    @Override
    public void addMember(String sid, String member, String moduleId) {
        if (!sid.startsWith(PREFIX_GROUP)) {
            throw new RuntimeException("只有以GROUP_开头的群组(sid)才能添加成员");
        }
        // if (!hasMember(sid, member, moduleId)) {
        AclSid aclSid = new AclSid(sid.startsWith(PREFIX_USERNAME), sid);
        aclSid = aclSidService.get(aclSid);
        if (aclSid == null) {
            aclSid = new AclSid(sid.startsWith(PREFIX_USERNAME), sid);
            aclSidService.save(aclSid);
        }

        AclSidMember sidMember = new AclSidMember();
        sidMember.setAclSid(aclSid);
        sidMember.setMember(member);
        sidMember.setModuleId(moduleId);

        this.aclSidMemberService.save(sidMember);
        // this.aclSidMemberService.flush();
        // }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#removeMember(java.lang.String, java.lang.String)
     */
    @Override
    public void removeMember(String sid, String member, String moduleId) {
        this.aclSidMemberService.removeMember(sid, member, moduleId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#removeAllMember(java.lang.String)
     */
    @Override
    public void removeAllMember(String sid, String moduleId) {
        this.aclSidMemberService.removeAllMember(sid, moduleId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#hasMember(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean hasMember(String sid, String member, String moduleId) {
        return aclSidMemberService.isExists(sid, member, moduleId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#hasPermission(com.wellsoft.pt.core.entity.IdEntity, org.springframework.security.acls.model.Permission, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> boolean hasPermission(ENTITY entity, Permission permission, String sid,
                                                           String member, String moduleId) {
        if (this.hasMember(sid, member, moduleId)) {
            return this.hasPermission(entity, permission, sid);
        }
        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#hasPermission(java.lang.Class, java.lang.String, org.springframework.security.acls.model.Permission, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> boolean hasPermission(Class<ENTITY> entityClass, String entityUuid,
                                                           Permission permission, String sid, String member, String moduleId) {
        if (this.hasMember(sid, member, moduleId)) {
            return this.hasPermission(entityClass, entityUuid, permission, sid);
        }
        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#createAcl(com.wellsoft.pt.core.entity.IdEntity, java.lang.String)
     */
    @Override
    public <ENTITY extends IdEntity> void createAcl(ENTITY entity, String sid) {
        Boolean principal = sid.startsWith(PREFIX_USERNAME);
        String entityClassName = getEntityClassName(entity);
        AclClass aclClass = aclClassService.getByClasssName(entityClassName);
        if (aclClass == null) {
            aclClass = new AclClass();
            aclClass.setCls(entityClassName);
            aclClassService.save(aclClass);
        }
        String objectIdIdentity = entity.getUuid();
        AclSid aclSid = new AclSid(principal, sid);
        aclSid = aclSidService.get(aclSid);
        if (aclSid == null) {
            aclSid = new AclSid(principal, sid);
            aclSidService.save(aclSid);
        }
        AclObjectIdentity aclObjectIdentity = new AclObjectIdentity(aclClass, objectIdIdentity, aclSid, Boolean.TRUE);
        aclObjectIdentityService.save(aclObjectIdentity);
        // aclObjectIdentityService.flush();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#createAcl(com.wellsoft.pt.core.entity.IdEntity, java.lang.String, org.springframework.security.acls.model.Permission)
     */
    @Override
    public <ENTITY extends IdEntity> void createAcl(ENTITY entity, String sid, Permission permission) {

        Boolean principal = sid.startsWith(PREFIX_USERNAME);
        String entityClassName = getEntityClassName(entity);
        AclClass aclClass = aclClassService.getByClasssName(entityClassName);
        if (aclClass == null) {
            aclClass = new AclClass();
            aclClass.setCls(entityClassName);
            aclClassService.save(aclClass);
        }
        String objectIdIdentity = entity.getUuid();
        AclSid aclSid = new AclSid(principal, sid);
        aclSid = aclSidService.get(aclSid);
        if (aclSid == null) {
            aclSid = new AclSid(principal, sid);
            aclSidService.save(aclSid);
        }
        // 当前acl
        AclObjectIdentity aclObjectIdentity = new AclObjectIdentity(aclClass, objectIdIdentity, aclSid, Boolean.TRUE);
        aclObjectIdentityService.save(aclObjectIdentity);
        // aclObjectIdentityService.flush();

        // 增加权限
        AclEntry aclEntry = new AclEntry();
        aclEntry.setAclObjectIdentity(aclObjectIdentity);
        aclEntry.setAclSid(aclSid);

        // Integer ace =
        // aclEntryService.getMaxAceByAclObjectIdentity(aclObjectIdentity.getId());
        // int aceOrder = ace == null ? 0 : ace + 1;
        // aclEntry.setAceOrder(aceOrder);
        // aclEntry.setAuditFailure(Boolean.FALSE);
        // aclEntry.setAuditSuccess(Boolean.FALSE);
        aclEntry.setObjectIdIdentity(objectIdIdentity);
        aclEntry.setGranting(Boolean.TRUE);
        aclEntry.setMask(permission.getMask());
        aclEntryService.save(aclEntry);
        // aclObjectIdentityService.flush();
        // aclObjectIdentity.getAclEntries().add(aclEntry);
        // aclObjectIdentityService.save(aclObjectIdentity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#createAcl(com.wellsoft.pt.core.entity.IdEntity, com.wellsoft.pt.core.entity.IdEntity, java.lang.String)
     */
    @Override
    public <ENTITY extends IdEntity> void createAcl(ENTITY entity, ENTITY parentEntity, String sid) {
        this.createAcl(parentEntity, sid);

        // 当前父acl
        AclObjectIdentity parentAclObjectIdentity = aclObjectIdentityService
                .findByObjectId(entity.getClass().getCanonicalName(), parentEntity.getUuid());
        // 当前父acl
        AclObjectIdentity aclObjectIdentity = aclObjectIdentityService.findByObjectId(getEntityClassName(entity),
                entity.getUuid());
        aclObjectIdentity.setAclObjectIdentity(parentAclObjectIdentity);
        aclObjectIdentityService.save(aclObjectIdentity);
        // aclObjectIdentityService.flush();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#createAcl(com.wellsoft.pt.core.entity.IdEntity, com.wellsoft.pt.core.entity.IdEntity, java.lang.String, org.springframework.security.acls.model.Permission)
     */
    @Override
    public <ENTITY extends IdEntity> void createAcl(ENTITY entity, ENTITY parentEntity, String sid,
                                                    Permission permission) {
        this.createAcl(parentEntity, sid, permission);

        // 当前父acl
        AclObjectIdentity parentAclObjectIdentity = aclObjectIdentityService
                .findByObjectId(entity.getClass().getCanonicalName(), parentEntity.getUuid());
        // 当前父acl
        AclObjectIdentity aclObjectIdentity = aclObjectIdentityService.findByObjectId(getEntityClassName(entity),
                entity.getUuid());
        aclObjectIdentity.setAclObjectIdentity(parentAclObjectIdentity);
        aclObjectIdentityService.save(aclObjectIdentity);
        // aclObjectIdentityService.flush();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#hasAcl(com.wellsoft.pt.core.entity.IdEntity, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> boolean hasAcl(ENTITY entity) {
        return aclObjectIdentityService.isExists(getEntityClassName(entity), entity.getUuid());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#hasAcl(java.lang.Class, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> boolean hasAcl(Class<ENTITY> entityClass, String entityUuid) {
        return aclObjectIdentityService.isExists(entityClass.getCanonicalName(), entityUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#hasPermission(com.wellsoft.pt.core.entity.IdEntity, org.springframework.security.acls.model.Permission, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> boolean hasPermission(ENTITY entity, Permission permission, String member,
                                                           String moduleId) {
        return hasPermission(entity.getClass(), entity.getUuid(), permission, member, moduleId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#hasPermission(java.lang.Class, java.lang.String, org.springframework.security.acls.model.Permission, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> boolean hasPermission(Class<ENTITY> entityClass, String entityUuid,
                                                           Permission permission, String member, String moduleId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("objectIdIdentity", entityUuid);
        params.put("mask", permission.getMask());
        params.put("member", member);
        params.put("moduleId", moduleId);
        Long count = this.nativeDao.findUniqueByNamedQuery("checkAclPermissionByModuleId", null, Long.class);
        return count > 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#getPermission(com.wellsoft.pt.core.entity.IdEntity, java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <ENTITY extends IdEntity> List<Permission> getPermission(ENTITY entity, String member, String moduleId) {
        String queryString = GET_PERMISSION_BY_MODULE_ID;

        Query query = this.dao.getSession().createQuery(queryString);
        query.setParameter("objectIdIdentity", entity.getUuid());
        query.setParameter("member", member);
        query.setParameter("moduleId", moduleId);

        List<Integer> masks = query.list();

        List<Permission> permissions = new ArrayList<Permission>();
        for (Integer mask : masks) {
            permissions.add(new AclPermission(mask));
        }
        return permissions;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#getAllPermission(com.wellsoft.pt.core.entity.IdEntity, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> List<Permission> getAllPermission(ENTITY entity, String member, String moduleId) {
        List<Permission> permissions = new ArrayList<Permission>();
        permissions.addAll(this.getPermission(entity, member, true));
        permissions.addAll(this.getPermission(entity, member, moduleId));
        return permissions;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#count(java.lang.Class, com.wellsoft.pt.security.acl.support.QueryInfo, java.util.List, java.util.List)
     */
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> Long count(Class<ENTITY> entityClass, QueryInfo<ENTITY> queryInfo,
                                                List<Permission> permissions, List<String> sids) {
        List<Integer> masks = new ArrayList<Integer>();
        for (Permission permission : permissions) {
            masks.add(permission.getMask());
        }
        // 查询实休可访问的id
        String hql = QUERY_ITEM_BASIC_NO_PRINCIPAL;

        String entityName = entityClass.getCanonicalName();
        String whereHql = "1 = 1" + AclUtil.buildWhereHql(queryInfo);
        // query.setCacheable(true);
        queryInfo.addQueryParams("cls", entityName);
        queryInfo.addQueryParams("masks", masks.toArray());
        queryInfo.addQueryParams("granting", true);
        queryInfo.addQueryParams("sids", sids.toArray());

        // 计算总数
        Query query = this.dao.getSession().createQuery(
                "select count(*) from " + entityName + " o where " + whereHql + " and exists (" + hql + ")");
        // query.setCacheable(true);
        // 设置查询参数
        AclUtil.setQueryParams(query, queryInfo.getQueryParams());
        return (Long) query.uniqueResult();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#count(java.lang.Class, com.wellsoft.pt.security.acl.support.QueryInfo, java.util.List, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public <ENTITY extends IdEntity> Long count(Class<ENTITY> entityClass, QueryInfo<ENTITY> queryInfo,
                                                List<Permission> permissions, String member, String moduleId) {
        List<Integer> masks = new ArrayList<Integer>();
        for (Permission permission : permissions) {
            masks.add(permission.getMask());
        }
        // AclClass aclClass = aclClassService.getOrCreate(new
        // AclClass(entityClass.getCanonicalName()));
        // String clsId = aclClass.getId();
        // 查询实休可访问的id
        String hql = QUERY_BASE_MEMBER;

        String entityName = entityClass.getCanonicalName();
        String whereHql = "1 = 1" + AclUtil.buildWhereHql(queryInfo);
        // query.setCacheable(true);
        // queryInfo.addQueryParams("clsId", clsId);
        queryInfo.addQueryParams("masks", masks.toArray());
        queryInfo.addQueryParams("granting", Boolean.TRUE);
        // 成员查询不需要条件principal
        // queryInfo.addQueryParams("principal",
        // member.startsWith(PREFIX_USERNAME));
        queryInfo.addQueryParams("member", member);
        queryInfo.addQueryParams("moduleId", moduleId);

        // 计算总数
        Query query = this.dao.getSession().createQuery(
                "select count(*) from " + entityName + " o where " + whereHql + " and exists (" + hql + ")");
        // query.setCacheable(true);
        // 设置查询参数
        AclUtil.setQueryParams(query, queryInfo.getQueryParams());
        return (Long) query.uniqueResult();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclService#countAll(java.lang.Class, com.wellsoft.pt.security.acl.support.QueryInfo, java.util.List, java.lang.String, java.lang.String)
     */
    @Override
    public <ENTITY extends IdEntity> Long countAll(Class<ENTITY> entityClass, QueryInfo<ENTITY> queryInfo,
                                                   List<Permission> permissions, String member, String moduleId) {
        List<Integer> masks = new ArrayList<Integer>();
        for (Permission permission : permissions) {
            masks.add(permission.getMask());
        }
        // 查询实休可访问的id
        String hql = QUERY_ALL_ITEM;

        String entityName = entityClass.getCanonicalName();
        String whereHql = "1 = 1" + AclUtil.buildWhereHql(queryInfo);
        // query.setCacheable(true);
        // queryInfo.addQueryParams("cls", entityName);
        queryInfo.addQueryParams("masks", masks.toArray());
        queryInfo.addQueryParams("granting", Boolean.TRUE);
        // 添加当前用户
        queryInfo.addQueryParams("sid", getUsername());
        // 成员查询不需要条件principal
        queryInfo.addQueryParams("principal", member.startsWith(PREFIX_USERNAME));
        queryInfo.addQueryParams("member", member);
        queryInfo.addQueryParams("moduleId", moduleId);

        // 计算总数
        Query query = this.dao.getSession().createQuery(
                "select count(*) from " + entityName + " o where " + whereHql + " and exists (" + hql + ")");
        // query.setCacheable(true);
        // 设置查询参数
        AclUtil.setQueryParams(query, queryInfo.getQueryParams());
        return (Long) query.uniqueResult();
    }

    /* add by huanglinchuan 2014.10.29 begin */
    @SuppressWarnings("unchecked")
    public <ENTITY extends IdEntity> List<QueryItem> queryForItemByCustomHql(Class<ENTITY> entityClass,
                                                                             QueryInfo<ENTITY> queryInfo, String hqlForAcl, Map<String, Object> hqlParasForAcl) {
        String entityName = entityClass.getCanonicalName();
        String whereHql = "1 = 1" + AclUtil.buildWhereHql(queryInfo);
        String queryHql = "select " + queryInfo.getSelectionHql() + " from " + entityName + " o where " + whereHql
                + " and exists (" + hqlForAcl + ")" + AclUtil.buildOrderby(queryInfo);
        Query query = this.dao.getSession().createQuery(queryHql);

        if (hqlParasForAcl != null) {
            Set<Map.Entry<String, Object>> setOfHqlParasForAcl = hqlParasForAcl.entrySet();
            Iterator<Map.Entry<String, Object>> it = setOfHqlParasForAcl.iterator();
            while (it.hasNext()) {
                Map.Entry<String, Object> entry = it.next();
                queryInfo.addQueryParams(entry.getKey(), entry.getValue());
            }
        }

        // 设置查询参数
        AclUtil.setQueryParams(query, queryInfo.getQueryParams());
        // 分页信息
        Page<ENTITY> page = queryInfo.getPage();
        if (page.getPageSize() != -1) {
            query.setFirstResult(page.getFirst() - 1);
            query.setMaxResults(page.getPageSize());
        }
        query.setResultTransformer(QueryItemResultTransformer.INSTANCE);
        List<QueryItem> queryItems = query.list();

        // 计算总数
        if (page.isAutoCount()) {
            query = this.dao.getSession().createQuery(
                    "select count(*) from " + entityName + " o where " + whereHql + " and exists (" + hqlForAcl + ")");
            AclUtil.setQueryParams(query, queryInfo.getQueryParams());
            page.setTotalCount((Long) query.uniqueResult());
        }
        return queryItems;
    }

    /* add by huanglinchuan 2014.10.29 end */

    /* add by huanglinchuan 2014.11.11 begin */
    public <ENTITY extends IdEntity> boolean hasPermissionByUserId(Class<ENTITY> entityClass, String entityUuid,
                                                                   Permission permission, String userId) {
        boolean flag = false;
        Set<String> userOrgIds = orgApiFacade.getUserOrgIds(userId);
        for (String userOrgId : userOrgIds) {
            String sid = userOrgId;
            if (sid.startsWith(IdPrefix.USER.getValue()) && !sid.equals(userId)) {
                continue;
            } else if (sid.startsWith(IdPrefix.DEPARTMENT.getValue())) {
                sid = "GROUP_" + sid;
            }

            if (!flag) {
                flag = hasPermission(entityClass, entityUuid, permission, sid);
            }
        }
        return flag;
    }

    public <ENTITY extends IdEntity> List<Permission> getAllPermissionByUserId(ENTITY entity, String userId) {
        Set<String> userOrgIds = orgApiFacade.getUserOrgIds(userId);
        List<String> sids = new ArrayList<String>();
        for (String userOrgId : userOrgIds) {
            String sid = userOrgId;
            /* modified by huanglinchuan 2014.12.13 begin */
            if (!sid.startsWith(IdPrefix.USER.getValue())) {
                sid = "GROUP_" + sid;
            } else if (!sid.equals(userId)) {
                continue;
            }
            /* modified by huanglinchuan 2014.12.13 end */

            if (!sids.contains(sid)) {
                sids.add(sid);
            }
        }
        return getAllPermissionBySids(entity, sids);
    }

    public <ENTITY extends IdEntity> List<Permission> getAllPermissionBySids(ENTITY entity, List<String> sids) {
        // 查询hql
        String hql = "select acl_entry.mask from AclEntry acl_entry where 	" + "acl_entry.aclSid.sid in (:sids) "
                + "and acl_entry.aclObjectIdentity.uuid = (select acl_object_identity.uuid	"
                + "from AclObjectIdentity acl_object_identity where acl_object_identity.aclClass.cls = :cls	"
                + "and acl_object_identity.objectIdIdentity = :objectIdIdentity)	";
        Query query = this.dao.getSession().createQuery(hql);
        query.setParameterList("sids", sids.toArray());
        query.setParameter("cls", getEntityClassName(entity));
        query.setParameter("objectIdIdentity", entity.getUuid());

        List<Integer> masks = query.list();

        Set<Permission> permissions = new HashSet<Permission>();
        for (Integer mask : masks) {
            permissions.add(new AclPermission(mask));
        }
        return Arrays.asList(permissions.toArray(new Permission[0]));
    }

    @SuppressWarnings("unchecked")
    public <ENTITY extends IdEntity> QueryInfo<ENTITY> queryAllByCustomHql(Class<ENTITY> entityClass,
                                                                           QueryInfo<ENTITY> queryInfo, String hqlForAcl, Map<String, Object> hqlParasForAcl) {
        String entityName = entityClass.getCanonicalName();
        String whereHql = "1 = 1" + AclUtil.buildWhereHql(queryInfo);
        String queryHql = "select " + queryInfo.getSelectionHql() + " from " + entityName + " o where " + whereHql
                + " and exists (" + hqlForAcl + ")" + AclUtil.buildOrderby(queryInfo);
        Query query = this.dao.getSession().createQuery(queryHql);

        if (hqlParasForAcl != null) {
            Set<Map.Entry<String, Object>> setOfHqlParasForAcl = hqlParasForAcl.entrySet();
            Iterator<Map.Entry<String, Object>> it = setOfHqlParasForAcl.iterator();
            while (it.hasNext()) {
                Map.Entry<String, Object> entry = it.next();
                queryInfo.addQueryParams(entry.getKey(), entry.getValue());
            }
        }

        // 设置查询参数
        AclUtil.setQueryParams(query, queryInfo.getQueryParams());
        // 分页信息
        Page<ENTITY> page = queryInfo.getPage();
        if (page.getPageSize() != -1) {
            query.setFirstResult(page.getFirst() - 1);
            query.setMaxResults(page.getPageSize());
        }

        // 计算总数
        if (page.isAutoCount()) {
            query = this.dao.getSession().createQuery(
                    "select count(*) from " + entityName + " o where " + whereHql + " and exists (" + hqlForAcl + ")");
            AclUtil.setQueryParams(query, queryInfo.getQueryParams());
            page.setTotalCount((Long) query.uniqueResult());
        }
        return queryInfo;
    }

    /* add by huanglinchuan 2014.11.11 end */

    @Override
    public boolean isGranted(String objectIdIdentity, Integer[] masks, List<String> sids) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("masks", masks);
        params.put("sids", sids);
        params.put("objectIdIdentity", objectIdIdentity);
        Number count = (Number) this.nativeDao.findUniqueByNamedQuery("checkAclGranted", params);
        return count.longValue() > 0;
    }

    @Override
    public List<AclEntry> getConsultListByUserId(String userId) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("userId", userId);
        return this.nativeDao.findUniqueByNamedQuery("getConsultListByUserId", values);
    }
}
