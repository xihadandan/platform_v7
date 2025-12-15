package com.wellsoft.pt.org.store;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.org.entity.BizOrgElementEntity;
import com.wellsoft.pt.org.entity.BizOrgElementMemberEntity;
import com.wellsoft.pt.org.entity.BizOrgRoleEntity;
import com.wellsoft.pt.org.service.*;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.user.service.UserInfoService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class BizOrgRoleMemberListDataStoreQuery extends AbstractDataStoreQueryInterface {


    @Resource
    OrgUserService orgUserService;

    @Resource
    BizOrganizationService bizOrganizationService;

    @Resource
    OrgElementPathService orgElementPathService;

    @Resource
    BizOrgElementService bizOrgElementService;

    @Resource
    UserInfoService userInfoService;

    @Resource
    BizOrgElementMemberService bizOrgElementMemberService;


    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "平台管理_组织服务_业务组织角色成员";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#initCriteriaMetadata(QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext queryContext) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("id", "id", "业务角色ID", String.class);
        criteriaMetadata.add("name", "name", "业务角色名称", String.class);
        criteriaMetadata.add("members", "members", "成员ID", String.class);
        criteriaMetadata.add("memberNames", "memberNames", "成员名称", String.class);
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
        List<BizOrgRoleEntity> bizOrgRoleEntities = bizOrganizationService.getBizOrgRolesByBizOrgUuid(bizOrgUuid);
        BizOrgElementEntity bizOrgElementEntity = StringUtils.isNotBlank(bizOrgElementId) ? bizOrgElementService.getById(bizOrgElementId) : null;
        Iterator<BizOrgRoleEntity> iterator = bizOrgRoleEntities.iterator();
        List<QueryItem> items = Lists.newArrayList();
        Set<String> roleIds = Sets.newHashSet();
        Map<String, QueryItem> roleQueryItemMap = Maps.newHashMap();
        while (iterator.hasNext()) {
            BizOrgRoleEntity roleEntity = iterator.next();
            if (bizOrgElementEntity != null && (
                    (bizOrgElementEntity.getIsDimension() && !roleEntity.getApplyTo().contains(BizOrgRoleEntity.ApplyTo.BIZ_DIMENSION_ELEMENT.name()))
                            || (!bizOrgElementEntity.getIsDimension() && !roleEntity.getApplyTo().contains(BizOrgRoleEntity.ApplyTo.ORG_ELEMENT.name()))
            )) {
                iterator.remove();
                continue;
            }
            QueryItem item = new QueryItem();
            item.put("name", roleEntity.getName());
            item.put("id", roleEntity.getId());
            roleIds.add(roleEntity.getId());
            items.add(item);
            roleQueryItemMap.put(roleEntity.getId(), item);
        }

        List<BizOrgElementMemberEntity> memberEntities = bizOrgElementMemberService.listByBizOrgElementIdAndRoleIds(bizOrgElementId, roleIds);
        Set<String> userIds = Sets.newHashSet();
        if (CollectionUtils.isNotEmpty(memberEntities)) {
            for (BizOrgElementMemberEntity m : memberEntities) {
                if (roleQueryItemMap.containsKey(m.getBizOrgRoleId())) {
                    QueryItem item = roleQueryItemMap.get(m.getBizOrgRoleId());
                    if (!item.containsKey("member")) {
                        item.put("member", Sets.newHashSet());
                    }
                    ((Set) item.get("member")).add(m.getMemberId());
                    userIds.add(m.getMemberId());
                }
            }
        }

        Map<String, String> userNames = userInfoService.getUserNamesByUserIds(userIds);

        for (QueryItem item : items) {
            if (item.containsKey("member")) {
                Set<String> member = (Set<String>) item.get("member");
                List<String> memberNames = Lists.newArrayList();
                if (CollectionUtils.isNotEmpty(member)) {
                    for (String m : member) {
                        if (userNames.containsKey(m)) {
                            memberNames.add(userNames.get(m));
                        }
                    }
                    item.put("memberNames", StringUtils.join(memberNames, Separator.SEMICOLON.getValue()));
                }
                item.put("members", StringUtils.join(member, Separator.SEMICOLON.getValue()));
            }
        }

        return items;

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
        return "SELECT R.NAME AS ROLE_NAME ,R.UUID AS ROLE_UUID, E.ID AS ELEMENT_ID, E.NAME AS ELEMENT_NAME ,M.MEMBER FROM BIZ_ORG_ELEMENT_ROLE_MEMBER M " +
                ", BIZ_ORG_ELEMENT E ,BIZ_ORG_ROLE R " +
                "WHERE M.BIZ_ORG_ROLE_UUID IN :bizOrgRoleUuids AND M.BIZ_ORG_ELEMENT_ID = E.ID AND R.UUID = M.BIZ_ORG_ROLE_UUID";
    }

    private String sql() {
        String sql = "SELECT U.USER_NAME , U.USER_ID ,U.AVATAR,U.GENDER FROM USER_INFO U WHERE EXISTS (" +
                "SELECT 1 FROM BIZ_ORG_USER OU WHERE OU.BIZ_ORG_UUID=:bizOrgUuid AND U.USER_ID = OU.USER_ID " +
                // 节点下的用户
                "AND ( OU.BIZ_ORG_ELEMENT_ID =:bizOrgElementId OR EXISTS ( SELECT 1 FROM BIZ_ORG_ELEMENT_PATH_CHAIN C WHERE C.ID =:bizOrgElementId AND C.SUB_ID = OU.BIZ_ORG_ELEMENT_ID ) )"
                + ")";
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
