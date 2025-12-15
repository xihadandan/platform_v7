/*
 * @(#)2021年8月7日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.calendar.store;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年8月7日.1	zhongzh		2021年8月7日		Create
 * </pre>
 * @date 2021年8月7日
 */
@Component
public class MyCalendarResourceDataStore extends AbstractDataStoreQueryInterface {

    @Override
    public String getQueryName() {
        return "APP_我的日程资源";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata metadata = CriteriaMetadata.createMetadata();
        metadata.add("uuid", "uuid", "UUID", String.class);
        metadata.add("id", "id", "ID", String.class);
        metadata.add("name", "name", "名称", String.class);
        metadata.add("deptId", "deptId", "部门ID", String.class);
        metadata.add("deptName", "deptName", "部门名称", String.class);
        metadata.add("loginName", "loginName", "登录名", String.class);
        return metadata;
    }

    @Override
    public long count(QueryContext context) {
        return 1;
    }

    @Override
    public List<QueryItem> query(QueryContext context) {
        Map<String, Object> queryParams = context.getQueryParams();

        List<String> creatorList = new ArrayList<>();
        try {
            creatorList = (List<String>) queryParams.get(IdEntity.CREATOR);
        } catch (Exception ignored) {
        }

        List<QueryItem> list = Lists.newArrayList();

        if (CollectionUtils.isEmpty(creatorList)) {
            QueryItem item = new QueryItem();
            list.add(item);
            UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
            item.put("uuid", userDetails.getUserUuid());
            item.put("id", userDetails.getUserId());
            item.put("name", userDetails.getUserName());
            item.put("loginName", userDetails.getLoginName());
            item.put("deptId", userDetails.getMainDepartmentId());
            item.put("deptName", userDetails.getMainDepartmentName());
        } else {
            OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
            List<MultiOrgUserAccount> accounts = orgApiFacade.getAccountsByUserIds(creatorList);
            for (MultiOrgUserAccount account : accounts) {
                QueryItem item = new QueryItem();
                list.add(item);
                item.put("uuid", account.getUuid());
                item.put("id", account.getId());
                item.put("name", account.getUserName());
                item.put("loginName", account.getLoginName());
                item.put("deptId", "");
                item.put("deptName", "");
            }
        }

        return list;
    }

}
