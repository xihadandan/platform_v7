/*
 * @(#)2020年1月3日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.store;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.multi.org.entity.MultiOrgVersion;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.service.MultiOrgVersionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author wangrf
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年1月3日.1	wangrf		2020年1月3日		Create
 * </pre>
 * @date 2020年1月3日
 */
@Component
public class MultiOrgVersionDataStoreQuery extends AbstractDataStoreQueryInterface {

    @Autowired
    private MultiOrgVersionService multiOrgVersionService;
    @Autowired
    private MultiOrgService multiOrgService;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "组织管理_组织版本列表";
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#initCriteriaMetadata(com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata metadata = CriteriaMetadata.createMetadata();
        metadata.add("uuid", "uuid", "UUID", String.class);
        metadata.add("fullName", "full_name", "完全名称", String.class);
        metadata.add("id", "id", "ID", String.class);
        metadata.add("systemUnitId", "system_unit_id", "归属组织ID", String.class);
        metadata.add("initSystemUnitName", "init_system_unit_name", "版本建立时对应的系统单位名称", String.class);

        metadata.add("name", "name", "系统单位的当前名称", String.class);
        metadata.add("sourceVersionName", "source_version_name", "来源版本名称", String.class);
        metadata.add("rootVersionId", "root_version_id", "版本根id", String.class);
        metadata.add("status", "status", "状态", Integer.class);
        metadata.add("isDefault", "is_default", "是否默认", Boolean.class);
        metadata.add("createTime", "create_time", "创建时间", Date.class);

        metadata.add("level", "level", "节点的级别", Integer.class);
        metadata.add("parentId", "parent_id", "父节点id", String.class);
        metadata.add("isLeaf", "is_leaf", "是否叶节点", Boolean.class);
        metadata.add("isExpandFirst", "is_expand_first", "第一个节点展开是否展开", Boolean.class);
        return metadata;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#count(com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    public long count(QueryContext context) {
        return context.getPagingInfo().getTotalCount();
    }

    /**
     * 如何描述该方法
     * 改造于multiOrgVersionFacade.getForPageAsTree(JqGridQueryInfo jqGridQueryInfo, QueryInfo queryInfo)
     * 方法
     * 前端上传的参数nodeid（点开二级分页时）、n_level
     */
    @Override
    public List<QueryItem> query(QueryContext context) {
        // 设置查询字段条件
        String systemUnitId = (String) context.getQueryParams().get("currentUserUnitId");

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("systemUnitId", systemUnitId);
        List<MultiOrgVersion> results = context.getNativeDao().namedQuery("queryVersionOfOrgTotree", params,
                MultiOrgVersion.class, context.getPagingInfo());
        List<QueryItem> result = new ArrayList<QueryItem>();
        MultiOrgSystemUnit systemUnit = multiOrgService.getSystemUnitById(systemUnitId);

        if (!CollectionUtils.isEmpty(results)) {
            // id - version
            Map<String, List<MultiOrgVersion>> versionMap = new HashMap<String, List<MultiOrgVersion>>();
            Map<String, MultiOrgVersion> map = new HashMap<String, MultiOrgVersion>();
            for (MultiOrgVersion version : results) {
                // 获取根数据
                String key = version.getSystemUnitId() + "_" + version.getFunctionType() + "_"
                        + version.getRootVersionId();
                if (versionMap.containsKey(key)) {
                    versionMap.get(key).add(version);
                } else {
                    List<MultiOrgVersion> list = new ArrayList<MultiOrgVersion>();
                    list.add(version);
                    versionMap.put(key, list);
                }
                map.put(version.getUuid(), version);
            }
            // 遍历map集合
            MultiOrgVersion version = null;
            for (String key : versionMap.keySet()) {
                List<MultiOrgVersion> list = versionMap.get(key);
                if (list.size() == 1) {
                    version = list.get(0);
                    QueryItem item = dealData(version, systemUnit, map, null, true);
                    result.add(item);
                } else {
                    // 降序排列
                    Collections.sort(list, new Comparator<MultiOrgVersion>() {
                        @Override
                        public int compare(MultiOrgVersion o1, MultiOrgVersion o2) {
                            if (Double.valueOf(o1.getVersion()) > Double.valueOf(o2.getVersion())) {
                                return -1;
                            }
                            if (Double.valueOf(o1.getVersion()) < Double.valueOf(o2.getVersion())) {
                                return 1;
                            }
                            return 0;
                        }
                    });
                    // 最大版本在第一个数据
                    String parentId = null;
                    for (int i = 0; i < list.size(); i++) {
                        version = list.get(i);
                        if (i == 0) {
                            parentId = version.getUuid();
                            QueryItem item = dealData(version, systemUnit, map, null, false);
                            result.add(item);
                        } else {
                            QueryItem item = dealData(version, systemUnit, map, parentId, true);
                            result.add(item);
                        }
                    }
                }
            }
        }
        context.getPagingInfo().setTotalCount(results.size());
        return result;
    }

    private QueryItem dealData(MultiOrgVersion version, MultiOrgSystemUnit systemUnit,
                               Map<String, MultiOrgVersion> map, String parentId, Boolean isLeaf) {
        QueryItem item = new QueryItem();
        item.put("uuid", version.getUuid());
        item.put("fullName", version.getFullName());
        item.put("id", version.getId());
        item.put("systemUnitId", version.getSystemUnitId());
        item.put("initSystemUnitName", version.getInitSystemUnitName());

        item.put("name", systemUnit.getName());
        String sourceVersionName = "";
        if (StringUtils.isNotBlank(version.getSourceVersionUuid())) {
            MultiOrgVersion sourceVersion = map.get(version.getSourceVersionUuid());
            sourceVersionName = sourceVersion.getFullName();
        }
        item.put("sourceVersionName", sourceVersionName);
        item.put("rootVersionId", version.getRootVersionId());//sourceVersion
        item.put("status", version.getStatus());
        item.put("isDefault", version.getIsDefault());
        item.put("createTime", version.getCreateTime());
        item.put("level", 0);

        item.put("parentId", parentId);
        item.put("isLeaf", isLeaf);
        item.put("isExpandFirst", false);

        return item;
    }
}
