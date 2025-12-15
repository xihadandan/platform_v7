/*
 * @(#)2012-10-30 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.acl.service;

import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.security.acl.entity.AclEntry;
import com.wellsoft.pt.security.acl.entity.AclSid;
import com.wellsoft.pt.security.acl.entity.AclSidMember;
import com.wellsoft.pt.security.acl.support.QueryInfo;
import org.springframework.security.acls.model.Permission;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
 * 2013-01-27.1	zhulh		2013-01-27		不对实体的状态进行增册改查等操作
 * 2013-02-21.1	zhulh		2013-02-21		增加SID成员控制
 * </pre>
 * @date 2012-10-30
 */
public interface AclService {
    public static final String PREFIX_USERNAME = IdPrefix.USER.getValue();

    public static final String PREFIX_JOB = IdPrefix.JOB.getValue();

    public static final String PREFIX_ROLE = "ROLE_";

    public static final String PREFIX_GROUP = "GROUP_";

    // 单个获取，当前用户ID默认权限为读
    @Deprecated
    <ENTITY extends IdEntity> ENTITY get(Class<ENTITY> entityClass, Serializable entityUuid);

    // 单个获取，指定操作权限
    @Deprecated
    <ENTITY extends IdEntity> ENTITY get(Class<ENTITY> entityClass, Serializable entityUuid, Permission permission);

    // 单个获取，指定操作权限、用户ID/角色
    <ENTITY extends IdEntity> ENTITY get(Class<ENTITY> entityClass, Serializable entityUuid, Permission permission,
                                         String sid);

    // 单个获取，指定操作多个权限、用户ID/角色
    <ENTITY extends IdEntity> ENTITY get(Class<ENTITY> entityClass, Serializable entityUuid,
                                         List<Permission> permissions, List<String> sids);

    // 查询全部，当前用户ID默认权限为读
    @Deprecated
    <ENTITY extends IdEntity> List<ENTITY> getAll(Class<ENTITY> entityClass);

    @Deprecated
    <ENTITY extends IdEntity> List<ENTITY> getAll(Class<ENTITY> entityClass, Permission permission);

    <ENTITY extends IdEntity> List<ENTITY> getAll(Class<ENTITY> entityClass, Permission permission, String sid);

    <ENTITY extends IdEntity> List<ENTITY> getAll(Class<ENTITY> entityClass, String orderByProperty, boolean isAsc,
                                                  Permission permission, String sid);

    <ENTITY extends IdEntity> List<ENTITY> getAll(Class<ENTITY> entityClass, List<Permission> permissions,
                                                  List<String> sids);

    // 条件查询，当前用户ID默认权限为读，QueryInfo查询信息包括条件字段、分页信息、排序等
    @Deprecated
    <ENTITY extends IdEntity> QueryInfo<ENTITY> query(Class<ENTITY> entityClass, QueryInfo<ENTITY> queryInfo);

    @Deprecated
    <ENTITY extends IdEntity> QueryInfo<ENTITY> query(Class<ENTITY> entityClass, QueryInfo<ENTITY> queryInfo,
                                                      Permission permission);

    <ENTITY extends IdEntity> QueryInfo<ENTITY> query(Class<ENTITY> entityClass, QueryInfo<ENTITY> queryInfo,
                                                      Permission permission, String sid);

    <ENTITY extends IdEntity> QueryInfo<ENTITY> query(Class<ENTITY> entityClass, QueryInfo<ENTITY> queryInfo,
                                                      List<Permission> permissions, List<String> sids);

    <ENTITY extends IdEntity> List<QueryItem> queryForItem(Class<ENTITY> entityClass, QueryInfo<ENTITY> queryInfo,
                                                           Permission permission, String sid);

    <ENTITY extends IdEntity> List<QueryItem> queryForItem(Class<ENTITY> entityClass, QueryInfo<ENTITY> queryInfo,
                                                           List<Permission> permissions, List<String> sids);

    /**
     * 保存ACL并增加管理员权限
     *
     * @param entity
     * @return
     * @see com.wellsoft.pt.security.acl.service.AclService#createAcl(com.wellsoft.pt.core.entity.IdEntity, com.wellsoft.pt.core.entity.IdEntity, java.lang.String, org.springframework.security.acls.model.Permission)
     */
    @Deprecated
    <ENTITY extends IdEntity> void save(ENTITY entity);

    /**
     * 保存ACL并增加SID管理员权限
     *
     * @param entity
     * @return
     * @see com.wellsoft.pt.security.acl.service.AclService#createAcl(com.wellsoft.pt.core.entity.IdEntity, com.wellsoft.pt.core.entity.IdEntity, java.lang.String, org.springframework.security.acls.model.Permission)
     */
    @Deprecated
    <ENTITY extends IdEntity> void save(ENTITY entity, String sid);

