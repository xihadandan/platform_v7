/*
 * @(#)8/1/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.org.provider;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.context.util.reflection.ServiceInvokeUtils;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.service.TaskInstanceService;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.multi.org.support.dataprovider.OrgSelectProvider;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description: 组织选择框——环节已办人
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 8/1/23.1	zhulh		8/1/23		Create
 * </pre>
 * @date 8/1/23
 */
@Component
public class TaskDoneUsersSelectProvider implements OrgSelectProvider {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskInstanceService taskInstanceService;

    @Autowired
    private WorkflowOrgService workflowOrgService;

    @Autowired
    private OrgFacadeService orgFacadeService;

    /**
     * 组织类型
     *
     * @return
     */
    @Override
    public String type() {
        return "TaskDoneUsers";
    }

    /**
     * 拉取数据
     *
     * @param params@return
     */
    @Override
    public List<Node> fetch(Params params) {
        // 业务组织
        String bizOrgId = (String) params.get("bizOrgId");
        if (StringUtils.isNotBlank(bizOrgId)) {
            Long bizOrgUuid = workflowOrgService.getBizOrgUuidByBizOrgId(bizOrgId);
            return (List<Node>) ServiceInvokeUtils.invoke("bizOrgElementService", new Class[]{Long.class, Params.class}, bizOrgUuid, params);
        }

        List<String> userIds = getUserIds(params);
        List<String> orgEleIds = getOrgEleIds(params);
        if (CollectionUtils.isEmpty(userIds) && CollectionUtils.isEmpty(orgEleIds)) {
            return Collections.emptyList();
        }

        return fetchTaskUsers(params, userIds);
    }

    private List<Node> fetchTaskUsers(Params params, List<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyList();
        }

        String orgVersionId = (String) params.get("orgVersionId");
        String orgUuid = (String) params.get("orgUuid");
        if (StringUtils.isBlank(orgVersionId) && StringUtils.isNotBlank(orgUuid)) {
            orgVersionId = orgFacadeService.getOrgVersionByOrgUuid(Long.valueOf(orgUuid)).getId();
        }

//        String[] orgVersionIds = getOrgVersionIds(params);
//        Map<String, UserDto> userDtoMap = workflowOrgService.getUsersByIds(userIds, orgVersionIds);

        StringBuilder sql = new StringBuilder(getElementQuerySql());
        sql.append(" ORDER BY O.PARENT_UUID DESC , O.SEQ ASC ");
        Map<String, Object> sqlParams = Maps.newHashMap();
        sqlParams.put("orgVersionId", orgVersionId);
        sqlParams.put("locale", LocaleContextHolder.getLocale().toString());
        List<QueryItem> queryItems = taskInstanceService.listQueryItemBySQL(sql.toString(), sqlParams, null);
        if (CollectionUtils.isEmpty(queryItems)) {
            return Collections.emptyList();
        }

        Map<String, Node> nodeMap = Maps.newLinkedHashMap();
        List<Node> root = buildOrgTree(queryItems, nodeMap, orgVersionId);

        // 查询节点下的用户
        List<QueryItem> userQueryItems = Lists.newArrayList();
        String userSql = this.getUserQuerySql();
        ListUtils.handleSubList(userIds, 1000, list -> {
            sqlParams.put("userIds", list);
            userQueryItems.addAll(taskInstanceService.getDao().listQueryItemBySQL(userSql, sqlParams, null));
        });
        userQueryItems.sort(Comparator
                .comparing(new Function<QueryItem, String>() {
                    @Override
                    public String apply(QueryItem queryItem) {
                        return queryItem.getString("userNo");
                    }
                }, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(new Function<QueryItem, String>() {
                    @Override
                    public String apply(QueryItem queryItem) {
                        return queryItem.getString("pinYin");
                    }
                }, Comparator.nullsFirst(Comparator.naturalOrder()))
        );
        for (QueryItem item : userQueryItems) {
            Node parent = nodeMap.get(item.getString("orgElementId"));
            if (parent == null) {
                continue;
            }
            Node node = new Node();
            item.put("type", "user");
            node.setKey(item.getString("userId"));
            node.setTitle(StringUtils.defaultIfBlank(item.getString("iUserName"), item.getString("userName")));
            node.setShortTitle(node.getTitle());
            node.setVersion(orgVersionId);
            node.setType("user");
            node.setData(item);
            node.setIsLeaf(true);
            node.setParentKey(parent.getKey());
            if (CollectionUtils.isEmpty(parent.getChildren())) {
                parent.setChildren(Lists.newArrayList());
                parent.setIsLeaf(false);
            }
            node.setTitlePath(StringUtils.join(new String[]{parent.getTitlePath(), node.getTitle()}, Separator.SLASH.getValue()));
            node.setKeyPath(StringUtils.join(new String[]{parent.getKeyPath(), node.getKey()}, Separator.SLASH.getValue()));
            parent.getChildren().add(node);
        }

        // 删除没有包含子节点的节点
        for (Node node : root) {
            removeEmptyChildNode(node, userIds);
        }

        return root;
    }

