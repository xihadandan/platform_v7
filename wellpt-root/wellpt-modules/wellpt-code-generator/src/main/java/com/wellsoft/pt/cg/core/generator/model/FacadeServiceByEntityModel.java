package com.wellsoft.pt.cg.core.generator.model;

import com.wellsoft.pt.cg.core.Context;
import com.wellsoft.pt.cg.core.Type;
import com.wellsoft.pt.cg.core.source.EntitySource;
import freemarker.template.Configuration;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.persistence.Table;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * 生成门面业务服务类
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
public class FacadeServiceByEntityModel extends AbstractModel {
    private static final int MODELCODE = Type.OUTPUTTYPE_FACADE_SERVICE;

    @Override
    public int getCode() {
        return MODELCODE;
    }

    @Override
    public void work(Context context) {
        EntitySource source = (EntitySource) context.getSource();
        Configuration cfg = initConfig(context);
        try {
            for (Class<?> clazz : source.getClazzs()) {
                Map<String, Object> other = new HashMap<String, Object>();
                String entityName = clazz.getSimpleName();
                if (clazz.isAnnotationPresent(Table.class)) {
                    Annotation table = clazz.getAnnotation(Table.class);
                    other.put("tableName",
                            org.springframework.core.annotation.AnnotationUtils.getAnnotationAttributes(
                                    table, true).get("name"));
                }
                other.put("entity", entityName);
                if (entityName.endsWith("Entity")) {
                    entityName = entityName.substring(0, entityName.lastIndexOf("Entity"));
                    other.put("entity", entityName);
                }
                writeTemplate(context, other, cfg, "base.facade.service.ftl",
                        entityName + "FacadeService.java",
                        "/facade/service");
                writeTemplate(context, other, cfg, "base.facade.service.impl.ftl",
                        entityName + "FacadeServiceImpl.java",
                        "/facade/service/impl");
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }


}