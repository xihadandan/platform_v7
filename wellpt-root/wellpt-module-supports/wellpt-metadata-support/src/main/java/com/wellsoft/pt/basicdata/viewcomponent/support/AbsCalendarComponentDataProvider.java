package com.wellsoft.pt.basicdata.viewcomponent.support;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.wellsoft.pt.basicdata.datastore.support.Condition;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreColumn;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreConditionConverter;
import com.wellsoft.pt.basicdata.viewcomponent.facade.support.calendar.CalendarComponentDataProvider;
import com.wellsoft.pt.basicdata.viewcomponent.facade.support.calendar.CalendarEventEntity;
import com.wellsoft.pt.basicdata.viewcomponent.facade.support.calendar.CalendarEventParams;
import com.wellsoft.pt.jpa.criteria.Criteria;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.EntityCriteria;
import com.wellsoft.pt.jpa.criterion.Criterion;
import com.wellsoft.pt.jpa.criterion.CriterionOperator;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.jpa.dao.UniversalDao;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbsCalendarComponentDataProvider<E extends CalendarEventEntity> implements
        CalendarComponentDataProvider<E> {
    protected Class<E> entityClass;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private NativeDao nativeDao;
    @Autowired
    private UniversalDao universalDao;

    public AbsCalendarComponentDataProvider() {
        super();
        try {
            Type genType = this.getClass().getGenericSuperclass();
            if (genType instanceof ParameterizedType) {
                Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
                entityClass = (Class<E>) params[0];
            }
        } catch (Exception e) {
            logger.error("AbsCalendarComponentDataProvider 实例化异常：", e);
            throw Throwables.propagate(e);
        }
    }

    public static Map<String, DataStoreColumn> criteriaMetadataToColumnMap(CriteriaMetadata criteriaMetadata) {
        Map<String, DataStoreColumn> columnMap = Maps.newHashMap();
        DataStoreColumn tempDataStoreColumn = null;
        for (int j = 0; j < criteriaMetadata.length(); j++) {
            tempDataStoreColumn = new DataStoreColumn();
            String columnIndex = criteriaMetadata.getColumnIndex(j);
            String comment = criteriaMetadata.getComment(j);
            tempDataStoreColumn.setColumnIndex(criteriaMetadata.getColumnIndex(j));
            tempDataStoreColumn.setColumnName(criteriaMetadata.getMapColumnIndex(columnIndex));
            tempDataStoreColumn.setDataType(criteriaMetadata.getDataType(j).getType());
            tempDataStoreColumn.setTitle(comment);
            columnMap.put(columnIndex, tempDataStoreColumn);
        }
        return columnMap;
    }

    public static String condition2whereSql(Criteria criteria, List<Condition> conList,
                                            Map<String, DataStoreColumn> columnMap) {
        if (CollectionUtils.isNotEmpty(conList)) {
            for (Condition con : conList) {
                Criterion c = DataStoreConditionConverter.covertCriterion(con, columnMap);
                criteria.addCriterion(c);
            }
        }
        StringBuilder whereSql = new StringBuilder(criteria.getCriterion().toSqlString(criteria)
                .replaceAll("this_.", ""));
        return whereSql.toString();
    }

    @Override
    public CriteriaMetadata getColumns() {
        // String tableName = this.this.getEventService().getTableName();
        // Criteria c = this.nativeDao.createTableCriteria(tableName);
        EntityCriteria c = new EntityCriteria(this.universalDao, entityClass);
        return c.getCriteriaMetadata();
    }

    @Override
    @Transactional
    public List<E> loadEvents(CalendarEventParams ep) {
        CriteriaMetadata criteriaMetadata = this.getColumns();
        Map<String, DataStoreColumn> columnMap = criteriaMetadataToColumnMap(criteriaMetadata);
        String tableName = this.getEventService().getTableName();
        Criteria c = this.nativeDao.createTableCriteria(tableName);
        String whereSql = condition2whereSql(c, ep.getCriterions(), columnMap);

        HashMap<String, Object> params = createLoadEventParams(ep, whereSql);

        return this.getEventService().loadEvents(ep, params);
    }

    protected HashMap<String, Object> createLoadEventParams(CalendarEventParams ep, String whereSql) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("whereSql", whereSql);
        params.put("startTime", new Timestamp(ep.getStartTime().getTime()));
        params.put("endTime", new Timestamp(ep.getEndTime().getTime()));
        if (StringUtils.isNotBlank(ep.getBelongObjId())) {
            params.put("belongObjId", ep.getBelongObjId());
        }
        // 有搜索过滤条件，则需要对params中赋值
        if (StringUtils.isNotBlank(whereSql) && !"1=1".equals(whereSql)) {
            for (Condition con : ep.getCriterions()) {
                String type = con.getType();
                if (CriterionOperator.and.getType().equals(type) || CriterionOperator.or.getType().equals(type)) {
                    // 第一层是or或and的设置，第二层才是变量的定义
                    if (con.getConditions() != null) {
                        for (Condition subCon : con.getConditions()) {
                            if ("like".equals(subCon.getType()) || "not like".equals(subCon.getType())) {
                                params.put(subCon.getColumnIndex(), "%" + subCon.getValue() + "%");
                            } else {
                                if (subCon.getColumnIndex().startsWith("is")) { // is开头的字段，认为是个数字形的字段
                                    Object value = subCon.getValue();
                                    if (value instanceof Collection) {
                                        Object collect = ((Collection) value).stream()
                                                .map(s -> Integer.parseInt(s.toString()))
                                                .collect(Collectors.toList());
                                        params.put(subCon.getColumnIndex(), collect);
                                    } else {
                                        params.put(subCon.getColumnIndex(), Integer.valueOf(subCon.getValue().toString()));
                                    }
                                } else {
                                    params.put(subCon.getColumnIndex(), subCon.getValue());
                                }

                            }

                        }
                    }
                } else {
                    if (con.getColumnIndex() == null) {
                        continue;
                    }
                    if (con.getColumnIndex().startsWith("is")) {
                        Object value = con.getValue();
                        if (value instanceof Collection) {
                            Object collect = ((Collection) value).stream()
                                    .map(s -> Integer.parseInt(s.toString()))
                                    .collect(Collectors.toList());
                            params.put(con.getColumnIndex(), collect);
                        } else {
                            params.put(con.getColumnIndex(), Integer.valueOf(con.getValue().toString()));
                        }
                    } else {
                        params.put(con.getColumnIndex(), con.getValue());
                    }

                }
            }
        }
        return params;
    }

    @Override
    public boolean deleteEvent(String uuid) {
        this.getEventService().deleteEvent(uuid);
        return true;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.viewcomponent.facade.support.calendar.CalendarComponentDataProvider#getEvent(java.lang.String)
     */
    @Override
    public E getEvent(String uuid) {
        return this.getEventService().getEvent(uuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.viewcomponent.facade.support.calendar.CalendarComponentDataProvider#saveEvent(com.wellsoft.pt.basicdata.viewcomponent.facade.support.calendar.CalendarEventEntity)
     */
    @Override
    public String saveEvent(E e) {
        // 这里不知道为什么，明明 e里面有UUID，但是用save方法会创建了一个新的UUID出来，导致编辑保存会变成新建，所以
        // 这里必须要通过BeanUtils.copyPropertiesExcludeBaseField来做保存处理
        E obj = e;
        if (StringUtils.isNotBlank(e.getUuid())) {
            obj = this.getEvent(e.getUuid());
            BeanUtils.copyPropertiesExcludeBaseField(e, obj, new String[]{"systemUnitId"});
        }
        return this.getEventService().saveEvent(obj);
    }

    @Override
    public Class<E> getEntityClass() {
        return entityClass;
    }

    @Override
    public String getStatusFieldName() {
        return null;
    }

    @Override
    public String getResourceFieldName() {
        return CalendarEventEntity.BELONG_OBJ_ID;
    }

}
