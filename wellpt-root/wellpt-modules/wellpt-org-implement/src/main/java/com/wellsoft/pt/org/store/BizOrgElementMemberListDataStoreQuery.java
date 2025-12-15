package com.wellsoft.pt.org.store;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.org.entity.BizOrgRoleEntity;
import com.wellsoft.pt.org.entity.OrgElementPathEntity;
import com.wellsoft.pt.org.entity.OrgUserEntity;
import com.wellsoft.pt.org.service.*;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

import static com.google.common.collect.Maps.uniqueIndex;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/6/3
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/6/3    chenq		2019/6/3		Create
 * </pre>
 */
@Component
public class BizOrgElementMemberListDataStoreQuery extends AbstractDataStoreQueryInterface {


    @Resource
    OrgUserService orgUserService;

    @Resource
    BizOrganizationService bizOrganizationService;

    @Resource
    OrgElementPathService orgElementPathService;

    @Resource
    BizOrgElementService bizOrgElementService;

    @Resource
    BizOrgElementMemberService bizOrgElementMemberService;


    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "平台管理_组织服务_业务组织成员列表";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#initCriteriaMetadata(QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext queryContext) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("userId", "USER_ID", "用户ID", String.class);
        criteriaMetadata.add("userName", "USER_NAME", "名称", String.class);
        criteriaMetadata.add("avatar", "AVATAR", "头像图片UUID", String.class);
        criteriaMetadata.add("bizOrgElementId", "BIZ_ORG_ELEMENT_ID", "归属业务组织节点", String.class);
        criteriaMetadata.add("bizOrgRoleId", "BIZ_ORG_ROLE_ID", "业务角色ID", String.class);
        criteriaMetadata.add("underBizOrgElementAndRole", "underBizOrgElementAndRole", "部门/角色", String.class);
        criteriaMetadata.add("deptAndJob", "deptAndJob", "组织部门/职位", String.class);
        return criteriaMetadata;
    }

    /**
     * (non-Javadoc)
     *
     * @see AbstractDataStoreQueryInterface#query(QueryContext)
     */
    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        Long bizOrgUuid = Long.parseLong(queryContext.getQueryParams().get("bizOrgUuid").toString());
        String bizOrgElementId = queryContext.getQueryParams().containsKey("bizOrgElementId") ? queryContext.getQueryParams().get("bizOrgElementId").toString() : null;
        Long orgUuid = Long.parseLong(queryContext.getQueryParams().get("orgUuid").toString());

        StringBuilder sql = new StringBuilder("SELECT U.USER_NAME , U.USER_ID ,U.AVATAR,U.GENDER ,M.BIZ_ORG_ELEMENT_ID ,P.ID_PATH,P.CN_PATH, M.BIZ_ORG_ROLE_ID FROM USER_INFO U , BIZ_ORG_ELEMENT_MEMBER M ," +
                "BIZ_ORG_ELEMENT_PATH P" +
                " WHERE " +
                " M.MEMBER_ID = U.USER_ID AND M.BIZ_ORG_UUID=:bizOrgUuid  AND M.BIZ_ORG_ELEMENT_ID = P.BIZ_ORG_ELEMENT_ID "
        );
        if (StringUtils.isNotBlank(bizOrgElementId)) {
            // 节点下的用户
            sql.append(" AND ( M.BIZ_ORG_ELEMENT_ID =:bizOrgElementId OR EXISTS ( SELECT 1 FROM BIZ_ORG_ELEMENT_PATH_CHAIN C WHERE C.ID =:bizOrgElementId AND C.SUB_ID = M.BIZ_ORG_ELEMENT_ID ) ) ");
        } else {
            sql.append(" AND M.BIZ_ORG_UUID =:bizOrgUuid");
        }
        sql.append(" ORDER BY U.USER_ID ASC");

        List<QueryItem> list = bizOrgElementMemberService.listQueryItemBySQL(sql.toString(), this.getQueryParams(queryContext), null
        );
        Set<String> userIds = Sets.newHashSet();
        Set<String> bizOrgRoleIds = Sets.newHashSet();
        Map<String, QueryItem> merge = Maps.newHashMap();
        for (QueryItem item : list) {
            userIds.add(item.getString("userId"));
            bizOrgRoleIds.add(item.getString("bizOrgRoleId"));
            String key = item.getString("userId") + item.getString("bizOrgElementId");
            if (!merge.containsKey(key)) {
                merge.put(key, item);
            }
            QueryItem obj = merge.get(key);
            if (!obj.containsKey("bizOrgRoleIds")) {
                obj.put("bizOrgRoleIds", Lists.<String>newArrayList());
            }
            ((List<String>) obj.get("bizOrgRoleIds")).add(item.getString("bizOrgRoleId"));
        }

        // 查询关联的组织部门与职位信息
        if (CollectionUtils.isNotEmpty(userIds)) {
            List<OrgUserEntity> orgUserEntities = orgUserService.listByUserIdsAndOrgUuidAndTypes(Lists.newArrayList(userIds), orgUuid, Lists.newArrayList(OrgUserEntity.Type.PRIMARY_JOB_USER, OrgUserEntity.Type.SECONDARY_JOB_USER,
                    OrgUserEntity.Type.MEMBER_USER));
            if (CollectionUtils.isNotEmpty(orgUserEntities)) {
                Set<String> jobIds = Sets.newHashSet();
                Map<String, List<String>> userJobs = Maps.newHashMap();
                Long orgVersionUuid = null;
                for (OrgUserEntity r : orgUserEntities) {
                    if (r.getOrgElementId() != null) {
                        if (!userJobs.containsKey(r.getUserId())) {
                            userJobs.put(r.getUserId(), Lists.newArrayList());
                        }
                        if (r.getOrgElementId().startsWith(IdPrefix.JOB.getValue() + Separator.UNDERLINE.getValue())) {
                            jobIds.add(r.getOrgElementId());
                            userJobs.get(r.getUserId()).add(r.getOrgElementId());
                        } else if (r.getOrgElementId().startsWith(IdPrefix.DEPARTMENT.getValue() + Separator.UNDERLINE.getValue())) {
                            jobIds.add(r.getOrgElementId());
                            userJobs.get(r.getUserId()).add(r.getOrgElementId());
                        }
                    }
                    orgVersionUuid = r.getOrgVersionUuid();

                }
                if (CollectionUtils.isNotEmpty(jobIds)) {
                    List<OrgElementPathEntity> pathEntities = orgElementPathService.listByOrgElementIdsAndOrgVersionUuid(Lists.newArrayList(jobIds), orgVersionUuid);
                    Map<String, OrgElementPathEntity> pathMap = uniqueIndex(pathEntities, OrgElementPathEntity::getOrgElementId);
                    for (QueryItem item : list) {
                        String userId = item.getString("userId");
                        Set<String> jobNames = Sets.newHashSet();
                        if (userJobs.containsKey(userId)) {
                            for (String j : userJobs.get(userId)) {
                                if (pathMap.containsKey(j)) {
                                    String[] paths = pathMap.get(j).getCnPath().split(Separator.SLASH.getValue());
                                    if (j.startsWith(IdPrefix.JOB.getValue() + Separator.UNDERLINE.getValue())) {
                                        // 职位
                                        jobNames.add(paths.length == 1 ? paths[0] : paths[paths.length - 2] + Separator.SLASH.getValue() + paths[paths.length - 1]);
                                    } else if (j.startsWith(IdPrefix.DEPARTMENT.getValue() + Separator.UNDERLINE.getValue())) {
                                        jobNames.add(paths[paths.length - 1]);
                                    }
                                }
                            }
                            item.put("deptAndJob", org.apache.commons.lang3.StringUtils.join(jobNames, Separator.SEMICOLON.getValue()));
                        }
                    }
                }
            }
            Map<String, Object> memberParam = Maps.newHashMap();
            memberParam.put("userIds", userIds);
            if (CollectionUtils.isNotEmpty(bizOrgRoleIds)) {
                List<BizOrgRoleEntity> bizOrgRoleEntities = bizOrganizationService.getBizOrgRolesByIds(Lists.newArrayList(bizOrgRoleIds), Long.parseLong(queryContext.getQueryParams().get("bizOrgUuid").toString()));
                ImmutableMap<String, BizOrgRoleEntity> map = Maps.uniqueIndex(bizOrgRoleEntities, bizOrgRoleEntity -> {
                    return bizOrgRoleEntity.getId();
                });

                Iterator<QueryItem> iterator = merge.values().iterator();
                while ((iterator.hasNext())) {
                    QueryItem item = iterator.next();
                    if (item.containsKey("bizOrgRoleIds")) {
                        List<String> itemRoleIds = (List<String>) item.get("bizOrgRoleIds");
                        List<String> itemRoleNames = Lists.newArrayList();
                        for (String id : itemRoleIds) {
                            if (map.containsKey(id)) {
                                String name = map.get(id).getName();
                                String[] parts = item.getString("cnPath").split(Separator.SLASH.getValue());
                                item.put("pathLevel", parts.length);
                                if (!(item.getString("bizOrgElementId").equalsIgnoreCase(bizOrgElementId))) {
                                    name = parts[parts.length - 1] + Separator.SLASH.getValue() + name;
                                }
                                itemRoleNames.add(name);
                            }
                        }
                        item.put("underBizOrgElementAndRole", StringUtils.join(itemRoleNames, Separator.SEMICOLON.getValue()));
                    }

                }
            }
        }
        // 按 path 排序
        List<QueryItem> queryItems = Lists.newArrayList(merge.values());
        Collections.sort(queryItems, (o1, o2) -> {
            return ((QueryItem) o1).getInt("pathLevel") - ((QueryItem) o2).getInt("pathLevel");
        });
        return queryItems;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#count(QueryContext)
     */
    @Override
    public long count(QueryContext queryContext) {
        return 0L;
    }

    private String elementRoleMemberSql() {
        return "SELECT R.NAME AS ROLE_NAME ,E.ID AS ELEMENT_ID, E.NAME AS ELEMENT_NAME ,M.MEMBER FROM BIZ_ORG_ELEMENT_ROLE_MEMBER M , BIZ_ORG_ELEMENT E ,BIZ_ORG_ROLE R " +
                "WHERE M.MEMBER IN :userIds AND M.BIZ_ORG_ELEMENT_ID = E.ID AND R.UUID = M.BIZ_ORG_ROLE_UUID";
    }

    private String sql() {
        String sql = "SELECT U.USER_NAME , U.USER_ID ,U.AVATAR,U.GENDER ,M.BIZ_ORG_ELEMENT_ID ,P.ID_PATH,P.CN_PATH, M.BIZ_ORG_ROLE_ID FROM USER_INFO U , BIZ_ORG_ELEMENT_MEMBER M ," +
                "BIZ_ORG_ELEMENT_PATH P" +
                " WHERE " +
                " M.MEMBER_ID = U.USER_ID AND M.BIZ_ORG_UUID=:bizOrgUuid  AND M.BIZ_ORG_ELEMENT_ID = P.BIZ_ORG_ELEMENT_ID " +
                // 节点下的用户
                " AND ( M.BIZ_ORG_ELEMENT_ID =:bizOrgElementId OR EXISTS ( SELECT 1 FROM BIZ_ORG_ELEMENT_PATH_CHAIN C WHERE C.ID =:bizOrgElementId AND C.SUB_ID = M.BIZ_ORG_ELEMENT_ID ) ) ";
        return sql;
    }

    /**
     * @param queryContext
     * @return
     */
    private Map<String, Object> getQueryParams(QueryContext queryContext) {
        Map<String, Object> params = Maps.newHashMap();
        params.putAll(queryContext.getQueryParams());
        params.put("keyword", queryContext.getKeyword());
        params.put("whereSql", queryContext.getWhereSqlString());
        params.put("orderBy", queryContext.getOrderString());
        if (StringUtils.isNotBlank(RequestSystemContextPathResolver.system())) {
            params.put("system", RequestSystemContextPathResolver.system());
            params.put("tenant", SpringSecurityUtils.getCurrentTenantId());
        } else {
            params.put("systemIsNull", true);
        }
        return params;
    }

}
