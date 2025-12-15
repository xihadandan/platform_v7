package com.wellsoft.pt.dms.dao.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.BaseQueryItem;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.PropertyFilter;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.dms.entity.DataMarkEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.jpa.support.QueryItemResultTransformer;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 数据标记 DAO实现类
 *
 * @author chenq
 * @date 2018/6/15
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/6/15    chenq		2018/6/15		Create
 * </pre>
 */
@Repository
public class DataMarkDaoImpl extends AbstractJpaDaoImpl<DataMarkEntity, String> {

    public void updateDataTableMarked(List<? extends DataMarkEntity> dataList, String tableName, String statusColumn, String updateTimeColumn,
                                      boolean isDeleteMark) {
        StringBuilder sql = new StringBuilder(String.format("update %s set %s='%s' %s  where uuid in (:uuids)", tableName, statusColumn,
                (isDeleteMark ? 0 : 1), (StringUtils.isNotBlank(updateTimeColumn) ? "," + updateTimeColumn + "=sysdate" : "")));

        Query query = this.getSession().createSQLQuery(sql.toString());
        Map<String, Object> params = Maps.newHashMap();
        List<String> uuids = Lists.newArrayList();
        for (DataMarkEntity dme : dataList) {
            uuids.add(dme.getDataUuid());
        }
        params.put("uuids", uuids);
        query.setProperties(params);
        query.executeUpdate();
    }

    @Override
    public DataMarkEntity getOne(String s) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public DataMarkEntity getUniqueBy(String propertyName, Object value) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public DataMarkEntity getOneByNameHQLQuery(String queryName, Map<String, Object> values) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public DataMarkEntity getOneByNameSQLQuery(String queryName, Map<String, Object> values) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public List<DataMarkEntity> listByNameHQLQuery(String queryName, Map<String, Object> values) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public List<DataMarkEntity> listByNameSQLQuery(String queryName, Map<String, Object> values) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public List<DataMarkEntity> listByNameSQLQuery(String queryName, Map<String, Object> values, PagingInfo pagingInfo) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public List<DataMarkEntity> listByNameHQLQuery(String queryName, Map<String, Object> values, PagingInfo pagingInfo) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public List<QueryItem> listQueryItemByNameHQLQuery(String queryName, Map<String, Object> values, PagingInfo pagingInfo) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public <ITEM extends BaseQueryItem> List<ITEM> listItemByNameSQLQuery(String queryName, Class<ITEM> itemClass, Map<String, Object> values,
                                                                          PagingInfo pagingInfo) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public <ITEM extends BaseQueryItem> List<ITEM> listItemByNameHQLQuery(String queryName, Class<ITEM> itemClass, Map<String, Object> values,
                                                                          PagingInfo pagingInfo) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public List<QueryItem> listQueryItemByHQL(String hql, Map<String, Object> values, PagingInfo pagingInfo) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public List<QueryItem> listQueryItemBySQL(String sql, Map<String, Object> values, PagingInfo pagingInfo) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public List<QueryItem> listQueryItemByNameSQLQuery(String queryName, Map<String, Object> values, PagingInfo pagingInfo) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public long countByHQL(String hql, Map<String, Object> values) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public long countBySQL(String sql, Map<String, Object> values) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public long countByEntity(DataMarkEntity entity) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    protected Query createHQLQuery(String hql, Map<String, Object> params) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    protected Query createSQLQuery(String sql, Map<String, Object> params) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public void saveAll(Collection<DataMarkEntity> entities) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public void save(DataMarkEntity entity) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public void update(DataMarkEntity entity) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public void deleteByUuids(Collection<String> strings) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public void delete(String s) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public void delete(DataMarkEntity entity) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public void deleteByEntities(Collection<DataMarkEntity> entities) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public void flushSession() {
        super.flushSession();
    }

