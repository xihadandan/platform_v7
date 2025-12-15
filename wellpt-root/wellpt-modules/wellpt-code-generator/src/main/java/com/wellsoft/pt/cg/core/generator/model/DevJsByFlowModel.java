package com.wellsoft.pt.cg.core.generator.model;

import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.cg.core.Context;
import com.wellsoft.pt.cg.core.Type;
import com.wellsoft.pt.cg.core.source.FlowSource;
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
import java.util.Map;

/**
 * 根据流程生成流程二次开发文件
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
public class DevJsByFlowModel extends AbstractModel {
    private static final int MODELCODE = Type.OUTPUTTYPE_DEV_JS;

    @Override
    public int getCode() {
        return MODELCODE;
    }

    @Override
    public void work(Context context) {
        FlowSource source = (FlowSource) context.getSource();
        Configuration cfg = initConfig(context);
        try {
            // 获取接口模板
            Template jsTemp = cfg.getTemplate("wf.develop.js.ftl", "UTF-8");
            for (FlowDefinition flow : source.getFlows()) {
                Map<String, Object> other = new HashMap<String, Object>();
                Map<String, Object> root = structureData(context, other);
                String path = context.getJSOutputDir();
                File f = new File(path);

                if (StringUtils.isEmpty(path) || !f.exists()) {
                    f.mkdirs();
                    // throw new
                    // RuntimeException("directory["+path+"] is not  exist！js文件输出目录");
                }

                f = new File(path + "/" + flow.getId().toLowerCase() + "_workflow" + ".js");
                f.createNewFile();
                Writer jsWriter = new FileWriter(f);
                jsTemp.process(root, jsWriter);
                jsWriter.flush();
                jsWriter.close();
            }

        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("templateFile[base.service.ftl,base.serviceImpl.ftl] is not exist！");
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
    protected Map<String, Object> structureData(Context context, Map<String, Object> other) {
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("createDate", getCreateDate());
        String author = context.getAuthor() != null ? context.getAuthor() : "well-soft";
        root.put("author", author);
        root.putAll(other);
        return root;
    }

}
