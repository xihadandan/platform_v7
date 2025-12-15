/*
 * @(#)2012-10-30 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.data.dao;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.data.enums.EnumFormFilterCondition;
import com.wellsoft.pt.dyform.implement.data.exceptions.SaveDataException;
import com.wellsoft.pt.dyform.implement.data.utils.FormDataHandler;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig.EnumFieldPropertyName;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinition;
import com.wellsoft.pt.dyform.implement.definition.enums.EnumRelationTblSystemField;
import com.wellsoft.pt.dyform.implement.definition.enums.EnumSystemField;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.FormDefinitionHandler;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.jpa.hibernate.HibernateDao;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.repository.dao.DbTableDao;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.type.Type;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

/**
 * Description: 对应FormDefinition的Dao操作
 *
 * @author jiangmb
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * </pre>
 * @date 2012-12-21
 */
@Repository
public class FormDataDao extends HibernateDao {

    @Autowired
    DbTableDao dbTableDao;

    @Autowired
    DyFormFacade dyFormFacade;

    @Autowired
    OrgApiFacade orgApiFacade;

    @Autowired
    NativeDao nativeDao;

    public static Map<String/* 字段名 */, Object/* 字段值 */> setKeyAsLowerCase(Map<String, Object> formData) {
        if (formData == null) {
            return null;
        }
        Map<String/* 字段名 */, Object/* 字段值 */> map = new HashMap<String, Object>();
        Iterator<String> it = formData.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            map.put(key.toLowerCase(), formData.get(key));
        }
        return map;
    }

    public static void setMemberAsLowerCase(Set<String> set) {
        Set<String> newSet = new HashSet<String>();
        for (String m : set) {
            newSet.add(m.toLowerCase());
        }

        set.clear();
        set.addAll(newSet);
    }

    private static void setDateValue4SqlQuery(SQLQuery sqlquery, int i, Object valueObj) {
        if (valueObj instanceof Timestamp) {
            sqlquery.setTimestamp(i, (Timestamp) valueObj);
        } else if (valueObj instanceof Date) {
            sqlquery.setTimestamp(i, (Date) valueObj);
        } else if (valueObj instanceof java.sql.Date) {
            sqlquery.setTimestamp(i, (java.sql.Date) valueObj);
        } else {
            sqlquery.setString(i, (String) valueObj);
        }
    }

    /**
     * 根据指定的字段名查询指定的表单表
     *
     * @param tblName
     * @param fieldName
     * @param fieldValue
     * @return
     * @throws Exception
     */
    public List<String> queryFormDataList(String tblName, String fieldName) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("tableName", tblName);
        params.put("fieldName", fieldName);
        return (List) nativeDao.namedQuery("formDataListQuery", params, String.class, null);
    }

    /**
     * 根据指定的字段名及值查询指定的表单表
     *
     * @param tblName
     * @param fieldName
     * @param fieldValue
     * @param j
     * @param beginPosition
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryFormDataList(String tblName, String fieldName, String fieldValue,
                                                       int beginPosition, int count) throws Exception {
        Map<String, Object> params = Maps.newHashMap();
        params.put("tableName", tblName);
        params.put("fieldName", fieldName);
        params.put("fieldValue", fieldValue);
        params.put("orderBy", "order by " + fieldName);
        return (List) nativeDao.namedQuery("formDataQuery", params, HashMap.class,
                new PagingInfo(beginPosition, count, false));
    }

    public List<Map<String, Object>> queryFormDataList(String tblName, String[] fieldName, String[] fieldValue,
                                                       int beginPosition, int count) throws Exception {
        Map<String, Object> params = Maps.newHashMap();
        params.put("tableName", tblName);
        params.put("fieldName", fieldName);
        params.put("fieldValue", fieldValue);
        return (List) nativeDao.namedQuery("formDataQueryMultiField", params, HashMap.class,
                new PagingInfo(beginPosition, count, false));
    }

    /**
     * 根据指定的字段名及值查询指定的表单表除了uuid指定的记录以外的记录
     *
     * @param tblName
     * @param fieldName
     * @param fieldValue
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> queryFormDataList(String uuid, String tblName, String fieldName, String fieldValue,
                                                       int beginPosition, int count) throws Exception {
        Map<String, Object> params = Maps.newHashMap();
        params.put("tableName", tblName);
        params.put("fieldName", fieldName);
        params.put("fieldValue", fieldValue);
        params.put("uuidExclude", uuid);
        params.put("orderBy", "order by " + fieldName);
        return (List) nativeDao.namedQuery("formDataQuery", params, HashMap.class,
                new PagingInfo(beginPosition, count, false));
    }

    public List<Map<String, Object>> queryFormDataList(String uuid, String tblName, String[] fieldName,
                                                       String[] fieldValue, int beginPosition, int count) throws Exception {
        Map<String, Object> params = Maps.newHashMap();
        params.put("tableName", tblName);
        params.put("fieldName", fieldName);
        params.put("fieldValue", fieldValue);
        params.put("uuidExclude", uuid);
        return (List) nativeDao.namedQuery("formDataQueryMultiField", params, HashMap.class,
                new PagingInfo(beginPosition, count, false));
    }

    /**
     * @param tblName  数据目的表
     * @param formData 数据
     * @param string
     * @throws JSONException
     * @throws ParseException
     */
    public void save(FormDefinition definition, Map<String/* 字段名 */, Object/* 字段值 */> formData)
            throws JSONException, ParseException {
        formData = setKeyAsLowerCase(formData);// 将值map中的key设置为小写
        FormDefinitionHandler dUtils = definition.doGetFormDefinitionHandler();
        StringBuilder columns = new StringBuilder("");
        StringBuilder values = new StringBuilder("");
        // columns.append("creator,create_time,modifier,modify_time");
        formData.put("creator", SpringSecurityUtils.getCurrentUserId());
        formData.put("create_time", Calendar.getInstance().getTime());
        formData.put("modifier", SpringSecurityUtils.getCurrentUserId());
        formData.put("modify_time", Calendar.getInstance().getTime());
        formData.put(EnumSystemField.system_unit_id.getName(), SpringSecurityUtils.getCurrentUserUnitId());
        formData.put("rec_ver", "0");
        formData.put(EnumSystemField.status.name(),
                FormDataHandler.getValueFromMap(EnumSystemField.status.name(), formData));
        if (StringUtils.isNotBlank(definition.getDefinitionVjson())) {// 新版表单设计
            formData.put(EnumSystemField.tenant.name(), SpringSecurityUtils.getCurrentTenantId());
            formData.put(EnumSystemField.system.name(), RequestSystemContextPathResolver.system());
        }
        // columns.append("creator,create_time,modifier,modify_time");
        // values.append("?").append(",").append("?").append(",").append("?").append(",").append("?");
        // values.append("?").append(",").append("?").append(",").append("?").append(",").append("?");
        List<String> fieldNames = new ArrayList<String>();
        /*
         * fieldNames.add("creator"); fieldNames.add("create_time");
         * fieldNames.add("modifier"); fieldNames.add("modify_time");
         * fieldNames.add("rec_ver");
         */
        Iterator<String> it = formData.keySet().iterator();
        Map<String, Object> fileNameMap = new HashMap<String, Object>();
        boolean isVOMform = dUtils.isFormTypeAsVform() || dUtils.isFormTypeAsMform();

        while (it.hasNext()) {
            String fieldName = it.next();

            Object obj = fileNameMap.get(fieldName.toLowerCase());
            if (obj != null) {
                continue;
            } else {
                fileNameMap.put(fieldName.toLowerCase(), new Object());
            }
            boolean isFieldInDef = dUtils.isFieldInDefinition(fieldName);

            if (isVOMform && definition.getpFormUuid() != null && !dUtils.isFieldInPform(fieldName)) {// 展现单据: 只涉及存储字段
                isFieldInDef = false;
            }
            if (!(isFieldInDef || FormDefinitionHandler.isSysTypeAsSystem(fieldName))) {// 非表单字段,
                // 非存储字段
                continue;
            } else if (isFieldInDef && dUtils.isInputModeEqAttach(fieldName)) {// 附件字段
                // 是否保存文件名称
                Boolean saveFileName2Field = dUtils.getFieldPropertyOfBooleanType(fieldName,
                        EnumFieldPropertyName.saveFileName2Field);
                if (!(saveFileName2Field != null && saveFileName2Field)) {
                    continue;
                }
            }
            if (columns.length() == 0) {
                columns.append(fieldName);
                values.append("?");
            } else {
                columns.append(",").append(fieldName);
                values.append(",?");
            }
            // columns.append(",").append(fieldName);

            fieldNames.add(fieldName);
        }

        String sql = "insert into " + definition.doGetTblNameOfpForm() + " (" + columns.toString() + ") values ("
                + values + ")";

        Session session = this.getSessionFactory().getCurrentSession();

        SQLQuery sqlquery = session.createSQLQuery(sql);

        /*
         * sqlquery.setString(0, SpringSecurityUtils.getCurrentUserId());
         * sqlquery.setTimestamp(1, Calendar.getInstance().getTime());
         * sqlquery.setString(2, SpringSecurityUtils.getCurrentUserId());
         * sqlquery.setTimestamp(3, Calendar.getInstance().getTime());
         */
        // String parent_id = (String) formData.get("parent_id");
        // sqlquery.setString(4, parent_id);
        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            Object valueObj = FormDataHandler.getValueFromMap(fieldName, formData);

            if (!dUtils.isFieldInDefinition(fieldName)) {// 在定义中没有该字段,默认为字段串类型

                setDateValue4SqlQuery(sqlquery, i, valueObj);

                continue;
            }
            String dbDataType = dUtils.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.dbDataType);
            if (dUtils.isDbDataTypeEqDate(fieldName)) {
                valueObj = FormDataHandler.convertData2DbType(dbDataType, valueObj, null,
                        dUtils.getDateTimePatternByFieldName(fieldName));
            } else {
                valueObj = FormDataHandler.convertData2DbType(dbDataType, valueObj);
            }

            if (dUtils.isDbDataTypeEqDate(fieldName)) {
                setDateValue4SqlQuery(sqlquery, i, valueObj);
                continue;
            } else if (dUtils.isInputModeEqNumber(fieldName)) {
                Type type = org.hibernate.type.IntegerType.INSTANCE;
                if (dUtils.isDbDataTypeEqLong(fieldName)) {
                    type = org.hibernate.type.LongType.INSTANCE;
                } else if (dUtils.isDbDataTypeEqInt(fieldName)) {
                    type = org.hibernate.type.IntegerType.INSTANCE;
                } else if (dUtils.isDbDataTypeEqFloat(fieldName)) {
                    type = org.hibernate.type.FloatType.INSTANCE;
                } else if (dUtils.isDbDataTypeEqDouble(fieldName)) {
                    type = org.hibernate.type.DoubleType.INSTANCE;
                } else if (dUtils.isDbDataTypeEqNumber(fieldName)) {
                    type = org.hibernate.type.BigDecimalType.INSTANCE;
                    if (valueObj != null && StringUtils.isNotBlank(String.valueOf(valueObj))) {
                        valueObj = new BigDecimal(String.valueOf(valueObj));
                    }

                }
                if (dUtils.isDbDataTypeEqNumber(fieldName) && valueObj != null) {
                    sqlquery.setParameter(i, valueObj == null ? null : new BigDecimal(String.valueOf(valueObj)), type);
                } else {
                    sqlquery.setParameter(i, valueObj, type);
                }
                continue;
            } else if (dUtils.isValueAsStringInputMode(fieldName)) {// 文本字段
                sqlquery.setString(i, valueObj != null ? valueObj.toString() : null);
                continue;
            }
            try {
                sqlquery.setString(i, valueObj != null ? valueObj.toString() : "");
            } catch (Exception e) {
                logger.error(fieldName + ":" + valueObj, e);
                throw new SaveDataException(fieldName + ":" + valueObj, e);
            }

        }

        sqlquery.executeUpdate();

        // 保存数据关系
        if (StringUtils.isBlank(definition.doGetRelationTblNameOfpForm()) || (StringUtils
                .isBlank((String) FormDataHandler.getValueFromMap(EnumRelationTblSystemField.mainform_data_uuid.name(),
                        formData))// 不是从表关系
                && StringUtils.isBlank((String) FormDataHandler
                .getValueFromMap(EnumRelationTblSystemField.parent_uuid.name(), formData))// 不是父子节点关系
        )) {
            return;
        }
        StringBuilder relatationColumns = new StringBuilder("");
        StringBuilder relatationValues = new StringBuilder("");
        relatationColumns.append(EnumRelationTblSystemField.create_time.name()).append(",")
                .append(EnumRelationTblSystemField.creator.name()).append(",")
                .append(EnumRelationTblSystemField.data_uuid.name()).append(",")
                .append(EnumRelationTblSystemField.mainform_data_uuid.name()).append(",")
                .append(EnumRelationTblSystemField.mainform_form_uuid.name()).append(",")
                .append(EnumRelationTblSystemField.modifier.name()).append(",")
                .append(EnumRelationTblSystemField.modify_time.name()).append(",")
                .append(EnumRelationTblSystemField.parent_uuid.name()).append(",")
                .append(EnumRelationTblSystemField.rec_ver.name()).append(",")
                .append(EnumRelationTblSystemField.sort_order.name()).append(",")
                .append(EnumRelationTblSystemField.uuid.name());

        relatationValues.append(":" + EnumRelationTblSystemField.create_time.name()).append(",")
                .append(":" + EnumRelationTblSystemField.creator.name()).append(",")
                .append(":" + EnumRelationTblSystemField.data_uuid.name()).append(",")
                .append(":" + EnumRelationTblSystemField.mainform_data_uuid.name()).append(",")
                .append(":" + EnumRelationTblSystemField.mainform_form_uuid.name()).append(",")
                .append(":" + EnumRelationTblSystemField.modifier.name()).append(",")
                .append(":" + EnumRelationTblSystemField.modify_time.name()).append(",")
                .append(":" + EnumRelationTblSystemField.parent_uuid.name()).append(",")
                .append(":" + EnumRelationTblSystemField.rec_ver.name()).append(",")
                .append(":" + EnumRelationTblSystemField.sort_order.name()).append(",")
                .append(":" + EnumRelationTblSystemField.uuid.name());

        String relationSql = "insert into " + definition.doGetRelationTblNameOfpForm() + " ("
                + relatationColumns.toString() + ") values (" + relatationValues.toString() + ")";

        SQLQuery relationSqlquery = session.createSQLQuery(relationSql);

        relationSqlquery.setTimestamp(EnumRelationTblSystemField.create_time.name(), new Date());
        relationSqlquery.setString(EnumRelationTblSystemField.creator.name(), SpringSecurityUtils.getCurrentUserId());
        relationSqlquery.setString(EnumRelationTblSystemField.data_uuid.name(),
                (String) FormDataHandler.getValueFromMap(EnumSystemField.uuid.name(), formData));
        relationSqlquery.setString(EnumRelationTblSystemField.mainform_data_uuid.name(), (String) FormDataHandler
                .getValueFromMap(EnumRelationTblSystemField.mainform_data_uuid.name(), formData));
        relationSqlquery.setString(EnumRelationTblSystemField.mainform_form_uuid.name(), (String) FormDataHandler
                .getValueFromMap(EnumRelationTblSystemField.mainform_form_uuid.name(), formData));
        relationSqlquery.setString(EnumRelationTblSystemField.modifier.name(), SpringSecurityUtils.getCurrentUserId());
        relationSqlquery.setTimestamp(EnumRelationTblSystemField.modify_time.name(), new Date());
        relationSqlquery.setString(EnumRelationTblSystemField.parent_uuid.name(),
                (String) FormDataHandler.getValueFromMap(EnumRelationTblSystemField.parent_uuid.name(), formData));
        relationSqlquery.setInteger(EnumRelationTblSystemField.rec_ver.name(), 0);
        relationSqlquery.setInteger(EnumRelationTblSystemField.sort_order.name(), Integer.parseInt((String) FormDataHandler.getValueFromMap(EnumRelationTblSystemField.sort_order.name(), formData)));
        relationSqlquery.setString(EnumRelationTblSystemField.uuid.name(), dyFormFacade.createUuid());
        relationSqlquery.executeUpdate();
    }

    public void update(FormDefinition dyFormFormDefinition, Map<String, Object> formData) throws JSONException {

        if (formData == null || formData.size() == 0) {
            return;
        }
        formData = setKeyAsLowerCase(formData);// 将值map中的key设置为小写
        String uuid = (String) formData.get(EnumSystemField.uuid.name());
        StringBuilder sql = new StringBuilder();
        Object statusValue = FormDataHandler.getValueFromMap(EnumSystemField.status.name(), formData);
        sql.append("update " + dyFormFormDefinition.doGetTblNameOfpForm() + " set modifier = ? ,modify_time = ? ");
        int increment = 2;
        if (statusValue != null) {
            increment = 3;
            sql.append(", status = ?");
        }
        List<String> fieldNames = new ArrayList<String>();
        Iterator<String> fieldNamesIt = formData.keySet().iterator();
        StringBuilder clobSql = new StringBuilder();
        List<String> clobFieldNames = new ArrayList<String>();
        FormDefinitionHandler formDefinitionHandler = dyFormFormDefinition.doGetFormDefinitionHandler();
        boolean isVOMform = formDefinitionHandler.isFormTypeAsVform() || formDefinitionHandler.isFormTypeAsMform();
        while (fieldNamesIt.hasNext()) {
            String fieldName = fieldNamesIt.next();
            if (isVOMform && !formDefinitionHandler.useDataModel() && !formDefinitionHandler.isFieldInPform(fieldName)) {// 展现单据:
                // 只涉及存储字段
                continue;
            }
            if (!formDefinitionHandler.isFieldInDefinition(fieldName)) {
                continue;
            } else if (formDefinitionHandler.isInputModeEqAttach(fieldName)) {
                /* add by zhangyh 20170411 begin */
                // 是否保存文件名称
                Boolean saveFileName2Field = formDefinitionHandler.getFieldPropertyOfBooleanType(fieldName,
                        EnumFieldPropertyName.saveFileName2Field);
                if (!(saveFileName2Field != null && saveFileName2Field)) {
                    continue;
                }
                /* add by zhangyh 20170411 end */
            }
            if (formDefinitionHandler.isDbDataTypeEqClob(fieldName)
                    || formDefinitionHandler.isDbDataTypeEqLong(fieldName)) {
                // 大字段要放置在最后设置，否则oracle会报错
                // ORA-24816: 在实际的 LONG 或 LOB 列之后提供了扩展的非 LONG 绑定数据
                clobSql.append(",");
                clobSql.append(fieldName);
                clobSql.append("=");
                clobSql.append("?");
                clobFieldNames.add(fieldName);
            } else {
                sql.append(",");
                sql.append(fieldName);
                sql.append("=");
                sql.append("?");
                fieldNames.add(fieldName);
            }

        }
        if (clobFieldNames.size() > 0) {
            fieldNames.addAll(clobFieldNames);
            sql.append(clobSql);
        }

        sql.append(" where uuid = ?");

        Session session = this.getSessionFactory().getCurrentSession();

        SQLQuery sqlquery = session.createSQLQuery(sql.toString());
        sqlquery.setString(0, SpringSecurityUtils.getCurrentUserId());
        sqlquery.setTimestamp(1, Calendar.getInstance().getTime());
        if (statusValue != null) {
            sqlquery.setString(2, (String) statusValue);
        }
        Object valueObj = null;
        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);

            if (!formDefinitionHandler.isFieldInDefinition(fieldName)) {// 只对用户定义的字段进行更新
                continue;
            }

            valueObj = FormDataHandler.getValueFromMap(fieldName, formData);

            if (formDefinitionHandler.isInputModeEqDate(fieldName)) {
                setDateValue4SqlQuery(sqlquery, i + increment, valueObj);
                continue;
            } else if (formDefinitionHandler.isInputModeEqNumber(fieldName)) {
                Type type = org.hibernate.type.IntegerType.INSTANCE;
                if (formDefinitionHandler.isDbDataTypeEqLong(fieldName)) {
                    type = org.hibernate.type.LongType.INSTANCE;
                } else if (formDefinitionHandler.isDbDataTypeEqInt(fieldName)) {
                    type = org.hibernate.type.IntegerType.INSTANCE;
                } else if (formDefinitionHandler.isDbDataTypeEqFloat(fieldName)) {
                    type = org.hibernate.type.FloatType.INSTANCE;
                } else if (formDefinitionHandler.isDbDataTypeEqDouble(fieldName)) {
                    type = org.hibernate.type.DoubleType.INSTANCE;
                } else if (formDefinitionHandler.isDbDataTypeEqNumber(fieldName)) {
                    type = org.hibernate.type.BigDecimalType.INSTANCE;
                    if (valueObj != null && StringUtils.isNotBlank(String.valueOf(valueObj))) {
                        valueObj = new BigDecimal(String.valueOf(valueObj));
                    }
                }
                if (valueObj instanceof String) {
                    if (formDefinitionHandler.isDbDataTypeEqLong(fieldName)) {
                        sqlquery.setParameter(i + increment, Long.valueOf(String.valueOf(valueObj)), type);
                    } else if (formDefinitionHandler.isDbDataTypeEqInt(fieldName)) {
                        sqlquery.setParameter(i + increment, Integer.valueOf(String.valueOf(valueObj)), type);
                    } else if (formDefinitionHandler.isDbDataTypeEqFloat(fieldName)) {
                        sqlquery.setParameter(i + increment, Float.valueOf(String.valueOf(valueObj)), type);
                    } else if (formDefinitionHandler.isDbDataTypeEqDouble(fieldName)) {
                        sqlquery.setParameter(i + increment, Double.valueOf(String.valueOf(valueObj)), type);
                    } else if (formDefinitionHandler.isDbDataTypeEqNumber(fieldName)) {
                        sqlquery.setParameter(i + increment, new BigDecimal(String.valueOf(valueObj)), type);
                    }
                } else {
                    sqlquery.setParameter(i + increment, valueObj, type);
                }

                continue;
            }

            sqlquery.setString(i + increment, valueObj == null ? null : valueObj.toString());

        }
        sqlquery.setString(increment + fieldNames.size(), uuid);
        if (fieldNames.size() == 0 && increment == 2) {
        } else {
            sqlquery.executeUpdate();
        }

        // 获得关联关系表
        String relationTable = dyFormFormDefinition.doGetRelationTblNameOfpForm();
        // 保存数据关系
        if (StringUtils.isBlank(relationTable)
                || (formData.get(EnumRelationTblSystemField.mainform_data_uuid.name()) == null// 不是从表关系
                && formData.get(EnumRelationTblSystemField.parent_uuid.name()) == null // 不是父子节点关系
        )) {
            return;
        }

        // 判读关联关系表中是否有数据，如果没有的话，插入数据，如果有的话，更新数据
        String mainform_data_uuid = (String) (FormDataHandler
                .getValueFromMap(EnumRelationTblSystemField.mainform_data_uuid.name(), formData));
        String uluuid = (String) (FormDataHandler.getValueFromMap(EnumRelationTblSystemField.uuid.name(), formData));

        String relSql = "select * from " + relationTable + " where mainform_data_uuid ='" + mainform_data_uuid
                + "' and " + "data_uuid = '" + uluuid + "'";

        SQLQuery relQuery = session.createSQLQuery(relSql);
        List relList = relQuery.list();
        if (relList.size() == 0) {// 添加主从表数据关系
            this.addSubRelation(relationTable, SpringSecurityUtils.getCurrentUserId(), new Date(),
                    SpringSecurityUtils.getCurrentUserId(), new Date(), uluuid,
                    (String) FormDataHandler.getValueFromMap(EnumRelationTblSystemField.mainform_form_uuid.name(),
                            formData),
                    (String) FormDataHandler.getValueFromMap(EnumRelationTblSystemField.mainform_data_uuid.name(),
                            formData),
                    (String) FormDataHandler.getValueFromMap(EnumRelationTblSystemField.parent_uuid.name(), formData),
                    0, (String) FormDataHandler.getValueFromMap(EnumRelationTblSystemField.sort_order.name(), formData),
                    dyFormFacade.createUuid());
        } else {// 更新排序顺序
            this.updateSortOrder(relationTable, mainform_data_uuid, uluuid, SpringSecurityUtils.getCurrentUserId(),
                    new Date(),
                    (String) FormDataHandler.getValueFromMap(EnumRelationTblSystemField.sort_order.name(), formData));
        }

    }

    /**
     * 添加主表从表映射关系
     *
     * @param relTblName
     * @param creator
     * @param createTime
     * @param modifier
     * @param modifyTime
     * @param dataUuid
     * @param mainformFormUuid
     * @param mainformDataUuid
     * @param parentDataUuid
     * @param recVer
     * @param sortOrder
     * @param uuid
     */
    public void addSubRelation(String relTblName, String creator, Date createTime, String modifier, Date modifyTime,
                               String dataUuid, String mainformFormUuid, String mainformDataUuid, String parentDataUuid, int recVer,
                               String sortOrder, String uuid) {

        StringBuilder insrelatationColumns = new StringBuilder("");
        StringBuilder insrelatationValues = new StringBuilder("");
        insrelatationColumns.append(EnumRelationTblSystemField.create_time.name()).append(",")
                .append(EnumRelationTblSystemField.creator.name()).append(",")
                .append(EnumRelationTblSystemField.data_uuid.name()).append(",")
                .append(EnumRelationTblSystemField.mainform_data_uuid.name()).append(",")
                .append(EnumRelationTblSystemField.mainform_form_uuid.name()).append(",")
                .append(EnumRelationTblSystemField.modifier.name()).append(",")
                .append(EnumRelationTblSystemField.modify_time.name()).append(",")
                .append(EnumRelationTblSystemField.parent_uuid.name()).append(",")
                .append(EnumRelationTblSystemField.rec_ver.name()).append(",")
                .append(EnumRelationTblSystemField.sort_order.name()).append(",")
                .append(EnumRelationTblSystemField.uuid.name());

        insrelatationValues.append(":" + EnumRelationTblSystemField.create_time.name()).append(",")
                .append(":" + EnumRelationTblSystemField.creator.name()).append(",")
                .append(":" + EnumRelationTblSystemField.data_uuid.name()).append(",")
                .append(":" + EnumRelationTblSystemField.mainform_data_uuid.name()).append(",")
                .append(":" + EnumRelationTblSystemField.mainform_form_uuid.name()).append(",")
                .append(":" + EnumRelationTblSystemField.modifier.name()).append(",")
                .append(":" + EnumRelationTblSystemField.modify_time.name()).append(",")
                .append(":" + EnumRelationTblSystemField.parent_uuid.name()).append(",")
                .append(":" + EnumRelationTblSystemField.rec_ver.name()).append(",")
                .append(":" + EnumRelationTblSystemField.sort_order.name()).append(",")
                .append(":" + EnumRelationTblSystemField.uuid.name());

        String relationSql = "insert into " + relTblName + " (" + insrelatationColumns.toString() + ") values ("
                + insrelatationValues.toString() + ")";

        Session session = this.getSessionFactory().getCurrentSession();
        SQLQuery relationSqlquery = session.createSQLQuery(relationSql);

        relationSqlquery.setTimestamp(EnumRelationTblSystemField.create_time.name(), createTime);
        relationSqlquery.setString(EnumRelationTblSystemField.creator.name(), creator);
        relationSqlquery.setString(EnumRelationTblSystemField.data_uuid.name(), dataUuid);
        relationSqlquery.setString(EnumRelationTblSystemField.mainform_data_uuid.name(), mainformDataUuid);
        relationSqlquery.setString(EnumRelationTblSystemField.mainform_form_uuid.name(), mainformFormUuid);
        relationSqlquery.setString(EnumRelationTblSystemField.modifier.name(), modifier);
        relationSqlquery.setTimestamp(EnumRelationTblSystemField.modify_time.name(), modifyTime);
        relationSqlquery.setString(EnumRelationTblSystemField.parent_uuid.name(), parentDataUuid);
        relationSqlquery.setInteger(EnumRelationTblSystemField.rec_ver.name(), recVer);
        relationSqlquery.setInteger(EnumRelationTblSystemField.sort_order.name(), sortOrder == null ? 0 : Integer.parseInt(sortOrder));
        relationSqlquery.setString(EnumRelationTblSystemField.uuid.name(), uuid);
        relationSqlquery.executeUpdate();

    }

    /**
     * 更新从表数据的排序顺序
     *
     * @param relTblName
     * @param mainDataUuid
     * @param dataUuid
     * @param modifier
     * @param string
     * @param modifyTime
     * @param sortOrder
     */
    public void updateSortOrder(String relTblName, String mainDataUuid, String dataUuid, String modifier,
                                Date modifyTime, String sortOrder) {
        Session session = this.getSessionFactory().getCurrentSession();
        StringBuilder relatationColumns = new StringBuilder("");
        relatationColumns.append(EnumRelationTblSystemField.modifier.name()).append(" = :")
                .append(EnumRelationTblSystemField.modifier.name()).append(",")
                .append(EnumRelationTblSystemField.modify_time.name()).append(" = :")
                .append(EnumRelationTblSystemField.modify_time.name());

        String seqNO = sortOrder;
        if (seqNO != null && seqNO.trim().length() > 0) {
            relatationColumns.append(",").append(EnumRelationTblSystemField.sort_order.name()).append(" = :")
                    .append(EnumRelationTblSystemField.sort_order.name()).append("");
        }

        // .append(EnumRelationTblSystemField.uuid.name()).append(" = :")
        // .append(EnumRelationTblSystemField.uuid.name());

        String relationSql = "update " + relTblName + "  set " + relatationColumns.toString() + " where "
                + EnumRelationTblSystemField.mainform_data_uuid + " = :"
                + EnumRelationTblSystemField.mainform_data_uuid.name() + " and "
                + EnumRelationTblSystemField.data_uuid.name() + " = :" + EnumRelationTblSystemField.data_uuid.name();

        SQLQuery relationSqlquery = session.createSQLQuery(relationSql);
        relationSqlquery.setString(EnumRelationTblSystemField.mainform_data_uuid.name(), mainDataUuid);
        relationSqlquery.setString(EnumRelationTblSystemField.data_uuid.name(), dataUuid);

        relationSqlquery.setString(EnumRelationTblSystemField.modifier.name(), modifier);
        relationSqlquery.setTimestamp(EnumRelationTblSystemField.modify_time.name(), modifyTime);

        if (seqNO != null && seqNO.trim().length() > 0) {
            relationSqlquery.setInteger(EnumRelationTblSystemField.sort_order.name(), Integer.parseInt(seqNO));
        }

        relationSqlquery.executeUpdate();
    }

    /**
     * 删除主表与子表关系<br/>
     * 删除父子关系
     *
     * @param tblNameOfMainform
     * @param tblNameOfSubform
     * @param dataUuids
     * @param dataUuidOfMainform
     */
    public void deleteSubformFormMainform(String relationTblNameOfSubform, List<String> dataUuids,
                                          String dataUuidOfMainform) {
        if (dataUuids == null || dataUuids.size() == 0
                || org.apache.commons.lang.StringUtils.isBlank(dataUuidOfMainform)) {
            return;
        }

        Session session = this.getSessionFactory().getCurrentSession();
        for (String dataUuid : dataUuids) {
            StringBuilder sql = new StringBuilder();
            sql.append("delete from  " + relationTblNameOfSubform + "  ");
            sql.append(" where  ");
            sql.append(EnumRelationTblSystemField.data_uuid.name());
            sql.append("= '");
            sql.append(dataUuid);
            sql.append("' and ");
            sql.append(EnumRelationTblSystemField.mainform_data_uuid.name());
            sql.append("= '");
            sql.append(dataUuidOfMainform);
            sql.append("'");
            SQLQuery sqlquery = session.createSQLQuery(sql.toString());
            sqlquery.executeUpdate();
        }

    }

    /**
     * 判断当前从表数据是否存在和其他主表有关联（处理多主表关联同一从表数据），存在则不删除从表数据
     *
     * @param relationTblName    关系表表名
     * @param subDataUuid        从表数据UUID
     * @param formUuidOfMainform 主表FORMUUID
     * @return
     */
    public boolean isExistOtherRelaction(String relationTblName, String subDataUuid, String formUuidOfMainform) {
        Session session = this.getSessionFactory().getCurrentSession();
        StringBuffer sql = new StringBuffer();
        sql.append("select * from  " + relationTblName + "  ");
        sql.append(" where  ");
        sql.append(EnumRelationTblSystemField.data_uuid.name());
        sql.append("= '");
        sql.append(subDataUuid);
        sql.append("' and ");
        sql.append(EnumRelationTblSystemField.mainform_form_uuid);
        sql.append(" != '");
        sql.append(formUuidOfMainform);
        sql.append("'");
        SQLQuery sqlquery = session.createSQLQuery(sql.toString());
        if (sqlquery.list().size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void delFormData(String tblName, String relationTblName, String dataUuid, String formUuid) {

        Session session = this.getSessionFactory().getCurrentSession();

        // 删除与主表的关系数据
        StringBuilder sql = new StringBuilder();
        sql.append("delete from  " + relationTblName + "  ");
        sql.append(" where  ");
        sql.append(EnumRelationTblSystemField.data_uuid.name());
        sql.append("= '");
        sql.append(dataUuid);
        /*
         * sql.append("' and ");
         * sql.append(EnumRelationTblSystemField.mainform_form_uuid.name());
         * sql.append("= '"); sql.append(formUuid);
         */
        sql.append("'");
        SQLQuery sqlquery = session.createSQLQuery(sql.toString());
        sqlquery.executeUpdate();

        // 删除主表数据
        sql = new StringBuilder();
        sql.append("delete from  " + tblName + "  ");
        sql.append(" where  ");
        sql.append(EnumSystemField.uuid.name());
        sql.append("= '");
        sql.append(dataUuid);
        sql.append("'   ");

        sqlquery = session.createSQLQuery(sql.toString());
        sqlquery.executeUpdate();

    }

    public Integer getMinOrderNo(String subformRlTblName, String mainformFormUuid, String mainformDataUuid,
                                 String subformFormUuid) {
        StringBuilder sql = new StringBuilder();
        String aliasName = "maxorderno";
        sql.append("select min(" + EnumRelationTblSystemField.sort_order.name() + ") " + aliasName + " from "
                + subformRlTblName + "   ");
        sql.append(" where ");
        sql.append(EnumRelationTblSystemField.mainform_form_uuid.name() + " = '" + mainformFormUuid + "'");
        sql.append(" and ");
        sql.append(EnumRelationTblSystemField.mainform_data_uuid.name() + " = '" + mainformDataUuid + "'");
        // sql.append(" and ");
        // sql.append(EnumRelationTblSystemField..name() + " = '" +
        // mainformDataUuid + "'");
        try {
            List<Map<String, Object>> mapList = dbTableDao.query(sql.toString());
            if (mapList != null && mapList.size() == 1) {
                return Integer.parseInt(mapList.get(0).get(aliasName).toString());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public Integer getMaxOrderNo(String subformRlTblName, String mainformFormUuid, String mainformDataUuid,
                                 String subformFormUuid) {
        StringBuilder sql = new StringBuilder();
        String aliasName = "minorderno";
        sql.append("select max(" + EnumRelationTblSystemField.sort_order.name() + ") " + aliasName + " from "
                + subformRlTblName + "   ");
        sql.append(" where ");
        sql.append(EnumRelationTblSystemField.mainform_form_uuid.name() + " = '" + mainformFormUuid + "'");
        sql.append(" and ");
        sql.append(EnumRelationTblSystemField.mainform_data_uuid.name() + " = '" + mainformDataUuid + "'");
        // sql.append(" and ");
        // sql.append(EnumRelationTblSystemField..name() + " = '" +
        // mainformDataUuid + "'");
        try {
            List<Map<String, Object>> mapList = dbTableDao.query(sql.toString());
            if (mapList != null && mapList.size() == 1) {
                return Integer.parseInt(mapList.get(0).get(aliasName).toString());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    // add by wujx 20161019
    public List<Map<String, Object>> getMainFormInfoBySubDataUuid(String relationTblName, String subDataUuid)
            throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append("select * from  " + relationTblName + "  ");
        sql.append(" where  ");
        sql.append(EnumRelationTblSystemField.data_uuid.name());
        sql.append("= '");
        sql.append(subDataUuid);
        sql.append("' and ");
        sql.append(EnumRelationTblSystemField.mainform_data_uuid.getName());
        sql.append(" is not null and ");
        sql.append(EnumRelationTblSystemField.mainform_form_uuid.getName());
        sql.append(" is not null");
        return dbTableDao.query(sql.toString());
    }

    public List<Map<String, Object>> getAllData(String tableName, String formUuid, String where) {
        StringBuffer sql = new StringBuffer();
        sql.append("select * from  " + tableName + "  ");
        sql.append(" where  ");
        sql.append(EnumSystemField.form_uuid.name());
        sql.append("= '");
        sql.append(formUuid);
        sql.append("' and ");
        sql.append(where);
        try {
            return dbTableDao.query(sql.toString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }

    }

    /**
     * 如何描述该方法
     *
     * @param uuid
     * @param tblNm          表名
     * @param params         唯一性校验组合字段名和字段值
     * @param fiterCondition D:部门  C:当前用户  O:单位
     * @return
     */

    public List getDataByTblNmAndParams(String tblNm, Map<String, Object> params, String fiterCondition, String uuid) {
        StringBuffer sql = new StringBuffer();
        sql.append("select uuid from  " + tblNm + "  ");
        sql.append(" where 1=1  ");
        if (params.size() > 0) {
            for (String key : params.keySet()) {
                sql.append("and " + key + " =:" + key + " ");
            }
        }
        if (StringUtils.isNotBlank(uuid)) {
            params.put("uuid", uuid);
            sql.append("and uuid !=:uuid  ");
        }
        if (EnumFormFilterCondition.DEPARTMENT.getValue().equals(fiterCondition)) {
            String departmentId = SpringSecurityUtils.getCurrentUserDepartmentId();
            HashMap<String, String> usersMap = orgApiFacade.getUsersByOrgIds(departmentId);
            if (usersMap.size() > 0) {
                Set<String> users = usersMap.keySet();
                params.put("users", users);
                sql.append("and creator in (:users) ");
            }
        } else if (EnumFormFilterCondition.CURRENTUSER.getValue().equals(fiterCondition)) {
            String currentUser = SpringSecurityUtils.getCurrentUserId();
            params.put("currentUser", currentUser);
            sql.append("and creator = :currentUser ");
        } else if (EnumFormFilterCondition.ORG.getValue().equals(fiterCondition)) {
            String currentUser = SpringSecurityUtils.getCurrentUserId();
            // Set<String> orgIdSet = orgApiFacade.getUserOrgIds(currentUser);
            // if (orgIdSet.size() > 0) {
            String unitId = SpringSecurityUtils.getCurrentUserUnitId();
            // List<String> orgIdList = new ArrayList<>(orgIdSet);
            // HashMap<String, String> usersMap = orgApiFacade.getUsersByOrgIds(orgIdList);
            // Set<String> users = usersMap.keySet();
            params.put("unitId", unitId);
            sql.append("and system_unit_id =:unitId");
            // }
        }
        Query query = getSession().createSQLQuery(sql.toString());
        query.setProperties(params);
        return query.list();
    }

    public List<String> queryFormDataList(String tblName, String fieldName, String where, Map<String, Object> namedParams) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("tableName", tblName);
        params.put("fieldName", fieldName);
        params.put("where", where);
        if (namedParams != null) {
            params.putAll(namedParams);
        }
        return (List) nativeDao.namedQuery("formDataListQuery", params, String.class, null);
    }
}
