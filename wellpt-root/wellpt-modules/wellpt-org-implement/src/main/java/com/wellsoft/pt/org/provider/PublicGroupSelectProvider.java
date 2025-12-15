package com.wellsoft.pt.org.provider;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.pt.org.entity.*;
import com.wellsoft.pt.org.service.*;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.user.service.UserInfoService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年05月15日   chenq	 Create
 * </pre>
 */
@Component
public class PublicGroupSelectProvider extends MyOrgSelectProvider {

    @Autowired
    private OrgGroupService orgGroupService;

    @Autowired
    private OrgGroupMemberService orgGroupMemberService;

    @Autowired
    private OrgUserService orgUserService;

    @Autowired
    private OrgElementService orgElementService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private OrgVersionService orgVersionService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private OrgElementI18nService orgElementI18nService;

    @Override
    public String type() {
        return "PublicGroup";
    }

    @Override
    public List<Node> fetch(Params params) {
        String system = RequestSystemContextPathResolver.system();
        String tenant = SpringSecurityUtils.getCurrentTenantId();
        Map<String, Object> sqlParams = Maps.newHashMap();
        if (StringUtils.isBlank(system) && (params.getOrgUuid() != null || StringUtils.isNotBlank(params.getOrgVersionId()))) {
            // 根据组织参数决定系统
            if (params.getOrgUuid() != null) {
                OrganizationEntity organizationEntity = organizationService.getOne(params.getOrgUuid());
                system = organizationEntity.getSystem();
            } else if (StringUtils.isNotBlank(params.getOrgVersionId())) {
                OrgVersionEntity versionEntity = orgVersionService.getById(params.getOrgVersionId());
                system = versionEntity.getSystem();
            }
        }
        sqlParams.put("system", system);
        sqlParams.put("tenant", tenant);
        StringBuilder sql = new StringBuilder(" from OrgGroupEntity where 1=1");
        if (StringUtils.isNotBlank(system)) {
            sql.append(" and system=:system and tenant=:tenant ");
        }
        sql.append(" order by createTime desc ");
        List<OrgGroupEntity> groupEntities = orgGroupService.listByHQL(sql.toString(), sqlParams);
        List<Node> nodes = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(groupEntities)) {
            for (OrgGroupEntity entity : groupEntities) {
                Node n = this.convertGroupToNode(entity);
//                n.setAsyncPreviewUser(true);
                nodes.add(n);
                // 查找成员
                List<String> memberIds = orgGroupMemberService.getMemberIdsByGroupUuid(entity.getUuid());
                if (CollectionUtils.isNotEmpty(memberIds)) {
                    n.setIsLeaf(false);
                    n.setChildren(Lists.newArrayList());
                    // 筛选出其下的成员
                    List<String> userIds = Lists.newArrayList();
                    List<String> orgElementIds = Lists.newArrayList();
                    memberIds.forEach(m -> {
                        if (m.startsWith(IdPrefix.USER.getValue() + Separator.UNDERLINE.getValue())) {
                            userIds.add(m);
                        } else {
                            orgElementIds.add(m);
                        }
                    });

                    OrgVersionEntity defaultVersion = orgVersionService.getDefaultOrgVersionBySystem(system);
                    if (!userIds.isEmpty()) {
                        sqlParams.put("userIds", userIds);
                        List<QueryItem> userQueryItems = orgElementService.getDao().listQueryItemBySQL(" SELECT U.UUID, U.LOGIN_NAME,U.USER_ID,U.AVATAR,U.GENDER,U.USER_NAME, U.PIN_YIN ,U.USER_NO, U.CEIL_PHONE_NUMBER, U.MAIL " +
                                "FROM USER_INFO U WHERE U.USER_ID IN (:userIds) ORDER BY U.PIN_YIN ASC ", sqlParams, null);
                        Map<String, Node> userMap = Maps.newHashMap();
                        List<String> userNodeIds = Lists.newArrayList();
                        Map<String, QueryItem> userUuidMap = Maps.newHashMap();
                        for (QueryItem item : userQueryItems) {
                            Node u = new Node();
                            item.put("type", "user");
                            u.setKey(item.getString("userId"));
                            u.setTitle(item.getString("userName"));
                            u.setShortTitle(n.getTitle());
                            u.setType("user");
                            u.setData(item);
                            u.setIsLeaf(true);
                            n.getChildren().add(u);
                            userNodeIds.add(n.getKey());
                            userMap.put(n.getKey(), n);
                            userUuidMap.put(item.getString("uuid"), item);
                        }

                        // 查询用户的职位归属信息
                        if (!userNodeIds.isEmpty()) {
                            List<QueryItem> orgUserItems = Lists.newArrayList();
                            Map<String, Object> orgParams = Maps.newHashMap();
                            orgParams.put("orgVersionUuid", defaultVersion.getUuid());
                            orgParams.put("orgElementId", params.getOrgElementId());
                            ListUtils.handleSubList(userNodeIds, 200, list -> {
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
                                        QueryItem item = (QueryItem) userMap.get(ent.getKey()).getData();
                                        if (org.apache.commons.lang3.StringUtils.isNotBlank(ent.getValue().getString("idPath"))) {
                                            String[] keys = ent.getValue().getString("idPath").split(Separator.SLASH.getValue());
                                            String[] titles = ent.getValue().getString("cnPath").split(Separator.SLASH.getValue());
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


                            // 查询用户扩展信息
                            String userAttrSql = "select attr_key,attr_value,user_uuid from USER_INFO_EXT where user_uuid in :userUuid ";
                            ListUtils.handleSubList(Lists.newArrayList(userUuidMap.keySet()), 200, list -> {
                                List<QueryItem> userAttrsQueryItems = orgElementService.getDao().listQueryItemBySQL(userAttrSql,
                                        ImmutableMap.<String, Object>builder().put("userUuid", list).build(), null);
                                if (CollectionUtils.isNotEmpty(userAttrsQueryItems)) {
                                    for (QueryItem q : userAttrsQueryItems) {
                                        QueryItem item = userUuidMap.get(q.getString("userUuid"));
                                        item.put(q.getString("attrKey"), q.getString("attrValue"), false);
                                    }
                                }
                            });
                        }
                    }

                    if (!orgElementIds.isEmpty()) {
                        // 组织元素
                        sql = new StringBuilder("SELECT O.UUID,O.ORG_VERSION_ID,O.ORG_VERSION_UUID,O.PARENT_UUID,O.ID,P.ID_PATH, O.NAME,O.SHORT_NAME,P.CN_PATH,P.PIN_YIN_PATH,O.SEQ,P.LEAF,O.TYPE " +
                                "FROM ORG_ELEMENT O , ORG_ELEMENT_PATH P WHERE O.ORG_VERSION_UUID = P.ORG_VERSION_UUID AND P.ORG_ELEMENT_ID = O.ID" +
                                " AND O.ID IN (:orgElementIds)  " +
                                "AND EXISTS ( SELECT 1 FROM ORG_VERSION V WHERE V.UUID = O.ORG_VERSION_UUID AND V.STATE= 0 ) ");
                        if (StringUtils.isNotBlank(system)) {
                            sql.append(" and O.TENANT = :tenant and O.SYSTEM=:system ");
                        }
                        sqlParams.put("orgElementIds", orgElementIds);
                        List<QueryItem> queryItems = orgVersionService.getDao().listQueryItemBySQL(sql.toString(), sqlParams, null);
                        if (CollectionUtils.isNotEmpty(queryItems)) {
                            for (QueryItem item : queryItems) {
                                orgElementIds.remove(item.getString("id"));
                                Node u = new Node();
                                item.put("uuid", item.getLong("uuid").toString());
                                u.setAsyncPreviewUser(true);
                                u.setKey(item.getString("id"));
                                u.setTitle(item.getString("name"));
                                u.setType(item.getString("type"));
                                u.setData(item);
                                u.setKeyPath(item.getString("idPath"));
                                u.setTitlePath(item.getString("cnPath"));
                                u.setIsLeaf(true);
                                n.getChildren().add(u);
                            }
                        }


                    }
                }
            }
        }
        return nodes;
    }

    private Node convertGroupToNode(OrgGroupEntity entity) {
        Node n = new Node();
        n.setKey(entity.getId());
        n.setTitle(entity.getName());
        n.setType("group");
        n.setShortTitle(entity.getName());
        n.setKeyPath(entity.getId());
        n.setTitlePath(entity.getName());
        n.setData(entity);
        n.setIsLeaf(true);
        if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
            OrgElementI18nEntity i18nEntity = orgElementI18nService.getOrgElementI18n(entity.getUuid(), "name", LocaleContextHolder.getLocale().toString());
            if (i18nEntity != null) {
                n.setTitle(i18nEntity.getContent());
                n.setShortTitle(n.getTitle());
                n.setTitlePath(n.getTitle());
            }
        }
        return n;
    }

    @Override
    public List<Node> fetchByKeys(Params params) {
        List<Node> nodes = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(params.getKeys())) {
            List<OrgGroupEntity> groupEntities = orgGroupService.listByIds(params.getKeys());
            if (CollectionUtils.isNotEmpty(groupEntities)) {
                for (OrgGroupEntity entity : groupEntities) {
                    nodes.add(this.convertGroupToNode(entity));
                }
            }
        }
        return nodes;
    }

