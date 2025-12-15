/*
 * @(#)2020年1月6日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.store;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.multi.org.entity.MultiOrgVersion;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgVersionFacade;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author wangrf
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年1月6日.1	wangrf		2020年1月6日		Create
 * </pre>
 * @date 2020年1月6日
 */
@Component
public class MultiOrgTreeNodeDataStoreQuery extends AbstractDataStoreQueryInterface {

    @Autowired
    private MultiOrgVersionFacade multiOrgVersionFacade;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "组织管理_配置组织版本";
    }

    /**
     * 如何描述该方法
     * 改造于
     * com.wellsoft.pt.multi.org.service.impl.MultiOrgTreeNodeServiceImpl.query(QueryInfo queryInfo)
     * 方法
     * 参数问题：
     * query_GTS_versionId修改为versionId，且为必传参数
     * query_LIKES_name改为name
     * query_LIKES_eleIdPath改为eleIdPath  注：没有找到有上传的场景
     *
     * @see
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata metadata = CriteriaMetadata.createMetadata();
        metadata.add("eleUuid", "ele_uuid", "eleUuid", String.class);
        metadata.add("code", "code", "CODE", String.class);
        metadata.add("name", "name", "名称", String.class);
        metadata.add("shortName", "short_name", "简称", String.class);
        metadata.add("sapCode", "sap_code", "sapCode", String.class);
        metadata.add("remark", "remark", "备注", String.class);
        metadata.add("type", "type", "类型", String.class);
        metadata.add("eleNamePath", "ele_name_path", "完整的路径", String.class);
        metadata.add("unitNamePath", "unit_name_path", "单位路径", String.class);
        metadata.add("deptNamePath", "dept_name_path", "部门开始算路径", String.class);
        metadata.add("systemUnitId", "system_unit_id", "归属的系统单位", String.class);
        metadata.add("functionType", "function_type", "functionType", String.class);

        metadata.add("eleId", "ele_id", "对应的节点元素的ID", String.class);
        metadata.add("eleIdPath", "ele_id_path", "完整的节点ID全路径", String.class);
        metadata.add("orgVersionId", "org_version_id", "组织版本ID", String.class);
        metadata.add("jsonParams", "json_params", "单位节点对应的参数", String.class);

        metadata.add("uuid", "uuid", "UUID", String.class);
        metadata.add("recVer", "rec_ver", "版本号", Integer.class);
        metadata.add("creator", "creator", "创建人", String.class);
        metadata.add("createTime", "create_time", "创建时间", Date.class);
        metadata.add("modifier", "modifier", "修改人", String.class);
        metadata.add("modifyTime", "modify_time", "修改时间", Date.class);
        metadata.add("attach", "attach", "附件属性", String.class);

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
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface#query(com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    public List<QueryItem> query(QueryContext context) {
        Map<String, Object> params = getQueryParams(context);
        // 默认的排序
        String eleIdPath = params.get("eleIdPath") == null ? "" : params.get("eleIdPath").toString();
        String orgVersionId = params.get("versionId").toString();
        // 查询的是引入的其他系统单位的情况
        if (StringUtils.isNotBlank(eleIdPath) && !eleIdPath.startsWith(orgVersionId)) {
            // 因为存在对方升级了版本，所以不能直接通过版本号来过滤，需要通过引入单位的单位ID来过滤
            String vid = eleIdPath.indexOf("/") != -1 ? eleIdPath.substring(0, eleIdPath.indexOf("/")) : eleIdPath;
            MultiOrgVersion ver = multiOrgVersionFacade.getVersionById(vid);
            params.put("otherSysUnitId", ver.getSystemUnitId());
            // 需要移除 eleIdPath的赋值5
            params.put("eleIdPath", null);
        }
        List<QueryItem> objs = context.getNativeDao().namedQuery("queryOrgTreeNodeListForPage", params,
                QueryItem.class, context.getPagingInfo());
        // 遍历结果集
        for (QueryItem o : objs) {
            if ("V".equals(o.getString("type"))) {
                // 根据ele_id进行查询版本信息
                Map<String, Object> pa = new HashMap<String, Object>();
                pa.put("id", o.getString("eleId"));
                MultiOrgVersion version = context
                        .getNativeDao()
                        .findUnique(
                                "select name name,function_type_name functionTypeName,version version from MULTI_ORG_VERSION where id = :id",
                                pa, MultiOrgVersion.class);
                if (version != null) {
                    o.put("fullName", version.getFullName());
                }
            }
            // TODO 获取
        }

        return objs;
    }

    /**
     * 如何描述该方法
     *
     * @param context
     * @return
     */
    private Map<String, Object> getQueryParams(QueryContext context) {
        Map<String, Object> param = new HashMap<String, Object>();
        Map<String, Object> queryParams = context.getQueryParams();
        param.put("versionId", queryParams.get("versionId"));
        param.put("eleIdPath", queryParams.get("eleIdPath"));
        param.put("otherSysUnitId", queryParams.get("otherSysUnitId"));
        param.put("name", queryParams.get("name"));
        param.put("sapCode", queryParams.get("sapCode"));
        param.put("remark", queryParams.get("remark"));
        String orderBy = context.getOrderString();
        if (StringUtils.isEmpty(orderBy)) {
            param.put("orderBy", "ele_id_path asc");
        } else {
            param.put("orderBy", orderBy.replace(" order by ", ""));
        }
        return param;
    }

}
