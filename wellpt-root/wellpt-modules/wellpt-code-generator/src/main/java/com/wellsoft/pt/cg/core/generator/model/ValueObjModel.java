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

/**
 * 业务数据值对象类
 *
 * @author lmw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-7-15.1	lmw		2015-7-15		Create
 * </pre>
 * @date 2015-6-18
 */
public abstract class ValueObjModel extends AbstractModel {
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
            Template temp = cfg.getTemplate("base.bean.ftl", "UTF-8");
            String path = context.getJavaOutputDir();
            // 检查路径是否存在
            checkPath(path, "java文件输出目录");

            path += "/bean";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }

            File f = new File(path + "/" + params.get("className") + ".java");
            f.createNewFile();
            Writer implWriter = new FileWriter(f);
            temp.process(params, implWriter);
            implWriter.flush();
            implWriter.close();
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("templateFile[base.bean.ftl] is not exist！");
        } catch (TemplateException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("template parse error！" + e.getMessage());
        }
    }

}
