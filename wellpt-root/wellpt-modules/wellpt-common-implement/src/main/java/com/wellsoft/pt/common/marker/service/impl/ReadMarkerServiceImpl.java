/*
 * @(#)2013-3-1 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.marker.service.impl;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.common.marker.dao.ReadMarkerDao;
import com.wellsoft.pt.common.marker.entity.ReadMarker;
import com.wellsoft.pt.common.marker.service.ReadMarkerService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.PropertyDescriptor;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-1.1	zhulh		2013-3-1		Create
 * </pre>
 * @date 2013-3-1
 */
@Service
@Transactional
public class ReadMarkerServiceImpl extends BaseServiceImpl implements ReadMarkerService {

    private static final Map<Class<?>, Map<String, PropertyDescriptor>> classPropertyDescriptorMap = new HashMap<Class<?>, Map<String, PropertyDescriptor>>();

    @Autowired
    private ReadMarkerDao readMarkerDao;
    private String hql = "select t.entityUuid as entityUuid from ReadMarker t where t.userId = :userId and t.entityUuid in (:entityUuids)";

    /**
     * 根据对象UUID，将对象设置为对所有人未读，删除UUID相关的记录即可 (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.marker.service.ReadMarkerService#markNew(java.lang.String)
     */
    @Override
    public void markNew(String uuid) {
        this.readMarkerDao.deleteByEntityUuid(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.marker.service.ReadMarkerService#markNew(com.wellsoft.pt.core.entity.IdEntity)
     */
    @Override
    public <ENTITY extends IdEntity> void markNew(ENTITY entity) {
        this.markNew(entity.getUuid());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.marker.service.ReadMarkerService#markNew(java.lang.String, java.lang.String)
     */
    @Override
    public void markNew(String uuid, String userId) {
        this.readMarkerDao.delete(uuid, userId);
    }

    @Override
    public void markNewList(List<String> uuidList, String userId) {
        for (String uuid : uuidList) {
            this.markNew(uuid, userId);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.marker.service.ReadMarkerService#markNew(com.wellsoft.pt.core.entity.IdEntity, java.lang.String)
     */
    @Override
    public <ENTITY extends IdEntity> void markNew(ENTITY entity, String userId) {
        this.markNew(entity.getUuid(), userId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.marker.service.ReadMarkerService#markRead(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void markRead(String entityUuid, String userId) {
        ReadMarker example = new ReadMarker();
        example.setEntityUuid(entityUuid);
        example.setUserId(userId);
        if (!isExist(example)) {
            example.setReadTime(new Date());
            this.readMarkerDao.save(example);
        }
    }

    @Override
    public void markReadList(List<String> entityUuidList, String userId) {
        for (String entityUuid : entityUuidList) {
            this.markRead(entityUuid, userId);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.marker.service.ReadMarkerService#markRead(com.wellsoft.pt.core.entity.IdEntity,
     * java.lang.String)
     */
    @Override
    public <ENTITY extends IdEntity> void markRead(ENTITY entity, String userId) {
        this.markRead(entity.getUuid(), userId);
    }

    private boolean isExist(ReadMarker readMarker) {
        List<ReadMarker> readMarkers = this.readMarkerDao.findByExample(readMarker);
        return readMarkers.size() != 0;
    }

    private boolean isExist(String entityUuid, String userId) {
        ReadMarker readMarker = new ReadMarker();
        readMarker.setEntityUuid(entityUuid);
        readMarker.setUserId(userId);
        return isExist(readMarker);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.marker.service.ReadMarkerService#isRead(java.lang.String, java.lang.String)
     */
    @Override
    public ReadMarker get(String entityUuid, String userId) {
        ReadMarker example = new ReadMarker();
        example.setEntityUuid(entityUuid);
        example.setUserId(userId);
        List<ReadMarker> readMarkers = this.readMarkerDao.findByExample(example);
        return readMarkers.size() == 0 ? null : readMarkers.get(0);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.marker.service.ReadMarkerService#getReadMarkers(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReadMarker> getReadMarkers(String entityUuid) {
        ReadMarker example = new ReadMarker();
        example.setEntityUuid(entityUuid);
        List<ReadMarker> readMarkers = this.readMarkerDao.findByExample(example);
        return readMarkers;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.marker.service.ReadMarkerService#getReadList(java.util.List,
     * java.lang.String)
     */
    @Override
    public <ENTITY extends IdEntity> List<String> getReadList(List<String> uuids, String userId) {
        List<String> readList = new ArrayList<String>();
        for (String uuid : uuids) {
            if (isExist(uuid, userId)) {
                readList.add(uuid);
            }
        }
        return readList;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.marker.service.ReadMarkerService#getReadList(java.util.Collection,
     * java.lang.String)
     */
    @Override
    public <ENTITY extends IdEntity> Collection<ENTITY> getReadList(Collection<ENTITY> entities, String userId) {
        List<ENTITY> readList = new ArrayList<ENTITY>();
        for (ENTITY entity : entities) {
            if (isExist(entity.getUuid(), userId)) {
                readList.add(entity);
            }
        }
        return readList;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.marker.service.ReadMarkerService#getUnReadList(java.util.List,
     * java.lang.String)
     */
    @Override
    public <ENTITY extends IdEntity> List<String> getUnReadList(List<String> uuids, String userId) {
        List<String> unReadList = new ArrayList<String>();
        for (String uuid : uuids) {
            if (!isExist(uuid, userId)) {
                unReadList.add(uuid);
            }
        }
        return unReadList;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.marker.service.ReadMarkerService#getUnReadList(java.util.Collection,
     * java.lang.String)
     */
    @Override
    public <ENTITY extends IdEntity> Collection<ENTITY> getUnReadList(Collection<ENTITY> entities, String userId) {
        List<ENTITY> unReadList = new ArrayList<ENTITY>();
        for (ENTITY entity : entities) {
            if (!isExist(entity.getUuid(), userId)) {
                unReadList.add(entity);
            }
        }
        return unReadList;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.marker.service.ReadMarkerService#markList(java.util.List,
     * java.lang.String, java.lang.String)
     */
    @Override
    public <ENTITY extends IdEntity> void markList(List<ENTITY> entities, String userId, String flagField) {
        for (ENTITY entity : entities) {
            BeanWrapper wrapper = new BeanWrapperImpl(entity);
            PropertyDescriptor propertyDescriptor = getPropertyDescriptor(entity.getClass(), flagField);
            if (propertyDescriptor != null) {
                Class<?> propertyType = propertyDescriptor.getPropertyType();
                if (isExist(entity.getUuid(), userId)) {
                    if (propertyType.isAssignableFrom(Boolean.class)) {
                        wrapper.setPropertyValue(flagField, true);
                    } else if (propertyType.isAssignableFrom(String.class)) {
                        wrapper.setPropertyValue(flagField, "true");
                    } else if (propertyType.isAssignableFrom(Integer.class)) {
                        wrapper.setPropertyValue(flagField, "1");
                    }
                } else {
                    if (propertyType.isAssignableFrom(Boolean.class)) {
                        wrapper.setPropertyValue(flagField, false);
                    } else if (propertyType.isAssignableFrom(String.class)) {
                        wrapper.setPropertyValue(flagField, "false");
                    } else if (propertyType.isAssignableFrom(Integer.class)) {
                        wrapper.setPropertyValue(flagField, "0");
                    }
                }
            }
        }
    }

    private PropertyDescriptor getPropertyDescriptor(Class<? extends IdEntity> entityClass, String flagField) {
        if (classPropertyDescriptorMap.containsKey(entityClass)) {
            Map<String, PropertyDescriptor> propertyDescriptorMap = classPropertyDescriptorMap.get(entityClass);
            return propertyDescriptorMap.get(flagField);
        } else {
            classPropertyDescriptorMap.put(entityClass, new HashMap<String, PropertyDescriptor>());
            BeanWrapper wrapper = new BeanWrapperImpl(entityClass);
            PropertyDescriptor[] propertyDescriptors = wrapper.getPropertyDescriptors();
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                if (propertyDescriptor.getName().equals(flagField)) {
                    classPropertyDescriptorMap.get(entityClass).put(propertyDescriptor.getName(), propertyDescriptor);
                    return propertyDescriptor;
                }
            }
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.marker.service.ReadMarkerService#markList(java.util.List,
     * java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void markList(List<QueryItem> items, String userId, String keyField, String flagField) {
        List<String> entityUuids = new ArrayList<String>();
        Map<String, QueryItem> queryItemMap = new HashMap<String, QueryItem>();
        for (QueryItem queryItem : items) {
            Object keyValue = queryItem.get(keyField);
            String entityUuid = keyValue == null ? "" : keyValue.toString();
            if (StringUtils.isNotBlank(entityUuid)) {
                entityUuids.add(entityUuid);
                // if (isExist(entityUuid, userId)) {
                // queryItem.put(flagField, true);
                // } else {
                // queryItem.put(flagField, false);
                // }
            }
            queryItemMap.put(entityUuid, queryItem);
        }

        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userId", userId);
        values.put("entityUuids", entityUuids);
        if (entityUuids.isEmpty()) {
            entityUuids.add(UUID.randomUUID().toString());
        }
        List<String> readMarkerEntityUuids = this.readMarkerDao.find(hql, values);

        for (String key : queryItemMap.keySet()) {
            if (readMarkerEntityUuids.contains(key)) {
                queryItemMap.get(key).put(flagField, true);
            } else {
                queryItemMap.get(key).put(flagField, false);
            }
        }
    }

    @Override
    @Transactional
    public Boolean readStatusUpdate(String userId, String[] flowInstUuids, String[] dataStatuses) {
        Boolean isSuccess = true;
        try {
            for (int i = 0; i < flowInstUuids.length; i++) {
                String dataStatus = dataStatuses[i];
                if ("true".equals(dataStatus)) {
                    //未阅变已阅
                    markRead(flowInstUuids[i], userId);
                } else if ("false".equals(dataStatus)) {
                    //已阅变未阅
                    this.readMarkerDao.delete(flowInstUuids[i], userId);
                }
            }
        } catch (Exception e) {
            logger.error("readStatusUpdate 失败：", e);
            isSuccess = false;
        }

        return isSuccess;
    }


}
