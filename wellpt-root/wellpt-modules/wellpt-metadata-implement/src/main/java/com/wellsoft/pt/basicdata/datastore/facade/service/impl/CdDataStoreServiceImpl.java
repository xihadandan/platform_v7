/*
 * @(#)2016年10月27日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.facade.service.impl;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.io.ClobUtils;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.basicdata.datastore.bean.*;
import com.wellsoft.pt.basicdata.datastore.entity.CdDataStoreDefinition;
import com.wellsoft.pt.basicdata.datastore.facade.service.CdDataStoreService;
import com.wellsoft.pt.basicdata.datastore.provider.DatastoreTreeDataProvider;
import com.wellsoft.pt.basicdata.datastore.service.CdDataStoreDefinitionService;
import com.wellsoft.pt.basicdata.datastore.service.DataStoreQueryService;
import com.wellsoft.pt.basicdata.datastore.support.*;
import com.wellsoft.pt.basicdata.datastore.support.export.DataStoreExport;
import com.wellsoft.pt.basicdata.datastore.support.export.DataStoreExportFactory;
import com.wellsoft.pt.basicdata.datastore.support.export.ExportColumn;
import com.wellsoft.pt.basicdata.datastore.support.export.ExportParams;
import com.wellsoft.pt.basicdata.datastore.support.renderer.DataStoreRenderer;
import com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeDataStoreRequestParam;
import com.wellsoft.pt.jpa.criteria.Criteria;
import com.wellsoft.pt.jpa.criterion.CriterionOperator;
import com.wellsoft.pt.jpa.criterion.Restrictions;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.enums.BuildInRole;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.DataSource;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Clob;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年10月27日.1	xiem		2016年10月27日		Create
 * </pre>
 * @date 2016年10月27日
 */
@Service
@Transactional(readOnly = true)
public class CdDataStoreServiceImpl extends BaseServiceImpl implements CdDataStoreService {
    private static final String KEY_DATA_INDEX = "dataIndex";

    @Autowired
    private CdDataStoreDefinitionService cdDataStoreDefinitionService;
    @Autowired(required = false)
    private List<DataStoreRenderer> dataStoreRenderers;
    private Map<String, DataStoreRenderer> dataStoreRendererMap;
    @Autowired
    private DataStoreExportFactory dataStoreExportFactory;

    @Autowired
    private DataStoreQueryService dataStoreQueryService;

