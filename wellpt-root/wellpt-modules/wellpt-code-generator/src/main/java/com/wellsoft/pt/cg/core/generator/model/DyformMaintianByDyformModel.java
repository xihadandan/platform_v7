/*
 * @(#)2015年8月20日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.cg.core.generator.model;

import com.wellsoft.pt.cg.core.Context;
import com.wellsoft.pt.cg.core.Type;
import com.wellsoft.pt.cg.core.generator.common.ParseUtil;
import com.wellsoft.pt.cg.core.source.DyformSource;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年8月20日.1	zhulh		2015年8月20日		Create
 * </pre>
 * @date 2015年8月20日
 */
public class DyformMaintianByDyformModel extends AbstractModel {
    private static final int MODELCODE = Type.OUTPUTTYPE_DYFORM_MAINTAIN;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.cg.core.generator.Model#getCode()
     */
    @Override
    public int getCode() {
        return MODELCODE;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.cg.core.generator.Model#work(com.wellsoft.pt.cg.core.Context)
     */
    @Override
    public void work(Context context) {
        DyformSource source = (DyformSource) context.getSource();
        Configuration cfg = initConfig(context);
        for (DyFormFormDefinition dyFormDefinition : source.getDyforms()) {
            // 构建数据
            Map<String, Object> root = structureData(context, dyFormDefinition);

            // 生成JSP文件
            genJsp(root, context, cfg);

            // 生成JS文件
            genJs(root, context, cfg);

            // 生成控制器文件
            genController(root, context, cfg);

            // 生成门面服务接口
            genFacade(root, context, cfg);

            // 生成门面服务实现类
            genFacadeImpl(root, context, cfg);
        }

    }

    /**
     * 如何描述该方法
     *
     * @param root
     * @param context
     * @param cfg
     */
    private void genJsp(Map<String, Object> params, Context context, Configuration cfg) {
        try {
            // 实现类接口模板
            Template template = cfg.getTemplate("dyform.maintain.jsp.ftl", "UTF-8");
            String path = context.getJSPOutputDir();
            // 检查路径是否存在
            checkPath(path, "jsp文件输出目录");
            File file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }

            String tableName = params.get("tableName").toString();
            File f = new File(path + "/" + tableName + "_form" + ".jsp");
            f.createNewFile();
            Writer writer = new FileWriter(f);
            template.process(params, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("templateFile[dyform.maintain.jsp.ftl] is not exist！");
        } catch (TemplateException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("template parse error！" + e.getMessage());
        }
    }

    /**
     * 如何描述该方法
     *
     * @param root
     * @param context
     * @param cfg
     */
    private void genJs(Map<String, Object> params, Context context, Configuration cfg) {
        try {
            // 实现类接口模板
            Template template = cfg.getTemplate("dyform.maintain.js.ftl", "UTF-8");
            String path = context.getJSOutputDir();
            // 检查路径是否存在
            checkPath(path, "java文件输出目录");
            File file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }

            String tableName = params.get("tableName").toString();
            File f = new File(path + "/" + tableName + "_form" + ".js");
            f.createNewFile();
            Writer writer = new FileWriter(f);
            template.process(params, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("templateFile[dyform.maintain.js.ftl] is not exist！");
        } catch (TemplateException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("template parse error！" + e.getMessage());
        }
    }

    /**
     * 如何描述该方法
     *
     * @param root
     * @param context
     * @param cfg
     */
    private void genController(Map<String, Object> params, Context context, Configuration cfg) {
        try {
            // 实现类接口模板
            Template template = cfg.getTemplate("dyform.maintain.controller.ftl", "UTF-8");
            String path = context.getJavaOutputDir();
            // 检查路径是否存在
            checkPath(path, "java文件输出目录");
            path += "/web";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }

            String entity = params.get("entity").toString();
            File f = new File(path + "/" + entity + "DyformMaintainController" + ".java");
            f.createNewFile();
            Writer writer = new FileWriter(f);
            template.process(params, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("templateFile[dyform.maintain.controller.ftl] is not exist！");
        } catch (TemplateException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("template parse error！" + e.getMessage());
        }
    }

    /**
     * 如何描述该方法
     *
     * @param root
     * @param context
     * @param cfg
     */
    private void genFacade(Map<String, Object> params, Context context, Configuration cfg) {
        try {
            // 实现类接口模板
            Template template = cfg.getTemplate("dyform.maintain.facadeService.ftl", "UTF-8");
            String path = context.getJavaOutputDir();
            // 检查路径是否存在
            checkPath(path, "java文件输出目录");
            path += "/facade/service";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }

            String entity = params.get("entity").toString();
            File f = new File(path + "/" + entity + "DyformMaintain" + ".java");
            f.createNewFile();
            Writer writer = new FileWriter(f);
            template.process(params, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("templateFile[dyform.maintain.facadeService.ftl] is not exist！");
        } catch (TemplateException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("template parse error！" + e.getMessage());
        }
    }

    /**
     * 如何描述该方法
     *
     * @param root
     * @param context
     * @param cfg
     */
    private void genFacadeImpl(Map<String, Object> params, Context context, Configuration cfg) {
        try {
            // 实现类接口模板
            Template template = cfg.getTemplate("dyform.maintain.facadeServiceImpl.ftl", "UTF-8");
            String path = context.getJavaOutputDir();
            // 检查路径是否存在
            checkPath(path, "java文件输出目录");
            path += "/facade/service/impl";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }

            String entity = params.get("entity").toString();
            File f = new File(path + "/" + entity + "DyformMaintainImpl" + ".java");
            f.createNewFile();
            Writer writer = new FileWriter(f);
            template.process(params, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("templateFile[dyform.maintain.facadeServiceImpl.ftl] is not exist！");
        } catch (TemplateException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("template parse error！" + e.getMessage());
        }
    }

    /**
     * 如何描述该方法
     *
     * @param context
     * @param dyFormDefinition
     * @return
     */
    private Map<String, Object> structureData(Context context, DyFormFormDefinition dyFormDefinition) {
        Map<String, Object> root = new HashMap<String, Object>();
        // 设置当前时间
        root.put("createDate", getCreateDate());

        // 设置作者
        String author = context.getAuthor() != null ? context.getAuthor() : "well-soft";
        root.put("author", author);

        // 设置表单实体名
        String tableName = dyFormDefinition.getName();
        String entity = ParseUtil.tableNameToClass(tableName);
        root.put("entity", entity);

        // 设置数据库表名
        root.put("tableName", tableName);

        // 表单定义ID
        root.put("formDefId", dyFormDefinition.getId());

        // 表单显示名
        root.put("displayName", dyFormDefinition.getName());

        String entityLowFisrt = ParseUtil.toLowerFirst(entity);
        root.put("entityLowFisrt", entityLowFisrt);
        root.put("mgr", entityLowFisrt + "Mgr");
        root.put("service", entity + "Service");
        root.put("serviceLowFirst", entityLowFisrt + "Service");
        root.put("interface", entity + "Mgr");
        root.put("package", context.getPackage());
        root.put("moduleRequestPath", context.getModuleRequestPath());
        root.put("valObj", entity + "Bean");
        root.put("bean", entity + "Bean");
        root.put("classRequestMappingPath", "/" + StringUtils.replace(tableName.toLowerCase(), "_", "/"));
        root.put("jspViewName", tableName.toLowerCase());

        return root;
    }

}
