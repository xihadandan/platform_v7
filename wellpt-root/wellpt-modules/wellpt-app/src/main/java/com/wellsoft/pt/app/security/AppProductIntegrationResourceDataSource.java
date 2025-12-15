/*
 * @(#)Sep 6, 2016 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.security;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.app.entity.AppProduct;
import com.wellsoft.pt.app.facade.service.AppProductIntegrationMgr;
import com.wellsoft.pt.app.function.AppFunctionSourceManager;
import com.wellsoft.pt.app.service.AppProductIntegrationService;
import com.wellsoft.pt.app.service.AppProductService;
import com.wellsoft.pt.app.support.*;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.security.audit.support.AbstractResourceDataSource;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-09-06.1	zhulh		2016-09-06		Create
 * </pre>
 * @date 2016-09-06
 */
@Component
// 产品集成树 7.0 不再使用
@Deprecated
public class AppProductIntegrationResourceDataSource extends AbstractResourceDataSource {

    @Autowired
    private AppProductIntegrationMgr appProductIntegrationMgr;

    @Autowired
    private AppProductService appProductService;

    @Autowired
    private AppProductIntegrationService appProductIntegrationService;

    @Autowired
    private AppFunctionSourceManager appFunctionSourceManager;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.support.AbstractResourceDataSource#getName()
     */
    @Override
    public String getName() {
        return "产品集成信息";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.support.AbstractResourceDataSource#getId()
     */
    @Override
    public String getId() {
        return AppFunctionType.AppProductIntegration;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.support.AbstractResourceDataSource#getOrder()
     */
    @Override
    public int getOrder() {
        return 200;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.support.ResourceDataSource#getData()
     */
    @Override
    public List<TreeNode> getData(Map<String, Object> params) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        List<AppProduct> appProducts = appProductService.getAll();
        // 只能获取归属自己单位的产品和平台产品
        String userUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        boolean onlyProtected = params != null ? BooleanUtils.isTrue((Boolean) params.get("onlyProtected")) : false;
        for (AppProduct appProduct : appProducts) {
            String proUnitId = appProduct.getSystemUnitId();
            if (proUnitId.equals(userUnitId) || proUnitId.equals(MultiOrgSystemUnit.PT_ID)) {
                String productUuid = appProduct.getUuid();
                TreeNode treeNode = onlyProtected ? appProductIntegrationMgr.getProtectedTree(productUuid)
                        : appProductIntegrationMgr.getTree(productUuid);
                treeNodes.add(treeNode);
            }
        }
        return treeNodes;
        //        return getDataByAppSystemUuid("13757a20-6a99-4bb0-afe6-35b722cc958f");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.support.AbstractResourceDataSource#getDataByAppSystemUuid(java.lang.String)
     */
    @Override
    public List<TreeNode> getDataByAppSystemUuid(String appSystemUuid) {
        return appProductIntegrationMgr.getTreeByAppSystemUuid(appSystemUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.support.AbstractResourceDataSource#getObjectIdentities(java.lang.String)
     */
    @Override
    public Map<String, Collection<Object>> getObjectIdentities(String objectIdIdentity) {
        Map<String, Collection<Object>> objectIdentityMap = Maps.newHashMap();
        PiItem piItem = AppCacheUtils.getPiItem(objectIdIdentity);
        // AppProductIntegration pi =
        // appProductIntegrationService.get(objectIdIdentity);
        if (piItem == null) {
            return super.getObjectIdentities(objectIdIdentity);
        }

        // 添加公共受保护的集成信息
        addCommonObjectIdentities(piItem, objectIdentityMap);

        // 页面功能
        if (isPagePiItem(piItem)) {
            return addPageObjectIdentities(piItem, objectIdentityMap);
        } else if (isFunctionOfPagePiItem(piItem)) {
            return addFunctionOfPageObjectIdentities(piItem, objectIdentityMap);
        } else {
            // 添加功能受保护的集成信息
            return addFunctionObjectIdentities(piItem, objectIdentityMap);
        }
    }

    /**
     * 添加公共受保护的集成信息
     *
     * @param piItem
     * @param objectIdentityMap
     * @return
     */
    private void addCommonObjectIdentities(PiItem piItem, Map<String, Collection<Object>> objectIdentityMap) {
        Collection<Object> objects = Sets.newHashSet();
        String uuid = piItem.getUuid();
        String path = piItem.getPath();

        String functionType = getId();
        objects.add(uuid);
        objects.add(path);
        objects.add(AppConstants.WEB_APP_PATH + path + AppConstants.DOT_HTML);
        if (objectIdentityMap.containsKey(functionType)) {
            objectIdentityMap.get(functionType).addAll(objects);
        } else {
            objectIdentityMap.put(functionType, objects);
        }
    }

    /**
     * 页面受保护的集成信息
     *
     * @param piItem
     * @param objectIdentityMap
     * @return
     */
    private Map<String, Collection<Object>> addPageObjectIdentities(PiItem piItem,
                                                                    Map<String, Collection<Object>> objectIdentityMap) {
        String[] piParts = StringUtils.split(piItem.getUuid(), Separator.UNDERLINE.getValue());
        // 获取页面对应的产品集成受保护的信息
        if (piParts.length == 3) {
            String piUuid = piParts[1];
            String pageUuid = piParts[2];
            List<Object> pageUuids = Lists.newArrayList();
            pageUuids.add(pageUuid);
            // 添加页面功能
            if (objectIdentityMap.containsKey(AppFunctionType.AppPageDefinition)) {
                objectIdentityMap.get(AppFunctionType.AppPageDefinition).addAll(pageUuids);
            } else {
                objectIdentityMap.put(AppFunctionType.AppPageDefinition, pageUuids);
            }
            PiItem belongToPiItem = AppCacheUtils.getPiItem(piUuid);
            if (belongToPiItem != null) {
                // 添加公共受保护的集成信息
                addCommonObjectIdentities(belongToPiItem, objectIdentityMap);
                // 添加功能受保护的集成信息
                addFunctionObjectIdentities(belongToPiItem, objectIdentityMap);
            }
        }
        return objectIdentityMap;
    }

    /**
     * 页面引用资源受保护的集成信息
     *
     * @param piItem
     * @param objectIdentityMap
     * @return
     */
    private Map<String, Collection<Object>> addFunctionOfPageObjectIdentities(PiItem piItem,
                                                                              Map<String, Collection<Object>> objectIdentityMap) {
        String[] piParts = StringUtils.split(piItem.getUuid(), Separator.UNDERLINE.getValue());
        // 获取页面引用资源对应的产品集成受保护的信息
        if (piParts.length == 5) {
            String functionUuid = piParts[4];
            addObjectIdentitiesByFunctionUuid(functionUuid, objectIdentityMap);
        }
        return objectIdentityMap;
    }

    /**
     * 添加功能受保护的集成信息
     *
     * @param piItem
     * @param objectIdentityMap
     * @return
     */
    private Map<String, Collection<Object>> addFunctionObjectIdentities(PiItem piItem,
                                                                        Map<String, Collection<Object>> objectIdentityMap) {
        String type = piItem.getType();
        // 添加功能受保护的对像
        Integer dataType = Integer.valueOf(type);
        if (AppType.FUNCTION.equals(dataType)) {
            String functionUuid = piItem.getDataUuid();
            addObjectIdentitiesByFunctionUuid(functionUuid, objectIdentityMap);
        }
        return objectIdentityMap;
    }

    /**
     * 添加功能受保护的对像
     *
     * @param functionUuid
     * @param objectIdentityMap
     */
    private void addObjectIdentitiesByFunctionUuid(String functionUuid,
                                                   Map<String, Collection<Object>> objectIdentityMap) {
        Map<String, Collection<Object>> functionMap = appFunctionSourceManager.getObjectIdentities(functionUuid);
        for (Entry<String, Collection<Object>> entry : functionMap.entrySet()) {
            String key = entry.getKey();
            if (objectIdentityMap.containsKey(key)) {
                objectIdentityMap.get(key).addAll(entry.getValue());
            } else {
                objectIdentityMap.put(key, entry.getValue());
            }
        }
    }

    /**
     * 页面及页面引用的集成信息
     *
     * @param piItem
     * @return
     */
    private boolean isPagePiItem(PiItem piItem) {
        String uuid = piItem.getUuid();
        if (StringUtils.startsWith(uuid, AppConstants.PAGE_PREFIX)
                || StringUtils.startsWith(uuid, AppConstants.PAGEREF_PREFIX)) {
            return true;
        }
        return false;
    }

    /**
     * 页面引用资源的集成信息
     *
     * @param piItem
     * @return
     */
    private boolean isFunctionOfPagePiItem(PiItem piItem) {
        String uuid = piItem.getUuid();
        if (StringUtils.startsWith(uuid, AppConstants.FUNCTIONREF_OF_PAGE_PREFIX)) {
            return true;
        }
        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.support.AbstractResourceDataSource#getAnonymousResources()
     */
    @Override
    public List<String> getAnonymousResources() {
        return appFunctionSourceManager.getAnonymousResources();
    }

}
