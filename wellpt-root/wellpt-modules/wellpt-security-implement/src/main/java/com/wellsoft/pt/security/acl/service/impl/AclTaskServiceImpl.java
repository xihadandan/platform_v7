package com.wellsoft.pt.security.acl.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.jpa.dao.UniversalDao;
import com.wellsoft.pt.security.acl.dao.AclTaskDoneMarkerDao;
import com.wellsoft.pt.security.acl.dao.AclTaskReadMarkerDao;
import com.wellsoft.pt.security.acl.entity.AclTaskDoneMarker;
import com.wellsoft.pt.security.acl.entity.AclTaskEntry;
import com.wellsoft.pt.security.acl.entity.AclTaskReadMarker;
import com.wellsoft.pt.security.acl.service.AclEntryService;
import com.wellsoft.pt.security.acl.service.AclTaskEntryService;
import com.wellsoft.pt.security.acl.service.AclTaskService;
import com.wellsoft.pt.security.acl.support.AclPermission;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Description: 环节实例权限服务实现类
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
@Service
@Transactional(readOnly = true)
public class AclTaskServiceImpl implements AclTaskService {

    @Autowired
    private AclTaskEntryService aclTaskEntryService;

    @Resource(name = "universalDao")
    protected UniversalDao dao;

    @Autowired
    private AclEntryService aclEntryService;

    @Autowired
    private AclTaskReadMarkerDao aclTaskReadMarkerDao;

    @Autowired
    private AclTaskDoneMarkerDao aclTaskDoneMarkerDao;

    private static final Log log = LogFactory.getLog(AclTaskServiceImpl.class);

    /**
     * 是否拥有权限
     */
    public static final String HAS_PERMISSION = "select count(*) from  AclTaskEntry WHERE sid = :sid and objectIdIdentity=:objectIdIdentity and {}=:authValue";
    public static final String HAS_OBJECT_PERMISSION = "select count(*) from  AclTaskEntry WHERE objectIdIdentity=:objectIdIdentity and {}=:authValue";

    /**
     * 获取权限
     */
    public static final String GET_PERMISSION = "from  AclTaskEntry WHERE sid = :sid and objectIdIdentity=:objectIdIdentity";

    /**
     * 根据多个sid获取权限
     */
    public static final String GET_PERMISSION_BY_SIDS = "from  AclTaskEntry WHERE sid in ( :sids) and objectIdIdentity=:objectIdIdentity";

    /**
     * 获取sid列表
     */
    public static final String GET_SIDS = "from  AclTaskEntry WHERE objectIdIdentity=:objectIdIdentity ";

    /**
     * 删除权限
     */
    public static final String REMOVE_PERMISSION = " delete from  AclTaskEntry WHERE objectIdIdentity=:objectIdIdentity ";

    /**
     * 根据权限条件获取sid列表
     */
    public static final String GET_SIDS_WITH_PERMISSION = "from  AclTaskEntry WHERE objectIdIdentity=:objectIdIdentity and {}=:authValue";


    /**
     * 是否拥有任意权限
     */
    public static final String HAS_ANY_PERMISSION = "select count(*) from  AclTaskEntry a WHERE sid in ( :sids) and objectIdIdentity=:objectIdIdentity  ";

    /**
     * 更新对象标识
     */
    public static final String UPDATE_OBJECT_ID_IDENTITY = " update AclTaskEntry set objectIdIdentity = :newObjectIdIdentity where objectIdIdentity=:objectIdIdentity";

    /**
     * 获取旧的权限列表
     */
    private static final String GET_ACL_ENTRY = " select a.MASK,as2.SID, a.OBJECT_ID_IDENTITY   " +
            " from acl_entry a LEFT JOIN ACL_SID as2  ON AS2.UUID = a.SID " +
            "WHERE EXISTS (" +
            "select 1 from ACL_OBJECT_IDENTITY t WHERE a.ACL_OBJECT_IDENTITY = t.uuid  AND   t.OBJECT_ID_CLASS " +
            "=(SELECT c.uuid FROM ACL_CLASS c WHERE c.class= 'com.wellsoft.pt.bpm.engine.entity.TaskInstance') " +
            ") 	";

    /**
     * 获取旧的权限列表个数
     */
    private static final String GET_ACL_ENTRY_COUNT = " select  count(*)   " +
            " from acl_entry a LEFT JOIN ACL_SID as2  ON AS2.UUID = a.SID " +
            "WHERE EXISTS (" +
            "select 1 from ACL_OBJECT_IDENTITY t WHERE a.ACL_OBJECT_IDENTITY = t.uuid  AND   t.OBJECT_ID_CLASS " +
            "=(SELECT c.uuid FROM ACL_CLASS c WHERE c.class= 'com.wellsoft.pt.bpm.engine.entity.TaskInstance') " +
            ") 	";

    @Override
    @Transactional
    public void addTodoPermission(String sid, String taskInstUuid) {
        addPermission(taskInstUuid, AclPermission.TODO, sid);
    }


    @Override
    @Transactional
    public void addTodoPermission(Set<String> sids, String taskInstUuid) {
        addPermission(taskInstUuid, AclPermission.TODO, sids);
    }

//    @Override
//    @Transactional
//    public void addTodoPermission(Map<String, AclTaskEntry> map, Set<String> sids, String taskInstUuid) {
//        addPermissionToMap(map, sids, taskInstUuid, AclPermission.TODO);
//    }

    @Override
    @Transactional
    public void removeTodoPermission(String sid, String taskInstUuid) {
        removePermission(taskInstUuid, AclPermission.TODO, sid);
    }

    @Override
    @Transactional
    public void addDonePermission(String sid, String taskInstUuid) {
        addPermission(taskInstUuid, AclPermission.DONE, sid);
    }


//    @Override
//    @Transactional
//    public void addDonePermission(Map<String, AclTaskEntry> map, String sid, String taskInstUuid) {
//        addPermissionToMap(map, sid, taskInstUuid, AclPermission.DONE);
//    }

