/*
 * @(#)2015-8-13 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.cg.core.generator.model;

import com.wellsoft.pt.cg.core.Context;
import com.wellsoft.pt.cg.core.Type;
import com.wellsoft.pt.cg.core.generator.common.ParseUtil;
import com.wellsoft.pt.cg.core.source.TableSource;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
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
 * 2015-8-13.1	zhulh		2015-8-13		Create
 * </pre>
 * @date 2015-8-13
 */
public class FrontPageViewMaintainByTableModel extends AbstractModel {
    private static final int MODELCODE = Type.OUTPUTTYPE_FRONT_PAGE_VIEW_MAINTAIN;

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
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.cg.core.generator.Model#work(com.wellsoft.pt.cg.core.Context)
     */
    @Override
    public void work(Context context) {
        TableSource source = (TableSource) context.getSource();
        Configuration cfg = initConfig(context);
        for (Map.Entry<String, List<TableSource.Column>> entry : source.getTables().entrySet()) {
            // 构建数据
            Map<String, Object> root = structureData(context, entry);

            // 生成视图数据源
            genDataSource(root, context, cfg);

            // 生成控制层
            genController(root, context, cfg);

            // 生成单表操作门面接品
            genFacadeService(root, context, cfg);

            // 生成单表操作门面接品实现类
            genFacadeServiceImpl(root, context, cfg);

            // 生成单表操作JS
            genJs(root, context, cfg);

            // 生成单表操作JSP
            genJsp(root, context, cfg, "new");
            genJsp(root, context, cfg, "edit");
        }
    }

