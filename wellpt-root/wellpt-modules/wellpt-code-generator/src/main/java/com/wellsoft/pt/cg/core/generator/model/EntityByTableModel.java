package com.wellsoft.pt.cg.core.generator.model;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.entity.TenantEntity;
import com.wellsoft.pt.cg.core.Context;
import com.wellsoft.pt.cg.core.Type;
import com.wellsoft.pt.cg.core.dialect.Dialect;
import com.wellsoft.pt.cg.core.dialect.OracleDialect;
import com.wellsoft.pt.cg.core.freeMarker.GenerateGetMethod;
import com.wellsoft.pt.cg.core.freeMarker.GeneratePropertyMethod;
import com.wellsoft.pt.cg.core.freeMarker.GenerateSetMethod;
import com.wellsoft.pt.cg.core.freeMarker.Method;
import com.wellsoft.pt.cg.core.generator.common.ParseUtil;
import com.wellsoft.pt.cg.core.source.TableSource;
import com.wellsoft.pt.cg.core.source.TableSource.Column;
import freemarker.template.Configuration;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 更具数据库表生成实体类
 *
 * @author lmw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-7-7.1	lmw		2015-7-7		Create
 * </pre>
 * @date 2015-6-18
 */
public class EntityByTableModel extends AbstractModel {
    private static final int MODELCODE = Type.OUTPUTTYPE_ENTITY;
    final Dialect dialect = new OracleDialect();

    @Override
    public int getCode() {
        return MODELCODE;
    }


    @Override
    public void freemarkerFormateProcess(Context context, Configuration cfg) {
        try {
            TableSource tableSource = (TableSource) context.getSource();
            for (Map.Entry<String, List<TableSource.Column>> entry : tableSource.getTables().entrySet()) {
                Map<String, Object> other = new HashMap<String, Object>();
                String className = ParseUtil.tableNameToClass(entry.getKey());
                className = className.indexOf("Uf") == 0 ? className.replaceFirst("Uf",
                        "") : className;
                other.put("className", className);

                Class superEntityClass = getExtendsEntityClass(entry.getValue());
                if (superEntityClass != null) {
                    other.put("extendsEntityClass", superEntityClass.getCanonicalName());
                    other.put("extendsEntity", ClassUtils.getShortClassName(superEntityClass));
                }
                List<TableSource.Column> list = filter(entry.getValue(), superEntityClass);
                other.put("tableName", entry.getKey());
                other.put("props", list);
                other.put("propTypePackages", getImports(list));
                Map<String, Object> root = structureData(context, other);

                writeTemplate(context, root, cfg, "base.entity.ftl", className + "Entity.java",
                        "/entity");
                /*writeTemplate(context, root, cfg, "base.dto.ftl",
                        className + "Dto.java",
                        "/dto");*/
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }


    /**
     * 获取实体类的父类
     *
     * @param columns
     * @return
     */
    private Class getExtendsEntityClass(List<Column> columns) {
        String[] baseFields = IdEntity.BASE_FIELDS;
        Map<String, Boolean> fieldMap = Maps.newHashMap();
        for (String f : baseFields) {
            fieldMap.put(f, true);
        }

        boolean hasSystemUnitIdColumn = false;
        for (Column col : columns) {
            String propName = ParseUtil.propName(col.getColumnName());
            if (col.getColumnName().equalsIgnoreCase(TenantEntity.SYSTEM_UNIT_ID4DB)) {
                hasSystemUnitIdColumn = true;
            }
            if (fieldMap.containsKey(propName)) {
                fieldMap.remove(propName);
            }
        }
        if (fieldMap.isEmpty() && hasSystemUnitIdColumn) {
            return TenantEntity.class;
        }

        return fieldMap.isEmpty() ? IdEntity.class : null;

    }

    /**
     * 获取需要引入的包
     *
     * @return
     */
    protected String getImports(List<TableSource.Column> list) {
        Map<String, String> map = new HashMap<String, String>();
        for (TableSource.Column c : list) {
            String pack = dialect.javaCanonicalName(c.getType());
            if (StringUtils.isNotBlank(pack)) {
                map.put(pack, "");
            }
        }

        StringBuilder tBuilder = new StringBuilder();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            tBuilder.append("import\t").append(entry.getKey()).append(";\n");
        }
        return tBuilder.toString();
    }


    /**
     * 构建数据
     *
     * @param context
     * @return
     */
    protected Map<String, Object> structureData(Context context, Map<String, Object> other) {
        final Map<String, Object> root = super.structureData(context, other);

        root.put("generateProperty", new GeneratePropertyMethod(new Method() {
            @Override
            public Object exec(Object arg) {
                TableSource.Column column = (TableSource.Column) arg;
                String simple = dialect.javaTypeSimpleName(column.getType());
                String name = ParseUtil.propName(column.getColumnName());
                return (StringUtils.isNotBlank(
                        column.getRemark()) ? ("// " + column.getRemark()) : "") + "\n\tprivate " + simple + " " + name + ";";
            }
        }));
        root.put("generateGet", new GenerateGetMethod(new Method() {
            @Override
            public Object exec(Object arg) {
                TableSource.Column column = (TableSource.Column) arg;
                String simple = dialect.javaTypeSimpleName(column.getType());
                String name = ParseUtil.propName(column.getColumnName());
                StringBuilder builder = new StringBuilder();
                builder.append("/**\n\t *").append(" @return the ").append(name).append(
                        "\n\t */\n");
                builder.append("\t@Column(name = \"" + column.getColumnName() + "\")\n");
                builder.append("\tpublic ");
                builder.append(simple);
                builder.append(" get").append(ParseUtil.propName(column.getColumnName(), false));
                builder.append("() {\n").append("\t").append("\treturn this.").append(
                        name + ";").append("\n\t}\n");
                return builder.toString();
            }
        }));
        root.put("generateSet", new GenerateSetMethod(new Method() {
            @Override
            public Object exec(Object arg) {
                TableSource.Column column = (TableSource.Column) arg;
                String lowName = ParseUtil.propName(column.getColumnName());
                StringBuilder builder = new StringBuilder();
                builder.append("/**\n\t *").append(" @param ").append(lowName).append("\n\t */\n");
                builder.append("\tpublic void");
                String type = dialect.javaTypeSimpleName(column.getType());
                //				builder.append(type);
                builder.append(" set").append(ParseUtil.propName(column.getColumnName(), false));

                builder.append("(").append(type + " ").append(lowName).append(") {\n").append(
                        "\t").append("\tthis.")
                        .append(lowName + " = ").append(lowName).append(";\n\t}\n");
                return builder.toString();
            }
        }));
        root.put("timestamp", String.valueOf(new Date().getTime()));
        root.putAll(other);
        return root;
    }


}