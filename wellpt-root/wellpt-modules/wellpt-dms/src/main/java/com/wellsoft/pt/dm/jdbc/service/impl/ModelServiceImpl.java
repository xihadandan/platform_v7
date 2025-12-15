package com.wellsoft.pt.dm.jdbc.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.SnowFlake;
import com.wellsoft.pt.dm.enums.DataUniqueType;
import com.wellsoft.pt.dm.factory.ddl.Table;
import com.wellsoft.pt.dm.jdbc.Model;
import com.wellsoft.pt.dm.jdbc.service.ModelService;
import com.wellsoft.pt.jpa.hibernate4.NamedQueryScriptLoader;
import com.wellsoft.pt.jpa.support.QueryItemResultTransformer;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.AliasedTupleSubsetResultTransformer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.sql.rowset.serial.SerialClob;
import java.math.BigDecimal;
import java.sql.Clob;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年05月15日   chenq	 Create
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class ModelServiceImpl implements ModelService {

    @Resource(name = "sessionFactory")
    protected SessionFactory sessionFactory;


    @Override
    @Transactional
    public Long saveOrUpdate(Model model) {
        Long uuid = model.getUuid();
        if (uuid != null && getModel(model.getTable(), uuid) != null) {
            if (model.getProps().isEmpty()) {
                return uuid;
            }
            StringBuilder sql = new StringBuilder();
            sql.append("UPDATE").append(" ").append(model.getTable().toUpperCase()).append(" SET ");
            model.setModifier(SpringSecurityUtils.getCurrentUserId());
            model.setModifyTime(new Date());
            List<Model.Prop> props = model.getProps();
            List<String> update = Lists.newArrayList("REC_VER = REC_VER + 1");
            for (Model.Prop p : props) {
                if (p.getValue() == null) {
                    update.add(p.getCode().toUpperCase() + " IS NULL ");
                } else {
                    update.add(p.getCode().toUpperCase() + " =:" + p.getCode());
                }
            }
            sql.append(StringUtils.join(update, ","));
            sql.append(" WHERE UUID=:UUID");
            if (model.getRecVer() != null) {
                sql.append(" AND REC_VER =:REC_VER");
            }
            SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            for (Model.Prop p : props) {
                if (p.getValue() != null) {
                    sqlQuery.setParameter(p.getCode(), p.getTransformedValue());
                }
            }
            sqlQuery.setParameter("UUID", model.getUuid());
            if (model.getRecVer() != null) {
                sqlQuery.setParameter("REC_VER", model.getRecVer());
            }
            sqlQuery.executeUpdate();

        } else {
            if (StringUtils.isBlank(model.getTenant())) {
                model.setTenant(SpringSecurityUtils.getCurrentTenantId());
            }
            if (StringUtils.isBlank(model.getCreator())) {
                model.setCreator(SpringSecurityUtils.getCurrentUserId());
            }
            if (model.getCreateTime() == null) {
                model.setCreateTime(new Date());
            }
            if (model.getModifyTime() == null) {
                model.setModifyTime(new Date());
            }
            if (StringUtils.isBlank(model.getModifier())) {
                model.setModifier(model.getCreator());
            }
            if (model.getUuid() == null) {
                model.setUuid(SnowFlake.getId());
            }
            if (model.getRecVer() == null) {
                model.setRecVer(1);
            }
            List<String> propNames = model.propNames();
            List<Object> propValues = model.propValues();
            for (int i = 0; i < propValues.size(); i++) {
                if (propValues.get(i) == null) {
                    propNames.remove(i);
                    propValues.remove(i--);
                }
            }


            SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(
                    "INSERT INTO " + model.getTable().toUpperCase() +
                            " ( " + StringUtils.join(propNames, ",") + " ) VALUES" +
                            " ( :" + StringUtils.join(propNames, ",:") + " )");

            for (int i = 0, len = propNames.size(); i < len; i++) {
                sqlQuery.setParameter(propNames.get(i), propValues.get(i));
            }
            sqlQuery.executeUpdate();
        }
        return model.getUuid();
    }

    @Override
    @Transactional
    public void delete(String table, Long uuid) {
        SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery("DELETE FROM " + table.toUpperCase() + " WHERE UUID=:UUID");
        sqlQuery.setParameter("UUID", uuid);
        sqlQuery.executeUpdate();
    }


    @Transactional
    public int updateBySQL(String sql, Map<String, Object> params) {
        return createSQLQuery(sql, params).executeUpdate();
    }

    public <NBR extends Number> NBR getNumberBySQL(String sql, Map<String, Object> params, Class<NBR> nbrClass) {
        Query query = createSQLQuery(sql, params);
        try {
            List list = query.list();
            if (CollectionUtils.isNotEmpty(list)) {
                Object obj = list.get(0);
                return obj != null ? org.springframework.util.NumberUtils.parseNumber(obj.toString(), nbrClass) : null;
            }
        } catch (Exception e) {
        }
        return null;
    }


    private Query createSQLQuery(String sql, final Map<String, Object> params) {
        Assert.hasText(sql, "sql不能为空");
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
        if (MapUtils.isNotEmpty(params)) {
            query.setProperties(params);
        }
        return query;
    }

    @Override
    @Transactional
    public void delete(String table, String where, Map<String, Object> parameter) {
        SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery("DELETE FROM " + table.toUpperCase() + " WHERE " + where);
        setParameter(sqlQuery, parameter);
        sqlQuery.executeUpdate();
    }

    @Override
    @Transactional
    public void delete(String table, List<Long> uuids) {
        SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery("DELETE FROM " + table.toUpperCase() + " WHERE UUID IN :UUID");
        sqlQuery.setParameterList("UUID", uuids);
        sqlQuery.executeUpdate();
    }

    @Override
    public Model getModel(String table, Long uuid) {
        SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery("SELECT * FROM " + table.toUpperCase() + " WHERE UUID=:UUID");
        sqlQuery.setParameter("UUID", uuid);
        sqlQuery.setResultTransformer(new ModelPropResultTransformer());
        List<Model> list = sqlQuery.list();
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    @Override
    public List<Table.Column> getTableColumns(String table) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("tableName", table);
        SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(NamedQueryScriptLoader.generateDynamicNamedQueryString(sessionFactory, "queryTableColumnMetadata", params));
        sqlQuery.setResultTransformer(QueryItemResultTransformer.INSTANCE);
        sqlQuery.setParameter("tableName", table);
        List<QueryItem> columns = sqlQuery.list();
        List<Table.Column> columnList = Lists.newArrayList();
        for (QueryItem column : columns) {
            columnList.add(new Table.Column(column.getString("columnName").toUpperCase(),
                    column.getString("dataType"), column.getString("comments"), true));
        }
        return columnList;
    }

    public boolean tableExist(String table) {
        try {
            SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery("select 1 from " + table.toUpperCase());
            sqlQuery.list();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean tableNotNull(String table) {
        SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery("select 1 from " + table.toUpperCase());
        return sqlQuery.list().size() != 0;
    }

    public boolean checkUnique(Model model, DataUniqueType uniqueType) {
        List<Model.Prop> props = model.getProps();
        StringBuilder sql = new StringBuilder("SELECT 1 FROM ").append(model.getTable().toUpperCase()).append(" WHERE ");
        List<String> conditions = Lists.newArrayList();
        if (model.getUuid() != null) {
            conditions.add("UUID <> :UUID");
            model.getProps().add(new Model.Prop("UUID", model.getUuid()));
        }
        if (uniqueType.equals(DataUniqueType.TENANT)) {
            // 租户唯一性判断
            props.add(new Model.Prop("TENANT", SpringSecurityUtils.getCurrentTenantId()));
        }
        for (Model.Prop prop : props) {
            if (prop.getValue() != null && !prop.getCode().equalsIgnoreCase("UUID")) {
                conditions.add(prop.getCode().toUpperCase() + "=:" + prop.getCode().toUpperCase());
            }
        }
        sql.append(StringUtils.join(conditions, " AND "));
        SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
        for (Model.Prop prop : props) {
            sqlQuery.setParameter(prop.getCode(), prop.getValue());
        }

        return sqlQuery.list().isEmpty();
    }

    @Override
    public List<Map<String, Object>> list(String table, PagingInfo page) {
        return this.list(table, page, null);
    }

    @Override
    public List<Map<String, Object>> list(String table, PagingInfo page, String order) {
        SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery("SELECT * FROM " + table.toUpperCase() + (StringUtils.isNotBlank(order) ? " ORDER BY " + order : ""));
        // 分页
        if (page != null) {
            if (page.isAutoCount()) {
                SQLQuery countQuery = sessionFactory.getCurrentSession().createSQLQuery("SELECT COUNT(UUID) AS TOTAL FROM " + table.toUpperCase());
                Number num = (Number) countQuery.list().get(0);
                page.setTotalCount(num.longValue());
            }
            sqlQuery.setFirstResult(page.getFirst());
            sqlQuery.setMaxResults(page.getPageSize());
        }
        sqlQuery.setResultTransformer(new MapResultTransformer());
        return sqlQuery.list();
    }

    private void setParameter(SQLQuery sqlQuery, Map<String, Object> parameter) {
        if (MapUtils.isNotEmpty(parameter)) {
            Set<String> keys = parameter.keySet();
            for (String k : keys) {
                Object value = parameter.get(k);
                if (value != null) {
                    if (value instanceof Collection && CollectionUtils.isNotEmpty((Collection) value)) {
                        if (((Collection) value).iterator().next().getClass().isEnum()) {
                            List<Integer> ordinals = Lists.newArrayListWithCapacity(((Collection) value).size());
                            Iterator<Object> iterator = ((Collection) value).iterator();
                            while (iterator.hasNext()) {
                                Enum enumObj = (Enum) iterator.next();
                                ordinals.add(enumObj.ordinal());
                            }
                            sqlQuery.setParameterList(k, ordinals);

                        } else {
                            sqlQuery.setParameterList(k, (Collection) value);
                        }
                    } else if (value.getClass().isArray()) {
                        sqlQuery.setParameterList(k, (Object[]) value);
                    } else {
                        sqlQuery.setParameter(k, value);
                    }
                }
            }
        }
    }

    @Override
    public List<Map<String, Object>> list(String table, String where, Map<String, Object> parameter) {
        SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery("SELECT * FROM " + table.toUpperCase() + " WHERE " + where);
        this.setParameter(sqlQuery, parameter);
        sqlQuery.setResultTransformer(new MapResultTransformer());
        return sqlQuery.list();
    }

    @Override
    @Transactional
    public void drop(String table, boolean safe) {
        boolean delete = this.tableExist(table);
        if (delete && safe) {
            // 判断是否存在数据
            if (this.tableNotNull(table)) {
                throw new BusinessException("存在数据, 无法删除表");
            }
        }
        if (delete) {
            SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery("DROP TABLE " + table.toUpperCase());
            sqlQuery.executeUpdate();
        }
    }

    @Override
    public List<Long> queryUuidsByFields(String table, Map<String, Object> fieldValue, String condition) {
        SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery("SELECT UUID FROM " + table.toUpperCase() + " WHERE " + condition);
        this.setParameter(sqlQuery, fieldValue);
        sqlQuery.setResultTransformer(new MapResultTransformer());
        return sqlQuery.list();
    }

    public static class MapResultTransformer extends AliasedTupleSubsetResultTransformer {
        private boolean aliasUpper = true;

        @Override
        public Object transformTuple(Object[] tuple, String[] aliases) {
            Map result = new HashMap(tuple.length);
            for (int i = 0; i < tuple.length; i++) {
                String alias = aliases[i];
                if (alias != null) {
                    Object val = tuple[i];
                    if (tuple[i] instanceof BigDecimal) {
//                        int precision = ((BigDecimal) tuple[i]).precision() - ((BigDecimal) tuple[i]).scale();
//                        if (precision > 16) {
//                            val = val.toString(); // 超过长度的要转字符串
//                        }
                    } else if (tuple[i] instanceof Clob) {
                        try {
                            SerialClob clob = new SerialClob((Clob) tuple[i]);
                            val = IOUtils.toString(clob.getCharacterStream());
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    result.put(this.aliasUpper ? alias.toUpperCase() : alias.toLowerCase(), val);
                }
            }
            return result;
        }

        @Override
        public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
            return false;
        }
    }

    public static class ModelPropResultTransformer extends AliasedTupleSubsetResultTransformer {

        @Override
        public Object transformTuple(Object[] tuple, String[] aliases) {
            Model model = new Model();
            for (int i = 0, len = aliases.length; i < len; i++) {
                if (tuple[i] != null) {
                    if (aliases[i].equalsIgnoreCase("UUID")) {
                        model.setUuid(Long.parseLong(tuple[i].toString()));
                    } else if (aliases[i].equalsIgnoreCase("CREATOR")) {
                        model.setCreator(tuple[i].toString());
                    } else if (aliases[i].equalsIgnoreCase("CREATE_TIME")) {
                        model.setCreateTime((Date) tuple[i]);
                    } else if (aliases[i].equalsIgnoreCase("MODIFIER")) {
                        model.setModifier(tuple[i].toString());
                    } else if (aliases[i].equalsIgnoreCase("MODIFY_TIME")) {
                        model.setModifyTime((Date) tuple[i]);
                    } else if (aliases[i].equalsIgnoreCase("TENANT")) {
                        model.setTenant(tuple[i].toString());
                    } else if (aliases[i].equalsIgnoreCase("SYSTEM")) {
                        model.setSystem(tuple[i].toString());
                    } else if (aliases[i].equalsIgnoreCase("REC_VER")) {
                        model.setRecVer(((BigDecimal) tuple[i]).intValue());
                    } else {
                        model.getProps().add(new Model.Prop(aliases[i], tuple[i]));
                    }
                }
            }
            return model;
        }


        @Override
        public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
            return false;
        }
    }
}
