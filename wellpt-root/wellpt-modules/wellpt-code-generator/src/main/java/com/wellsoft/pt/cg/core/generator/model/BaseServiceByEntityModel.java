package com.wellsoft.pt.cg.core.generator.model;

import com.wellsoft.pt.cg.core.Context;
import com.wellsoft.pt.cg.core.Type;
import com.wellsoft.pt.cg.core.source.EntitySource;
import freemarker.template.Configuration;

import javax.persistence.Table;
import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * 根据数据库表生成基本业务服务类
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
public class BaseServiceByEntityModel extends BaseServiceModel {
    private static final int MODELCODE = Type.OUTPUTTYPE_BASIC_SERVICE;

    @Override
    public int getCode() {
        return MODELCODE;
    }

    @Override
    public void work(Context context) {
        try {
            EntitySource source = (EntitySource) context.getSource();
            Configuration cfg = initConfig(context);
            for (Class<?> clazz : source.getClazzs()) {
                Map<String, Object> root = structureData(context, clazz);

                genJavaImpl(root, context, cfg);

                writeTemplate(context, root, cfg, "base.dao.ftl",
                        root.get("entity") + "Dao.java",
                        "/dao/impl");
                writeTemplate(context, root, cfg, "base.dao.impl.ftl",
                        root.get("entity") + "DaoImpl.java",
                        "/dao");

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
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
        String entityName = clazz.getSimpleName();
        if (clazz.isAnnotationPresent(Table.class)) {
            Annotation table = clazz.getAnnotation(Table.class);
            root.put("tableName",
                    org.springframework.core.annotation.AnnotationUtils.getAnnotationAttributes(
                            table, true).get("name"));
        }
        root.put("entity", entityName);
        if (entityName.endsWith("Entity")) {
            entityName = entityName.substring(0, entityName.lastIndexOf("Entity"));
            root.put("entity", entityName);
        }
        root.put("serviceInterface", entityName + "Service");
        return root;
    }


}