    private String getUserQuerySql() {
        StringBuilder userSql = new StringBuilder("SELECT U.LOGIN_NAME,U.USER_ID,U.AVATAR,U.GENDER,U.USER_NAME, U.PIN_YIN, U.USER_NO,  RELA.ORG_ELEMENT_ID ");
        if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
            userSql.append(", I.USER_NAME AS I_USER_NAME ");
        }
        userSql.append(" FROM USER_INFO U INNER JOIN ORG_USER RELA ON RELA.USER_ID=U.USER_ID ");
        if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
            userSql.append(" LEFT JOIN USER_NAME_I18N I ON I.USER_ID =U.USER_ID AND I.LOCALE=:locale ");
        }
        userSql.append(" WHERE RELA.USER_ID in (:userIds) AND RELA.ORG_VERSION_UUID=(SELECT UUID FROM ORG_VERSION V WHERE V.ID = :orgVersionId)  ORDER BY U.USER_NO ASC ");
        return userSql.toString();
    }

    private String getElementQuerySql() {
        StringBuilder sql = new StringBuilder("SELECT O.UUID,O.ORG_VERSION_ID,O.ORG_VERSION_UUID,O.PARENT_UUID,O.ID,P.ID_PATH,O.NAME,O.SHORT_NAME,P.CN_PATH,P.PIN_YIN_PATH,O.SEQ,P.LEAF,O.TYPE");
        if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
            sql.append(",I.CONTENT AS I_NAME ,S.CONTENT AS S_NAME ");
        }
        sql.append(" FROM ORG_ELEMENT O INNER JOIN ORG_ELEMENT_PATH P ON O.ORG_VERSION_UUID = P.ORG_VERSION_UUID AND P.ORG_ELEMENT_ID = O.ID ");
        if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
            sql.append(" LEFT JOIN ORG_ELEMENT_I18N I ON I.DATA_ID =O.ID AND I.LOCALE=:locale AND I.DATA_CODE ='name' LEFT JOIN ORG_ELEMENT_I18N S ON S.DATA_ID =O.ID AND S.LOCALE=:locale AND S.DATA_CODE ='short_name' ");
        }
        sql.append(" WHERE O.ORG_VERSION_ID = :orgVersionId ");
        return sql.toString();
    }

    /**
     * @param root
     * @param nodeIds
     */
    private void removeEmptyChildNode(Node root, List<String> nodeIds) {
        List<Node> children = root.getChildren();
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        List<Node> removeNodes = Lists.newArrayList();
        for (Node child : children) {
            if (hasContainAny(child, nodeIds)) {
                removeEmptyChildNode(child, nodeIds);
            } else {
                removeNodes.add(child);
            }
        }
        children.removeAll(removeNodes);
    }

    /**
     * @param node
     * @param nodeIds
     * @return
     */
    private boolean hasContainAny(Node node, List<String> nodeIds) {
        if (nodeIds.contains(node.getKey())) {
            return true;
        } else {
            List<Node> children = node.getChildren();
            if (CollectionUtils.isEmpty(children)) {
                return false;
            }
            for (Node child : children) {
                if (hasContainAny(child, nodeIds)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param queryItems
     * @param nodeMap
     * @param orgVersionId
     * @return
     */
    private List<Node> buildOrgTree(List<QueryItem> queryItems, Map<String, Node> nodeMap, String orgVersionId) {
        List<Node> nodes = Lists.newArrayList();
        Map<String, String> uuidIdMap = Maps.newHashMap();
        List<Long> eleUuids = Lists.newArrayList();
        for (QueryItem item : queryItems) {
            Node node = new Node();
            eleUuids.add(item.getLong("uuid"));
            item.put("uuid", item.getLong("uuid").toString());
            String uuid = item.getString("uuid");
            String id = item.getString("id");
            node.setKey(id);
            node.setVersion(orgVersionId);
            node.setTitle(StringUtils.defaultIfBlank(item.getString("iName"), item.getString("name")));
            node.setType(item.getString("type"));
            node.setShortTitle(StringUtils.defaultIfBlank(item.getString("sName"), item.getString("shortName")));
            node.setKeyPath(item.getString("idPath"));
            node.setTitlePath(item.getString("cnPath"));
            node.setData(item);
            node.setIsLeaf(item.getInt("leaf") == 1);
            node.setParentKey(item.getLong("parentUuid") != null ? item.getLong("parentUuid").toString() : null);
            nodeMap.put(id, node);
            uuidIdMap.put(uuid, id);
            nodes.add(node);
        }
        if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
            Map<Long, String> i18nPathMap = orgFacadeService.getLocaleOrgElementPaths(eleUuids, LocaleContextHolder.getLocale().toString());
            for (Long uuid : eleUuids) {
                if (i18nPathMap.containsKey(uuid)) {
                    Node node = nodeMap.get(uuidIdMap.get(uuid.toString()));
                    node.setTitlePath(i18nPathMap.get(uuid));
                }
            }
        }
        // 构建父子层级
        Set<String> keys = nodeMap.keySet();
        List<Node> root = Lists.newArrayList();
        for (String key : keys) {
            Node node = nodeMap.get(key);
            if (org.apache.commons.lang3.StringUtils.isNotBlank(node.getParentKey())) {
                String pid = uuidIdMap.get(node.getParentKey());
                if (StringUtils.isNotBlank(pid)) {
                    node.setParentKey(pid); // 父级uuid转为父级id
                }
                pid = node.getParentKey();
                Node parent = nodeMap.get(pid);

                if (CollectionUtils.isEmpty(parent.getChildren())) {
                    parent.setChildren(Lists.newArrayList(node));
                    continue;
                }
                parent.getChildren().add(node);
            } else {
                root.add(node);
            }
        }
        return root;
    }

    /**
     * @param params
     * @return
     */
    private List<String> getUserIds(Params params) {
        String taskInstUuid = (String) params.get("taskInstUuid");
        if (StringUtils.isBlank(taskInstUuid)) {
            return Collections.emptyList();
        }
        return taskService.getDoneUserIds(taskInstUuid);
    }

    /**
     * @param params
     * @return
     */
    private List<String> getOrgEleIds(Params params) {
        List<String> userIds = (List<String>) params.get("userIds");
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        return userIds.stream().filter(userId -> !StringUtils.startsWith(userId, IdPrefix.USER.getValue())).collect(Collectors.toList());
    }

    /**
     * @param params
     * @return
     */
    private String[] getOrgVersionIds(Params params) {
        Set<String> versionIds = Sets.newLinkedHashSet();
        String orgVersionId = (String) params.get("orgVersionId");
        if (StringUtils.isNotBlank(orgVersionId)) {
            versionIds.add(orgVersionId);
        }
        List<String> orgVersionIds = (List<String>) params.get("orgVersionIds");
        if (CollectionUtils.isNotEmpty(orgVersionIds)) {
            versionIds.addAll(orgVersionIds);
        }
        return versionIds.toArray(new String[0]);
    }

    @Override
    public List<Node> fetchByKeys(Params params) {
        List<Node> nodes = Lists.newArrayList();
        return nodes;
    }

    @Override
    public PageNode fetchUser(Params params) {
        return null;
    }

    @Override
    public List<Node> fetchByTitles(Params params) {
        return null;
    }
}
