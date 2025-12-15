/*
 * @(#)2015年8月5日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.cg.core.generator.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.cg.core.Context;
import com.wellsoft.pt.cg.core.generator.Model;
import com.wellsoft.pt.cg.core.generator.common.ParseUtil;
import com.wellsoft.pt.cg.core.source.TableSource;
import com.wellsoft.pt.cg.core.source.TableSource.Column;
import com.wellsoft.pt.cg.core.support.Property;
import freemarker.template.Configuration;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
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
 * 2015年8月5日.1	zhulh		2015年8月5日		Create
 * </pre>
 * @date 2015年8月5日
 */
public abstract class AbstractModel implements Model {
    protected static List<String> identityProperties = new ArrayList<String>();

    static {
        identityProperties.add(IdEntity.UUID);
        identityProperties.add(IdEntity.REC_VER);
        identityProperties.add(IdEntity.CREATOR);
        identityProperties.add(IdEntity.CREATE_TIME);
        identityProperties.add(IdEntity.MODIFIER);
        identityProperties.add(IdEntity.MODIFY_TIME);
    }

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @param value
     * @return
     */
    protected static List<Property> convert(List<Column> columns) {
        List<Property> properties = new ArrayList<Property>();
        for (Column column : columns) {
            Property property = new Property();
            property.setName(ParseUtil.propName(column.getColumnName(), true));
            property.setType(column.getType());
            properties.add(property);
        }
        return properties;
    }

    /**
     * 如何描述该方法
     *
     * @param allFileds
     * @return
     */
    protected static List<Property> convertWithoutDefaults(Field[] allFileds) {
        List<Property> properties = new ArrayList<Property>();
        for (Field field : allFileds) {
            String propName = field.getName();
            if (identityProperties.contains(propName)) {
                continue;
            }
            Property property = new Property();
            property.setName(propName);
            property.setType(field.getType().toString());
            property.setRemark(propName);
            properties.add(property);
        }
        return properties;
    }

    /**
     * 如何描述该方法
     *
     * @param allFileds
     * @return
     */
    protected static List<Property> convert(Field[] allFileds) {
        List<Property> properties = new ArrayList<Property>();
        for (Field field : allFileds) {
            Property property = new Property();
            property.setName(field.getName());
            property.setType(field.getType().toString());
            properties.add(property);
        }
        return properties;
    }

    protected static void writeTemplate(Context context, Map<String, Object> root,
                                        Configuration cfg,
                                        String templateName,
                                        String fileName,
                                        String fileCatalog) throws Exception {
        Writer writer = getOutput(context, fileName, fileCatalog);
        cfg.getTemplate(templateName, "UTF-8").process(root, writer);
        writer.flush();
        writer.close();
    }

    protected static Writer getOutput(Context context, String fileName, String fileCatalog) {
        try {
            String path = context.getJavaOutputDir();
            File f = new File(path);

            if (!f.exists()) {
                f.mkdirs();
                // throw new RuntimeException("directory["+path+"] is not  exist！java文件输出目录");
            }
            //创建包文件目录
            String entityPackagePath = (context.getPackage() != null ? "/" + context.getPackage().replaceAll(
                    "\\.",
                    "/") : "") + fileCatalog;
            if (StringUtils.isNotBlank(entityPackagePath)) {
                String[] pks = entityPackagePath.split("/");
                String folers = "";
                for (String pkFolder : pks) {
                    folers += "/" + pkFolder;
                    f = new File(path + folers);
                    if (!f.exists()) {
                        f.mkdir();
                    }
                }
            }

            f = new File(path + "/" + entityPackagePath + "/" + fileName);
            f.createNewFile();
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(f),
                    Charsets.UTF_8);
            return writer;
        } catch (IOException e) {
            throw new RuntimeException("创建文件失败");
        }
    }

    protected Configuration initConfig(Context context) {
        return ParseUtil.initConfig(context);
    }

    public void work(Context context) {
        Configuration cfg = initConfig(context);
        freemarkerFormateProcess(context, cfg);
    }

    /**
     * fremarker格式化过程
     *
     * @param context
     * @param config
     */
    protected void freemarkerFormateProcess(Context context, Configuration config) {
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    protected String getCreateDate() {
        return ParseUtil.createDate("yyyy-MM-dd");
    }

    /**
     * 驼峰命名
     *
     * @param tableName
     * @return
     */
    protected String getClassName(String tableName) {
        return ParseUtil.tableNameToClass(tableName);
    }

    /**
     * 检查系统路径是否存在
     *
     * @param path
     */
    protected void checkPath(String path, String msg) {
        File f = new File(path);
        if (StringUtils.isEmpty(path)) {
            throw new RuntimeException("directory is  empty！" + msg);
        }
        if (!f.exists()) {
            f.mkdirs();
            // throw new RuntimeException("directory["+path+"] is not  exist！"+msg);
        }
    }

    /**
     * 过滤掉父类实体中有的字段
     *
     * @param list
     * @return
     */
    protected List<TableSource.Column> filter(List<TableSource.Column> list, Class superClass) {
        Set<Column> columns = Sets.newHashSet();
        Iterator<TableSource.Column> itr = list.iterator();
        Map<String, Boolean> fieldMap = Maps.newHashMap();//保留字段
        //        fieldMap.put("status", true);
//        fieldMap.put("formUuid", true);
//        fieldMap.put("signature_", true);
//        fieldMap.put("version", true);

        if (superClass != null) {
            List<Class> allSuperClasses = ClassUtils.getAllSuperclasses(superClass);
            Field[] fs = superClass.getDeclaredFields();
            for (Field f : fs) {
                fieldMap.put(f.getName(), true);
            }
            for (Class sc : allSuperClasses) {
                Field[] superFields = sc.getDeclaredFields();
                for (Field f : superFields) {
                    fieldMap.put(f.getName(), true);
                }
            }
        }

        while (itr.hasNext()) {
            TableSource.Column column = itr.next();
            if (!fieldMap.containsKey(ParseUtil.propName(column.getColumnName()))) {
                columns.add(column);
            }
        }
        return Lists.newArrayList(columns);
    }

    /**
     * 如何描述该方法
     *
     * @param value
     * @return
     */
    protected List<Property> convertWithoutDefaults(List<Column> columns) {
        List<Property> properties = new ArrayList<Property>();
        for (Column column : columns) {
            String propName = ParseUtil.propName(column.getColumnName(), true);
            if (identityProperties.contains(propName)) {
                continue;
            }
            Property property = new Property();
            property.setName(propName);
            property.setType(column.getType());
            property.setRemark(column.getRemark());
            properties.add(property);
        }
        return properties;
    }

    protected Map<String, Object> structureData(Context context, Map<String, Object> other) {
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("package", context.getPackage());
        root.put("moduleRequestPath", context.getModuleRequestPath());
        root.put("createDate", getCreateDate());
        String author = context.getAuthor() != null ? context.getAuthor() : "well-soft";
        root.put("author", author);
        root.put("createDate", getCreateDate());
        root.putAll(other);
        return root;
    }


}
