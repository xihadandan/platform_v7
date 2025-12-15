/*
 * @(#)2015-6-18 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.cg.service.impl;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.cg.entity.CodeGeneratorConfig;
import com.wellsoft.pt.cg.service.CodeGeneratorConfigService;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.workflow.work.service.WorkService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;

import javax.persistence.Entity;
import java.io.IOException;
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
 * 2015-6-18.1	zhulh		2015-6-18		Create
 * </pre>
 * @date 2015-6-18
 */
@Service
@Transactional
public class CodeGeneratorConfigServiceImpl extends BaseServiceImpl implements CodeGeneratorConfigService {
    protected static final TypeFilter ENTITY_TYPE_FILTER = new AnnotationTypeFilter(Entity.class, false);

    private static final String RESOURCE_PATTERN = "/**/*.class";
    private String packagesToScan = "com.wellsoft";

    @Autowired
    private ResourceLoader resourceLoader;
    private ResourcePatternResolver resourcePatternResolver;

    @Autowired
    private WorkService workService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.cg.service.CodeGeneratorConfigService#get(java.lang.String)
     */
    @Override
    public CodeGeneratorConfig get(String uuid) {
        return this.dao.get(CodeGeneratorConfig.class, uuid);
    }

    /**
     * 获得所有的系统表
     *
     * @return
     */
    protected List<Map> getAllDataBaseTables() {
        List tableList = null;
        try {
            Session s = dao.getSession();
            Transaction t = s.beginTransaction();
            tableList = s.createSQLQuery(
                    "select t.OBJECT_NAME,t.OBJECT_ID  from user_objects t where object_type='TABLE'").list();
            t.commit();
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return tableList;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.cg.service.CodeGeneratorConfigService#save(com.wellsoft.pt.cg.entity.CodeGeneratorConfig)
     */
    @Override
    public void save(CodeGeneratorConfig codeGeneratorConfig) {
        this.dao.save(codeGeneratorConfig);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.cg.service.CodeGeneratorConfigService#remove(java.lang.String)
     */
    @Override
    public void remove(String uuid) {
        this.dao.deleteByPk(CodeGeneratorConfig.class, uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.cg.service.CodeGeneratorConfigService#removeAll(java.util.Collection)
     */
    @Override
    public void removeAll(Collection<String> uuids) {
        for (String uuid : uuids) {
            this.dao.deleteByPk(CodeGeneratorConfig.class, uuid);
        }
    }

    private Collection<Class<?>> getObject() throws Exception {
        resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);

        Collection<Class<?>> annotatedClasses = new ArrayList<Class<?>>();
        String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                + ClassUtils.convertClassNameToResourcePath(packagesToScan) + RESOURCE_PATTERN;
        Resource[] resources = this.resourcePatternResolver.getResources(pattern);
        MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);
        for (Resource resource : resources) {
            if (resource.isReadable()) {
                MetadataReader reader = readerFactory.getMetadataReader(resource);
                String className = reader.getClassMetadata().getClassName();
                if (matchesFilter(reader, readerFactory)) {
                    annotatedClasses.add(this.resourcePatternResolver.getClassLoader().loadClass(className));
                }
            }
        }
        return annotatedClasses;
    }

    private boolean matchesFilter(MetadataReader reader, MetadataReaderFactory readerFactory) throws IOException {
        if (ENTITY_TYPE_FILTER.match(reader, readerFactory)) {
            return true;
        }
        return false;
    }

    @Override
    public List<TreeNode> getEntity(String a) {
        Collection<Class<?>> classes;
        try {
            classes = this.getObject();
            List<TreeNode> treeNodes = new ArrayList<TreeNode>();
            for (Iterator<Class<?>> iterator = classes.iterator(); iterator.hasNext(); ) {
                Class<?> object = (Class<?>) iterator.next();
                TreeNode treeNode = new TreeNode();
                treeNode.setData(object.getName());
                treeNode.setName(object.getName());
                treeNodes.add(treeNode);
            }
            return treeNodes;
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    @Override
    public List<TreeNode> getTables(String s) {
        List dataBaseTable = this.getAllDataBaseTables();
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        for (Iterator iterator = dataBaseTable.iterator(); iterator.hasNext(); ) {
            Object[] object = (Object[]) iterator.next();
            TreeNode treeNode = new TreeNode();
            treeNode.setName((String) object[0]);
            treeNode.setData((String) object[0]);
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.cg.service.CodeGeneratorConfigService#getDyforms(java.lang.String)
     */
    @Override
    public List<TreeNode> getDyforms(String uuid) {
        List<DyFormFormDefinition> dyFormDefinitions = this.dao.namedQuery("getAllDyFormDefinitionBasicInfo", null,
                DyFormFormDefinition.class);
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        for (DyFormFormDefinition dyFormDefinition : dyFormDefinitions) {
            TreeNode treeNode = new TreeNode();
            treeNode.setId(dyFormDefinition.getUuid());
            treeNode.setName(dyFormDefinition.getName());
            treeNode.setData(dyFormDefinition.getUuid());
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    @Override
    public List getFlows(String s) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        QueryInfo query = new QueryInfo();
        PagingInfo info = new PagingInfo();
        info.setPageSize(Integer.MAX_VALUE);
        info.setCurrentPage(0);
        query.setPagingInfo(info);
        List<FlowDefinition> list = workService.query(query);
        for (Iterator iterator = list.iterator(); iterator.hasNext(); ) {
            FlowDefinition object = (FlowDefinition) iterator.next();
            TreeNode treeNode = new TreeNode();
            treeNode.setName(object.getName() + "(" + Math.round(object.getVersion() * 10) / 10.0 + ")");
            treeNode.setData(object.getUuid());
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

}
