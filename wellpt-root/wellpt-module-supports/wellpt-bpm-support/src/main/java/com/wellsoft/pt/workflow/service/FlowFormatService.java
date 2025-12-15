/*
 * @(#)2012-12-3 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.Page;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.workflow.dao.FlowFormatDao;
import com.wellsoft.pt.workflow.entity.FlowFormat;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-12-3.1	zhulh		2012-12-3		Create
 * </pre>
 * @date 2012-12-3
 */
public interface FlowFormatService extends JpaService<FlowFormat, FlowFormatDao, String> {
    FlowFormat get(String uid);

    FlowFormat getByCode(String code);

    Page<FlowFormat> getAll(Integer page, Integer rows);

    void save(FlowFormat entity);

    List<FlowFormat> saveAll(Collection<FlowFormat> entities);

    void remove(FlowFormat entity);

    void removeAll(Collection<FlowFormat> entities);

    void removeByPk(String uid);

    void removeAllByPk(Collection<String> uids);

    /**
     * 信息格式编号是否存在
     *
     * @param code
     * @return
     */
    boolean existsFormatCode(String code);

    /**
     * 根据信息格式编号、实体集合及动态表单数据，解析并返回相应的信息内容
     *
     * @param code
     * @param entities
     * @param dytableMap
     * @return
     */
    String getFormatValue(String code, List<IdEntity> entities, Map<?, ?> dytableMap, Map<String, Object> extraData);

    /**
     * @param system
     * @return
     */
    List<FlowFormat> listBySystem(String system);
}
