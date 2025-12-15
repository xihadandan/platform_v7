package com.wellsoft.pt.dm.service.impl;

import cn.hutool.core.util.XmlUtil;
import com.google.common.base.Function;
import com.google.common.collect.*;
import com.google.gson.reflect.TypeToken;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreData;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreOrder;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreParams;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreRendererBean;
import com.wellsoft.pt.basicdata.datastore.facade.service.CdDataStoreService;
import com.wellsoft.pt.basicdata.datastore.support.Condition;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreColumn;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreConfiguration;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreType;
import com.wellsoft.pt.basicdata.datastore.support.export.DataStoreExport;
import com.wellsoft.pt.basicdata.datastore.support.export.DataStoreExportFactory;
import com.wellsoft.pt.basicdata.datastore.support.export.ExportColumn;
import com.wellsoft.pt.basicdata.datastore.support.export.ExportParams;
import com.wellsoft.pt.dm.controller.request.AclDataRequest;
import com.wellsoft.pt.dm.controller.request.ModelFormDataRequest;
import com.wellsoft.pt.dm.dao.impl.DataModelDaoImpl;
import com.wellsoft.pt.dm.dao.impl.DataModelDetailDaoImpl;
import com.wellsoft.pt.dm.dto.ColumnDto;
import com.wellsoft.pt.dm.dto.DataModelDto;
import com.wellsoft.pt.dm.dto.DmQueryInfo;
import com.wellsoft.pt.dm.dto.TreeDataModelDataRequestParam;
import com.wellsoft.pt.dm.entity.DataModelDetailEntity;
import com.wellsoft.pt.dm.entity.DataModelEntity;
import com.wellsoft.pt.dm.enums.AclPer;
import com.wellsoft.pt.dm.enums.DataUniqueType;
import com.wellsoft.pt.dm.enums.MarkType;
import com.wellsoft.pt.dm.factory.ddl.Table;
import com.wellsoft.pt.dm.hibernate.GenerateMapping;
import com.wellsoft.pt.dm.hibernate.HibernateConfiguration;
import com.wellsoft.pt.dm.hibernate.Property;
import com.wellsoft.pt.dm.hibernate.PropertyBuilder;
import com.wellsoft.pt.dm.jdbc.Model;
import com.wellsoft.pt.dm.jdbc.service.ModelService;
import com.wellsoft.pt.dm.jdbc.service.impl.ModelServiceImpl;
import com.wellsoft.pt.dm.service.DataModelService;
import com.wellsoft.pt.dm.sql.SqlQueryObj;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.criteria.DataType;
import com.wellsoft.pt.jpa.criterion.CriterionOperator;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Constraint;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.type.Type;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.DataSource;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLSyntaxErrorException;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年04月06日   chenq	 Create
 * </pre>
 */
@Service
public class DataModelServiceImpl extends AbstractJpaServiceImpl<DataModelEntity, DataModelDaoImpl, Long> implements DataModelService, InitializingBean {

    @Resource(name = "sessionFactory")
    protected SessionFactory sessionFactory;
    @Autowired
    DataModelDetailDaoImpl dataModelDetailDao;

    @Autowired
    ModelService modelService;
    @Autowired
    DyFormFacade dyFormFacade;

    Set<String> exposeEntitis = Sets.newHashSet();

    freemarker.template.Configuration freemarkerConfig;

    @Value("${multi.tenancy.tenant.username}")
    private String defaultTenantName;

    @Autowired
    private CdDataStoreService cdDataStoreService;

    @Autowired
    private DataStoreExportFactory dataStoreExportFactory;


    @Override
    @Transactional
    public Long saveDataModel(DataModelDto dto) {
        DataModelEntity entity = dto.getUuid() != null ? getOne(dto.getUuid()) : new DataModelEntity();
        String originalRemark = null;
        if (dto.getUuid() == null) {
            if (dto.getId().length() > 24) {// Oracle表名长度不超过30（留6为用于生成其关联的扩展表名）
                throw new BusinessException("ID长度超过限制: 24");
            }
            // uuid 根据 ID 生成唯一数字，避免表单同步生成的UUID与自创建的UUID不一致，导致导入导出生成不同的数据模型
            long uuid = Math.abs(new BigInteger(1, DigestUtils.getMd5Digest().digest(dto.getId().toLowerCase().getBytes())).longValue());
            entity = getOne(uuid);
            if (entity == null) {
                entity = new DataModelEntity();
                entity.setUuid(uuid);
            }
            BeanUtils.copyProperties(dto, entity, entity.BASE_FIELDS);
        } else {
            entity.setId(dto.getId());
            entity.setName(dto.getName());
            entity.setModule(dto.getModule());
            originalRemark = entity.getRemark();
            entity.setRemark(dto.getRemark());
        }
        dao.save(entity);

        // 保存表详情
        String originalCols = null;


        DataModelDetailEntity tableDetailEntity = dto.getUuid() != null ? dataModelDetailDao.getOneByFieldEq("dataModelUuid", entity.getUuid()) : new DataModelDetailEntity();

        // 保存视图
        if (DataModelEntity.Type.VIEW.equals(entity.getType())) {
            tableDetailEntity.setColumnJson(dto.getColumnJson());
            tableDetailEntity.setModelJson(dto.getModelJson());
            tableDetailEntity.setSql(dto.getSql());
            tableDetailEntity.setSqlParameter(dto.getSqlParameter());
            tableDetailEntity.setId(entity.getId());
            tableDetailEntity.setDataModelUuid(entity.getUuid());
            tableDetailEntity.setSqlObjJson(dto.getSqlObjJson());
            dataModelDetailDao.save(tableDetailEntity);
            return entity.getUuid();
        } else if (DataModelEntity.Type.TABLE.equals(entity.getType())) {
            if (dto.getUuid() != null) {
                originalCols = tableDetailEntity.getColumnJson();
            }
            tableDetailEntity.setColumnJson(dto.getColumnJson());
            tableDetailEntity.setRuleJson(dto.getRuleJson());
            tableDetailEntity.setIndexJson(dto.getIndexJson());
            tableDetailEntity.setId(entity.getId());
            tableDetailEntity.setDataModelUuid(entity.getUuid());
            if (dto.getUuid() == null) {
                tableDetailEntity.setUuid(entity.getUuid());
            }
            dataModelDetailDao.save(tableDetailEntity);
        }


        if (DataModelEntity.Type.TABLE.equals(entity.getType())) {
            this.updateDataModelTableConstruct(originalCols, dto.getColumnJson(), originalRemark, entity.getUuid(), dto.getCreateMainTable(),
                    dto.getCreateRlTable(), dto.getCreateVnTable());
        }
        return entity.getUuid();
    }


    private void createFormRlTable(DataModelEntity entity) {

        DataModelEntity relaEntity = new DataModelEntity(entity.getId() + "_RL", entity.getName() + "从表数据关系",
                DataModelEntity.Type.RELATION, null, entity.getModule());
        if (getById(relaEntity.getId()) == null) {
            relaEntity.setUuid(Math.abs(new BigInteger(1, DigestUtils.getMd5Digest().digest(("(RELATION)_" + relaEntity.getId()).toLowerCase().getBytes())).longValue()));
            dao.save(relaEntity);
            DataModelDetailEntity relaDetailEntity = new DataModelDetailEntity();
            relaDetailEntity.setDataModelUuid(relaEntity.getUuid());
            relaDetailEntity.setId(relaEntity.getId());
            relaDetailEntity.setUuid(relaEntity.getUuid());
            List<ColumnDto> relaColumnDtos = Lists.newArrayList(getBaseColumnDtos());
            relaColumnDtos.add(new ColumnDto("DATA_UUID", "数据UUID", "DATA_UUID", "varchar", 64, true));
            relaColumnDtos.add(new ColumnDto("MAINFORM_DATA_UUID", "主表数据UUID", "MAINFORM_DATA_UUID", "varchar", 64, false));
            relaColumnDtos.add(new ColumnDto("MAINFORM_FORM_UUID", "主表单UUID", "MAINFORM_FORM_UUID", "varchar", 64, false));
            relaColumnDtos.add(new ColumnDto("SORT_ORDER", "排序号", "SORT_ORDER", "number", 9, false));
            relaColumnDtos.add(new ColumnDto("PARENT_UUID", "父级数据UUID", "PARENT_UUID", "varchar", 64, false));
            relaDetailEntity.setColumnJson(JsonUtils.object2Gson(relaColumnDtos));

            this.dataModelDetailDao.save(relaDetailEntity);
        }

        GenerateMapping relamapping = new GenerateMapping("UF_" + relaEntity.getId().toUpperCase(), relaEntity.getName()
                , new GenerateMapping.Id("UUID", "long"));
        // 创建应用与表单业务的从表关系表
        if (!modelService.tableExist(relamapping.getTableName())) {
            relamapping.addProperty(this.getBaseProperties());
            relamapping.addProperty(new PropertyBuilder().name("DATA_UUID").type("string").length(64).toProperty());
            relamapping.addProperty(new PropertyBuilder().name("MAINFORM_DATA_UUID").type("string").length(64).toProperty());
            relamapping.addProperty(new PropertyBuilder().name("MAINFORM_FORM_UUID").type("string").length(64).toProperty());
            relamapping.addProperty(new PropertyBuilder().name("SORT_ORDER").type("integer").toProperty());
            relamapping.addProperty(new PropertyBuilder().name("PARENT_UUID").type("long").toProperty());
            try {
                createByMapping(relamapping);
            } catch (Exception e) {
                logger.error("创建模型关系数据表异常：", e);
                throw new BusinessException(e);
            }
        }
    }


