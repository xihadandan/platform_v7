/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.definition.iexport.provider;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinition;
import com.wellsoft.pt.dyform.implement.definition.enums.DyformTypeEnum;
import com.wellsoft.pt.dyform.implement.definition.iexport.acceptor.FormDefinitionIexportData;
import com.wellsoft.pt.dyform.implement.definition.service.FormDefinitionService;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.FormDefinitionHandler;
import com.wellsoft.pt.jpa.util.HqlUtils;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: 表单定义
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-16.1	zhongzh		2015-6-16		Create
 * </pre>
 * @date 2015-6-16
 */
@Service
@Transactional(readOnly = true)
public class FormDefinitionIexportDataProvider extends AbstractIexportDataProvider<FormDefinition, String> {

    Pattern pattern = Pattern.compile("/proxy-repository/repository/file/mongo/(download|downloadVideoSegment)\\?fileID=[0-9]+");

    static {
        // 8.3 多态表单
        TableMetaData.register(IexportType.FormDefinition, "表单定义", FormDefinition.class);
        // TODO remove old definition
        TableMetaData.register(IexportType.DyFormDefinition, "表单定义", FormDefinition.class);
        TableMetaData.register(IexportType.DyFormDisplayModel, "表单定义", FormDefinition.class);
    }

    @Autowired
    private FormDefinitionService dyFormDefinitionService;


    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.FormDefinition;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getData(java.lang.String)
     */
    @Override
    public IexportData getData(String uuid) {
        FormDefinition dyFormDefinition = this.dao.get(FormDefinition.class, uuid);
        if (dyFormDefinition == null) {
            return new ErrorDataIexportData(IexportType.FormDefinition, "找不到对应的表单定义依赖关系,可能已经被删除", "表单定义", uuid);
        }
        return new FormDefinitionIexportData(dyFormDefinition);
    }