    @Override
    public List<DataMarkEntity> listByHQL(String hql, Map<String, Object> params) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public List<DataMarkEntity> listByHQLAndPage(String hql, Map<String, Object> params, PagingInfo page) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public List<DataMarkEntity> listBySQLAndPage(String sql, Map<String, Object> params, PagingInfo page) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public DataMarkEntity getOneByHQL(String hql, Map<String, Object> params) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public DataMarkEntity getOneBySQL(String sql, Map<String, Object> params) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public <NBR extends Number> NBR getNumberBySQL(String sql, Map<String, Object> params) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public <NBR extends Number> NBR getNumberByHQL(String hql, Map<String, Object> params) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public <NBR extends Number> List<NBR> listNumberBySQL(String sql, Map<String, Object> params) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public <NBR extends Number> List<NBR> listNumberByHQL(String hql, Map<String, Object> params) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public <CHAR> CHAR getCharSequenceBySQL(String sql, Map<String, Object> params) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public <CHAR> CHAR getCharSequenceByHQL(String hql, Map<String, Object> params) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public <CHAR> List<CHAR> listCharSequenceBySQL(String sql, Map<String, Object> params) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public <CHAR> List<CHAR> listCharSequenceByHQL(String hql, Map<String, Object> params) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public List<DataMarkEntity> listBySQL(String sql, Map<String, Object> params) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public List<DataMarkEntity> listByFieldEqValue(String field, Object value) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public List<DataMarkEntity> listByFieldNotEqValue(String field, Object value) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public List<DataMarkEntity> listByFieldNotInValues(String field, List<Object> value) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public List<DataMarkEntity> listByFieldInValues(String field, List<?> value) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public int updateByHQL(String hql, Map<String, Object> params) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public int deleteByHQL(String hql, Map<String, Object> params) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public int deleteByNamedSQL(String queryName, Map<String, Object> params) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public int deleteByNamedHQL(String queryName, Map<String, Object> params) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public int updateByNamedSQL(String queryName, Map<String, Object> params) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public int updateByNamedHQL(String queryName, Map<String, Object> params) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public int updateBySQL(String sql, Map<String, Object> params) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public int deleteBySQL(String sql, Map<String, Object> params) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public List<DataMarkEntity> listByEntity(DataMarkEntity entityInst, List<PropertyFilter> propertyFilters, String orderBy, PagingInfo pagingInfo) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public List<DataMarkEntity> listAllByOrderPage(PagingInfo pagingInfo, String orderBy) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    protected long countCriteriaResult(Criteria c) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public List<DataMarkEntity> listByEntity(DataMarkEntity entity) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    @Override
    public List<DataMarkEntity> listByEntityAndPage(DataMarkEntity entity, PagingInfo pagingInfo, String orderBy) {
        throw new UnsupportedOperationException("不支持该方法");
    }

    public void saveOrDeleteDataMarkRela(List<? extends DataMarkEntity> dataMarkEntityList, boolean isDeleteMark) {

        for (DataMarkEntity entity : dataMarkEntityList) {
            String entityName = ClassUtils.getShortName(entity.getClass());
            Query query = getSession().createQuery(" from " + entityName + " where dataUuid=:dataUuid and userId=:userId");
            Map<String, Object> param = Maps.newHashMap();
            param.put("dataUuid", entity.getDataUuid());
            param.put("userId", entity.getUserId());
            query.setProperties(param);
            List result = query.list();
            if (!isDeleteMark && CollectionUtils.isEmpty(result)) {
                UserDetails user = SpringSecurityUtils.getCurrentUser();
                Calendar now = Calendar.getInstance();
                entity.setUserId(user.getUserId());
                entity.setSystemUnitId(user.getSystemUnitId());
                entity.setCreateTime(now.getTime());
                entity.setCreator(user.getUserId());
                entity.setModifier(entity.getCreator());
                entity.setModifyTime(entity.getCreateTime());
                getSession().save(entity);
            } else if (isDeleteMark && !CollectionUtils.isEmpty(result)) {
                for (Object obj : result) {
                    getSession().delete(obj);
                }
            }
        }
    }