    /**
     * 保存ACL并增加SID指定的权限
     *
     * @param entity
     * @return
     * @see com.wellsoft.pt.security.acl.service.AclService#createAcl(com.wellsoft.pt.core.entity.IdEntity, com.wellsoft.pt.core.entity.IdEntity, java.lang.String, org.springframework.security.acls.model.Permission)
     */
    @Deprecated
    <ENTITY extends IdEntity> void save(ENTITY entity, String sid, Permission permission);

    /**
     * 保存ACL并增加管理员权限。允许指定实体的父节点
     *
     * @param entity
     * @return
     * @see com.wellsoft.pt.security.acl.service.AclService#createAcl(com.wellsoft.pt.core.entity.IdEntity, com.wellsoft.pt.core.entity.IdEntity, java.lang.String, org.springframework.security.acls.model.Permission)
     */
    @Deprecated
    <ENTITY extends IdEntity> void save(ENTITY entity, ENTITY parentEntity);

    /**
     * 保存ACL并增加SID管理员权限。允许指定实体的父节点
     *
     * @param entity
     * @return
     * @see com.wellsoft.pt.security.acl.service.AclService#createAcl(com.wellsoft.pt.core.entity.IdEntity, com.wellsoft.pt.core.entity.IdEntity, java.lang.String, org.springframework.security.acls.model.Permission)
     */
    @Deprecated
    <ENTITY extends IdEntity> void save(ENTITY entity, ENTITY parentEntity, String sid);

    /**
     * 保存ACL并增加SID指定的权限。允许指定实体的父节点
     *
     * @param entity
     * @return
     */
    @Deprecated
    <ENTITY extends IdEntity> void save(ENTITY entity, ENTITY parentEntity, String sid, Permission permission);

    /**
     * 删除实体所具有的ACL相关权限。若存在子结点，则子结点也会删除
     *
     * @param entity
     */
    <ENTITY extends IdEntity> void remove(ENTITY entity);

    <ENTITY extends IdEntity> void removeAcl(Class<ENTITY> entityClass, String entityUuid);

    <ENTITY extends IdEntity> void removeAll(Collection<ENTITY> entities);

    <ENTITY extends IdEntity> void removeByPk(Class<ENTITY> entityClass, String entityUuid);

    <ENTITY extends IdEntity> void removeAllByPk(Class<ENTITY> entityClass, Collection<String> entityUuids);

    /**
     * 更改实体的所有者，原所有者的访问权限也更新为新的所有者
     *
     * @param entity
     * @param sid
     */
    <ENTITY extends IdEntity> void changeOwner(Class<ENTITY> entityClass, String entityUuid, String sid);

    /**
     * 更改实体的访问权限，对原实体的所有访问权限更改为对新实体的访问权限，若原实体不存在直接返回
     *
     * @param entity
     * @param sid
     */
    <ENTITY extends IdEntity> void changeAcl(Class<ENTITY> entityClass, String entityUuid, String newEntityUuid);

    /**
     * 合并实体的访问权限，对原实体的所有访问权限合并到对新实体的访问权限，若目标实体不存在则创建
     *
     * @param entity
     * @param sid
     */
    <ENTITY extends IdEntity> void mergeAcl(Class<ENTITY> entityClass, String sourceEntityUuid,
                                            String targetEntityUuid);

    /**
     * 增加权限，实体增加权限，当前用户ID
     *
     * @param entity
     * @param permission
     */
    @Deprecated
    <ENTITY extends IdEntity> void addPermission(ENTITY entity, Permission permission);

    @Deprecated
    <ENTITY extends IdEntity> void addPermission(ENTITY entity, Permission permission, String sid);

    <ENTITY extends IdEntity> void addPermission(Class<ENTITY> entityClass, String entityUuid, Permission permission,
                                                 String sid);

    <ENTITY extends IdEntity> void addPermission(Class<ENTITY> entityClass, String entityUuid, Permission permission,
                                                 Set<String> sids);

    // 删除权限，删除当前用户ID的权限
    // @Deprecated
    // <ENTITY extends IdEntity> void removePermission(ENTITY entity, Permission
    // permission);

    // 删除权限，删除所有用户、角色、群组的权限
    <ENTITY extends IdEntity> void removePermission(Class<ENTITY> entityClass, String entityUuid,
                                                    Permission permission);

    // 删除权限，删除所有用户、角色、群组的权限
    <ENTITY extends IdEntity> void removePermission(Class<ENTITY> entityClass, String entityUuid);

    @Deprecated
    <ENTITY extends IdEntity> void removePermission(ENTITY entity, Permission permission, String sid);

    <ENTITY extends IdEntity> void removePermission(Class<ENTITY> entityClass, String entityUuid, Permission permission,
                                                    String sid);

