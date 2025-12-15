/*
 * @(#)2017年10月14日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.mapper.service;

import com.wellsoft.pt.basicdata.mapper.bean.MapperBean;
import com.wellsoft.pt.basicdata.mapper.dao.MapperEntityDao;
import com.wellsoft.pt.basicdata.mapper.entity.MapperEntity;
import com.wellsoft.pt.jpa.service.JpaService;
import org.dozer.MappingException;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年10月14日.1	zhongzh		2017年10月14日		Create
 * </pre>
 * @date 2017年10月14日
 */
public interface MapperService extends JpaService<MapperEntity, MapperEntityDao, String> {

    /**
     * 获取
     *
     * @param uuid
     * @return
     */
    MapperBean getBean(String uuid);

    /**
     * 保存
     *
     * @param bean
     * @return
     */
    void saveBean(MapperBean bean);

    /**
     * 删除
     *
     * @param uuid
     * @return
     */
    void remove(String uuid);

    /**
     * 批量删除
     *
     * @param uuid
     * @return
     */
    void removeAll(Collection<String> uuids);

    public Object convert(String source, String mapId, HttpServletRequest request);

    /**
     * Constructs new instance of destinationClass and performs mapping between from source
     *
     * @param source
     * @param destinationClass
     * @param <T>
     * @return
     * @throws MappingException
     */
    <T> T map(Object source, Class<T> destinationClass) throws MappingException;

    /**
     * Performs mapping between source and destination objects
     *
     * @param source
     * @param destination
     * @throws MappingException
     */
    void map(Object source, Object destination) throws MappingException;

    /**
     * Constructs new instance of destinationClass and performs mapping between from source
     *
     * @param source
     * @param destinationClass
     * @param mapId
     * @param <T>
     * @return
     * @throws MappingException
     */
    <T> T map(Object source, Class<T> destinationClass, String mapId) throws MappingException;

    /**
     * Performs mapping between source and destination objects
     *
     * @param source
     * @param destination
     * @param mapId
     * @throws MappingException
     */
    void map(Object source, Object destination, String mapId) throws MappingException;

    /**
     * @param mapId
     * @return
     */
    String find(String mapId);

}
