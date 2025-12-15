/*
 * @(#)2013-4-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.dao;

import com.wellsoft.pt.org.entity.DutyAgent;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-15.1	zhulh		2013-4-15		Create
 * </pre>
 * @date 2013-4-15
 */
@Repository
public class DutyAgentDao extends OrgHibernateDao<DutyAgent, String> {

    private static final String QUERY_ACTIVE_DUTY_AGENT = "from DutyAgent duty_agent where duty_agent.consignor = :consignor "
            + "and duty_agent.businessType = :businessType and :currentTime between duty_agent.fromTime and duty_agent.toTime and "
            + "duty_agent.status = 1";

    private static final String QUERY_ACTIVE_DUTY_AGENTS = "from DutyAgent duty_agent where duty_agent.consignor in (:consignorIds) "
            + "and duty_agent.businessType = :businessType and :currentTime between duty_agent.fromTime and duty_agent.toTime and "
            + "duty_agent.status = 1";

    /**
     * 根据委托人及业务类型获取该委托人当前有效的业务代理人列表
     *
     * @param consignor
     * @param businessType
     * @return
     */
    public List<DutyAgent> getActiveDutyAgents(String consignor, String businessType) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("consignor", consignor);
        values.put("businessType", businessType);
        values.put("currentTime", Calendar.getInstance().getTime());
        return this.find(QUERY_ACTIVE_DUTY_AGENT, values);
    }

    /**
     * 根据委托人ID列表及业务类型获取该委托人当前有效的业务代理人列表
     *
     * @param consignorIds
     * @param businessType
     * @return
     */
    public List<DutyAgent> getActiveDutyAgents(List<String> consignorIds, String businessType) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("consignorIds", consignorIds);
        values.put("businessType", businessType);
        values.put("currentTime", Calendar.getInstance().getTime());
        return this.find(QUERY_ACTIVE_DUTY_AGENTS, values);
    }

}
