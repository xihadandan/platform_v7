/*
 * @(#)2016年1月13日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.service;


import com.alibaba.druid.DbType;
import com.alibaba.druid.stat.TableStat;
import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.pt.app.entity.AppFunction;
import com.wellsoft.pt.app.service.AppDataDefinitionRefResourceService;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.jpa.datasource.DatabaseType;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.HqlUtils;
import com.wellsoft.pt.jpa.util.SqlUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.annotations.Cascade;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.sql.rowset.serial.SerialBlob;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Blob;
import java.sql.Clob;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年1月13日.1	zhulh		2016年1月13日		Create
 * </pre>
 * @date 2016年1月13日
 */
@Service
@Transactional(readOnly = true)
public abstract class AbstractIexportDataProvider<T extends JpaEntity<UUID>, UUID extends Serializable> extends BaseServiceImpl
        implements IexportDataProvider<T, UUID> {

    private static Map<Class, String> updateSqlMap = new HashMap<>();
    private static Map<Class, String> insertSqlMap = new HashMap<>();
    @Autowired
    protected IexportDataRecordSetService iexportDataMetaDataService;

    @Resource(name = "sharedGuavaPool")
    private ListeningExecutorService executorService;
    @Autowired
    private HibernateTransactionManager transactionManager;

    protected Class<UUID> uuidClass;
    protected Class<T> entityClass;

    protected ObjectMapper objectMapper = new ObjectMapper();


    public AbstractIexportDataProvider() {
        super();
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            Type[] params = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
            uuidClass = (Class<UUID>) params[1];
            entityClass = (Class<T>) params[0];
        }

        objectMapper = IExportJsonObjectMapperFactory.objectMapper();
    }


    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#storeData(com.wellsoft.pt.basicdata.iexport.acceptor.IexportData, boolean)
     */
    @Override
    @Transactional(readOnly = false)
    public void storeData(IexportData iexportData, boolean newVer) throws Exception {
        IexportDataRecordSet object = IexportDataResultSetUtils
                .inputStream2IexportDataResultSet(iexportData.getInputStream());
        iexportDataMetaDataService.save((IexportDataRecordSet) object);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getMetaData()
     */
    @Override
    public IexportMetaData getMetaData() {
        IexportMetaData iexportMetaData = new IexportMetaData();
        return iexportMetaData;
    }


    protected void addDataUuid(ProtoDataHql protoDataHql, UUID uuid) {
        this.addDataUuid(protoDataHql, uuid, "uuids");
    }

    protected void addDataUuid(ProtoDataHql protoDataHql, UUID uuid, String fields) {
        Set<UUID> uuids = (Set<UUID>) protoDataHql.getParams().get(fields);
        if (uuids == null) {
            uuids = new HashSet<>();
            protoDataHql.getParams().put(fields, uuids);
        }
        uuids.add(uuid);
    }

    protected ProtoDataHql getProtoDataHql(String entityName) {
        ProtoDataHql protoDataHql = new ProtoDataHql(getType(), entityName);
        protoDataHql.setGenerateHql(new GenerateHql() {
            @Override
            public void build(ProtoDataHql protoDataHql) {
                protoDataHql.getSbHql().append("from ").append(protoDataHql.getEntityName()).append(" where ");
                HqlUtils.appendSql("uuid", protoDataHql.getParams(), protoDataHql.getSbHql(),
                        (Set<Serializable>) protoDataHql.getParams().get("uuids"));
            }
        });
        return protoDataHql;
    }

    protected String getChildHqlKey(String childType) {
        String childHqlkey = getType() + Separator.UNDERLINE.getValue() + childType;
        return childHqlkey;
    }

    protected void putParentMap(Map<String, List<T>> map, T t, String key) {
        List<T> tList = map.get(key);
        if (tList == null) {
            tList = new ArrayList<>();
            map.put(key, tList);
        }
        tList.add(t);
    }

    protected void putAppFunctionParentMap(T t, Map<String, T> parentMap, Map<String, ProtoDataHql> hqlMap) {
        String start = getType() + Separator.UNDERLINE.getValue();
        String key = start + t.getUuid();
        parentMap.put(key, t);
        if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.AppFunctionParent))) {
            hqlMap.put(this.getChildHqlKey(IexportType.AppFunctionParent), this.getProtoDataHql("AppFunction"));
        }
        this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.AppFunctionParent)), t.getUuid());
    }

    @Override
    public Map<UUID, String> getTreeNameMap(Collection<T> list) {
        Map<UUID, String> map = new HashMap<>();
        for (T t : list) {
            map.put(t.getUuid(), this.getTreeName(t));
        }
        return map;
    }


    public T getEntity(Serializable uuid) {
        return this.dao.get(entityClass, castUUID(uuid));
    }

    @Override
    public List<T> getList(Collection<Serializable> uuids) {
        List<T> list = new ArrayList<>();
        List<UUID> jpaUuids = convertUuids(uuids);
        Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
        if (jpaUuids.size() > 0) {
            String entityName = entityClass.getSimpleName();
            if (jpaUuids.size() <= 1000) {
                list = this.dao.get(entityClass, "uuid", jpaUuids);
            } else {
                Map<String, Object> values = new HashMap<>();
                StringBuilder hql = new StringBuilder("from ");
                hql.append(entityName).append(" where ");
                HqlUtils.appendSql("uuid", values, hql, new HashSet<Serializable>(jpaUuids));
                list = this.dao.find(hql.toString(), values, entityClass);
            }
        }
        return list;
    }

    protected List<UUID> convertUuids(Collection<Serializable> uuids) {
        List<UUID> list = Lists.newArrayList();
        uuids.forEach(uuid -> {
            list.add(castUUID(uuid));
        });
        return list;
    }

    @Override
    public Set<UUID> dataCheck(Collection<T> list, Map<String, ProtoDataBean> beanMap) {
        return null;
    }

    @Override
    public JoinTableProcessor<T, UUID> getJoinTableProcessor() {
        return null;
    }

    @Override
    public <P extends JpaEntity<UUID>, C extends JpaEntity<UUID>> BusinessProcessor<T> saveOrUpdate(
            Map<String, ProtoDataBeanTree<T, P, C>> map, Collection<Serializable> uuids) {
        List<T> oldList = this.getList(uuids);
        for (T old : oldList) {
            ProtoDataBeanTree<T, P, C> t = map.get(old.getUuid());
            // 版本号不一致 修改
            if (!old.getRecVer().equals(t.getProtoDataBean().getData().getRecVer())) {
                String sql = this.entityToUpdateSql(t.getProtoDataBean().getData());
                this.executeUpdateSql(sql, t);
            }
            map.remove(old.getUuid());
        }
        // 剩余的添加
        for (ProtoDataBeanTree<T, P, C> t : map.values()) {
            String sql = this.entityToInsertSql(t.getProtoDataBean().getData());
            this.executeUpdateSql(sql, t);
        }
        return null;
    }

    @Deprecated
    @Override
    public IexportData getData(UUID uuid) {
        return null;
    }

    protected <P extends JpaEntity, C extends JpaEntity> void executeUpdateSql(String sql,
                                                                               ProtoDataBeanTree<T, P, C> t) {
        Query query = this.dao.getSession().createSQLQuery(sql);
        query.setProperties(t.getProtoDataBean().getData());
        try {
            query.executeUpdate();
        } catch (Exception e) {
            if (e instanceof ConstraintViolationException) {
                // 违反了完整性约束（外键，主键或唯一键）
                ConstraintViolationException old = (ConstraintViolationException) e;
                String message = t.getProtoDataBean().getTreeName() + "：存在唯一键相同的资源，且当前系统不存在与这些资源UUID相同的资源，无法导入";
                ConstraintViolationException newException = new ConstraintViolationException(message, null,
                        old.getSQL(), old.getConstraintName());
                throw newException;
            }
            throw e;
        }
        JoinTableProcessor joinTableProcessor = this.getJoinTableProcessor();
        if (joinTableProcessor != null) {
            joinTableProcessor.joinTableSave(this.dao.getSession(), t);
        }
    }

    protected String entityToUpdateSql(T t) {
        if (updateSqlMap.containsKey(t.getClass())) {
            return updateSqlMap.get(t.getClass());
        }

        SessionFactoryImpl sessionFactory = (SessionFactoryImpl) this.dao.getSession().getSessionFactory();
        SingleTableEntityPersister entityPersister = (SingleTableEntityPersister) sessionFactory
                .getEntityPersister(t.getClass().getName());
        Map<String, String> map = this.getPropertyMap(entityPersister);
        StringBuilder columnSb = new StringBuilder();
        for (String key : map.keySet()) {
            String value = map.get(key);
            if (value.equals(entityPersister.getIdentifierPropertyName())) {
                continue;
            }
            columnSb.append(key).append("=:").append(value).append(",");
        }
        columnSb.deleteCharAt(columnSb.length() - 1);
        StringBuilder hql = new StringBuilder("update ");
        hql.append(entityPersister.getTableName()).append(" set ");
        hql.append(columnSb);
        hql.append(" where ").append(map.get(entityPersister.getIdentifierPropertyName())).append("=:")
                .append(entityPersister.getIdentifierPropertyName());
        updateSqlMap.put(t.getClass(), hql.toString());
        return hql.toString();
    }

    protected String entityToInsertSql(T t) {
        if (insertSqlMap.containsKey(t.getClass())) {
            return insertSqlMap.get(t.getClass());
        }
        SessionFactoryImpl sessionFactory = (SessionFactoryImpl) this.dao.getSession().getSessionFactory();
        SingleTableEntityPersister entityPersister = (SingleTableEntityPersister) sessionFactory
                .getEntityPersister(t.getClass().getName());
        Map<String, String> map = this.getPropertyMap(entityPersister);
        StringBuilder columnSb = new StringBuilder();
        StringBuilder valueSb = new StringBuilder();
        for (String key : map.keySet()) {
            columnSb.append(key).append(",");
            valueSb.append(":").append(map.get(key)).append(",");
        }
        columnSb.deleteCharAt(columnSb.length() - 1);
        valueSb.deleteCharAt(valueSb.length() - 1);
        StringBuilder hql = new StringBuilder("insert into ");
        hql.append(entityPersister.getTableName()).append("(");
        hql.append(columnSb).append(") values (").append(valueSb).append(")");
        insertSqlMap.put(t.getClass(), hql.toString());
        return hql.toString();
    }

    private Map<String, String> getPropertyMap(SingleTableEntityPersister entityPersister) {
        String uuidName = entityPersister.getIdentifierPropertyName();
        Map<String, String> map = new HashMap<>();
        String[] strs = entityPersister.getSubclassPropertyColumnNames(uuidName);
        if (strs.length == 1) {
            map.put(strs[0], uuidName);
        } else if (strs.length > 1) {
            throw new RuntimeException(
                    entityPersister.getEntityName() + "." + uuidName + ":" + StringUtils.join(strs, ","));
        }
        String[] propertyNames = entityPersister.getPropertyNames();
        for (String propertyName : propertyNames) {
            String[] columnNames = entityPersister.getSubclassPropertyColumnNames(propertyName);
            if (columnNames.length == 1) {
                map.put(columnNames[0], propertyName);
            } else if (columnNames.length > 1) {
                throw new RuntimeException(entityPersister.getEntityName() + "." + propertyName + ":"
                        + StringUtils.join(columnNames, ","));
            }
        }
        return map;
    }

    @Override
    public Set<String> getFileIds(T t) {
        return null;
    }

    @Override
    @Deprecated
    public void putChildProtoDataHqlParams(T t, Map<String, T> parentMap, Map<String, ProtoDataHql> hqlMap) {

    }

    @Override
    @Deprecated
    public Map<String, List<T>> getParentMapList(ProtoDataHql protoDataHql) {
        if (StringUtils.isNotBlank(protoDataHql.getParentType())) {
            throw new RuntimeException(getType() + "====" + protoDataHql.getParentType());
        }
        return new HashMap<>();
    }

    @Override
    public TreeNode exportAsTreeNode(Serializable uuid) {
        if (uuid != null && ExportTreeContextHolder.add(uuid.toString())) {
            TreeNode node = treeNode(castUUID(uuid));
            if (node != null) {
                T entity = getEntity(uuid);
                if (entity != null && entity.getModifyTime() != null) {
                    // 返回修改时间戳
                    node.setVersion(entity.getModifyTime().getTime());
                }
            }
            return node;
        }
        return null;
    }


    protected UUID castUUID(Serializable uuid) {
        UUID uid = null;
        if (uuidClass.isAssignableFrom(String.class)) {
            uid = (UUID) uuid.toString();
        } else if (uuidClass.isAssignableFrom(Long.class)) {
            uid = (UUID) Long.valueOf(uuid.toString());
        }
        return uid;
    }


    public TreeNode treeNode(UUID uuid) {
        T entity = dao.get(entityClass, uuid);
        if (entity != null) {
            TreeNode node = new TreeNode();
            node.setType(getType());
            node.setId(uuid.toString());
            node.setName(getTreeName(entity));
            return node;
        }
        return null;
    }

    protected TreeNode exportTreeNodeByDataProvider(Serializable uuid, String exportType) {
        IexportDataProvider iexportDataProvider = IexportDataProviderFactory.getDataProvider(exportType);
        if (iexportDataProvider != null) {
            TreeNode child = iexportDataProvider.exportAsTreeNode(uuid);
            if (child != null) {
                return child;
            }
        }
        return null;
    }


    protected void exportTreeNodeByDataProvider(Serializable uuid, String exportType, TreeNode parent) {
        Assert.notNull(parent, "父级节点不为空");
        this.exportTreeNodeByDataProvider(uuid, exportType, parent, null);
    }

    protected void exportTreeNodeByDataProvider(Serializable uuid, String exportType, TreeNode parent, Function<TreeNode, TreeNode> nodeFunctionCallback) {
        IexportDataProvider iexportDataProvider = IexportDataProviderFactory.getDataProvider(exportType);
        if (iexportDataProvider != null) {
            if (uuid != null) {
                if (ExportTreeContextHolder.getEnableThread()) {
                    ExportTreeContextHolder.submitExportTask(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            try {
                                TreeNode node = IexportDataProviderFactory.getDataProvider(exportType).exportAsTreeNode(uuid);
                                parent.appendChild(node);
                                if (nodeFunctionCallback != null) {
                                    nodeFunctionCallback.apply(node);
                                }
                            } catch (Exception e) {
                                throw e;
                            }
                            return null;
                        }
                    });
                } else {
                    TreeNode node = iexportDataProvider.exportAsTreeNode(uuid);
                    parent.appendChild(node);
                    if (nodeFunctionCallback != null) {
                        nodeFunctionCallback.apply(node);
                    }
                }
            }


