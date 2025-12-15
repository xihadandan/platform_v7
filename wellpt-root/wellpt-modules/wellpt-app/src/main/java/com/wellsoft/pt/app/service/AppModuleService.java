/*
 * @(#)2016-05-09 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.service;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.dao.AppModuleDao;
import com.wellsoft.pt.app.dto.AppModuleDto;
import com.wellsoft.pt.app.entity.AppModule;
import com.wellsoft.pt.app.entity.AppModuleResSeqEntity;
import com.wellsoft.pt.app.web.api.request.AppModuleKeywordQuery;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author t
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-05-09.1	t		2016-05-09		Create
 * </pre>
 * @date 2016-05-09
 */
public interface AppModuleService extends JpaService<AppModule, AppModuleDao, String> {

    /**
     * 根据UUID获取
     *
     * @param uuid
     * @return
     */
    AppModule get(String uuid);

    AppModule getById(String id);

    /**
     * @param id
     * @return
     */
    String getModuleNameById(String id);

    /**
     * 获取所有数据
     *
     * @return
     */
    List<AppModule> getAll();

    /**
     * 根据实例查询列表
     *
     * @param example
     * @return
     */
    List<AppModule> findByExample(AppModule example);

    /**
     * 删除
     *
     * @param entity
     */
    void remove(AppModule entity);

    /**
     * 批量删除
     *
     * @param entities
     */
    void removeAll(Collection<AppModule> entities);

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
     * 按父节点id统计数量
     *
     * @param uuid
     * @return
     */
    Long countByParentUuid(String uuid);

    /**
     * 如何描述该方法
     *
     * @param ids
     * @return
     */
    List<AppModule> getByIds(String[] ids);

    /**
     * 根据系统单位Id统计数量
     *
     * @param systemUnitId
     * @return
     */
    long countBySystemUnitId(String systemUnitId);


    void saveModuleResSeq(List<AppModuleResSeqEntity> list);

    List<AppModuleResSeqEntity> listModuleResSeq(String moduleId, String type);

    Boolean moduleIdExist(String id);

    void updateEnabled(String uuid, boolean enable);

    String saveModule(AppModuleDto dto);

    List<AppModule> queryByKeyword(AppModuleKeywordQuery request);

    List<AppModule> listModuleByIds(List<String> id);

    List<AppModule> getAllEnableModules();

    void deleteModulesByUuids(List<String> uuid);

    List<String> queryRelaModuleIds(String moduleId);

    List<QueryItem> queryRelaModulesByModuleId(String moduleId);

    List<AppModule> listModuleUnderSystem(String system, String tenant);

    Set<String> getModuleRelaSystems(String moduleId);
}