    <ENTITY extends IdEntity> void removePermissions(Class<ENTITY> entityClass, String entityUuid, List<Permission> permissions,
                                                     Collection<String> sids);

    // 删除权限，删除SID的所有实体的相应权限
    <ENTITY extends IdEntity> void removePermission(Permission permission, String sid);

    // 删除权限，删除SID对实体的所有权限
    <ENTITY extends IdEntity> void removePermission(ENTITY entity, String sid);

    // 判断是否有指定权限
    <ENTITY extends IdEntity> boolean hasPermission(ENTITY entity, Permission permission);

    <ENTITY extends IdEntity> boolean hasPermission(Class<ENTITY> entityClass, String entityUuid,
                                                    Permission permission);

    <ENTITY extends IdEntity> boolean hasPermission(ENTITY entity, Permission permission, String sid);

    <ENTITY extends IdEntity> boolean hasPermission(Class<ENTITY> entityClass, String entityUuid, Permission permission,
                                                    String sid);

    // 获取所有对实体的操作权限列表
    @Deprecated
    <ENTITY extends IdEntity> List<Permission> getPermission(ENTITY entity);

    <ENTITY extends IdEntity> List<Permission> getPermission(ENTITY entity, String sid);

    // 判断对象标识是否授权
    boolean isGranted(String objectIdIdentity, List<Permission> permissions, List<String> sids);

    // 判断对象标识是否授权
    boolean isGranted(String objectIdIdentity, List<String> sids);

    // 删除指定用户ID/角色的ACL中的所有实体的操作权限列表
    void removeSid(String sid);

    // 获取当前用户ID/角色名的SID列表
    <ENTITY extends IdEntity> List<AclSid> getSid(ENTITY entity);

    <ENTITY extends IdEntity> List<AclSid> getSid(ENTITY entity, Permission permission);

    <ENTITY extends IdEntity> List<AclSid> getSid(Class<ENTITY> entityClass, String entityUuid, Permission permission);

    // /////////////////////////////////////////SID
    // 成员相关///////////////////////////////////////////
    // 当前用户ID作为成员在moduleId中的SID列表查询
    @Deprecated
    <ENTITY extends IdEntity> QueryInfo<ENTITY> query(Class<ENTITY> entityClass, QueryInfo<ENTITY> queryInfo,
                                                      List<Permission> permissions, String moduleId);

    // 指定成员在moduleId中的SID列表查询
    <ENTITY extends IdEntity> QueryInfo<ENTITY> query(Class<ENTITY> entityClass, QueryInfo<ENTITY> queryInfo,
                                                      List<Permission> permissions, String member, String moduleId);

    // 指定成员在moduleId中的SID列表查询
    <ENTITY extends IdEntity> List<QueryItem> queryForItem(Class<ENTITY> entityClass, QueryInfo<ENTITY> queryInfo,
                                                           List<Permission> permissions, String member, String moduleId);

    // 指定成员在sids及moduleId中的SID列表查询
    @Deprecated
    <ENTITY extends IdEntity> QueryInfo<ENTITY> queryAll(Class<ENTITY> entityClass, QueryInfo<ENTITY> queryInfo,
                                                         List<Permission> permissions, String member, String moduleId);

    // 指定成员在sids及moduleId中的SID列表查询
    @Deprecated
    <ENTITY extends IdEntity> List<QueryItem> queryAllForItem(Class<ENTITY> entityClass, QueryInfo<ENTITY> queryInfo,
                                                              List<Permission> permissions, String member, String moduleId);

    // 判断成员在moduleId中的SID中是否具有指定权限
    <ENTITY extends IdEntity> boolean hasPermission(ENTITY entity, Permission permission, String sid, String member,
                                                    String moduleId);

    <ENTITY extends IdEntity> boolean hasPermission(Class<ENTITY> entityClass, String entityUuid, Permission permission,
                                                    String sid, String member, String moduleId);

    // 判断成员在moduleId中的所有SID中是否具有指定权限
    <ENTITY extends IdEntity> boolean hasPermission(ENTITY entity, Permission permission, String member,
                                                    String moduleId);

    <ENTITY extends IdEntity> boolean hasPermission(Class<ENTITY> entityClass, String entityUuid, Permission permission,
                                                    String member, String moduleId);

    // 获取成员在moduleId对实体具有的所有权限
    <ENTITY extends IdEntity> List<Permission> getPermission(ENTITY entity, String member, String moduleId);

    <ENTITY extends IdEntity> List<Permission> getAllPermission(ENTITY entity, String member, String moduleId);

    // 获取SID在moduleId中的成员
    List<AclSidMember> getMember(String sid, String moduleId);

