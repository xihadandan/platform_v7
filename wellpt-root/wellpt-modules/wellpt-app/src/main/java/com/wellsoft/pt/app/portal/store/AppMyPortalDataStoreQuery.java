/*
 * @(#)2019年7月10日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.portal.store;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.entity.AppPageDefinition;
import com.wellsoft.pt.app.portal.facade.service.AppPortalFacadeService;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
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
 * 2019年7月10日.1	zhulh		2019年7月10日		Create
 * </pre>
 * @date 2019年7月10日
 */
@Component
public class AppMyPortalDataStoreQuery extends AbstractDataStoreQueryInterface {

    @Autowired
    private AppPortalFacadeService appPortalFacadeService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "平台管理_产品集成_门户_我的门户";
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#initCriteriaMetadata(com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext queryContext) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("uuid", "t.uuid", "UUID", String.class);
        criteriaMetadata.add("createTime", "t.create_time", "创建时间", String.class);
        criteriaMetadata.add("creator", "t.creator", "创建人", String.class);
        criteriaMetadata.add("modifier", "t.modifier", "修改人", String.class);
        criteriaMetadata.add("modifyTime", "t.modify_time", "修改时间", Date.class);
        criteriaMetadata.add("recVer", "t.rec_ver", "recVer", Integer.class);
        criteriaMetadata.add("name", "t.name", "名称", String.class);
        criteriaMetadata.add("id", "t.id", "ID", String.class);
        criteriaMetadata.add("version", "t.version", "版本号", String.class);
        criteriaMetadata.add("code", "t.code", "编号", String.class);
        criteriaMetadata.add("wtype", "t.wtype", "页面容器类型", String.class);
        criteriaMetadata.add("title", "t.title", "标题", String.class);
        criteriaMetadata.add("shared", "t.shared", "是否共享的页面", Boolean.class);
        criteriaMetadata.add("isDefault", "t.is_default", "是否默认的页面", Boolean.class);
        criteriaMetadata.add("remark", "t.remark", "备注", String.class);
        criteriaMetadata.add("appPiUuid", "t.app_pi_uuid", "产品集成信息UUID", String.class);
        return criteriaMetadata;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface#query(com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        String appPiUuid = ObjectUtils.toString(queryContext.getQueryParams().get("appPiUuid"), StringUtils.EMPTY);
        List<AppPageDefinition> appPageDefinitions = appPortalFacadeService.getUserAppPageDefinitionByPiUuid(appPiUuid);
        appPageDefinitions = filterData(queryContext.getKeyword(), appPageDefinitions);
        PagingInfo pagingInfo = queryContext.getPagingInfo();
        pagingInfo.setAutoCount(false);
        pagingInfo.setTotalCount(appPageDefinitions.size());
        int first = pagingInfo.getFirst();
        int pageSize = pagingInfo.getPageSize();
        appPageDefinitions = cutupData(appPageDefinitions, first, pageSize);
        return convert2QueryItems(appPageDefinitions);
    }

    /**
     * 过滤数据
     *
     * @param keyword
     * @param appPageDefinitions
     * @return
     */
    private List<AppPageDefinition> filterData(String keyword, List<AppPageDefinition> appPageDefinitions) {
        List<AppPageDefinition> tmpDefinitions = Lists.newArrayList();
        if (StringUtils.isNotBlank(keyword)) {
            for (AppPageDefinition appPageDefinition : appPageDefinitions) {
                if (StringUtils.contains(appPageDefinition.getName(), keyword)) {
                    tmpDefinitions.add(appPageDefinition);
                }
            }
        } else {
            tmpDefinitions.addAll(appPageDefinitions);
        }
        return tmpDefinitions;
    }

    /**
     * 截取数据
     *
     * @param appPageDefinitions
     * @param first
     * @param pageSize
     * @return
     */
    private List<AppPageDefinition> cutupData(List<AppPageDefinition> appPageDefinitions, int first, int pageSize) {
        List<AppPageDefinition> definitions = Lists.newArrayList();
        for (int index = first; index < appPageDefinitions.size(); index++) {
            definitions.add(appPageDefinitions.get(index));
            if (pageSize == (index - first + 1)) {
                break;
            }
        }
        return definitions;
    }

    /**
     * @param appPageDefinitions
     * @return
     */
    private List<QueryItem> convert2QueryItems(List<AppPageDefinition> appPageDefinitions) {
        List<QueryItem> queryItems = Lists.newArrayList();
        for (AppPageDefinition appPageDefinition : appPageDefinitions) {
            QueryItem queryItem = new QueryItem();
            queryItem.put("uuid", appPageDefinition.getUuid());
            queryItem.put("createTime", appPageDefinition.getCreateTime());
            queryItem.put("creator", appPageDefinition.getCreator());
            queryItem.put("modifier", appPageDefinition.getModifier());
            queryItem.put("modifyTime", appPageDefinition.getModifyTime());
            queryItem.put("recVer", appPageDefinition.getRecVer());
            queryItem.put("name", appPageDefinition.getName());
            queryItem.put("id", appPageDefinition.getId());
            queryItem.put("version", appPageDefinition.getVersion());
            queryItem.put("code", appPageDefinition.getCode());
            queryItem.put("wtype", appPageDefinition.getWtype());
            queryItem.put("title", appPageDefinition.getTitle());
            queryItem.put("shared", appPageDefinition.getShared());
            queryItem.put("isDefault", appPageDefinition.getIsDefault());
            queryItem.put("remark", appPageDefinition.getRemark());
            queryItem.put("appPiUuid", appPageDefinition.getAppPiUuid());
            queryItems.add(queryItem);
        }
        return queryItems;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#count(com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    public long count(QueryContext queryContext) {
        return queryContext.getPagingInfo().getTotalCount();
    }

}
