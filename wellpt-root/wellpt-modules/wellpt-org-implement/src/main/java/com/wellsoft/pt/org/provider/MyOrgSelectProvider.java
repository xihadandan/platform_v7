package com.wellsoft.pt.org.provider;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.pt.multi.org.support.dataprovider.OrgSelectProvider;
import com.wellsoft.pt.org.entity.*;
import com.wellsoft.pt.org.service.*;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.enums.BuildInRole;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * Description: 我的组织
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年12月12日   chenq	 Create
 * </pre>
 */
@Component
@Order(1)
public class MyOrgSelectProvider implements OrgSelectProvider {
    @Resource
    OrgElementService orgElementService;
    @Resource
    OrgVersionService orgVersionService;
    @Resource
    OrgElementPathService orgElementPathService;
    @Resource
    OrgSettingService orgSettingService;
    @Resource
    OrgUserService orgUserService;
    @Resource
    BizOrgElementService bizOrgElementService;
    @Resource
    BizOrgRoleService bizOrgRoleService;
    @Resource
    BizOrganizationService bizOrganizationService;
    @Resource
    OrgElementI18nService orgElementI18nService;
    @Resource
    OrgElementModelService orgElementModelService;


    @Override
    public String type() {
        return "MyOrg";
    }


    @Override
    public List<Node> fetch(Params params) {
        boolean search = false;
        boolean userHidden = params.containsKey("userHidden") && params.get("userHidden").toString().equalsIgnoreCase("true");
        String parentKeyStr = params.getParentKey();
        StringBuilder sql = new StringBuilder("SELECT O.UUID,O.ORG_VERSION_ID,O.ORG_VERSION_UUID,O.PARENT_UUID,O.ID,P.ID_PATH,O.NAME,O.SHORT_NAME,P.CN_PATH,P.PIN_YIN_PATH,O.SEQ,P.LEAF,O.TYPE");
        if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
            sql.append(", N_I18N.CONTENT AS NAME_I18N , S_I18N.CONTENT AS SHORT_NAME_I18N ");
        }
        List<String> conditions = Lists.newArrayList();
        Map<String, Object> sqlParams = Maps.newHashMap();
        Long parentUuid = null;
        OrgVersionEntity version = null;
        OrgElementEntity parentOrgElement = null;
        // 未指定组织版本号时候，取当前组织的发布版
        if (StringUtils.isBlank(params.getOrgVersionId())) {
            // 指定组织版本
            if (params.getOrgUuid() != null) {
                version = orgVersionService.getByStateAndOrgUuid(OrgVersionEntity.State.PUBLISHED, params.getOrgUuid());
            } else {
                // 默认组织版本
                version = orgVersionService.getDefaultOrgVersionBySystem(RequestSystemContextPathResolver.system());
            }
        } else {
            version = orgVersionService.getById(params.getOrgVersionId());
        }
        if (version != null) {
            sqlParams.put("orgVersionUuid", version.getUuid());
        } else {
            return null;
        }
        conditions.add("O.ORG_VERSION_UUID=:orgVersionUuid");

        OrgSettingEntity jobLevelVisibleSet = orgSettingService.getOrgSettingBySystemTenantAndAttrKey(version.getSystem(), version.getTenant(), "ORG_DIALOG_JOB_LEVEL_VISIBLE");
        List<String> underUnitIds = Lists.newArrayList();
        boolean crossUnitDataDisplay = true;
        if (!SpringSecurityUtils.hasAnyRole(BuildInRole.ROLE_TENANT_ADMIN.name(), BuildInRole.ROLE_ADMIN.name())) {
            OrgSettingEntity crossUnitDataVisibleSetting = orgSettingService.getOrgSettingBySystemTenantAndAttrKey(version.getSystem(), version.getTenant(), "ORG_CROSS_DATA_VISIBLE");
            crossUnitDataDisplay = crossUnitDataVisibleSetting != null && BooleanUtils.isTrue(crossUnitDataVisibleSetting.getEnable());
        }
        if (!crossUnitDataDisplay) {
            // 仅展示用户的单位下节点
            OrgElementPathChainService orgElementPathChainService = ApplicationContextHolder.getBean(OrgElementPathChainService.class);
            Map<String, Object> rankParams = ImmutableMap.of("userId", SpringSecurityUtils.getCurrentUserId());
            List<OrgElementPathChainEntity> chainEntities = orgElementPathChainService.listBySQL("SELECT *\n" +
                    "FROM (\n" +
                    "    SELECT c.*,\n" +
                    "           RANK() OVER (ORDER BY c.\"LEVEL\") AS rn\n" +
                    "    FROM org_element_path_chain c\n" +
                    "    WHERE c.org_element_type = 'unit'\n" +
                    "      AND EXISTS (\n" +
                    "          SELECT 1\n" +
                    "          FROM org_user ou\n" +
                    "          WHERE ou.org_element_id = c.sub_org_element_id\n" +
                    "            AND ou.user_id = :userId\n" +
                    "      )\n" +
                    ")\n" +
                    "WHERE rn = 1", rankParams);
            if (CollectionUtils.isNotEmpty(chainEntities)) {
                for (OrgElementPathChainEntity c : chainEntities) {
                    underUnitIds.add(c.getOrgElementId());
                }
                sqlParams.put("underUnitIds", underUnitIds);
            }
        }
        List<OrgElementModelEntity> orgElementModelEntities = orgElementModelService.listOrgElementModels(version.getTenant(), version.getSystem());
        List<String> modelIds = Lists.newArrayList();
        for (OrgElementModelEntity m : orgElementModelEntities) {
            if (BooleanUtils.isTrue(m.getEnable())) {
                modelIds.add(m.getId());
            }
        }

        Map<String, Object> excludeKeyFilterResult = extractIds(params.getExcludeKeys());
        Map<String, Object> includeKeyFilterResult = extractIds(params.getIncludeKeys());
        Set<String> excludeUserIds = (Set<String>) excludeKeyFilterResult.get("filterUserIds");
        Set<String> includeUserIds = (Set<String>) includeKeyFilterResult.get("filterUserIds");
        Set<String> includeOrgElementIds = (Set<String>) includeKeyFilterResult.get("filterElementIds");
        Set<String> excludeOrgElementIds = (Set<String>) excludeKeyFilterResult.get("filterElementIds");
        Map<String, Set<String>> includeUserUnderElements = (Map<String, Set<String>>) includeKeyFilterResult.get("filterUserUnderElementIds");
        Map<String, Set<String>> excludeUserUnderElements = (Map<String, Set<String>>) excludeKeyFilterResult.get("filterUserUnderElementIds");
        if (!includeUserIds.isEmpty()) {
            // 筛选包含用户的组织节点
            List<OrgUserEntity> includeOrgUser = orgUserService.listByUserIdsAndOrgVersionUuidAndTypes(Lists.newArrayList(includeUserIds),
                    version.getUuid(), Lists.newArrayList(OrgUserEntity.Type.PRIMARY_JOB_USER, OrgUserEntity.Type.SECONDARY_JOB_USER));
            if (CollectionUtils.isNotEmpty(includeOrgUser)) {
                for (OrgUserEntity o : includeOrgUser) {
                    if (!includeUserUnderElements.containsKey(o.getOrgElementId())) {
                        includeUserUnderElements.put(o.getOrgElementId(), Sets.newHashSet());
                    }
                    includeUserUnderElements.get(o.getOrgElementId()).add(o.getUserId());
                }
            }
        }

        if (!excludeUserIds.isEmpty()) {
            // 筛选排除用户的组织节点
            List<OrgUserEntity> excludeOrgUser = orgUserService.listByUserIdsAndOrgVersionUuidAndTypes(Lists.newArrayList(excludeUserIds),
                    version.getUuid(), Lists.newArrayList(OrgUserEntity.Type.PRIMARY_JOB_USER, OrgUserEntity.Type.SECONDARY_JOB_USER));
            if (CollectionUtils.isNotEmpty(excludeOrgUser)) {
                for (OrgUserEntity o : excludeOrgUser) {
                    if (!excludeUserUnderElements.containsKey(o.getOrgElementId())) {
                        excludeUserUnderElements.put(o.getOrgElementId(), Sets.newHashSet());
                    }
                    excludeUserUnderElements.get(o.getOrgElementId()).add(o.getUserId());
                }
            }
        }


        boolean jobLevelVisible = jobLevelVisibleSet != null && BooleanUtils.isTrue(jobLevelVisibleSet.getEnable());
        if (StringUtils.isBlank(params.getKeyword())) {
            // 非搜索情况下，如果总节点数超过指定数量，则强制开启异步模式
            if (!params.async() && parentKeyStr == null) {
                StringBuilder countsql = new StringBuilder("SELECT count(1) as total FROM ORG_ELEMENT O WHERE O.ORG_VERSION_UUID=:orgVersionUuid");
                if (!crossUnitDataDisplay && CollectionUtils.isNotEmpty(underUnitIds)) {
                    countsql.append(" AND ( EXISTS (" +
                            "SELECT 1 FROM ORG_ELEMENT_PATH_CHAIN C WHERE C.ORG_ELEMENT_ID IN (:underUnitIds) AND C.SUB_ORG_ELEMENT_ID = O.ID  AND C.SUB_ORG_ELEMENT_TYPE <> 'unit' " +
                            // 子节点非某个子单位下
                            " AND NOT EXISTS (SELECT 1 FROM ORG_ELEMENT_PATH_CHAIN S WHERE S.SUB_ORG_ELEMENT_ID = C.SUB_ORG_ELEMENT_ID  AND S.ORG_ELEMENT_TYPE = 'unit' AND S.ORG_ELEMENT_ID NOT IN (:underUnitIds) )" +
                            ") OR O.ID IN (:underUnitIds) ) ");
                }
                List<QueryItem> count = orgElementService.listQueryItemBySQL(countsql.toString(), sqlParams, null);
                OrgSettingEntity settingEntity = orgSettingService.getOrgSettingBySystemTenantAndAttrKey(version.getSystem(), version.getTenant(), "MY_ORG_FORCE_ASYNC_FETCH_THRESHOLD");
                int total = 1000;
                if (settingEntity != null && StringUtils.isNotBlank(settingEntity.getAttrVal())) {
                    JSONObject obj = JSONObject.fromObject(settingEntity.getAttrVal());
                    if (obj != null && obj.containsKey("num")) {
                        total = obj.optInt("num", total);
                    }
                }
                if (count.get(0).getLong("total") >= total) {
                    // 大于 1000 个节点则采用异步加载
                    params.put("async", "true");
                    params.put("FORCE_ASYNC", 1); // 标记给前端强制转为异步
                }
                // 用户数据量较大，标记给前端强制要求异步获取用户数据
                long userCount = orgUserService.countUserByOrgVersionUuid(version.getUuid());
                if (userCount >= 500) {
                    params.put("FORCE_USER_QUERY_ASYNC", 1);
                }
            }
            if (parentKeyStr != null) {
                params.put("async", "true");
            }
        } else {
            params.put("async", "false");
            sqlParams.put("keyword", "%" + params.getKeyword() + "%");
            conditions.add(" ( O.NAME LIKE :keyword OR O.SHORT_NAME LIKE :keyword ) ");
            if (!includeOrgElementIds.isEmpty()) {
                conditions.add(" O.ID IN (:includeIds) ");
                sqlParams.put("includeIds", includeOrgElementIds);
            }
            if (!excludeOrgElementIds.isEmpty()) {
                conditions.add(" O.ID NOT IN (:excludeIds) ");
                sqlParams.put("excludeIds", excludeOrgElementIds);
            }
            search = true;
        }