    @Override
    public void storeData(IexportData iexportData, boolean newVer) throws Exception {
        super.storeData(iexportData, newVer);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider#getMetaData()
     */
    @Override
    public IexportMetaData getMetaData() {
        IexportMetaData iexportMetaData = super.getMetaData();
        // 表单ID生成方式
        iexportMetaData.registerColumnValueProcessor(TableMetaData.getTableName(IexportType.FormDefinition), "id",
                EntityIdColumnValueProcessorFactory.getColumnValueProcessor(FormDefinition.class));
        return iexportMetaData;
    }


    @Override
    public String getTreeName(FormDefinition formDefinition) {
        return new FormDefinitionIexportData(formDefinition).getName();
    }

    @Override
    public TreeNode treeNode(String uuid) {
        FormDefinition formDefinition = dyFormDefinitionService.getOne(uuid);
        if (formDefinition == null || !ExportTreeContextHolder.add(getType() + formDefinition.getId())) {
            // 避免导出多个版本的表单
            return null;
        }
        TreeNode node = new TreeNode();
        node.setId(uuid);
        node.setType(getType());
        node.setName(getTreeName(formDefinition));
        if ("v".equalsIgnoreCase(formDefinition.getFormType()) && StringUtils.isNotBlank(formDefinition.getDefinitionVjson())) {
            JSONObject jsonObject = JSONObject.fromObject(formDefinition.getDefinitionVjson());
            if (jsonObject.containsKey("dataModelUuid") && StringUtils.isNotBlank(jsonObject.getString("dataModelUuid"))) {
                this.exportTreeNodeByDataProvider(jsonObject.getString("dataModelUuid"), IexportType.DataModel, node);
//                node.appendChild(this.exportTreeNodeByDataProvider(jsonObject.getString("dataModelUuid"), IexportType.DataModel));
            }
        }
        this.convertRefAppFunction2TreeNodes(uuid, node);


        // 解析定义中涉及的附件文件
        if (StringUtils.isNotBlank(formDefinition.getDefinitionVjson())) {
            Matcher matcher = pattern.matcher(formDefinition.getDefinitionVjson());
            while (matcher.find()) {
                this.exportTreeNodeByDataProvider(matcher.group()
                        .replaceFirst("/proxy-repository/repository/file/mongo/(download|downloadVideoSegment)\\?fileID=", ""), IexportType.LogicFileInfo, node);
//                node.appendChild(this.exportTreeNodeByDataProvider(matcher.group()
//                        .replaceFirst("/proxy-repository/repository/file/mongo/(download|downloadVideoSegment)\\?fileID=", ""), IexportType.LogicFileInfo));
            }
        }

        if ("P".equalsIgnoreCase(formDefinition.getFormType())) {
            // 导出数据模型
            String id = formDefinition.getTableName().toUpperCase().replace("UF_", "");
            List<QueryItem> dataModels = nativeDao.query("select uuid from data_model where type = 0 and id =:id", ImmutableMap.<String, Object>builder().put("id", id).build(), QueryItem.class);
            if (CollectionUtils.isNotEmpty(dataModels)) {
                for (QueryItem i : dataModels) {
                    this.exportTreeNodeByDataProvider(i.getLong("uuid"), IexportType.DataModel, node);
//                    node.appendChild(this.exportTreeNodeByDataProvider(i.getLong("uuid"), IexportType.DataModel));
                }
            }
        }
        return node;
    }

    @Override
    protected List<IExportTable> childTableStream() {
        // 导出表单依赖的资源定义关系数据
        return Lists.newArrayList(
                new IExportTable(Lists.newArrayList("DATA_DEF_UUID", "APP_FUNCTION_UUID", "ITEM_ID"), "select * from APP_DATA_DEF_REF_RESOURCE where data_def_uuid =:uuid and data_def_type ='formDefinition' "),
                new IExportTable("select * from APP_FUNCTION a where exists ( select 1  from APP_DATA_DEF_REF_RESOURCE r where r.app_function_uuid=a.uuid and r.data_def_uuid =:uuid and r.data_def_type ='formDefinition') ")
                ,
                /* 国际化语言定义 */
                new IExportTable("select * from APP_DEF_ELEMENT_I18N where def_id=:id and apply_to='" + IexportType.DyFormDefinition + "'"),
                /* 资源组 */
                new IExportTable("select * from app_module_res_group g where exists ( select 1 from app_module_res_group_member m where m.member_uuid=:uuid and m.group_uuid = g.uuid  ) "),
                new IExportTable("select * from app_module_res_group_member m where m.member_uuid=:uuid ")
        );
    }

    @Override
    @Transactional
    public FormDefinition saveEntityStream(IExportEntityStream stream) {
        FormDefinition formDefinition = super.saveEntityStream(stream);
        dyFormDefinitionService.createFormDefinitionAndFormTable(formDefinition);
        return formDefinition;
    }

    @Override
    public void putChildProtoDataHqlParams(FormDefinition formDefinition, Map<String, FormDefinition> parentMap, Map<String, ProtoDataHql> hqlMap) {
        FormDefinitionHandler dyFormDefinitionJSON = formDefinition.doGetFormDefinitionHandler();
        String start = getType() + Separator.UNDERLINE.getValue();
        String uuid = formDefinition.getUuid();
        // 取出表单从表信息
        for (String subform : dyFormDefinitionJSON.getFormUuidsOfSubform()) {
            if (!subform.equals(uuid)) {
                this.addSub(formDefinition, parentMap, hqlMap, start, subform);
            }
        }

        if (StringUtils.equals(DyformTypeEnum.P.getValue(), formDefinition.getFormType())) {
            parentMap.put(start + "pform" + Separator.UNDERLINE.getValue() + formDefinition.getUuid(), formDefinition);
            if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.FormDefinition))) {
                hqlMap.put(this.getChildHqlKey(IexportType.FormDefinition), new ProtoDataHql(getType(), "FormDefinition"));
            }
            this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.FormDefinition)), formDefinition.getUuid(), "pFormUuids");

            for (String template : dyFormDefinitionJSON.getFormUuidsOfTemplate()) {
                if (!template.equals(uuid)) {
                    this.addSub(formDefinition, parentMap, hqlMap, start, template);
                }
            }
        } else if (StringUtils.equals(DyformTypeEnum.V.getValue(), formDefinition.getFormType())) {
            String pFormUuid = formDefinition.getpFormUuid();
            if (StringUtils.isNotBlank(pFormUuid) && !pFormUuid.equals(uuid)) {
                this.addSub(formDefinition, parentMap, hqlMap, start, pFormUuid);
            }
        } else if (StringUtils.equals(DyformTypeEnum.M.getValue(), formDefinition.getFormType())) {
            String pFormUuid = formDefinition.getpFormUuid();
            if (StringUtils.isNotBlank(pFormUuid) && !pFormUuid.equals(uuid)) {
                this.addSub(formDefinition, parentMap, hqlMap, start, pFormUuid);
            }
        }

        // 遍历字段取出数据字典应用信息,数据源
        for (String fieldName : dyFormDefinitionJSON.getFieldNamesOfMainform()) {
            String inputMode = dyFormDefinitionJSON.getInputMode(fieldName);
            String dictCode = dyFormDefinitionJSON.getFieldPropertyOfStringType(fieldName,
                    DyFormConfig.EnumFieldPropertyName.dictCode);
            String dataStoreId = null, viewStoreId = null;
            if (DyFormConfig.INPUTMODE_CHECKBOX.equals(inputMode) || DyFormConfig.INPUTMODE_RADIO.equals(inputMode)
                    || DyFormConfig.INPUTMODE_SELECTMUTILFASE.equals(inputMode)
                    || DyFormConfig.INPUTMODE_COMBOSELECT.equals(inputMode)
                    || DyFormConfig.INPUTMODE_SELECT.equals(inputMode)) {
                dataStoreId = dyFormDefinitionJSON.getFieldPropertyOfStringType(fieldName,
                        DyFormConfig.EnumFieldPropertyName.dataSourceId);
            } else if (DyFormConfig.INPUTMODE_DIALOG.equals(inputMode)) {
                String relativeMethod = dyFormDefinitionJSON.getFieldPropertyOfStringType(fieldName,
                        DyFormConfig.DYRELATIVEMETHOD_FIELD);
                if (StringUtils.equals(DyFormConfig.DYRELATIVEMETHOD_DIALOG, relativeMethod)) {
                    viewStoreId = dyFormDefinitionJSON.getFieldPropertyOfStringType(fieldName,
                            DyFormConfig.EnumFieldPropertyName.relationDataValueTwo);
                } else if (StringUtils.equals(DyFormConfig.DYRELATIVEMETHOD_SEARCH, relativeMethod)) {
                    dataStoreId = dyFormDefinitionJSON.getFieldPropertyOfStringType(fieldName,
                            DyFormConfig.EnumFieldPropertyName.relationDataValueTwo);
                }
            }
            // 数据字典
            if (StringUtils.isNotBlank(dictCode)) {
                String dictCodeTemp = dictCode.split(":")[0];
                parentMap.put(start + "dictCode" + Separator.UNDERLINE.getValue() + dictCodeTemp, formDefinition);
                if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.DataDictionary))) {
                    hqlMap.put(this.getChildHqlKey(IexportType.DataDictionary), this.getProtoDataHqlType("DataDictionary"));
                }
                this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.DataDictionary)), dictCodeTemp, "types");

                if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.DataDictionaryParent))) {
                    hqlMap.put(this.getChildHqlKey(IexportType.DataDictionaryParent), this.getProtoDataHqlType("DataDictionary"));
                }
                this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.DataDictionaryParent)), dictCodeTemp, "types");

            }
            // 视图
            if (StringUtils.isNotBlank(viewStoreId)) {
                parentMap.put(start + "viewStore" + Separator.UNDERLINE.getValue() + viewStoreId, formDefinition);
                if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.AppWidgetDefinition))) {
                    hqlMap.put(this.getChildHqlKey(IexportType.AppWidgetDefinition), this.getProtoDataHql("AppWidgetDefinition"));
                }
                this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.AppWidgetDefinition)), viewStoreId);
            }
            // 数据源
            if (StringUtils.isNotBlank(dataStoreId)) {
                parentMap.put(start + "dataStore" + Separator.UNDERLINE.getValue() + dataStoreId, formDefinition);
                if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.DataStoreDefinition))) {
                    hqlMap.put(this.getChildHqlKey(IexportType.DataStoreDefinition), this.getProtoDataHqlId("CdDataStoreDefinition"));
                }
                this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.DataStoreDefinition)), dataStoreId, "ids");
            }
        }
        if (StringUtils.isNotBlank(formDefinition.getModuleId())) {
            this.putAppFunctionParentMap(formDefinition, parentMap, hqlMap);
        }
    }

    private void addSub(FormDefinition formDefinition, Map<String, FormDefinition> parentMap, Map<String, ProtoDataHql> hqlMap, String start, String subform) {
        parentMap.put(start + "sub" + Separator.UNDERLINE.getValue() + subform, formDefinition);
        if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.FormDefinition))) {
            hqlMap.put(this.getChildHqlKey(IexportType.FormDefinition), new ProtoDataHql(getType(), "FormDefinition"));
        }
        this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.FormDefinition)), subform);
    }

    private ProtoDataHql getProtoDataHqlType(String entityName) {
        ProtoDataHql protoDataHql = new ProtoDataHql(getType(), entityName);
        protoDataHql.setGenerateHql(new GenerateHql() {
            @Override
            public void build(ProtoDataHql protoDataHql) {
                protoDataHql.getSbHql().append("from ").append(protoDataHql.getEntityName()).append(" where ");
                HqlUtils.appendSql("type", protoDataHql.getParams(), protoDataHql.getSbHql(), (Set<Serializable>) protoDataHql.getParams().get("types"));
            }
        });
        return protoDataHql;
    }

    private ProtoDataHql getProtoDataHqlId(String entityName) {
        ProtoDataHql protoDataHql = new ProtoDataHql(getType(), entityName);
        protoDataHql.setGenerateHql(new GenerateHql() {
            @Override
            public void build(ProtoDataHql protoDataHql) {
                protoDataHql.getSbHql().append("from ").append(protoDataHql.getEntityName()).append(" where ");
                HqlUtils.appendSql("id", protoDataHql.getParams(), protoDataHql.getSbHql(), (Set<Serializable>) protoDataHql.getParams().get("ids"));
            }
        });
        return protoDataHql;
    }


    @Override
    public Map<String, List<FormDefinition>> getParentMapList(ProtoDataHql protoDataHql) {
        Map<String, List<FormDefinition>> map = new HashMap<>();
        // 页面或组件定义依赖的表单定义
        if (protoDataHql.getParentType().equals(IexportType.AppPageDefinition)
                || protoDataHql.getParentType().equals(IexportType.AppWidgetDefinition)) {
            List<FormDefinition> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), FormDefinition.class);
            for (FormDefinition formDefinition : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + protoDataHql.getParams().get("dependencyUuid");
                this.putParentMap(map, formDefinition, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.AppFunction)) {
            List<FormDefinition> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), FormDefinition.class);
            for (FormDefinition formDefinition : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + formDefinition.getUuid();
                this.putParentMap(map, formDefinition, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.FormDefinition)) {
            if (protoDataHql.getParams().get("uuids") != null) {
                StringBuilder hql = new StringBuilder("from FormDefinition where ");
                HqlUtils.appendSql("uuid", protoDataHql.getParams(), hql, (Set<Serializable>) protoDataHql.getParams().get("uuids"));
                List<FormDefinition> list = this.dao.find(hql.toString(), protoDataHql.getParams(), FormDefinition.class);
                for (FormDefinition formDefinition : list) {
                    String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + "sub" + Separator.UNDERLINE.getValue() + formDefinition.getUuid();
                    this.putParentMap(map, formDefinition, key);
                }
            }
            if (protoDataHql.getParams().get("pFormUuids") != null) {
                StringBuilder hql = new StringBuilder("from FormDefinition where formType in ('M','V') and ");
                HqlUtils.appendSql("pFormUuid", protoDataHql.getParams(), hql, (Set<Serializable>) protoDataHql.getParams().get("pFormUuids"));
                List<FormDefinition> list = this.dao.find(hql.toString(), protoDataHql.getParams(), FormDefinition.class);
                for (FormDefinition formDefinition : list) {
                    String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + "pform" + Separator.UNDERLINE.getValue() + formDefinition.getpFormUuid();
                    this.putParentMap(map, formDefinition, key);
                }
            }

        } else if (protoDataHql.getParentType().equals(IexportType.FlowDefinition)) {
            List<FormDefinition> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), FormDefinition.class);
            for (FormDefinition formDefinition : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + "form" + Separator.UNDERLINE.getValue() + formDefinition.getUuid();
                this.putParentMap(map, formDefinition, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.BizItemDefinition)) {
            String hql = "from FormDefinition t where t.id in(:uuids)";
            List<FormDefinition> list = this.dao.find(hql, protoDataHql.getParams(), FormDefinition.class);
            for (FormDefinition formDefinition : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + "form" + Separator.UNDERLINE.getValue() + formDefinition.getId();
                this.putParentMap(map, formDefinition, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.BizProcessDefinition)) {
            List<FormDefinition> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), FormDefinition.class);
            for (FormDefinition formDefinition : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + "form" + Separator.UNDERLINE.getValue() + formDefinition.getUuid();
                this.putParentMap(map, formDefinition, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;
    }


}