//            ListenableFuture<TreeNode> future = iexportDataProvider.exportAsTreeNodeFuture(uuid);
//            if (future != null) {
//                ExportTreeContextHolder.pushFuture(future);
//                Futures.addCallback(future, new FutureCallback<TreeNode>() {
//                    @Override
//                    public void onSuccess(TreeNode result) {
//                        parent.appendChild(result);
//                    }
//
//                    @Override
//                    public void onFailure(Throwable t) {
//                    }
//                }, MoreExecutors.directExecutor());
//                return future;
//            }
        }
    }


    @Autowired
    private AppDataDefinitionRefResourceService appDataDefinitionRefResourceService;

    public List<TreeNode> convertRefAppFunction2TreeNodes(String uuid, TreeNode node) {
        List<AppFunction> appFunctions = appDataDefinitionRefResourceService.getDataRefFunctions(uuid);
        List<TreeNode> nodes = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(appFunctions)) {
            for (AppFunction func : appFunctions) {
                if (func.getExportable() && StringUtils.isNotBlank(func.getExportType())) {
                    IexportDataProvider iexportDataProvider =
                            IexportDataProviderFactory.getDataProvider(func.getExportType());
                    if (iexportDataProvider != null) {
                        exportTreeNodeByDataProvider(func.getUuid(), func.getExportType(), node, new Function<TreeNode, TreeNode>() {
                            @javax.annotation.Nullable
                            @Override
                            public TreeNode apply(@javax.annotation.Nullable TreeNode treeNode) {
                                if (treeNode == null) {
                                    iexportDataProvider.exportAsTreeNodeByFunction(func);
                                }
                                return null;
                            }
                        });
//                        TreeNode child = iexportDataProvider.exportAsTreeNode(func.getUuid());
//                        if (child == null) { // 有些功能同步的UUID并非实体的UUID，则通过功能获取
//                            child = this.exportAsTreeNodeByFunction(func);
//                        }
//                        if (child != null) {
//                            nodes.add(child);
//                        }
                    }
                }
            }
        }
        return nodes;
    }

    public TreeNode exportAsTreeNodeByFunction(AppFunction function) {
        return null;
    }


    public IExportEntityStream exportEntityStream(String uuid) {
        return exportStream(castUUID(uuid));
    }

    public String entityJsonString(Serializable uuid) {
        T t = this.getEntity(uuid);
        if (t != null) {
            try {
                StringWriter writer = new StringWriter();
                objectMapper.writeValue(writer, t);
                return t.toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public String entityJsonString(T t) {
        if (t != null) {
            try {
                StringWriter writer = new StringWriter();
                objectMapper.writeValue(writer, t);
                return writer.toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    protected IExportEntityStream exportStream(UUID uuid) {
        T entity = dao.get(entityClass, uuid);
        StringWriter writer = new StringWriter();
        IExportEntityStream stream = null;
        try {
            objectMapper.writeValue(writer, entity);
            stream = new IExportEntityStream(this.getTreeName(entity),
                    new IExportEntityStream.Metadata(writer.toString(), getType(),
                            uuid.toString(), entity.getRecVer()));

            // 获取关联表数据
            List<IExportTable> childTables = this.childTableStream();
            if (CollectionUtils.isNotEmpty(childTables)) {
                BeanMap map = BeanMap.create(entity);
                Set<Map.Entry<Object, Object>> beanMapEntrySet = map.entrySet();
                Map<String, Object> params = Maps.newHashMap();
                for (Map.Entry<Object, Object> entry : beanMapEntrySet) {
                    if (entry.getValue() == null || entry.getValue() instanceof Clob || entry.getValue() instanceof Blob) {
                        continue;
                    }
                    params.put(entry.getKey().toString(), entry.getValue());
                }

                for (IExportTable table : childTables) {
                    IExportTableData tableData = new IExportTableData();
                    tableData.setLookupColumn(table.getDataLookupColumn());
                    List<HashMap> dataList = nativeDao.query(table.getExportSql(), params, HashMap.class, null);
                    if (CollectionUtils.isNotEmpty(dataList)) {
                        for (HashMap<String, Object> data : dataList) {
                            IExportTableData.Row row = new IExportTableData.Row();
                            Set<Map.Entry<String, Object>> entrySet = data.entrySet();
                            for (Map.Entry<String, Object> ent : entrySet) {
                                String dbType = null;
                                if (ent.getValue() != null && ent.getValue() instanceof Blob) {
                                    try {
                                        SerialBlob blob = new SerialBlob((Blob) ent.getValue());
                                        ent.setValue(IOUtils.toString(blob.getBinaryStream()));
                                        dbType = "blob";
                                    } catch (Exception ex) {
                                        throw new RuntimeException(ex);
                                    }
                                }
                                IExportTableData.Column column = new IExportTableData.Column(ent.getKey(),
                                        ent.getValue() != null ? objectMapper.writeValueAsString(ent.getValue()) : null,
                                        ent.getValue() != null ? ent.getValue().getClass().getCanonicalName() : null);
                                if (dbType != null) {
                                    column.setDbType(dbType);
                                }
                                row.getColumns().add(column);
                            }
                            tableData.getRows().add(row);
                        }
                        IExportEntityStream tableStream = new IExportEntityStream();
                        DbType dbType = DbType.oracle;
                        String databaseType = Config.getValue("database.type");
                        if (DatabaseType.MySQL5.getName().equalsIgnoreCase(databaseType)) {
                            dbType = DbType.mysql;
                        } else if (DatabaseType.DM.getName().equalsIgnoreCase(databaseType)) {
                            dbType = DbType.dm;
                        } else if (DatabaseType.KB.getName().equalsIgnoreCase(databaseType)) {
                            dbType = DbType.kingbase;
                        }
                        String tableName = table.getTable();
                        if (StringUtils.isBlank(tableName)) {
                            // 自动解析出表
                            List<TableStat.Column> columns = SqlUtils.getColumns(table.getExportSql(), dbType);
                            if (CollectionUtils.isNotEmpty(columns)) {
                                Iterator<TableStat.Column> columnIterator = columns.iterator();
                                while (columnIterator.hasNext()) {
                                    TableStat.Column col = columnIterator.next();
                                    if (col.getName().equals("*")) {
                                        tableName = col.getTable();
                                        break;
                                    }
                                }
                                if (StringUtils.isBlank(tableName)) {
                                    throw new RuntimeException("导出表数据异常, 未解析到导出表名");
                                }
                            }
                        }
                        tableStream.setMetadata(new IExportEntityStream.Metadata(objectMapper.writeValueAsString(tableData), "TABLE:" + tableName, null, null));
                        stream.getChildren().add(tableStream);
                    }

                }
            }

        } catch (Exception e) {
            logger.error("序列化实体 {} - {} 异常", entityClass.getCanonicalName() + ":" + uuid,
                    Throwables.getStackTraceAsString(e));
        }
        return stream;
    }


    protected List<IExportTable> childTableStream() {
        return null;
    }

    protected void beforeSaveEntityStream(T entity) {
    }

    protected void afterSaveEntityStream(T entity) {
    }

    public T entityFromJsonString(String json) {
        if (StringUtils.isNotBlank(json)) {
            try {
                return objectMapper.readValue(json, entityClass);
            } catch (Exception e) {
                logger.error("实体json转实体类异常: ", e);
            }
        }
        return null;
    }

    @Transactional
    public T saveEntityStream(IExportEntityStream stream) {
        String string = stream.getMetadata().getData();
        try {
            if (StringUtils.isNotBlank(string)) {
                T entity = objectMapper.readValue(string, entityClass);
                T persist = getEntity(entity.getUuid());
                if (persist != null) {
                    com.wellsoft.pt.jpa.util.BeanUtils.copyProperties(entity, persist, new String[]{JpaEntity.REC_VER, JpaEntity.UUID}
                            , new Class[]{Cascade.class});
                    entity = persist;
                }

                this.beforeSaveEntityStream(entity);
                if (persist != null) {
                    dao.save(entity);
                } else {
                    dao.getSession().save(entity);
                }

                List<IExportEntityStream> children = stream.getChildren();
                if (CollectionUtils.isNotEmpty(children)) {
                    for (IExportEntityStream child : children) {
                        if (child.getMetadata().getType().startsWith("com.wellsoft")) {
                            if (child.getMetadata().getData().startsWith("[") && child.getMetadata().getData().endsWith("]")) {
                                JavaType javaType =
                                        objectMapper.constructType(Class.forName(child.getMetadata().getType()));
                                TypeFactory typeFactory = objectMapper.getTypeFactory();
                                JavaType listType = typeFactory.constructParametricType(List.class, javaType);
                                List<JpaEntity> list = (List) objectMapper.readValue(child.getMetadata().getData(),
                                        listType);
                                List<JpaEntity> waitSave = Lists.newArrayList();
                                for (JpaEntity childEntity : list) {
                                    JpaEntity exist = dao.get(childEntity.getClass(), childEntity.getUuid());
                                    if (exist != null) {
                                        BeanUtils.copyProperties(childEntity, exist, new String[]{JpaEntity.REC_VER});
                                        waitSave.add(exist);
                                    } else {
                                        //不存在的，则 insert
                                        dao.getSession().save(childEntity);
                                    }
                                }
                                // 更新操作
                                dao.saveAll(waitSave);
                            } else {
                                JpaEntity obj = (JpaEntity) objectMapper.readValue(child.getMetadata().getData(),
                                        Class.forName(child.getMetadata().getType()));
                                JpaEntity exist = dao.get(obj.getClass(), obj.getUuid());
                                if (exist != null) {
                                    BeanUtils.copyProperties(obj, exist, new String[]{JpaEntity.REC_VER});
                                    obj = exist;
                                    dao.save(obj);
                                } else {
                                    dao.getSession().save(obj);
                                }
                            }

                        } else if (child.getMetadata().getType().startsWith("TABLE:")) {
                            // 表数据
                            String[] table = child.getMetadata().getType().split(":");
                            IExportTableData tableData = objectMapper.readValue(child.getMetadata().getData(),
                                    IExportTableData.class);
                            if (CollectionUtils.isNotEmpty(tableData.getRows())) {
                                for (IExportTableData.Row row : tableData.getRows()) {
                                    if (CollectionUtils.isEmpty(tableData.getLookupColumn())) {
                                        throw new RuntimeException("表数据定义无法定位唯一数据列");
                                    }
                                    List<String> columnFormulas = Lists.newArrayList();
                                    List<String> lookup = Lists.newArrayList();
                                    List<String> lookUpCodes = Lists.newArrayList();
                                    List<String> codes = Lists.newArrayList();
                                    Map<String, Object> parameters = Maps.newHashMap();
                                    List<String> lowerCaseLookupColumns =
                                            Lists.newArrayListWithCapacity(tableData.getLookupColumn().size());
                                    for (String c : tableData.getLookupColumn()) {
                                        lowerCaseLookupColumns.add(c.toLowerCase());
                                    }
                                    for (IExportTableData.Column col : row.getColumns()) {
                                        if (col.getData() == null) {
                                            columnFormulas.add(col.getCode() + " = null ");
                                            if (lowerCaseLookupColumns.contains(col.getCode().toLowerCase())) {
                                                lookup.add(col.getCode() + " is null");
                                            }
                                        } else {
                                            Object data = objectMapper.readValue(col.getData(),
                                                    Class.forName(col.getJavaType()));
                                            if ("blob".equals(col.getDbType())) {
                                                // 大字段需要转为byte
                                                parameters.put(col.getCode(),
                                                        IOUtils.toByteArray(new StringReader(data.toString())));
                                            } else {
                                                if (lowerCaseLookupColumns.contains(col.getCode().toLowerCase())) {
                                                    lookup.add(col.getCode() + "=:" + col.getCode());
                                                    lookUpCodes.add(col.getCode());
                                                }
                                                parameters.put(col.getCode(), data);
                                            }
                                            columnFormulas.add(col.getCode() + "=:" + col.getCode());
                                            codes.add((col.getCode()));
                                        }
                                    }
                                    SQLQuery sqlQuery = dao.getSession().createSQLQuery(new StringBuilder("select 1 " +
                                            "from ").append(table[1]).append(" where ").append(
                                            StringUtils.join(lookup, " and ")
                                    ).toString());
                                    if (MapUtils.isNotEmpty(parameters)) {
                                        for (String lc : lookUpCodes) {
                                            sqlQuery.setParameter(lc, parameters.get(lc));
                                        }
                                    }
                                    if (CollectionUtils.isNotEmpty(sqlQuery.list())) {
                                        // 更新
                                        sqlQuery =
                                                dao.getSession().createSQLQuery(new StringBuilder("update ").append(table[1]).append(" set ").append(StringUtils.join(columnFormulas, " , "))
                                                        .append(" where ").append(StringUtils.join(lookup, " and ")).toString());
                                        if (MapUtils.isNotEmpty(parameters)) {
                                            Set<Map.Entry<String, Object>> entrySet = parameters.entrySet();
                                            for (Map.Entry<String, Object> entry : entrySet) {
                                                sqlQuery.setParameter(entry.getKey(), entry.getValue());
                                            }
                                        }
                                        sqlQuery.executeUpdate();
                                    } else {
                                        sqlQuery =
                                                dao.getSession().createSQLQuery(new StringBuilder("insert into ").append(table[1]).append("(").append(StringUtils.join(codes, " , ")).append(" ) values (:")
                                                        .append(StringUtils.join(codes, ",:")).append(")").toString());
                                        if (MapUtils.isNotEmpty(parameters)) {
                                            Set<Map.Entry<String, Object>> entrySet = parameters.entrySet();
                                            for (Map.Entry<String, Object> entry : entrySet) {
                                                sqlQuery.setParameter(entry.getKey(), entry.getValue());
                                            }
                                        }
                                        sqlQuery.executeUpdate();
                                    }
                                }
                            }

                        } else {
                            IexportDataProvider dataProvider =
                                    IexportDataProviderFactory.getDataProvider(child.getMetadata().getType());
                            if (dataProvider != null) {
                                dataProvider.saveEntityStream(child);
                            }
                        }

                    }
                }

                this.afterSaveEntityStream(entity);
                return entity;
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompareStatus importEntityCompare(T entity) {
        return null;
    }
}