    @Override
    @Transactional
    public void addDonePermission(String userId, List<String> sids, String taskInstUuid) {
        addPermission(taskInstUuid, AclPermission.DONE, userId);
        // 大于用户的权限粒度添加已办时，记录对应的已办记录
        if (CollectionUtils.isNotEmpty(sids)) {
            List<AclTaskEntry> aclTaskEntries = getAclTaskEntryByPermissionAndSids(taskInstUuid, AclPermission.TODO, sids);
            aclTaskEntries = aclTaskEntries.stream().filter(entry -> !StringUtils.startsWith(entry.getSid(), IdPrefix.USER.getValue())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(aclTaskEntries)) {
                List<AclTaskDoneMarker> doneMarkers = aclTaskEntries.stream().flatMap(entry -> {
                    long count = aclTaskDoneMarkerDao.countByAclTaskUuidAndUserId(entry.getUuid(), userId);
                    if (count > 0) {
                        return Stream.empty();
                    }
                    AclTaskDoneMarker readMarker = new AclTaskDoneMarker();
                    readMarker.setAclTaskUuid(entry.getUuid());
                    readMarker.setUserId(userId);
                    return Stream.of(readMarker);
                }).collect(Collectors.toList());
                aclTaskDoneMarkerDao.saveAll(doneMarkers);
            }
        }
    }

    @Override
    public List<String> listSidDoneMarkerUserId(String sid, String taskInstUuid) {
        List<String> aclTaskUuids = getAclTaskEntryUuidByPermissionAndSids(taskInstUuid, AclPermission.TODO, Lists.newArrayList(sid));
        if (CollectionUtils.isEmpty(aclTaskUuids)) {
            return Collections.emptyList();
        }
        return aclTaskDoneMarkerDao.listUserIdByAclTaskUuids(aclTaskUuids);
    }

    @Override
    public List<String> listSidDoneMarkerUserId(String taskInstUuid) {
        List<String> aclTaskUuids = getAclTaskEntryUuidByPermission(taskInstUuid, AclPermission.TODO);
        if (CollectionUtils.isEmpty(aclTaskUuids)) {
            return Collections.emptyList();
        }
        return aclTaskDoneMarkerDao.listUserIdByAclTaskUuids(aclTaskUuids);
    }

    @Override
    @Transactional
    public void removeDonePermission(String sid, String taskInstUuid) {
        removePermission(taskInstUuid, AclPermission.DONE, sid);
    }

    @Override
    @Transactional
    public void removeSidPermission(String sid, String entityUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("sid", sid);
        params.put("objectIdIdentity", entityUuid);

        if (!StringUtils.startsWith(sid, IdPrefix.USER.getValue())) {
            String hql1 = "delete from AclTaskReadMarker where aclTaskUuid in(select uuid from AclTaskEntry WHERE sid = :sid and objectIdIdentity=:objectIdIdentity)";
            aclTaskReadMarkerDao.deleteByHQL(hql1, params);

            String hql2 = "delete from AclTaskDoneMarker where aclTaskUuid in(select uuid from AclTaskEntry WHERE sid = :sid and objectIdIdentity=:objectIdIdentity)";
            aclTaskDoneMarkerDao.deleteByHQL(hql2, params);
        }

        String hql3 = "delete from AclTaskEntry WHERE sid = :sid and objectIdIdentity=:objectIdIdentity";
        aclTaskEntryService.deleteByHQL(hql3, params);
    }

    @Override
    @Transactional
    public void clearEmptyPermission(String entityUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("objectIdIdentity", entityUuid);
        params.put("auth", false);

        String hql = " from AclTaskEntry WHERE objectIdIdentity=:objectIdIdentity and readAuth=:auth and todoAuth=:auth " +
                "and doneAuth=:auth and attentionAuth=:auth and unReadAuth=:auth and flagReadAuth=:auth and superviseAuth=:auth and monitorAuth=:auth";

        String hql1 = "delete from AclTaskReadMarker where aclTaskUuid in(select uuid " + hql + ")";
        aclTaskReadMarkerDao.deleteByHQL(hql1, params);

        String hql2 = "delete from AclTaskDoneMarker where aclTaskUuid in(select uuid " + hql + ")";
        aclTaskDoneMarkerDao.deleteByHQL(hql2, params);

        String hql3 = "delete" + hql;
        aclTaskEntryService.deleteByHQL(hql3, params);
    }

    @Override
    @Transactional
    public void addFlagReadPermission(String userId, List<String> userSids, String taskInstUuid) {
        addPermission(taskInstUuid, AclPermission.FLAG_READ, userId);

        // 大于用户的权限粒度标记为已读时，记录对应的已读记录
        if (CollectionUtils.isNotEmpty(userSids)) {
            List<AclTaskEntry> aclTaskEntries = getAclTaskEntryByPermissionAndSids(taskInstUuid, AclPermission.UNREAD, userSids);
            aclTaskEntries = aclTaskEntries.stream().filter(entry -> !StringUtils.startsWith(entry.getSid(), IdPrefix.USER.getValue())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(aclTaskEntries)) {
                List<AclTaskReadMarker> readMarkers = aclTaskEntries.stream().flatMap(entry -> {
                    long count = aclTaskReadMarkerDao.countByAclTaskUuidAndUserId(entry.getUuid(), userId);
                    if (count > 0) {
                        return Stream.empty();
                    }
                    AclTaskReadMarker readMarker = new AclTaskReadMarker();
                    readMarker.setAclTaskUuid(entry.getUuid());
                    readMarker.setUserId(userId);
                    return Stream.of(readMarker);
                }).collect(Collectors.toList());
                aclTaskReadMarkerDao.saveAll(readMarkers);
            }
        }
    }

    @Override
    @Transactional
    public void addUnreadPermission(String userId, String taskInstUuid) {
        addPermission(taskInstUuid, AclPermission.UNREAD, userId);
    }

