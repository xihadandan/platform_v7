package com.wellsoft.pt.cg.core;

import com.wellsoft.pt.cg.core.source.Source;
import com.wellsoft.pt.cg.core.support.ConfigJson;

import java.util.HashMap;
import java.util.Map;

/**
 * 上下文
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
public final class Context {
    private static final String OBLIGATE_DIR_JS = "js";// js目录
    private static final String OBLIGATE_DIR_JSP = "jsp";// jsp目录
    private static final String OBLIGATE_DIR_JAVA = "java";// java目录
    private static final String OBLIGATE_GETYPE = "gentype";// 生成类型
    private static final String OBLIGATE_PG = "pg";// 包名
    private static final String OBLIGATE_MODULE_REQUEST_PATH = "mrp";// 模块请求路径
    private Source source;// 数据，由嗅探器获取
    private String templateDir;// 模板文件地址
    private int model;// 模型
    private int type;// 类型
    private String name;// 模板名称
    private String author;// 作者
    private boolean isDefault = false;// 是否使用默认模板
    private Map<String, Object> params = new HashMap<String, Object>();// 额外参数
    private ConfigJson configJson;

    public String getTemplateDir() {
        return templateDir;
    }

    public void setTemplateDir(String templateDir) {
        this.templateDir = templateDir;
    }

    public String getJSOutputDir() {
        return (String) params.get(OBLIGATE_DIR_JS);
    }

    public void setJSOutputDir(String dir) {
        params.put(OBLIGATE_DIR_JS, dir);
    }

    public String getJavaOutputDir() {
        return (String) params.get(OBLIGATE_DIR_JAVA);
    }

    public void setJavaOutputDir(String dir) {
        params.put(OBLIGATE_DIR_JAVA, dir);
    }

    public String getJSPOutputDir() {
        return (String) params.get(OBLIGATE_DIR_JSP);
    }

    public void setJSPOutputDir(String dir) {
        params.put(OBLIGATE_DIR_JSP, dir);
    }

    public String getPackage() {
        return (String) params.get(OBLIGATE_PG);
    }

    public void setPackage(String type) {
        params.put(OBLIGATE_PG, type);
    }

    public String getModuleRequestPath() {
        return (String) params.get(OBLIGATE_MODULE_REQUEST_PATH);
    }

    public void setModuleRequestPath(String moduleRequestPath) {
        params.put(OBLIGATE_MODULE_REQUEST_PATH, moduleRequestPath);
    }

    public void setParam(String key, Object value) {
        if (OBLIGATE_DIR_JSP.equalsIgnoreCase(key) || OBLIGATE_DIR_JAVA.equalsIgnoreCase(key)
                || OBLIGATE_DIR_JS.equalsIgnoreCase(key) || OBLIGATE_GETYPE.equalsIgnoreCase(key)
                || OBLIGATE_PG.equalsIgnoreCase(key)) {
            throw new RuntimeException("The key can not be used");
        }
        params.put(key, value);
    }

    public Object getParam(String key) {
        return params.get(key);
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public int getModel() {
        return model;
    }

    public void setModel(int model) {
        this.model = model;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    /**
     * @return the configJson
     */
    public ConfigJson getConfigJson() {
        return configJson;
    }

    /**
     * @param configJson 要设置的configJson
     */
    public void setConfigJson(ConfigJson configJson) {
        this.configJson = configJson;
    }

}
