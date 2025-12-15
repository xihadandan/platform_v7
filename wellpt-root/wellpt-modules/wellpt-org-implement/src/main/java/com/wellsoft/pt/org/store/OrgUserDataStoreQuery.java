package com.wellsoft.pt.org.store;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.org.entity.OrgElementPathEntity;
import com.wellsoft.pt.org.entity.OrgUserEntity;
import com.wellsoft.pt.org.service.OrgElementPathService;
import com.wellsoft.pt.org.service.OrgUnitService;
import com.wellsoft.pt.org.service.OrgUserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
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
public class OrgUserDataStoreQuery extends AbstractDataStoreQueryInterface {

    @Resource
    OrgUnitService orgUnitService;

    @Resource
    OrgUserService orgUserService;

    @Resource
    OrgElementPathService orgElementPathService;


    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "平台管理_组织管理_版本用户列表";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#initCriteriaMetadata(QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext queryContext) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("uuid", "uuid", "UUID", String.class);
        criteriaMetadata.add("accountUuid", "accountUuid", "账号UUID", String.class);
        criteriaMetadata.add("userId", "userId", "ID", String.class);
        criteriaMetadata.add("userName", "userName", "用户姓名", String.class);
        criteriaMetadata.add("loginName", "loginName", "账号", String.class);
        criteriaMetadata.add("userNo", "userNo", "编号", String.class);
        criteriaMetadata.add("deptJobName", "deptJobName", "部门职位", String.class);
        criteriaMetadata.add("avatar", "avatar", "头像", String.class);
        criteriaMetadata.add("type", "type", "用户类型", String.class);
        criteriaMetadata.add("isAccountNonExpired", "isAccountNonExpired", "未过期", String.class);
        criteriaMetadata.add("isAccountNonLocked", "isAccountNonLocked", "未冻结", String.class);
        criteriaMetadata.add("isCredentialsNonExpired", "isCredentialsNonExpired", "密码未过期", String.class);
        criteriaMetadata.add("isEnable", "isEnable", "是否启用", Boolean.class);
        criteriaMetadata.add("createTime", "createTime", "创建时间", String.class);
        criteriaMetadata.add("modifyTime", "modifyTime", "修改时间", String.class);
        return criteriaMetadata;
    }

    /**
     * (non-Javadoc)
     *
     * @see AbstractDataStoreQueryInterface#query(QueryContext)
     */
    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        if (!queryContext.getQueryParams().containsKey("orgVersionUuid")) {
            return Collections.EMPTY_LIST;
        }
        List<QueryItem> list = orgUnitService.listQueryItemBySQL(this.getSQL(queryContext) + " order by u.user_no asc , u.pin_yin asc", getQueryParams(queryContext), queryContext.getPagingInfo());
        if (CollectionUtils.isNotEmpty(list)) {
            List<String> userIds = Lists.newArrayList();
            Map<String, List<String>> userJobs = Maps.newHashMap();
            for (QueryItem item : list) {
                userIds.add(item.getString("userId"));
            }
            List<OrgUserEntity> orgUserEntities = orgUserService.listByUserIdsAndOrgVersionUuidAndTypes(userIds, Long.parseLong(queryContext.getQueryParams().get("orgVersionUuid").toString()), Lists.newArrayList(OrgUserEntity.Type.PRIMARY_JOB_USER, OrgUserEntity.Type.SECONDARY_JOB_USER,
                    OrgUserEntity.Type.MEMBER_USER));
            if (CollectionUtils.isNotEmpty(orgUserEntities)) {
                Set<String> jobIds = Sets.newHashSet();
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

                }
                if (CollectionUtils.isNotEmpty(jobIds)) {
                    List<OrgElementPathEntity> pathEntities = orgElementPathService.listByOrgElementIdsAndOrgVersionUuid(Lists.newArrayList(jobIds), Long.parseLong(queryContext.getQueryParams().get("orgVersionUuid").toString()));
                    Map<String, OrgElementPathEntity> pathMap = Maps.uniqueIndex(pathEntities, OrgElementPathEntity::getOrgElementId);
                    for (QueryItem item : list) {
                        String userId = item.getString("userId");
                        Set<String> jobNames = Sets.newHashSet();
                        if (userJobs.containsKey(userId)) {
                            for (String j : userJobs.get(userId)) {
                                if (pathMap.containsKey(j)) {
                                    String[] paths = pathMap.get(j).getCnPath().split(Separator.SLASH.getValue());
                                    jobNames.add(paths.length == 1 ? paths[0] : paths[paths.length - 2] + Separator.SLASH.getValue() + paths[paths.length - 1]);
                                }
                            }
                            item.put("deptJobName", StringUtils.join(jobNames, Separator.SEMICOLON.getValue()));
                        }

                    }
                }
            }
        }


        return list;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#count(QueryContext)
     */
    @Override
    public long count(QueryContext queryContext) {
        return orgUnitService.getDao().countBySQL(this.getSQL(queryContext), getQueryParams(queryContext));
    }

    /**
     * @param queryContext
     * @return
     */
    private Map<String, Object> getQueryParams(QueryContext queryContext) {
        Map<String, Object> params = Maps.newHashMap();
        if (StringUtils.isNotBlank(queryContext.getKeyword())) {
            params.put("keyword", '%' + queryContext.getKeyword() + '%');
        }
        params.put("whereSql", queryContext.getWhereSqlString());
        params.put("orgElementId", queryContext.getQueryParams().get("orgElementId"));
        params.put("orgVersionUuid", queryContext.getQueryParams().containsKey("orgVersionUuid") ? Long.parseLong(queryContext.getQueryParams().get("orgVersionUuid").toString()) : null);
        params.put("orgRoleUuid", queryContext.getQueryParams().containsKey("orgRoleUuid") ? Long.parseLong(queryContext.getQueryParams().get("orgRoleUuid").toString()) : null);
        return params;
    }

    private String getSQL(QueryContext queryContext) {

        StringBuilder builder = new StringBuilder(" select u.* ,\n" +
                "      a.is_enabled,a.is_account_non_expired,a.is_account_non_locked,a.is_credentials_non_expired,a.expired_time\n" +
                "\t\tfrom user_info u , user_account a \n" +
                "\t\t where  u.account_uuid = a.uuid ");
        Object orgVersionUuid = queryContext.getQueryParams().get("orgVersionUuid");
        if (StringUtils.isNotBlank(queryContext.getKeyword())) {
            builder.append(" and  ( u.user_name like :keyword or u.user_no like :keyword or u.login_name like :keyword )");
        }
        if (orgVersionUuid == null || StringUtils.isBlank(orgVersionUuid.toString())) {
            builder.append(" and  1<>1 ");
            return builder.toString();
        } else {
            builder.append(" and exists ( select 1 from org_user ou where ou.user_id = u.user_id and  ou.org_version_uuid=:orgVersionUuid )");
        }

        // 查询指定组织节点下的用户
        if (queryContext.getQueryParams().containsKey("orgElementId") && StringUtils.isNotBlank(queryContext.getQueryParams().get("orgElementId").toString())) {
            builder.append("  and  ( exists (\n" +
                    // 当前节点下的用户
                    "                select 1 from ORG_USER r\n" +
                    "                where r.org_version_uuid=:orgVersionUuid and r.user_id = u.user_id and r.org_element_id=:orgElementId\n" +
                    "           ) or exists (\n" +
                    // 子节点下的用户
                    "                select 1 from ORG_USER r , ORG_ELEMENT_PATH_CHAIN c\n" +
                    "                where r.org_version_uuid=:orgVersionUuid and r.user_id = u.user_id and c.org_element_id=:orgElementId\n" +
                    "                and c.sub_org_element_id = r.org_element_id\n" +
                    "               )\n" +
                    "           ) ");
        } else {


            // 查询指定角色
            if (queryContext.getQueryParams().containsKey("orgRoleUuid") && StringUtils.isNotBlank(queryContext.getQueryParams().get("orgRoleUuid").toString())) {
                builder.append(" and exists ( select 1 from ORG_ELEMENT_ROLE_MEMBER m where m.member = u.user_id and m.org_version_uuid=:orgVersionUuid and m.org_role_uuid=:orgRoleUuid) ");

            } else if (queryContext.contains("queryQuitJobUser")) {
                // 查询离职


            } else {
                // 查询无分配部门、职位的人员
                if (queryContext.getQueryParams().containsKey("queryNoDeptNoJob")) {
                    builder.append(" and not exists (\n" +
                            "            select 1 from ORG_USER  r\n" +
                            "               where r.org_version_uuid=:orgVersionUuid and r.user_id = u.user_id\n" +
                            "         and ( (r.type = 0  and  r.org_element_type = 'dept')  or (  ( r.type = 1 or r.type=2 ) and r.org_element_type = 'job'  ) ) ) ");
//
                }


            }

        }


        return builder.toString();
    }

}