    @Override
    public PageNode fetchUser(Params params) {
        // 查询群组下节点的用户
        Params queryParams = new Params();
        queryParams.putAll(params);
        PageNode pageNode = new PageNode();
        if (StringUtils.isNotBlank(params.getOrgElementId())
                && !params.getOrgElementId().startsWith(IdPrefix.USER.getValue() + Separator.UNDERLINE.getValue())) {
//            if (params.getOrgElementId().startsWith(IdPrefix.GROUP.getValue() + Separator.UNDERLINE.getValue())) {
//                // 点击的是群组节点查询其下的用户
//                List<String> memberIds = orgGroupMemberService.getMemberIdsByGroupId(params.getOrgVersionId());
//                if(CollectionUtils.isNotEmpty(memberIds)){
//                    for(String id:memberIds){
//                        if(id.startsWith(IdPrefix.USER.getValue()+Separator.UNDERLINE.getValue())){
            //                        }
//                    }
//                }
//
//            }
            OrgElementEntity elementEntity = orgElementService.getOrgElementByIdPublished(params.getOrgElementId());
            if (elementEntity != null) {
                OrgVersionEntity versionEntity = orgVersionService.getOne(elementEntity.getOrgVersionUuid());
                queryParams.put("orgVersionId", versionEntity.getId());
            }
            return super.fetchUser(queryParams);
        }
        return pageNode;
    }
}
