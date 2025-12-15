package com.wellsoft.pt.security.acl.service;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.security.acl.entity.AclTaskEntry;
import org.springframework.security.acls.model.Permission;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Description: 环节权限实例实现类
 *
 * @author liuxj
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本  修改人    修改日期      修改内容
 * V1.0   liuxj    2024/12/19    Create
 * </pre>
 * @date 2024/12/19
 */
public interface AclTaskService {

    /**
     * 添加待办权限
     *
     * @param sids         身份标识列表
     * @param taskInstUuid 实例id
     */
    void addTodoPermission(Set<String> sids, String taskInstUuid);

//    /**
//     * 添加代办权限
//     *
//     * @param map          权限map
//     * @param sids         标识id列表
//     * @param taskInstUuid 环节实例id
//     */
//    void addTodoPermission(Map<String, AclTaskEntry> map, Set<String> sids, String taskInstUuid);

    /**
     * 移除待办权限
     *
     * @param sid
     * @param taskInstUuid
     */
    void removeTodoPermission(String sid, String taskInstUuid);

    /**
     * 添加待办权限
     *
     * @param sid
     * @param taskInstUuid
     */
    void addTodoPermission(String sid, String taskInstUuid);

    /**
     * 添加已办权限
     *
     * @param sid
     * @param taskInstUuid
     */
    void addDonePermission(String sid, String taskInstUuid);

    /**
     * 添加已办权限
     *
     * @param sid
     * @param taskInstUuid
     */
    // void addDonePermission(Map<String, AclTaskEntry> map, String sid, String taskInstUuid);

    /**
     * 添加已办权限
     *
     * @param userId
     * @param sids
     * @param taskInstUuid
     */
    void addDonePermission(String userId, List<String> sids, String taskInstUuid);

    /**
     * @param sid
     * @param taskInstUuid
     * @return
     */
    List<String> listSidDoneMarkerUserId(String sid, String taskInstUuid);

    /**
     * @param taskInstUuid
     * @return
     */
    List<String> listSidDoneMarkerUserId(String taskInstUuid);

    /**
     * 移除已办权限
     *
     * @param sid
     * @param taskInstUuid
     */
    void removeDonePermission(String sid, String taskInstUuid);

    /**
     * 删除SID的所有权限
     *
     * @param sid
     * @param entityUuid
     */
    void removeSidPermission(String sid, String entityUuid);

    /**
     * 清空没有权限的数据
     *
     * @param entityUuid
     */
    void clearEmptyPermission(String entityUuid);

    /**
     * 添加阅读权限
     *
     * @param userId
     * @param userSids
     * @param taskInstUuid
     */
    void addFlagReadPermission(String userId, List<String> userSids, String taskInstUuid);

    /**
     * 添加未阅权限
     *
     * @param userId
     * @param taskInstUuid
     */
    void addUnreadPermission(String userId, String taskInstUuid);

    /**
     * 添加未阅权限
     *
     * @param userIds
     * @param taskInstUuid
     */
    void addUnreadPermission(Set<String> userIds, String taskInstUuid);

    /**
     * 添加督办权限
     *
     * @param sid
     * @param taskInstUuid
     */
    void addSupervisePermission(String sid, String taskInstUuid);

    /**
     * 添加督办权限
     *
     * @param sids
     * @param taskInstUuid
     */
    void addSupervisePermission(Set<String> sids, String taskInstUuid);

    /**
     * 添加权限到原有map权限列表去
     *
     * @param map        原有权限列表
     * @param sid        身份标识
     * @param entityUuid 实例uuid
     * @param permission 权限类别
     */
    // void addPermissionToMap(Map<String, AclTaskEntry> map, String sid, String entityUuid, Permission permission);

    /**
     * 添加权限
     *
     * @param entityUuid 实例uuid
     * @param permission 权限
     * @param sids       身份标识列表
     */
    void addPermission(String entityUuid, Permission permission, Set<String> sids);

    /**
     * 更新acl权限
     *
     * @param entityUuid    原来的实例uuid
     * @param newEntityUuid 新的实例uuid
     */
    @Transactional(rollbackFor = Exception.class)
    void changeAcl(String entityUuid,
                   String newEntityUuid);

    /**
     * 添加权限
     *
     * @param entityClass 实体类
     * @param entityUuid  实例uuid
     * @param permission  权限
     * @param sids        身份标识列表
     */
    @Transactional
    <ENTITY extends IdEntity> void addPermission(Class<ENTITY> entityClass, String entityUuid, Permission permission, Set<String> sids);

