package com.wellsoft.pt.unit.service;

import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.org.entity.User;
import com.wellsoft.pt.unit.bean.CommonUnitBean;
import com.wellsoft.pt.unit.entity.CommonUnit;
import com.wellsoft.pt.unit.entity.CommonUnitTree;

import java.util.Collection;
import java.util.List;

/**
 * Description: CommonUnitService.java
 *
 * @author liuzq
 * @date 2013-11-5
 */
public interface CommonUnitService {

    /**
     * 查询单位
     *
     * @param queryInfo
     * @return
     */
    List<CommonUnitBean> query(QueryInfo queryInfo);

    /**
     * 保存单位信息
     *
     * @param bean
     */
    void saveBean(CommonUnitBean bean);

    /**
     * 根据UUID获取单位信息
     *
     * @param uuid
     * @return
     */
    CommonUnitBean getBean(String uuid);

    /**
     * 根据uuid删除单位
     *
     * @param uuid
     */
    void removeByUuid(String uuid);

    /**
     * 批量删除单位信息
     *
     * @param uuids
     */
    void removeByUuids(Collection<String> uuids);

    /**
     * 根据租户ID获取单位列表
     *
     * @param tenantId
     * @return
     */
    List<CommonUnit> getListByTenantId(String tenantId);

    /**
     * 根据ID获取单位
     *
     * @param id
     * @return
     */
    CommonUnit getById(String id);

    /**
     * 更加用户ID获取用户所在租户的单位列表
     *
     * @param userId
     * @return
     */
    List<CommonUnit> getByUserId(String userId);

    /**
     * 根据当前登陆用户获取所属单位
     *
     * @param userId
     * @return
     */
    CommonUnit getByCurrentUserId(String userId);

    /**
     * 根据业务类别ID获取业务管理单位
     *
     * @param businessTypeId
     * @return
     */
    CommonUnit getByBusinessTypeId(String businessTypeId);

    /**
     * 获取所有公共库单位
     *
     * @return
     */
    List<CommonUnit> getAllCommonUnits();

    /**
     * 通过单位名称模糊查询所有单位
     *
     * @param unitNameKey
     * @return
     */
    List<CommonUnit> getCommonUnitsByBlurUnitName(String unitNameKey);

    /**
     * 根据租户id获得单位id
     *
     * @param tenantId
     * @return
     */
    List<CommonUnit> getCommonUnitByTenantId(String tenantId);

    CommonUnitTree getCommonUnitTreeByUuid(String uuid);

    /**
     * 根据ID获取BEAN
     *
     * @param commonUnitId
     * @return
     */
    CommonUnitBean getCommonUnitBean(String commonUnitId);

    public CommonUnit getByUnitId(String unitId);

    /**
     * 本方法仅用于数据初始化操作
     */
    void updateUnitIdForInit();

    /**
     * 根据单位UNITid O开头的哦不是租户ID是
     *
     * @param unitId
     * @return
     */
    List<User> getAllUserByUnitId(String unitId);

}
