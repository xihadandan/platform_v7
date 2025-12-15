/*
 * @(#)2012-12-3 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.Page;
import com.wellsoft.context.util.TextUtils;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.template.TemplateEngine;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.dao.FlowFormatDao;
import com.wellsoft.pt.workflow.entity.FlowFormat;
import com.wellsoft.pt.workflow.service.FlowFormatService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.htmlparser.Parser;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.TextExtractingVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-12-3.1	zhulh		2012-12-3		Create
 * </pre>
 * @date 2012-12-3
 */
@Service
public class FlowFormatServiceImpl extends AbstractJpaServiceImpl<FlowFormat, FlowFormatDao, String> implements
        FlowFormatService {

    private static final String WORKFLOW_CACHE_NAME = "Work flow";
    private static Logger LOG = LoggerFactory.getLogger(FlowFormatServiceImpl.class);

    /**
     * Java过滤HTML标签实例
     *
     * @param inputString
     * @return
     */
    @Deprecated
    public static String html2Text(String inputString) {
        if (StringUtils.isBlank(inputString)) {
            return StringUtils.EMPTY;
        }
        Parser parser;
        String textStr = inputString;
        try {
            parser = new Parser(inputString);
            parser.setEncoding("UTF-8");
            // 创建StringFindingVisitor对象
            TextExtractingVisitor visitor = new TextExtractingVisitor();
            // 去除网页中的所有标签,提出纯文本内容
            parser.visitAllNodesWith(visitor);
            textStr = visitor.getExtractedText();
        } catch (ParserException e) {
            LOG.error(ExceptionUtils.getStackTrace(e));
        }
        return textStr;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowFormatService#get(java.lang.String)
     */
    @Override
    public FlowFormat get(String uid) {
        return dao.getOne(uid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowFormatService#getByCode(java.lang.String)
     */
    @Override
    @Cacheable(value = WORKFLOW_CACHE_NAME)
    public FlowFormat getByCode(String code) {
        return dao.getByCode(code);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowFormatService#getAll(java.lang.Integer, java.lang.Integer)
     */
    @Override
    public Page<FlowFormat> getAll(Integer page, Integer rows) {
        Page<FlowFormat> dataPage = new Page<FlowFormat>();
        dataPage.setPageNo(page);
        dataPage.setPageSize(rows);
        return dao.findPage(dataPage);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowFormatService#save(com.wellsoft.pt.workflow.entity.FlowFormat)
     */
    @Override
    @Transactional
    @CacheEvict(value = WORKFLOW_CACHE_NAME, allEntries = true)
    public void save(FlowFormat entity) {
        FlowFormat format = new FlowFormat();
        if (StringUtils.isNotBlank(entity.getUuid())) {
            format = this.dao.getOne(entity.getUuid());
        } else {
            // 编号唯一性判断
            if (CollectionUtils.isNotEmpty(dao.listByFieldEqValue("code", entity.getCode()))) {
                throw new RuntimeException("已经存在编号为[" + entity.getCode() + "]的信息格式!");
            }
            entity.setSystem(RequestSystemContextPathResolver.system());
            entity.setTenant(SpringSecurityUtils.getCurrentTenantId());
        }
        BeanUtils.copyProperties(entity, format);
        dao.save(format);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowFormatService#saveAll(java.util.Collection)
     */
    @Override
    @Transactional
    @CacheEvict(value = WORKFLOW_CACHE_NAME, allEntries = true)
    public List<FlowFormat> saveAll(Collection<FlowFormat> entities) {
        for (FlowFormat format : entities) {
            this.save(format);
        }
        return Arrays.asList(entities.toArray(new FlowFormat[0]));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowFormatService#remove(com.wellsoft.pt.workflow.entity.FlowFormat)
     */
    @Override
    @Transactional
    @CacheEvict(value = WORKFLOW_CACHE_NAME, allEntries = true)
    public void remove(FlowFormat entity) {
        dao.delete(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowFormatService#removeAll(java.util.Collection)
     */
    @Override
    @Transactional
    @CacheEvict(value = WORKFLOW_CACHE_NAME, allEntries = true)
    public void removeAll(Collection<FlowFormat> entities) {
        for (FlowFormat format : entities) {
            this.remove(format);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowFormatService#removeByPk(java.lang.String)
     */
    @Override
    @Transactional
    @CacheEvict(value = WORKFLOW_CACHE_NAME, allEntries = true)
    public void removeByPk(String uid) {
        dao.delete(uid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowFormatService#removeAllByPk(java.util.Collection)
     */
    @Override
    @Transactional
    @CacheEvict(value = WORKFLOW_CACHE_NAME, allEntries = true)
    public void removeAllByPk(Collection<String> uids) {
        for (String uid : uids) {
            this.removeByPk(uid);
        }
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowFormatService#existsFormatCode(java.lang.String)
     */
    @Override
    public boolean existsFormatCode(String code) {
        return dao.getByCode(code) != null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowFormatService#getFormatValue(java.lang.String, java.util.List, java.util.Map)
     */
    @Override
    public String getFormatValue(String code, List<IdEntity> entities, Map<?, ?> dytableMap,
                                 Map<String, Object> extraData) {
        String result = "";
        try {
            FlowFormat format = this.dao.getByCode(code);
            String source = format.getValue();

            TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
            Map<Object, Object> root = templateEngine.mergeDataAsMap(entities, dytableMap, extraData, true, true);
            root.put("currentUserId", SpringSecurityUtils.getCurrentUserId());
            root.put("currentUsername", StringUtils.isNotBlank(SpringSecurityUtils.getCurrentLocalUserName()) ? SpringSecurityUtils.getCurrentLocalUserName() : SpringSecurityUtils.getCurrentUserName());
            root.put("currentTime", DateUtils.formatDateTime(Calendar.getInstance().getTime()));
            root.put("currentDate", DateUtils.formatDate(Calendar.getInstance().getTime()));
            root.put("currentUserDepartmentName", SpringSecurityUtils.getCurrentUserDepartmentName());
            root.put("currentUserDepartmentPath", SpringSecurityUtils.getCurrentUserDepartmentPath());
            root.put("currentUserJobName", SpringSecurityUtils.getCurrentUserJobName());
            //FIXME: 是否要替换为 v-html 对国际化内容输出
            /*// 替换里面的变量支持 v-html 的国际化翻译
            if (extraData.containsKey("taskId")) {
                source = source.replace("${taskName}", "<label v-html=\"$t('" + extraData.get("taskId").toString() + ".taskName'),'${taskName}'" + ")\">${taskName}</label>");
            }*/
            result = templateEngine.process(templateEngine.decorateSource(source), root);


            // 清空HTML格式
            if (Boolean.TRUE.equals(format.getIsClear())) {
                result = TextUtils.html2Text(result);
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return result;
    }


    @Override
    public List<FlowFormat> listBySystem(String system) {
        String hql = "from FlowFormat t where 1 = 1";
        Map<String, Object> params = Maps.newHashMap();
        if (StringUtils.isNotBlank(system)) {
            hql += " and t.system = :system";
            params.put("system", system);
        }
        hql += " order by t.code asc";
        return this.dao.listByHQL(hql, params);
    }

}