    @Override
    @Transactional
    public void addUnreadPermission(Set<String> userIds, String taskInstUuid) {
        addPermission(taskInstUuid, AclPermission.UNREAD, userIds);
    }

    @Override
    @Transactional
    public void addSupervisePermission(String sid, String taskInstUuid) {
        addPermission(taskInstUuid, AclPermission.SUPERVISE, sid);
    }

    @Override
    @Transactional
    public void addSupervisePermission(Set<String> sids, String taskInstUuid) {
        addPermission(taskInstUuid, AclPermission.SUPERVISE, sids);
    }


//    /**
//     * 添加权限到原有map权限列表去
//     *
//     * @param map        原有权限列表
//     * @param sids       身份标识列表
//     * @param entityUuid 实例uuid
//     * @param permission 权限类别
//     */
//    public void addPermissionToMap(Map<String, AclTaskEntry> map, Set<String> sids, String entityUuid, Permission permission) {
//        for (String sid : sids) {
//            addPermissionToMap(map, sid, entityUuid, permission);
//        }
//    }

//    @Override
//    @Transactional
//    public void addPermissionToMap(Map<String, AclTaskEntry> map, String sid, String entityUuid, Permission permission) {
//        if (ObjectUtil.isNull(map)) {
//            map = new LinkedHashMap<>();
//        }
//        AclTaskEntry aclTaskEntry = map.get(sid);
//        if (aclTaskEntry == null) {
//            aclTaskEntry = new AclTaskEntry();
//        }
//        setAclTaskEntry(sid, entityUuid, permission, aclTaskEntry);
//    }

