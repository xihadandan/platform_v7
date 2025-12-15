/*
 * @(#)2013-7-30 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datadict.dto.CdDataDictionaryItemDto;
import com.wellsoft.pt.basicdata.datadict.facade.service.CdDataDictionaryFacadeService;
import com.wellsoft.pt.basicdata.params.facade.SystemParams;
import com.wellsoft.pt.basicdata.selective.facade.SelectiveDatas;
import com.wellsoft.pt.basicdata.selective.support.DataItem;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.bean.FlowOpinionBean;
import com.wellsoft.pt.workflow.bean.FlowOpinionCategoryBean;
import com.wellsoft.pt.workflow.dao.FlowOpinionDao;
import com.wellsoft.pt.workflow.entity.FlowOpinion;
import com.wellsoft.pt.workflow.entity.FlowOpinionCategory;
import com.wellsoft.pt.workflow.service.FlowOpinionCategoryService;
import com.wellsoft.pt.workflow.service.FlowOpinionService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 意见立场服务类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-7-30.1	zhulh		2013-7-30		Create
 * </pre>
 * @date 2013-7-30
 */
@Service
public class FlowOpinionServiceImpl extends AbstractJpaServiceImpl<FlowOpinion, FlowOpinionDao, String> implements
        FlowOpinionService {

    // 启用公共意见
    private static final String KEY_ENABLE_PUBLIC_FLOW_OPINION = "workflow.work.public.opinion.enable";
    // 公共意见
    private static final String CONFIG_KEY_PUBLIC_FLOW_OPINION = "PUBLIC_FLOW_OPINION";
    // 初始化的流程意见
    private static final String CONFIG_KEY_PERSONAL_INIT_FLOW_OPINION = "PERSONAL_INIT_FLOW_OPINION";

    private static final String ID_PUBLIC = "public";

    // 个人最近使用的意见分类前缀
    private static final String ID_RECENT_PREFIX = "recent_";

    @Autowired
    private FlowOpinionCategoryService flowOpinionCategoryService;

    @Autowired
    private CdDataDictionaryFacadeService dataDictionaryFacadeService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowOpinionService#getOpinionCategoryBeans()
     */
    @Override
    public List<FlowOpinionCategoryBean> getOpinionCategoryBeans() {
        String userId = SpringSecurityUtils.getCurrentUserId();
        List<FlowOpinionCategory> categories = flowOpinionCategoryService
                .getUserOpinionCategoriesWithoutRecentCategory(userId);
        if (categories.isEmpty()) {
            // 增加默认的流程个人意见分类
            addDefaultFlowOpinion();
            categories = flowOpinionCategoryService.getUserOpinionCategoriesWithoutRecentCategory(userId);
        }
        List<FlowOpinionCategoryBean> beans = new ArrayList<FlowOpinionCategoryBean>();
        for (FlowOpinionCategory flowOpinionCategory : categories) {
            FlowOpinionCategoryBean bean = new FlowOpinionCategoryBean();
            BeanUtils.copyProperties(flowOpinionCategory, bean);
            bean.setOpinions(this.getOpinionBeanByCategory(flowOpinionCategory.getUuid()));
            beans.add(bean);
        }
        return beans;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowOpinionService#deleteByCategoryUuid(java.lang.String)
     */
    @Override
    @Transactional
    public void deleteByCategoryUuid(String uuid) {
        dao.deleteByCategoryUuid(uuid);
    }

    @Override
    @Transactional
    public void deleteUserRecentOption(String content, String userId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("content", content);
        params.put("categoryId", ID_RECENT_PREFIX + userId);
        dao.deleteByNamedSQL("deleteUserRecentOption", params);
    }

    /**
     * @param categories
     * @return
     */
    private boolean hasPublic(List<FlowOpinionCategory> categories) {
        for (FlowOpinionCategory flowOpinionCategory : categories) {
            if (ID_PUBLIC.equals(flowOpinionCategory.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return
     */
    private FlowOpinionCategoryBean addPublicCategory() {
        FlowOpinionCategoryBean category = new FlowOpinionCategoryBean();
        category.setCode("000");
        category.setId(ID_PUBLIC);
        category.setName("公共意见");
        // this.flowOpinionCategoryService.save(category);
        return category;
    }

    /**
     * @param publicCategory
     * @return
     */
    private FlowOpinionCategory addPublicCategoryOpinion(FlowOpinionCategoryBean publicCategory) {
        List<FlowOpinionBean> opinions = new ArrayList<FlowOpinionBean>();
        List<CdDataDictionaryItemDto> itemDtos = dataDictionaryFacadeService.listItemByDictionaryCode(CONFIG_KEY_PUBLIC_FLOW_OPINION);
        itemDtos.forEach(itemDto -> {
            FlowOpinionBean opinion = new FlowOpinionBean();
            opinion.setUuid(itemDto.getUuid() + StringUtils.EMPTY);
            opinion.setContent(itemDto.getLabel());
            opinion.setCode(itemDto.getValue());
            opinions.add(opinion);
        });
//        List<DataItem> items = SelectiveDatas.getItems(CONFIG_KEY_PUBLIC_FLOW_OPINION, DataItem.class);
//        if (!items.isEmpty()) {
//            for (DataItem dataItem : items) {
//                FlowOpinionBean opinion = new FlowOpinionBean();
//                opinion.setUuid(dataItem.getAceId());
//                opinion.setContent(dataItem.getLabel());
//                opinion.setCode(dataItem.getValue() + "");
//                opinions.add(opinion);
//            }
//        }
        publicCategory.setOpinions(opinions);
        return publicCategory;
    }

    /**
     * 如何描述该方法
     */
    @Override
    @Transactional
    public void addDefaultFlowOpinion() {
        FlowOpinionCategory category = new FlowOpinionCategory();
        category.setCode("001");
        category.setId("001");
        category.setName("个人意见");
        this.flowOpinionCategoryService.save(category);

        List<DataItem> items = SelectiveDatas.getItems(CONFIG_KEY_PERSONAL_INIT_FLOW_OPINION, DataItem.class);
        if (!items.isEmpty()) {
            for (DataItem dataItem : items) {
                FlowOpinion opinion = new FlowOpinion();
                opinion.setContent(dataItem.getLabel());
                opinion.setCode(dataItem.getValue() + "");
                opinion.setOpinionCategoryUuid(category.getUuid());
                this.dao.save(opinion);
            }
        } else {
            FlowOpinion opinion1 = new FlowOpinion();
            opinion1.setContent("同意");
            opinion1.setCode("001");
            opinion1.setOpinionCategoryUuid(category.getUuid());
            this.dao.save(opinion1);

            FlowOpinion opinion2 = new FlowOpinion();
            opinion2.setContent("不同意");
            opinion2.setCode("002");
            opinion2.setOpinionCategoryUuid(category.getUuid());
            this.dao.save(opinion2);

            FlowOpinion opinion3 = new FlowOpinion();
            opinion3.setContent("拒绝");
            opinion3.setCode("003");
            opinion3.setOpinionCategoryUuid(category.getUuid());
            this.dao.save(opinion3);
        }
        flushSession();
        clearSession();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowOpinionService#getOpinionBeanByCategory(java.lang.String)
     */
    @Override
    public List<FlowOpinionBean> getOpinionBeanByCategory(String categoryUuid) {
        FlowOpinion entity = new FlowOpinion();
        entity.setOpinionCategoryUuid(categoryUuid);
        List<FlowOpinion> opinions = this.dao.listByEntityAndPage(entity, null, "seq asc");
        List<FlowOpinionBean> beans = new ArrayList<FlowOpinionBean>();
        for (FlowOpinion flowOpinion : opinions) {
            FlowOpinionBean bean = new FlowOpinionBean();
            BeanUtils.copyProperties(flowOpinion, bean);
            beans.add(bean);
        }
        return beans;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowOpinionService#getAllOpinionCategoryBeans()
     */
    @Override
    public List<FlowOpinionCategoryBean> getAllOpinionCategoryBeans() {
        String userId = SpringSecurityUtils.getCurrentUserId();
        List<FlowOpinionCategory> categories = flowOpinionCategoryService
                .getUserOpinionCategoriesWithoutRecentCategory(userId);
        if (categories.isEmpty()) {
            // 增加默认的流程分类
            addDefaultFlowOpinion();
            categories = flowOpinionCategoryService.getUserOpinionCategoriesWithoutRecentCategory(userId);
        }

        List<FlowOpinionCategoryBean> beans = new ArrayList<FlowOpinionCategoryBean>();
        // 添加公共意见分类
        String enablePublicOpinion = SystemParams.getValue(KEY_ENABLE_PUBLIC_FLOW_OPINION);
        if (Config.TRUE.equalsIgnoreCase(enablePublicOpinion) && !hasPublic(categories)) {
            categories.add(0, addPublicCategory());
        }
        for (FlowOpinionCategory flowOpinionCategory : categories) {
            // 设置意见立场分类
            FlowOpinionCategoryBean bean = new FlowOpinionCategoryBean();
            BeanUtils.copyProperties(flowOpinionCategory, bean);

            // 加载公共意见
            if (ID_PUBLIC.equals(flowOpinionCategory.getId())) {
                addPublicCategoryOpinion(bean);
                beans.add(bean);
                continue;
            }

            // 设置意见
            bean.setOpinions(getOpinionBeanByCategory(flowOpinionCategory.getUuid()));

            beans.add(bean);
        }
        return beans;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowOpinionService#saveFlowOpinionCategoryBean(com.wellsoft.pt.workflow.bean.FlowOpinionCategoryBean)
     */
    @Override
    @Transactional
    public void saveFlowOpinionCategoryBeans(FlowOpinionCategoryBean[] beans, Collection<String> deletedCategoryUuids) {
        for (FlowOpinionCategoryBean bean : beans) {
            FlowOpinionCategory flowOpinionCategory = new FlowOpinionCategory();
            if (StringUtils.isNotBlank(bean.getUuid())) {
                flowOpinionCategory = this.flowOpinionCategoryService.getOne(bean.getUuid());
            }
            BeanUtils.copyProperties(bean, flowOpinionCategory);
            this.flowOpinionCategoryService.save(flowOpinionCategory);

            // 删除已存在的意见
            this.dao.deleteByCategoryUuid(flowOpinionCategory.getUuid());

            int i = 0;
            for (FlowOpinion opinionBean : bean.getOpinions()) {
                FlowOpinion opinion = new FlowOpinion();
                opinion.setContent(StringUtils.trim(opinionBean.getContent()));
                opinion.setCode(opinionBean.getCode());
                opinion.setOpinionCategoryUuid(flowOpinionCategory.getUuid());
                opinion.setSeq(i++);
                this.dao.save(opinion);
            }
        }

        // 删除意见立场分类
        for (String categoryUuid : deletedCategoryUuids) {
            FlowOpinionCategory category = this.flowOpinionCategoryService.getOne(categoryUuid);
            if (category != null) {
                this.flowOpinionCategoryService.delete(categoryUuid);
            }
        }
    }

    /**
     * @param userId
     * @return
     */
    @Transactional
    public FlowOpinionCategory addRecentCategory(String userId) {
        String id = ID_RECENT_PREFIX + userId;
        FlowOpinionCategory example = new FlowOpinionCategory();
        example.setId(id);
        List<FlowOpinionCategory> categories = this.flowOpinionCategoryService.findByExample(example);
        if (categories.isEmpty()) {
            FlowOpinionCategory category = new FlowOpinionCategory();
            category.setCode("999");
            category.setId(ID_RECENT_PREFIX + userId);
            category.setName("最近使用");
            this.flowOpinionCategoryService.save(category);
            return category;
        }
        return categories.get(0);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowOpinionService#addRecentOpinion(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public FlowOpinion addRecentOpinion(String userId, String flowDefId, String taskId, String opinionText) {
        FlowOpinionCategory opinionCategory = addRecentCategory(userId);

        FlowOpinion example = new FlowOpinion();
        example.setContent(opinionText);
        example.setFlowDefId(flowDefId);
        example.setTaskId(taskId);
        example.setOpinionCategoryUuid(opinionCategory.getUuid());
        List<FlowOpinion> flowOpinions = this.dao.listByEntity(example);

        FlowOpinion opinion = new FlowOpinion();
        if (!flowOpinions.isEmpty()) {
            opinion = flowOpinions.get(0);
        }

        opinion.setContent(opinionText);
        opinion.setCode(Calendar.getInstance().getTimeInMillis() + StringUtils.EMPTY);
        opinion.setFlowDefId(flowDefId);
        opinion.setTaskId(taskId);
        opinion.setOpinionCategoryUuid(opinionCategory.getUuid());
        this.dao.save(opinion);

        return opinion;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowOpinionService#getUserRecentOpinions(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public List<FlowOpinion> getUserRecentOpinions(String userId, String flowDefId, String taskId) {

        List<FlowOpinion> flowOpinions = getUserRecentOpinions(userId, flowDefId, taskId, 10);

        return flowOpinions;
    }

    @Override
    public List<FlowOpinion> getUserRecentOpinions(String userId, String flowDefId, String taskId, Integer num) {
        if (num <= 0) {
            //默认10条
            num = 10;
        }
        String categoryId = ID_RECENT_PREFIX + userId;
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userId", userId);
        values.put("categoryId", categoryId);
        values.put("flowDefId", flowDefId);
        values.put("system", RequestSystemContextPathResolver.system());
        if (StringUtils.isNotBlank(taskId)) {
            values.put("taskId", taskId);
        }
        PagingInfo pagingInfo = new PagingInfo();
        pagingInfo.setCurrentPage(1);
        pagingInfo.setPageSize(num);
        pagingInfo.setAutoCount(false);
        List<FlowOpinion> flowOpinions = Lists.newArrayList();
        List<QueryItem> queryItems = Lists.newArrayList();
        Set<String> excludesContent = Sets.newLinkedHashSet();
        // 1、获取该环节最近使用的意见
        List<QueryItem> tempList = this.dao.listQueryItemByNameSQLQuery("getUserRecentOpinions", values, pagingInfo);
        if (CollectionUtils.isNotEmpty(tempList)) {
            queryItems.addAll(tempList);
            for (QueryItem item : tempList) {
                excludesContent.add(item.getString("content"));
            }
        }
        if (queryItems.size() < num) {
            // 2. 获取该流程最近使用的意见
            values.remove("taskId");
            values.put("excludesContent", excludesContent);
            pagingInfo.setPageSize(num - queryItems.size());
            tempList = this.dao.listQueryItemByNameSQLQuery("getUserRecentOpinions", values, pagingInfo);
            if (CollectionUtils.isNotEmpty(tempList)) {
                queryItems.addAll(tempList);
                for (QueryItem item : tempList) {
                    excludesContent.add(item.getString("content"));
                }
            }
            if (queryItems.size() < num) {
                // 3. 获取其他流程最近使用的意见
                values.remove("flowDefId");
                values.put("excludesContent", excludesContent);
                pagingInfo.setPageSize(num - queryItems.size());
                tempList = this.dao.listQueryItemByNameSQLQuery("getUserRecentOpinions", values, pagingInfo);
                if (CollectionUtils.isNotEmpty(tempList)) {
                    queryItems.addAll(tempList);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(queryItems)) {
            for (QueryItem item : queryItems) {
                FlowOpinion opinion = new FlowOpinion();
                opinion.setContent(item.getString("content"));
                flowOpinions.add(opinion);
            }
        }
        return flowOpinions;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowOpinionService#getPublicOpinionCategory()
     */
    @Override
    public FlowOpinionCategoryBean getPublicOpinionCategory() {
        FlowOpinionCategoryBean opinionCategory = null;
        // 添加公共意见分类
        String enablePublicOpinion = SystemParams.getValue(KEY_ENABLE_PUBLIC_FLOW_OPINION);
        if (Config.TRUE.equalsIgnoreCase(enablePublicOpinion)) {
            opinionCategory = addPublicCategory();
            addPublicCategoryOpinion(opinionCategory);
        }
        return opinionCategory;
    }


    @Override
    public List<FlowOpinion> getByOpinionCategory(String categoryUuid) {
        return this.dao.listByFieldEqValue("opinionCategoryUuid", categoryUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowOpinionService#removeMoreThanTenRecentOpinion(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public void removeMoreThanTenRecentOpinion(String userId, String flowDefId, String taskId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(flowDefId) || StringUtils.isBlank(taskId)) {
            return;
        }
        PagingInfo pagingInfo = new PagingInfo(1, 10000);
        pagingInfo.setFirst(10);
        String hql = "select t.uuid from FlowOpinion t where t.creator = :userId and t.flowDefId = :flowDefId and t.taskId = :taskId order by t.createTime desc";
        Map<String, Object> values = Maps.newHashMap();
        values.put("userId", userId);
        values.put("flowDefId", flowDefId);
        values.put("taskId", taskId);
        List<String> uuids = this.dao.listCharSequenceByHqlAndPage(hql, values, pagingInfo);
        if (CollectionUtils.isNotEmpty(uuids)) {
            this.dao.deleteByUuids(uuids);
        }
    }

}
