package com.wellsoft.pt.cg.core.generator.model;

import com.wellsoft.pt.cg.core.Context;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public abstract class BAMModel extends AbstractModel {

    /**
     * 生成jsp文件
     *
     * @param clazz
     * @param params
     * @param context
     */
    protected void genJsp(Map<String, Object> params, Context context, Configuration cfg) {
        try {
            // 获取接口模板
            Template jspTemp = cfg.getTemplate("bam.jsp.ftl", "UTF-8");
            String path = context.getJSPOutputDir();

            // 检查路径是否存在
            checkPath(path, "jsp文件输出目录");

            File f = new File(path + "/" + params.get("tableName") + "_list" + ".jsp");
            f.createNewFile();
            Writer serviceWriter = new FileWriter(f);
            jspTemp.process(params, serviceWriter);
            serviceWriter.flush();
            serviceWriter.close();
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("templateFile[bam.js.ftl] is not exist！");
        } catch (TemplateException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("template parse error！" + e.getMessage());
        }
    }

    /**
     * 生成js文件
     *
     * @param clazz
     * @param params
     * @param context
     */
    protected void genJs(Map<String, Object> params, Context context, Configuration cfg) {
        try {
            // 实现类接口模板
            Template jsTemp = cfg.getTemplate("bam.js.ftl", "UTF-8");
            String path = context.getJSOutputDir();
            // 检查路径是否存在
            checkPath(path, "js文件输出目录");
            File f = new File(path + "/" + params.get("tableName") + "_list" + ".js");
            f.createNewFile();
            Writer implWriter = new FileWriter(f);
            jsTemp.process(params, implWriter);
            implWriter.flush();
            implWriter.close();
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("templateFile[bam.js.ftl] is not exist！");
        } catch (TemplateException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("template parse error！" + e.getMessage());
        }

    }

    /**
     * 生成java接口文件
     *
     * @param clazz
     * @param params
     * @param context
     */
    protected void genJava(Map<String, Object> params, Context context, Configuration cfg) {
        try {
            // 实现类接口模板
            Template jsTemp = cfg.getTemplate("bam.facadeService.ftl", "UTF-8");
            String path = context.getJavaOutputDir();
            // 检查路径是否存在
            checkPath(path, "java文件输出目录");
            path += "/facade/service";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }

            File f = new File(path + "/" + params.get("className") + ".java");
            f.createNewFile();
            Writer implWriter = new FileWriter(f);
            jsTemp.process(params, implWriter);
            implWriter.flush();
            implWriter.close();
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("templateFile[bam.facadeService.ftl] is not exist！");
        } catch (TemplateException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("template parse error！" + e.getMessage());
        }
    }

    /**
     * 生成java实现文件
     *
     * @param clazz
     * @param params
     * @param context
     */
    protected void genJavaImpl(Map<String, Object> params, Context context, Configuration cfg) {
        try {
            // 实现类接口模板
            Template jsTemp = cfg.getTemplate("bam.facadeServiceImpl.ftl", "UTF-8");
            String path = context.getJavaOutputDir();
            // 检查路径是否存在
            checkPath(path, "java文件输出目录");
            path += "/facade/service";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }

            path += "/impl";
            file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }

            File f = new File(path + "/" + params.get("className") + ".java");
            f.createNewFile();
            Writer implWriter = new FileWriter(f);
            jsTemp.process(params, implWriter);
            implWriter.flush();
            implWriter.close();
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("templateFile[bam.facadeServiceImpl.ftl] is not exist！");
        } catch (TemplateException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("template parse error！" + e.getMessage());
        }
    }

    /**
     * @param params
     * @param context
     * @param cfg
     */
    protected void genController(Map<String, Object> params, Context context, Configuration cfg) {
        try {
            // 实现类接口模板
            Template jsTemp = cfg.getTemplate("bam.controller.ftl", "UTF-8");
            String path = context.getJavaOutputDir();
            // 检查路径是否存在
            checkPath(path, "java文件输出目录");
            path += "/web";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }

            File f = new File(path + "/" + params.get("className") + ".java");
            f.createNewFile();
            Writer implWriter = new FileWriter(f);
            jsTemp.process(params, implWriter);
            implWriter.flush();
            implWriter.close();
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("templateFile[bam.controller.ftl] is not exist！");
        } catch (TemplateException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("template parse error！" + e.getMessage());
        }

    }

}
