/*
 * @(#)8/11/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.store;

import com.google.common.collect.Maps;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.workflow.report.utils.FlowReportUtils;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.support.WorkFlowSettings;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.jpa.criterion.Criterion;
import com.wellsoft.pt.org.dto.OrgUserDto;
import com.wellsoft.pt.org.entity.OrgUserEntity;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 8/11/25.1	    zhulh		8/11/25		    Create
 * </pre>
 * @date 8/11/25
 */
@Component
public class FlowHandleOverdueRateByUserDataStore extends AbstractDataStoreQueryInterface {

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Autowired
    private WorkflowOrgService workflowOrgService;

    @Autowired
    private OrgFacadeService orgFacadeService;

    @Override
    public String getQueryName() {
        return "流程统计分析_办理逾期分析_人员逾期率统计表";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("uuid", "t.uuid", "用户UUID", String.class);
        criteriaMetadata.add("userId", "t.user_id", "用户ID", String.class);
        criteriaMetadata.add("userName", "t.user_name", "用户名称", String.class);
        criteriaMetadata.add("deptName", "t.dept_name", "用户部门名称", String.class);
        criteriaMetadata.add("handleCount", "t.handle_count", "操作次数", Long.class);
        criteriaMetadata.add("overdueCount", "t.overdue_count", "逾期次数", Long.class);
        criteriaMetadata.add("overduePercent", "t.overdue_percent", "逾期率", String.class);
        return criteriaMetadata;
    }

    @Override
    public List<QueryItem> query(QueryContext context) {
        String userRange = Objects.toString(context.getQueryParams().get("userRange"), StringUtils.EMPTY);
        if (StringUtils.isBlank(userRange) || StringUtils.isBlank(RequestSystemContextPathResolver.system())) {
            return Collections.emptyList();
        }

        Map<String, Object> queryParams = getQueryParams(context);
        List<QueryItem> queryItems = context.getNativeDao().namedQuery("flowHandleOverdueRateByUserQuery",
                queryParams, QueryItem.class, null);
        queryItems.forEach(item -> {
            Long overdueCount = item.getLong("overdueCount");
            Long handleCount = item.getLong("handleCount");
            if (overdueCount != null && handleCount != null && handleCount == 0 && overdueCount != 0) {
                item.put("overduePercent", "100.00");
            } else if (overdueCount == null || overdueCount == 0 || handleCount == null || handleCount == 0) {
                item.put("overduePercent", "0.00");
            } else {
                item.put("overduePercent", String.format("%.2f", (overdueCount * 1.0 / handleCount) * 100));
            }
        });

        List<String> userIds = queryItems.stream().map(item -> item.getString("userId")).distinct().collect(Collectors.toList());
        List<OrgUserDto> orgUserEntities = workflowOrgService.listOrgUser(userIds, getOrgVersionIds());
        Map<String, List<OrgUserDto>> orgUserMap = orgUserEntities.stream().collect(Collectors.groupingBy(OrgUserDto::getUserId));
        Map<String, String> userDeptNameMap = Maps.newHashMap();
        orgUserMap.forEach((userId, orgUserDtos) -> {
            OrgUserDto orgUserDto =
                    orgUserDtos.stream().filter(dto -> OrgUserEntity.Type.PRIMARY_JOB_USER.equals(dto.getType())).findFirst().orElse(null);
            if (orgUserDto == null) {
                orgUserDto =
                        orgUserDtos.stream().filter(dto -> !IdPrefix.startsUser(dto.getOrgElementIdPath()) && org.apache.commons.lang3.StringUtils.isNotBlank(dto.getOrgElementIdPath())).findFirst().orElse(null);

            }
            if (orgUserDto != null) {
                List<String> elementIds = Arrays.asList(org.apache.commons.lang3.StringUtils.split(orgUserDto.getOrgElementIdPath(), Separator.SLASH.getValue()));
                List<String> elementNames = Arrays.asList(org.apache.commons.lang3.StringUtils.split(orgUserDto.getOrgElementCnPath(), Separator.SLASH.getValue()));
                Collections.reverse(elementIds);
                Collections.reverse(elementNames);
                for (int i = 0; i < elementIds.size(); i++) {
                    String elementId = elementIds.get(i);
                    if (!IdPrefix.startsUser(elementId) && !org.apache.commons.lang3.StringUtils.startsWith(elementId, IdPrefix.JOB.getValue())) {
                        userDeptNameMap.put(userId, elementNames.get(i));
                        break;
                    }
                }
            }
        });

        queryItems.stream().forEach(item -> {
            String userId = item.getString("userId");
            String deptName = userDeptNameMap.get(userId);
            if (org.apache.commons.lang3.StringUtils.isNotBlank(deptName)) {
                item.put("deptName", deptName);
            }
        });

        queryItems.sort((o1, o2) -> -Double.valueOf(o1.getString("overduePercent")).compareTo(Double.valueOf(o2.getString("overduePercent"))));
        return queryItems;
    }

    private String[] getOrgVersionIds() {
        String system = RequestSystemContextPathResolver.system();
        if (org.apache.commons.lang3.StringUtils.isBlank(system)) {
            return new String[]{orgFacadeService.getDefaultOrgVersionBySystem(system).getId()};
        }

        return orgFacadeService.listOrgVersionBySystem(system, SpringSecurityUtils.getCurrentTenantId())
                .stream().map(orgVersionEntity -> orgVersionEntity.getId()).collect(Collectors.toList())
                .toArray(new String[0]);
    }

    protected Map<String, Object> getQueryParams(QueryContext context) {
        Map<String, Object> queryParams = context.getQueryParams();
        String system = RequestSystemContextPathResolver.system();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(system)) {
            queryParams.put("system", system);
        }
        queryParams.put("whereSql", context.getWhereSqlString());
        queryParams.put("orderBy", context.getOrderString());

        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        Criterion criterion = FlowReportUtils.createFlowHandleOverdueCriterion(queryParams, workFlowSettings);// createCriterion(params);
        String flowInstWhereSql = criterion.toSqlString(context.getCriteria());
        queryParams.put("flowInstWhereSql", flowInstWhereSql);
        queryParams.put("userQuerySql", FlowReportUtils.getUserQuerySql(context.getNativeDao(), queryParams));
        return queryParams;
    }

    @Override
    public long count(QueryContext context) {
        return context.getPagingInfo().getTotalCount();
    }

}
