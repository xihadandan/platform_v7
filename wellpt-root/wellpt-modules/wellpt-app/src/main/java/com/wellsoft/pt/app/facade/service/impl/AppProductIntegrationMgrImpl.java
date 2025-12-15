/*
 * @(#)2016-07-24 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.facade.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.ConfigurableIdEntity;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.pt.app.bean.AppProductIntegrationBean;
import com.wellsoft.pt.app.entity.*;
import com.wellsoft.pt.app.facade.service.AppPageDefinitionMgr;
import com.wellsoft.pt.app.facade.service.AppProductIntegrationMgr;
import com.wellsoft.pt.app.security.AppProductIntegrationResourceDataSource;
import com.wellsoft.pt.app.service.*;
import com.wellsoft.pt.app.support.AppCacheUtils;
import com.wellsoft.pt.app.support.AppConstants;
import com.wellsoft.pt.app.support.AppType;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
 * 2016-07-24.1	zhulh		2016-07-24		Create
 * </pre>
 * @date 2016-07-24
 */
@Service
public class AppProductIntegrationMgrImpl implements AppProductIntegrationMgr {

    private static final String REMOVE_BY_PI_PARENT_UUID = "delete from AppProductIntegration t where t.parentUuid = :parentUuid";
    private static final String REMOVE_BY_PRODUCT_UUID = "delete from AppProductIntegration t where t.appProductUuid = :appProductUuid";
    private static final String GET_ALL_PI_APP_WIDGET_DEFINITION = "from AppWidgetDefinition t where exists(select pi from AppProductIntegration pi where pi.appPageUuid = t.appPageUuid)";
    @Autowired
    private AppProductIntegrationService appProductIntegrationService;
    @Autowired
    private AppFunctionService appFunctionService;
    @Autowired
    private AppModuleService appModuleService;
    @Autowired
    private AppApplicationService appApplicationService;
    @Autowired
    private AppPageDefinitionMgr appPageDefinitionMgr;
    @Autowired
    private AppProductService appProductService;
    @Autowired
    private AppSystemService appSystemService;
    @Autowired
    private AppWidgetDefinitionService appWidgetDefinitionService;
    @Autowired
    private AppProductIntegrationResourceDataSource appProductIntegrationResourceDataSource;

    @Override
    public AppProductIntegrationBean getBean(String uuid) {
        AppProductIntegration entity = appProductIntegrationService.get(uuid);
        AppProductIntegrationBean bean = new AppProductIntegrationBean();
        BeanUtils.copyProperties(entity, bean);
        return bean;
    }

    @Override
    public void saveBean(AppProductIntegrationBean bean) {
        String uuid = bean.getUuid();
        AppProductIntegration entity = new AppProductIntegration();
        if (StringUtils.isNotBlank(uuid)) {
            entity = appProductIntegrationService.get(uuid);
        }
        BeanUtils.copyProperties(bean, entity);
        appProductIntegrationService.save(entity);
    }

    @Override
    public void remove(String uuid) {
        appProductIntegrationService.remove(uuid);
    }

