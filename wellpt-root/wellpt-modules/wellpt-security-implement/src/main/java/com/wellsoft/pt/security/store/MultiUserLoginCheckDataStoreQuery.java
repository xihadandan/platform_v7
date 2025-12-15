package com.wellsoft.pt.security.store;

import com.alibaba.fastjson.JSONObject;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.security.config.entity.MultiUserLoginSettingsEntity;
import com.wellsoft.pt.security.config.service.MultiUserLoginSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * 检查登录账号重复数据仓库
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/11/19   Create
 * </pre>
 */
@Component
public class MultiUserLoginCheckDataStoreQuery extends AbstractDataStoreQueryInterface {

    @Autowired
    MultiUserLoginSettingsService multiUserLoginSettingsService;

    @Override
    public List<QueryItem> query(QueryContext context) {
        NativeDao nativeDao = context.getNativeDao();
        Map<String, Object> queryParams = context.getQueryParams();
        MultiUserLoginSettingsEntity entity = multiUserLoginSettingsService.getLoginSettingsEntity();
        if (entity == null) {
            return new ArrayList<>();
        }
        queryParams.putAll(JSONObject.parseObject(JSONObject.toJSONString(entity)));
        return multiUserLoginSettingsService.queryUserLoginDoubleInfo(queryParams, context.getPagingInfo());
        //return nativeDao.namedQuery("userLoginDoubleInfo", queryParams, QueryItem.class, context.getPagingInfo());

    }

    @Override
    public String getQueryName() {
        return "登录设置-重复登录名";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata metadata = CriteriaMetadata.createMetadata();
        metadata.add("loginName", "loginName", "账号名", String.class);
        metadata.add("userName", "userName", "姓名", String.class);
        metadata.add("accountName", "accountName", "重复账号类型", String.class);
        metadata.add("reduplicate", "reduplicate", "重复账号值", String.class);
        metadata.add("systemUnitName", "systemUnitName", "所属系统单位", String.class);
        return metadata;
    }

    @Override
    public long count(QueryContext context) {
        return context.getPagingInfo().getTotalCount();
    }
}