    private List<ColumnDto> getBaseColumnDtos() {
        return Lists.newArrayList(new ColumnDto("UUID", "UUID", "UUID", "varchar", 64, true)
                , new ColumnDto("CREATOR", "创建人", "CREATOR", "varchar", 64, false)
                , new ColumnDto("CREATE_TIME", "创建时间", "CREATE_TIME", "timestamp", 6, false)
                , new ColumnDto("MODIFIER", "修改人", "MODIFIER", "varchar", 64, false)
                , new ColumnDto("MODIFY_TIME", "修改时间", "MODIFY_TIME", "timestamp", 6, false)
                , new ColumnDto("REC_VER", "修改时间", "REC_VER", "number", 12, false));
    }

    private List<ColumnDto> getTenantSysColumnDtos() {
        return Lists.newArrayList(new ColumnDto("TENANT", "归属租户", "TENANT", "varchar", 64, false)
                , new ColumnDto("SYSTEM", "归属系统", "SYSTEM", "varchar", 64, false));
    }

    private Property[] getTenantSysProperties() {
        return new Property[]{new PropertyBuilder().name("TENANT").type("string").length(64).comment("归属租户").toProperty(),
                new PropertyBuilder().name("SYSTEM").type("string").comment("归属系统").length(64).toProperty()};
    }

    private Property[] getBaseProperties() {
        return new Property[]{new PropertyBuilder().name("CREATOR").type("string").length(64).comment("创建人").toProperty(),
                new PropertyBuilder().name("CREATE_TIME").type("timestamp").comment("创建时间").toProperty(),
                new PropertyBuilder().name("MODIFIER").type("string").length(64).comment("修改人").toProperty(),
                new PropertyBuilder().name("MODIFY_TIME").type("timestamp").comment("修改时间").toProperty(),
                new PropertyBuilder().name("REC_VER").type("java.math.BigDecimal").precision(10).defaultValue("1").comment("版本号").toProperty()};
    }


    private void createByMapping(GenerateMapping mapping) throws Exception {
        com.wellsoft.pt.dm.hibernate.SchemaUpdate update = null;
        StringWriter sw = new StringWriter();
        try {
            this.freemarkerConfig.getTemplate("update.mapping.ftl").process(mapping, sw);
        } catch (Exception e) {
            logger.error("执行mapping freemarker输出异常", e);
            throw new BusinessException("执行mapping freemarker输出异常: " + e.getMessage());
        }
        String mappingStr = sw.toString();
        String mappingXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<!DOCTYPE hibernate-mapping PUBLIC \"-//Hibernate/Hibernate Mapping DTD 3.0//EN\"\n" +
                "        \"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd\">" + mappingStr;
        StandardServiceRegistry serviceRegistry = sessionFactory.getSessionFactoryOptions().getServiceRegistry();
        Configuration configuration = new HibernateConfiguration();
        logger.info("打印修改表结构mapping : {}", XmlUtil.toStr(XmlUtil.parseXml(mappingStr), true));
        configuration.addXML(mappingXML);
        configuration.setProperty("hibernate.default_schema", defaultTenantName);
        update = new com.wellsoft.pt.dm.hibernate.SchemaUpdate(serviceRegistry, configuration);
        update.execute(true, true);
        if (CollectionUtils.isNotEmpty(update.getExceptions())) {
            List<Exception> exceptions = update.getExceptions();
            StringBuilder sb = new StringBuilder();
            for (Exception ex : exceptions) {
                sb.append(ex.getMessage());
            }
            throw new SQLSyntaxErrorException(sb.toString());
        }

    }

    @Override
    public DataModelDto getDataModelDto(Long uuid) {
        DataModelEntity entity = getOne(uuid);
        return getDataModelDto(entity);
    }

