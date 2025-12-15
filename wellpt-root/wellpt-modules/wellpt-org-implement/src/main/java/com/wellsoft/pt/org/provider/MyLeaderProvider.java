package com.wellsoft.pt.org.provider;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.pt.common.i18n.AppCodeI18nMessageSource;
import com.wellsoft.pt.multi.org.support.dataprovider.OrgSelectProvider;
import com.wellsoft.pt.org.entity.OrgElementModelEntity;
import com.wellsoft.pt.org.entity.OrgUserEntity;
import com.wellsoft.pt.org.entity.OrgVersionEntity;
import com.wellsoft.pt.org.service.OrgElementService;
import com.wellsoft.pt.org.service.OrgUserService;
import com.wellsoft.pt.org.service.OrgVersionService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Description: 我的领导
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年12月12日   chenq	 Create
 * </pre>
 */
@Component
public class MyLeaderProvider implements OrgSelectProvider {
    @Resource
    OrgElementService orgElementService;

    @Resource
    OrgVersionService orgVersionService;

    @Resource
    OrgUserService orgUserService;


    @Override
    public String type() {
        return "MyLeader";
    }


    @Override
    public List<Node> fetch(Params params) {
        OrgVersionEntity version = null;
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
        if (version == null) {
            throw new RuntimeException("未找到相关组织版本");
        }
        Long orgVersionUuid = version.getUuid();
        // 查询全部
        Map<String, Object> sqlParams = Maps.newHashMap();
        sqlParams.put("orgVersionUuid", version.getUuid());
        String userId = SpringSecurityUtils.getCurrentUserId();
        sqlParams.put("userId", userId);
        List<Node> nodes = Lists.newArrayList();
        Node directorNode = new Node();
        directorNode.setKey("1");
        directorNode.setTitle(AppCodeI18nMessageSource.getMessage("organization.deptLeaderNodeTitle", "pt-org", LocaleContextHolder.getLocale().toString(), "部门领导"));
        directorNode.setType("director");
        directorNode.setIsLeaf(false);
        directorNode.setCheckable(false);
        directorNode.setFictional(true);
        directorNode.setChildren(Lists.newArrayList());
        nodes.add(directorNode);
        Node leaderNode = new Node();
        leaderNode.setKey("2");
        leaderNode.setTitle(AppCodeI18nMessageSource.getMessage("organization.ResponsibleLeaderNodeTitle", "pt-org", LocaleContextHolder.getLocale().toString(), "分管领导"));
        leaderNode.setType("leader");
        leaderNode.setIsLeaf(false);
        leaderNode.setCheckable(false);
        leaderNode.setFictional(true);
        leaderNode.setChildren(Lists.newArrayList());
        nodes.add(leaderNode);
        if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
            sqlParams.put("locale", LocaleContextHolder.getLocale().toString());
        }
        List<OrgUserEntity> orgUserEntities = orgUserService.listByHQL("from OrgUserEntity o where o.orgElementType in :types and o.userId=:userId and o.orgVersionUuid=:orgVersionUuid"
                , ImmutableMap.<String, Object>builder().put("userId", userId).put("types", new String[]{OrgElementModelEntity.ORG_DEPT_ID, OrgElementModelEntity.ORG_JOB_ID}).put("orgVersionUuid", orgVersionUuid).build());
        if (CollectionUtils.isEmpty(orgUserEntities)) {
            return nodes;
        }
        Set<String> eleIds = Sets.newHashSet();
        for (OrgUserEntity entity : orgUserEntities) {
            eleIds.add(entity.getOrgElementId());
            if (OrgElementModelEntity.ORG_JOB_ID.equals(entity.getOrgElementType())) {
                // 获取职位的直接部门
                String[] userPaths = entity.getUserPath().split(Separator.SLASH.getValue());
                ArrayUtils.reverse(userPaths);
                for (int i = 1, len = userPaths.length; i < len; i++) {
                    if (userPaths[i].startsWith(IdPrefix.DEPARTMENT.getValue() + Separator.UNDERLINE.getValue())) {
                        eleIds.add(userPaths[i]);
                        break;
                    }
                }
            }
        }
        sqlParams.put("elementIds", eleIds);
        List<QueryItem> queryItems = this.orgElementService.getDao().listQueryItemBySQL("select u.uuid, u.user_name, u.user_id, u.login_name,u.avatar,u.gender,u.pin_yin,u.user_no , u.ceil_phone_number  , u.mail, o.director, o.leader, o.org_element_id" +
                (sqlParams.containsKey("locale") ? " , i.user_name AS i_user_name " : "") +
                "  from user_info u inner join org_element_management o on ( u.user_id = o.director or u.user_id = o.leader )" +
                (sqlParams.containsKey("locale") ? " left join user_name_i18n i on i.user_id = u.user_id and i.locale=:locale " : "") +
                " where o.org_element_id in :elementIds" +
                "   and o.org_version_uuid =:orgVersionUuid" +
                " order by u.user_no asc ", sqlParams, null);


