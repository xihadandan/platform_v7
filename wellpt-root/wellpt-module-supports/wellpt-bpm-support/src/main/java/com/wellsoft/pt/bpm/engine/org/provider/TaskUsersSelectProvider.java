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
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description: 组织选择框——环节办理人
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
public class TaskUsersSelectProvider implements OrgSelectProvider {

    @Autowired
    private WorkflowOrgService workflowOrgService;

    @Autowired
    private TaskInstanceService taskInstanceService;

    @Autowired
    private TaskService taskService;

    @Autowired
    @Qualifier("myOrgSelectProvider")
    private OrgSelectProvider myOrgSelectProvider;

    @Autowired
    private OrgFacadeService orgFacadeService;


    /**
     * 组织类型
     *
     * @return
     */
    @Override
    public String type() {
        return "TaskUsers";
    }

    /**
     * 拉取数据
     *
     * @param params@return
     */
    @Override
    public List<Node> fetch(Params params) {
        if (StringUtils.isNotBlank(params.getParentKey())) {
            return myOrgSelectProvider.fetch(params);
        }

        // 业务组织
        String bizOrgId = (String) params.get("bizOrgId");
        String currentBizOrgUuid = (String) params.get("currentBizOrgUuid");
        if (StringUtils.isNotBlank(bizOrgId) || StringUtils.isNotBlank(currentBizOrgUuid)) {
            Long bizOrgUuid = null;
            if (StringUtils.isNotBlank(bizOrgId)) {
                bizOrgUuid = workflowOrgService.getBizOrgUuidByBizOrgId(bizOrgId);
            } else {
                bizOrgUuid = Long.valueOf(currentBizOrgUuid);
            }
            List<Node> bizOrgTree = (List<Node>) ServiceInvokeUtils.invoke("bizOrgElementService.getTreeByBizOrgUuid", new Class[]{Long.class, Params.class}, bizOrgUuid, params);
            List<String> userIds = (List<String>) params.get("userIds");
            if (CollectionUtils.isEmpty(userIds) || CollectionUtils.isEmpty(userIds.stream().filter(userId -> !IdPrefix.startsUser(userId)).collect(Collectors.toList()))) {
                return bizOrgTree;
            }
            return filterBizOrgTree(bizOrgTree, userIds);
        }

        List<String> userIds = getUserIds(params);
        List<String> orgEleIds = getOrgEleIds(params);
        if (CollectionUtils.isEmpty(userIds) && CollectionUtils.isEmpty(orgEleIds)) {
            String taskInstUuid = (String) params.get("taskInstUuid");
            if (StringUtils.isNotBlank(taskInstUuid)) {
                List<String> taskUserIds = taskService.getTodoUserIds(taskInstUuid);
                userIds = taskUserIds.stream().filter(userId -> IdPrefix.startsUser(userId)).collect(Collectors.toList());
                orgEleIds = taskUserIds.stream().filter(userId -> IdPrefix.startsUser(userId)).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(userIds) && CollectionUtils.isEmpty(orgEleIds)) {
                    return Collections.emptyList();
                }
            } else {
                return Collections.emptyList();
            }
        }

