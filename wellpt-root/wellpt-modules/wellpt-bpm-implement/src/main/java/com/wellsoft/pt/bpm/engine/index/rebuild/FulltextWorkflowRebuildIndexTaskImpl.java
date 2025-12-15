/*
 * @(#)6/20/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.index.rebuild;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.service.FlowIndexDocumentService;
import com.wellsoft.pt.bpm.engine.service.FlowInstanceService;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.fulltext.support.FulltextRebuildIndexTask;
import com.wellsoft.pt.fulltext.support.FulltextSetting;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 6/20/25.1	    zhulh		6/20/25		    Create
 * </pre>
 * @date 6/20/25
 */
@Service
public class FulltextWorkflowRebuildIndexTaskImpl implements FulltextRebuildIndexTask {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FlowIndexDocumentService flowIndexDocumentService;

    @Autowired
    private FlowInstanceService flowInstanceService;

    @Override
    public long indexCount(FulltextSetting.RebuildRule rebuildRule, FulltextSetting fulltextSetting) {
        return flowIndexDocumentService.countBySystem(fulltextSetting.getSystem());
    }

    @Override
    public void rebuildIndex(FulltextSetting.RebuildRule rebuildRule, FulltextSetting fulltextSetting) {
        String system = fulltextSetting.getSystem();
        flowIndexDocumentService.deleteBySystem(system);
        FlowInstance deadlineFlowInstance = getEarliestInstance(system);
        if (deadlineFlowInstance == null) {
            return;
        }

        Map<String, UserDetails> userDetailsMap = Maps.newHashMap();
        Date deadline = deadlineFlowInstance.getCreateTime();
        Calendar fromCalendar = Calendar.getInstance();
        Calendar toCalendar = Calendar.getInstance();
        fromCalendar.add(Calendar.DAY_OF_YEAR, -10);
        Date fromTime = fromCalendar.getTime();
        Date toTime = toCalendar.getTime();
        int loopCount = 0;
        while (fromTime.after(deadline) || loopCount <= 0) {
            loopCount++;
            List<FlowInstance> flowInstances = listFlowInstances(system, fromTime, toTime);
            rebuildIndex(flowInstances, fulltextSetting, userDetailsMap);
            fromCalendar.add(Calendar.DAY_OF_YEAR, -10);
            toCalendar.setTime(fromTime);
            fromTime = fromCalendar.getTime();
            toTime = toCalendar.getTime();
        }
    }

    @Override
    public void buildIncrementIndex(Date fromTime, FulltextSetting fulltextSetting) {
        String system = fulltextSetting.getSystem();

        Map<String, UserDetails> userDetailsMap = Maps.newHashMap();
        Calendar toCalendar = Calendar.getInstance();
        Date toTime = toCalendar.getTime();
        List<FlowInstance> flowInstances = listFlowInstances(system, fromTime, toTime);
        rebuildIndex(flowInstances, fulltextSetting, userDetailsMap);
    }

    private void rebuildIndex(List<FlowInstance> flowInstances, FulltextSetting fulltextSetting, Map<String, UserDetails> userDetailsMap) {
        for (FlowInstance flowInstance : flowInstances) {
            try {
                RequestSystemContextPathResolver.setSystem(fulltextSetting.getSystem());
                UserDetails userDetails = userDetailsMap.get(flowInstance.getModifier());
                if (userDetails == null) {
                    IgnoreLoginUtils.login(fulltextSetting.getTenant(), flowInstance.getModifier());
                    userDetails = IgnoreLoginUtils.getUserDetails();
                    userDetailsMap.put(flowInstance.getModifier(), userDetails);
                } else {
                    IgnoreLoginUtils.login(userDetails);
                }
                TaskData taskData = new TaskData();
                taskData.setFlowInstUuid(flowInstance.getUuid());
                taskData.setUserId(flowInstance.getModifier());
                flowIndexDocumentService.buildAndSaveIndex(taskData);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                IgnoreLoginUtils.logout();
                RequestSystemContextPathResolver.clear();
            }
        }
    }

    private List<FlowInstance> listFlowInstances(String system, Date fromTime, Date toTime) {
        String hql = "from FlowInstance t where t.system = :system and t.createTime >= :fromTime and t.createTime < :toTime";
        Map<String, Object> params = Maps.newHashMap();
        params.put("system", system);
        params.put("fromTime", fromTime);
        params.put("toTime", toTime);
        return flowInstanceService.listByHQL(hql, params);
    }

    private FlowInstance getEarliestInstance(String system) {
        String hql = "from FlowInstance t where t.system = :system order by t.createTime asc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("system", system);
        List<FlowInstance> flowInstances = flowInstanceService.listByHQLAndPage(hql, params, new PagingInfo(1, 1));
        return CollectionUtils.isNotEmpty(flowInstances) ? flowInstances.get(0) : null;
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
