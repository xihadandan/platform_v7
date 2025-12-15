/*
 * @(#)2018年4月9日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.web.JsonDataServicesContextHolder;
import com.wellsoft.context.util.web.ServletUtils;
import com.wellsoft.pt.bpm.engine.dao.FlowSchemaDao;
import com.wellsoft.pt.bpm.engine.entity.FlowSchema;
import com.wellsoft.pt.bpm.engine.management.dao.FlowSchemaLogDao;
import com.wellsoft.pt.bpm.engine.management.entity.FlowSchemaLog;
import com.wellsoft.pt.bpm.engine.parser.FlowConfiguration;
import com.wellsoft.pt.bpm.engine.service.FlowSchemaService;
import com.wellsoft.pt.document.MongoDocumentService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.timer.dto.TsTimerConfigDto;
import com.wellsoft.pt.timer.dto.TsWorkTimePlanDto;
import com.wellsoft.pt.timer.facade.service.TsTimerConfigFacadeService;
import com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService;
import com.wellsoft.pt.workflow.service.FlowSchemeService;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月9日.1	chenqiong		2018年4月9日		Create
 * </pre>
 * @date 2018年4月9日
 */
@Service
public class FlowSchemaServiceImpl extends AbstractJpaServiceImpl<FlowSchema, FlowSchemaDao, String>
        implements FlowSchemaService {

    @Autowired
    private TsWorkTimePlanFacadeService workTimePlanFacadeService;

    @Autowired
    private TsTimerConfigFacadeService timerConfigFacadeService;

    @Autowired
    private MongoDocumentService mongoDocumentService;

    @Autowired
    private MongoFileService mongoFileService;

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    @Autowired
    private FlowSchemaLogDao flowSchemaLogDao;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.support.TsWorkTimePlanUsedChecker#isWorkTimePlanUsed(java.lang.String)
     */
    @Override
    public boolean isWorkTimePlanUsed(String workTimePlanUuid) {
        TsWorkTimePlanDto workTimePlanDto = workTimePlanFacadeService.getDto(workTimePlanUuid);
        Date createTime = workTimePlanDto.getCreateTime();
        String workTimePlanId = workTimePlanDto.getId();
        String hql = "select count(t.uuid) from FlowSchema t where t.modifyTime > :timePoint and t.content like '%" + workTimePlanId + "%'";
        Map<String, Object> values = Maps.newHashMap();
        values.put("timePoint", createTime != null ? createTime : Calendar.getInstance().getTime());
        return this.dao.countByHQL(hql, values) > 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.support.TsTimerConfigUsedChecker#isTimerConfigUsed(java.lang.String)
     */
    @Override
    public boolean isTimerConfigUsed(String configUuid) {
        TsTimerConfigDto timerConfigDto = timerConfigFacadeService.getDto(configUuid);
        Date createTime = timerConfigDto.getCreateTime();
        String hql = "select count(t.uuid) from FlowSchema t where t.modifyTime > :timePoint and t.content like '%" + configUuid + "%'";
        Map<String, Object> values = Maps.newHashMap();
        values.put("timePoint", createTime != null ? createTime : Calendar.getInstance().getTime());
        return this.dao.countByHQL(hql, values) > 0;
    }

    @Override
    @Transactional
    public String save(String uuid, String name, FlowConfiguration configuration) {
        FlowSchema flowSchema = null;
        if (StringUtils.isNotBlank(uuid)) {
            return update(uuid, name, configuration);
        } else {
            flowSchema = new FlowSchema();
            flowSchema.setName(name);
            String configXml = configuration.asXML();
            if (StringUtils.isNotBlank(configXml)) {
                flowSchema.setContent(Hibernate.getLobCreator(dao.getSession()).createClob(configXml));
            }
            String configJson = configuration.asJSON();
            if (StringUtils.isNotBlank(configJson)) {
                flowSchema.setDefinitionJson(Hibernate.getLobCreator(dao.getSession()).createClob(configJson));
            }
            this.dao.save(flowSchema);
            this.saveLog(flowSchema.getUuid(), flowSchema.getName(), flowSchema.getContentAsString(), flowSchema.getDefinitionJsonAsString(),
                    configuration, true, false);
            return flowSchema.getUuid();
        }
    }

    /**
     * @param uuid
     * @param name
     * @param configuration
     * @return
     */
    private String update(String uuid, String name, FlowConfiguration configuration) {
        FlowSchema flowSchema = this.dao.getOne(uuid);
        String configXml = configuration.asXML();
        String configJson = configuration.asJSON();
        // 流程定义大于两百万字符，则异步更新
        if (StringUtils.length(configXml) > 2000000 || StringUtils.length(configJson) > 2000000) {
            this.dao.getSession().evict(flowSchema);
            asyncUpdate(uuid, name, configXml, configJson);
            this.dao.getSession().flush();
            this.dao.getSession().clear();
        } else {
            flowSchema.setName(name);
            flowSchema.setContent(Hibernate.getLobCreator(dao.getSession()).createClob(configXml));
            flowSchema.setDefinitionJson(Hibernate.getLobCreator(dao.getSession()).createClob(configJson));
            this.dao.save(flowSchema);
        }
        return flowSchema.getUuid();
    }

    /**
     * @param uuid
     * @param name
     * @param configXml
     * @param configJson
     */
    private void asyncUpdate(String uuid, String name, String configXml, String configJson) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        scheduledExecutorService.submit(() -> {
            try {
                IgnoreLoginUtils.login(userDetails);
                ApplicationContextHolder.getBean(FlowSchemaService.class).update(uuid, name, configXml, configJson);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                ApplicationContextHolder.getBean(FlowSchemeService.class).clearFlowElementCache(uuid);
                IgnoreLoginUtils.logout();
            }
        });
    }

    /**
     * @param uuid
     * @param name
     * @param configXml
     * @param configJson
     */
    @Override
    @Transactional
    public void update(String uuid, String name, String configXml, String configJson) {
        synchronized (uuid) {
            FlowSchema flowSchema = this.getOne(uuid);
            flowSchema.setName(name);
            flowSchema.setContent(Hibernate.getLobCreator(dao.getSession()).createClob(configXml));
            flowSchema.setDefinitionJson(Hibernate.getLobCreator(dao.getSession()).createClob(configJson));
            this.save(flowSchema);
            this.dao.getSession().flush();
            this.dao.getSession().clear();
        }
    }

    @Override
    @Transactional
    public void saveLog(String uuid, String name, String oldXml, String oldJson, FlowConfiguration configuration, boolean isCreate, boolean allowAsync) {
        if (allowAsync && (StringUtils.length(oldXml) > 2000000 || StringUtils.length(oldJson) > 2000000)) {
            asyncSaveLog(uuid, name, oldXml, oldJson, configuration, isCreate);
        } else {
            FlowSchemaLog flowSchemaLog = new FlowSchemaLog();
            flowSchemaLog.setContent(oldXml);
            flowSchemaLog.setDefinitionJson(oldJson);
            flowSchemaLog.setCreateTime(Calendar.getInstance().getTime());
            flowSchemaLog.setParentFlowSchemaUUID(uuid);
            flowSchemaLog.setFlowVersion(flowSchemaLogDao.genVersionByParentUuid(uuid, configuration.getFlowElement()));
            UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
            String remoteAddr = ServletUtils.getRemoteAddr(JsonDataServicesContextHolder.getRequest());
            if (isCreate) {
                flowSchemaLog.setLog(userDetails.getUserName() + "/" + remoteAddr + ",创建了:" + name + ",版本："
                        + flowSchemaLog.getFlowVersion());
            } else {
                flowSchemaLog.setLog(userDetails.getUserName() + "/" + remoteAddr + ",更新了:" + name
                        + ",升级到小版本：" + flowSchemaLog.getFlowVersion());
            }
            flowSchemaLogDao.save(flowSchemaLog);
        }
    }

    /**
     * @param uuid
     * @param name
     * @param oldXml
     * @param oldJson
     * @param configuration
     * @param isCreate
     */
    private void asyncSaveLog(String uuid, String name, String oldXml, String oldJson, FlowConfiguration configuration, boolean isCreate) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        scheduledExecutorService.submit(() -> {
            try {
                IgnoreLoginUtils.login(userDetails);
                ApplicationContextHolder.getBean(FlowSchemaService.class).saveLog(uuid, name, oldXml, oldJson, configuration, isCreate, false);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                IgnoreLoginUtils.logout();
            }
        });
    }

}
