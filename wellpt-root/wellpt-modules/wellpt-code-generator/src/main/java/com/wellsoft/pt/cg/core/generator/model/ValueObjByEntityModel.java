package com.wellsoft.pt.cg.core.generator.model;

import com.wellsoft.pt.cg.core.Context;
import com.wellsoft.pt.cg.core.Type;
import com.wellsoft.pt.cg.core.source.EntitySource;
import freemarker.template.Configuration;

import java.util.Date;
import java.util.Map;

/**
 * 业务数据值对象类
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
public class ValueObjByEntityModel extends ValueObjModel {
    private static final int MODELCODE = Type.OUTPUTTYPE_VALUE_OBJECT;

    @Override
    public int getCode() {
        return MODELCODE;
    }


    @Override
    protected void freemarkerFormateProcess(Context context, Configuration config) {
        EntitySource source = (EntitySource) context.getSource();
        Configuration cfg = initConfig(context);
        for (Class<?> clazz : source.getClazzs()) {
            // 构建数据
            Map<String, Object> root = structureData(context, clazz);

            // java文件生成
            genJava(root, context, cfg);

        }

    }

    /**
     * 构建数据
     *
     * @param context
     * @return
     */
    protected Map<String, Object> structureData(Context context, Class<?> clazz) {
        Map<String, Object> root = super.structureData(context, null);
        root.put("package", context.getPackage());
        root.put("moduleRequestPath", context.getModuleRequestPath());
        root.put("createDate", getCreateDate());
        String author = context.getAuthor() != null ? context.getAuthor() : "well-soft";
        root.put("author", author);
        root.put("createDate", getCreateDate());
        root.put("timestamp", String.valueOf(new Date().getTime()));
        String className = clazz.getSimpleName();
        root.put("className", className + "Bean");
        root.put("entity", className);
        return root;
    }
}