    /**
     * 查询权限信息
     *
     * @param entityClass 实体类信息
     * @param queryInfo   查询条件
     * @param permission  权限信息
     * @param sid         身份标识
     * @param <ENTITY>
     * @return
     */
//    @Transactional(readOnly = true)
//    <ENTITY extends IdEntity> QueryInfo<ENTITY> query(Class<ENTITY> entityClass, QueryInfo<ENTITY> queryInfo,
//                                                      Permission permission, String sid);

    /**
     * 查询权限信息
     *
     * @param entityClass 实体类信息
     * @param queryInfo   查询条件
     * @param permission  权限信息
     * @param sids        身份标识列表
     * @param <ENTITY>
     * @return
     */
//    @Transactional(readOnly = true)
//    <ENTITY extends IdEntity> QueryInfo<ENTITY> query(Class<ENTITY> entityClass, QueryInfo<ENTITY> queryInfo,
//                                                      Permission permission, List<String> sids);

    /**
     * 迁移旧权限,将AclEntry 环节实例迁移到AclTaskEntry
     *
     * @param messageSend 消息消费
     */
    void convertAclEntry(Consumer<String> messageSend);

    /**
     * 添加权限
     *
     * @param entityUuid 实例uuid
     * @param permission 权限
     * @param sid        身份标识
     */
    void addPermission(String entityUuid, Permission permission, String sid);

    /**
     * 添加权限
     *
     * @param entityUuid 实例uuid
     * @param permission 权限
     * @param sid        身份标识
     */
    void addPermission(String entityUuid, Permission permission, String sid, String doneUserId);

    /**
     * 获取同一个用户及群组id的权限
     *
     * @param entityUuid 实例id
     * @param sids       用户身份标识id列表
     * @return
     */
    List<Permission> getAllPermissionBySids(String entityUuid, List<String> sids);

    /**
     * 获取用户所有的权限
     *
     * @param entityUuid 实例id
     * @param sid        用户身份标识id
     * @return
     */
    List<Permission> getPermission(String entityUuid, String sid);

    /**
     * 是否有授权
     *
     * @param objectIdIdentity 实例id
     * @param masks            权限列表
     * @param sids             身份id表示
     * @return
     */
    boolean isGranted(String objectIdIdentity, Integer[] masks, List<String> sids);

    /**
     * 是否授权
     *
     * @param objectIdIdentity 对象实例id
     * @param permissions      权限列表
     * @param sids             身份标识列表
     * @return
     */
    boolean isGranted(String objectIdIdentity, List<Permission> permissions, List<String> sids);

    /**
     * 获取授权where条件
     *
     * @param permissions 权限列表
     * @return
     */
    String getAuthWhere(Collection<Permission> permissions);

    /**
     * 是否授权
     *
     * @param objectIdIdentity 对象实例id
     * @param sids             身份标识列表
     * @return
     */
    boolean isGranted(String objectIdIdentity, List<String> sids);

    /**
     * 是否拥有权限
     *
     * @param entityUuid 实例uuid
     * @param permission 权限列表
     * @param sid        标识id
     * @return
     */
    boolean hasPermission(String entityUuid, Permission permission, String sid);

    /**
     * 是否拥有权限
     *
     * @param entityUuid 实例uuid
     * @param permission 权限列表
     * @return
     */
    boolean hasPermission(String entityUuid, Permission permission);

    /**
     * 删除权限
     *
     * @param entityUuid 实例uuid
     */
    void removePermission(String entityUuid);

    /**
     * 删除权限
     *
     * @param entityUuid 实例id
     * @param permission 权限
     * @param sid        身份标识
     */
    void removePermission(String entityUuid, Permission permission, String sid);

    /**
     * 删除权限
     *
     * @param entityUuid 实例id
     * @param permission 权限
     * @param sid        身份标识
     */
    void removePermission(String entityUuid, List<Permission> permissions, String sid);

    /**
     * 删除该实例所有权限
     *
     * @param entityUuid 实例uuid
     * @param permission 权限
     */
    void removePermission(String entityUuid, Permission permission);

    void removeUserDoneMarker(String entityUuid, String sid, String userId);

    void removeUserDoneMarker(String entityUuid, String userId);

    /**
     * 批量保存权限
     *
     * @param map 权限map
     */
    void savePermission(Map<String, AclTaskEntry> map);

    /**
     * 获取sid列表
     *
     * @param entityUuid 实例uuid
     * @return
     */
    @Transactional(readOnly = true)
    List<AclTaskEntry> getSid(String entityUuid);

    /**
     * 删除权限
     *
     * @param entityUuid 实例uuid
     */
    void removeAcl(String entityUuid);

    /**
     * 获取sid 列表
     *
     * @param entityUuid 实例uuid
     * @param permission 权限类型
     * @return
     */
    @Transactional(readOnly = true)
    List<AclTaskEntry> getSid(String entityUuid, Permission permission);

}
