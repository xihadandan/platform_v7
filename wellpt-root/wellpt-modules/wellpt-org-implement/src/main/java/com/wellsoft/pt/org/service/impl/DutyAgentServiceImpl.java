/*
 * @(#)2013-4-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service.impl;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.WorkFlowException;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.pt.cache.config.CacheName;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyformFieldDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.message.facade.service.MessageClientApiFacade;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.org.bean.DutyAgentBean;
import com.wellsoft.pt.org.entity.DutyAgent;
import com.wellsoft.pt.org.service.DutyAgentService;
import com.wellsoft.pt.org.support.DutyAgentConditionJson;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.task.facade.TaskApiFacade;
import com.wellsoft.pt.workflow.service.FlowSchemeService;
import com.wellsoft.pt.xxljob.model.ExecutionParam;
import com.wellsoft.pt.xxljob.service.JobHandlerName;
import com.wellsoft.pt.xxljob.service.XxlJobService;
import com.xxl.job.core.well.model.TmpJobParam;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 职务代理人服务实现类
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
@Service
@Transactional
public class DutyAgentServiceImpl extends BaseServiceImpl implements DutyAgentService {
    public static final String KEY_DUTY_AGENT_UUID = "dutyAgentUuid";
    // 委托失效
    private static final String DUTY_AGENT_DEACTIVE = "DUTY_AGENT_DEACTIVE";
    // 委托生效
    private static final String DUTY_AGENT_ACTIVE = "DUTY_AGENT_ACTIVE";
    // 征求受托人意见
    private static final String DUTY_AGENT_CONSULT = "DUTY_AGENT_CONSULT";
    private static final String QUERY_ACTIVE_DUTY_AGENT = "from DutyAgent duty_agent where duty_agent.consignor = :consignor "
            + "and duty_agent.businessType = :businessType and :currentTime between duty_agent.fromTime and duty_agent.toTime and "
            + "duty_agent.status = 1";
    private static final String QUERY_ACTIVE_DUTY_AGENTS = "from DutyAgent duty_agent where duty_agent.consignor in (:consignorIds) "
            + "and duty_agent.businessType = :businessType and :currentTime between duty_agent.fromTime and duty_agent.toTime and "
            + "duty_agent.status = 1";
    private Logger logger = LoggerFactory.getLogger(DutyAgentServiceImpl.class);
    @Autowired
    private TaskApiFacade taskApiFacade;
    @Autowired
    private OrgApiFacade orgApiFacade;
    @Autowired
    private DyFormFacade dyFormApiFacade;
    @Autowired
    private MessageClientApiFacade messageClientApiFacade;
    @Autowired
    private XxlJobService xxlJobService;
    @Autowired
    private FlowSchemeService flowSchemeService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DutyAgentService#query(com.wellsoft.pt.core.support.QueryInfo)
     */
    @Override
    public List<QueryItem> query(QueryInfo queryInfo) {
        List<DutyAgent> dutyAgents = this.dao.findByExample(new DutyAgent(), queryInfo.getOrderBy(),
                queryInfo.getPagingInfo());
        // 1、设置委托人及受托人的用户Map
        Set<String> userIdSet = new HashSet<String>();
        for (DutyAgent dutyAgent : dutyAgents) {
            if (StringUtils.isNotBlank(dutyAgent.getConsignor())) {
                userIdSet.addAll(Arrays.asList(StringUtils.split(dutyAgent.getConsignor(),
                        Separator.SEMICOLON.getValue())));
            }
            if (StringUtils.isNotBlank(dutyAgent.getTrustee())) {
                userIdSet
                        .addAll(Arrays.asList(StringUtils.split(dutyAgent.getTrustee(), Separator.SEMICOLON.getValue())));
            }
        }
        List<MultiOrgUserAccount> users = orgApiFacade.queryUserAccountListByIds(userIdSet);
        Map<String, MultiOrgUserAccount> userMap = ConvertUtils.convertElementToMap(users, "id");
        // 设置用户名及格式化日期显示
        List<QueryItem> queryItems = new ArrayList<QueryItem>();
        for (DutyAgent dutyAgent : dutyAgents) {
            QueryItem item = new QueryItem();
            item.put("uuid", dutyAgent.getUuid());
            item.put("consignor", dutyAgent.getConsignor());
            item.put("trustee", dutyAgent.getTrustee());
            item.put("businessType", dutyAgent.getBusinessType());
            item.put("content", dutyAgent.getContent());
            item.put("condition", dutyAgent.getCondition());
            item.put("status", dutyAgent.getStatus());
            if (dutyAgent.getFromTime() != null) {
                item.put("fromTime", DateUtils.formatDateTimeMin(dutyAgent.getFromTime()));
            }
            if (dutyAgent.getToTime() != null) {
                item.put("toTime", DateUtils.formatDateTimeMin(dutyAgent.getToTime()));
            }
            item.put("consignorName", resolveUsername(userMap, dutyAgent.getConsignor()));
            item.put("trusteeName", resolveUsername(userMap, dutyAgent.getTrustee()));
            queryItems.add(item);
        }
        return queryItems;
    }

    /**
     * 根据用户ID返回相应的用户名
     *
     * @param userMap
     * @param consignor
     * @return
     */
    private String resolveUsername(Map<String, MultiOrgUserAccount> userMap, String userId) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(userId)) {
            String[] userIds = userId.split(Separator.SEMICOLON.getValue());
            Iterator<String> it = Arrays.asList(userIds).iterator();
            while (it.hasNext()) {
                String key = it.next();
                if (userMap.containsKey(key)) {
                    sb.append(userMap.get(key).getUserName());
                } else {
                    sb.append(key);
                }
                if (it.hasNext()) {
                    sb.append(Separator.SEMICOLON.getValue());
                }
            }
        }
        return sb.toString();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DutyAgentService#getBean(java.lang.String)
     */
    @Override
    public DutyAgentBean getBean(String uuid) {
        DutyAgent dutyAgent = this.dao.get(DutyAgent.class, uuid);
        DutyAgentBean agentBean = new DutyAgentBean();
        BeanUtils.copyProperties(dutyAgent, agentBean);
        // 1、设置委托人及受托人的名称
        Set<String> userIdSet = new HashSet<String>();
        if (StringUtils.isNotBlank(dutyAgent.getConsignor())) {
            userIdSet
                    .addAll(Arrays.asList(StringUtils.split(dutyAgent.getConsignor(), Separator.SEMICOLON.getValue())));
        }
        if (StringUtils.isNotBlank(dutyAgent.getTrustee())) {
            userIdSet.addAll(Arrays.asList(StringUtils.split(dutyAgent.getTrustee(), Separator.SEMICOLON.getValue())));
        }
        List<MultiOrgUserAccount> users = orgApiFacade.queryUserAccountListByIds(userIdSet);
        Map<String, MultiOrgUserAccount> userMap = ConvertUtils.convertElementToMap(users, "id");
        agentBean.setConsignorName(resolveUsername(userMap, dutyAgent.getConsignor()));
        agentBean.setTrusteeName(resolveUsername(userMap, dutyAgent.getTrustee()));
        // 2、格式化开始时间与结束时间
        if (dutyAgent.getFromTime() != null) {
            agentBean.setFormatedFromTime(DateUtils.formatDateTimeMin(dutyAgent.getFromTime()));
        }
        if (dutyAgent.getToTime() != null) {
            agentBean.setFormatedToTime(DateUtils.formatDateTimeMin(dutyAgent.getToTime()));
        }
        return agentBean;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DutyAgentService#saveBean(com.wellsoft.pt.org.entity.DutyAgent)
     */
    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public void saveBean(DutyAgentBean bean) {
        DutyAgent dutyAgent = new DutyAgent();

        if (StringUtils.isBlank(bean.getUuid())) {
            bean.setTenantId(SpringSecurityUtils.getCurrentTenantId());
            bean.setUuid(null);
        } else {
            dutyAgent = this.dao.get(DutyAgent.class, bean.getUuid());
        }

        BeanUtils.copyProperties(bean, dutyAgent);
        try {
            if (StringUtils.isNotBlank(bean.getFormatedFromTime())) {
                dutyAgent.setFromTime(DateUtils.parseDateTimeMin(bean.getFormatedFromTime()));
            } else {
                dutyAgent.setFromTime(null);
            }
            if (StringUtils.isNotBlank(bean.getFormatedToTime())) {
                dutyAgent.setToTime(DateUtils.parseDateTimeMin(bean.getFormatedToTime()));
            } else {
                dutyAgent.setToTime(null);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);

            throw new RuntimeException(e);
        }
        if (dutyAgent.getToTime() == null || dutyAgent.getFromTime() == null) {
            throw new RuntimeException("开始时间和结束时间不能为空!");
        }
        // 结束时间不能小于开始时间
        if (dutyAgent.getToTime().before(dutyAgent.getFromTime())) {
            throw new RuntimeException("结束时间不能小于开始时间!");
        }
        // 结束时间不能小于当前时间
        if (dutyAgent.getToTime().before(Calendar.getInstance().getTime())) {
            throw new RuntimeException("结束时间不能小于当前时间!");
        }

        // 委托条件生成
        String conditionJson = dutyAgent.getConditionJson();
        if (StringUtils.isNotBlank(conditionJson)) {
            DutyAgentConditionJson dutyAgentConditionJson = JsonUtils.json2Object(conditionJson,
                    DutyAgentConditionJson.class);
            dutyAgent.setCondition(dutyAgentConditionJson.getCondition());
        } else {
            dutyAgent.setCondition(conditionJson);
        }
        this.dao.save(dutyAgent);

        // 征求受托人意见，发送消息
        Integer status = dutyAgent.getStatus();
        if (DutyAgent.STATUS_CONSULT.equals(status)) {
            consultTrustee(dutyAgent);
        }

        // 当前工作委托
        // FlowEngine.getInstance().getDelegationExecutor().delegationCurrentWork(dutyAgent);

        // 启动委托时间过时终止委托任务
        startActiveStatusTraceJob(dutyAgent);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DutyAgentService#active(java.lang.String)
     */
    @Override
    public void active(String uuid) {
        DutyAgent dutyAgent = this.dao.get(DutyAgent.class, uuid);

        // 委托时间判断
        Date fromDate = dutyAgent.getFromTime();
        Date toDate = dutyAgent.getToTime();
        if (fromDate == null || toDate == null) {
            throw new WorkFlowException("委托开始时间无效");
        }
        if (fromDate.after(toDate)) {
            throw new WorkFlowException("委托结束时间无效");
        }
        Date currentDate = Calendar.getInstance().getTime();
        if (currentDate.before(fromDate) || currentDate.after(toDate)) {
            throw new WorkFlowException("委托时间已失效");
        }

        dutyAgent.setStatus(DutyAgent.STATUS_ACTIVE);
        this.dao.save(dutyAgent);

        // 受托人受托生效，发送消息
        List<String> consignor = Arrays.asList(StringUtils.split(dutyAgent.getConsignor(),
                Separator.SEMICOLON.getValue()));
        messageClientApiFacade.send(DUTY_AGENT_ACTIVE, dutyAgent, consignor);

        // 当前工作委托
        // FlowEngine.getInstance().getDelegationExecutor().delegationCurrentWork(dutyAgent);

        // 启动委托时间过时终止委托任务
        startActiveStatusTraceJob(dutyAgent);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DutyAgentService#deactive(java.lang.String)
     */
    @Override
    public void deactive(String uuid) {
        DutyAgent dutyAgent = this.dao.get(DutyAgent.class, uuid);
        dutyAgent.setStatus(DutyAgent.STATUS_DEACTIVE);
        this.dao.save(dutyAgent);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DutyAgentService#refuse(java.lang.String)
     */
    @Override
    public void refuse(String uuid) {
        DutyAgent dutyAgent = this.dao.get(DutyAgent.class, uuid);
        dutyAgent.setStatus(DutyAgent.STATUS_DEACTIVE);
        this.dao.save(dutyAgent);

        // 受托人委托终止，发送消息
        List<String> consignor = Arrays.asList(StringUtils.split(dutyAgent.getConsignor(),
                Separator.SEMICOLON.getValue()));
        messageClientApiFacade.send(DUTY_AGENT_DEACTIVE, dutyAgent, consignor);
    }

    /**
     * 启动激活状态的结束时间状态跟踪
     *
     * @param dutyAgent
     */
    private void startActiveStatusTraceJob(DutyAgent dutyAgent) {
        String name = dutyAgent.getUuid() + "_" + "active";
        Integer active = dutyAgent.getStatus();
        if (!DutyAgent.STATUS_ACTIVE.equals(active)) {
            return;
        }

        Date endTime = dutyAgent.getToTime();
        if (endTime == null) {
            return;
        }
        // 延时10秒终止
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endTime);
        calendar.add(Calendar.SECOND, 10);

        //xxlJob执行需要的参数
        ExecutionParam executionParam = new ExecutionParam()
                .setTenantId(Config.DEFAULT_TENANT)
                .setUserId(dutyAgent.getConsignor())
                .putKeyVal(KEY_DUTY_AGENT_UUID, dutyAgent.getUuid());
        TmpJobParam.Builder builder = TmpJobParam.toBuilder()
                .setJobDesc(name)
                .setExecutorHandler(JobHandlerName.Temp.DutyAgentActiveStatusTraceJob)
                .addExecutionTimeParams(endTime, executionParam.toJson());
        xxlJobService.addTmpStart(builder.build());
    }

    /**
     * 征求受托人意见
     *
     * @param dutyAgent
     */
    private void consultTrustee(DutyAgent dutyAgent) {
        if (StringUtils.isBlank(dutyAgent.getTrustee())) {
            return;
        }

        List<String> trustees = Arrays
                .asList(StringUtils.split(dutyAgent.getTrustee(), Separator.SEMICOLON.getValue()));
        messageClientApiFacade.send(DUTY_AGENT_CONSULT, dutyAgent, trustees);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DutyAgentService#remove(java.lang.String)
     */
    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public void remove(String uuid) {
        DutyAgent dutyAgent = this.dao.get(DutyAgent.class, uuid);
        this.dao.delete(dutyAgent);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DutyAgentService#removeAll(java.util.Collection)
     */
    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public void removeAll(Collection<String> uuids) {
        for (String uuid : uuids) {
            DutyAgent dutyAgent = this.dao.get(DutyAgent.class, uuid);
            if (DutyAgent.STATUS_ACTIVE.equals(dutyAgent.getStatus())) {
                throw new RuntimeException("激活状态的工作委托不能删除，需先变更为终止或征求受托人意见的状态！");
            }
            this.remove(uuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DutyAgentService#getDutyAgents(java.lang.String, java.lang.String, java.lang.String, java.util.Map)
     */
    @Override
    public List<String> getDutyAgentIds(String consignor, String businessType, String content, Map<Object, Object> root) {
        List<String> trustees = new ArrayList<String>();
        // 根据委托人及业务类型获取该委托人当前有效的业务代理人列表
        Set<String> userIds = new HashSet<String>();
        List<DutyAgent> dutyAgents = this.getActiveDutyAgents(consignor, businessType);
        for (DutyAgent dutyAgent : dutyAgents) {
            // 如果委托内容为空则表示委托所有工作
            String dutyContent = dutyAgent.getContent();
            if (StringUtils.isBlank(dutyContent)) {
                String trustee = dutyAgent.getTrustee();
                if (StringUtils.isBlank(trustee)) {
                    continue;
                }
                userIds.addAll(Arrays.asList(StringUtils.split(trustee, Separator.SEMICOLON.getValue())));
            } else {
                // 根据委托内容获取相应的受托人
                String[] contents = StringUtils.split(dutyContent, Separator.SEMICOLON.getValue());
                for (String contentId : contents) {
                    if (contentId.equals(content)) {
                        String trustee = dutyAgent.getTrustee();
                        if (StringUtils.isBlank(trustee)) {
                            continue;
                        }
                        userIds.addAll(Arrays.asList(StringUtils.split(trustee, Separator.SEMICOLON.getValue())));
                    }
                }
            }
        }
        trustees.addAll(userIds);
        return trustees;
    }

    /**
     * 根据委托人及业务类型获取该委托人当前有效的业务代理人列表
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DutyAgentService#getDutyAgents(java.util.List, java.lang.String)
     */
    @Override
    public List<DutyAgent> getDutyAgents(List<String> consignorIds, String businessType) {
        List<DutyAgent> dutyAgents = this.getActiveDutyAgents(consignorIds, businessType);
        for (DutyAgent dutyAgent : dutyAgents) {
            this.dao.getSession().evict(dutyAgent);
        }
        return dutyAgents;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DutyAgentService#getContentAsTreeAsync(java.lang.String, java.lang.String)
     */
    @Override
    public List<TreeNode> getContentAsTreeAsync(String uuid, String businessType) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        if ("workflow".equals(businessType) && TreeNode.ROOT_ID.equals(uuid)) {
            // TreeNode root = new TreeNode();
            // root.setId(TreeNode.ROOT_ID);
            // root.setName("全部工作");
            // root.setData(WorkFlowVariables.FLOW_ALL.getName());
            // treeNodes.add(root);
            // if (TreeNode.ROOT_ID.equals(uuid)) {
            treeNodes.addAll(flowSchemeService.getAllFlowAsCategoryTree());
            // }
        }
        return treeNodes;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DutyAgentService#getKeyValuePair(java.lang.String, java.lang.String)
     */
    @Override
    public QueryItem getKeyValuePair(String businessType, String value) {
        QueryItem queryItem = flowSchemeService.getFlowKeyValuePair(businessType, value);
        return queryItem;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DutyAgentService#getAllDyFormDefinitionBasicInfo()
     */
    @Override
    public List<QueryItem> getAllDyFormDefinitionBasicInfo() {
        List<DyFormFormDefinition> dyFormDefinitions = this.dao.namedQuery("getAllDyFormDefinitionBasicInfo", null,
                DyFormFormDefinition.class);
        List<QueryItem> queryItems = new ArrayList<QueryItem>();
        for (DyFormFormDefinition dyFormDefinition : dyFormDefinitions) {
            QueryItem queryItem = new QueryItem();
            queryItem.put("uuid", dyFormDefinition.getUuid());
            queryItem.put("id", dyFormDefinition.getId());
            queryItem.put("name", dyFormDefinition.getName() + "(" + dyFormDefinition.getVersion() + ")");
            queryItems.add(queryItem);
        }
        return queryItems;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DutyAgentService#getLimitDyformUuids(java.lang.String)
     */
    @Override
    public List<String> getLimitDyformUuids(String limitFlowDefId) {
        List<String> dyformIds = new ArrayList<String>(0);
        if (StringUtils.isBlank(limitFlowDefId)) {
            dyformIds.add(TreeNode.ROOT_ID);
            return dyformIds;
        }

        List<String> limits = Arrays.asList(StringUtils.split(limitFlowDefId, Separator.SEMICOLON.getValue()));
        int size = limits.size();

        final int MAX = 500;
        List<String> tmpLimits = new ArrayList<String>();
        if (size > MAX) {
            tmpLimits.addAll(limits);
            addLimitDyformUuids(dyformIds, tmpLimits);
        } else {
            int loop = size / MAX;
            for (int i = 0; i < loop + 1; i++) {
                tmpLimits = new ArrayList<String>();
                for (int index = 0; index < MAX; index++) {
                    int j = loop * 500 + index;
                    if (j + 1 > size) {
                        break;
                    }
                    String limit = limits.get(j);
                    tmpLimits.add(limit);
                }
                addLimitDyformUuids(dyformIds, tmpLimits);
            }
        }
        return dyformIds;
    }

    /**
     * @param dyformIds
     * @param tmpLimits
     */
    private void addLimitDyformUuids(List<String> dyformIds, List<String> tmpLimits) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowDefIds", tmpLimits);
        List<DyFormFormDefinition> dyFormDefinitions = this.dao.namedQuery("getDyFormDefinitionBasicInfoByFlowDefIds",
                values, DyFormFormDefinition.class);
        for (DyFormFormDefinition dyFormDefinition : dyFormDefinitions) {
            dyformIds.add(dyFormDefinition.getUuid());
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DutyAgentService#getDyFormFieldDefinition(java.lang.String)
     */
    @Override
    public List<QueryItem> getDyFormFieldDefinition(String formUuid) {
        List<DyformFieldDefinition> fieldDefinitions = dyFormApiFacade.getFieldDefinitions(formUuid);
        List<QueryItem> queryItems = new ArrayList<QueryItem>();
        for (DyformFieldDefinition fieldDefinition : fieldDefinitions) {
            QueryItem queryItem = new QueryItem();
            queryItem.put("id", fieldDefinition.getName());
            queryItem.put("name", fieldDefinition.getDisplayName());
            queryItems.add(queryItem);
        }
        return queryItems;
    }

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
        return this.dao.query(QUERY_ACTIVE_DUTY_AGENT, values);
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
        return this.dao.query(QUERY_ACTIVE_DUTY_AGENTS, values);
    }

}
