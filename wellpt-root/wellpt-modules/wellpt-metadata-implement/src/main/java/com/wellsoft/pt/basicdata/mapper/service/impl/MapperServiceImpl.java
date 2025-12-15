/*
 * @(#)2017年10月14日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.mapper.service.impl;

import com.wellsoft.pt.basicdata.mapper.DataFactory;
import com.wellsoft.pt.basicdata.mapper.DataMapper;
import com.wellsoft.pt.basicdata.mapper.MapperException;
import com.wellsoft.pt.basicdata.mapper.QueryParameter;
import com.wellsoft.pt.basicdata.mapper.bean.MapperBean;
import com.wellsoft.pt.basicdata.mapper.dao.MapperEntityDao;
import com.wellsoft.pt.basicdata.mapper.entity.MapperEntity;
import com.wellsoft.pt.basicdata.mapper.service.MapperService;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.workflow.work.service.WorkService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dozer.MappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wellsoft.pt.basicdata.mapper.support.MapperContants.DATA_FACTORY;
import static com.wellsoft.pt.basicdata.mapper.support.MapperContants.FACTORY_DATA_ID;

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
@Service
public class MapperServiceImpl extends AbstractJpaServiceImpl<MapperEntity, MapperEntityDao, String> implements
        MapperService {

    @Autowired
    private WorkService workService;

    @Autowired
    private DyFormFacade dyFormApiFacade;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.mapper.service.MapperService#getBean(java.lang.String)
     */
    @Override
    public MapperBean getBean(String uuid) {
        MapperEntity entity = dao.getOne(uuid);
        MapperBean bean = new MapperBean();
        BeanUtils.copyProperties(entity, bean);
        return bean;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.mapper.service.MapperService#saveBean(com.wellsoft.pt.basicdata.mapper.bean.MapperBean)
     */
    @Override
    @Transactional
    public void saveBean(MapperBean bean) {
        String uuid = bean.getUuid();
        MapperEntity entity = new MapperEntity();
        if (StringUtils.isNotBlank(uuid)) {
            entity = dao.getOne(uuid);
        }
        BeanUtils.copyProperties(bean, entity);
        dao.save(entity);
        // 更新运行时配置
        if (bean.getUpdateMapping() != null && bean.getUpdateMapping() && StringUtils.isNotBlank(bean.getMapping())) {
            DataMapper.getInstance().addDataMapper(bean.getMapId(), null);
        }
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.mapper.service.MapperService#remove(java.lang.String)
     */
    @Override
    @Transactional
    public void remove(String uuid) {
        dao.delete(uuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.mapper.service.MapperService#removeAll(java.util.Collection)
     */
    @Override
    @Transactional
    public void removeAll(Collection<String> uuids) {
        dao.deleteByUuids(uuids);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.mapper.MapperLoader#find(java.lang.String)
     */
    @Override
    public String find(String mapId) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("mapId", mapId);
        values.put("mapId2", mapId);
        List<MapperEntity> lists = listByHQL(
                "from MapperEntity t where t.mapId = :mapId and t.version = (select max(tt.version) from MapperEntity tt where tt.mapId = :mapId2)",
                values);
        return CollectionUtils.isNotEmpty(lists) ? lists.get(0).getMapping() : null;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.mapper.service.MapperService#convert(java.lang.String, java.lang.String)
     */
    @Override
    public Object convert(String source, String mapId, HttpServletRequest request) {
        // List<QueryItem> fieldMappings = new ArrayList<QueryItem>();
        // QueryItem fieldMapping = new QueryItem();
        // fieldMapping.put("value", "aaa>>bbb");
        // fieldMappings.add(fieldMapping);
        // DataMapper.getInstance().addDataMapperIfNeed(mapId, new
        // ApiMapperBuilder<QueryItem>(fieldMappings) {
        // @Override
        // protected String[] fieldsSplitA2B(Object fieldMapping, String mapId)
        // {
        // return ((QueryItem) fieldMapping).getString("value").split(">>");
        // }
        // });
        String[] mapIds = mapId.split(">>");
        URI uriA = null;
        URI uriB = null;
        try {
            uriA = new URI(mapIds[0]);
            uriB = new URI(mapIds.length == 1 ? mapIds[0] : mapIds[1]);
            QueryParameter params = new QueryParameter(uriA.getQuery());
            String dataFactory = params.getString(DATA_FACTORY);
            Class<?> df = Class.forName(dataFactory);
            DataFactory dFactory = (DataFactory) df.newInstance();
            Class<?> srcClass = Class.forName(uriA.getScheme());
            Object srcObj = dFactory.createBean(request, request.getClass(), params.getString(FACTORY_DATA_ID));
            Object sourceObj = dFactory.createData(srcObj, srcClass, source);
            Class<?> destinationClass = Class.forName(uriB.getScheme());
            return DataMapper.getInstance().map(sourceObj, destinationClass, mapId);
        } catch (URISyntaxException ex) {
            throw new MapperException("mapId格式有误", ex);
        } catch (UnsupportedEncodingException ex) {
            throw new MapperException("mapId编码有误", ex);
        } catch (ClassNotFoundException e) {
            logger.warn(e.getMessage(), e);
        } catch (InstantiationException e) {
            logger.warn(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            logger.warn(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public <T> T map(Object source, Class<T> destinationClass) throws MappingException {
        return DataMapper.getInstance().map(source, destinationClass);
    }

    @Override
    public void map(Object source, Object destination) throws MappingException {
        DataMapper.getInstance().map(source, destination);
    }

    @Override
    public <T> T map(Object source, Class<T> destinationClass, String mapId) throws MappingException {
        return DataMapper.getInstance().map(source, destinationClass, mapId);
    }

    @Override
    public void map(Object source, Object destination, String mapId) throws MappingException {
        DataMapper.getInstance().map(source, destination, mapId);
    }
}