    @Autowired
    private SecurityApiFacade securityApiFacade;

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public DataStoreData loadDataWithNewTransaction(DataStoreParams params) {
        return loadData(params);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.facade.service.CdDataStoreService#loadData(com.wellsoft.pt.basicdata.datastore.bean.DataStoreParams)
     */
    @Override
    @Transactional(readOnly = true)
    public DataStoreData loadData(DataStoreParams params) {
        // 数据仓库访问认证
        authenticate(params.getDataStoreId());

        DataStoreConfiguration dataStoreConfiguration = loadDataStoreConfiguration(params);
        DataStoreData result = new DataStoreData();
        result.setPagination(params.getPagingInfo());
        if (dataStoreConfiguration == null) {
            return result;
        }

        return this.loadData(params, dataStoreConfiguration);
    }

    @Override
    public DataStoreData loadData(DataStoreParams params, DataStoreConfiguration dataStoreConfiguration) {

        DataStoreData result = new DataStoreData();
        result.setPagination(params.getPagingInfo());
        if (dataStoreConfiguration == null) {
            return result;
        }
        Criteria criteria = dataStoreQueryService.createCriteriaQuery(params, dataStoreConfiguration);
        if (criteria == null) {
            return result;
        }

        addDefaultQueryParams(criteria);

        Map<String, DataStoreColumn> columnMap = dataStoreConfiguration.getColumnMap();
        for (String paramKey : params.getParams().keySet()) {
            criteria.addQueryParams(paramKey, params.getParam(paramKey));
        }
        criteria.setPagingInfo(params.getPagingInfo());
        if (StringUtils.isNotBlank(dataStoreConfiguration.getDefaultCondition())) {
            criteria.addCriterion(Restrictions.sql(dataStoreConfiguration.getDefaultCondition()));
        }
        Iterator<Condition> it = params.getCriterions().iterator();
        while (it.hasNext()) {
            criteria.addCriterion(DataStoreConditionConverter.covertCriterion(it.next(), columnMap));
        }
        Iterator<DataStoreOrder> itt = params.getOrders().iterator();
        while (itt.hasNext()) {
            criteria.addOrder(itt.next().toOrder());
        }
        // 默认排序
        String defaultOrder = dataStoreConfiguration.getDefaultOrder();
        if (StringUtils.isNotBlank(defaultOrder)) {
            Iterator<DataStoreOrder> orderIterator = DataStoreOrderConverter.covertOrders(defaultOrder).iterator();
            while (orderIterator.hasNext()) {
                criteria.addOrder(orderIterator.next().toOrder());
            }
        }
        Stopwatch timer = Stopwatch.createStarted();
        List<QueryItem> list = criteria.list(QueryItem.class);
        logger.info("数据仓库[{}], 数据接口[{}], 数据查询耗时：{}",
                new Object[]{dataStoreConfiguration.getName(), dataStoreConfiguration.getDataInterfaceName(), timer});
        if (list != null && !list.isEmpty()) {
            for (QueryItem item : list) {
                Map<String, Object> map = new HashMap<String, Object>();
                for (DataStoreColumn column : columnMap.values()) {
                    Object value = item.get(QueryItem.getKey(column.getColumnIndex()));
                    if (value instanceof Clob) {
                        value = ClobUtils.ClobToString((Clob) value);
                    } else if (value instanceof BigDecimal) {
                        value = ((BigDecimal) value).toString();
                    }
                    map.put(column.getColumnIndex(), value);
                }
                result.addData(map);
            }
        }
        timer.reset().start();
        result.setData(renderData(result.getData(), params.getRenderersMap()));
        logger.info("数据仓库[{}]，渲染器渲染耗时：{}", dataStoreConfiguration.getName(), timer.stop());
        return result;
    }

    /**
     * @param dataStoreId
     */
    private void authenticate(String dataStoreId) {
        // 非游客用户或者匿名客户端访问不进行验证
        if (!SpringSecurityUtils.isAnonymousUser() || SpringSecurityUtils.hasAnyRole(BuildInRole.ROLE_TRUSTED_CLIENT.name())) {
            return;
        }
        // 游客用户访问数据仓库验证
        if (!securityApiFacade.isGranted(dataStoreId, AppFunctionType.DataStoreDefinition)) {
            throw new BusinessException("无权限访问数据仓库" + dataStoreId);
        }
    }

    /**
     * 如何描述该方法
     *
     * @param criteria
     */
    private void addDefaultQueryParams(Criteria criteria) {
        Map<String, Object> params = TemplateEngineFactory.getExplainRootModel();
        Set<String> keys = params.keySet();
        for (String k : keys) {
            criteria.addQueryParams(k, params.get(k));
        }
    }

    /**
     * 如何描述该方法
     *
     * @param params
     * @return
     */
    private DataStoreConfiguration loadDataStoreConfiguration(DataStoreParams params) {
        CdDataStoreDefinition dataStoreDefinition = cdDataStoreDefinitionService.getBeanById(params.getDataStoreId());
        if (dataStoreDefinition == null) {
            return null;
        }
        DataStoreConfiguration dataStoreConfiguration = new DataStoreConfiguration();
        BeanUtils.copyProperties(dataStoreDefinition, dataStoreConfiguration);
        dataStoreConfiguration.setDbLinkConfUuid(CdDataStoreDefinition.DataSourceType.EXTERNAL_DS.equals(dataStoreDefinition.getDbSourceType()) ? dataStoreDefinition.getDbLinkConfUuid() : null);
        return dataStoreConfiguration;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.facade.service.CdDataStoreService#loadCount(com.wellsoft.pt.basicdata.datastore.bean.DataStoreParams)
     */
    @Override
    public long loadCount(DataStoreParams params) {
        // 数据仓库访问认证
        authenticate(params.getDataStoreId());

        DataStoreConfiguration dataStoreConfiguration = loadDataStoreConfiguration(params);
        if (dataStoreConfiguration == null) {
            return 0;
        }
        return this.loadCount(params, dataStoreConfiguration);
    }

    @Override
    public long loadCount(DataStoreParams params, DataStoreConfiguration dataStoreConfiguration) {
        Criteria criteria = dataStoreQueryService.createCriteriaQuery(params, dataStoreConfiguration);
        if (criteria == null) {
            return 0;
        }

        addDefaultQueryParams(criteria);
        // 其他命名参数键值对
        for (String paramKey : params.getParams().keySet()) {
            criteria.addQueryParams(paramKey, params.getParam(paramKey));
        }

        Map<String, DataStoreColumn> columnMap = dataStoreConfiguration.getColumnMap();
        if (StringUtils.isNotBlank(dataStoreConfiguration.getDefaultCondition())) {
            criteria.addCriterion(Restrictions.sql(dataStoreConfiguration.getDefaultCondition()));
        }
        Iterator<Condition> it = params.getCriterions().iterator();
        while (it.hasNext()) {
            Condition condition = it.next();
            if (condition != null) {
                criteria.addCriterion(DataStoreConditionConverter.covertCriterion(condition, columnMap));
            }
        }
        return criteria.count();
    }

    @Override
    public Map<String, Object> loadFieldRenderData(DataStoreRendererBean renderer, Map<String, Object> data,
                                                   Map<String, Integer> indexMap) {
        RendererParam renderParam = renderer.getParam();
        DataStoreRenderer dataStoreRenderer = getDataStoreRendererMap()
                .get(renderParam.get(DataStoreRenderer.TYPE_KEY));
        String columnIndex = renderer.getColumnIndex();
        Object value = data.get(columnIndex);
        Map<String, Object> map = new HashMap<String, Object>();
        renderParam.remove("loginType");
        if (dataStoreRenderer != null) {
            value = dataStoreRenderer.renderData(columnIndex, value, data, renderParam);
        }
        map.put(columnIndex + "RenderValue", value);
        map.put("indexMap", indexMap);
        return map;
    }

    private List<Map<String, Object>> renderData(List<Map<String, Object>> data,
                                                 Map<String, DataStoreRendererBean> renderersMap) {
        if (renderersMap == null || renderersMap.isEmpty()) {
            return data;
        }
        if (CollectionUtils.isNotEmpty(data)) {
            for (int index = 0; index < data.size(); index++) {
                Map<String, Object> rowData = data.get(index);
                rowData.put(KEY_DATA_INDEX, index);
                for (String columnIndex : renderersMap.keySet()) {
                    if (rowData.containsKey(columnIndex)) {
                        DataStoreRendererBean renderer = renderersMap.get(columnIndex);
                        RendererParam renderParam = renderer.getParam();
                        if ("1".equals(renderParam.get("loadType"))) {
                            rowData.put(columnIndex + "RenderValue", "");
                        } else {
                            Object value = rowData.get(columnIndex);
                            DataStoreRenderer dataStoreRenderer = getDataStoreRendererMap().get(
                                    renderParam.get(DataStoreRenderer.TYPE_KEY));
                            if (dataStoreRenderer != null) {
                                try {
                                    value = dataStoreRenderer.renderData(columnIndex, value, rowData, renderParam);
                                } catch (Exception e) {
                                    logger.warn("数据仓库字段{}值渲染异常: {}", new Object[]{columnIndex, e.getMessage()});
                                }
                            }
                            rowData.put(columnIndex + "RenderValue", value);

                        }
                    }
                }
                rowData.remove(KEY_DATA_INDEX);
            }
        }
        return data;
    }

    public Map<String, DataStoreRenderer> getDataStoreRendererMap() {
        if (dataStoreRendererMap == null) {
            dataStoreRendererMap = ConvertUtils.convertElementToMap(dataStoreRenderers, "type");
        }
        return dataStoreRendererMap;
    }

    @Override
    public DataSource exportData(String type, ExportParams exportParams) {
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
        List<Condition> crions = p.getCriterions();
        // 处理树形结构导出报错问题  当含有null时会报错
        for (Condition c : crions) {
            Object obj = c.getValue();
            if (obj instanceof List) {
                @SuppressWarnings("rawtypes")
                List cons = (List) obj;
                if (CollectionUtils.isNotEmpty(cons)) {
                    Object o = cons.get(0);
                    if (o == null) {
                        cons.remove(0);
                    }
                }
            }
        }

        p.setRenderers(rendererList);
        DataStoreData data = loadData(p);
        exportParams.setData(data);
        if (MapUtils.isNotEmpty(exportParams.getExtras()) && exportParams.getExtras().containsKey("selectedBy")) {
            Map<String, List<String>> map = (Map<String, List<String>>) exportParams.getExtras().get("selectedBy");
            String dataIndex = map.keySet().iterator().next();
            Iterator<Map<String, Object>> iterator = data.getData().iterator();
            while (iterator.hasNext()) {
                Map<String, Object> dataMap = iterator.next();
                if (dataMap.get(dataIndex) != null && !(map.get(dataIndex).contains(dataMap.get(dataIndex).toString()))) {
                    iterator.remove();
                }
            }
        }

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

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.facade.service.CdDataStoreService#getBeanById(java.lang.String)
     */
    @Override
    public CdDataStoreDefinitionBean getBeanById(String id) {
        return this.cdDataStoreDefinitionService.getBeanById(id);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.facade.service.CdDataStoreService#loadSelect2Data(com.wellsoft.pt.common.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData loadSelect2Data(Select2QueryInfo query) {
        String searchValue = query.getSearchValue();
        String dataStoreId = query.getOtherParams("dataStoreId");
        String idColumnIndex = query.getOtherParams("idColumnIndex");
        String textColumnIndex = query.getOtherParams("textColumnIndex");
        DataStoreParams params = new DataStoreParams();
        params.setPagingInfo(query.getPagingInfo());
        params.setDataStoreId(dataStoreId);
        if (StringUtils.isNotBlank(searchValue)) {
            List<Condition> conditions = new ArrayList<Condition>();
            conditions.add(new Condition(idColumnIndex, searchValue, CriterionOperator.like.getType()));
            conditions.add(new Condition(textColumnIndex, searchValue, CriterionOperator.like.getType()));
            Condition select2Condition = new Condition();
            select2Condition.setConditions(conditions);
            select2Condition.setType(CriterionOperator.or.getType());
            params.getCriterions().add(select2Condition);
        }
        DataStoreData data = loadData(params);
        Select2QueryData returnData = new Select2QueryData(data.getData(), idColumnIndex, textColumnIndex,
                data.getPagination());
        return returnData;
    }

    @Override
    public Select2QueryData loadSelectDataByIds(Select2QueryInfo query) {
        String dataStoreId = query.getOtherParams("dataStoreId");
        String idColumnIndex = query.getOtherParams("idColumnIndex");
        String textColumnIndex = query.getOtherParams("textColumnIndex");
        DataStoreParams params = new DataStoreParams();
        Condition condition = new Condition(idColumnIndex, query.getIds(), "in");
        params.getCriterions().add(condition);
        params.setDataStoreId(dataStoreId);
        DataStoreData data = loadData(params);
        Select2QueryData returnData = new Select2QueryData(data.getData(), idColumnIndex, textColumnIndex,
                data.getPagination());
        return returnData;
    }

    @Override
    public List<TreeNode> loadTreeNodes(TreeDataStoreRequestParam param) {
        // 数据仓库访问认证
        authenticate(param.getDataStoreId());
        return ApplicationContextHolder.getBean(DatastoreTreeDataProvider.class).loadTreeData(param);
    }

    @Override
    public List<Map<String, String>> getQueryInterfaceParams(String className) {
        try {
            List<Map<String, String>> params = Lists.newArrayList();
            AbstractDataStoreQueryInterface dataStoreQueryInterface = (AbstractDataStoreQueryInterface) Class.forName(
                    className).newInstance();
            Class clazz = dataStoreQueryInterface.interfaceParamsClass();
            if (clazz == null) {
                return null;
            }
            Field[] fields = clazz.getDeclaredFields();
            for (Field f : fields) {
                DataStoreInterfaceField interfaceField = f.getAnnotation(DataStoreInterfaceField.class);
                if (interfaceField == null) {
                    continue;
                }
                Map<String, String> p = Maps.newHashMap();
                p.put("name", interfaceField.name());
                p.put("domType", interfaceField.domType().name());
                p.put("id", f.getName());
                p.put("dataJSON", interfaceField.dataJSON());
                p.put("service", interfaceField.service());
                p.put("placeholder", interfaceField.placeholder());
                p.put("defaultValue", interfaceField.defaultValue());
                params.add(p);
            }
            return params;
        } catch (Exception e) {

        }
        return null;

    }

}
