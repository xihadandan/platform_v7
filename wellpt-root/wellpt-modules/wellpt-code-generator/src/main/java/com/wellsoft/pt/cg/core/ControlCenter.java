package com.wellsoft.pt.cg.core;

import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.cg.core.generator.Generator;
import com.wellsoft.pt.cg.core.generator.GeneratorFactory;
import com.wellsoft.pt.cg.core.sniffer.Sniffer;
import com.wellsoft.pt.cg.core.sniffer.SnifferFactory;
import com.wellsoft.pt.cg.core.sniffer.impl.DyformSniffer;
import com.wellsoft.pt.cg.core.sniffer.impl.EntitySniffer;
import com.wellsoft.pt.cg.core.sniffer.impl.FlowSniffer;
import com.wellsoft.pt.cg.core.sniffer.impl.TableSniffer;
import com.wellsoft.pt.cg.core.support.ConfigJson;
import com.wellsoft.pt.cg.entity.CodeGeneratorConfig;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 生成器控制中心
 *
 * @author lmw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-7-8.1	lmw		2015-7-8		Create
 * </pre>
 * @date 2015-7-8
 */
public class ControlCenter {

    public static void generate(CodeGeneratorConfig config) {
        start(create(config));
    }

    /**
     * 开启流程
     *
     * @param context
     */
    public static void start(Context context) {
        if (context == null) {
            throw new RuntimeException("context is null");
        }

        Sniffer sniffer = SnifferFactory.intendSniffer(context.getType());
        sniffer.snifferAndImpact(context);
        Generator generator = GeneratorFactory.intendGenerator(context.getType());
        generator.generate(context);
    }

    public static Context create(CodeGeneratorConfig config) {
        Context context = new Context();
        context.setName(config.getName());
        context.setAuthor(config.getAuthor());

        context.setJavaOutputDir(config.getJavaFileOutputDir());
        context.setJSOutputDir(config.getJsFileOutputDir());
        context.setJSPOutputDir(config.getJspFileOutputDir());

        context.setPackage(config.getJavaPackage());
        context.setModuleRequestPath(config.getModuleRequestPath());
        context.setModel(getModel(config));
        initContext(context, config);
        if ("custom".equals(config.getTemplateType())) {
            context.setTemplateDir(config.getCustomTemplateDir());
        } else {
            context.setDefault(true);
        }
        context.setConfigJson(JsonUtils.json2Object(config.getConfigJson(), ConfigJson.class));
        return context;
    }

    public static void initContext(Context context, CodeGeneratorConfig config) {
        int type = 0;
        if ("ENTITY".equals(config.getGenerateType())) {
            type = Type.GENTYPE_ENTITY;
            JSONObject jsonObject = JSONObject.fromObject(config.getConfigJson());
            String ary = jsonObject.getString("className");
            context.setParam(EntitySniffer.PARAM_CLAZZ, ary);
        } else if ("FLOW_DEFINITION".equals(config.getGenerateType())) {
            JSONObject jsonObject = JSONObject.fromObject(config.getConfigJson());
            String ary = jsonObject.getString("flowDefUuid");
            context.setParam(FlowSniffer.PARAM_FLOWS, ary);
            type = Type.GENTYPE_FLOW_DEFINITION;
        } else if ("TABLE".equals(config.getGenerateType())) {
            type = Type.GENTYPE_TABLE;
            JSONObject jsonObject = JSONObject.fromObject(config.getConfigJson());
            String ary = jsonObject.getString("tableName");
            context.setParam(TableSniffer.PARAM_TB, ary);
        } else if ("DYFORM_DEFINITION".equals(config.getGenerateType())) {
            type = Type.GENTYPE_DYFORM_DEFINITION;
            JSONObject jsonObject = JSONObject.fromObject(config.getConfigJson());
            String ary = jsonObject.getString("formDefUuid");
            context.setParam(DyformSniffer.PARAM_DYFORMS, ary);
        } else {
            throw new RuntimeException(config.getGenerateType() + " is not support！");
        }
        context.setType(type);
    }

    public static int getModel(CodeGeneratorConfig config) {
        if ("DYFORM_DEFINITION".equals(config.getGenerateType())) {
            JSONObject jsonObject = JSONObject.fromObject(config.getConfigJson());
            JSONArray ary = jsonObject.getJSONArray("formOutputType");
            return getModel(ary);
        }
        if ("ENTITY".equals(config.getGenerateType())) {
            JSONObject jsonObject = JSONObject.fromObject(config.getConfigJson());
            JSONArray ary = jsonObject.getJSONArray("classOutputType");
            return getModel(ary);
        }
        if ("FLOW_DEFINITION".equals(config.getGenerateType())) {
            JSONObject jsonObject = JSONObject.fromObject(config.getConfigJson());
            JSONArray ary = jsonObject.getJSONArray("flowOutputType");
            return getModel(ary);
        }
        if ("TABLE".equals(config.getGenerateType())) {
            JSONObject jsonObject = JSONObject.fromObject(config.getConfigJson());
            JSONArray ary = jsonObject.getJSONArray("tableOutputType");
            return getModel(ary);
        }

        throw new RuntimeException(config.getGenerateType() + " is not support！");
    }

    public static int getModel(JSONArray array) {
        int model = 0;
        String[] tStrings = (String[]) JSONArray.toArray(array, String.class);
        for (String st : tStrings) {
            if ("ENTITY".equals(st)) {
                model += Type.OUTPUTTYPE_ENTITY;
            } else if ("DATABASE_TABLE".equals(st)) {
                model += Type.OUTPUTTYPE_DATABASE_TABLE;
            } else if ("BASIC_SERVICE".equals(st)) {
                model += Type.OUTPUTTYPE_BASIC_SERVICE;
            } else if ("VALUE_OBJECT".equals(st)) {
                model += Type.OUTPUTTYPE_VALUE_OBJECT;
            } else if ("FACADE_SERVICE".equals(st)) {
                model += Type.OUTPUTTYPE_FACADE_SERVICE;
            } else if ("BAM".equals(st)) {
                model += Type.OUTPUTTYPE_BAM;
            } else if ("FRONT_PAGE_VIEW_MAINTAIN".equals(st)) {
                model += Type.OUTPUTTYPE_FRONT_PAGE_VIEW_MAINTAIN;
            } else if ("NAVIGATION".equals(st)) {
                model += Type.OUTPUTTYPE_NAVIGATION;
            } else if ("RESOURCE".equals(st)) {
                model += Type.OUTPUTTYPE_RESOURCE;
            } else if ("FLOWLISTENER".equals(st)) {
                model += Type.OUTPUTTYPE_FLOW_LISTENER;
            } else if ("DIRECTIONLISTENER".equals(st)) {
                model += Type.OUTPUTTYPE_DIRECTION_LISTENER;
            } else if ("TASKLISTENER".equals(st)) {
                model += Type.OUTPUTTYPE_TASK_LISTENER;
            } else if ("DEVJS".equals(st)) {
                model += Type.OUTPUTTYPE_DEV_JS;
            } else if ("DYFORM_MAINTAIN".equals(st)) {
                model += Type.OUTPUTTYPE_DYFORM_MAINTAIN;
            } else {
                throw new RuntimeException(st + " is not support！");
            }
        }

        return model;
    }

}
