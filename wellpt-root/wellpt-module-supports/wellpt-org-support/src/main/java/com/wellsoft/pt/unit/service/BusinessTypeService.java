package com.wellsoft.pt.unit.service;

import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.unit.bean.BusinessTypeBean;
import com.wellsoft.pt.unit.entity.BusinessType;

import java.util.Collection;
import java.util.List;

/**
 * Description: BusinessTypeService.java
 *
 * @author liuzq
 * @date 2013-11-5
 */
public interface BusinessTypeService {
    /**
     * 公共库查询业务类别
     *
     * @param queryInfo
     * @return
     */
    List<BusinessTypeBean> query(QueryInfo queryInfo);

    /**
     * 通过ID获取业务类型
     *
     * @param id
     * @return
     */
    BusinessType getById(String id);

    /**
     * 保存或更新业务类别信息
     *
     * @param bean
     */
    void saveBean(BusinessTypeBean bean);

    /**
     * 将用户ID保存到业务类别里
     *
     * @param businessTypeUuid
     * @param userId
     * @param type
     */
    void saveUnitManagerToBusinessType(String businessTypeUuid, String unitManagerUserId, String unitManagerUserName);

    /**
     * 根据UUID获取业务类别信息
     *
     * @param uuid
     * @return
     */
    BusinessTypeBean getBean(String uuid);

    /**
     * 根据UUID删除业务类别
     *
     * @param uuid
     */
    void remove(String uuid);

    /**
     * 批量删除业务类别
     *
     * @param uuids
     */
    void removes(Collection<String> uuids);

    /**
     * 验证业务类别ID的唯一性
     *
     * @param id
     * @param uuid
     * @return
     */
    boolean validateId(String id, String uuid);

    /**
     * 根据业务类别ID和单位ID获取业务类别
     *
     * @param businessTypeId 业务类别ID
     * @return
     */
    BusinessType getBusinessTypeById(String businessTypeId);

    /**
     * 获取所有的业务类别
     *
     * @return
     */
    List<BusinessType> getAllBusinessTypes();
}
