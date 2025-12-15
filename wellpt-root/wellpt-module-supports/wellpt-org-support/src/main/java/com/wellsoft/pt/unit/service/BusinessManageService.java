package com.wellsoft.pt.unit.service;

import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.unit.bean.BusinessUnitTreeBean;

import java.util.List;

public interface BusinessManageService {
    List<BusinessUnitTreeBean> query(QueryInfo queryInfo);

    /**
     * 获得设置业务负责人，发送人，接收人的表单
     *
     * @param uuid
     * @return
     */
    BusinessUnitTreeBean getBusinessUnitTreeBean(String uuid);
}