    private DataModelDto getDataModelDto(DataModelEntity entity) {
        DataModelDto dto = new DataModelDto();
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
            DataModelDetailEntity tableDetailEntity = dataModelDetailDao.getOneByFieldEq("dataModelUuid", entity.getUuid());
            if (tableDetailEntity != null) {
                dto.setColumnJson(tableDetailEntity.getColumnJson());
                dto.setIndexJson(tableDetailEntity.getIndexJson());
                dto.setRuleJson(tableDetailEntity.getRuleJson());
                dto.setSql(tableDetailEntity.getSql());
                dto.setSqlParameter(tableDetailEntity.getSqlParameter());
                dto.setModelJson(tableDetailEntity.getModelJson());
                dto.setSqlObjJson(tableDetailEntity.getSqlObjJson());
            }

        }
        return dto;
    }

    @Override
    public DataModelDto getDataModelDto(String id) {
        DataModelEntity entity = getById(id);
        return getDataModelDto(entity);
    }

    @Override
    public DataModelEntity getById(String id) {
        return this.dao.getOneByFieldEq("id", id);
    }

    @Override
    public List<DataModelEntity> getByType(DataModelEntity.Type type, String module) {
        DataModelEntity example = new DataModelEntity();
        if (type != null) {
            example.setType(type);
        }
        if (StringUtils.isNotBlank(module)) {
            example.setModule(module);
        }
        return this.dao.listByEntity(example);
    }

    @Override
    public List<Map<String, Object>> queryViewDataById(String id, DmQueryInfo queryInfo) {
        DataModelDto model = this.getDataModelDto(id);
        if (model != null && StringUtils.isNotBlank(model.getSql())) {
            String sql = model.getSql();
            if (StringUtils.isNotBlank(model.getSqlObjJson())) {
                sql = SqlQueryObj.fromJsonString(model.getSqlObjJson()).toSqlString(queryInfo.getQueryParams(), true);
            }
            return this.queryViewDataBySql(sql, queryInfo.getQueryParams(), queryInfo.getPagingInfo());
        }
        return null;
    }

    public List<Table.Column> getTableColumns(String tableName) {
        return modelService.getTableColumns(tableName);
    }

    @Override
    public DataStoreData loadDataStoreData(Long uuid, DataStoreParams params) {
        DataStoreConfiguration configuration = this.convertDataModelAsDataStoreConfiguration(uuid, params);
        if (configuration == null) {
            return null;
        }
        if (params.getParam("LEFT_JOIN_TOP_MARK") != null) {// 关联置顶数据
            configuration.setType(DataStoreType.SQL.getType());
            configuration.setSqlStatement("select this_.* ,MK.TYPE AS MK_TYPE_0 , MK.MODIFY_TIME AS MK_MODIFY_TIME_0 from " + configuration.getTableName() + " this_\n" +
                    "LEFT JOIN " + configuration.getTableName() + "_DL MK ON MK.DATA_UUID = this_.UUID AND MK.TYPE=3 \n" +
                    " AND MK.CREATOR = '" + SpringSecurityUtils.getCurrentUserId() + "'");
            configuration.setDefaultOrder("MK_TYPE_0 ASC , MK_MODIFY_TIME_0 DESC");// 按置顶降序
            List<DataStoreOrder> orders = Lists.newArrayList(new DataStoreOrder("MK_TYPE_0", true), new DataStoreOrder("MK_MODIFY_TIME_0", false));
            orders.addAll(params.getOrders());
            params.setOrders(orders);
        }
        StringBuilder defaultCondition = new StringBuilder("");
        if (params.getParam("CURRENT_USER") != null) {
            defaultCondition.append("this_.CREATOR = '" + SpringSecurityUtils.getCurrentUserId() + "'");
        }
//        if (params.getParam("ACL") != null) {
//            // 解析访问权限
//            List<String> acls = (List<String>) params.getParam("ACL");
//            StringBuilder aclCondition = new StringBuilder("");
//            if (CollectionUtils.isNotEmpty(acls)) {
//                params.getParams().put("_now", new Date());
//                // 解析sid
//                List<String> sids = Lists.newArrayList(SpringSecurityUtils.getCurrentUserId());
//                if (params.getParam("ACL_ROLES") != null) {
//                    sids.addAll((List<String>) params.getParam("ACL_ROLES"));
//                }
//                params.getParams().put("_sids", sids);
//                aclCondition.append(" EXISTS ( SELECT 1 FROM ").append(configuration.getTableName()).append("_AC ac_")
//                        .append(" WHERE this_.UUID = ac_.DATA_UUID AND ac_.SID IN :_sids AND (ac_.EXPIRE_TIME IS NULL OR ac_.EXPIRE_TIME > :_now) )");
//            }
//
//            if (aclCondition.length() > 0) {
//                defaultCondition.append(defaultCondition.length() > 0 ? " AND " : "").append(aclCondition.toString());
//            }
//        }
        configuration.setDefaultCondition(defaultCondition.toString());
        return cdDataStoreService.loadData(params, configuration);
    }

    @Override
    public DataSource exportData(Long uuid, String type, ExportParams exportParams) {
        List<ExportColumn> list = exportParams.getColumns();
        // 需要将渲染器转成导出配置中对应的渲染器
        List<DataStoreRendererBean> rendererList = Lists.newArrayList();
        for (ExportColumn exportColumn : list) {
            if (exportColumn.getRenderer() != null && !exportColumn.getRenderer().isEmpty()) {
                DataStoreRendererBean b = new DataStoreRendererBean();
                b.setColumnIndex(exportColumn.getColumnIndex());
                b.setParam(exportColumn.getRenderer());
                rendererList.add(b);
            }
        }
        DataStoreParams p = exportParams.getParams();
        p.setRenderers(rendererList);
        DataStoreData dataStoreData = this.loadDataStoreData(uuid, p);
        if (MapUtils.isNotEmpty(exportParams.getExtras()) && exportParams.getExtras().containsKey("selectedBy")) {
            Map<String, List<String>> map = (Map<String, List<String>>) exportParams.getExtras().get("selectedBy");
            String dataIndex = map.keySet().iterator().next();
            Iterator<Map<String, Object>> iterator = dataStoreData.getData().iterator();
            while (iterator.hasNext()) {
                Map<String, Object> dataMap = iterator.next();
                if (dataMap.get(dataIndex) != null && !(map.get(dataIndex).contains(dataMap.get(dataIndex).toString()))) {
                    iterator.remove();
                }
            }
        }
        exportParams.setData(dataStoreData);
        DataStoreExport export = dataStoreExportFactory.get(type);
        if (StringUtils.isNotBlank(exportParams.getFileName()) && exportParams.getFileName().indexOf("${") != -1) {
            try {
                String fileName = TemplateEngineFactory.getDefaultTemplateEngine().process(exportParams.getFileName(), TemplateEngineFactory.getExplainRootModel());
                exportParams.setFileName(fileName);
            } catch (Exception e) {
                logger.error("格式化下载文件名异常", e);
            }
        }
        return export.export(exportParams);
    }

    @Override
    public List<TreeNode> loadTreeNodes(final TreeDataModelDataRequestParam reqParams) {
        List<TreeNode> nodeList = Lists.newArrayList();
        Long dataModelUuid = reqParams.getDataModelUuid();
        if (dataModelUuid == null && StringUtils.isNotBlank(reqParams.getDataModelId())) {
            DataModelEntity modelEntity = getById(reqParams.getDataModelId());
            if (modelEntity != null) {
                dataModelUuid = modelEntity.getUuid();
            }
        }
        if (!reqParams.getAsync()) {
            reqParams.setParentColumnValue(null);
        }
        DataStoreParams dataStoreParams = buildDataModelTreeDataQuery(reqParams);
        DataStoreConfiguration configuration = this.convertDataModelAsDataStoreConfiguration(dataModelUuid, dataStoreParams);
        if (!reqParams.getAsync()) {//非异步情况下，拉取全部数据，生成树形节点数据
            DataStoreData dataStoreData = cdDataStoreService.loadData(dataStoreParams, configuration);
            if (CollectionUtils.isNotEmpty(dataStoreData.getData())) {
                List<Map<String, Object>> dataList = dataStoreData.getData();
                // 将所有节点数据按上下级关系分组
                Multimap<String, Map<String, Object>> map = Multimaps.index(dataList,
                        new Function<Map<String, Object>, String>() {
                            @Nullable
                            @Override
                            public String apply(@Nullable Map<String, Object> map) {
                                Object parentValue = map.get(reqParams.getParentColumn());
                                if (parentValue != null) {
                                    return StringUtils.isNotBlank(
                                            parentValue.toString()) ? parentValue.toString() : "-1";
                                }
                                return "-1";
                            }
                        });
                Map<String, Collection<Map<String, Object>>> groupDataMap = map.asMap();
                if (groupDataMap.size() == 0) {
                    return nodeList;
                }
                Collection<Map<String, Object>> roots = groupDataMap.get("-1");
                if (CollectionUtils.isEmpty(roots)) {
                    return null;
                }
                Iterator<Map<String, Object>> topIterator = roots.iterator();
                while (topIterator.hasNext()) {
                    Map<String, Object> node = topIterator.next();
                    TreeNode treeNode = new TreeNode(Objects.toString(node.get(reqParams.getUniqueColumn())),
                            Objects.toString(node.get(reqParams.getDisplayColumn())), null);
                    treeNode.setNodeLevel(0);
                    if (reqParams.getNoCheckLevel().contains(treeNode.getNodeLevel())) {
                        treeNode.setNocheck(true);
                    }
                    treeNode.setData(node);
                    cascadeAddChildTreeNode(treeNode, groupDataMap, reqParams);
                    nodeList.add(treeNode);
                }
            }
        } else {
            DataStoreData dataStoreData = cdDataStoreService.loadData(dataStoreParams, configuration);
            if (CollectionUtils.isNotEmpty(dataStoreData.getData())) {
                Iterator<Map<String, Object>> topIterator = dataStoreData.getData().iterator();
                while (topIterator.hasNext()) {
                    Map<String, Object> node = topIterator.next();
                    TreeNode treeNode = new TreeNode(node.get(reqParams.getUniqueColumn()).toString(),
                            node.get(reqParams.getDisplayColumn()).toString(), null);
                    if (reqParams.getNoCheckLevel().contains(treeNode.getNodeLevel())) {
                        treeNode.setNocheck(true);
                    }
                    treeNode.setData(node);
                    nodeList.add(treeNode);
                }
            }

        }

        return nodeList;
    }

    private void cascadeAddChildTreeNode(TreeNode parentNode,
                                         Map<String, Collection<Map<String, Object>>> groupDataMap,
                                         TreeDataModelDataRequestParam reqParams) {
        Collection collection = groupDataMap.get(parentNode.getId());
        if (collection == null) {
            return;
        }
        Iterator<Map<String, Object>> childIterator = groupDataMap.get(
                parentNode.getId()).iterator();
        while (childIterator.hasNext()) {
            Map<String, Object> node = childIterator.next();
            TreeNode treeNode = new TreeNode(node.get(reqParams.getUniqueColumn()).toString(),
                    node.get(reqParams.getDisplayColumn()).toString(), null);
            treeNode.setNodeLevel(parentNode != null ? parentNode.getNodeLevel() + 1 : 0);
            if (reqParams.getNoCheckLevel().contains(treeNode.getNodeLevel())) {
                treeNode.setNocheck(true);
            }
            treeNode.setData(node);
            cascadeAddChildTreeNode(treeNode, groupDataMap, reqParams);
            parentNode.setIsParent(true);
            // parentNode.setNocheck(true);
            parentNode.getChildren().add(treeNode);
        }

    }

    private DataStoreParams buildDataModelTreeDataQuery(TreeDataModelDataRequestParam param) {
        DataStoreParams dsParams = new DataStoreParams();
        dsParams.getParams().putAll(param.getParams());
        dsParams.setPagingInfo(null);
        if (param.getAsync() && StringUtils.isNotBlank(param.getParentColumn())) {//父级查询条件
            //查询指定父级值的节点
            Condition parentEqCondition = new Condition(param.getParentColumn(),
                    param.getParentColumnValue(),
                    CriterionOperator.eq);
            if (StringUtils.isBlank(param.getParentColumnValue())) {
                parentEqCondition = new Condition();
                parentEqCondition.setType(CriterionOperator.or.getType());
                Condition isNullCondition = new Condition(param.getParentColumn(), null, CriterionOperator.ISNULL);
                Condition isEmptyCondition = new Condition(param.getParentColumn(), StringUtils.EMPTY,
                        CriterionOperator.eq);
                parentEqCondition.setConditions(Lists.newArrayList(isNullCondition, isEmptyCondition));
            }
            dsParams.getCriterions().add(parentEqCondition);
        }
        if (StringUtils.isNotBlank(param.getDefaultCondition())) {//默认查询条件
            Condition sqlConditon = new Condition();
            sqlConditon.setSql(param.getDefaultCondition());
            dsParams.getCriterions().add(sqlConditon);
        }

        return dsParams;
    }

    private DataStoreConfiguration convertDataModelAsDataStoreConfiguration(Long uuid, DataStoreParams params) {
        DataModelDto modelDto = getDataModelDto(uuid);
        if (modelDto == null) {
            return null;
        }

        DataStoreConfiguration configuration = new DataStoreConfiguration();
        if (modelDto.getType().equals(DataModelEntity.Type.TABLE)) {
            configuration.setType(DataStoreType.TABLE.getType());
            configuration.setTableName("UF_" + modelDto.getId());
        } else if (modelDto.getType().equals(DataModelEntity.Type.VIEW)) {
            configuration.setType(DataStoreType.SQL.getType());
//            Map<String, Object> criterionParams = Maps.newHashMap();
//            if (CollectionUtils.isNotEmpty(params.getCriterions())) {
//                for (Condition con : params.getCriterions()) {
//                    if (StringUtils.isBlank(con.getSql()) && StringUtils.isNotBlank(con.getColumnIndex())
//                            && !params.getParams().containsKey(con.getColumnIndex()) && con.getValue() != null) {
//                        if (con.getValue() instanceof Array) {
//                            List list = Arrays.asList(con.getValue());
//                            criterionParams.put(con.getColumnIndex(), list.get(0));
//                            criterionParams.put(con.getColumnIndex() + "_2", list.get(1));
//                        } else {
//                            if (CriterionOperator.like.getType().equalsIgnoreCase(con.getType())) {
//                                criterionParams.put(con.getColumnIndex(), "%" + con.getValue() + "%");
//                            } else {
//                                criterionParams.put(con.getColumnIndex(), con.getValue());
//                            }
//                        }
//                    }
//                }
//                if (!criterionParams.isEmpty()) {
//                    params.getParams().putAll(criterionParams);
//                }
//            }

            if (StringUtils.isNotBlank(modelDto.getSqlObjJson())) {
                Map<String, Object> defaultParams = TemplateEngineFactory.getExplainRootModel();
                Map<String, Object> queryParams = params.getParams();
                Map<String, Object> filterParams = Maps.newHashMap();
                Set<String> keys = queryParams.keySet();
                for (String k : keys) {
                    if (queryParams.get(k) == null || StringUtils.isEmpty(queryParams.get(k).toString())) {
                        continue;
                    }
                    filterParams.put(k, queryParams.get(k));
                }
                filterParams.putAll(defaultParams);
                configuration.setSqlStatement(JsonUtils.gson2Object(modelDto.getSqlObjJson(), SqlQueryObj.class).toSqlString(filterParams, true));
            } else {
                configuration.setSqlStatement(modelDto.getSql());
            }
        }
        configuration.setName(modelDto.getName());
        List<ColumnDto> colList = JsonUtils.gson2List(modelDto.getColumnJson(), new TypeToken<List<ColumnDto>>() {
        }.getType());
        Map<String, DataStoreColumn> dsColMap = Maps.newHashMap();
        for (ColumnDto dto : colList) {
            DataStoreColumn column = new DataStoreColumn();
            column.setColumnIndex(dto.getAlias() != null ? dto.getAlias() : dto.getColumn());
            column.setColumnName(dto.getAlias() != null ? dto.getAlias() : dto.getColumn());
            column.setColumnType(dto.getDataType());
            column.setDataType(DataType.S.getType());
            if (dto.getDataType().indexOf("number") != -1) {
                if (dto.getScale() != null && dto.getScale() > 0) {
                    column.setDataType(DataType.D.getType());
                } else if (dto.getLength() != null && dto.getLength() > 9) {
                    column.setDataType(DataType.L.getType());
                }
            } else if (dto.getDataType().indexOf("timestamp") != -1) {
                column.setDataType(DataType.T.getType());
            }
            column.setTitle(dto.getTitle());
            dsColMap.put(column.getColumnIndex(), column);
        }
        configuration.setColumnMap(dsColMap);
        return configuration;
    }

    @Override
    public long loadDataStoreDataCount(Long uuid, DataStoreParams params) {
        DataStoreConfiguration configuration = this.convertDataModelAsDataStoreConfiguration(uuid, params);
        if (configuration == null) {
            return 0L;
        }
        if (params.getParam("CURRENT_USER") != null) {
            configuration.setDefaultCondition("this_.CREATOR = '" + SpringSecurityUtils.getCurrentUserId() + "'");
        }
        return cdDataStoreService.loadCount(params, configuration);
    }

    @Override
    @Transactional
    public void updateDataRelaMarker(Long uuid, List<Long> dataUuids, MarkType type) {
        DataModelEntity dataModelEntity = getOne(uuid);
        if (dataModelEntity != null) {
            this.deleteDataRelaMarker(uuid, dataUuids, Lists.newArrayList(type));
            for (Long dataUuid : dataUuids) {
                Model model = new Model("UF_" + dataModelEntity.getId() + "_DL");
                model.getProps().add(new Model.Prop("DATA_UUID", dataUuid));
                model.getProps().add(new Model.Prop("TYPE", type.ordinal()));
                modelService.saveOrUpdate(model);
            }
        }

    }

    @Override
    @Transactional
    public void deleteDataRelaMarker(Long uuid, List<Long> dataUuids, List<MarkType> type) {
        DataModelEntity dataModelEntity = getOne(uuid);
        if (dataModelEntity != null) {
            Map<String, Object> parameter = Maps.newHashMap();
            parameter.put("dataUuids", dataUuids);
            parameter.put("type", type);
            parameter.put("userId", SpringSecurityUtils.getCurrentUserId());
            modelService.delete("UF_" + dataModelEntity.getId() + "_DL", " DATA_UUID IN :dataUuids AND TYPE IN :type AND CREATOR  = :userId ", parameter);
        }
    }

    @Override
    public List<Map<String, Object>> getDataRelaMarker(Long uuid, List<Long> dataUuids, List<MarkType> type) {
        DataModelEntity dataModelEntity = getOne(uuid);
        if (dataModelEntity != null) {
            Map<String, Object> parameter = Maps.newHashMap();
            parameter.put("dataUuids", dataUuids);
            List<Integer> types = Lists.newArrayListWithCapacity(type.size());
            for (MarkType t : type) {
                types.add(t.ordinal());
            }
            parameter.put("type", types);
            parameter.put("userId", SpringSecurityUtils.getCurrentUserId());
            return modelService.list("UF_" + dataModelEntity.getId() + "_DL", " DATA_UUID IN (:dataUuids) AND TYPE IN (:type) AND CREATOR = :userId ", parameter);
        }
        return null;
    }

    @Override
    @Transactional
    public void updateDataRelaData(Long uuid, List<Long> dataUuids, List<Long> relaDataUuids,
                                   MarkType type, String relaId, Boolean override) {
        DataModelEntity modelEntity = getOne(uuid);
        if (modelEntity != null) {
            if (override || MarkType.ONE_TO_ONE.equals(type)) {
                // 删除关系
                Map<String, Object> parameter = Maps.newHashMap();
                parameter.put("dataUuids", dataUuids);
                parameter.put("type", type.ordinal());
                parameter.put("relaId", relaId);
                modelService.delete("UF_" + modelEntity.getId() + "_DL",
                        " DATA_UUID IN :dataUuids AND TYPE = :type AND RELA_ID = :relaId ", parameter);
            }

            if (!override) {
                // 删除重复设置的关系
                Map<String, Object> parameter = Maps.newHashMap();
                parameter.put("dataUuids", dataUuids);
                parameter.put("type", type.ordinal());
                parameter.put("relaId", relaId);
                parameter.put("relaDataUuids", relaDataUuids);
                modelService.delete("UF_" + modelEntity.getId() + "_DL",
                        " DATA_UUID IN :dataUuids AND RELA_DATA_UUID IN :relaDataUuids AND TYPE = :type AND RELA_ID = :relaId ", parameter);
            }

            for (Long dataUuid : dataUuids) {
                for (Long relaUuid : relaDataUuids) {
                    Model model = new Model("UF_" + modelEntity.getId() + "_DL");
                    model.getProps().add(new Model.Prop("DATA_UUID", dataUuid));
                    model.getProps().add(new Model.Prop("RELA_DATA_UUID", relaUuid));
                    model.getProps().add(new Model.Prop("TYPE", type.ordinal()));
                    model.getProps().add(new Model.Prop("RELA_ID", relaId));
                    modelService.saveOrUpdate(model);
                }
            }

        }
    }

    @Override
    @Transactional
    public void deleteDataRelaData(Long uuid, List<Long> dataUuids, MarkType type, List<Long> relaDataUuids, String relaId) {
        DataModelEntity modelEntity = getOne(uuid);
        if (modelEntity != null) {
            Map<String, Object> parameter = Maps.newHashMap();
            parameter.put("dataUuids", dataUuids);
            parameter.put("type", type.ordinal());
            parameter.put("relaId", relaId);
            if (CollectionUtils.isNotEmpty(relaDataUuids)) {
                parameter.put("relaDataUuids", relaDataUuids);
            }
            modelService.delete("UF_" + modelEntity.getId() + "_DL",
                    " DATA_UUID IN :dataUuids AND TYPE = :type AND RELA_ID = :relaId "
                            + (parameter.containsKey("relaDataUuids") ? " AND RELA_DATA_UUID IN :relaDataUuids" : ""), parameter);
        }
    }

    @Override
    public List<Map<String, Object>> getDataRelaData(Long uuid, List<Long> dataUuids, MarkType type, String relaId) {
        DataModelEntity modelEntity = getOne(uuid);
        if (modelEntity != null) {
            Map<String, Object> parameter = Maps.newHashMap();
            parameter.put("dataUuids", dataUuids);
            parameter.put("relaId", relaId);
            parameter.put("type", type.ordinal());
            parameter.put("creator", SpringSecurityUtils.getCurrentUserId());
            return modelService.list("UF_" + modelEntity.getId() + "_DL", " DATA_UUID IN :dataUuids AND TYPE = :type AND RELA_ID = :relaId AND CREATOR=:creator", parameter);
        }
        return null;
    }

    @Override
    @Transactional
    public Long saveDataModelDataAsNewVersion(Long uuid, Long dataUuid) {
        DataModelEntity modelEntity = getOne(uuid);
        if (modelEntity != null) {
            Model model = modelService.getModel("UF_" + modelEntity.getId(), dataUuid);

            // 查询数据版本
            Map<String, Object> params = Maps.newHashMap();
            params.put("nuuid", dataUuid);
            BigDecimal maxVer = modelService.getNumberBySQL("SELECT max(VERSION) FROM " + "UF_" + modelEntity.getId() + "_VN" + " WHERE NUUID=:nuuid", params, BigDecimal.class);
            if (maxVer == null) {
                maxVer = new BigDecimal("1.0");
            } else {
                maxVer.add(new BigDecimal(0.1));
            }
            model.setUuid(null);
            model.setCreateTime(null);
            model.setModifyTime(null);
            model.setTable("UF_" + modelEntity.getId());
            modelService.saveOrUpdate(model);// 保存为最新数据
            modelService.delete(model.getTable(), dataUuid);// 删除旧数据
            Model versionModel = new Model();
            BeanUtils.copyProperties(model, versionModel);
            versionModel.setUuid(dataUuid);
            versionModel.setTable("UF_" + modelEntity.getId() + "_VN");
            versionModel.setPropValue("NUUID", model.getUuid());
            versionModel.setPropValue("VERSION", maxVer);
            // 更新所有的版本数据的NUUID为最新的
            params.put("nuuid", model.getUuid());
            params.put("oldNuuid", dataUuid);
            modelService.updateBySQL("UPDATE " + versionModel.getTable() + " SET NUUID=:nuuid WHERE NUUID=:oldNuuid", params);
            modelService.saveOrUpdate(versionModel);
            return model.getUuid();
        }
        return null;
    }

    @Override
    @Transactional
    public Long saveOrUpdateByFormData(ModelFormDataRequest dataRequest) {
        DyFormData dyFormData = this.dyFormFacade.parseDyformData(dataRequest.getFormDataJson());
        if (dyFormData == null) {
            logger.error("数据解析失败: " + dataRequest.getFormDataJson());
            throw new BusinessException("解析表单json数据异常");
        }
        boolean isNew = MapUtils.isEmpty(dyFormData.getUpdatedFormDatas()) && MapUtils.isNotEmpty(dyFormData.getAddedFormDatas()) && MapUtils.isEmpty(dyFormData.getDeletedFormDatas());
        String uuid = this.dyFormFacade.saveFormData(dyFormData);


        // 保存数据权限
//        if (isNew) {
//            Model aclModel = new Model(dyFormData.getTableName() + "_AC");
//            aclModel.setPropValue("DATA_UUID", uuid);
//            aclModel.setPropValue("SID", SpringSecurityUtils.getCurrentUserId());
//            aclModel.setPropValue("PER", AclPer.OWN.ordinal());// 数据所有权
//            modelService.saveOrUpdate(aclModel);
//
//            // 解析其他权限维度
//            if (CollectionUtils.isNotEmpty(dataRequest.getAllowAccessTypes())) {
//                // 获取当前用户的组织节点数据，生成权限维度
////                PlatformUserDetials userDetials = SpringSecurityUtils.getCurrentUser();
//                //TODO: 获取用户的部门、职位
//
//            }
//
//
//        }


        return Long.parseLong(uuid);
    }

    @Override
    @Transactional
    public void saveAcl(AclDataRequest dataRequest) {
        if (CollectionUtils.isNotEmpty(dataRequest.getDataList())) {
            for (AclDataRequest.AclData data : dataRequest.getDataList()) {
                if (data.getDelete()) {
                    Map<String, Object> params = Maps.newHashMap();
                    params.put("sid", data.getSids());
                    params.put("aclPer", data.getAclPerList());
                    params.put("dataUuid", data.getDataUuid());
                    modelService.delete(" UF_" + dataRequest.getDataModelId() + "_AL", "SID IN :sid AND DATA_UUID=:dataUuid AND ACL_PER IN :aclPer", params);
                } else {
                    // 添加权限
                    for (String sid : data.getSids()) {
                        for (AclPer acl : data.getAclPerList()) {
                            Model aclModel = new Model("UF_" + dataRequest.getDataModelId() + "_AL");
                            aclModel.setPropValue("DATA_UUID", data.getDataUuid());
                            aclModel.setPropValue("SID", sid);
                            aclModel.setPropValue("PER", acl.ordinal());// 数据所有权
                            modelService.saveOrUpdate(aclModel);
                        }
                    }

                }
            }
        }
    }

    @Override
    public List<Map<String, Object>> getDataAcls(String dataModelId, List<Long> dataUuids, boolean filterExpire) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("dataUuids", dataUuids);
        if (filterExpire) {
            param.put("_now", new Date());
        }
        return modelService.list("UF_" + dataModelId + "_AC", "DATA_UUID IN :dataUuids " + (filterExpire ? "  AND (EXPIRE_TIME IS NULL OR EXPIRE_TIME > :_now) " : ""), param);
    }

    @Override
    @Transactional
    public void deleteDataModel(Long uuid) {
        DataModelEntity dataModelEntity = getOne(uuid);
        if (dataModelEntity != null) {
            delete(uuid);
            dataModelDetailDao.deleteByDataModelUuid(uuid);
        }
    }

    @Override
    @Transactional
    public void dropDataModel(String id, Boolean force) {
        String[] ids = new String[]{id, id + "_RL", id + "_VN"  /* id + "_AC"*/, id + "_DL"};
        for (String i : ids) {

            DataModelEntity modelEntity = getById(i);
            if (modelEntity != null) {
                try {
                    modelService.drop("UF_" + i, force ? false : id.equals(i)); // 主表安全删除
                    // 删除表数据
                    delete(modelEntity.getUuid());
                    dataModelDetailDao.deleteByDataModelUuid(modelEntity.getUuid());
                } catch (Exception e) {
                    logger.error("删除表 [ UF_" + i + " ] 异常: ", e);
                    if (id.equals(i)) { // 主表因为存在数据，无法删除
                        throw new RuntimeException(e.getMessage());
                    }
                }
            }
        }
    }

    @Override
    public DataModelDetailEntity getDetailByDataModelUuid(Long uuid) {
        return dataModelDetailDao.getOneByFieldEq("dataModelUuid", uuid);
    }


    @Override
    @Transactional
    public void updateDataModelTableConstruct(String oldColumnJson, String columnJson, String oldRemark, Long uuid,
                                              Boolean createMainTable, Boolean creatFormRl, Boolean createDmRl) {
        DataModelEntity entity = getOne(uuid);
        // 创建主表
        GenerateMapping mapping = new GenerateMapping("UF_" + entity.getId().toUpperCase(), entity.getRemark()
                , new GenerateMapping.Id("UUID", "string", 64));//FIXME: 由于表单生成的数据库表的UUID为字符型，这里先保持一致，待后续统一为长整型
        mapping.addProperty(this.getBaseProperties());
        mapping.addProperty(new PropertyBuilder().name("TENANT").type("string").length(64).comment("归属租户").toProperty());
        mapping.addProperty(new PropertyBuilder().name("SYSTEM").type("string").length(64).comment("归属系统").toProperty());
        mapping.addProperty(new PropertyBuilder().name("STATUS").type("string").precision(1).comment("是否删除").toProperty());
        mapping.addProperty(new PropertyBuilder().name("SYSTEM_UNIT_ID").type("string").length(32).comment("归属单位ID").toProperty());
        mapping.addProperty(new PropertyBuilder().name("FORM_UUID").type("string").length(64).comment("表单UUID").toProperty());
        List<ColumnDto> colList = JsonUtils.gson2List(columnJson, new TypeToken<List<ColumnDto>>() {
        }.getType());

        List<ColumnDto> originalList = oldColumnJson != null ? JsonUtils.gson2List(oldColumnJson, new TypeToken<List<ColumnDto>>() {
        }.getType()) : null;
        Map<String, ColumnDto> originalColMap = null;
        final GenerateMapping rollbackMapping = new GenerateMapping("UF_" + entity.getId().toUpperCase(), oldRemark
                , new GenerateMapping.Id("UUID", "string", 64));//FIXME: 由于表单生成的数据库表的UUID为字符型，这里先保持一致，待后续统一为长整型
        if (originalList != null) {
            rollbackMapping.setPropertyList(new PropertyBuilder().copy(mapping.getPropertyList()));
            List<String> originalTenantUk = Lists.newArrayList();// 与租户字段关联的唯一约束key
            originalColMap = Maps.uniqueIndex(originalList, new Function<ColumnDto, String>() {
                @Nullable
                @Override
                public String apply(@Nullable ColumnDto col) {
                    // 回滚使用的mapping
                    if (!rollbackMapping.hasProperty(col.getColumn())) {
                        Property prop = new PropertyBuilder().name(col.getColumn()).type(col.getDataType())
                                .length(col.getLength()).scale(col.getScale()).comment(col.getRemark())
                                .notNull(col.getNotNull()).unique(DataUniqueType.GLOBAL.name().equalsIgnoreCase(col.getUnique())).toProperty();
                        if (DataUniqueType.TENANT.name().equalsIgnoreCase(col.getUnique())) {
                            // 租户级唯一，则需要创建组合字段唯一
                            String uk = Constraint.generateName("UK_", new org.hibernate.mapping.Table(mapping.getTableName())
                                    , new Column[]{new Column(col.getColumn()), new Column("TENANT")});
                            prop.setUniqueKey(uk);
                            originalTenantUk.add(uk);
                        }
                        rollbackMapping.addProperty(prop);
                    }
                    return col.getUuid();
                }
            });
            if (CollectionUtils.isNotEmpty(originalTenantUk)) {
                Property tenantProp = rollbackMapping.getProperty("TENANT");
                if (tenantProp != null) {
                    tenantProp.setUniqueKey(StringUtils.join(originalTenantUk, ", "));
                }
            }
        }
        List<String> unDeletedColumns = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(colList)) {
            List<String> tenantUk = Lists.newArrayList();// 与租户字段关联的唯一约束key
            for (ColumnDto col : colList) {
                if (!mapping.hasProperty(col.getColumn())) {
                    Property property = new PropertyBuilder().name(col.getColumn()).type(col.getDataType())
                            .length(col.getLength()).scale(col.getScale()).comment(col.getRemark())
                            .notNull(col.getNotNull()).unique(DataUniqueType.GLOBAL.name().equalsIgnoreCase(col.getUnique())).toProperty();
                    if (DataUniqueType.TENANT.name().equalsIgnoreCase(col.getUnique())) {
                        // 租户级唯一，则需要创建组合字段唯一
                        String uk = Constraint.generateName("UK_", new org.hibernate.mapping.Table(mapping.getTableName())
                                , new Column[]{new Column(col.getColumn()), new Column("TENANT")});
                        property.setUniqueKey(uk);
                        tenantUk.add(uk);
                    }
                    if (originalColMap != null && originalColMap.get(col.getUuid()) != null) {
                        ColumnDto orginalCol = originalColMap.get(col.getUuid());
                        unDeletedColumns.add(orginalCol.getColumn());
                        if (!orginalCol.getColumn().equalsIgnoreCase(col.getColumn())) {
                            // 改名: 通过备注json传递
                            JSONObject object = new JSONObject();
                            object.put("comment", property.getComment());
                            object.put("rename", orginalCol.getColumn());
                            property.setComment(object.toString());

                            // 回滚字段要反过来，否则会导致回滚时候已经改名的字段被删除（导致改名字段数据丢失）
                            Property prop = rollbackMapping.getProperty(orginalCol.getColumn());
                            if (prop != null) {
                                object = new JSONObject();
                                object.put("comment", orginalCol.getRemark());
                                object.put("rename", property.getName());
                                object.put("rollback", "true");
                                prop.setComment(object.toString());
                            }
                        }
                    }
                    mapping.addProperty(property);
                }
            }

            // 租户字段唯一性
            if (!tenantUk.isEmpty()) {
                Property tenantProperty = mapping.getProperty("TENANT");
                if (tenantProperty != null) {
                    tenantProperty.setUniqueKey(StringUtils.join(tenantUk, ", "));
                }
            }

        }
        if (CollectionUtils.isNotEmpty(originalList)) {
            for (ColumnDto dto : originalList) {
                if (!(mapping.hasProperty(dto.getColumn()) || unDeletedColumns.contains(dto.getColumn()))) {
                    Integer num = modelService.getNumberBySQL("select 1 from " + "UF_" + entity.getId().toUpperCase() + " where " + dto.getColumn() + " is not null", null, Integer.class);
                    if (num != null) {
                        throw new RuntimeException("字段[" + dto.getColumn() + "]存在数据，不允许删除字段");
                    }
                }
            }
        }

        try {
            if (createMainTable) {
                createByMapping(mapping);
            }
        } catch (Exception e) {
            // 回滚映射表
            if (e instanceof SQLSyntaxErrorException && rollbackMapping.getPropertyList().size() > 0) {
                try {
                    logger.info("执行DDL Mapping 异常，开始执行回滚Mapping");
                    createByMapping(rollbackMapping);
                } catch (Exception ex) {
                }

            }
            throw new RuntimeException(e);
        }

        if (creatFormRl) {
            createFormRlTable(entity);
        }
        if (createMainTable || createDmRl) {
            // 创建数据模型对应的数据关系表，用于实现数据的标记、关系等数据行为
            DataModelEntity dlEntity = new DataModelEntity(entity.getId() + "_DL", entity.getName() + "的数据关系",
                    DataModelEntity.Type.RELATION, entity.getName() + "的数据关系", entity.getModule());
            if (getById(dlEntity.getId()) == null) {
                dlEntity.setUuid(Math.abs(new BigInteger(1, DigestUtils.getMd5Digest().digest(("(DL)_" + dlEntity.getId()).toLowerCase().getBytes())).longValue()));
                dao.save(dlEntity);
                DataModelDetailEntity rnDetailEntity = new DataModelDetailEntity();
                rnDetailEntity.setDataModelUuid(dlEntity.getUuid());
                rnDetailEntity.setId(dlEntity.getId());
                rnDetailEntity.setUuid(dlEntity.getUuid());
                List<ColumnDto> rnColumnDtos = Lists.newArrayList(getBaseColumnDtos());
                rnColumnDtos.add(new ColumnDto("DATA_UUID", "数据UUID", "DATA_UUID", "varchar", 64, true));
                rnColumnDtos.add(new ColumnDto("RELA_DATA_UUID", "关联数据UUID", "RELA_DATA_UUID", "varchar", 64, false));
                rnColumnDtos.add(new ColumnDto("RELA_ID", "关联对象ID", "RELA_ID", "varchar", 30, false));
                rnColumnDtos.add(new ColumnDto("TYPE", "关系类型", "TYPE", "number", 2, true));
                rnColumnDtos.add(new ColumnDto("SEQ", "序号", "SEQ", "number", 9, false));
                rnColumnDtos.addAll(getTenantSysColumnDtos());
                rnDetailEntity.setColumnJson(JsonUtils.object2Gson(rnColumnDtos));
                this.dataModelDetailDao.save(rnDetailEntity);
            }


            GenerateMapping dlMapping = new GenerateMapping("UF_" + dlEntity.getId().toUpperCase(), entity.getRemark()
                    , new GenerateMapping.Id("UUID", "string", 64));
            if (!modelService.tableExist(dlMapping.getTableName())) {
                dlMapping.addProperty(this.getBaseProperties());
                dlMapping.addProperty(new PropertyBuilder().name("DATA_UUID").type("string").length(64).notNull(true).toProperty());
                dlMapping.addProperty(new PropertyBuilder().name("RELA_DATA_UUID").type("string").length(64).toProperty());
                dlMapping.addProperty(new PropertyBuilder().name("TYPE").type("java.math.BigDecimal").precision(2).scale(0).notNull(true).toProperty());
                dlMapping.addProperty(new PropertyBuilder().name("SEQ").type("integer").toProperty());
                dlMapping.addProperty(new PropertyBuilder().name("RELA_ID").type("string").length(30).comment("归属租户").notNull(false).toProperty());
                dlMapping.addProperty(getTenantSysProperties());
                try {
                    createByMapping(dlMapping);
                } catch (Exception e) {
                    logger.error("创建模型关系数据表异常：", e);
                    throw new BusinessException(e);
                }
            }


            // 生成数据版本表
            DataModelEntity versionEntity = getById(entity.getId() + "_VN");
            if (versionEntity == null) {
                versionEntity = new DataModelEntity(entity.getId() + "_VN", entity.getName() + "数据版本", DataModelEntity.Type.RELATION, null, entity.getModule());
            }
            if (getById(versionEntity.getId()) == null) {
                DataModelDetailEntity vrDetailEntity = new DataModelDetailEntity();
                if (versionEntity.getUuid() == null) {
                    versionEntity.setUuid(Math.abs(new BigInteger(1, DigestUtils.getMd5Digest().digest(("(VN)_" + versionEntity.getId()).toLowerCase().getBytes())).longValue()));
                    dao.save(versionEntity);
                    vrDetailEntity.setUuid(versionEntity.getUuid());
                    vrDetailEntity.setDataModelUuid(versionEntity.getUuid());
                    vrDetailEntity.setId(versionEntity.getId());
                } else {
                    vrDetailEntity = getDetailByDataModelUuid(versionEntity.getUuid());
                }
                List<ColumnDto> columnDtos = JsonUtils.gson2List(columnJson, new TypeToken<List<ColumnDto>>() {
                }.getType());
                columnDtos.add(new ColumnDto("NUUID", "最新数据的UUID", "NUUID", "number", 19, true));
                columnDtos.add(new ColumnDto("VERSION", "数据版本号", "VERSION", "number", 12, false));
                vrDetailEntity.setColumnJson(JsonUtils.object2Gson(columnDtos));
                this.dataModelDetailDao.save(vrDetailEntity);
            }

            GenerateMapping versionMapping = new GenerateMapping("UF_" + versionEntity.getId(), "数据版本"
                    , mapping.getId());
//            if (!modelService.tableExist(versionMapping.getTableName())) {
            List<Property> properties = mapping.getPropertyList();
            for (Property prop : properties) {
                Property vp = new Property();
                BeanUtils.copyProperties(prop, vp);
                // 历史版本数据去掉唯一性约束
                vp.setUnique(false);
                vp.setUniqueKey(null);
                versionMapping.addProperty(vp);
            }
            versionMapping.addProperty(new PropertyBuilder().name("NUUID").type("long").toProperty());// 最新的UUID
            versionMapping.addProperty(new PropertyBuilder().name("VERSION").type("java.math.BigDecimal").precision(9).scale(2).notNull(true).toProperty());
            try {
                createByMapping(versionMapping);
            } catch (Exception e) {
                logger.error("创建模型关系数据表异常：", e);
                throw new BusinessException(e);
            }
//            }


            // 创建数据访问控制表
//            DataModelEntity acEntity = new DataModelEntity(entity.getId() + "_AC", entity.getName() + "数据控制",
//                    DataModelEntity.Type.RELATION, entity.getName() + "数据控制", entity.getModule());
//            if (getById(acEntity.getId()) == null) {
//                acEntity.setUuid(Math.abs(new BigInteger(1, DigestUtils.getMd5Digest().digest(("(AC)_" + acEntity.getId()).toLowerCase().getBytes())).longValue()));
//                dao.save(acEntity);
//                DataModelDetailEntity acDetailEntity = new DataModelDetailEntity();
//                acDetailEntity.setUuid(acEntity.getUuid());
//                acDetailEntity.setDataModelUuid(acEntity.getUuid());
//                acDetailEntity.setId(acEntity.getId());
//                List<ColumnDto> acColumnDtos = Lists.newArrayList();
//                acColumnDtos.addAll(getBaseColumnDtos());
//                acColumnDtos.add(new ColumnDto("DATA_UUID", "数据UUID", "DATA_UUID", "number", 19, true));
//                acColumnDtos.add(new ColumnDto("SID", "权限ID", "SID", "varchar", 120, false));
//                acColumnDtos.add(new ColumnDto("PER", "允许操作", "PER", "number", 2, false));
//                acColumnDtos.add(new ColumnDto("EXPIRE_TIME", "失效时间", "EXPIRE_TIME", "timestamp", 6, false));
//                acColumnDtos.addAll(getTenantSysColumnDtos());
//                acDetailEntity.setColumnJson(JsonUtils.object2Gson(acColumnDtos));
//                this.dataModelDetailDao.save(acDetailEntity);
//            }
//
//
//            GenerateMapping acMapping = new GenerateMapping("UF_" + acEntity.getId(), entity.getRemark()
//                    , new GenerateMapping.Id("UUID", "long"));
//            if (!modelService.tableExist(acMapping.getTableName())) {
//                acMapping.addProperty(this.getBaseProperties());
//                acMapping.addProperty(new PropertyBuilder().name("DATA_UUID").type("long").toProperty());
//                acMapping.addProperty(new PropertyBuilder().name("SID").type("string").length(120).comment("权限资源ID").notNull(true).toProperty());
//                acMapping.addProperty(new PropertyBuilder().name("PER").type("java.math.BigDecimal").precision(2).scale(0).notNull(true).comment("权限类型").defaultValue("0").toProperty());
//                acMapping.addProperty(new PropertyBuilder().name("EXPIRE_TIME").type("timestamp").comment("失效时间").toProperty());
//                acMapping.addProperty(getTenantSysProperties());
//                try {
//                    createByMapping(acMapping);
//                } catch (Exception e) {
//                    logger.error("创建模型关系数据表异常：", e);
//                    throw new BusinessException(e);
//                }
//            }


        }

    }


    @Override
    public List<Map<String, Object>> queryViewDataBySql(String sql, Map<String, Object> sqlParameter, PagingInfo pagingInfo) {
        if (StringUtils.isNotBlank(sql)) {

            List<SQLQuery> queries = Lists.newArrayListWithCapacity(2);

            SQLQuery dataQuery = this.dao.getSession().createSQLQuery(sql);
            dataQuery.setResultTransformer(new ModelServiceImpl.MapResultTransformer());
            queries.add(dataQuery);

            SQLQuery countQuery = null;
            if (pagingInfo != null) {
                countQuery = this.dao.getSession().createSQLQuery(
                        "select count(1) " + sql.substring(sql.indexOf("FROM")));
                queries.add(countQuery);// 统计梳理
                dataQuery.setFirstResult(pagingInfo.getFirst());
                dataQuery.setMaxResults(pagingInfo.getPageSize());
            }


            if (MapUtils.isNotEmpty(sqlParameter)) {
                Set<Map.Entry<String, Object>> entries = sqlParameter.entrySet();
                // 设置参数
                for (SQLQuery query : queries) {
                    for (Map.Entry<String, Object> map : entries) {
                        Object value = map.getValue();
                        if (value != null) {
                            query.setParameter(map.getKey(), value);
                        }
                    }
                }
            }

            List list = dataQuery.list();
            if (countQuery != null) {
                pagingInfo.setTotalCount(NumberUtils.toLong(countQuery.list().get(0).toString()));
            }

            return list;
        }
        return null;
    }

    @Override
    public Boolean canDropColumn(Long uuid, String column) {
        DataModelEntity modelEntity = getOne(uuid);
        if (modelEntity != null) {
            Integer num = modelService.getNumberBySQL("select 1 from " + "UF_" + modelEntity.getId().toUpperCase() + " where " + column + " is not null", null, Integer.class);
            if (num != null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<Table> getTableComments(List<String> tables) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("tableName", tables);
        List<QueryItem> queryItems = dao.listQueryItemByNameSQLQuery("queryTableComments", param, null);
        List<Table> result = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(queryItems)) {
            for (QueryItem item : queryItems) {
                result.add(new Table(item.getString("tableName"), item.getString("comments")));
            }
        }
        return result;
    }

    @Override
    public List<DataModelEntity> getByModuleAndTypes(List<DataModelEntity.Type> type, List<String> module) {
        Map<String, Object> param = Maps.newHashMap();
        List<String> where = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(type)) {
            param.put("type", type);
            if (type.size() == 1) {
                param.put("type", type.get(0));
                where.add("dm.type = :type");
            } else {
                where.add("dm.type in :type");
            }
        }
        if (CollectionUtils.isNotEmpty(module)) {
            param.put("module", module);
            if (module.size() == 1) {
                param.put("module", module.get(0));
                where.add("dm.module = :module");
            } else {
                where.add("dm.module in :module");
            }
        }

        String system = RequestSystemContextPathResolver.system();
        if (StringUtils.isNotBlank(system)) {
            param.put("system", system);
            where.add("(dm.system = :system or exists(select pm.moduleId from AppProdModuleEntity pm, AppSystemInfoEntity si\n" +
                    "                            where pm.prodVersionUuid = si.prodVersionUuid\n" +
                    "                            and dm.module = pm.moduleId and si.system = :system))");
        }

        return dao.listByHQL("from DataModelEntity dm "
                + (where.isEmpty() ? "" : (" where " + StringUtils.join(where, " and ")))
                + " order by dm.modifyTime desc", param);
    }


    @Override
    public List<Table> getDmExposedTableEntities() {
        SessionFactory sessionFactory = this.dao.getSession().getSessionFactory();
        Map<String, ClassMetadata> classMetadataMap = sessionFactory.getAllClassMetadata();
        Set<Map.Entry<String, ClassMetadata>> entries = classMetadataMap.entrySet();
        List<Table> tables = Lists.newArrayList();
        List<String> tableNames = Lists.newArrayList();
        Map<String, Table> nameMap = Maps.newHashMap();
        for (Map.Entry<String, ClassMetadata> ent : entries) {
            ClassMetadata metadata = ent.getValue();
            SingleTableEntityPersister persister = (SingleTableEntityPersister) metadata;
            String tableName = persister.getTableName().toLowerCase();
            Class mappedClass = metadata.getMappedClass();
            if (!exposeEntitis.contains(mappedClass.getCanonicalName())
                    && !StringUtils.startsWithAny(tableName, Lists.newArrayList("app_", "wf_", "repo_", "biz_", "audit_", "org_", "user_", "db_", "api_").toArray(new String[]{}))) {
                continue;
            }
            Table table = new Table(persister.getTableName().toUpperCase());
            tableNames.add(table.getTableName());
            nameMap.put(table.getTableName(), table);
            tables.add(table);
//                table.setColumns( tableRepositoryFactory.getTableRepository(persister.getTableName()).getTableColumns());
//                String[] props = persister.getPropertyNames();
//                String idProp = persister.getIdentifierPropertyName();
//                if (StringUtils.isNotBlank(idProp)) {
//                    table.addColumn(new Table.Column(persister.getPropertyColumnNames(idProp)[0].toUpperCase(),
//                            this.converColumnType(persister.getPropertyType(idProp))));
//                }
//
//                for (String p : props) {
//                    Type type = persister.getPropertyType(p);
//                    table.addColumn(new Table.Column(persister.getPropertyColumnNames(p)[0].toUpperCase(), this.converColumnType(type)));
//                }

        }
        if (!tableNames.isEmpty()) {
            List<Table> tableComments = this.getTableComments(tableNames);
            if (CollectionUtils.isNotEmpty(tableComments)) {
                for (Table t : tableComments) {
                    if (nameMap.containsKey(t.getTableName()) && StringUtils.isNotBlank(t.getComment())) {
                        nameMap.get(t.getTableName()).setComment(t.getComment());
                    }
                }
            }
        }
        return tables;
    }

    private String converColumnType(Type type) {
        String name = type.getName();
        if ("string".equalsIgnoreCase(name)) {
            return "varchar";
        } else if ("integer".equalsIgnoreCase(name)
                || "long".equalsIgnoreCase(name) || "double".equalsIgnoreCase(name) || "float".equalsIgnoreCase(name)) {
            return "number";
        }
        return name;
    }

    @Value("${expose.entity}")
    public void setExposeEntitis(String value) {
        if (StringUtils.isNotBlank(value)) {
            exposeEntitis.addAll(Lists.newArrayList(value.split(",|;")));
        }

    }


    @Override
    public void afterPropertiesSet() throws Exception {
        this.freemarkerConfig = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_22);
        this.freemarkerConfig.setDefaultEncoding("UTF-8");
        this.freemarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        this.freemarkerConfig.setClassForTemplateLoading(HibernateConfiguration.class, "");
    }
}
