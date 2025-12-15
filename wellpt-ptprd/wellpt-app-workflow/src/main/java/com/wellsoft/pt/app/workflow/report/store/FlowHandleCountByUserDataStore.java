/*
 * @(#)7/3/25 V1.0
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
import com.wellsoft.pt.org.service.OrgUserService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
 * 7/3/25.1	    zhulh		7/3/25		    Create
 * </pre>
 * @date 7/3/25
 */
@Component
public class FlowHandleCountByUserDataStore extends AbstractDataStoreQueryInterface {

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Autowired
    private OrgUserService orgUserService;

    @Autowired
    private WorkflowOrgService workflowOrgService;

    @Autowired
    private OrgFacadeService orgFacadeService;

    @Override
    public String getQueryName() {
        return "流程统计分析_流程办理情况按人员统计表";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("uuid", "u.uuid", "用户UUID", String.class);
        criteriaMetadata.add("userId", "u.user_id", "用户ID", String.class);
        criteriaMetadata.add("userName", "u.user_name", "用户名称", String.class);
        criteriaMetadata.add("deptName", "u.dept_name", "用户部门名称", String.class);
        criteriaMetadata.add("participateCount", "t1.participate_count", "参与", Long.class);
        criteriaMetadata.add("startCount", "t2.start_count", "发起", Long.class);
        criteriaMetadata.add("beEntrustedCount", "t3.be_entrusted_count", "受托", Long.class);
        criteriaMetadata.add("todoCount", "t4.todo_count", "待办", Long.class);
        criteriaMetadata.add("todoReadCount", "t5.todo_read_count", "待办（已阅）", Long.class);
        criteriaMetadata.add("doneCount", "t6.done_count", "已办", Long.class);
        criteriaMetadata.add("completedCount", "t7.completed_count", "办结", Long.class);
        criteriaMetadata.add("donePercent", "done_percent", "已办率", String.class);
        criteriaMetadata.add("completedPercent", "completed_percent", "办结率", String.class);
        return criteriaMetadata;
    }

    @Override
    public List<QueryItem> query(QueryContext context) {
        Map<String, Object> queryParams = getQueryParams(context);
        List<QueryItem> queryItems = context.getNativeDao().namedQuery("flowHandleCountByUserQuery", queryParams, QueryItem.class, context.getPagingInfo());
        queryItems.forEach(item -> {
            long participateCount = item.getLong("participateCount");
            if (participateCount > 0) {
                item.put("donePercent", String.format("%.2f", (item.getLong("doneCount") * 1.0 / participateCount) * 100));
                item.put("completedPercent", String.format("%.2f", (item.getLong("completedCount") * 1.0 / participateCount) * 100));
            } else {
                item.put("donePercent", "0.00");
                item.put("completedPercent", "0.00");
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
                        orgUserDtos.stream().filter(dto -> !IdPrefix.startsUser(dto.getOrgElementIdPath()) && StringUtils.isNotBlank(dto.getOrgElementIdPath())).findFirst().orElse(null);

            }
            if (orgUserDto != null) {
                List<String> elementIds = Arrays.asList(StringUtils.split(orgUserDto.getOrgElementIdPath(), Separator.SLASH.getValue()));
                List<String> elementNames = Arrays.asList(StringUtils.split(orgUserDto.getOrgElementCnPath(), Separator.SLASH.getValue()));
                Collections.reverse(elementIds);
                Collections.reverse(elementNames);
                for (int i = 0; i < elementIds.size(); i++) {
                    String elementId = elementIds.get(i);
                    if (!IdPrefix.startsUser(elementId) && !StringUtils.startsWith(elementId, IdPrefix.JOB.getValue())) {
                        userDeptNameMap.put(userId, elementNames.get(i));
                        break;
                    }
                }
            }
        });

        queryItems.stream().forEach(item -> {
            String userId = item.getString("userId");
            String deptName = userDeptNameMap.get(userId);
            if (StringUtils.isNotBlank(deptName)) {
                item.put("deptName", deptName);
            }
        });
        return queryItems;
    }

    private String[] getOrgVersionIds() {
        String system = RequestSystemContextPathResolver.system();
        if (StringUtils.isBlank(system)) {
            return new String[]{orgFacadeService.getDefaultOrgVersionBySystem(system).getId()};
        }

        return orgFacadeService.listOrgVersionBySystem(system, SpringSecurityUtils.getCurrentTenantId())
                .stream().map(orgVersionEntity -> orgVersionEntity.getId()).collect(Collectors.toList())
                .toArray(new String[0]);
    }

    private Map<String, Object> getQueryParams(QueryContext context) {
        Map<String, Object> queryParams = context.getQueryParams();
        String system = RequestSystemContextPathResolver.system();
        if (StringUtils.isNotBlank(system)) {
            queryParams.put("system", system);
        }
        queryParams.put("whereSql", context.getWhereSqlString());
        queryParams.put("orderBy", context.getOrderString());

        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        Criterion criterion = FlowReportUtils.createFlowHandleCountCriterion(queryParams, workFlowSettings);// createCriterion(params);
        String flowInstWhereSql = criterion.toSqlString(context.getCriteria());
        queryParams.put("flowInstWhereSql", flowInstWhereSql);
        return queryParams;
    }

    @Override
    public long count(QueryContext context) {
        long totalCount = context.getPagingInfo().getTotalCount();
        return totalCount > 0 ? totalCount : totalCount(context);
    }

    private long totalCount(QueryContext context) {
        Map<String, Object> queryParams = getQueryParams(context);
        return context.getNativeDao().countByNamedQuery("flowHandleCountByUserQuery", queryParams);
    }

}
