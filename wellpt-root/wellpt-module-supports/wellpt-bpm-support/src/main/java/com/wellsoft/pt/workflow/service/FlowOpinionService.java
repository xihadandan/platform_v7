/*
 * @(#)2013-7-30 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.workflow.bean.FlowOpinionBean;
import com.wellsoft.pt.workflow.bean.FlowOpinionCategoryBean;
import com.wellsoft.pt.workflow.dao.FlowOpinionDao;
import com.wellsoft.pt.workflow.entity.FlowOpinion;
import com.wellsoft.pt.workflow.entity.FlowOpinionCategory;

import java.util.Collection;
import java.util.List;

/**
 * Description: 如何描述该类
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
public interface FlowOpinionService extends JpaService<FlowOpinion, FlowOpinionDao, String> {

    /**
     * 获取用户的意见立场分类，如果没有，返回个人意见分类
     *
     * @return
     */
    List<FlowOpinionCategoryBean> getOpinionCategoryBeans();

    /**
     * 添加默认办理意见
     */
    void addDefaultFlowOpinion();

    /**
     * 通过意见立场分类获取意见立场
     *
     * @param categoryUuid
     * @return
     */
    List<FlowOpinionBean> getOpinionBeanByCategory(String categoryUuid);

    /**
     * 获取所有的意见立场分类
     *
     * @return
     */
    List<FlowOpinionCategoryBean> getAllOpinionCategoryBeans();

    /**
     * 保存意见立场
     *
     * @param bean
     */
    void saveFlowOpinionCategoryBeans(FlowOpinionCategoryBean[] beans, Collection<String> deletedCategoryUuids);

    /**
     * 根据用户ID，添加使用的意见分类
     *
     * @param userId
     * @return
     */
    FlowOpinionCategory addRecentCategory(String userId);

    /**
     * 添加用户的最近签署意见
     *
     * @param userId
     * @param flowDefId
     * @param taskId
     * @param opinionText
     */
    FlowOpinion addRecentOpinion(String userId, String flowDefId, String taskId, String opinionText);

    /**
     * 根据用户ID，获取用户最近使用的意见
     *
     * @param userId
     * @param flowDefId
     * @param taskId
     * @return
     */
    List<FlowOpinion> getUserRecentOpinions(String userId, String flowDefId, String taskId);

    /**
     * 根据用户ID，获取用户最近使用的意见
     *
     * @param userId
     * @param flowDefId
     * @param taskId
     * @param num       返回指定条数
     * @return
     */
    List<FlowOpinion> getUserRecentOpinions(String userId, String flowDefId, String taskId, Integer num);

    /**
     * 获取公共意见信息
     *
     * @return
     */
    FlowOpinionCategoryBean getPublicOpinionCategory();

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    void deleteByCategoryUuid(String uuid);

    /**
     * 删除用户的最近意见
     *
     * @param content
     * @param userId
     */
    void deleteUserRecentOption(String content, String userId);

    List<FlowOpinion> getByOpinionCategory(String categoryUuid);

    @Override
    void save(FlowOpinion entity);

    @Override
    void deleteByUuids(List<String> strings);

    @Override
    FlowOpinion getOne(String uuid);

    /**
     * 删除多于10条的最近办理意见
     *
     * @param userId
     * @param flowDefId
     * @param taskId
     */
    void removeMoreThanTenRecentOpinion(String userId, String flowDefId, String taskId);
}