        Set<String> directors = Sets.newHashSet();
        Set<String> leaders = Sets.newHashSet();
        Map<String, List<Node>> userIdNodes = Maps.newHashMap();
        Map<String, List<Node>> userUuidNodes = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(queryItems)) {
            for (QueryItem item : queryItems) {
                Node n = new Node();
                item.put("type", "user");
                n.setKey(item.getString("userId"));
                n.setKeyPath(n.getKey());
                n.setTitle(item.getString("userName"));
                n.setTitle(StringUtils.defaultString(item.getString("iUserName"), item.getString("userName")));
                n.setTitlePath(n.getTitle());
                n.setType("user");
                n.setData(item);
                n.setIsLeaf(true);
                if (!userIdNodes.containsKey(n.getKey())) {
                    userIdNodes.put(n.getKey(), Lists.newArrayList());
                }
                userIdNodes.get(n.getKey()).add(n);

                if (!userUuidNodes.containsKey(item.getString("uuid"))) {
                    userUuidNodes.put(item.getString("uuid"), Lists.newArrayList());
                }
                userUuidNodes.get(item.getString("uuid")).add(n);

                if (item.getString("userId").equalsIgnoreCase(item.getString("leader"))) {
                    // 分管领导
                    if (leaders.add(item.getString("leader"))) {
                        leaderNode.getChildren().add(n);
                    }
                } else {
                    if (directors.add(item.getString("director"))) {
                        // 负责人：部门领导
                        directorNode.getChildren().add(n);
                    }

                }
            }
        }


        // 查询用户在该版本下职位、部门归属
        if (MapUtils.isNotEmpty(userIdNodes)) {
            ListUtils.handleSubList(Lists.newArrayList(userIdNodes.keySet()), 200, list -> {
                List<QueryItem> orgElements = orgElementService.getDao().listQueryItemBySQL(
                        "SELECT OU.USER_ID, OU.TYPE,P.CN_PATH,P.ID_PATH FROM ORG_USER OU INNER JOIN  ORG_ELEMENT_PATH P  ON P.ORG_ELEMENT_ID = OU.ORG_ELEMENT_ID AND OU.ORG_VERSION_UUID = P.ORG_VERSION_UUID  WHERE OU.TYPE =1 AND  OU.USER_ID IN (:userIds) AND OU.ORG_VERSION_UUID=:orgVersionUuid",
                        ImmutableMap.<String, Object>builder().put("userIds", list).put("orgVersionUuid", orgVersionUuid).build(), null);
                if (CollectionUtils.isNotEmpty(orgElements)) {
                    for (QueryItem q : orgElements) {
                        if (q.getInt("type") == OrgUserEntity.Type.PRIMARY_JOB_USER.ordinal()) {
                            List<Node> userNodes = userIdNodes.get(q.getString("userId"));
                            for (Node n : userNodes) {
                                QueryItem item = (QueryItem) n.getData();
                                if (StringUtils.isNotBlank(q.getString("idPath"))) {
                                    String[] keys = q.getString("idPath").split(Separator.SLASH.getValue());
                                    String[] titles = q.getString("cnPath").split(Separator.SLASH.getValue());
                                    for (int i = keys.length - 1; i >= 0; i--) {
                                        if (keys[i].startsWith(IdPrefix.JOB.getValue() + Separator.UNDERLINE.getValue())) {
                                            item.put("job_id", keys[i]);
                                            item.put("job_name", titles[i]);
                                        } else if (keys[i].startsWith(IdPrefix.DEPARTMENT.getValue() + Separator.UNDERLINE.getValue())) {
                                            item.put("dept_id", keys[i]);
                                            item.put("dept_name", titles[i]);
                                        } else if (keys[i].startsWith(IdPrefix.SYSTEM_UNIT.getValue() + Separator.UNDERLINE.getValue())) {
                                            item.put("unit_id", keys[i]);
                                            item.put("unit_name", titles[i]);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            });
        }
        // 查询用户扩展信息
        String userAttrSql = "select attr_key,attr_value,user_uuid from USER_INFO_EXT where user_uuid in :userUuid ";
        if (MapUtils.isNotEmpty(userUuidNodes)) {
            ListUtils.handleSubList(Lists.newArrayList(userUuidNodes.keySet()), 200, list -> {
                List<QueryItem> userAttrsQueryItems = orgElementService.getDao().listQueryItemBySQL(userAttrSql,
                        ImmutableMap.<String, Object>builder().put("userUuid", list).build(), null);
                if (CollectionUtils.isNotEmpty(userAttrsQueryItems)) {
                    for (QueryItem q : userAttrsQueryItems) {
                        if (userUuidNodes.containsKey(q.getString("userUuid"))) {
                            for (Node n : userUuidNodes.get(q.getString("userUuid"))) {
                                ((QueryItem) n.getData()).put(q.getString("attrKey"), q.getString("attrValue"), false);
                            }
                        }
                    }
                }
            });
        }
        return nodes;

    }


    @Override
    public List<Node> fetchByKeys(Params params) {
        if (CollectionUtils.isNotEmpty(params.getKeys())) {
            OrgVersionEntity version = orgVersionService.getByStateAndOrgUuid(OrgVersionEntity.State.PUBLISHED, params.getOrgUuid());

            List<Node> nodes = Lists.newArrayList();
            StringBuilder sql = new StringBuilder("SELECT O.UUID,O.ORG_VERSION_ID,O.ORG_VERSION_UUID,O.PARENT_UUID,O.ID,P.ID_PATH, O.NAME,O.SHORT_NAME,P.CN_PATH,P.PIN_YIN_PATH,O.SEQ,P.LEAF,O.TYPE " +
                    "FROM ORG_ELEMENT O , ORG_ELEMENT_PATH P WHERE O.ORG_VERSION_UUID = P.ORG_VERSION_UUID AND P.ORG_ELEMENT_ID = O.ID AND O.ID IN (:ids) AND O.ORG_VERSION_UUID=:orgVersionUuid");
            Map<String, Object> sqlParams = Maps.newHashMap();
            sqlParams.put("orgVersionUuid", version.getUuid());

            Set<String> keys = Sets.newHashSetWithExpectedSize(100);

            for (int i = 0; i < params.getKeys().size(); i++) {
                keys.add(params.getKeys().get(i));
                if ((i + 1) % 100 == 0 || i == params.getKeys().size() - 1) {
                    sqlParams.put("ids", keys);
                    List<QueryItem> queryItems = orgElementService.getDao().listQueryItemBySQL(sql.toString(), sqlParams, null);
                    keys.clear();
                    for (QueryItem item : queryItems) {
                        Node n = new Node();
                        item.put("uuid", item.getLong("uuid").toString());
                        n.setKey(item.getString("id"));
                        n.setTitle(item.getString("name"));
                        n.setType(item.getString("type"));
                        n.setData(item);
                        n.setIsLeaf(item.getInt("leaf") == 1);
                        n.setParentKey(item.getLong("parentUuid") != null ? item.getLong("parentUuid").toString() : null);
                        nodes.add(n);
                    }
                }
            }
            return nodes;
        }

        return null;
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
