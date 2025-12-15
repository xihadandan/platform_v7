package com.wellsoft.pt.integration.service.impl;

import com.wellsoft.pt.basicdata.systemtable.facade.service.SystemTableFacadeService;
import com.wellsoft.pt.integration.service.ExchangeDataOpService;
import com.wellsoft.pt.jpa.dao.UniversalDao;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.SimpleValue;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Table;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.sql.Clob;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author ruanhg
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-7-9.1	ruanhg		2014-7-9		Create
 * </pre>
 * @date 2014-7-9
 */
@Service
public class ExchangeDataOpServiceImpl implements ExchangeDataOpService {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SystemTableFacadeService systemTableService;

    @Autowired
    private UniversalDao dao;

    @Override
    @Transactional
    public Boolean backData(String serializeStr) {
        try {

            Object obj = this.deserializationToObject(serializeStr);
            // 得到对象的类
            Class c = obj.getClass();
            String entityName = c.getName();
            // 得到对象中所有的方法
            Method[] methods = c.getMethods();
            // 从类的名字中解析出表名
            Annotation[] anno = obj.getClass().getAnnotations();
            String tableName = "";
            for (int i = 0; i < anno.length; i++) {
                if (anno[i] instanceof Table) {
                    Table table = (Table) anno[i];
                    tableName = table.name();
                }
            }
            // 遍历对象的方法
            Map<String, Method> methodMap = new HashMap<String, Method>();
            for (Method method : methods) {
                methodMap.put(method.getName(), method);
            }
            String mainUuid = methodMap.get("getUuid").invoke(obj, null).toString();
            String insertCollectionSql = "";
            String fieldNames = "";
            String fieldValues = "";
            String upFieldSql = "";
            Map<String, Object> typeMap = new HashMap<String, Object>();
            Map<String, Object> valueMap = new HashMap<String, Object>();
            for (String mName : methodMap.keySet()) {
                Method method = methodMap.get(mName);
                Annotation[] annoMethod = method.getAnnotations();
                String zjName = "";
                for (int i = 0; i < annoMethod.length; i++) {
                    String tempArrzjStr = annoMethod[i].annotationType().toString();
                    String tempArrzjName[] = tempArrzjStr.split("\\.");
                    zjName += tempArrzjName[tempArrzjName.length - 1];
                }
                if (zjName.indexOf("Transient") < 0) {
                    if ((mName.startsWith("is") || mName.startsWith("get")) && !mName.startsWith("getClass")
                            && !mName.equals("getAttach")) {
                        try {
                            String fieldName = "";
                            if (mName.startsWith("is")) {
                                fieldName = mName.substring(2, mName.length());
                            } else {
                                fieldName = mName.substring(3, mName.length());
                            }

                            String dbFieldName = toDbField(fieldName);
                            String[] type_ = method.getReturnType().toString().split("\\.");
                            String type = type_[(type_.length - 1)];
                            Object value = method.invoke(obj, null);
                            if (!type.equals("List") && !type.equals("Set")) {
                                if (!type.equals("String") && !type.equals("Date") && !type.equals("Clob")
                                        && !type.equals("Integer") && !type.equals("int") && !type.equals("Long")
                                        && !type.equals("long") && !type.equals("Boolean") && !type.equals("boolean")
                                        && !type.equals("Double") && !type.equals("double") && !type.equals("float")
                                        && !type.equals("Float")) {// 取对象类型的字段
                                    Map<String, String> map_ = systemTableService.getColumnMap(c,
                                            fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1));
                                    dbFieldName = map_.get("name");
                                }
                                if (dbFieldName != null) {
                                    fieldNames += "," + dbFieldName;
                                    fieldValues += ",:" + dbFieldName;
                                    upFieldSql += "," + dbFieldName + "=:" + dbFieldName;
                                    valueMap.put(dbFieldName, value);
                                    typeMap.put(dbFieldName, method.getReturnType());
                                }
                            } else {// 列表时，处理多对多的关系
                                Map<String, Object> map_ = systemTableService.getColumnPropOfSet(c, fieldName
                                        .substring(0, 1).toLowerCase() + fieldName.substring(1));
                                if (map_ != null && map_.get("oneToMany") != null && !(Boolean) map_.get("oneToMany")) {// 多对多
                                    // 有多对多的关系就删除关系数据
                                    String collectionTableName = map_.get("collectionTableName").toString();
                                    Column[] columns = (Column[]) map_.get("columns");
                                    Column column1 = columns[0];
                                    Column column2 = columns[1];
                                    // 删除对应关系表数据
                                    String mainColumnName = "";
                                    String subColumnName = "";
                                    String delCollectionSql = "delete " + collectionTableName + " d where d.";
                                    if (column2.getValue() instanceof ManyToOne) {
                                        String columnForEntityName = ((SimpleValue) column2.getValue()).getType()
                                                .getName();
                                        if (!StringUtils.isBlank(columnForEntityName)
                                                && entityName.equals(columnForEntityName)) {
                                            // 字段对应的实体=当前实体
                                            mainColumnName = column2.getName();
                                            subColumnName = column1.getName();
                                        } else {
                                            mainColumnName = column1.getName();
                                            subColumnName = column2.getName();
                                        }
                                    } else {
                                        String columnForEntityName = ((SimpleValue) column1.getValue()).getType()
                                                .getName();
                                        if (!StringUtils.isBlank(columnForEntityName)
                                                && entityName.equals(columnForEntityName)) {
                                            // 字段对应的实体=当前实体
                                            mainColumnName = column1.getName();
                                            subColumnName = column2.getName();
                                        } else {
                                            mainColumnName = column2.getName();
                                            subColumnName = column1.getName();
                                        }
                                    }
                                    delCollectionSql += mainColumnName + "='" + mainUuid + "'";
                                    this.dao.getSession().createSQLQuery(delCollectionSql).executeUpdate();
                                    // 当关系表存在时添加关系，关系在hibernate的代理对象中
                                    if (value instanceof PersistentSet) {
                                        PersistentSet ps = (PersistentSet) value;
                                        Set<Object> sets = (Set<Object>) ps.getValue();
                                        for (Object subObj : sets) {
                                            Class subClass = subObj.getClass();
                                            for (Method subMethod : subClass.getMethods()) {
                                                if (subMethod.getName().equals("getUuid")) {
                                                    String subUuidValue = subMethod.invoke(subObj, null).toString();
                                                    insertCollectionSql += "insert into " + collectionTableName + " ("
                                                            + mainColumnName + "," + subColumnName + ") " + "values ('"
                                                            + mainUuid + "','" + subUuidValue + "');";
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            logger.info(e.getMessage());
                        }
                    }
                }
            }
            /************************变更数据*********************************/
            List slist = this.dao.getSession()
                    .createSQLQuery("select uuid from " + tableName + " s where s.uuid='" + mainUuid + "'").list();
            String sql = "";
            if (slist.size() > 0) {
                sql += "update " + tableName + " set " + upFieldSql.replaceFirst(",", "") + " where uuid='" + mainUuid
                        + "'";
            } else {
                sql += "insert into " + tableName + " (" + fieldNames.replaceFirst(",", "") + ") values ("
                        + fieldValues.replaceFirst(",", "") + ")";
            }
            SQLQuery sqlquery = this.dao.getSession().createSQLQuery(sql);
            for (String key_ : valueMap.keySet()) {
                Object fieldObj = valueMap.get(key_);
                String[] type_ = typeMap.get(key_).toString().split("\\.");
                String type = type_[(type_.length - 1)];
                if (type.equals("String")) {
                    sqlquery.setString(key_, fieldObj == null ? "" : fieldObj.toString());
                } else if (type.equals("Clob")) {
                    Clob clob = (Clob) fieldObj;
                    if (clob != null) {
                        sqlquery.setText(key_, IOUtils.toString(clob.getCharacterStream()).toString());
                    } else {
                        sqlquery.setText(key_, "");
                    }
                } else if (type.equals("Date")) {
                    sqlquery.setTimestamp(key_, fieldObj == null ? null : (Date) fieldObj);
                } else if (type.equals("Integer") || type.equals("int")) {
                    sqlquery.setInteger(key_, fieldObj == null ? 0 : Integer.parseInt(fieldObj.toString()));
                } else if (type.equals("Long") || type.equals("long")) {
                    sqlquery.setLong(key_, fieldObj == null ? 0 : Long.parseLong(fieldObj.toString()));
                } else if (type.equals("Float") || type.equals("float")) {
                    sqlquery.setFloat(key_, fieldObj == null ? 0 : Float.parseFloat(fieldObj.toString()));
                } else if (type.equals("Double") || type.equals("double")) {
                    sqlquery.setDouble(key_, fieldObj == null ? 0 : Double.parseDouble(fieldObj.toString()));
                } else if (type.equals("Boolean") || type.equals("boolean")) {
                    sqlquery.setBoolean(key_, fieldObj == null ? false : (Boolean) fieldObj);
                } else {
                    if (fieldObj != null) {
                        Class c_ = fieldObj.getClass();
                        Method[] methods_ = c_.getMethods();
                        for (Method method : methods_) {
                            if (method.getName().equals("getUuid")) {
                                Object value = method.invoke(fieldObj, null);
                                sqlquery.setString(key_, value == null ? "" : value.toString());
                            }
                        }
                    } else {// 对象为空时
                        sqlquery.setString(key_, "");
                    }
                }
            }
            sqlquery.executeUpdate();
            // 先保存实体后插入多对多的关系
            if (!StringUtils.isBlank(insertCollectionSql)) {
                String[] insertCollectionSqlItem = insertCollectionSql.split(";");
                for (int i = 0; i < insertCollectionSqlItem.length; i++) {
                    this.dao.getSession().createSQLQuery(insertCollectionSqlItem[i]).executeUpdate();
                }
            }
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.info(e.getMessage());
            return false;
        }
    }

    @Override
    public String serializeEntityToString(Object obj) {
        // TODO Auto-generated method stub
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream;
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            String serStr = byteArrayOutputStream.toString("ISO-8859-1");
            serStr = java.net.URLEncoder.encode(serStr, "UTF-8");
            objectOutputStream.close();
            byteArrayOutputStream.close();
            return serStr;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.info(e.getMessage());
            return "";
        }
    }

    public Object deserializationToObject(String serStr) {
        // TODO Auto-generated method stub
        try {
            String redStr = java.net.URLDecoder.decode(serStr, "UTF-8");
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(redStr.getBytes("ISO-8859-1"));
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object object = objectInputStream.readObject();
            objectInputStream.close();
            byteArrayInputStream.close();
            return object;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.info(e.getMessage());
            return new Object();
        }
    }

    /**
     * 实体字段转成数据库字段
     *
     * @param field
     * @return
     */
    public String toDbField(String field) {
        String dbField = "";
        for (int i = 0; i < field.length(); i++) {
            if (!Character.isLowerCase(field.charAt(i))) {
                if (i == 0) {
                    dbField += (field.charAt(i) + "").toLowerCase();
                } else {
                    dbField += ("_" + field.charAt(i)).toLowerCase();
                }
            } else {
                dbField += field.charAt(i) + "";
            }
        }
        return dbField;
    }
}