        if (CollectionUtils.isEmpty(orgEleIds)) {
            return fetchTaskUsers(params, userIds);
        } else {
            return fetchMixTaskUsers(params, userIds, orgEleIds);
        }
    }

    private List<Node> filterBizOrgTree(List<Node> bizOrgTree, List<String> userIds) {
        List<Node> tree = Lists.newArrayList();
        extractBizOrgTree(bizOrgTree, userIds, tree);
        return tree;
    }

    private void extractBizOrgTree(List<Node> bizOrgTree, List<String> userIds, List<Node> tree) {
        bizOrgTree.forEach(node -> {
            if (userIds.contains(node.getKey())) {
                tree.add(node);
            }
            if (CollectionUtils.isNotEmpty(node.getChildren())) {
                extractBizOrgTree(node.getChildren(), userIds, tree);
            }
        });
    }

    private List<Node> fetchTaskUsers(Params params, List<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        Map<String, String> userJobIdentityMap = getUserJobIdentityMap(params);
        String orgUuid = (String) params.get("orgUuid");
        String orgVersionId = (String) params.get("orgVersionId");
        if (StringUtils.isNotBlank(orgUuid)) {
            String latestOrgVersionId = workflowOrgService.getOrgVersionIdByOrgUuid(Long.valueOf(orgUuid));
            if (StringUtils.isNotBlank(latestOrgVersionId)) {
                orgVersionId = latestOrgVersionId;
            }
        }
        String[] orgVersionIds = getOrgVersionIds(orgVersionId, params);
//        Map<String, UserDto> userDtoMap = workflowOrgService.getUsersByIds(userIds, orgVersionIds);

        StringBuilder sql = new StringBuilder(getElementQuerySql(orgVersionIds));
        sql.append(" ORDER BY O.PARENT_UUID DESC , O.SEQ ASC ");
        Map<String, Object> sqlParams = Maps.newHashMap();
        sqlParams.put("orgVersionId", orgVersionId);
        sqlParams.put("orgVersionIds", orgVersionIds);
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

            // 用户身份路径匹配
            String userJobIdentity = userJobIdentityMap.get(item.getString("userId"));
            if (StringUtils.isNotBlank(userJobIdentity)) {
                List<String> parentKeys = Arrays.asList(StringUtils.split(parent.getKeyPath(), Separator.SLASH.getValue()));
                List<String> identityKeys = Arrays.asList(StringUtils.split(userJobIdentity, Separator.SEMICOLON.getValue()));
                if (CollectionUtils.containsAny(parentKeys, identityKeys)) {
                    node.setTitlePath(StringUtils.join(new String[]{parent.getTitlePath(), node.getTitle()}, Separator.SLASH.getValue()));
                    node.setKeyPath(StringUtils.join(new String[]{parent.getKeyPath(), node.getKey()}, Separator.SLASH.getValue()));
                    parent.getChildren().add(node);
                }
            } else {
                node.setTitlePath(StringUtils.join(new String[]{parent.getTitlePath(), node.getTitle()}, Separator.SLASH.getValue()));
                node.setKeyPath(StringUtils.join(new String[]{parent.getKeyPath(), node.getKey()}, Separator.SLASH.getValue()));
                parent.getChildren().add(node);
            }
        }

        // 删除没有包含子节点的节点
        for (Node node : root) {
            removeEmptyChildNode(node, userIds);
        }

        return root;
    }

    private String getUserQuerySql() {
        StringBuilder userSql = new StringBuilder("SELECT U.LOGIN_NAME,U.USER_ID,U.AVATAR,U.GENDER,U.USER_NAME, U.PIN_YIN, U.USER_NO, RELA.ORG_ELEMENT_ID ");
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

    private String getElementQuerySql(String[] orgVersionIds) {
        StringBuilder sql = new StringBuilder("SELECT O.UUID,O.ORG_VERSION_ID,O.ORG_VERSION_UUID,O.PARENT_UUID,O.ID,P.ID_PATH,O.NAME,O.SHORT_NAME,P.CN_PATH,P.PIN_YIN_PATH,O.SEQ,P.LEAF,O.TYPE");
        if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
            sql.append(",I.CONTENT AS I_NAME ,S.CONTENT AS S_NAME ");
        }
        sql.append(" FROM ORG_ELEMENT O INNER JOIN ORG_ELEMENT_PATH P ON O.ORG_VERSION_UUID = P.ORG_VERSION_UUID AND P.ORG_ELEMENT_ID = O.ID ");
        if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
            sql.append(" LEFT JOIN ORG_ELEMENT_I18N I ON I.DATA_ID =O.ID AND I.LOCALE=:locale AND I.DATA_CODE ='name' LEFT JOIN ORG_ELEMENT_I18N S ON S.DATA_ID =O.ID AND S.LOCALE=:locale AND S.DATA_CODE ='short_name' ");
        }
        if (orgVersionIds != null && orgVersionIds.length > 1) {
            sql.append(" WHERE O.ORG_VERSION_ID in :orgVersionIds ");
        } else {
            sql.append(" WHERE O.ORG_VERSION_ID = :orgVersionId ");
        }
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
                    if (node != null) {
                        node.setTitlePath(i18nPathMap.get(uuid));
                    }
                }
            }
        }

        // 构建父子层级
        List<Node> root = buildTreeNode(nodeMap, uuidIdMap);
        return root;
    }

    private List<Node> buildTreeNode(Map<String, Node> nodeMap, Map<String, String> uuidIdMap) {
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
                if (parent == null) {
                    root.add(node);
                    continue;
                }

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
     * @param userIds
     * @param orgEleIds
     * @return
     */
    private List<Node> fetchMixTaskUsers(Params params, List<String> userIds, List<String> orgEleIds) {
        List<Node> root = Lists.newArrayList();
        Map<String, Node> nodeMap = Maps.newLinkedHashMap();
        String orgUuid = (String) params.get("orgUuid");
        String orgVersionId = (String) params.get("orgVersionId");
        if (StringUtils.isNotBlank(orgUuid)) {
            String latestOrgVersionId = workflowOrgService.getOrgVersionIdByOrgUuid(Long.valueOf(orgUuid));
            if (StringUtils.isNotBlank(latestOrgVersionId)) {
                orgVersionId = latestOrgVersionId;
            }
        }
        String[] orgVersionIds = getOrgVersionIds(orgVersionId, params);

        boolean loadEleUser = CollectionUtils.isNotEmpty(orgEleIds) && StringUtils.equals(Objects.toString(params.get("loadEleUser")), "true");

        List<String> excludeOrgTypes = getExcludeOrgTypes(orgEleIds);
        StringBuilder sql = new StringBuilder(getElementQuerySql(orgVersionIds));
        if (CollectionUtils.isNotEmpty(excludeOrgTypes)) {
            sql.append(" and O.type not in(:excludeOrgTypes)");
        }
        Map<String, Object> sqlParams = Maps.newHashMap();
        sqlParams.put("orgVersionId", orgVersionId);
        sqlParams.put("orgVersionIds", orgVersionIds);
        sqlParams.put("excludeOrgTypes", excludeOrgTypes);
        // 加载组织元素下的组织元素
        if (loadEleUser && CollectionUtils.isNotEmpty(orgEleIds)) {
            Map<String, String> elementMap = orgFacadeService.getOrgElementsByIds(orgEleIds, orgVersionIds);
            List<String> allOrgEleIds = Lists.newArrayList(elementMap.keySet());
            allOrgEleIds.addAll(orgEleIds);
            sqlParams.put("orgEleIds", allOrgEleIds);
        } else {
            sqlParams.put("orgEleIds", orgEleIds);
        }
        sqlParams.put("locale", LocaleContextHolder.getLocale().toString());
        if (CollectionUtils.isNotEmpty(userIds)) {
            sql.append(" order by O.PARENT_UUID DESC ,O.SEQ ASC ");
            List<QueryItem> queryItems = taskInstanceService.listQueryItemBySQL(sql.toString(), sqlParams, null);
            root = buildOrgTree(queryItems, nodeMap, orgVersionId);
        }

        // 查询组织元素
        if (CollectionUtils.isNotEmpty(orgEleIds)) {
            sql = new StringBuilder(getElementQuerySql(orgVersionIds));
            sql.append(" and O.id in(:orgEleIds)");
            sql.append(" order by o.parent_uuid desc ,o.seq asc");
            List<QueryItem> queryItems = taskInstanceService.listQueryItemBySQL(sql.toString(), sqlParams, null);
            Map<String, String> uuidIdMap = Maps.newHashMap();
            for (QueryItem item : queryItems) {
                String uuid = item.getLong("uuid").toString();
                if (uuidIdMap.containsKey(uuid)) {
                    continue;
                }
                Node node = new Node();
                item.put("uuid", uuid);
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
                root.add(node);
            }
            // 加载组织元素下的组织元素时，构建组织元素节点树
            if (loadEleUser && CollectionUtils.isNotEmpty(orgEleIds)) {
                root = buildTreeNode(nodeMap, uuidIdMap);
            }
        }

        List<String> allUserIds = Lists.newArrayList(userIds);
        // 加载组织元素下的用户
        if (loadEleUser) {
            Map<String, String> orgUserIds = orgFacadeService.getUsersByIds(orgEleIds, orgVersionIds);
            allUserIds.addAll(orgUserIds.keySet());
        }

        // 查询节点下的用户
        if (CollectionUtils.isNotEmpty(allUserIds)) {
            sqlParams.put("userIds", allUserIds);
            for (String versionId : orgVersionIds) {
                sqlParams.put("orgVersionId", versionId);
                List<QueryItem> userQueryItems = taskInstanceService.getDao().listQueryItemBySQL(getUserQuerySql(), sqlParams, null);
                for (QueryItem item : userQueryItems) {
                    Node node = new Node();
                    item.put("type", "user");
                    node.setKey(item.getString("userId"));
                    node.setTitle(StringUtils.defaultIfBlank(item.getString("iUserName"), item.getString("userName")));
                    node.setShortTitle(node.getTitle());
                    node.setVersion(orgVersionId);
                    node.setType("user");
                    node.setData(item);
                    node.setIsLeaf(true);
                    Node parent = nodeMap.get(item.getString("orgElementId"));
                    if (parent == null) {
                        if (!loadEleUser) {
                            root.add(node);
                        }
                    } else {
                        node.setParentKey(parent.getKey());
                        if (CollectionUtils.isEmpty(parent.getChildren())) {
                            parent.setChildren(Lists.newArrayList());
                            parent.setIsLeaf(false);
                        }
                        node.setTitlePath(StringUtils.join(new String[]{parent.getTitlePath(), node.getTitle()}, Separator.SLASH.getValue()));
                        node.setKeyPath(StringUtils.join(new String[]{parent.getKeyPath(), node.getKey()}, Separator.SLASH.getValue()));
                        parent.getChildren().add(node);
                    }
                }
            }
        }
        return root;
    }

    /**
     * @param orgEleIds
     * @return
     */
    private List<String> getExcludeOrgTypes(List<String> orgEleIds) {
        if (CollectionUtils.isEmpty(orgEleIds)) {
            return Collections.emptyList();
        }
        Set<String> excludeOrgTypes = Sets.newHashSet();
        orgEleIds.forEach(orgEleId -> {
            if (StringUtils.startsWith(orgEleId, IdPrefix.USER.getValue())) {
                excludeOrgTypes.add("user");
            } else if (StringUtils.startsWith(orgEleId, IdPrefix.JOB.getValue())) {
                excludeOrgTypes.add("job");
            } else if (StringUtils.startsWith(orgEleId, IdPrefix.DEPARTMENT.getValue())) {
                excludeOrgTypes.add("dept");
            } else if (StringUtils.startsWith(orgEleId, IdPrefix.SYSTEM_UNIT.getValue())) {
                excludeOrgTypes.add("unit");
            }
        });
        return Lists.newArrayList(excludeOrgTypes);
    }

    /**
     * @param params
     * @return
     */
    private List<String> getUserIds(Params params) {
        List<String> userIds = (List<String>) params.get("userIds");
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        return userIds.stream().filter(userId -> StringUtils.startsWith(userId, IdPrefix.USER.getValue())).collect(Collectors.toList());
    }

    /**
     * @param params
     * @return
     */
    private Map<String, String> getUserJobIdentityMap(Params params) {
        Map<String, String> userJobIdentityMap = (Map<String, String>) params.get("userJobIdentityMap");
        if (MapUtils.isEmpty(userJobIdentityMap)) {
            return Collections.emptyMap();
        }
        return userJobIdentityMap;
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
    private String[] getOrgVersionIds(String orgVersionId, Params params) {
        Set<String> versionIds = Sets.newLinkedHashSet();
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
