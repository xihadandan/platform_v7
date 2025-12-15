/*
 * @(#)2016-07-24 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.service;

import com.wellsoft.pt.app.dao.AppProductDao;
import com.wellsoft.pt.app.dto.AppProductDto;
import com.wellsoft.pt.app.entity.AppProdVersionEntity;
import com.wellsoft.pt.app.entity.AppProduct;
import com.wellsoft.pt.app.web.api.request.AppProdKeywordQuery;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.Collection;
import java.util.List;

/**
 * Description: 产品实体类服务接口
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
public interface AppProductService extends JpaService<AppProduct, AppProductDao, String> {

    /**
     * 根据UUID获取
     *
     * @param uuid
     * @return
     */
    AppProduct get(String uuid);

    AppProduct getProductByProdVersionUuid(Long prodVersionUuid);


    AppProductDto saveProduct(AppProductDto product);

    /**
     * 获取所有数据
     *
     * @return
     */
    List<AppProduct> getAll();

    /**
     * 根据实例查询列表
     *
     * @param example
     * @return
     */
    List<AppProduct> findByExample(AppProduct example);

    /**
     * 删除
     *
     * @param entity
     */
    void remove(AppProduct entity);

    /**
     * 批量删除
     *
     * @param entities
     */
    void removeAll(Collection<AppProduct> entities);

    /**
     * 根据UUID删除记录
     *
     * @param uuid
     */
    void remove(String uuid);

    /**
     * 批量删除
     *
     * @param uuids
     */
    void removeAllByPk(Collection<String> uuids);

    /**
     * 根据系统单位Id统计数量
     *
     * @param systemUnitId
     * @return
     */
    long countBySystemUnitId(String systemUnitId);

    boolean idExist(String id);

    void deleteProd(String uuid);


    List<AppProduct> queryByKeyword(AppProdKeywordQuery query);

    AppProductDto getProductDetail(String uuid);

    AppProductDto updateStatus(String uuid, AppProduct.Status status);

    AppProductDto getProductDetailById(String id);

    AppProdVersionEntity getProdVersionByVersionId(String appId);

}
