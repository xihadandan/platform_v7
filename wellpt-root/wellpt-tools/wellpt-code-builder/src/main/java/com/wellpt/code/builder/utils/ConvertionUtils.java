package com.wellpt.code.builder.utils;

import com.wellpt.code.builder.support.*;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.*;
import java.util.*;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/8/1
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/8/1    chenq		2018/8/1		Create
 * </pre>
 */
public class ConvertionUtils {


    public static void table2Entity(Context context, Configuration cfg) {
        try {
            TableSource tableSource = (TableSource) context.getSource();
            for (Map.Entry<String, List<TableSource.Column>> entry : tableSource.getTables().entrySet()) {
                Map<String, Object> other = new HashMap<String, Object>();
                String className = ParseUtil.tableNameToClass(entry.getKey());
                className = className.indexOf("Uf") == 0 ? className.replaceFirst("Uf",
                        "") : className;
                other.put("className", className);

                String superEntityClass = getExtendsEntityClass(entry.getValue());
                if (superEntityClass != null) {
                    other.put("extendsEntity", superEntityClass);
                }
                List<TableSource.Column> list = filter(entry.getValue());
                other.put("tableName", entry.getKey().toUpperCase());
                other.put("props", list);
                other.put("propTypePackages", getImports(list, context));
                Map<String, Object> root = structureData(context, other);

                writeTemplate(context, root, cfg, "base.entity.ftl", className + "Entity.java",
                        "/entity");
                writeTemplate(context, root, cfg, "base.dto.ftl",
                        className + "Dto.java",
                        "/dto");

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void table2ServiceDao(Context context, Configuration cfg) {
        try {
          
            Set<String> tableSet = ((TableSource) context.getSource()).getTableSet();
            for (String table : tableSet) {
                Map<String, Object> other = new HashMap<String, Object>();
                String className = ParseUtil.tableNameToClass(table);
                className = className.indexOf("Uf") == 0 ? className.replaceFirst("Uf",
                        "") : className;
                other.put("entity", className);
                other.put("tableName", table.toUpperCase());
                Map<String, Object> root = structureData(context, other);

                writeTemplate(context, root, cfg, "base.service.ftl", className + "Service.java",
                        "/service");
                writeTemplate(context, root, cfg, "base.service.impl.ftl",
                        className + "ServiceImpl.java",
                        "/service/impl");
                writeTemplate(context, root, cfg, "base.dao.ftl", className + "Dao.java",
                        "/dao");
                writeTemplate(context, root, cfg, "base.dao.impl.ftl", className + "DaoImpl.java",
                        "/dao/impl");
                writeTemplate(context, root, cfg, "base.facade.service.ftl",
                        className + "FacadeService.java",
                        "/facade/service");
                writeTemplate(context, root, cfg, "base.facade.service.impl.ftl",
                        className + "FacadeServiceImpl.java",
                        "/facade/service/impl");

            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private static void writeTemplate(Context context, Map<String, Object> root, Configuration cfg,
                                      String templateName,
                                      String fileName,
                                      String fileCatalog) throws Exception {
        Writer writer = getOutput(context, fileName, fileCatalog);
        cfg.getTemplate(templateName, "UTF-8").process(root, writer);
        writer.flush();
        writer.close();
    }

    /**
     * 获取实体类的父类
     *
     * @param columns
     * @return
     */
    private static String getExtendsEntityClass(List<TableSource.Column> columns) {
        String[] baseFields = new String[]{"uuid", "creator", "createTime", "modifier", "modifyTime",
                "recVer"};
        Map<String, Boolean> fieldMap = new HashMap<String, Boolean>();
        for (String f : baseFields) {
            fieldMap.put(f, true);
        }

        boolean hasSystemUnitIdColumn = false;
        for (TableSource.Column col : columns) {
            String propName = ParseUtil.propName(col.getColumnName());
            if (col.getColumnName().equalsIgnoreCase("SYSTEM_UNIT_ID")) {
                hasSystemUnitIdColumn = true;
            }
            if (fieldMap.containsKey(propName)) {
                fieldMap.remove(propName);
            }
        }
        if (fieldMap.isEmpty() && hasSystemUnitIdColumn) {
            return "TenantEntity";
        }

        return fieldMap.isEmpty() ? "IdEntity" : null;

    }

    /**
     * 过滤掉父类实体中有的字段
     *
     * @param list
     * @return
     */
    private static List<TableSource.Column> filter(List<TableSource.Column> list) {
        Set<TableSource.Column> columns = new HashSet<TableSource.Column>();
        Iterator<TableSource.Column> itr = list.iterator();

        List<String> baseFields = new ArrayList<String>();
        baseFields.addAll(Arrays.asList("uuid", "creator", "createTime", "modifier",
                "modifyTime",
                "recVer", "systemUnitId", "version", "formUuid", "signature", "status"));


        while (itr.hasNext()) {
            TableSource.Column column = itr.next();
            if (!baseFields.contains(ParseUtil.propName(column.getColumnName()))) {
                columns.add(column);
            }
        }
        List<TableSource.Column> columnList = new ArrayList();
        columnList.addAll(columns);
        return columnList;
    }


    /**
     * 获取需要引入的包
     *
     * @return
     */
    private static String getImports(List<TableSource.Column> list, Context context) {
        Map<String, String> map = new HashMap<String, String>();
        for (TableSource.Column c : list) {
            String pack = context.getDialect().javaCanonicalName(c.getType());
            if (!CommonUtils.isBlank(pack)) {
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
    private static Map<String, Object> structureData(final Context context,
                                                     Map<String, Object> other) {
        final Map<String, Object> root = new HashMap<String, Object>();
        root.put("package", context.getPackage());
        root.put("moduleRequestPath", context.getModuleRequestPath());
        root.put("createDate", getCreateDate());
        String author = context.getAuthor() != null ? context.getAuthor() : "well-soft";
        root.put("author", author);

        root.put("generateProperty", new GeneratePropertyMethod(new Method() {
            @Override
            public Object exec(Object arg) {
                TableSource.Column column = (TableSource.Column) arg;
                String simple = context.getDialect().javaTypeSimpleName(column.getType());
                String name = ParseUtil.propName(column.getColumnName());
                return (!CommonUtils.isBlank(
                        column.getRemark()) ? ("// " + column.getRemark()) : "") + "\n\tprivate " + simple + " " + name + ";";
            }
        }));
        root.put("generateGet", new GenerateGetMethod(new Method() {
            @Override
            public Object exec(Object arg) {
                TableSource.Column column = (TableSource.Column) arg;
                String simple = context.getDialect().javaTypeSimpleName(column.getType());
                String name = ParseUtil.propName(column.getColumnName());
                StringBuilder builder = new StringBuilder();
                builder.append("/**\n\t *").append(" @return the ").append(name).append(
                        "\n\t */\n");
                //builder.append("\t@Column(name = \"" + column.getColumnName() + "\")\n");
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
                String type = context.getDialect().javaTypeSimpleName(column.getType());
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


    /**
     * 获取当前时间
     *
     * @return
     */
    private static String getCreateDate() {
        return ParseUtil.createDate("yyyy-MM-dd");
    }

    /**
     * 检查系统路径是否存在
     *
     * @param path
     */
    private static void checkPath(String path, String msg) {
        File f = new File(path);
        if (CommonUtils.isBlank(path)) {
            throw new RuntimeException("目录为空！" + path);
        }
        if (!f.exists()) {
            f.mkdirs();
        }
    }


    private static Writer getOutput(Context context, String fileName, String fileCatalog) {
        try {
            String path = context.getJavaOutputDir();
            File f = new File(path);

            if (CommonUtils.isBlank(path) || !f.exists()) {
                f.mkdirs();
                // throw new RuntimeException("directory["+path+"] is not  exist！java文件输出目录");
            }
            //创建包文件目录
            String entityPackagePath = (context.getPackage() != null ? "/" + context.getPackage().replaceAll(
                    "\\.",
                    "/") : "") + fileCatalog;
            if (!CommonUtils.isBlank(entityPackagePath)) {
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
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(f), "utf-8");
            return writer;
        } catch (IOException e) {
            throw new RuntimeException("创建文件失败");
        }
    }
}