    /**
     * 如何描述该方法
     *
     * @param root
     * @param context
     * @param cfg
     */
    private void genDataSource(Map<String, Object> params, Context context, Configuration cfg) {
        try {
            // 实现类接口模板
            Template template = cfg.getTemplate("view.crud.dataSource.ftl", "UTF-8");
            String path = context.getJavaOutputDir();
            // 检查路径是否存在
            checkPath(path, "java文件输出目录");
            path += "/api";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }

            String entity = params.get("entity").toString();
            File f = new File(path + "/" + entity + "ViewMaintainSourceProvider" + ".java");
            f.createNewFile();
            Writer writer = new FileWriter(f);
            template.process(params, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("templateFile[view.crud.dataSource.ftl] is not exist！");
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
            Template template = cfg.getTemplate("view.crud.controller.ftl", "UTF-8");
            String path = context.getJavaOutputDir();
            // 检查路径是否存在
            checkPath(path, "java文件输出目录");
            path += "/web";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }

            String entity = params.get("entity").toString();
            File f = new File(path + "/" + entity + "ViewMaintainController" + ".java");
            f.createNewFile();
            Writer writer = new FileWriter(f);
            template.process(params, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("templateFile[view.crud.controller.ftl] is not exist！");
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
    private void genFacadeService(Map<String, Object> params, Context context, Configuration cfg) {
        try {
            // 实现类接口模板
            Template template = cfg.getTemplate("view.crud.facadeService.ftl", "UTF-8");
            String path = context.getJavaOutputDir();
            // 检查路径是否存在
            checkPath(path, "java文件输出目录");
            path += "/facade";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }

            String entity = params.get("entity").toString();
            File f = new File(path + "/" + entity + "ViewMaintain" + ".java");
            f.createNewFile();
            Writer writer = new FileWriter(f);
            template.process(params, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("templateFile[view.crud.facadeService.ftl] is not exist！");
        } catch (TemplateException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("template parse error！" + e.getMessage());
        }
    }

    /**
     * 如何描述该方法
     *
     * @param params
     * @param context
     * @param cfg
     */
    private void genFacadeServiceImpl(Map<String, Object> params, Context context, Configuration cfg) {
        try {
            // 实现类接口模板
            Template template = cfg.getTemplate("view.crud.facadeServiceImpl.ftl", "UTF-8");
            String path = context.getJavaOutputDir();
            // 检查路径是否存在
            checkPath(path, "java文件输出目录");
            path += "/facade/impl";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }

            String entity = params.get("entity").toString();
            File f = new File(path + "/" + entity + "ViewMaintainImpl" + ".java");
            f.createNewFile();
            Writer writer = new FileWriter(f);
            template.process(params, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("templateFile[view.crud.facadeServiceImpl.ftl] is not exist！");
        } catch (TemplateException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("template parse error！" + e.getMessage());
        }
    }

    /**
     * 如何描述该方法
     *
     * @param params
     * @param context
     * @param cfg
     */
    private void genJs(Map<String, Object> params, Context context, Configuration cfg) {
        try {
            // 实现类接口模板
            Template template = cfg.getTemplate("view.crud.js.ftl", "UTF-8");
            String path = context.getJSOutputDir();
            // 检查路径是否存在
            checkPath(path, "java文件输出目录");
            File file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }

            String tableName = params.get("tableName").toString();
            File f = new File(path + "/" + tableName + "_view" + ".js");
            f.createNewFile();
            Writer writer = new FileWriter(f);
            template.process(params, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("templateFile[view.crud.js.ftl] is not exist！");
        } catch (TemplateException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("template parse error！" + e.getMessage());
        }
    }

    /**
     * 如何描述该方法
     *
     * @param params
     * @param context
     * @param cfg
     */
    private void genJsp(Map<String, Object> params, Context context, Configuration cfg, String suffix) {
        try {
            // 实现类接口模板
            Template template = cfg.getTemplate("view.crud.jsp.ftl", "UTF-8");
            String path = context.getJSPOutputDir();
            // 检查路径是否存在
            checkPath(path, "jsp文件输出目录");
            File file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }

            String tableName = params.get("tableName").toString();
            File f = new File(path + "/" + tableName + "_" + suffix + ".jsp");
            f.createNewFile();
            Writer writer = new FileWriter(f);
            template.process(params, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("templateFile[view.crud.jsp.ftl] is not exist！");
        } catch (TemplateException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("template parse error！" + e.getMessage());
        }
    }

    protected Configuration initConfig(Context context) {
        try {
            Configuration cfg = new Configuration();
            if (!context.isDefault()) {
                cfg.setDirectoryForTemplateLoading(new File(context.getTemplateDir()));
            } else {
                cfg.setClassForTemplateLoading(Context.class, "ft");
            }

            cfg.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
            return cfg;
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(" templateDir is not exist！");
        }
    }

    /**
     * 构建数据
     *
     * @param context
     * @return
     */
    protected Map<String, Object> structureData(Context context, Map.Entry<String, List<TableSource.Column>> entry) {
        Map<String, Object> root = new HashMap<String, Object>();
        // 设置当前时间
        root.put("createDate", getCreateDate());

        // 设置作者
        String author = context.getAuthor() != null ? context.getAuthor() : "well-soft";
        root.put("author", author);

        // 设置实体类名
        String entity = getClassName(entry.getKey());
        root.put("entity", entity);

        // 设置属性集合
        root.put("props", entry.getValue());
        root.put("properties", convert(entry.getValue()));
        root.put("propertyWithoutDefaults", convertWithoutDefaults(entry.getValue()));

        // 设置数据库表名
        String tableName = ParseUtil.classNameToTable(entity);
        root.put("tableName", tableName);

        // js文件生成
        StringBuilder entityBean = new StringBuilder("{\n");
        StringBuilder colNames = new StringBuilder("[");
        StringBuilder colModel = new StringBuilder("[");
        for (TableSource.Column column : entry.getValue()) {
            String colName = ParseUtil.propName(column.getColumnName());
            if (identityProperties.contains(colName) && !"uuid".equalsIgnoreCase(colName)) {
                continue;
            }
            entityBean.append("\t\t" + colName + " : null,\n");
            colNames.append("\"" + column.getRemark() + "\",");
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
        root.put("bean", entity + "Bean");
        root.put("classRequestMappingPath", "/" + StringUtils.replace(tableName.toLowerCase(), "_", "/"));
        root.put("jspViewName", tableName.toLowerCase());

        return root;
    }

}
