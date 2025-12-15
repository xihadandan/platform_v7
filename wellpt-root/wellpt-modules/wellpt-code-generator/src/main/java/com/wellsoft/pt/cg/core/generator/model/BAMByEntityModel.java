package com.wellsoft.pt.cg.core.generator.model;

import com.wellsoft.pt.cg.core.Context;
import com.wellsoft.pt.cg.core.Type;
import com.wellsoft.pt.cg.core.freeMarker.GenerateTableMethod;
import com.wellsoft.pt.cg.core.freeMarker.Method;
import com.wellsoft.pt.cg.core.generator.common.ParseUtil;
import com.wellsoft.pt.cg.core.source.EntitySource;
import freemarker.template.Configuration;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 生成后台管理功能文件
 * <p>
 * 在获取entity类的属性时，只获取了在本类中声明的非静态属性，对父类中声明的属性没有做任何处理
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
public class BAMByEntityModel extends BAMModel {
    private static final int MODELCODE = Type.OUTPUTTYPE_BAM;

    /**
     * 如何描述该方法
     *
     * @param clazz
     * @return
     */
    private static Field[] filter(Class<?> clazz) {
        Field[] fields = ParseUtil.getAllFileds(clazz);
        List<Field> newFields = new ArrayList<Field>();
        for (Field field : fields) {
            String colName = field.getName();
            if ("uuid".equals(colName) || "creator".equals(colName) || "createTime".equals(colName)
                    || "modifier".equals(colName) || "modifyTime".equals(colName) || "recVer".equals(colName)) {
                continue;
            }
            newFields.add(field);
        }
        return newFields.toArray(new Field[0]);
    }

    @Override
    public int getCode() {
        return MODELCODE;
    }

    @Override
    public void work(Context context) {
        EntitySource source = (EntitySource) context.getSource();
        Configuration cfg = initConfig(context);
        for (Class<?> clazz : source.getClazzs()) {
            // 构建数据
            Map<String, Object> root = structureData(context, clazz);

            // jsp文件生成
            root.put("props", filter(clazz));
            root.put("properties", convert(ParseUtil.getAllFileds(clazz)));
            root.put("propertyWithoutDefaults", convertWithoutDefaults(ParseUtil.getAllFileds(clazz)));

            genJsp(root, context, cfg);

            // js文件生成
            root.put("props", ParseUtil.getAllFileds(clazz));
            genJs(root, context, cfg);

            String entity = clazz.getSimpleName();
            root.put("className", entity + "Mgr");
            // facade service文件生成
            genJava(root, context, cfg);

            // facade serviceImpl文件生成
            root.put("className", entity + "MgrImpl");
            genJavaImpl(root, context, cfg);

            // controller文件生成
            root.put("className", entity + "MgrController");
            genController(root, context, cfg);
        }
    }

    /**
     * 构建数据
     *
     * @param context
     * @return
     */
    protected Map<String, Object> structureData(Context context, Class<?> clazz) {
        Map<String, Object> root = new HashMap<String, Object>();
        // 设置generateTable函数
        root.put("generateTable", new GenerateTableMethod(new Method() {

            @Override
            public Object exec(Object arg) {
                Field[] fields = (Field[]) arg;
                StringBuilder builder = new StringBuilder("<table>\n");
                int index = 0;
                for (Field field : fields) {
                    // 过滤掉静态变量
                    if (Modifier.isStatic(field.getModifiers())) {
                        continue;
                    }
                    String prop = field.getName();
                    if (index == 0) {
                        builder.append("<tr>\n<td style=\"width: 65px;\"><label for=\"" + prop + "\">");
                    } else {
                        builder.append("<tr>\n<td><label for=\"" + prop + "\">");
                    }
                    builder.append(prop).append("</label></td><td><input id=\"").append(prop + "\" ")
                            .append("name=\"" + prop + "\" ")
                            .append(" type=\"text\" class=\"full-width\" /></td><td></td></tr>");
                    index++;
                }
                builder.append("</table>");
                return builder.toString();
            }
        }));
        // 设置当前时间
        root.put("createDate", getCreateDate());

        // 设置作者
        String author = context.getAuthor() != null ? context.getAuthor() : "well-soft";
        root.put("author", author);

        // 设置实体类名
        String entity = clazz.getSimpleName();
        root.put("entity", entity);

        // 设置属性集合
        Field[] fields = ParseUtil.getAllFileds(clazz);
        root.put("props", fields);

        // 设置数据库表名
        String tableName = ParseUtil.classNameToTable(entity);
        root.put("tableName", tableName);

        // js文件生成
        StringBuilder entityBean = new StringBuilder("{\n");
        StringBuilder colNames = new StringBuilder("[");
        StringBuilder colModel = new StringBuilder("[");
        for (Field field : fields) {
            // 过滤掉静态变量
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            String colName = field.getName();
            if ("creator".equals(colName) || "createTime".equals(colName) || "modifier".equals(colName)
                    || "modifyTime".equals(colName) || "recVer".equals(colName)) {
                continue;
            }
            entityBean.append("\t\t" + colName + ":null,\n");
            colNames.append("\"" + colName + "\",");
            colModel.append("{\n\t\t\tname : '" + colName + "',\n").append("\t\t\tindex : '" + colName + "',\n")
                    .append("\t\t\twidth : 180,\n");
            if ("uuid".equalsIgnoreCase(colName)) {
                colModel.append("\t\t\thidden : true,\n").append("\t\t\tkey : true\n");
            }
            colModel.append("\t\t},");
        }
        entityBean.deleteCharAt(entityBean.length() - 1).append("\n\t};");
        colNames.deleteCharAt(colNames.length() - 1).append("]");
        colModel.deleteCharAt(colModel.length() - 1).append("]");
        root.put("entityBean", entityBean.toString());
        root.put("colNames", colNames.toString());
        root.put("colModel", colModel.toString());
        String entityLowFisrt = ParseUtil.toLowerFirst(entity);
        root.put("entityLowFisrt", entityLowFisrt);
        root.put("mgr", entityLowFisrt + "Mgr");
        root.put("service", entity + "Service");
        root.put("serviceLowFirst", entityLowFisrt + "Service");
        root.put("interface", entity + "Mgr");
        root.put("package", context.getPackage());
        root.put("moduleRequestPath", context.getModuleRequestPath());
        root.put("valObj", entity + "Bean");
        root.put("classRequestMappingPath", "/" + StringUtils.replace(tableName.toLowerCase(), "_", "/"));
        root.put("jspViewName", tableName.toLowerCase());

        return root;
    }
}