    // 向SID增加模块为moduleId的成员
    void addMember(String sid, String member, String moduleId);

    // 删除SID模块为moduleId的成员
    void removeMember(String sid, String member, String moduleId);

    // 删除SID模块为moduleId的所有的成员
    void removeAllMember(String sid, String moduleId);

    // 判断SID是否有模块为moduleId的成员
    boolean hasMember(String sid, String member, String moduleId);

    // 创建指定用户ID/角色的ACL
    <ENTITY extends IdEntity> void createAcl(ENTITY entity, String sid);

    // 创建指定用户ID/角色的ACL并增加SID指定权限
    <ENTITY extends IdEntity> void createAcl(ENTITY entity, String sid, Permission permission);

    // 创建指定用户ID/角色的ACL，允许指定实体的父节点
    <ENTITY extends IdEntity> void createAcl(ENTITY entity, ENTITY parentEntity, String sid);

    // 创建指定用户ID/角色的ACL并增加SID指定权限，允许指定实体的父节点
    <ENTITY extends IdEntity> void createAcl(ENTITY entity, ENTITY parentEntity, String sid, Permission permission);

    /**
     * 判断实体的ACL是否存在
     *
     * @param entity
     * @return
     * @see hasAcl(Class<ENTITY> entityClass, String entityUuid) instead
     */
    @Deprecated
    <ENTITY extends IdEntity> boolean hasAcl(ENTITY entity);

    // 判断实体的ACL是否存在
    <ENTITY extends IdEntity> boolean hasAcl(Class<ENTITY> entityClass, String entityUuid);

    // 根据查询信息查询总条数
    <ENTITY extends IdEntity> Long count(Class<ENTITY> entityClass, QueryInfo<ENTITY> queryInfo,
                                         List<Permission> permissions, List<String> sids);

    // 根据查询信息查询总条数
    <ENTITY extends IdEntity> Long count(Class<ENTITY> entityClass, QueryInfo<ENTITY> queryInfo,
                                         List<Permission> permissions, String member, String moduleId);

    // 根据查询信息查询总条数
    <ENTITY extends IdEntity> Long countAll(Class<ENTITY> entityClass, QueryInfo<ENTITY> queryInfo,
                                            List<Permission> permissions, String member, String moduleId);

    /* add by huanglinchuan 2014.10.29 begin */

    /**
     * 根据自己定义acl条件HQL检索授权数据集合
     *
     * @param entityClass    授权对象
     * @param queryInfo      授权对象的过滤条件
     * @param hqlForAcl      acl过滤HQL语句
     * @param hqlParasForAcl acl过滤HQL语句对应的参数集合
     * @return
     */
    <ENTITY extends IdEntity> List<QueryItem> queryForItemByCustomHql(Class<ENTITY> entityClass,
                                                                      QueryInfo<ENTITY> queryInfo, String hqlForAcl, Map<String, Object> hqlParasForAcl);

    /* add by huanglinchuan 2014.10.29 end */
    /* add by huanglinchuan 2014.11.11 begin */

    /**
     * 检查当用户是否具有某个数据对象的指定权限，包括用户所在的机构组织等sid
     *
     * @param entityClass
     * @param entityUuid
     * @param permission
     * @param userId
     * @return
     */
    <ENTITY extends IdEntity> boolean hasPermissionByUserId(Class<ENTITY> entityClass, String entityUuid,
                                                            Permission permission, String userId);

    /**
     * 检索指定用户对某个数据对象的所有权限，包括用户所在的机构组织等sid
     *
     * @param entity
     * @param userId
     * @return
     */
    <ENTITY extends IdEntity> List<Permission> getAllPermissionByUserId(ENTITY entity, String userId);

    /**
     * @param entityClass
     * @param entityUuids
     * @param sids
     * @return
     */
    <ENTITY extends IdEntity> List<Permission> getAllPermissionBySids(ENTITY entity, List<String> sids);

    /**
     * 根据自己定义acl条件HQL检索授权数据集合,返回查询对象
     *
     * @param entityClass
     * @param queryInfo
     * @param hqlForAcl
     * @param hqlParasForAcl
     * @return
     */
    <ENTITY extends IdEntity> QueryInfo<ENTITY> queryAllByCustomHql(Class<ENTITY> entityClass,
                                                                    QueryInfo<ENTITY> queryInfo, String hqlForAcl, Map<String, Object> hqlParasForAcl);

    /* add by huanglinchuan 2014.11.11 end */
    boolean isGranted(String objectIdIdentity, Integer[] masks, List<String> sids);

    /**
     * 获取指定用户的查阅权限的权限列表数据
     *
     * @param userId
     * @return java.util.List<com.wellsoft.pt.security.acl.entity.AclEntry>
     **/
    List<AclEntry> getConsultListByUserId(String userId);
}