        // 异步节点数据的情况下，需要判断各个节点下是否有用户节点，从而决定节点是否是叶子节点
        if (!userHidden && params.async()) {
            sql.append(", ( SELECT COUNT(1)  FROM ORG_USER R WHERE R.ORG_ELEMENT_ID = O.ID AND R.ORG_VERSION_UUID = O.ORG_VERSION_UUID ) AS USER_UNDER_COUNT");
        }
        sql.append(" FROM ORG_ELEMENT O INNER JOIN ORG_ELEMENT_PATH P ON P.ORG_ELEMENT_ID = O.ID");
        if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
            sql.append(" LEFT JOIN ORG_ELEMENT_I18N N_I18N ON N_I18N.DATA_CODE = 'name' AND N_I18N.DATA_UUID = O.UUID AND N_I18N.LOCALE ='" + LocaleContextHolder.getLocale().toString() + "' ");
            sql.append(" LEFT JOIN ORG_ELEMENT_I18N S_I18N ON S_I18N.DATA_CODE = 'short_name' AND S_I18N.DATA_UUID = O.UUID AND S_I18N.LOCALE ='" + LocaleContextHolder.getLocale().toString() + "' ");
        }
        sql.append(" WHERE O.ORG_VERSION_UUID = P.ORG_VERSION_UUID AND ");
        if (!crossUnitDataDisplay && CollectionUtils.isNotEmpty(underUnitIds)) {
            sql.append(" ( EXISTS (" +
                    "SELECT 1 FROM ORG_ELEMENT_PATH_CHAIN C WHERE C.ORG_ELEMENT_ID IN (:underUnitIds) AND C.SUB_ORG_ELEMENT_ID = O.ID  AND C.SUB_ORG_ELEMENT_TYPE <> 'unit' " +
                    // 子节点非某个子单位下
                    " AND NOT EXISTS (SELECT 1 FROM ORG_ELEMENT_PATH_CHAIN S WHERE S.SUB_ORG_ELEMENT_ID = C.SUB_ORG_ELEMENT_ID  AND S.ORG_ELEMENT_TYPE = 'unit' AND S.ORG_ELEMENT_ID NOT IN (:underUnitIds) )" +
                    ") OR O.ID IN (:underUnitIds) ) AND ");
        }

        List<QueryItem> nodeQueryItems = Lists.newArrayList();
        Set<String> pathIds = Sets.newHashSet();
        if (!params.async()) {
            // 获取所有数据
            sql.append(StringUtils.join(conditions, " AND "));
            sql.append(" ORDER BY O.PARENT_UUID DESC,O.SEQ ASC");
            // 查询全部
            nodeQueryItems = orgElementService.listQueryItemBySQL(sql.toString(), sqlParams, null);
            if (CollectionUtils.isEmpty(nodeQueryItems) && !search) { // 非检索状态的情况下，没有节点则直接返回，搜索情况下，还需要继续下面的用户查询
                return null;
            }
        } else {
            // 获取指定节点下的数据
            String inExIdConditions = null;
            if (parentKeyStr == null) {
                if (!includeOrgElementIds.isEmpty() || !excludeOrgElementIds.isEmpty()) {
                    List<String> parts = Lists.newArrayList();

                    parts.add(" ( O.PARENT_UUID IS NULL ");
                    if (!includeOrgElementIds.isEmpty()) {
                        parts.add(" AND O.ID IN (:includeIds) ");
                        sqlParams.put("includeIds", includeOrgElementIds);
                    }
                    if (!excludeOrgElementIds.isEmpty()) {
                        parts.add(" AND O.ID NOT IN (:excludeIds) ");
                        sqlParams.put("excludeIds", excludeOrgElementIds);
                    }
                    parts.add(" ) ");
                    inExIdConditions = StringUtils.join(parts, "");
                    conditions.add(inExIdConditions);
                } else {
                    conditions.add("O.PARENT_UUID IS NULL");
                }

            } else {
                // 查找父级节点
                if (NumberUtils.isNumber(parentKeyStr)) {
                    parentUuid = Long.parseLong(parentKeyStr);
                } else {
                    OrgElementEntity parentElement = orgElementService.getByIdAndOrgVersionUuid(parentKeyStr, version.getUuid());
                    if (parentElement != null) {
                        parentOrgElement = parentElement;
                        parentUuid = parentElement.getUuid();
                    } else if (parentKeyStr.indexOf(Separator.SLASH.getValue()) != -1) {
                        OrgElementPathEntity pathEntity = orgElementPathService.getByIdPathAndOrgVersionUuid(parentKeyStr, version.getUuid());
                        if (pathEntity != null) {
                            parentUuid = pathEntity.getOrgElementUuid();
                        }
                    }
                }
                if (parentUuid == null) {
                    throw new RuntimeException("未知的组织数据");
                }
                sqlParams.put("parentUuid", parentUuid);
                conditions.add("O.PARENT_UUID = :parentUuid");
                if (parentOrgElement == null) {
                    parentOrgElement = orgElementService.getOne(parentUuid);
                }
            }
            sql.append(StringUtils.join(conditions, " AND "));

            // 异步模式要根据前端已选内容，判断是否在即将加载的节点路径下，是则半选反馈节点
            if (CollectionUtils.isNotEmpty(params.getCheckedKeys())) {
                Map<String, Object> pathParam = Maps.newHashMap();
                pathParam.put("ids", params.getCheckedKeys());
                List<OrgElementPathEntity> pathEntities = orgElementPathService.listByHQL("from OrgElementPathEntity where orgElementId in :ids or idPath in :ids", pathParam);
                if (CollectionUtils.isNotEmpty(pathEntities)) {
                    for (OrgElementPathEntity p : pathEntities) {
                        pathIds.addAll(Arrays.asList(p.getIdPath().split(Separator.SLASH.getValue())));
                    }
                }
            }
            nodeQueryItems = orgElementService.listQueryItemBySQL(sql.toString(), sqlParams, null);
            if (CollectionUtils.isEmpty(nodeQueryItems) && parentKeyStr == null && !includeOrgElementIds.isEmpty()) {
                // 顶级节点未筛选出节点，则直接取包含节点的
                String trySqlString = sql.toString().replace(inExIdConditions, " ( O.ID IN (:includeIds) ) ");
                nodeQueryItems = orgElementService.listQueryItemBySQL(trySqlString, sqlParams, null);
                // 筛选出层级较高的返回即可
                // 过滤出树节点
                if (CollectionUtils.isNotEmpty(nodeQueryItems)) {
                    Map<String, List<QueryItem>> sameChainMap = Maps.newLinkedHashMap();
                    // 重新基于包含节点构建树
                    Map<String, Node> tempNodeMap = Maps.newHashMap();
                    Set<String> subNodes = Sets.newHashSet();
                    for (QueryItem item : nodeQueryItems) {
                        Node n = new Node();
                        n.setKey(item.getString("id"));
                        n.setData(item);
                        tempNodeMap.put(item.getString("id"), n);
                        String idPath = item.getString("idPath");
                        String parentIdPath = idPath.substring(0, idPath.lastIndexOf(Separator.SLASH.getValue()));
                        String[] parentIds = parentIdPath.split(Separator.SLASH.getValue());
                        ArrayUtils.reverse(parentIds);
                        for (String p : parentIds) {
                            if (tempNodeMap.containsKey(p)) {
                                // 加入直接子级
                                if (tempNodeMap.get(p).getChildren() == null) {
                                    tempNodeMap.get(p).setChildren(Lists.newArrayList());
                                }
                                n.setData(item);
                                tempNodeMap.get(p).getChildren().add(n);
                                break;
                            }
                        }
                    }
                    for (QueryItem item : nodeQueryItems) {
                        String idPath = item.getString("idPath");
                        String parentIdPath = idPath.substring(0, idPath.lastIndexOf(Separator.SLASH.getValue()));
                        String[] parentIds = parentIdPath.split(Separator.SLASH.getValue());
                        ArrayUtils.reverse(parentIds);
                        for (String p : parentIds) {
                            if (tempNodeMap.containsKey(p)) {
                                // 加入直接子级
                                if (tempNodeMap.get(p).getChildren() == null) {
                                    tempNodeMap.get(p).setChildren(Lists.newArrayList());
                                }
                                Node n = new Node();
                                n.setData(item);
                                tempNodeMap.get(p).getChildren().add(n);
                                subNodes.add(item.getString("id"));
                                break;
                            }
                        }
                    }
                    List<QueryItem> result = Lists.newArrayList();
                    for (QueryItem item : nodeQueryItems) {
                        if (!subNodes.contains(item.getString("id"))) {
                            result.add(item);
                        }
                    }
                    nodeQueryItems = result;
                }
            }
        }

        // 组装数据
        Map<String, Node> nodeMap = Maps.newLinkedHashMap();
        Map<String, String> uuidIdMap = Maps.newHashMap();
        List<Node> nodes = Lists.newArrayList();
        List<Node> root = Lists.newArrayList();


        boolean includeCurrentLevelShowNodes = false;
        boolean excludeCurrentLevelShowNodes = false;
        if (params.async() && (!includeOrgElementIds.isEmpty() || !excludeOrgElementIds.isEmpty())) {
            // 异步加载情况下，要判断当前级的节点有没有指定显示范围，如果有，则当前级的节点仅限显示的这部分
            for (QueryItem item : nodeQueryItems) {
                String id = item.getString("id");
                if (includeOrgElementIds.contains(id)) {
                    includeCurrentLevelShowNodes = true;
                    break;
                }
                if (excludeOrgElementIds.contains(id)) {
                    excludeCurrentLevelShowNodes = true;
                    break;
                }
            }
        }
        Map<String, String> idI18ns = Maps.newHashMap();
        List<String> hiddenNodeIds = Lists.newArrayList();
        List<String> unUsedNodeIds = Lists.newArrayList();
        for (QueryItem item : nodeQueryItems) {
            Node n = new Node();
            item.put("uuid", item.getLong("uuid").toString());
            String uuid = item.getString("uuid");
            String id = item.getString("id");
            String keyPath = item.getString("idPath");
            if (!modelIds.contains(item.getString("type"))) {
                n.setParentKey(item.getLong("parentUuid") != null ? item.getLong("parentUuid").toString() : null);
                n.setKey(id);
                n.setTitle(StringUtils.defaultString(item.getString("nameI18n"), item.getString("name")));
                unUsedNodeIds.add(id);
                nodeMap.put(id, n);
                uuidIdMap.put(uuid, id);
                continue;
            }
            if (excludeOrgElementIds.contains(id) || (includeCurrentLevelShowNodes && !includeOrgElementIds.contains(id))) {
                continue;
            }
            // 判断是否是包含节点的子节点
            if (!includeOrgElementIds.isEmpty() && StringUtils.isNotBlank(keyPath)) {
                String[] keyParts = keyPath.split(Separator.SLASH.getValue());
                boolean underPath = false;
                for (String p : keyParts) {
                    if (includeOrgElementIds.contains(p)) {
                        underPath = true;
                        break;
                    }
                }

                if (!underPath) {
                    continue;
                }
            }


            n.setKey(id);
            n.setVersion(version.getId());
            n.setTitle(StringUtils.defaultString(item.getString("nameI18n"), item.getString("name")));
            if (StringUtils.isNotBlank(item.getString("nameI18n"))) {
                idI18ns.put(id, item.getString("nameI18n"));
            }
            n.setType(item.getString("type"));
            n.setShortTitle(StringUtils.defaultString(item.getString("shortNameI18n"), item.getString("shortName")));
            n.setKeyPath(item.getString("idPath"));
            n.setTitlePath(item.getString("cnPath"));
            n.setData(item);
            n.setIsLeaf(item.getInt("leaf") == 1);
            if (!userHidden && params.async() && item.containsKey("userUnderCount") && item.getInt("userUnderCount") > 0) {
                // 判断用户是否在其下
                n.setIsLeaf(false);
            }
            if (pathIds.contains(id)) {// 层级下有已选的内容，半选状态
                n.setHalfChecked(true);
            }

            n.setParentKey(item.getLong("parentUuid") != null ? item.getLong("parentUuid").toString() : null);
            nodeMap.put(id, n);
            uuidIdMap.put(uuid, id);
            nodes.add(n);
        }

        if (params.async() && parentUuid != null && !includeOrgElementIds.isEmpty() && (excludeCurrentLevelShowNodes || includeCurrentLevelShowNodes)) {
            // 查找包含节点是否有当前级的下一级候补顶上来该级的（由于下一级的父级没有在当前级展示出来）
            sqlParams.put("includeIds", includeOrgElementIds);
            String trySql = sql.toString().replace(" AND O.PARENT_UUID = :parentUuid", " AND O.ID IN (:includeIds) ");
            if (!excludeOrgElementIds.isEmpty()) {
                trySql += " AND O.ID NOT IN (:excludeIds) ";
                sqlParams.put("excludeIds", excludeOrgElementIds);
            }
            List<QueryItem> includeNodeQueryItems = orgElementService.listQueryItemBySQL(trySql, sqlParams, null);
            for (QueryItem item : includeNodeQueryItems) {
                String[] idPaths = item.getString("idPath").split(Separator.SLASH.getValue());
                String pid = idPaths[idPaths.length - 2];
                if (hiddenNodeIds.contains(pid) || CollectionUtils.containsAny(Lists.newArrayList(idPaths), hiddenNodeIds)) {
                    Node n = new Node();
                    item.put("uuid", item.getLong("uuid").toString());
                    String uuid = item.getString("uuid");
                    String id = item.getString("id");
                    n.setKey(id);
                    n.setVersion(version.getId());
                    n.setTitle(item.getString("name"));
                    n.setType(item.getString("type"));
                    n.setShortTitle(item.getString("shortName"));
                    n.setKeyPath(item.getString("idPath"));
                    n.setTitlePath(item.getString("cnPath"));
                    n.setData(item);
                    n.setIsLeaf(item.getInt("leaf") == 1);
                    if (!userHidden && params.async() && item.containsKey("userUnderCount") && item.getInt("userUnderCount") > 0) {
                        // 判断用户是否在其下
                        n.setIsLeaf(false);
                    }
                    if (pathIds.contains(id)) {// 层级下有已选的内容，半选状态
                        n.setHalfChecked(true);
                    }

                    n.setParentKey(item.getLong("parentUuid") != null ? item.getLong("parentUuid").toString() : null);
                    nodeMap.put(id, n);
                    uuidIdMap.put(uuid, id);
                    nodes.add(n);
                }
            }
        }

        // 更新国际化路径
        if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
            for (Node n : nodes) {
                String keyPath = n.getKeyPath();
                if (StringUtils.isNotBlank(keyPath)) {
                    String[] keys = keyPath.split(Separator.SLASH.getValue());
                    String[] titlePaths = n.getTitlePath().split(Separator.SLASH.getValue());
                    for (int i = 0, len = keys.length; i < len; i++) {
                        if (idI18ns.containsKey(keys[i])) {
                            titlePaths[i] = idI18ns.get(keys[i]);
                        }
                    }
                    n.setTitlePath(StringUtils.join(titlePaths, Separator.SLASH.getValue()));
                }
            }
        }

        // 非搜索状态下的全量数据，需要构建树形结构
        Map<String, Node> jobParentMap = Maps.newHashMap();
        if (!search && !params.async()) {
            Set<String> keys = nodeMap.keySet();
            // 构建父子层级
            for (String key : keys) {
                Node n = nodeMap.get(key);
                if (!crossUnitDataDisplay && CollectionUtils.isNotEmpty(underUnitIds) && underUnitIds.contains(n.getKey())) {
                    String pid = uuidIdMap.get(n.getParentKey());
                    if (StringUtils.isNotBlank(pid)) {
                        n.setParentKey(pid); // 父级uuid转为父级id
                    }
                    pid = n.getParentKey();
                    Node parent = nodeMap.get(pid);
                    if (parent == null) {
                        root.add(n);
                        continue;
                    }
                }
                if (unUsedNodeIds.contains(key)) {
                    continue;
                }
                boolean jobPassed = false;
                if (!jobLevelVisible && OrgElementModelEntity.ORG_JOB_ID.equalsIgnoreCase(n.getType())) {
                    // 不显示职位的情况下，需要把职位下的子节点挂载到职位的父节点底下
                    if (StringUtils.isNotBlank(n.getParentKey())) {
                        Node parent = nodeMap.get(n.getParentKey());
                        if (parent == null) {
                            parent = nodeMap.get(uuidIdMap.get(n.getParentKey()));
                        }
                        jobParentMap.put(n.getKey(), parent);
                    }
                    jobPassed = true;
                }
                if (StringUtils.isNotBlank(n.getParentKey())) {
                    String pid = uuidIdMap.get(n.getParentKey());
                    if (StringUtils.isNotBlank(pid)) {
                        n.setParentKey(pid); // 父级uuid转为父级id
                    }
                    pid = n.getParentKey();
                    Node parent = nodeMap.get(pid);
                    if (parent == null) {
                        // 父级被过滤掉了，直接往上一级挂载
                        String[] parts = n.getKeyPath().split(Separator.SLASH.getValue());
                        List children = root;
                        for (int i = parts.length - 3; i >= 0; i--) {
                            parent = nodeMap.get(parts[i]);
                            if (parent != null) {
                                children = parent.getChildren();
                                break;
                            }
                        }
                        children.add(n);
                        continue;
                    }
                    if (!jobLevelVisible && OrgElementModelEntity.ORG_JOB_ID.equalsIgnoreCase(parent.getType())) {
                        // 父节点为职位的情况下，职位不可见，需要把当前节点的父级调整为职位的父级
                        if (StringUtils.isNotBlank(parent.getParentKey())) {
                            parent = nodeMap.get(parent.getParentKey());
                            if (parent == null) {
                                parent = nodeMap.get(uuidIdMap.get(parent.getParentKey()));
                            }
                            n.setParentKey(parent.getKey());
                        } else {
                            n.setParentKey(null);
                            root.add(n);
                        }


                    }
                    if (jobPassed) {
                        if (parent.getChildren() == null) {
                            parent.setChildren(Lists.newArrayList());
                        }
                        // 把职位下的子节点挂接到父级的子节点集合内
                        if (CollectionUtils.isNotEmpty(n.getChildren())) {
                            mergeChildren(n.getChildren(), parent);
                        }
                        continue;
                    }


                    if (CollectionUtils.isEmpty(parent.getChildren())) {
                        parent.setChildren(Lists.newArrayList(n));
                        continue;
                    }
                    parent.getChildren().add(n);
                } else {
                    if (jobPassed) {
                        continue;
                    }
                    root.add(n);
                }
            }
        } else {
            root.addAll(nodes);
        }

        if (userHidden) {
            return root;
        }

        // 异步查询顶级时候，如果指定显示用户，要查询出指定用户，并且判断用户是否在指定的节点下，如果不在，则需要提到顶级展示
        boolean filterUserToRoot = parentKeyStr == null && CollectionUtils.isNotEmpty(includeUserIds) && !search && params.async();

        if (search || parentUuid != null || !params.async() || filterUserToRoot) {
            // 用户查询
            // 查询节点下的用户
            sql = new StringBuilder("SELECT U.UUID, U.LOGIN_NAME,U.USER_ID,U.AVATAR,U.GENDER,U.USER_NAME, U.PIN_YIN,U.USER_NO, RELA.ORG_ELEMENT_ID,U.CEIL_PHONE_NUMBER, U.MAIL  ");
            if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                sql.append(" , N.USER_NAME AS LOCALE_USER_NAME ");
            }
            if (params.async()) {
                sql.append(" , P.CN_PATH AS PARENT_CN_PATH, P.ID_PATH AS PARENT_ID_PATH FROM USER_INFO U INNER JOIN ORG_USER RELA ON RELA.USER_ID = U.USER_ID " +
                        "INNER JOIN ORG_ELEMENT_PATH P ON  P.ORG_ELEMENT_ID = RELA.ORG_ELEMENT_ID AND P.ORG_VERSION_UUID = RELA.ORG_VERSION_UUID ");
                if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                    sql.append(" LEFT JOIN USER_NAME_I18N N ON N.USER_UUID= U.UUID AND N.LOCALE='" + LocaleContextHolder.getLocale().toString() + "'");
                }
                // 查询指定节点下的用户
                if (!filterUserToRoot) {
                    sql.append(" AND EXISTS ( SELECT 1 FROM ORG_ELEMENT E WHERE E.ID = RELA.ORG_ELEMENT_ID AND E.ORG_VERSION_UUID = RELA.ORG_VERSION_UUID " +
                            (parentUuid == null ? " AND E.PARENT_UUID IS NULL  ) " : " AND E.UUID =:parentUuid ) "));
                    if (parentUuid != null) {
                        // 指定节点下，仅展示包含的用户，同级的其他用户不展示
                        if (includeUserUnderElements.containsKey(parentOrgElement.getId())) {
                            sql.append(" AND U.USER_ID IN (:includeUserIds) ");
                            sqlParams.put("includeUserIds", includeUserUnderElements.get(parentOrgElement.getId()));
                        }

                    }
                } else if (!includeUserIds.isEmpty() || !includeUserUnderElements.isEmpty()) {
                    List<String> parts = Lists.newArrayList();
                    if (!includeUserIds.isEmpty()) {
                        parts.add(" U.USER_ID IN (:includeUserIds)  ");
                        sqlParams.put("includeUserIds", includeUserIds);
                    }
                    if (!includeUserUnderElements.isEmpty()) {
                        parts.add(" ( RELA.ORG_ELEMENT_ID IN (:includeUserIdsUnderEleIds) AND RELA.USER_ID IN (:includeUserIdsUnderEle) )");
                        parts.add(" RELA.USER_ID IN (:includeElementIdsHasUser) ");
                        sqlParams.put("includeElementIdsHasUser", includeUserUnderElements.keySet());
                        Set<String> uids = Sets.newHashSet();
                        Set<Map.Entry<String, Set<String>>> entries = includeUserUnderElements.entrySet();
                        for (Map.Entry<String, Set<String>> ent : entries) {
                            uids.addAll(ent.getValue());
                        }
                        sqlParams.put("includeUserIdsUnderEle", uids);
                    }
                    sql.append(" AND ( ").append(StringUtils.join(parts, " OR ")).append(" ) ");
                }
            } else {
                if (search) {
                    sql.append(", P.CN_PATH AS PARENT_CN_PATH, P.ID_PATH AS PARENT_ID_PATH ");
                }
                sql.append(" FROM USER_INFO U INNER JOIN ORG_USER RELA ON RELA.USER_ID = U.USER_ID ");
                if (search) {
                    sql.append(" INNER JOIN ORG_ELEMENT_PATH P ON P.ORG_ELEMENT_ID = RELA.ORG_ELEMENT_ID AND P.ORG_VERSION_UUID = RELA.ORG_VERSION_UUID  ");
                }
                if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                    sql.append(" LEFT JOIN USER_NAME_I18N N ON N.USER_UUID= U.UUID AND N.LOCALE='" + LocaleContextHolder.getLocale().toString() + "'");
                }
                sql.append(" WHERE RELA.ORG_VERSION_UUID=:orgVersionUuid ");

                if (CollectionUtils.isNotEmpty(excludeOrgElementIds)) {
                    // 要排除这个节点下的用户
                    sql.append(" AND RELA.ORG_ELEMENT_ID NOT IN (:excludeOrgElementIds) AND NOT EXISTS ( SELECT 1 FROM ORG_ELEMENT_PATH_CHAIN C WHERE C.SUB_ORG_ELEMENT_ID=RELA.ORG_ELEMENT_ID AND C.ORG_ELEMENT_ID IN (:excludeOrgElementIds) AND C.ORG_VERSION_UUID = RELA.ORG_VERSION_UUID ) ");
                    sqlParams.put("excludeOrgElementIds", excludeOrgElementIds);
                }

                if (parentKeyStr == null && !crossUnitDataDisplay && CollectionUtils.isNotEmpty(underUnitIds)) {
                    sql.append(" AND ( EXISTS (" +
                            " SELECT 1 FROM ORG_ELEMENT_PATH_CHAIN C WHERE C.ORG_ELEMENT_ID IN (:underUnitIds) AND C.SUB_ORG_ELEMENT_ID = RELA.ORG_ELEMENT_ID  AND C.SUB_ORG_ELEMENT_TYPE <> 'unit' " +
                            // 子节点非某个子单位下
                            " AND NOT EXISTS (SELECT 1 FROM ORG_ELEMENT_PATH_CHAIN S WHERE S.SUB_ORG_ELEMENT_ID = C.SUB_ORG_ELEMENT_ID  AND S.ORG_ELEMENT_TYPE = 'unit' AND S.ORG_ELEMENT_ID NOT IN (:underUnitIds) )" +
                            ") OR RELA.ORG_ELEMENT_ID IN (:underUnitIds) ) ");
                }

            }
            if (search) {
                sql.append(" AND ( U.USER_NAME LIKE :keyword OR U.PIN_YIN LIKE :keyword  OR U.USER_NO LIKE :keyword  ");
                if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                    sql.append(" OR N.USER_NAME LIKE =:keyword OR U.USER_NO LIKE :keyword  ");
                }
                sql.append(")");
            }
            sql.append(" ORDER BY U.USER_NO ASC , U.PIN_YIN ASC");

            List<QueryItem> userQueryItems = orgElementService.getDao().listQueryItemBySQL(sql.toString(), sqlParams, null);
            Set<String> userAdded = Sets.newHashSet();
            Map<String, List<Node>> userUuidNodeMap = Maps.newHashMap();
            for (QueryItem item : userQueryItems) {
                if (StringUtils.isBlank(item.getString("orgElementId"))) {
                    continue;
                }

                String orgElementId = item.getString("orgElementId");
                if (excludeUserUnderElements.containsKey(orgElementId) && excludeUserUnderElements.get(orgElementId).contains(item.getString("userId"))) {
                    continue;
                }
                if (!includeUserUnderElements.isEmpty() && includeUserUnderElements.containsKey(orgElementId)
                        && !includeUserUnderElements.get(orgElementId).contains(item.getString("userId"))) {
                    continue;
                }

                Node n = new Node();
                item.put("type", "user");
                n.setKey(item.getString("userId"));
                if (!userUuidNodeMap.containsKey(item.getString("uuid"))) {
                    userUuidNodeMap.put(item.getString("uuid"), Lists.newArrayList());
                }
                userUuidNodeMap.get(item.getString("uuid")).add(n);
                n.setTitle(item.getString("userName"));
                if (StringUtils.isNotBlank(item.getString("localeUserName"))) {
                    n.setTitle(item.getString("localeUserName"));
                }
                n.setShortTitle(n.getTitle());
                n.setVersion(version.getId());
                n.setType("user");
                n.setData(item);
                n.setIsLeaf(true);
                if (!params.async() && !search) {
                    // 全量的情况下，需要把用户挂载到指定的节点下
                    Node parent = nodeMap.get(item.getString("orgElementId"));
                    if (parent == null) {// 没有父节点，就跳过
                        continue;
                    } else {
                        boolean passedJob = false;
                        if (OrgElementModelEntity.ORG_JOB_ID.equalsIgnoreCase(parent.getType()) && !jobLevelVisible) {
                            parent = jobParentMap.get(item.getString("orgElementId"));
                            passedJob = true;
                        }

                        n.setParentKey(parent.getKey());
                        if (CollectionUtils.isEmpty(parent.getChildren())) {
                            parent.setChildren(Lists.newArrayList());
                            parent.setIsLeaf(false);
                        }
                        n.setTitlePath(StringUtils.join(new String[]{parent.getTitlePath(), n.getTitle()}, Separator.SLASH.getValue()));
                        n.setKeyPath(StringUtils.join(new String[]{parent.getKeyPath(), n.getKey()}, Separator.SLASH.getValue()));
                        QueryItem userData = (QueryItem) n.getData();
                        // 获取上级职位、部门、职级
                        if (StringUtils.isNotBlank(n.getKeyPath())) {
                            String[] keys = n.getKeyPath().split(Separator.SLASH.getValue());
                            String[] titles = n.getTitlePath().split(Separator.SLASH.getValue());
                            for (int i = keys.length - 1; i >= 0; i--) {
                                if (keys[i].startsWith(IdPrefix.JOB.getValue() + Separator.UNDERLINE.getValue()) && !userData.containsKey("jobId")) {
                                    userData.put("job_id", keys[i]);
                                    userData.put("job_name", titles[i]);
                                } else if (keys[i].startsWith(IdPrefix.DEPARTMENT.getValue() + Separator.UNDERLINE.getValue()) && !userData.containsKey("deptId")) {
                                    userData.put("dept_id", keys[i]);
                                    userData.put("dept_name", titles[i]);
                                } else if (keys[i].startsWith(IdPrefix.SYSTEM_UNIT.getValue() + Separator.UNDERLINE.getValue()) && !userData.containsKey("unitId")) {
                                    userData.put("unit_id", keys[i]);
                                    userData.put("unit_name", titles[i]);
                                }
                            }
                        }
                        if (passedJob) {
                            mergeChildren(Lists.newArrayList(n), parent);
                        } else {
                            parent.getChildren().add(n);
                        }
                    }

                } else {
                    if (!includeOrgElementIds.isEmpty() || !includeUserIds.isEmpty()) {
                        String[] idPaths = item.getString("parentIdPath").split(Separator.SLASH.getValue());
                        if (!org.apache.commons.collections4.CollectionUtils.containsAny(includeOrgElementIds, idPaths)) {
                            if (filterUserToRoot && userAdded.add(n.getKey())) {
                                root.add(n);
                            }
                            continue;
                        } else if (filterUserToRoot) {
                            continue;
                        }
                    }
                    n.setTitlePath(item.containsKey("parentCnPath") && StringUtils.isNotBlank(item.getString("parentCnPath")) ?
                            StringUtils.join(new String[]{item.getString("parentCnPath"), n.getTitle()}, Separator.SLASH.getValue()) : n.getTitle());
                    n.setKeyPath(item.containsKey("parentIdPath") && StringUtils.isNotBlank(item.getString("parentIdPath")) ?
                            StringUtils.join(new String[]{item.getString("parentIdPath"), n.getKey()}, Separator.SLASH.getValue()) : n.getKey());
                    if (userAdded.add(n.getKey())) {
                        root.add(n);
                    }
                }


            }
            // 查询用户扩展信息
            String userAttrSql = "select attr_key,attr_value,user_uuid from USER_INFO_EXT where user_uuid in :userUuid ";
            if (MapUtils.isNotEmpty(userUuidNodeMap)) {
                ListUtils.handleSubList(Lists.newArrayList(userUuidNodeMap.keySet()), 200, list -> {
                    List<QueryItem> userAttrsQueryItems = orgElementService.getDao().listQueryItemBySQL(userAttrSql,
                            ImmutableMap.<String, Object>builder().put("userUuid", list).build(), null);
                    if (CollectionUtils.isNotEmpty(userAttrsQueryItems)) {
                        for (QueryItem q : userAttrsQueryItems) {
                            List<Node> userNodes = userUuidNodeMap.get(q.getString("userUuid"));
                            if (CollectionUtils.isNotEmpty(userNodes)) {
                                for (Node n : userNodes) {
                                    ((QueryItem) n.getData()).put(q.getString("attrKey"), q.getString("attrValue"), false);
                                }
                            }
                        }
                    }
                });
            }


        }
        return root;
    }

    private Map<String, Object> extractIds(List<String> ids) {
        Map<String, Set<String>> filterUserUnderElementIds = Maps.newHashMap();
        Set<String> filterElementIds = Sets.newHashSet();
        Set<String> filterUserIds = Sets.newHashSet();
        Map<String, Object> result = Maps.newHashMap();
        result.put("filterUserUnderElementIds", filterUserUnderElementIds);
        result.put("filterUserIds", filterUserIds);
        result.put("filterElementIds", filterElementIds);
        if (CollectionUtils.isNotEmpty(ids)) {
            for (String i : ids) {
                String[] parts = i.split(Separator.SLASH.getValue());
                if (parts.length == 1) {
                    if (i.startsWith(IdPrefix.USER.getValue() + Separator.UNDERLINE.getValue())) {
                        filterUserIds.add(i);
                    } else {
                        filterElementIds.add(i);
                    }
                } else {
                    if (parts[parts.length - 1].startsWith(IdPrefix.USER.getValue() + Separator.UNDERLINE.getValue())) {
                        // 用户在指定节点下的情况
                        String id = parts[parts.length - 2];
                        if (!filterUserUnderElementIds.containsKey(id)) {
                            filterUserUnderElementIds.put(id, Sets.newHashSet());
                        }
                        filterUserUnderElementIds.get(id).add(parts[parts.length - 1]);
                    } else {
                        filterElementIds.add(parts[parts.length - 1]);
                    }
                }
            }
        }
        return result;
    }

    private void mergeChildren(List<Node> children, Node parent) {
        if (CollectionUtils.isEmpty(parent.getChildren())) {
            parent.setChildren(children);
            return;
        }
        Set<String> childKeys = Sets.newHashSet();
        for (Node child : parent.getChildren()) {
            childKeys.add(child.getKey());
        }
        for (Node c : children) {
            if (!childKeys.contains(c.getKey())) {
                parent.getChildren().add(c);
            }
        }


    }


    @Override
    public List<Node> fetchByKeys(Params params) {
        List<Node> nodes = Lists.newArrayList();
        Map<String, String> idI18ns = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(params.getKeys())) {
            Map<Object, OrgVersionEntity> idVersions = Maps.newHashMap();
            List<OrgVersionEntity> allVersions = Lists.newArrayList();
            if (params.getOrgUuid() != null) {
                List<OrgVersionEntity> temps = orgVersionService.getAllPublishedVersionBySystem(RequestSystemContextPathResolver.system());
                // 把当前组织的发布版本排第一个匹配
                Iterator<OrgVersionEntity> iterator = temps.iterator();
                while (iterator.hasNext()) {
                    OrgVersionEntity orgVer = iterator.next();
                    if (!orgVer.getOrgUuid().equals(params.getOrgUuid())) {
                        allVersions.add(orgVer);
                    } else {
                        allVersions.add(0, orgVer);
                    }
                }
            }

            Iterator<String> keyIterator = params.getKeys().iterator();
            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                if (key.startsWith(IdPrefix.BIZ_PREFIX.getValue() + Separator.UNDERLINE.getValue())
                        || key.startsWith(IdPrefix.BIZ_ORG_DIM.getValue() + Separator.UNDERLINE.getValue())
                ) {
                    String[] keys = key.split(Separator.SLASH.getValue());
                    List<String> titlePath = Lists.newArrayListWithCapacity(keys.length);
                    Node n = new Node();
                    String nearBizOrgEleId = null;
                    Long bizOrgUuid = null;
                    for (String k : keys) {
                        if (k.startsWith(IdPrefix.BIZ_PREFIX.getValue() + Separator.UNDERLINE.getValue())
                                || k.startsWith(IdPrefix.BIZ_ORG_DIM.getValue() + Separator.UNDERLINE.getValue())) {
                            BizOrgElementEntity entity = bizOrgElementService.getById(k);
                            if (entity != null) {
                                n.setTitle(entity.getName());
                                n.setData(entity);
                                n.setKey(k);
                                nearBizOrgEleId = entity.getId();
                                bizOrgUuid = entity.getBizOrgUuid();
                                n.setKeyPath(key);
                                if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                                    OrgElementI18nEntity i18nEntity = orgElementI18nService.getOrgElementI18n(entity.getUuid(), "name", LocaleContextHolder.getLocale().toString());
                                    if (i18nEntity != null && StringUtils.isNotBlank(i18nEntity.getContent())) {
                                        n.setTitle(i18nEntity.getContent());
                                        entity.setName(i18nEntity.getContent());
                                    }
                                }
                                titlePath.add(n.getTitle());
                            }
                        } else if (k.startsWith(IdPrefix.USER.getValue() + Separator.UNDERLINE.getValue())) {
                            Map<String, Object> p = Maps.newHashMap();
                            p.put("userId", k);
                            StringBuilder sql = new StringBuilder("SELECT U.LOGIN_NAME,U.USER_ID,U.AVATAR,U.GENDER,U.USER_NAME, U.PIN_YIN");
                            if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                                sql.append(", I.USER_NAME AS IUSER_NAME");
                            }
                            sql.append(" from user_info U ");
                            if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                                p.put("locale", LocaleContextHolder.getLocale().toString());
                                sql.append(" left join user_name_i18n I on I.user_uuid = U.uuid and I.locale=:locale");
                            }
                            sql.append(" where U.user_id = :userId ");
                            List<QueryItem> items = orgUserService.listQueryItemBySQL(sql.toString(), p, null);
                            if (CollectionUtils.isNotEmpty(items)) {
                                String userName = StringUtils.defaultIfBlank(items.get(0).getString("iuserName"), items.get(0).getString("userName"));
                                titlePath.add(userName);
                                n.setTitle(userName);
                                n.setData(items.get(0));
                                n.setType("user");
                                n.setKey(k);
                                n.setKeyPath(key);
                            }
                        } else {
                            // 业务角色
                            BizOrgRoleEntity roleEntity = bizOrgRoleService.getByIdAndBizOrgUuid(k, bizOrgUuid);
                            n.setTitle(roleEntity.getName());
                            n.setData(roleEntity);
                            n.setType("bizRole");
                            n.setKey(nearBizOrgEleId + Separator.SLASH.getValue() + k);
                            n.setKeyPath(nearBizOrgEleId + Separator.SLASH.getValue() + k);
                            if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                                OrgElementI18nEntity i18nEntity = orgElementI18nService.getOrgElementI18n(roleEntity.getUuid(), "name", LocaleContextHolder.getLocale().toString());
                                if (i18nEntity != null && StringUtils.isNotBlank(i18nEntity.getContent())) {
                                    n.setTitle(i18nEntity.getContent());
                                    roleEntity.setName(i18nEntity.getContent());
                                }
                            }
                            titlePath.add(n.getTitle());
                        }
                    }
                    n.setTitlePath(StringUtils.join(titlePath, Separator.SLASH.getValue()));
                    nodes.add(n);
                    keyIterator.remove();
                    continue;
                }
                OrgVersionEntity v = null;
                String[] keyParts = key.split(Separator.SLASH.getValue());
                List<OrgVersionEntity> versions = Lists.newArrayList();
                boolean prefixVersion = false;
                // 确定版本:
                if (keyParts[0].startsWith(IdPrefix.ORG_VERSION.getValue() + Separator.UNDERLINE.getValue())) {
                    // 指定版本号
                    String orgVersionId = keyParts[0];
                    if (!idVersions.containsKey(orgVersionId)) {
                        idVersions.put(orgVersionId, orgVersionService.getById(orgVersionId));
                    }
                    v = idVersions.get(orgVersionId);
                    versions.add(v);
                    prefixVersion = true;
                } else if (StringUtils.isNotBlank(params.getOrgVersionId())) {
                    if (!idVersions.containsKey(params.getOrgVersionId())) {
                        idVersions.put(params.getOrgVersionId(), orgVersionService.getById(params.getOrgVersionId()));
                    }
                    v = idVersions.get(params.getOrgVersionId());
                    versions.add(v);
                }
                List<OrgVersionEntity> forList = v == null ? allVersions : versions;
                for (OrgVersionEntity version : forList) {
                    if (keyParts[keyParts.length - 1].startsWith(IdPrefix.USER.getValue() + Separator.UNDERLINE.getValue())) {
                        String userId = key;
                        if (prefixVersion) {
                            userId = key.replace(keyParts[0] + Separator.SLASH.getValue(), "");
                        }
                        if (userId.indexOf(Separator.SLASH.getValue()) != -1) {
                            Map<String, Object> sqlParams = Maps.newHashMap();
                            // 按用户路径查询
                            StringBuilder sql = new StringBuilder(" SELECT U.LOGIN_NAME,U.USER_ID,U.AVATAR,U.GENDER,U.USER_NAME, U.PIN_YIN,U.USER_NO \n" +
                                    " ,OU.USER_PATH,OU.ORG_ELEMENT_ID ");
                            if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                                sql.append(", I.USER_NAME AS I_USER_NAME ");
                            }
                            sql.append(" FROM USER_INFO U INNER JOIN  ORG_USER OU ON U.USER_ID  = OU.USER_ID ");
                            if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                                sql.append(" LEFT JOIN USER_NAME_I18N I ON I.USER_UUID = U.UUID AND I.LOCALE=:locale ");
                                sqlParams.put("locale", LocaleContextHolder.getLocale().toString());
                            }
                            sql.append(" WHERE  OU.USER_PATH=:path and OU.ORG_VERSION_UUID=:orgVersionUuid");
                            sqlParams.put("path", userId);
                            sqlParams.put("orgVersionUuid", version.getUuid());
                            List<QueryItem> queryItems = orgElementService.getDao().listQueryItemBySQL(sql.toString(), sqlParams, null);
                            if (CollectionUtils.isNotEmpty(queryItems)) {
                                QueryItem item = queryItems.get(0);
                                Node n = new Node();
                                item.put("type", "user");
                                n.setKey(item.getString("userId"));
                                n.setTitle(StringUtils.defaultString(item.getString("iUserName"), item.getString("userName")));
                                n.setShortTitle(n.getTitle());
                                n.setKeyPath(item.getString("userPath"));
                                if (StringUtils.isNotBlank(item.getString("orgElementId"))) {
                                    if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                                        n.setTitlePath(orgElementPathService.getLocaleOrgElementPath(item.getString("orgElementId"), version.getUuid(), LocaleContextHolder.getLocale().toString()) + Separator.SLASH.getValue() + n.getTitle());
                                    } else {
                                        OrgElementPathEntity pathEntity = orgElementPathService.getByOrgElementIdAndOrgVersionUuid(item.getString("orgElementId"), version.getUuid());
                                        if (pathEntity != null) {
                                            n.setTitlePath(pathEntity.getCnPath() + Separator.SLASH.getValue() + n.getTitle());
                                        }
                                    }

                                }
                                n.setVersion(version.getId());
                                n.setType("user");
                                n.setData(item);
                                nodes.add(n);
                                keyIterator.remove(); // 移除已匹配到的数据
                                break;
                            }
                        } else {
                            Map<String, Object> sqlParams = Maps.newHashMap();
                            StringBuilder sql = new StringBuilder(" SELECT U.LOGIN_NAME,U.USER_ID,U.AVATAR,U.GENDER,U.USER_NAME, U.PIN_YIN  ");
                            if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                                sql.append(", I.USER_NAME AS I_USER_NAME ");
                            }
                            sql.append(" FROM USER_INFO U ");
                            if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                                sql.append(" LEFT JOIN USER_NAME_I18N I ON I.USER_ID = U.USER_ID AND I.LOCALE=:locale ");
                                sqlParams.put("locale", LocaleContextHolder.getLocale().toString());
                            }
                            sql.append(" WHERE U.USER_ID = :id ");
                            sqlParams.put("id", userId);
                            List<QueryItem> queryItems = orgElementService.getDao().listQueryItemBySQL(sql.toString(), sqlParams, null);
                            // 获取当前组织下发布版本
                            OrgVersionEntity userVersion = version;
                            if (userVersion == null && params.getOrgUuid() != null) {
                                userVersion = orgVersionService.getByStateAndOrgUuid(OrgVersionEntity.State.PUBLISHED, params.getOrgUuid());
                            }
                            if (CollectionUtils.isNotEmpty(queryItems)) {
                                QueryItem item = queryItems.get(0);
                                Node n = new Node();
                                item.put("type", "user");
                                n.setKey(item.getString("userId"));
                                n.setTitle(StringUtils.defaultString(item.getString("iUserName"), item.getString("userName")));
                                OrgUserEntity typeUserEntity = null;
                                if (userVersion != null) {
                                    List<OrgUserEntity> orgUserEntity = orgUserService.listByUserIdsAndOrgVersionUuidAndTypes(Lists.newArrayList(userId), userVersion.getUuid(), Lists.newArrayList(OrgUserEntity.Type.PRIMARY_JOB_USER, OrgUserEntity.Type.SECONDARY_JOB_USER, OrgUserEntity.Type.MEMBER_USER));
                                    if (CollectionUtils.isNotEmpty(orgUserEntity)) {
                                        Map<OrgUserEntity.Type, OrgUserEntity> typeUser = Maps.newHashMap();
                                        for (OrgUserEntity entity : orgUserEntity) {
                                            if (!typeUser.containsKey(entity.getType())) {
                                                typeUser.put(entity.getType(), entity);
                                            }
                                        }
                                        // 优先使用主职
                                        if (typeUser.containsKey(OrgUserEntity.Type.PRIMARY_JOB_USER)) {
                                            typeUserEntity = typeUser.get(OrgUserEntity.Type.PRIMARY_JOB_USER);
                                        } else if (typeUser.containsKey(OrgUserEntity.Type.SECONDARY_JOB_USER)) {
                                            typeUserEntity = typeUser.get(OrgUserEntity.Type.SECONDARY_JOB_USER);
                                        } else if (typeUser.containsKey(OrgUserEntity.Type.MEMBER_USER)) {
                                            typeUserEntity = typeUser.get(OrgUserEntity.Type.MEMBER_USER);
                                        }
                                        if (typeUserEntity != null) {
                                            n.setKeyPath(typeUserEntity.getUserPath());
                                            if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                                                n.setTitlePath(orgElementPathService.getLocaleOrgElementPath(typeUserEntity.getOrgElementId(), version.getUuid(), LocaleContextHolder.getLocale().toString()) + Separator.SLASH.getValue() + n.getTitle());
                                            } else {
                                                OrgElementPathEntity pathEntity = orgElementPathService.getByOrgElementIdAndOrgVersionUuid(typeUserEntity.getOrgElementId(), userVersion.getUuid());
                                                if (pathEntity != null) {
                                                    n.setTitlePath(pathEntity.getCnPath() + Separator.SLASH.getValue() + n.getTitle());
                                                }
                                            }
                                        }
                                    }
                                }
                                if (typeUserEntity == null) {
                                    // FIXME: 是否获取其他版本的用户工作信息代入
                                    Map<String, Object> param = Maps.newHashMap();
                                    param.put("userId", userId);
                                    List<QueryItem> orgUserItems = orgUserService.listQueryItemBySQL("select org_element_id , org_version_uuid from org_user o" +
                                            " where o.user_id=:userId and org_element_id is not null ", param, null);
                                    if (CollectionUtils.isNotEmpty(orgUserItems)) {
                                        OrgElementPathEntity pathEntity = null;
                                        if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                                            pathEntity = orgElementPathService.getByOrgElementIdAndOrgVersionUuid(orgUserItems.get(0).getString("orgElementId"), orgUserItems.get(0).getLong("orgVersionUuid"), LocaleContextHolder.getLocale().toString());
                                        } else {
                                            pathEntity = orgElementPathService.getByOrgElementIdAndOrgVersionUuid(orgUserItems.get(0).getString("orgElementId"), orgUserItems.get(0).getLong("orgVersionUuid"));
                                        }
                                        if (pathEntity != null) {
                                            n.setTitlePath(pathEntity.getCnPath() + Separator.SLASH.getValue() + n.getTitle());
                                            n.setKeyPath(pathEntity.getIdPath() + Separator.SLASH.getValue() + n.getKey());
                                        }
                                    }
                                }

                                n.setShortTitle(n.getTitle());
                                n.setVersion(userVersion != null ? userVersion.getId() : null);
                                n.setType("user");
                                n.setData(item);
                                nodes.add(n);
                                keyIterator.remove();
                                break;
                            }
                        }


                    } else {
                        // 组织元素
                        StringBuilder sql = new StringBuilder("SELECT O.UUID,O.ORG_VERSION_ID,O.ORG_VERSION_UUID,O.PARENT_UUID,O.ID,P.ID_PATH, O.NAME,O.SHORT_NAME,P.CN_PATH,P.PIN_YIN_PATH,O.SEQ,P.LEAF,O.TYPE ");
                        if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                            sql.append(", N_I18N.CONTENT AS NAME_I18N , S_I18N.CONTENT AS SHORT_NAME_I18N ");
                        }
                        sql.append(" FROM ORG_ELEMENT O INNER JOIN ORG_ELEMENT_PATH P ON P.ORG_ELEMENT_ID = O.ID  ");
                        if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                            sql.append(" LEFT JOIN ORG_ELEMENT_I18N N_I18N ON N_I18N.DATA_CODE = 'name' AND N_I18N.DATA_UUID = O.UUID AND N_I18N.LOCALE ='" + LocaleContextHolder.getLocale().toString() + "' ");
                            sql.append(" LEFT JOIN ORG_ELEMENT_I18N S_I18N ON S_I18N.DATA_CODE = 'short_name' AND S_I18N.DATA_UUID = O.UUID AND S_I18N.LOCALE ='" + LocaleContextHolder.getLocale().toString() + "' ");
                        }
                        sql.append(" WHERE O.ORG_VERSION_UUID = P.ORG_VERSION_UUID  AND O.ID = :id  and O.TENANT = :tenant ");


                        Map<String, Object> sqlParams = Maps.newHashMap();
                        String system = RequestSystemContextPathResolver.system();
                        if (StringUtils.isNotBlank(system)) {
                            sqlParams.put("system", RequestSystemContextPathResolver.system());
                            sql.append(" AND O.SYSTEM=:system ");
                        }
                        sqlParams.put("tenant", SpringSecurityUtils.getCurrentTenantId());
                        if (version != null) {
                            sqlParams.put("orgVersionUuid", version.getUuid());
                            sql.append(" AND O.ORG_VERSION_UUID=:orgVersionUuid ");
                        } else {
                            sql.append(" AND EXISTS ( SELECT 1 FROM ORG_VERSION V WHERE V.UUID = O.ORG_VERSION_UUID AND V.STATE= 0 ) ");
                        }
                        sqlParams.put("id", keyParts[keyParts.length - 1]);
                        List<QueryItem> queryItems = orgElementService.getDao().listQueryItemBySQL(sql.toString(), sqlParams, null);
                        if (CollectionUtils.isNotEmpty(queryItems)) {
                            QueryItem item = queryItems.get(0);
                            Node n = new Node();
                            item.put("uuid", item.getLong("uuid").toString());
                            n.setKey(item.getString("id"));
                            n.setTitle(StringUtils.defaultString(item.getString("nameI18n"), item.getString("name")));
                            n.setShortTitle(StringUtils.defaultString(item.getString("shortNameI18n"), item.getString("shortName")));
                            idI18ns.put(n.getKey(), item.getString("nameI18n"));
                            n.setType(item.getString("type"));
                            n.setData(item);
                            n.setKeyPath(item.getString("idPath"));
                            n.setTitlePath(item.getString("cnPath"));
                            n.setIsLeaf(item.getInt("leaf") == 1);
                            n.setParentKey(item.getLong("parentUuid") != null ? item.getLong("parentUuid").toString() : null);
                            nodes.add(n);
                            keyIterator.remove();
                            if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                                n.setTitlePath(orgElementPathService.getLocaleOrgElementPath(n.getKey(), item.getLong("orgVersionUuid"), LocaleContextHolder.getLocale().toString()));
                            }

                            break;
                        }


                    }


                }


            }


        }

        return nodes;
    }

    @Override
    public PageNode fetchUser(Params params) {
//        if (params.containsKey("bizOrgUuid") && StringUtils.isNotBlank(params.get("bizOrgUuid").toString())) {
//            return this.fetchBizUser(params);
//        }
        OrgVersionEntity version = null;
        Map<String, Object> sqlParams = Maps.newHashMap();
        // 未指定组织版本号时候，取当前组织的发布版
        if (StringUtils.isBlank(params.getOrgVersionId())) {
            // 指定组织版本
            if (params.getOrgUuid() != null) {
                version = orgVersionService.getByStateAndOrgUuid(OrgVersionEntity.State.PUBLISHED, params.getOrgUuid());
            } else {
                // 默认组织版本
                version = orgVersionService.getDefaultOrgVersionBySystem(null);
            }
        } else {
            version = orgVersionService.getById(params.getOrgVersionId());
        }
        if (version != null) {
            sqlParams.put("orgVersionUuid", version.getUuid());
        } else {
            throw new RuntimeException("未找到相关组织版本");
        }
        // 指定节点下的用户
        String orgElementId = params.getOrgElementId();
        sqlParams.put("orgElementId", orgElementId);
        Map<String, Object> excludeKeyFilterResult = extractIds(params.getExcludeKeys());
        Map<String, Object> includeKeyFilterResult = extractIds(params.getIncludeKeys());
        Set<String> excludeUserIds = (Set<String>) excludeKeyFilterResult.get("filterUserIds");
        Set<String> includeUserIds = (Set<String>) includeKeyFilterResult.get("filterUserIds");
        Set<String> includeOrgElementIds = (Set<String>) includeKeyFilterResult.get("filterElementIds");
        Set<String> excludeOrgElementIds = (Set<String>) excludeKeyFilterResult.get("filterElementIds");
        Map<String, Set<String>> includeUserUnderElements = (Map<String, Set<String>>) includeKeyFilterResult.get("filterUserUnderElementIds");
        Map<String, Set<String>> excludeUserUnderElements = (Map<String, Set<String>>) excludeKeyFilterResult.get("filterUserUnderElementIds");
        if (!includeUserIds.isEmpty()) {
            // 筛选包含用户的组织节点
            List<OrgUserEntity> includeOrgUser = orgUserService.listByUserIdsAndOrgVersionUuidAndTypes(Lists.newArrayList(includeUserIds),
                    version.getUuid(), Lists.newArrayList(OrgUserEntity.Type.PRIMARY_JOB_USER, OrgUserEntity.Type.SECONDARY_JOB_USER));
            if (CollectionUtils.isNotEmpty(includeOrgUser)) {
                for (OrgUserEntity o : includeOrgUser) {
                    if (!includeUserUnderElements.containsKey(o.getOrgElementId())) {
                        includeUserUnderElements.put(o.getOrgElementId(), Sets.newHashSet());
                    }
                    includeUserUnderElements.get(o.getOrgElementId()).add(o.getUserId());
                }
            }
        }

        if (!excludeUserIds.isEmpty()) {
            // 筛选排除用户的组织节点
            List<OrgUserEntity> excludeOrgUser = orgUserService.listByUserIdsAndOrgVersionUuidAndTypes(Lists.newArrayList(excludeUserIds),
                    version.getUuid(), Lists.newArrayList(OrgUserEntity.Type.PRIMARY_JOB_USER, OrgUserEntity.Type.SECONDARY_JOB_USER));
            if (CollectionUtils.isNotEmpty(excludeOrgUser)) {
                for (OrgUserEntity o : excludeOrgUser) {
                    if (!excludeUserUnderElements.containsKey(o.getOrgElementId())) {
                        excludeUserUnderElements.put(o.getOrgElementId(), Sets.newHashSet());
                    }
                    excludeUserUnderElements.get(o.getOrgElementId()).add(o.getUserId());
                }
            }
        }


        // 查询组织下的用户
        StringBuilder sql = new StringBuilder(" from USER_INFO U  WHERE   " +
                " EXISTS ( SELECT 1 FROM ORG_USER E WHERE E.USER_ID = U.USER_ID AND E.ORG_VERSION_UUID = :orgVersionUuid " +
                (StringUtils.isNotBlank(orgElementId) ? " AND (E.ORG_ELEMENT_ID =:orgElementId " +
                        "OR EXISTS ( SELECT 1 FROM ORG_ELEMENT_PATH_CHAIN C WHERE C.SUB_ORG_ELEMENT_ID=E.ORG_ELEMENT_ID AND C.ORG_ELEMENT_ID=:orgElementId AND C.ORG_VERSION_UUID = E.ORG_VERSION_UUID  ) ) " : "") +
                ")" +
                (params.getKeyword() != null ? " AND ( U.PIN_YIN LIKE :keyword OR U.USER_NAME LIKE :keyword OR U.USER_NO LIKE :keyword )" : "")
        );
        if (!excludeUserIds.isEmpty()) {
            sql.append(" AND U.USER_ID NOT IN (:excludeUserIds) ");
            sqlParams.put("excludeUserIds", excludeUserIds);
        }


        if (!excludeOrgElementIds.isEmpty()) {
            // 要排除这个节点下的用户
            sql.append(" AND EXISTS ( SELECT 1 FROM ORG_USER E WHERE E.USER_ID = U.USER_ID AND E.ORG_VERSION_UUID = :orgVersionUuid AND ( E.ORG_ELEMENT_ID NOT IN (:excludeOrgElementIds) " +
                    "AND EXISTS ( SELECT 1 FROM ORG_ELEMENT_PATH_CHAIN C WHERE C.SUB_ORG_ELEMENT_ID=E.ORG_ELEMENT_ID AND C.ORG_ELEMENT_ID NOT IN (:excludeOrgElementIds) AND C.ORG_VERSION_UUID = E.ORG_VERSION_UUID )  )  ) ");
            sqlParams.put("excludeOrgElementIds", excludeOrgElementIds);
        }

        if (!includeUserIds.isEmpty() || !includeOrgElementIds.isEmpty()) {
            sql.append(" AND ( ");
            Set<String> includeUserIdUnderOrgEleIdSet = includeUserUnderElements.keySet();
            if (!includeUserIds.isEmpty()) {
                sqlParams.put("includeUserIds", includeUserIds);
                sqlParams.put("includeUserUnderOrgEleIds", includeUserIdUnderOrgEleIdSet);
                // 包含用户，仅限在用户的归属节点里，不影响其他节点
                sql.append(" (  U.USER_ID IN (:includeUserIds) AND EXISTS ( SELECT 1 FROM ORG_USER E WHERE E.USER_ID = U.USER_ID AND E.ORG_VERSION_UUID =:orgVersionUuid" +
                        " AND E.ORG_ELEMENT_ID IN (:includeUserUnderOrgEleIds) ) ) ");
            } else {
                sql.append(" 1=1 ");
            }

            if (CollectionUtils.isNotEmpty(includeUserIdUnderOrgEleIdSet)) {
                // 用户在指定的节点下，要排除 includeUserUnderOrgEleIds 上述指定用户展示的节点
                CollectionUtils.removeAll(includeOrgElementIds, includeUserIdUnderOrgEleIdSet);
            }

            if (!includeOrgElementIds.isEmpty()) {
                sql.append(CollectionUtils.isNotEmpty(includeUserIdUnderOrgEleIdSet) ? " OR " : " AND ");
                sqlParams.put("includeOrgElementIds", includeOrgElementIds);
                // 筛选有层级关系的节点
                List<QueryItem> subItems = this.orgElementPathService.getDao().listQueryItemBySQL("SELECT CC.SUB_ORG_ELEMENT_ID ,CC.ORG_ELEMENT_ID FROM ORG_ELEMENT_PATH_CHAIN CC WHERE" +
                        " CC.ORG_ELEMENT_ID IN (:includeOrgElementIds)" +
                        " AND CC.SUB_ORG_ELEMENT_ID IN (:includeOrgElementIds)", sqlParams, null);
                List<String> chainIncludeIds = Lists.newArrayList(includeOrgElementIds);
                if (CollectionUtils.isNotEmpty(subItems)) {
                    for (QueryItem i : subItems) {
                        chainIncludeIds.remove(i.getString("orgElementId"));
                    }
                }
                sqlParams.put("chainIncludeIds", chainIncludeIds);
                sql.append(" EXISTS ( SELECT 1 FROM ORG_USER E WHERE E.USER_ID = U.USER_ID AND E.ORG_VERSION_UUID = :orgVersionUuid AND" + (CollectionUtils.isEmpty(includeUserIdUnderOrgEleIdSet) ? "" : " E.ORG_ELEMENT_ID NOT IN (:includeUserUnderOrgEleIds) AND ") +
                        " ( E.ORG_ELEMENT_ID IN (:includeOrgElementIds) " +
                        "OR EXISTS ( SELECT 1 FROM ORG_ELEMENT_PATH_CHAIN C WHERE C.SUB_ORG_ELEMENT_ID=E.ORG_ELEMENT_ID AND C.ORG_ELEMENT_ID  IN (:chainIncludeIds)" +
                        " AND C.ORG_VERSION_UUID = E.ORG_VERSION_UUID )  )  ) ");
            }
            sql.append(" ) ");
        }

        if (params.get("letter") != null) {
            if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                sql.append(" AND ( U.PIN_YIN LIKE :letter OR U.USER_NO LIKE :keyword ) ");
            } else {
                sql.append(" AND ( U.USER_NAME LIKE :letter OR U.USER_NO LIKE :keyword ) ");
            }
            // 按字母前缀查询
            sqlParams.put("letter", params.get("letter").toString() + "%");
        }
        if (params.getKeyword() != null) {
            sqlParams.put("keyword", "%" + params.get("keyword").toString() + "%");

        }
        PagingInfo pageInfo = null;
        Integer pageSize = params.getPageSize();
        Integer pageIndex = params.getPageIndex();
        long total = 0L;
        if (pageSize != null && pageIndex != null) {
            if (pageIndex == 0) {
                // 第一页加载时候下，需要判断用户基数，如果超过指定值，则必须异步分页加载
                int max = 500;
                OrgSettingEntity settingEntity = orgSettingService.getOrgSettingBySystemTenantAndAttrKey(version.getSystem(), version.getTenant(), "MY_ORG_USER_FORCE_ASYNC_FETCH_THRESHOLD");
                if (settingEntity != null && StringUtils.isNotBlank(settingEntity.getAttrVal())) {
                    JSONObject obj = JSONObject.fromObject(settingEntity.getAttrVal());
                    if (obj != null && obj.containsKey("num")) {
                        max = obj.optInt("num", max);
                    }
                }
                total = orgElementService.getDao().countBySQL("SELECT count(1)  " + sql.toString(), sqlParams);
                if (total > max) {
                    // 超过限制进行分页加载，否则全量加载
                    pageInfo = new PagingInfo(pageIndex, pageSize);
                }
                // 否则单页显示全部
            } else {
                pageInfo = new PagingInfo(pageIndex, pageSize);
                total = orgElementService.getDao().countBySQL("SELECT count(1)  " + sql.toString(), sqlParams);
            }
        }
        List<QueryItem> userQueryItems = orgElementService.getDao().listQueryItemBySQL(" SELECT U.LOGIN_NAME,U.USER_ID,U.AVATAR,U.GENDER,U.USER_NAME, U.PIN_YIN, U.USER_NO ,U.CEIL_PHONE_NUMBER , U.MAIL " +
                sql.toString() + " ORDER BY U.USER_NO ASC, U.PIN_YIN ASC ", sqlParams, pageInfo);
        List<Node> userNodes = Lists.newArrayList();
        List<String> userIds = Lists.newArrayList();
        Map<String, Node> userMap = Maps.newHashMap();
        int index = 0;
        for (QueryItem item : userQueryItems) {
            Node n = new Node();
            item.put("type", "user");
            n.setKey(item.getString("userId"));
            n.setTitle(item.getString("userName"));
            n.setShortTitle(n.getTitle());
            n.setVersion(version.getId());
            n.setType("user");
            n.setData(item);
            n.setIsLeaf(true);
            userNodes.add(n);
            userIds.add(n.getKey());
            userMap.put(n.getKey(), n);
        }
        // 查询用户的职位归属信息
        if (!userIds.isEmpty()) {
            List<QueryItem> orgUserItems = Lists.newArrayList();
            Map<String, Object> orgParams = Maps.newHashMap();
            orgParams.put("orgVersionUuid", version.getUuid());
            orgParams.put("orgElementId", params.getOrgElementId());
            ListUtils.handleSubList(userIds, 200, list -> {
                orgParams.put("userIds", list);
                orgUserItems.addAll(orgUserService.listQueryItemBySQL("select o.user_id ,o.org_element_id ,o.type ,o.user_path,e.id_path,e.cn_path from org_user o,org_element_path e where o.user_id in (:userIds)  " +
                        "and o.org_element_id = e.org_element_id and o.org_version_uuid=:orgVersionUuid order by o.type asc", orgParams, null));
            });
            if (CollectionUtils.isNotEmpty(orgUserItems)) {
                Map<OrgUserEntity.Type, OrgUserEntity> typeUser = Maps.newHashMap();
                Map<String, QueryItem> orgUserMap = Maps.newHashMap();
                Map<String, QueryItem> orgElementUserMap = Maps.newHashMap();
                for (QueryItem item : orgUserItems) {
                    if (StringUtils.isNotBlank(orgElementId) && item.getString("idPath").indexOf(orgElementId) == -1) {
                        continue;
                    }
                    if (excludeUserUnderElements.containsKey(item.getString("orgElementId")) && excludeUserUnderElements.get(item.getString("orgElementId")).contains(item.getString("userId"))) {
                        continue;
                    }

                    // 优先取基于节点的职位信息
                    if (StringUtils.isNotBlank(orgElementId)) {
                        if (orgElementUserMap.containsKey(item.getString("userId"))) {
                            // 要比较用户距离节点
                            QueryItem saved = orgElementUserMap.get(item.getString("userId"));
                            String[] savedPath = saved.getString("idPath").split(Separator.SLASH.getValue());
                            ArrayUtils.reverse(savedPath);
                            String[] itemPath = item.getString("idPath").split(Separator.SLASH.getValue());
                            ArrayUtils.reverse(itemPath);
                            if (ArrayUtils.indexOf(itemPath, orgElementId) < ArrayUtils.indexOf(savedPath, orgElementId)) {
                                orgElementUserMap.put(item.getString("userId"), item);
                            }
                        } else {
                            orgElementUserMap.put(item.getString("userId"), item);
                        }
                    }
                    if (OrgUserEntity.Type.PRIMARY_JOB_USER.ordinal() == item.getInt("type")) {
                        // 优先取主职位信息
                        orgUserMap.put(item.getString("userId"), item);
                    } else {
                        if (!orgUserMap.containsKey(item.getString("userId")) || OrgUserEntity.Type.SECONDARY_JOB_USER.ordinal() == orgUserMap.get(item.getString("userId")).getInt("type")) {
                            orgUserMap.put(item.getString("userId"), item);
                        }
                    }
                }
                Set<Map.Entry<String, QueryItem>> entrySet = orgUserMap.entrySet();
                for (Map.Entry<String, QueryItem> ent : entrySet) {
                    if (userMap.containsKey(ent.getKey())) {
                        userMap.get(ent.getKey()).setKeyPath(orgElementUserMap.containsKey(ent.getKey()) ? orgElementUserMap.get(ent.getKey()).getString("userPath") : ent.getValue().getString("userPath"));
                        userMap.get(ent.getKey()).setTitlePath((orgElementUserMap.containsKey(ent.getKey()) ? orgElementUserMap.get(ent.getKey()).getString("cnPath") : ent.getValue().getString("cnPath")) + Separator.SLASH.getValue() + userMap.get(ent.getKey()).getTitle());
                    }
                }
                Iterator<Node> userNodeIterator = userNodes.iterator();
                while (userNodeIterator.hasNext()) {
                    Node u = userNodeIterator.next();
                    if (StringUtils.isBlank(u.getKeyPath())) {
                        userNodeIterator.remove();
                    }
                }
            }


        }
        return new PageNode(userNodes, total);
    }

    private PageNode fetchBizUser(Params params) {
        Map<String, Object> sqlParams = Maps.newHashMap();
        String orgElementId = params.getOrgElementId();
        Long bizOrgUuid = Long.parseLong(params.get("bizOrgUuid").toString());
        sqlParams.put("orgElementId", orgElementId);
        sqlParams.put("bizOrgUuid", params.get("bizOrgUuid").toString());
        // 查询组织下的用户
        StringBuilder sql = new StringBuilder(" from USER_INFO U  WHERE   " +
                " EXISTS ( SELECT 1 FROM BIZ_ORG_ELEMENT_MEMBER E WHERE E.MEMBER_ID = U.USER_ID AND E.BIZ_ORG_UUID = :bizOrgUuid " +
                (StringUtils.isNotBlank(orgElementId) ? " AND (E.BIZ_ORG_ELEMENT_ID =:orgElementId " +
                        "OR EXISTS ( SELECT 1 FROM BIZ_ORG_ELEMENT_PATH_CHAIN C WHERE C.SUB_ID=E.BIZ_ORG_ELEMENT_ID AND C.ID=:orgElementId   ) ) " : "") +
                ")" +
                (params.getKeyword() != null ? " AND ( U.PIN_YIN LIKE :keyword OR U.USER_NAME LIKE :keyword OR U.USER_NO LIKE :keyword )" : "") +
                (params.get("letter") != null ? " AND U.PIN_YIN LIKE :letter" : "")
        );

        if (params.get("letter") != null) {
            // 按字母前缀查询
            sqlParams.put("letter", params.get("letter").toString() + "%");
        }
        if (params.getKeyword() != null) {
            sqlParams.put("keyword", "%" + params.get("keyword").toString() + "%");

        }
        List<QueryItem> userQueryItems = orgElementService.getDao().listQueryItemBySQL(" SELECT U.LOGIN_NAME,U.USER_ID,U.AVATAR,U.GENDER,U.USER_NAME, U.PIN_YIN " +
                sql.toString() + " ORDER BY U.PIN_YIN ASC ", sqlParams, null);
        List<Node> userNodes = Lists.newArrayList();
        List<String> userIds = Lists.newArrayList();
        Map<String, Node> userMap = Maps.newHashMap();
        for (QueryItem item : userQueryItems) {
            Node n = new Node();
            item.put("type", "user");
            n.setKey(item.getString("userId"));
            n.setTitle(item.getString("userName"));
            n.setShortTitle(n.getTitle());
            n.setVersion(null);
            n.setType("user");
            n.setData(item);
            n.setIsLeaf(true);
            userNodes.add(n);
            userIds.add(n.getKey());
            userMap.put(n.getKey(), n);
        }
        // 查询用户的职位归属信息
        if (!userIds.isEmpty()) {
            List<QueryItem> orgUserItems = Lists.newArrayList();
            Map<String, Object> orgParams = Maps.newHashMap();
            BizOrganizationEntity bizOrganizationEntity = bizOrganizationService.getOne(bizOrgUuid);
            OrgVersionEntity version = orgVersionService.getByStateAndOrgUuid(OrgVersionEntity.State.PUBLISHED, bizOrganizationEntity.getOrgUuid());
            orgParams.put("orgVersionUuid", version.getUuid());
            ListUtils.handleSubList(userIds, 200, list -> {
                orgParams.put("userIds", list);
                orgUserItems.addAll(orgUserService.listQueryItemBySQL("select o.user_id ,o.org_element_id ,o.type ,o.user_path,e.id_path,e.cn_path from org_user o,org_element_path e where o.user_id in (:userIds)  " +
                        "and o.org_element_id = e.org_element_id  and o.org_version_uuid=:orgVersionUuid", orgParams, null));
            });
            if (CollectionUtils.isNotEmpty(orgUserItems)) {
                Map<OrgUserEntity.Type, OrgUserEntity> typeUser = Maps.newHashMap();
                Map<String, QueryItem> orgUserMap = Maps.newHashMap();
                for (QueryItem item : orgUserItems) {
                    if (OrgUserEntity.Type.PRIMARY_JOB_USER.ordinal() == item.getInt("type")) {
                        // 优先取主职位信息
                        orgUserMap.put(item.getString("userId"), item);
                    } else {
                        if (!orgUserMap.containsKey(item.getString("userId")) || OrgUserEntity.Type.SECONDARY_JOB_USER.ordinal() == orgUserMap.get(item.getString("userId")).getInt("type")) {
                            orgUserMap.put(item.getString("userId"), item);
                        }
                    }

                }
                Set<Map.Entry<String, QueryItem>> entrySet = orgUserMap.entrySet();
                for (Map.Entry<String, QueryItem> ent : entrySet) {
                    if (userMap.containsKey(ent.getKey())) {
                        userMap.get(ent.getKey()).setKeyPath(ent.getValue().getString("userPath"));
                        userMap.get(ent.getKey()).setTitlePath(ent.getValue().getString("cnPath") + Separator.SLASH.getValue() + userMap.get(ent.getKey()).getTitle());
                    }
                }
            }


        }
        return new PageNode(userNodes, userQueryItems.size());
    }

    @Override
    public List<Node> fetchByTitles(Params params) {
        OrgVersionEntity version = null;
        // 未指定组织版本号时候，取当前组织的发布版
        boolean canMatchOtherVersion = false;
        if (StringUtils.isBlank(params.getOrgVersionId())) {
            // 指定组织版本
            if (params.getOrgUuid() != null) {
                version = orgVersionService.getByStateAndOrgUuid(OrgVersionEntity.State.PUBLISHED, params.getOrgUuid());
            } else {
                canMatchOtherVersion = true;
                // 默认组织版本
                version = orgVersionService.getDefaultOrgVersionBySystem(RequestSystemContextPathResolver.system());
            }
        } else {
            version = orgVersionService.getById(params.getOrgVersionId());
        }
        if (version == null) {
            throw new RuntimeException("未找到相关组织版本");
        }

        List<String> titles = params.getTitles();
        if (CollectionUtils.isEmpty(titles)) {
            return null;
        }
        List<Node> nodes = Lists.newArrayList();
        for (String title : titles) {
            List<OrgVersionEntity> otherVersions = null;
            if (title.indexOf(Separator.SLASH.getValue()) != -1) {
                // 带路径的标题
                List<Node> nodeList = this.getOrgElementNodesLikeSuffixPath(title, version);
                if (CollectionUtils.isNotEmpty(nodeList)) {
                    nodes.addAll(nodeList);
                    continue;
                }

                if (canMatchOtherVersion && StringUtils.isNotBlank(RequestSystemContextPathResolver.system())) {
                    otherVersions = orgVersionService.getAllPublishedVersionBySystem(RequestSystemContextPathResolver.system());
                    if (CollectionUtils.isNotEmpty(otherVersions)) {
                        boolean getNode = false;
                        for (OrgVersionEntity v : otherVersions) {
                            if (!v.getId().equalsIgnoreCase(version.getId())) {
                                nodeList = this.getOrgElementNodesLikeSuffixPath(title, v);
                                if (CollectionUtils.isNotEmpty(nodeList)) {
                                    nodes.addAll(nodeList);
                                    getNode = true;
                                    break;
                                }
                            }
                        }
                        if (getNode) {
                            continue;
                        }
                    }
                }

                // 判断用户
                nodeList = getOrgUserNodesLikeSuffixUserPath(title, version);
                if (CollectionUtils.isNotEmpty(nodeList)) {
                    nodes.addAll(nodeList);
                    continue;
                }
                if (canMatchOtherVersion && StringUtils.isNotBlank(RequestSystemContextPathResolver.system()) && otherVersions != null) {
                    boolean getNode = false;
                    for (OrgVersionEntity v : otherVersions) {
                        if (!v.getId().equalsIgnoreCase(version.getId())) {
                            nodeList = getOrgUserNodesLikeSuffixUserPath(title, v);
                            if (CollectionUtils.isNotEmpty(nodeList)) {
                                nodes.addAll(nodeList);
                                getNode = true;
                                break;
                            }
                        }
                    }
                    if (getNode) {
                        continue;
                    }
                }

            } else {
                List<Node> nodeList = this.getOrgElementNodesByName(title, version);
                if (CollectionUtils.isNotEmpty(nodeList)) {
                    nodes.addAll(nodeList);
                    continue;
                }

                if (canMatchOtherVersion && StringUtils.isNotBlank(RequestSystemContextPathResolver.system())) {
                    otherVersions = orgVersionService.getAllPublishedVersionBySystem(RequestSystemContextPathResolver.system());
                    if (CollectionUtils.isNotEmpty(otherVersions)) {
                        boolean getNode = false;
                        for (OrgVersionEntity v : otherVersions) {
                            if (!v.getId().equalsIgnoreCase(version.getId())) {
                                nodeList = this.getOrgElementNodesByName(title, v);
                                if (CollectionUtils.isNotEmpty(nodeList)) {
                                    nodes.addAll(nodeList);
                                    getNode = true;
                                    break;
                                }
                            }
                        }
                        if (getNode) {
                            continue;
                        }
                    }
                }

                nodeList = this.getOrgUserNodesByName(title, version);
                if (CollectionUtils.isNotEmpty(nodeList)) {
                    nodes.addAll(nodeList);
                    continue;
                }

                if (canMatchOtherVersion && StringUtils.isNotBlank(RequestSystemContextPathResolver.system()) && otherVersions != null) {

                    for (OrgVersionEntity v : otherVersions) {
                        if (!v.getId().equalsIgnoreCase(version.getId())) {
                            nodeList = this.getOrgUserNodesByName(title, v);
                            if (CollectionUtils.isNotEmpty(nodeList)) {
                                nodes.addAll(nodeList);
                                return nodes;
                            }

                        }
                    }
                }
            }

        }
        return nodes;
    }

    private List<Node> getOrgUserNodesByName(String title, OrgVersionEntity version) {
        Map<String, Object> sqlParams = Maps.newHashMap();
        sqlParams.put("orgVersionUuid", version.getUuid());
        sqlParams.put("userName", title);
        List<OrgUserEntity> orgUserEntities = orgUserService.listByHQL("from OrgUserEntity o where o.orgVersionUuid= :orgVersionUuid and exists (" +
                " select 1 from UserInfoEntity u where u.userName=:userName and u.userId = o.userId " +
                ") order by type asc ", sqlParams);
        List<Node> nodes = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(orgUserEntities)) {
            OrgUserEntity userEntity = null;
            for (OrgUserEntity orgUserEntity : orgUserEntities) {
                if (OrgUserEntity.Type.PRIMARY_JOB_USER.equals(orgUserEntity.getType())) {
                    userEntity = orgUserEntity;
                    break;
                }
                userEntity = orgUserEntity;
            }
            if (userEntity != null) {
                Node n = new Node();
                n.setKey(userEntity.getUserId());
                n.setTitle(title);
                n.setShortTitle(n.getTitle());
                n.setVersion(version.getId());
                n.setType("user");
                n.setKeyPath(userEntity.getUserPath());
                OrgElementPathEntity pathEntity = orgElementPathService.getByIdPathAndOrgVersionUuid(
                        userEntity.getUserPath().substring(0, userEntity.getUserPath().lastIndexOf(Separator.SLASH.getValue())), version.getUuid());
                if (pathEntity != null) {
                    n.setTitlePath(pathEntity.getCnPath() + Separator.SLASH.getValue() + n.getTitle());
                }
                nodes.add(n);
            }
        }
        return nodes;
    }

    private List<Node> getOrgElementNodesByName(String title, OrgVersionEntity version) {
        List<OrgElementEntity> orgElementEntities = orgElementService.getOrgElementsByNameAndVersion(title, version.getUuid());
        List<Node> nodes = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(orgElementEntities)) {
            for (OrgElementEntity elementEntity : orgElementEntities) {
                Node n = new Node();
                n.setKey(elementEntity.getId());
                n.setVersion(version.getId());
                n.setTitle(elementEntity.getName());
                n.setType(elementEntity.getType());
                n.setShortTitle(elementEntity.getShortName());
                OrgElementPathEntity pathEntity = orgElementPathService.getByOrgElementIdAndOrgVersionUuid(elementEntity.getId(), version.getUuid());
                n.setKeyPath(pathEntity.getIdPath());
                n.setTitlePath(pathEntity.getCnPath());
                n.setData(elementEntity);
                nodes.add(n);
            }
            return nodes;
        }
        return null;
    }


    private List<Node> getOrgElementNodesLikeSuffixPath(String suffixPath, OrgVersionEntity v) {
        List<OrgElementPathEntity> orgElementPathEntities = orgElementPathService.getOrgElementPathLikeSuffixPath(suffixPath, v.getUuid());
        List<Node> nodes = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(orgElementPathEntities)) {
            for (OrgElementPathEntity pathEntity : orgElementPathEntities) {
                Node n = new Node();
                n.setKey(pathEntity.getOrgElementId());
                n.setVersion(v.getId());
                OrgElementEntity elementEntity = orgElementService.getByIdAndOrgVersionUuid(pathEntity.getOrgElementId(), v.getUuid());
                n.setTitle(elementEntity.getName());
                n.setType(elementEntity.getType());
                n.setShortTitle(elementEntity.getShortName());
                n.setKeyPath(pathEntity.getIdPath());
                n.setTitlePath(pathEntity.getCnPath());
                n.setData(elementEntity);
                nodes.add(n);
            }
            return nodes;
        }
        return null;
    }

    public List<Node> getOrgUserNodesLikeSuffixUserPath(String suffixPath, OrgVersionEntity version) {
        List<Node> nodes = Lists.newArrayList();
        List<OrgUserEntity> orgUserEntities = orgUserService.getOrgUserLikeSuffixUserPath(suffixPath, version.getUuid());
        if (CollectionUtils.isNotEmpty(orgUserEntities)) {
            OrgUserEntity orgUserEntity = orgUserEntities.get(0);
            Node n = new Node();
            n.setKey(orgUserEntity.getUserId());
            n.setTitle(suffixPath.substring(suffixPath.lastIndexOf(Separator.SLASH.getValue()) + 1));
            n.setShortTitle(n.getTitle());
            n.setVersion(version.getId());
            n.setKeyPath(orgUserEntity.getUserPath());
            OrgElementPathEntity pathEntity = orgElementPathService.getByIdPathAndOrgVersionUuid(
                    orgUserEntity.getUserPath().substring(0, orgUserEntity.getUserPath().lastIndexOf(Separator.SLASH.getValue())), version.getUuid());
            if (pathEntity != null) {
                n.setTitlePath(pathEntity.getCnPath() + Separator.SLASH.getValue() + n.getTitle());
            }
            n.setType("user");
            nodes.add(n);
            return nodes;
        }
        return null;
    }

}
