package com.wellsoft.pt.cg.core.generator.model;

import com.wellsoft.pt.cg.core.Context;
import freemarker.template.Configuration;

import java.util.Map;

public abstract class BaseServiceModel extends AbstractModel {
    /**
     * 生成java接口文件
     *
     * @param clazz
     * @param params
     * @param context
     */
    protected void genJava(Map<String, Object> params, Context context, Configuration cfg) {
        try {

            String entityName = params.get("entity").toString();
            writeTemplate(context, params, cfg, "base.service.ftl",
                    entityName + "Service.java", "/service");


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 生成java文件
     *
     * @param clazz
     * @param params
     * @param context
     */
    protected void genJavaImpl(Map<String, Object> params, Context context, Configuration cfg) {
        try {
            String entityName = params.get("entity").toString();
            writeTemplate(context, params, cfg, "base.service.impl.ftl",
                    entityName + "ServiceImpl.java", "/service/impl");
        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

}
