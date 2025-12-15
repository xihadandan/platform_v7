/*
 * @(#)2019年6月6日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.manager.facade.service;

import com.wellsoft.context.component.tree.OrgNode;
import com.wellsoft.context.dto.DataItem;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.app.manager.dto.AppPageDefinitionDto;

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
 * 2019年6月6日.1	zhulh		2019年6月6日		Create
 * </pre>
 * @date 2019年6月6日
 */
public interface AppPageDefinitionManager extends BaseService {

    /**
     * 保存
     *
     * @param appPageDefinitionDto
     */
    void saveDto(AppPageDefinitionDto appPageDefinitionDto);

    /**
     * 保存工作台 使用者
     *
     * @param uuid   工作台Id
     * @param eleIds 节点id（组织节点，用户 角色uuid）
     */
    void saveEleIds(String appPiUuid, String uuid, List<String> eleIds);

    /**
     * 查询工作台使用者
     *
     * @param uuid
     * @return
     */
    List<OrgNode> getEleIds(String appPiUuid, String uuid);

    /**
     * 保存为新版本
     *
     * @param appPageDefinitionDto
     */
    void saveNewVersion(AppPageDefinitionDto appPageDefinitionDto);

    /**
     * @param uuid
     * @return
     */
    AppPageDefinitionDto getDto(String uuid);

    /**
     * 获取页面设计器列表数据
     *
     * @return
     */
    List<DataItem> getPageDesignerList();

    /**
     * 页面复制
     *
     * @param appPiUuid
     * @param copyPageUuid
     */
    void copyPageDefinition(String appPiUuid, String copyPageUuid, String copyPageId);

    /**
     * 页面引用
     *
     * @param appPiUuid
     * @param refPageUuid
     */
    void refPageDefinition(String appPiUuid, String refPageUuid);

    /**
     * 获取页面引用信息
     *
     * @param refPageUuid
     */
    Map<String, Object> getRefPageInfo(String refPageUuid);

    /**
     * 根据页面UUID删除页面
     *
     * @param uuid
     */
    void remove(String uuid);

    /**
     * 取消页面引用
     *
     * @param pageUuid
     * @param appPiUuid
     */
    void cancelRef(String pageUuid, String appPiUuid);

}
