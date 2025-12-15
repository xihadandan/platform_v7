/*
 * @(#)2016-05-09 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.facade.service;

import com.wellsoft.context.annotation.Description;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.app.bean.AppPageDefinitionBean;
import com.wellsoft.pt.app.bean.AppPageDefinitionParamDto;
import com.wellsoft.pt.app.bean.AppPageDefinitionPathBean;
import com.wellsoft.pt.app.entity.AppPageDefinition;
import com.wellsoft.pt.app.entity.AppWidgetDefinition;
import com.wellsoft.pt.app.ui.client.widget.configuration.AppWidgetDefinitionElement;
import com.wellsoft.pt.app.ui.client.widget.configuration.FunctionElement;
import org.springframework.security.core.AuthenticationException;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author t
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期            修改内容
 * 2016-05-09.1 t       2016-05-09      Create
 * </pre>
 * @date 2016-05-09
 */
public interface AppPageDefinitionMgr extends BaseService {

    /**
     * 获取
     *
     * @param uuid
     * @return
     */
    AppPageDefinitionBean getBean(String uuid);

    /**
     * 保存
     *
     * @param bean
     * @return
     */
    AppPageDefinition saveBean(AppPageDefinitionBean bean);

    /**
     * 保存新版本
     *
     * @param bean
     */
    AppPageDefinition saveNewVersion(AppPageDefinitionBean bean);

    /**
     * 根据页面定义JSON保存定义信息，返回页面定义UUID
     *
     * @param piUuid
     * @param definitionJson
     */
    String saveDefinitionJson(String piUuid, String definitionJson, String pageId);

    /**
     * 根据页面定义JSON保存定义信息，返回页面定义UUID
     *
     * @param piUuid
     * @param definitionJson
     * @param newVersion
     * @return
     */
    String saveDefinitionJson(String piUuid, String definitionJson, boolean newVersion);


    void syncPageResource(AppPageDefinition pageDefinition, List<AppWidgetDefinition> widgetDefinitions);

    /**
     * 复制页面定义，返回新的页面定义UUID
     *
     * @param sourcePageUuid
     * @return
     */
    String copyPageDefinition(String sourcePageUuid) throws Exception;

    /**
     * 复制页面定义，到指定的页面UUID
     *
     * @param sourcePageUuid
     * @param targetPageUuid
     * @return
     * @throws Exception
     */
    String copyPageDefinition(String sourcePageUuid, String targetPageUuid, String copyPageId) throws Exception;

    String getCopyPageDefinionJson(String sourcePageUuid, String pageId) throws Exception;

    /**
     * 保存用户页面定义
     *
     * @param sourcePageUuid
     * @param name
     * @param theme
     * @param definitionJson
     * @return
     */
    @Description("保存用户门户页面")
    String saveUserPageDefinitionJson(String sourcePageUuid, String name, String theme, String definitionJson);

    /**
     * 删除
     *
     * @param uuid
     * @return
     */
    void remove(String uuid);

    /**
     * 批量删除
     *
     * @param uuid
     * @return
     */
    void removeAll(Collection<String> uuids);

    List<String> loadPageResouces();

    List<QueryItem> listUserSystemPageDefinition(Map<String, Object> queryParams);

    /**
     * 后台查询用户工作台
     *
     * @param userUuid
     * @param source   0：全部，1：用户，2：组织，3：角色，4：默认
     * @param keyword
     * @return
     */
    List<AppPageDefinitionPathBean> listPath(String userUuid, String source, String keyword);

    /**
     * 前台查询用户工作台
     */
    List<AppPageDefinitionPathBean> listFacade();

    /**
     * 根据产品集成路径，前台查询用户工作台
     */
    List<AppPageDefinitionPathBean> listFacadeByAppPath(String appPiPath);

    /**
     * 根据角色Ids 查询工作台列表
     *
     * @param roleIds
     * @return
     */
    List<AppPageDefinition> queryByRoleIds(Collection<String> roleIds);

    /**
     * 根据事件处理路径判断是否支持页面锚点
     *
     * @param appPath
     * @return
     */
    boolean isSupportsAppHashByAppPath(String appPath);

    /**
     * 根据事件处理路径获取对应的页面锚点树
     *
     * @param appPath
     * @return
     */
    List<TreeNode> getAppHashTreeByAppPath(String appPath);

    @SuppressWarnings({"unchecked"})
    String getWidgetDefinitionByAppPath(String appPath);

    String saveDefinitionJson(String piUuid, String definitionJson, boolean newVersion, HashMap<String, ArrayList<FunctionElement>> functionElements, String name, String id, List<AppWidgetDefinitionElement> appWidgetDefinitionElements);

    String savePageDefinition(AppPageDefinitionParamDto params);

    AppPageDefinition authenticatePage(String uuid, String id) throws AuthenticationException;

    List<String> getUnauthorizedAppPageResource(String pageUuid);
}