    public List<String> existMarkRelasByRelaEntities(List<DataMarkEntity> dataMarkEntityList) {
        List<String> dataUuids = Lists.newArrayList();
        for (DataMarkEntity entity : dataMarkEntityList) {
            String entityName = ClassUtils.getShortName(entity.getClass());
            Query query = getSession().createQuery(" select dataUuid from " + entityName + " where dataUuid=:dataUuid and userId=:userId");
            Map<String, Object> param = Maps.newHashMap();
            param.put("dataUuid", entity.getDataUuid());
            param.put("userId", entity.getUserId());
            query.setProperties(param);
            Object result = query.uniqueResult();
            if (result != null) {
                dataUuids.add(entity.getDataUuid());
            }

        }
        return dataUuids;
    }

    public List<QueryItem> listMarkStatusByTable(List<String> requestUuids, String tableName, String columnName) {
        StringBuilder sql = new StringBuilder(String.format("select %s as status ,uuid from %s  where uuid in (:uuids)", columnName, tableName));
        Query query = this.getSession().createSQLQuery(sql.toString());
        Map<String, Object> params = Maps.newHashMap();
        List<String> uuids = Lists.newArrayList();
        for (String u : requestUuids) {
            uuids.add(u);
        }
        params.put("uuids", uuids);
        query.setProperties(params);
        query.setResultTransformer(QueryItemResultTransformer.INSTANCE);
        List<QueryItem> queryItemList = query.list();
        if (!CollectionUtils.isEmpty(queryItemList)) {
            for (QueryItem queryItem : queryItemList) {
                Object status = queryItem.get("status");
                if (status == null || StringUtils.isBlank(status.toString())) {
                    queryItem.put("status", 0);
                }
            }
        }
        return queryItemList;
    }

    public void saveOrDeleteDataMarkRela(List<String> dataUuids, Class<? extends DataMarkEntity> markClass, boolean isDeleteMark) throws Exception {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        for (String dataUuid : dataUuids) {
            String entityName = ClassUtils.getShortName(markClass);
            Query query = getSession().createQuery(" from " + entityName + " where dataUuid=:dataUuid and userId=:userId");
            Map<String, Object> param = Maps.newHashMap();
            param.put("dataUuid", dataUuid);
            param.put("userId", user.getUserId());
            query.setProperties(param);
            List result = query.list();
            if (!isDeleteMark && CollectionUtils.isEmpty(result)) {
                DataMarkEntity dataMarkEntity = (DataMarkEntity) markClass.newInstance();
                Calendar now = Calendar.getInstance();
                dataMarkEntity.setUserId(user.getUserId());
                dataMarkEntity.setSystemUnitId(user.getSystemUnitId());
                dataMarkEntity.setCreateTime(now.getTime());
                dataMarkEntity.setCreator(user.getUserId());
                dataMarkEntity.setModifier(dataMarkEntity.getCreator());
                dataMarkEntity.setModifyTime(dataMarkEntity.getCreateTime());
                getSession().save(dataMarkEntity);
            } else if (isDeleteMark && !CollectionUtils.isEmpty(result)) {
                for (Object obj : result) {
                    getSession().delete(obj);
                }
            }
        }
    }

    public List<? extends DataMarkEntity> listByDataUuidAndClass(String dataUuid,
                                                                 Class<? extends DataMarkEntity> markClass,
                                                                 PagingInfo page) {

        Query query = getSession().createQuery(
                " from " + ClassUtils.getShortName(
                        markClass) + " where dataUuid=:dataUuid");
        Map<String, Object> param = Maps.newHashMap();
        param.put("dataUuid", dataUuid);
        if (page != null) {
            query.setFirstResult(page.getFirst());
            query.setMaxResults(page.getPageSize());
        }
        query.setProperties(param);
        return query.list();

    }
}