    @Override
    public void removeAll(Collection<String> uuids) {
        appProductIntegrationService.removeAllByPk(uuids);
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional
    public void savePiTreeNode(TreeNode piTreeNode) {
        if (piTreeNode == null) {
            return;
        }
        String appProductUuid = piTreeNode.getId();
        List<TreeNode> children = piTreeNode.getChildren();
        List<AppProductIntegration> pis = new ArrayList<AppProductIntegration>();
        Map<String, TreeNode> systemUuidMap = new HashMap<String, TreeNode>();
        for (int index = 0; index < children.size(); index++) {
            TreeNode treeNode = children.get(index);
            Map<String, Object> dataMap = (Map<String, Object>) treeNode.getData();
            String systemUuid = ObjectUtils.toString(dataMap.get(IdEntity.UUID));
            systemUuidMap.put(systemUuid, treeNode);
            extract2Pi(appProductUuid, systemUuid, Separator.SLASH.getValue(), index, treeNode, null, pis);
        }
        appProductIntegrationService.removeByProductUuid(appProductUuid);
        appProductIntegrationService.saveAll(pis);

        // 验证数据的有效性
        validatePiDatas(systemUuidMap);

        AppCacheUtils.clear();
    }

    private void validatePiDatas(Map<String, TreeNode> systemUuidMap) {
        // 确保集成信息的系统唯一
        for (Entry<String, TreeNode> entry : systemUuidMap.entrySet()) {
            if (appProductIntegrationService.count(entry.getKey(), AppType.SYSTEM) > 1) {
                throw new RuntimeException("系统[" + entry.getValue().getName() + "]已集成进产品，不能再被使用!");
            }
        }
        // 确保集成信息的模块在同一系统下唯一
        for (Entry<String, TreeNode> entry : systemUuidMap.entrySet()) {
            String appSystemUuid = entry.getKey();
            Map<String, Object> values = new HashMap<String, Object>();
            values.put("appSystemUuid", appSystemUuid);
            List<AppProductIntegration> existProductIntegrations = appProductIntegrationService.listByNameSQLQuery(
                    "piCountModuleWidthId", values);
            if (CollectionUtils.isEmpty(existProductIntegrations)) {
                continue;
            }
            String moduleId = existProductIntegrations.get(0).getDataId();
            AppProductIntegration example = new AppProductIntegration();
            example.setAppSystemUuid(appSystemUuid);
            example.setDataId(moduleId);
            example.setDataType(AppType.MODULE.toString());
            List<AppProductIntegration> appProductIntegrations = appProductIntegrationService.findByExample(example);
            throw new RuntimeException("模块[" + appProductIntegrations.get(0).getDataName() + "]已集成进系统，不能再被使用!");
        }
        // 确保集成信息的路径唯一
        for (Entry<String, TreeNode> entry : systemUuidMap.entrySet()) {
            String appSystemUuid = entry.getKey();
            Map<String, Object> values = new HashMap<String, Object>();
            values.put("appSystemUuid", appSystemUuid);
            List<AppProductIntegration> existProductIntegrations = appProductIntegrationService.listByNameSQLQuery(
                    "piCountByPath", values);
            if (CollectionUtils.isEmpty(existProductIntegrations)) {
                continue;
            }
            String moduleId = existProductIntegrations.get(0).getDataId();
            AppProductIntegration example = new AppProductIntegration();
            example.setAppSystemUuid(appSystemUuid);
            example.setDataId(moduleId);
            List<AppProductIntegration> appProductIntegrations = appProductIntegrationService.findByExample(example);
            List<String> paths = new ArrayList<String>();
            for (AppProductIntegration appProductIntegration : appProductIntegrations) {
                paths.add(appProductIntegration.getDataPath());
            }
            throw new RuntimeException("与集成信息[" + StringUtils.join(paths, Separator.SEMICOLON.getValue())
                    + "]ID相同的数据已集成进系统，不能再被使用!");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public void savePiChildNodes(TreeNode piTreeNode) {
        if (piTreeNode == null) {
            return;
        }
        // 更新父节点页面引用信息
        AppProductIntegration appProductIntegration = this.appProductIntegrationService.get(piTreeNode.getId());

        Map<String, Object> dataMap = (Map<String, Object>) piTreeNode.getData();
        if (dataMap != null) {
            appProductIntegration.setAppPageUuid(ObjectUtils.toString(dataMap.get("pageUuid")));
            appProductIntegration
                    .setAppPageReference(Boolean.valueOf(ObjectUtils.toString(dataMap.get("pageReference"))));
            appProductIntegrationService.save(appProductIntegration);
            AppCacheUtils.clear();
        }

        removeByParentTreeNode(piTreeNode);

        // 更新子结点信息
        List<TreeNode> children = piTreeNode.getChildren();
        if (children == null || children.isEmpty()) {
            return;
        }

        String appProductUuid = appProductIntegration.getAppProductUuid();
        String dataPath = appProductIntegration.getDataPath();
        String parentUuid = appProductIntegration.getUuid();
        List<AppProductIntegration> pis = new ArrayList<AppProductIntegration>();
        Map<String, TreeNode> systemUuidMap = new HashMap<String, TreeNode>();
        String systemUuid = appProductIntegration.getAppSystemUuid();
        for (int index = 0; index < children.size(); index++) {
            TreeNode treeNode = children.get(index);
            systemUuidMap.put(systemUuid, treeNode);
            extract2Pi(appProductUuid, systemUuid, dataPath + Separator.SLASH.getValue(), index, treeNode, parentUuid,
                    pis);
        }
        appProductIntegrationService.saveAll(pis);

        // 验证数据的有效性
        validatePiDatas(systemUuidMap);

        AppCacheUtils.clear();
    }

    /**
     * @param piTreeNode
     */
    @Transactional
    public void removeByParentTreeNode(TreeNode piTreeNode) {
        List<TreeNode> childrenNodes = piTreeNode.getChildren();
        if (childrenNodes != null && !childrenNodes.isEmpty()) {
            for (TreeNode treeNode : childrenNodes) {
                removeByParentTreeNode(treeNode);
            }
        }

        String piUuid = piTreeNode.getId();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("parentUuid", piUuid);
        appProductIntegrationService.deleteByHQL(REMOVE_BY_PI_PARENT_UUID, values);
        appProductIntegrationService.flushSession();
    }

    @Override
    public void removeByAppProductUuid(String appProductUuid) {
        // 删除页面定义
        // appPageDefinitionMgr.removeAppProductIntegrationPageByAppProductUuid(appProductUuid);

        // 删除集成信息
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("appProductUuid", appProductUuid);
        appProductIntegrationService.deleteByHQL(REMOVE_BY_PRODUCT_UUID, values);
        AppCacheUtils.clear();
    }

    /**
     * @param appProductUuid
     * @param appSystemUuid
     * @param sortOrder
     * @param treeNode
     * @param pis
     */
    @SuppressWarnings("unchecked")
    private void extract2Pi(String appProductUuid, String appSystemUuid, String dataPath, int sortOrder,
                            TreeNode treeNode, String parentUuid, List<AppProductIntegration> pis) {
        // 扩展结点，不进行保存
        if (isExtNode(treeNode)) {
            return;
        }
        Map<String, Object> dataMap = (Map<String, Object>) treeNode.getData();
        AppProductIntegration productIntegration = new AppProductIntegration();
        productIntegration.setUuid(treeNode.getId());
        productIntegration.setAppProductUuid(appProductUuid);
        productIntegration.setAppSystemUuid(appSystemUuid);
        productIntegration.setDataUuid(ObjectUtils.toString(dataMap.get("uuid")));
        productIntegration.setDataName(ObjectUtils.toString(dataMap.get("name")));
        productIntegration.setDataId(ObjectUtils.toString(dataMap.get("id")));
        productIntegration.setDataType(ObjectUtils.toString(dataMap.get("type")));
        productIntegration.setDataPath(dataPath + productIntegration.getDataId());
        productIntegration.setAppPageUuid(ObjectUtils.toString(dataMap.get("pageUuid")));
        productIntegration.setAppPageReference(Boolean.valueOf(ObjectUtils.toString(dataMap.get("pageReference"))));
        productIntegration.setSortOrder(sortOrder);
        productIntegration.setParentUuid(parentUuid);
        pis.add(productIntegration);

        List<TreeNode> children = treeNode.getChildren();
        for (int index = 0; index < children.size(); index++) {
            TreeNode child = children.get(index);
            String piPath = dataPath + productIntegration.getDataId() + Separator.SLASH.getValue();
            extract2Pi(appProductUuid, appSystemUuid, piPath, index, child, treeNode.getId(), pis);
        }
    }

    /**
     * 判断是否页面扩展结点
     *
     * @param treeNode
     * @return
     */
    private boolean isExtNode(TreeNode treeNode) {
        String id = treeNode.getId();
        return StringUtils.startsWithAny(id, new String[]{AppConstants.PAGE_PREFIX, AppConstants.PAGEREF_PREFIX,
                AppConstants.FUNCTIONREF_OF_PAGE_PREFIX});
    }

    @Override
    @Transactional(readOnly = true)
    public TreeNode getTree(String productUuid) {
        String[] dataTypes = {"1", "2", "3", "4"};
        return this.getTreeByProductUuidAndDataType(productUuid, dataTypes);
    }

    @Override
    @Transactional(readOnly = true)
    public TreeNode getProtectedTree(String productUuid) {
        String[] dataTypes = {"1", "2", "3", "4"};
        return this.getTreeByProdUuidAndDataTypeAndProtected(productUuid, dataTypes, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TreeNode> getTreeWithPtProduct(String productUuid) {
        return appProductIntegrationResourceDataSource.getData(null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.facade.service.AppProductIntegrationMgr#getTreeWithPtProductAndFunctionType(java.lang.String, java.lang.String[])
     */
    @Override
    public List<TreeNode> getTreeWithPtProductAndFunctionType(String productUuid, String[] functionTypes) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        List<AppProduct> appProducts = appProductService.getAll();
        // 只能获取归属自己单位的产品和平台产品
        String userUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        for (AppProduct appProduct : appProducts) {
            String proUnitId = appProduct.getSystemUnitId();
            if (proUnitId.equals(userUnitId) || proUnitId.equals(MultiOrgSystemUnit.PT_ID)) {
                String appProductUuid = appProduct.getUuid();
                String[] dataTypes = {"1", "2", "3"};
                TreeNode treeNode = this.getTreeByProductUuidAndDataTypeAndFunctionType(appProductUuid, dataTypes,
                        functionTypes);
                treeNodes.add(treeNode);
            }
        }
        return treeNodes;
    }

    @Override
    @Transactional(readOnly = true)
    public TreeNode getTreeByDataType(String[] dataTypes) {
        List<AppProduct> appProducts = appProductService.listAll();
        TreeNode treeNode = new TreeNode();
        treeNode.setName("产品");
        treeNode.setNocheck(true);
        for (AppProduct appProduct : appProducts) {
            treeNode.getChildren().add(this.getTreeByProductUuidAndDataType(appProduct.getUuid(), dataTypes));
        }
        return treeNode;
    }

    @Override
    public List<TreeNode> getTreeByDataType2(String[] dataTypes, String topName) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        List<AppProduct> appProducts = appProductService.listAll();
        TreeNode treeNode = new TreeNode();
        treeNode.setName(StringUtils.isBlank(topName) ? "产品" : topName);
        treeNode.setId("Global");
        // treeNode.setNocheck(true);
        treeNodes.add(treeNode);
        for (AppProduct appProduct : appProducts) {
            treeNodes.add(getTreeByProductUuidAndDataType(appProduct.getUuid(), dataTypes));
            // treeNode.getChildren().add(this.getTreeByProductUuidAndDataType(appProduct.getUuid(), dataTypes));
        }
        return treeNodes;
    }


    private TreeNode getTreeByProdUuidAndDataTypeAndProtected(String productUuid, String[] dataTypes, boolean onlyProctected) {
        List<AppProductIntegration> productIntegrations = Lists.newArrayList();
        TreeNode treeNode = new TreeNode();
        if (productUuid != null) {
            AppProduct appProduct = appProductService.get(productUuid);
            treeNode.setName(appProduct.getName());
            treeNode.setId(appProduct.getUuid());
            treeNode.setData(convert2TreeData(AppType.PRODUCT, appProduct));
            treeNode.setNocheck(true);
        }
        Map<String, Object> params = Maps.newHashMap();
        params.put("appProductUuid", productUuid);
        params.put("dataTypes", dataTypes);
        String hql = "FROM AppProductIntegration a WHERE " + (productUuid == null ? "" : " a.appProductUuid=:appProductUuid AND") + " a.dataType in (:dataTypes)" +
                (onlyProctected ? " and isProtected = true " : "") +
                " ORDER BY a.sortOrder ASC,a.createTime ASC";
        List<AppProductIntegration> appProductIntegrations = appProductIntegrationService.listByHQL(hql, params);
        productIntegrations.addAll(appProductIntegrations);

        // 获取页面引用功能的产品集成
        if (onlyProctected) {
            params.put("isProtected", true);
        }
        if (ArrayUtils.indexOf(dataTypes, "4") != -1) {
            // 获取页面的产品集成
            List<AppProductIntegration> appPageDefinitionPis = appProductIntegrationService.listByNameSQLQuery(
                    "getAppPageDefinitionAsAppProductIntegration", params);
            productIntegrations.addAll(appPageDefinitionPis);

            List<AppProductIntegration> appFunctionOfAppPageDefinitionPis = appProductIntegrationService
                    .listByNameSQLQuery("getAppFunctionOfAppPageDefinitionAsAppProductIntegration", params);
            productIntegrations.addAll(appFunctionOfAppPageDefinitionPis);
        }
        return buildTreeNode(treeNode, productIntegrations);
    }

    @Override
    @Transactional(readOnly = true)
    public TreeNode getTreeByProductUuidAndDataType(String productUuid, String[] dataTypes) {
        return this.getTreeByProdUuidAndDataTypeAndProtected(productUuid, dataTypes, false);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.facade.service.AppProductIntegrationMgr#getTreeByProductUuidAndDataTypeAndFunctionType(java.lang.String, java.lang.String[], java.lang.String[])
     */
    public TreeNode getTreeByProductUuidAndDataTypeAndFunctionType(String productUuid, String[] dataTypes,
                                                                   String[] functionTypes) {
        AppProduct appProduct = appProductService.get(productUuid);
        TreeNode treeNode = new TreeNode();
        treeNode.setName(appProduct.getName());
        treeNode.setId(appProduct.getUuid());
        treeNode.setData(convert2TreeData(AppType.PRODUCT, appProduct));
        treeNode.setNocheck(true);
        Map<String, Object> params = Maps.newHashMap();
        params.put("appProductUuid", productUuid);
        params.put("dataTypes", dataTypes);
        params.put("functionTypes", functionTypes);
        String hql = "FROM AppProductIntegration a WHERE a.appProductUuid=:appProductUuid AND a.dataType in (:dataTypes) ORDER BY a.sortOrder ASC,a.createTime ASC";
        List<AppProductIntegration> appProductIntegrations = appProductIntegrationService.listByHQL(hql, params);
        // 获取功能产品信息
        String functionHql = "from AppProductIntegration t1 where t1.appProductUuid = :appProductUuid and exists (select t2.uuid from AppFunction t2 where t1.dataUuid = t2.uuid and t2.type in(:functionTypes))";
        List<AppProductIntegration> appProductIntegrationFunctions = appProductIntegrationService.listByHQL(functionHql, params);
        // 获取页面的产品集成
        List<AppProductIntegration> appPageDefinitionPis = appProductIntegrationService.listByNameSQLQuery(
                "getAppPageDefinitionAsAppProductIntegration", params);
        // 获取页面引用功能的产品集成
        List<AppProductIntegration> appFunctionOfAppPageDefinitionPis = appProductIntegrationService
                .listByNameSQLQuery("getAppFunctionOfAppPageDefinitionAsAppProductIntegration", params);
        List<AppProductIntegration> productIntegrations = Lists.newArrayList();
        productIntegrations.addAll(appPageDefinitionPis);
        productIntegrations.addAll(appProductIntegrations);
        productIntegrations.addAll(appProductIntegrationFunctions);
        productIntegrations.addAll(appFunctionOfAppPageDefinitionPis);
        return buildTreeNode(treeNode, productIntegrations);
    }

    private TreeNode buildTreeNode(TreeNode rootTreeNode, List<AppProductIntegration> appProductIntegrations) {
        Map<String, List<AppProductIntegration>> piMap = new LinkedHashMap<String, List<AppProductIntegration>>();
        List<AppProductIntegration> topPis = new ArrayList<AppProductIntegration>();
        for (AppProductIntegration pi : appProductIntegrations) {
            String parentUuid = pi.getParentUuid();
            if (StringUtils.isNotBlank(parentUuid)) {
                if (!piMap.containsKey(parentUuid)) {
                    piMap.put(parentUuid, new ArrayList<AppProductIntegration>());
                }
                piMap.get(parentUuid).add(pi);
            } else {
                topPis.add(pi);
            }
        }

        for (AppProductIntegration topPi : topPis) {
            TreeNode node = convert2TreeNode(topPi);
            rootTreeNode.getChildren().add(node);

            // 生成子结点
            buildChildNodes(node, piMap);
        }

        return rootTreeNode;
    }

    @Override
    @Transactional(readOnly = true)
    public TreeNode getTreeByUuid(String uuid, String[] dataTypes) {
        return getTreeNodeByUuid(uuid, dataTypes, ArrayUtils.EMPTY_STRING_ARRAY);
    }

    @Override
    @Transactional(readOnly = true)
    public TreeNode getTreeNodeByUuid(String uuid, String[] dataTypes, String[] functionTypes) {
        List<AppProductIntegration> appProductIntegrations = appProductIntegrationService
                .queryAppProductIntegrationTree(uuid, dataTypes, functionTypes);
        Map<String, AppProductIntegration> appPdtIgtMap = ConvertUtils.convertElementToMap(appProductIntegrations,
                "uuid");
        TreeNode treeNode = convert2TreeNode(appPdtIgtMap.get(uuid));

        // 页面结点所在的上级结点信息
        List<String> parentUuids = Lists.newArrayList();
        for (AppProductIntegration appProductIntegration : appProductIntegrations) {
            parentUuids.add(appProductIntegration.getUuid());
        }
        if (CollectionUtils.isEmpty(parentUuids)) {
            parentUuids.add(uuid);
        }

        // 获取页面及页面引用的产品集成
        List<AppProductIntegration> appPageDefinitionPis = Lists.newArrayList();
        List<AppProductIntegration> appFunctionOfAppPageDefinitionPis = Lists.newArrayList();
        int num = 0;
        Set<String> partitionParentUuids = Sets.newHashSet();
        for (String parentUuid : parentUuids) {
            num++;
            partitionParentUuids.add(parentUuid);
            if (num % 1000 == 0 || num == parentUuids.size()) {
                Map<String, Object> params = Maps.newHashMap();
                params.put("parentUuids", partitionParentUuids);
                // 获取页面的产品集成
                appPageDefinitionPis.addAll(appProductIntegrationService.listByNameSQLQuery(
                        "getAppPageDefinitionAsAppProductIntegration", params));
                // 获取页面引用功能的产品集成
                appFunctionOfAppPageDefinitionPis.addAll(appProductIntegrationService.listByNameSQLQuery(
                        "getAppFunctionOfAppPageDefinitionAsAppProductIntegration", params));
                partitionParentUuids.clear();
            }
        }

        List<AppProductIntegration> productIntegrations = Lists.newArrayList();
        productIntegrations.addAll(appPageDefinitionPis);
        productIntegrations.addAll(appProductIntegrations);
        productIntegrations.addAll(appFunctionOfAppPageDefinitionPis);
        buildChildNodes(treeNode, productIntegrations);
        return treeNode;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.facade.service.AppProductIntegrationMgr#getTreeByAppSystemUuid(java.lang.String)
     */
    @Override
    public List<TreeNode> getTreeByAppSystemUuid(String appSystemUuid) {
        TreeNode treeNode = new TreeNode();
        Map<String, Object> params = Maps.newHashMap();
        params.put("appSystemUuid", appSystemUuid);
        params.put("isProtected", 1);
        String hql = "FROM AppProductIntegration a WHERE a.appSystemUuid=:appSystemUuid and a.isProtected = true ORDER BY a.sortOrder ASC,a.createTime ASC";
        List<AppProductIntegration> appProductIntegrations = appProductIntegrationService.listByHQL(hql, params);
        // 获取页面的产品集成
        List<AppProductIntegration> appPageDefinitionPis = appProductIntegrationService.listByNameSQLQuery(
                "getAppPageDefinitionAsAppProductIntegration", params);
        // 获取页面引用功能的产品集成
        List<AppProductIntegration> appFunctionOfAppPageDefinitionPis = appProductIntegrationService
                .listByNameSQLQuery("getAppFunctionOfAppPageDefinitionAsAppProductIntegration", params);
        List<AppProductIntegration> productIntegrations = Lists.newArrayList();
        productIntegrations.addAll(appPageDefinitionPis);
        productIntegrations.addAll(appProductIntegrations);
        productIntegrations.addAll(appFunctionOfAppPageDefinitionPis);
        buildTreeNode(treeNode, productIntegrations);
        return treeNode.getChildren();
    }

    private void buildChildNodes(TreeNode treeNode, List<AppProductIntegration> appProductIntegrations) {
        List<TreeNode> childrenNodes = treeNode.getChildren();
        for (AppProductIntegration appProductIntegration : appProductIntegrations) {
            if (treeNode.getId().equals(appProductIntegration.getParentUuid())) {
                childrenNodes.add(convert2TreeNode(appProductIntegration));
            }
        }
        if (childrenNodes != null && childrenNodes.size() > 0) {
            for (TreeNode childNode : childrenNodes) {
                this.buildChildNodes(childNode, appProductIntegrations);
            }
        }
    }

    /**
     * @param treeNode
     * @param piMap
     * @return
     */
    private void buildChildNodes(TreeNode treeNode, Map<String, List<AppProductIntegration>> piMap) {
        String key = treeNode.getId();
        List<AppProductIntegration> piList = piMap.get(key);
        if (piList == null) {
            return;
        }
        for (AppProductIntegration pi : piList) {
            TreeNode child = convert2TreeNode(pi);
            treeNode.getChildren().add(child);
            buildChildNodes(child, piMap);
        }
    }

    /**
     * @param piTreeNode
     * @return
     */
    @Override
    public List<TreeNode> getPiChildNodes(TreeNode piTreeNode) {
        String[] dataTypes = {"1", "2", "3", "4"};
        TreeNode treeNode = getTreeByUuid(piTreeNode.getId(), dataTypes);
        return treeNode.getChildren();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.facade.service.AppProductIntegrationMgr#getPiTreeNodes(java.lang.String, java.lang.Integer, java.lang.String)
     */
    @Override
    public List<TreeNode> getPiTreeNodes(String parentPiPath, Integer piType, String piValue) {
        if (StringUtils.isBlank(piValue)) {
            return Collections.emptyList();
        }
        String[] ids = StringUtils.split(piValue, Separator.SEMICOLON.getValue());
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        List<ConfigurableIdEntity> entities = new ArrayList<ConfigurableIdEntity>();
        switch (piType) {
            case 1:
                List<AppSystem> appSystems = appSystemService.getByIds(ids);
                entities.addAll(appSystems);
                treeNodes.addAll(convert2TreeNode(parentPiPath, piType, entities));
                break;
            case 2:

                List<AppModule> appModules = appModuleService.getByIds(ids);
                entities.addAll(appModules);
                treeNodes.addAll(convert2TreeNode(parentPiPath, piType, entities));
                break;
            case 3:
                List<AppApplication> appApplications = appApplicationService.getByIds(ids);
                entities.addAll(appApplications);
                treeNodes.addAll(convert2TreeNode(parentPiPath, piType, entities));
                break;
            case 4:
                // 功能唯一标识为UUID
                List<AppFunction> appFunctions = appFunctionService.getAll(ids);
                entities.addAll(appFunctions);
                treeNodes.addAll(convert2TreeNode(parentPiPath, piType, entities));
                break;
            default:
                break;
        }
        return treeNodes;
    }

    /**
     * @param piType
     * @param configurableIdEntities
     * @return
     */
    private Collection<? extends TreeNode> convert2TreeNode(String parentPiPath, Integer piType,
                                                            List<ConfigurableIdEntity> configurableIdEntities) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        for (ConfigurableIdEntity configurableIdEntity : configurableIdEntities) {
            String path = parentPiPath + Separator.SLASH.getValue() + configurableIdEntity.getId();
            Map<String, Object> data = convert2TreeData(piType, configurableIdEntity);
            data.put("path", path);
            String uuid = DigestUtils.md5Hex(path + piType + configurableIdEntity.getUuid());

            TreeNode treeNode = new TreeNode();
            treeNode.setName(getDisplayName(piType, configurableIdEntity.getName()));
            treeNode.setId(uuid);
            treeNode.setData(data);
            treeNode.setNocheck(true);
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    /**
     * @param piType
     * @param configurableIdEntity
     * @return
     */
    private Map<String, Object> convert2TreeData(Integer piType, ConfigurableIdEntity configurableIdEntity) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("uuid", configurableIdEntity.getUuid());
        values.put("name", configurableIdEntity.getName());
        values.put("id", configurableIdEntity.getId());
        values.put("type", piType);
        return values;
    }

    /**
     * @param pi
     * @return
     */
    private TreeNode convert2TreeNode(AppProductIntegration pi) {
        TreeNode treeNode = new TreeNode();
        String piUuid = pi.getUuid();
        treeNode.setId(piUuid);
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("uuid", pi.getDataUuid());
        values.put("recVer", pi.getRecVer());
        values.put("name", pi.getDataName());
        values.put("id", pi.getDataId());
        values.put("type", Integer.valueOf(pi.getDataType()));
        values.put("pageUuid", pi.getAppPageUuid());
        values.put("pageReference", pi.getAppPageReference() == null ? false : pi.getAppPageReference());
        values.put("path", pi.getDataPath());
        if (StringUtils.startsWith(piUuid, AppConstants.PAGE_PREFIX)
                || StringUtils.startsWith(piUuid, AppConstants.PAGEREF_PREFIX)) {
            treeNode.setName("页面:" + pi.getDataName());
        } else {
            treeNode.setName(getDisplayName(Integer.valueOf(pi.getDataType()), pi.getDataName()));
            // zyguo 为了减少查询量，只在系统节点上打上归属单位ID的值，其他类型的节点全部为空
            String systemUnitId = null;
            if (AppType.SYSTEM.toString().equals(pi.getDataType())) { // 系统节点
                AppSystem appSystem = this.appSystemService.get(pi.getDataUuid());
                systemUnitId = appSystem.getSystemUnitId();
            }
            values.put("systemUnitId", systemUnitId);
        }
        treeNode.setPath(pi.getDataPath());
        treeNode.setData(values);
        return treeNode;
    }

    /**
     * @param dataType
     * @param dataName
     * @return
     */
    private String getDisplayName(Integer appType, String dataName) {
        return AppType.getName(appType) + ": " + dataName;
    }

    @Override
    public void savePiPageDefinition(String piUuid, String pageUuid) {
        AppProductIntegration appProductIntegration = appProductIntegrationService.get(piUuid);
        if (appProductIntegration == null) {
            throw new RuntimeException("集成信息不存在，请先保存页面的集成信息!");
        }

        Integer dataType = Integer.valueOf(appProductIntegration.getDataType());
        if (!(AppType.SYSTEM.equals(dataType) || AppType.MODULE.equals(dataType) || AppType.APPLICATION
                .equals(dataType))) {
            throw new RuntimeException("集成信息的类型不能配置页面定义!");
        }
        appProductIntegration.setAppPageUuid(pageUuid);
        appProductIntegration.setAppPageReference(false);
        appProductIntegrationService.save(appProductIntegration);

        AppCacheUtils.clear();
    }

    @Override
    public List<AppWidgetDefinition> getAllAppWidgetDefinition() {
        return appWidgetDefinitionService.listByHQL(GET_ALL_PI_APP_WIDGET_DEFINITION, null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.facade.service.AppProductIntegrationMgr#countByAppProductUuid(java.lang.String)
     */
    @Override
    public long countByAppProductUuid(String appProductUuid) {
        return appProductIntegrationService.countByAppProductUuid(appProductUuid);
    }

    @Override
    public List<TreeNode> getSysModuleAppTreeByAppSystemUuid(String appSystemUuid) {
        TreeNode treeNode = new TreeNode();
        Map<String, Object> params = Maps.newHashMap();
        params.put("appSystemUuid", appSystemUuid);
        params.put("isProtected", 1);
        String hql = "FROM AppProductIntegration a WHERE a.appSystemUuid=:appSystemUuid and a.isProtected = true and a.dataType in ('1','2','3') ORDER BY a.sortOrder ASC,a.createTime ASC";
        List<AppProductIntegration> appProductIntegrations = appProductIntegrationService.listByHQL(hql, params);
        List<AppProductIntegration> productIntegrations = Lists.newArrayList();
        productIntegrations.addAll(appProductIntegrations);
        buildTreeNode(treeNode, productIntegrations);
        return treeNode.getChildren();
    }

}