    @Override
    @Transactional
    public void addPermission(String entityUuid, Permission permission, Set<String> sids) {
        List<AclTaskEntry> list = new ArrayList<>(sids.size());
        for (String sid : sids) {
            AclTaskEntry aclTaskEntry = setAclTaskEntry(entityUuid, permission, sid);
            list.add(aclTaskEntry);
        }
        aclTaskEntryService.saveAll(list);

        if (AclPermission.TODO.equals(permission)) {
            removeDoneMarker(entityUuid, sids);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeAcl(String entityUuid, String newEntityUuid) {
        if (StringUtils.isBlank(entityUuid) || StringUtils.isBlank(newEntityUuid)) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("objectIdIdentity", entityUuid);
        params.put("newObjectIdIdentity", newEntityUuid);
        aclTaskEntryService.updateByHQL(UPDATE_OBJECT_ID_IDENTITY, params);

        String hql = "delete from AclTaskDoneMarker where aclTaskUuid in(select uuid from AclTaskEntry WHERE objectIdIdentity=:newObjectIdIdentity)";
        aclTaskDoneMarkerDao.deleteByHQL(hql, params);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <ENTITY extends IdEntity> void addPermission(Class<ENTITY> entityClass, String entityUuid, Permission permission, Set<String> sids) {
        addPermission(entityUuid, permission, sids);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public <ENTITY extends IdEntity> QueryInfo<ENTITY> query(Class<ENTITY> entityClass, QueryInfo<ENTITY> queryInfo,
//                                                             Permission permission, String sid) {
//        return query(entityClass, queryInfo, permission, Arrays.asList(sid));
//    }

//    @Override
//    @Transactional(readOnly = true)
//    public <ENTITY extends IdEntity> QueryInfo<ENTITY> query(Class<ENTITY> entityClass, QueryInfo<ENTITY> queryInfo,
//                                                             Permission permission, List<String> sids) {
//        String fieldName = getAuthFieldName(permission);
//        String hql = StrUtil.format("select objectIdIdentity from AclTaskEntry where sid in (:sids) and {}=1", fieldName);
//
//        String entityName = entityClass.getCanonicalName();
//        String whereHql = "1 = 1" + AclUtil.buildWhereHql(queryInfo);
//        StringBuilder sb = new StringBuilder();
//        if (StrUtil.isNotBlank(queryInfo.getSelectionHql())) {
//            sb.append("select ");
//            sb.append(queryInfo.getSelectionHql());
//        }
//        sb.append(" from ");
//        sb.append(entityName);
//        sb.append(" o where ");
//        sb.append(whereHql);
//        sb.append(" and exists (");
//        sb.append(hql);
//        sb.append(")");
//        sb.append(AclUtil.buildOrderby(queryInfo));
//        String queryHql = sb.toString();
//        queryInfo.addQueryParams("sids", sids);
//        Query query = this.dao.getSession().createQuery(queryHql);
//        // 设置查询参数
//        AclUtil.setQueryParams(query, queryInfo.getQueryParams());
//        // 分页信息
//        Page<ENTITY> page = queryInfo.getPage();
//        if (page.getPageSize() != -1) {
//            query.setFirstResult(page.getFirst() - 1);
//            query.setMaxResults(page.getPageSize());
//        }
////        query.setResultTransformer(QueryItemResultTransformer.INSTANCE);
//        page.setResult(query.list());
//        // 计算总数
//        if (page.isAutoCount()) {
//            query = this.dao.getSession().createQuery(
//                    "select count(*) from " + entityName + " o where " + whereHql + " and exists (" + hql + ")");
//            AclUtil.setQueryParams(query, queryInfo.getQueryParams());
//            page.setTotalCount((Long) query.uniqueResult());
//        }
//        return queryInfo;
//    }

    /**
     * 查找并更新列表
     *
     * @param aclTaskEntries 现有的任务实例列表
     * @param entityUuid     实例uuid
     * @param mask           权限码
     * @param sid            身份标识id
     * @return
     */
    private void findAndUpdateAclTaskEntry(List<AclTaskEntry> aclTaskEntries, String entityUuid, int mask, String sid) {
        Optional<AclTaskEntry> findResult = aclTaskEntries.stream().filter(entry -> entry.getObjectIdIdentity().equals(entityUuid) &&
                entry.getSid().equals(sid)).findFirst();
        if (findResult.isPresent()) {
            AclTaskEntry aclTaskEntry = findResult.get();
            setAclTaskEntry(sid, entityUuid, mask, aclTaskEntry);
        } else {
            //未找到，就添加
            AclTaskEntry aclTaskEntry = setAclTaskEntry(sid, entityUuid, mask, null);
            aclTaskEntries.add(aclTaskEntry);
        }
    }

    /**
     * 合并权限，将源权限内容合并到目标权限
     *
     * @param source 源权限
     * @param dest   目标权限
     * @return
     */
    private AclTaskEntry mergeAclTaskEntry(AclTaskEntry source, AclTaskEntry dest) {
        if (source.getReadAuth()) {
            dest.setReadAuth(true);
        }
        if (source.getTodoAuth()) {
            dest.setTodoAuth(true);
        }
        if (source.getDoneAuth()) {
            dest.setDoneAuth(true);
        }
        if (source.getAttentionAuth()) {
            dest.setAttentionAuth(true);
        }
        if (source.getUnReadAuth()) {
            dest.setUnReadAuth(true);
        }
        if (source.getFlagReadAuth()) {
            dest.setFlagReadAuth(true);
        }
        if (source.getSuperviseAuth()) {
            dest.setSuperviseAuth(true);
        }
        if (source.getMonitorAuth()) {
            dest.setMonitorAuth(true);
        }
        return dest;
    }

    @Override
    public void convertAclEntry(Consumer<String> messageSend) {
        Map<String, Object> params = Maps.newHashMap();
        String hql = GET_ACL_ENTRY_COUNT;
        Query query = this.dao.getSession().createSQLQuery(hql);
        Long count = ((BigDecimal) query.uniqueResult()).longValue();
        String message = "总数：{}，成功数：{}，失败数：{},耗时：{}";
        messageSend.accept("总数：" + count);
        int pageSize = 20;
        PagingInfo pageInfo = new PagingInfo();
        pageInfo.setPageSize(pageSize);
        pageInfo.setTotalCount(count);
        long totalPages = pageInfo.getTotalPages();
        StatelessSession statelessSession = this.dao.getSession().getSessionFactory().openStatelessSession();
        String entityUuid;
        int mask;
        String sid;
        long successCount = 0;
        long failCount = 0;
        long startTime = System.currentTimeMillis();
        long endTime;
        List<AclTaskEntry> aclTaskEntries = new ArrayList<>(pageSize);
        try {
            for (long i = 0; i < totalPages; i++) {
                pageInfo.setCurrentPage((int) i);
                List<QueryItem> aclEntries = aclEntryService.listQueryItemBySQL(GET_ACL_ENTRY, params, pageInfo);
                Transaction transaction = null;
                try {
                    transaction = statelessSession.beginTransaction();
                    for (QueryItem aclEntry : aclEntries) {
                        entityUuid = aclEntry.getString("objectIdIdentity");
                        sid = aclEntry.getString("sid");
                        if (StrUtil.isBlank(sid)) {
                            continue;
                        }
                        mask = aclEntry.getInt("mask");
                        findAndUpdateAclTaskEntry(aclTaskEntries, entityUuid, mask, sid);
                    }
                    //更新到数据库
                    for (AclTaskEntry entry : aclTaskEntries) {
                        AclTaskEntry aclTaskEntry = getAclTaskEntry(entry.getObjectIdIdentity(), entry.getSid());
                        if (aclTaskEntry == null) {
                            statelessSession.insert(entry);
                        } else {
                            aclTaskEntry = mergeAclTaskEntry(entry, aclTaskEntry);
                            statelessSession.update(aclTaskEntry);
                        }
                    }
                    transaction.commit();
                    aclTaskEntries.clear();
                    successCount += aclEntries.size();
                    endTime = (System.currentTimeMillis() - startTime) / 1000;
                    messageSend.accept(StrUtil.format(message, count, successCount, failCount, endTime));

                } catch (Exception e) {
                    if (transaction != null && transaction.isActive()) {
                        transaction.rollback();
                    }
                    failCount += aclEntries.size();
                    endTime = (System.currentTimeMillis() - startTime) / 1000;
                    messageSend.accept(StrUtil.format(message, count, successCount, failCount, endTime));
                    log.error("迁移数据出错：" + e.getMessage(), e);
                    messageSend.accept("迁移数据出错：" + e.getMessage());
                }
            }
            messageSend.accept("全部迁移完成！");
        } finally {
            statelessSession.close();
        }
    }

    private AclTaskEntry setAclTaskEntry(String entityUuid, int mask, String sid) {
        AclTaskEntry aclTaskEntry = getAclTaskEntry(entityUuid, sid);
        aclTaskEntry = setAclTaskEntry(sid, entityUuid, mask, aclTaskEntry);
        return aclTaskEntry;
    }

    @Override
    @Transactional
    public void addPermission(String entityUuid, Permission permission, String sid) {
        AclTaskEntry aclTaskEntry = setAclTaskEntry(entityUuid, permission, sid);
        aclTaskEntryService.save(aclTaskEntry);

        if (AclPermission.TODO.equals(permission)) {
            removeDoneMarker(entityUuid, sid);
        }
    }

    @Override
    @Transactional
    public void addPermission(String entityUuid, Permission permission, String sid, String doneUserId) {
        AclTaskEntry aclTaskEntry = setAclTaskEntry(entityUuid, permission, sid);
        aclTaskEntryService.save(aclTaskEntry);

        if (AclPermission.TODO.equals(permission)) {
            removeDoneMarker(entityUuid, doneUserId);
        }
    }

    private AclTaskEntry setAclTaskEntry(String entityUuid, Permission permission, String sid) {
        AclTaskEntry aclTaskEntry = getAclTaskEntry(entityUuid, sid);
        aclTaskEntry = setAclTaskEntry(sid, entityUuid, permission, aclTaskEntry);
        return aclTaskEntry;
    }

    private AclTaskEntry getAclTaskEntry(String entityUuid, String sid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("sid", sid);
        params.put("objectIdIdentity", entityUuid);
        AclTaskEntry aclTaskEntry = null;
        List<AclTaskEntry> aclTaskEntries = aclTaskEntryService.listByHQL(GET_PERMISSION, params);
        if (CollUtil.isNotEmpty(aclTaskEntries)) {
            aclTaskEntry = aclTaskEntries.get(0);
        }
        return aclTaskEntry;
    }

    private List<AclTaskEntry> getAclTaskEntryBySids(String entityUuid, List<String> sids) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("sids", sids);
        params.put("objectIdIdentity", entityUuid);
        List<AclTaskEntry> aclTaskEntries = aclTaskEntryService.listByHQL(GET_PERMISSION_BY_SIDS, params);
        return aclTaskEntries;
    }

    private List<AclTaskEntry> getDoneAclTaskEntryBySids(String entityUuid, List<String> sids) {
        String hql = "from  AclTaskEntry t WHERE t.todoAuth = true and t.sid in ( :sids) and t.objectIdIdentity=:objectIdIdentity and exists(select 1 from AclTaskDoneMarker d where d.userId in(:sids) and d.aclTaskUuid = t.uuid)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("sids", sids);
        params.put("objectIdIdentity", entityUuid);
        List<AclTaskEntry> aclTaskEntries = aclTaskEntryService.listByHQL(hql, params);
        return aclTaskEntries;
    }

    private List<AclTaskEntry> getAclTaskEntryByPermissionAndSids(String entityUuid, Permission permission, List<String> sids) {
        String hql = GET_PERMISSION_BY_SIDS;
        String fieldName = getAuthFieldName(permission);
        hql += " and " + fieldName + " = true";
        Map<String, Object> params = Maps.newHashMap();
        params.put("sids", sids);
        params.put("objectIdIdentity", entityUuid);
        List<AclTaskEntry> aclTaskEntries = aclTaskEntryService.listByHQL(hql, params);
        return aclTaskEntries;
    }

    private List<String> getAclTaskEntryUuidByPermissionAndSids(String entityUuid, Permission permission, List<String> sids) {
        String hql = "select uuid " + GET_PERMISSION_BY_SIDS;
        String fieldName = getAuthFieldName(permission);
        hql += " and " + fieldName + " = true";
        Map<String, Object> params = Maps.newHashMap();
        params.put("sids", sids);
        params.put("objectIdIdentity", entityUuid);
        List<String> aclTaskEntryUuids = aclTaskEntryService.listCharSequenceByHQL(hql, params);
        return aclTaskEntryUuids;
    }

    private List<String> getAclTaskEntryUuidByPermission(String entityUuid, Permission permission) {
        String hql = "select uuid " + GET_SIDS;
        String fieldName = getAuthFieldName(permission);
        hql += " and " + fieldName + " = true";
        Map<String, Object> params = Maps.newHashMap();
        params.put("objectIdIdentity", entityUuid);
        List<String> aclTaskEntryUuids = aclTaskEntryService.listCharSequenceByHQL(hql, params);
        return aclTaskEntryUuids;
    }

    @Override
    public List<Permission> getAllPermissionBySids(String entityUuid, List<String> sids) {
        // 查询hql
        List<AclTaskEntry> aclTaskEntries = getAclTaskEntryBySids(entityUuid, sids);
        List<AclTaskEntry> doneAclTaskEntries = getDoneAclTaskEntryBySids(entityUuid, sids);
        List<Permission> permissions = new ArrayList<Permission>();
        for (AclTaskEntry aclTaskEntry : aclTaskEntries) {
            setPermission(aclTaskEntry, permissions, doneAclTaskEntries);
        }
        List<Permission> result = permissions.stream().distinct().collect(Collectors.toList());
        return result;
    }

    @Override
    public List<Permission> getPermission(String entityUuid, String sid) {
        // 查询hql
        AclTaskEntry aclTaskEntry = getAclTaskEntry(entityUuid, sid);
        List<AclTaskEntry> doneAclTaskEntry = getDoneAclTaskEntryBySids(entityUuid, Lists.newArrayList(sid));
        List<Permission> permissions = new ArrayList<Permission>();
        setPermission(aclTaskEntry, permissions, doneAclTaskEntry);
        return permissions;
    }

    private void setPermission(AclTaskEntry aclTaskEntry, List<Permission> permissions, List<AclTaskEntry> doneAclTaskEntries) {
        if (aclTaskEntry != null) {
            if (aclTaskEntry.getReadAuth()) {
                permissions.add(new AclPermission(AclPermission.READ.getMask()));
            }
            if (aclTaskEntry.getTodoAuth()) {
                if (IdPrefix.startsUser(aclTaskEntry.getSid()) || CollectionUtils.isEmpty(doneAclTaskEntries)) {
                    permissions.add(new AclPermission(AclPermission.TODO.getMask()));
                } else {
                    boolean hasDone = doneAclTaskEntries.stream().filter(entry -> StringUtils.equals(aclTaskEntry.getUuid(), entry.getUuid())).findFirst().isPresent();
                    if (BooleanUtils.isNotTrue(hasDone)) {
                        permissions.add(new AclPermission(AclPermission.TODO.getMask()));
                    }
                }
            }
            if (aclTaskEntry.getDoneAuth()) {
                permissions.add(new AclPermission(AclPermission.DONE.getMask()));
            }
            if (aclTaskEntry.getAttentionAuth()) {
                permissions.add(new AclPermission(AclPermission.ATTENTION.getMask()));
            }
            if (aclTaskEntry.getUnReadAuth()) {
                permissions.add(new AclPermission(AclPermission.UNREAD.getMask()));
            }
            if (aclTaskEntry.getFlagReadAuth()) {
                permissions.add(new AclPermission(AclPermission.FLAG_READ.getMask()));
            }
            if (aclTaskEntry.getSuperviseAuth()) {
                permissions.add(new AclPermission(AclPermission.SUPERVISE.getMask()));
            }
            if (aclTaskEntry.getMonitorAuth()) {
                permissions.add(new AclPermission(AclPermission.MONITOR.getMask()));
            }
        }
        if (CollectionUtils.isNotEmpty(doneAclTaskEntries) && !permissions.contains(AclPermission.DONE.getMask())) {
            permissions.add(new AclPermission(AclPermission.DONE.getMask()));
        }
    }

    @Override
    public boolean isGranted(String objectIdIdentity, Integer[] masks, List<String> sids) {
        String[] authFields = new String[masks.length];
        for (int i = 0; i < masks.length; i++) {
            authFields[i] = getAuthFieldName(masks[i]) + " = true";
        }
        return isGranted(objectIdIdentity, sids, authFields);
    }

    @Override
    public boolean isGranted(String objectIdIdentity, List<Permission> permissions, List<String> sids) {
        String[] authFields = new String[permissions.size()];
        for (int i = 0; i < permissions.size(); i++) {
            authFields[i] = getAuthFieldName(permissions.get(i)) + " = true";
        }
        return isGranted(objectIdIdentity, sids, authFields);
    }

    @Override
    public String getAuthWhere(Collection<Permission> permissions) {
        String[] authFields = new String[permissions.size()];
        String where = "";
        List<Permission> list = new ArrayList<>(permissions);

        for (int i = 0; i < list.size(); i++) {
            authFields[i] = getAuthTableFieldName(list.get(i)) + " = 1";
        }
        if (ArrayUtil.isNotEmpty(authFields)) {
            String authFieldWhere = StrUtil.join(" or ", authFields);
            where = " and (" + authFieldWhere + ")";
        }
        // 已阅人员排除大于用户权限粒度的的未阅数据
        if (permissions.contains(AclPermission.UNREAD) && !permissions.contains(AclPermission.READ)) {
            where += " and not exists (select 1 from acl_task_read_marker m where m.user_id = :currentUserId and m.acl_task_uuid = a1.uuid)";
        }
        // 已办人员排除大于用户权限粒度的的未办数据
        if (permissions.contains(AclPermission.TODO) && !permissions.contains(AclPermission.DONE)) {
            where += " and not exists (select 1 from acl_task_done_marker m where m.user_id = :currentUserId and m.acl_task_uuid = a1.uuid)";
        }
        return where;
    }

    @Override
    public boolean isGranted(String objectIdIdentity, List<String> sids) {
        Permission[] masks = new Permission[]{
                AclPermission.READ, AclPermission.TODO, AclPermission.DONE, AclPermission.ATTENTION, AclPermission.UNREAD,
                AclPermission.FLAG_READ, AclPermission.SUPERVISE, AclPermission.MONITOR
        };
        String[] authFields = new String[masks.length];
        for (int i = 0; i < masks.length; i++) {
            authFields[i] = getAuthFieldName(masks[i]) + " = true";
        }
        return isGranted(objectIdIdentity, sids, authFields);
    }

    private boolean isGranted(String objectIdIdentity, List<String> sids, String[] authFields) {
        String hql = HAS_ANY_PERMISSION;
        if (ArrayUtil.isNotEmpty(authFields)) {
            String authFieldWhere = StrUtil.join(" or ", authFields);
            hql += " and (" + authFieldWhere + ")";
            if (StringUtils.contains(authFieldWhere, "todoAuth = true")) {
                hql += " and not exists(select 1 from AclTaskDoneMarker t where t.userId in(:sids) and t.aclTaskUuid = a.uuid)";
            }
        }
        Query query = this.dao.getSession().createQuery(hql);
        query.setParameterList("sids", sids);
        query.setParameter("objectIdIdentity", objectIdIdentity);
        Long count = (Long) query.uniqueResult();
        return count > 0;

    }

    @Override
    public boolean hasPermission(String entityUuid, Permission permission, String sid) {
        String fieldName = getAuthFieldName(permission);
        String hql = StrUtil.format(HAS_PERMISSION, fieldName);
        Query query = this.dao.getSession().createQuery(hql);
        query.setParameter("sid", sid);
        query.setParameter("objectIdIdentity", entityUuid);
        query.setParameter("authValue", true);
        Long count = (Long) query.uniqueResult();
        return count > 0;
    }

    @Override
    public boolean hasPermission(String entityUuid, Permission permission) {
        String fieldName = getAuthFieldName(permission);
        String hql = StrUtil.format(HAS_OBJECT_PERMISSION, fieldName);
        Query query = this.dao.getSession().createQuery(hql);
        query.setParameter("objectIdIdentity", entityUuid);
        query.setParameter("authValue", true);
        Long count = (Long) query.uniqueResult();
        return count > 0;
    }

    private AclTaskEntry setAclTaskEntry(String sid, String entityUuid, Permission permission, AclTaskEntry aclTaskEntry) {
        if (aclTaskEntry == null) {
            aclTaskEntry = new AclTaskEntry();
        }
        aclTaskEntry.setSid(sid);
        aclTaskEntry.setObjectIdIdentity(entityUuid);
        if (AclPermission.READ.equals(permission)) {
            aclTaskEntry.setReadAuth(true);
        } else if (AclPermission.TODO.equals(permission)) {
            aclTaskEntry.setTodoAuth(true);
        } else if (AclPermission.DONE.equals(permission)) {
            aclTaskEntry.setDoneAuth(true);
        } else if (AclPermission.ATTENTION.equals(permission)) {
            aclTaskEntry.setAttentionAuth(true);
        } else if (AclPermission.UNREAD.equals(permission)) {
            aclTaskEntry.setUnReadAuth(true);
        } else if (AclPermission.FLAG_READ.equals(permission)) {
            aclTaskEntry.setFlagReadAuth(true);
        } else if (AclPermission.SUPERVISE.equals(permission)) {
            aclTaskEntry.setSuperviseAuth(true);
        } else if (AclPermission.MONITOR.equals(permission)) {
            aclTaskEntry.setMonitorAuth(true);
        }
        return aclTaskEntry;
    }

    private AclTaskEntry setAclTaskEntry(String sid, String entityUuid, int mask, AclTaskEntry aclTaskEntry) {
        if (aclTaskEntry == null) {
            aclTaskEntry = new AclTaskEntry();
        }
        aclTaskEntry.setSid(sid);
        aclTaskEntry.setObjectIdIdentity(entityUuid);
        if (AclPermission.READ.getMask() == mask) {
            aclTaskEntry.setReadAuth(true);
        } else if (AclPermission.TODO.getMask() == mask) {
            aclTaskEntry.setTodoAuth(true);
        } else if (AclPermission.DONE.getMask() == mask) {
            aclTaskEntry.setDoneAuth(true);
        } else if (AclPermission.ATTENTION.getMask() == mask) {
            aclTaskEntry.setAttentionAuth(true);
        } else if (AclPermission.UNREAD.getMask() == mask) {
            aclTaskEntry.setUnReadAuth(true);
        } else if (AclPermission.FLAG_READ.getMask() == mask) {
            aclTaskEntry.setFlagReadAuth(true);
        } else if (AclPermission.SUPERVISE.getMask() == mask) {
            aclTaskEntry.setSuperviseAuth(true);
        } else if (AclPermission.MONITOR.getMask() == mask) {
            aclTaskEntry.setMonitorAuth(true);
        }
        return aclTaskEntry;
    }


    @Override
    @Transactional
    public void removePermission(String entityUuid) {
        removeAcl(entityUuid);
    }

    @Override
    @Transactional
    public void removePermission(String entityUuid, Permission permission, String sid) {
        if (AclPermission.TODO.equals(permission)) {
            removeDoneMarker(entityUuid, sid);
        }

        String fieldName = getAuthFieldName(permission);
        String hql = StrUtil.format("UPDATE AclTaskEntry  SET {} = 0 WHERE sid = :sid and objectIdIdentity=:objectIdIdentity", fieldName);
        Map<String, Object> params = Maps.newHashMap();
        params.put("sid", sid);
        params.put("objectIdIdentity", entityUuid);
        aclTaskEntryService.updateByHQL(hql, params);
    }

    /**
     * @param entityUuid
     * @param sid
     */
    private void removeDoneMarker(String entityUuid, String sid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("sid", sid);
        params.put("objectIdIdentity", entityUuid);
        String hql = "delete from AclTaskDoneMarker where aclTaskUuid in(select uuid from AclTaskEntry WHERE sid = :sid and objectIdIdentity=:objectIdIdentity and todoAuth = true)";
        if (IdPrefix.startsUser(sid)) {
            hql = "delete from AclTaskDoneMarker where userId = :sid and aclTaskUuid in(select uuid from AclTaskEntry WHERE sid = :sid and objectIdIdentity=:objectIdIdentity and todoAuth = true)";
        }
        aclTaskDoneMarkerDao.deleteByHQL(hql, params);
    }

    /**
     * @param entityUuid
     * @param sids
     */
    private void removeDoneMarker(String entityUuid, Collection<String> sids) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("sids", sids);
        params.put("objectIdIdentity", entityUuid);
        List<String> orgSids = sids.stream().filter(sid -> !IdPrefix.startsUser(sid)).collect(Collectors.toList());
        List<String> userSids = sids.stream().filter(sid -> IdPrefix.startsUser(sid)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(orgSids)) {
            params.put("orgSids", orgSids);
            String hql = "delete from AclTaskDoneMarker where aclTaskUuid in(select uuid from AclTaskEntry WHERE sid in(:orgSids) and objectIdIdentity=:objectIdIdentity and todoAuth = true)";
            aclTaskDoneMarkerDao.deleteByHQL(hql, params);
        }
        if (CollectionUtils.isNotEmpty(userSids)) {
            params.put("userSids", userSids);
            String hql = "delete from AclTaskDoneMarker where userId in(:userSids) and aclTaskUuid in(select uuid from AclTaskEntry WHERE sid in(:userSids) and objectIdIdentity=:objectIdIdentity and todoAuth = true)";
            aclTaskDoneMarkerDao.deleteByHQL(hql, params);
        }
    }

    /**
     * @param entityUuid
     */
    private void removeDoneMarker(String entityUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("objectIdIdentity", entityUuid);
        String hql = "delete from AclTaskDoneMarker where aclTaskUuid in(select uuid from AclTaskEntry WHERE objectIdIdentity=:objectIdIdentity and todoAuth = true)";
        aclTaskDoneMarkerDao.deleteByHQL(hql, params);
    }

    @Override
    @Transactional
    public void removePermission(String entityUuid, List<Permission> permissions, String sid) {
        if (permissions.contains(AclPermission.TODO)) {
            removeDoneMarker(entityUuid, sid);
        }

        List<String> fieldNames = Lists.newArrayList();
        permissions.forEach(permission -> {
            fieldNames.add(getAuthFieldName(permission) + " = 0");
        });
        String hql = StrUtil.format("UPDATE AclTaskEntry  SET {} WHERE sid = :sid and objectIdIdentity=:objectIdIdentity", StringUtils.join(fieldNames, ", "));
        Map<String, Object> params = Maps.newHashMap();
        params.put("sid", sid);
        params.put("objectIdIdentity", entityUuid);
        aclTaskEntryService.updateByHQL(hql, params);
    }

    @Override
    @Transactional
    public void removePermission(String entityUuid, Permission permission) {
        if (AclPermission.TODO.equals(permission)) {
            removeDoneMarker(entityUuid);
        }

        String fieldName = getAuthFieldName(permission);
        String hql = StrUtil.format("UPDATE AclTaskEntry  SET {} = 0 WHERE  objectIdIdentity=:objectIdIdentity", fieldName);
        Map<String, Object> params = Maps.newHashMap();
        params.put("objectIdIdentity", entityUuid);
        aclTaskEntryService.updateByHQL(hql, params);
    }

    @Override
    @Transactional
    public void removeUserDoneMarker(String entityUuid, String sid, String userId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("sid", sid);
        params.put("userId", userId);
        params.put("objectIdIdentity", entityUuid);
        String hql = "delete from AclTaskDoneMarker where userId = :userId and aclTaskUuid in(select uuid from AclTaskEntry WHERE sid = :sid and objectIdIdentity=:objectIdIdentity and todoAuth = true)";
        aclTaskDoneMarkerDao.deleteByHQL(hql, params);
    }

    @Override
    @Transactional
    public void removeUserDoneMarker(String entityUuid, String userId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", userId);
        params.put("objectIdIdentity", entityUuid);
        String hql = "delete from AclTaskDoneMarker where userId = :userId and aclTaskUuid in(select uuid from AclTaskEntry WHERE objectIdIdentity=:objectIdIdentity and todoAuth = true)";
        aclTaskDoneMarkerDao.deleteByHQL(hql, params);
    }

    /**
     * 获取权限字段名
     *
     * @param permission 权限类别
     * @return
     */
    private String getAuthFieldName(Permission permission) {
        String fieldName = "";
        if (AclPermission.READ.equals(permission)) {
            fieldName = "readAuth";
        } else if (AclPermission.TODO.equals(permission)) {
            fieldName = "todoAuth";
        } else if (AclPermission.DONE.equals(permission)) {
            fieldName = "doneAuth";
        } else if (AclPermission.ATTENTION.equals(permission)) {
            fieldName = "attentionAuth";
        } else if (AclPermission.UNREAD.equals(permission)) {
            fieldName = "unReadAuth";
        } else if (AclPermission.FLAG_READ.equals(permission)) {
            fieldName = "flagReadAuth";
        } else if (AclPermission.SUPERVISE.equals(permission)) {
            fieldName = "superviseAuth";
        } else if (AclPermission.MONITOR.equals(permission)) {
            fieldName = "monitorAuth";
        }
        return fieldName;
    }

    /**
     * 获取权限表字段名
     *
     * @param permission 权限类别
     * @return
     */
    private String getAuthTableFieldName(Permission permission) {
        String fieldName = "";
        if (AclPermission.READ.equals(permission)) {
            fieldName = "read_auth";
        } else if (AclPermission.TODO.equals(permission)) {
            fieldName = "todo_auth";
        } else if (AclPermission.DONE.equals(permission)) {
            fieldName = "done_auth";
        } else if (AclPermission.ATTENTION.equals(permission)) {
            fieldName = "attention_auth";
        } else if (AclPermission.UNREAD.equals(permission)) {
            fieldName = "unread_auth";
        } else if (AclPermission.FLAG_READ.equals(permission)) {
            fieldName = "flag_read_auth";
        } else if (AclPermission.SUPERVISE.equals(permission)) {
            fieldName = "supervise_auth";
        } else if (AclPermission.MONITOR.equals(permission)) {
            fieldName = "monitor_auth";
        }
        return fieldName;
    }

    /**
     * 获取权限字段名
     *
     * @param mask 权限码
     * @return
     */
    private String getAuthFieldName(Integer mask) {
        String fieldName = "";
        if (AclPermission.READ.getMask() == mask) {
            fieldName = "readAuth";
        } else if (AclPermission.TODO.getMask() == mask) {
            fieldName = "todoAuth";
        } else if (AclPermission.DONE.getMask() == mask) {
            fieldName = "doneAuth";
        } else if (AclPermission.ATTENTION.getMask() == mask) {
            fieldName = "attentionAuth";
        } else if (AclPermission.UNREAD.getMask() == mask) {
            fieldName = "unReadAuth";
        } else if (AclPermission.FLAG_READ.getMask() == mask) {
            fieldName = "flagReadAuth";
        } else if (AclPermission.SUPERVISE.getMask() == mask) {
            fieldName = "superviseAuth";
        } else if (AclPermission.MONITOR.getMask() == mask) {
            fieldName = "monitorAuth";
        }
        return fieldName;
    }

    @Override
    @Transactional
    public void savePermission(Map<String, AclTaskEntry> map) {
        List<AclTaskEntry> aclTaskEntries = map.values()
                .stream()
                .collect(Collectors.toList());
        aclTaskEntryService.saveAll(aclTaskEntries);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AclTaskEntry> getSid(String entityUuid) {
        return getSid(entityUuid, null);
    }

    @Override
    @Transactional
    public void removeAcl(String entityUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("objectIdIdentity", entityUuid);
        String hql1 = "delete from AclTaskReadMarker where aclTaskUuid in(select uuid from AclTaskEntry WHERE objectIdIdentity=:objectIdIdentity)";
        aclTaskReadMarkerDao.deleteByHQL(hql1, params);

        String hql2 = "delete from AclTaskDoneMarker where aclTaskUuid in(select uuid from AclTaskEntry WHERE objectIdIdentity=:objectIdIdentity)";
        aclTaskDoneMarkerDao.deleteByHQL(hql2, params);

        aclTaskEntryService.deleteByHQL(REMOVE_PERMISSION, params);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AclTaskEntry> getSid(String entityUuid, Permission permission) {
        // 查询hql
        String hql = permission == null ? GET_SIDS : GET_SIDS_WITH_PERMISSION;
        if (permission != null) {
            String fieldName = getAuthFieldName(permission);
            hql = StrUtil.format(hql, fieldName);
        }
        Query query = this.dao.getSession().createQuery(hql);
        query.setParameter("objectIdIdentity", entityUuid);
        if (permission != null) {
            query.setParameter("authValue", true);
        }
        return query.list();
    }

}
