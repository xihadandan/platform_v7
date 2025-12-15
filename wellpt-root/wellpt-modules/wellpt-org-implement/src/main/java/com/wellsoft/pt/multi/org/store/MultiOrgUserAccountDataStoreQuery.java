/*
 * @(#)2020年1月2日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.store;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeDto;
import com.wellsoft.pt.multi.org.entity.MultiOrgElement;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.service.MultiOrgTreeNodeService;
import org.apache.commons.collections.CollectionUtils;
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
 * 2020年1月2日.1	wangrf		2020年1月2日		Create
 * </pre>
 * @date 2020年1月2日
 */
@Component
public class MultiOrgUserAccountDataStoreQuery extends AbstractDataStoreQueryInterface {

    @Autowired
    private MultiOrgService multiOrgService;
    @Autowired
    private MultiOrgTreeNodeService multiOrgTreeNodeService;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "用户列表";
    }

    /**
     * 如何描述该方法
     * <p>
     * 定义查询字段
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata metadata = CriteriaMetadata.createMetadata();
        metadata.add("code", "code", "CODE", String.class);
        metadata.add("id", "id", "用户id", String.class);
        metadata.add("type", "type", "用户类型", Integer.class);
        metadata.add("lastLoginTime", "last_login_time", "最后一次登录时间", Date.class);
        metadata.add("loginName", "login_name", "登录名", String.class);
        metadata.add("loginNameZh", "login_name_zh", "中文账号名", String.class);
        metadata.add("loginNameLowerCase", "login_name_lower_case", "登录名称小写", String.class);
        metadata.add("userName", "user_name", "用户名", String.class);
        metadata.add("password", "password", "密码", String.class);
        metadata.add("md5LoginName", "md5_login_name", "md5LoginName", String.class);
        metadata.add("systemUnitId", "system_unit_id", "归属组织ID", String.class);
        metadata.add("remark", "remark", "备注信息", String.class);
        metadata.add("isExpired", "is_expired", "是否过期", Integer.class);
        metadata.add("isLocked", "is_locked", "是否冻结锁住", Integer.class);
        metadata.add("isForbidden", "is_forbidden", "是否禁止", Integer.class);
        metadata.add("jobIds", "job_ids", "职位ids", String.class);
        metadata.add("deptIds", "dept_ids", "部门ids", String.class);
        metadata.add("jobNames", "job_names", "所有职位名称", String.class);
        metadata.add("deptNames", "dept_names", "所有部门名称", String.class);

        metadata.add("userId", "user_id", "用户id", String.class);
        metadata.add("employeeNumber", "employee_number", "员工编号", String.class);
        metadata.add("fax", "fax", "传真号", String.class);
        metadata.add("idNumber", "id_number", "id编号", String.class);
        metadata.add("mobilePhone", "mobile_phone", "手机号码", String.class);
        metadata.add("officePhone", "office_phone", "办公电话", String.class);
        metadata.add("photoUuid", "photo_uuid", "所有部门名称", String.class);
        metadata.add("sex", "sex", "性别", String.class);
        metadata.add("homePhone", "home_phone", "家庭电话", String.class);
        metadata.add("englishName", "english_name", "英文名", String.class);
        metadata.add("mainEmail", "main_email", "邮件", String.class);
        metadata.add("smallPhotoUuid", "small_photo_uuid", "照片uuid", String.class);

        metadata.add("uuid", "uuid", "UUID", String.class);
        metadata.add("recVer", "rec_ver", "版本号", Integer.class);
        metadata.add("creator", "creator", "创建人", String.class);
        metadata.add("createTime", "create_time", "创建时间", Date.class);
        metadata.add("modifier", "modifier", "修改人", String.class);
        metadata.add("modifyTime", "modify_time", "修改时间", Date.class);
        metadata.add("attach", "attach", "附件属性", String.class);
        metadata.add("pwdErrorLock", "pwdErrorLock", "是否密码错误锁定", String.class);
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
     * 真正执行查询的方法
     * 方法改造自MultiOrgUserAccountServiceImpl.query(QueryInfo queryInfo)
     * 前端参数上传：query_LIKES_eleIdPath改成eleIdPath
     * query_LIKES_name:改成name
     *
     * @author wangrf
     */
    @Override
    public List<QueryItem> query(QueryContext context) {
        Map<String, Object> params = context.getQueryParams();
        // params.put("eleIdPath", "V0000000420/J0000000081");
        // params.put("currentUserUnitId", "S0000000070");
        // 需要按组织节点过滤，所以计算出该节点对应的所有职位
        if (params.containsKey("eleIdPath")) {
            String[] ids = params.get("eleIdPath").toString().split("/");
            String verId = ids[0];
            String eleId = ids[ids.length - 1];
            List<OrgTreeNodeDto> jobs = this.multiOrgTreeNodeService.queryJobListByEleIdAndVersionId(verId, eleId);
            if (CollectionUtils.isNotEmpty(jobs)) {
                List<String> jobSql = Lists.newArrayList();
                for (OrgTreeNodeDto dto : jobs) {
                    jobSql.add(" ( i.job_ids like '%" + dto.getEleId() + "%' ) ");
                }
                params.put("jobSql", StringUtils.join(jobSql, "or"));
            } else {
                // 该节点下面没有任何职位，可以直接返回空数组了
                return Lists.newArrayList();
            }
        }
        List<QueryItem> objs = context.getNativeDao().namedQuery("queryUserAccountListForPage", getQueryParams(context),
                QueryItem.class, context.getPagingInfo());
        if (CollectionUtils.isNotEmpty(objs)) {
            for (QueryItem vo : objs) {
                List<String> jobNames = Lists.newArrayList();
                List<String> deptNames = Lists.newArrayList();
                String[] jobIds = StringUtils.isBlank(vo.getString("jobIds")) ? new String[0]
                        : vo.getString("jobIds").split(";");
                String[] deptIds = StringUtils.isBlank(vo.getString("deptIds")) ? new String[0]
                        : vo.getString("deptIds").split(";");
                if (jobIds.length > 0) {
                    for (String id : jobIds) {
                        MultiOrgElement e = multiOrgService.getOrgElementById(id);
                        if (e != null)
                            jobNames.add(e.getName());
                    }
                }
                if (deptIds.length > 0) {
                    for (String id : deptIds) {
                        MultiOrgElement e = multiOrgService.getOrgElementById(id);
                        if (e != null)
                            deptNames.add(e.getName());
                    }
                }
                vo.put("jobNames", StringUtils.join(jobNames, ";"));
                vo.put("deptNames", StringUtils.join(deptNames, ";"));
            }
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
        param.put("jobSql", queryParams.get("jobSql"));
        // 前端搜索框输入的内容
        if (queryParams.get("keyword") != null) {
            queryParams.put("keyword", queryParams.get("keyword").toString().replaceAll("_", "\\\\_"));
        }
        param.put("name", queryParams.get("keyword"));
        param.put("type", queryParams.get("type"));
        param.put("systemUnitId", queryParams.get("currentUserUnitId"));
        String orderByStr = context.getOrderString().replace("order by", "");
        if (StringUtils.isBlank(orderByStr)) {
            orderByStr = "a.code";
        }
        param.put("orderBy", orderByStr);
        return param;
    }
}
