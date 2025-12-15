/*
 * @(#)2020年6月10日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.params.facade;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.params.bean.SysParamItemBean;
import com.wellsoft.pt.basicdata.params.core.precompressor.PrecompressorCenter;
import com.wellsoft.pt.basicdata.params.entity.SysParamItem;
import com.wellsoft.pt.cache.Cache;
import com.wellsoft.pt.cache.CacheManager;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * Description: ModuleConfig提供配置项模块化管理，以数据库配置优先，然后固话的配置文件
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年6月10日.1	zhongzh		2020年6月10日		Create
 * </pre>
 * @see:com.wellsoft.pt.basicdata.params.facade.SystemParams以配置文件优先
 * @date 2020年6月10日
 */
public abstract class ModuleConfig {

    private final static ConcurrentMap<String, ModuleConfig> modules = Maps.newConcurrentMap();

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected String module;
    protected String prefix;
    protected Cache cache;
    protected SysParamItemConfigMgr sysParamItemConfigMgr;

    /**
     * @param module
     * @param prefix
     */
    public ModuleConfig(String module, String prefix) {
        Assert.hasText(prefix, "prefix不能为空");
        Assert.hasText(module, "module不能为空");
        Assert.isNull(modules.putIfAbsent(module, this), "module[" + module + "]已经存在");
        this.module = module;
        this.prefix = prefix;
        this.sysParamItemConfigMgr = ApplicationContextHolder.getBean(SysParamItemConfigMgr.class);
        this.cache = ApplicationContextHolder.getBean(CacheManager.class).getCache(ModuleID.SECURITY);
    }

    public static final Set<String> getModuleKeySet() {
        return modules.keySet();
    }

    public static final String getDbKey(String module, String key) {
        return module + "." + key;
    }

    public static final String getPropKey(String module, String key) {
        ModuleConfig moduleConfig = modules.get(module);
        return moduleConfig == null ? null : moduleConfig.getPropKey(key);
    }

    public static final String getModuleValue(String module, String key) {
        ModuleConfig moduleConfig = modules.get(module);
        return moduleConfig == null ? null : moduleConfig.getValue(key);
    }

    public static final Map<String, String> getModuleConfig(String module) {
        ModuleConfig moduleConfig = modules.get(module);
        return moduleConfig == null ? null : moduleConfig.getConfig();
    }

    public static final boolean saveModuleConfig(String module, Map<String, String> params) {
        ModuleConfig moduleConfig = modules.get(module);
        return moduleConfig == null ? false : moduleConfig.saveConfig(params);
    }

    public final String getDbKey(String key) {
        return getDbKey(module, key);
    }

    public final String getPropKey(String key) {
        return prefix + "." + key;
    }

    public final String getValue(String key) {
        String dbKey = getDbKey(key);
        String cacheKey = SystemParams.getCacheKey(dbKey);
        String value = (String) cache.getValue(cacheKey);
        if (value == null) {
            synchronized (this) {
                value = (String) cache.getValue(cacheKey);
                if (null == value) {// 读取系统参数
                    value = sysParamItemConfigMgr.getValueByKey(dbKey);
                }
                if (null == value) {// 读取配置文件
                    value = Config.getValue(getPropKey(key));
                }
                cache.put(cacheKey, value);
            }
        }
        return value;
    }

    public final String getValue(String key, String defaultValue) {
        String value = getValue(key);
        return value == null ? defaultValue : value;
    }

    public final void saveValue(String key, String value) {
        String dbKey = getDbKey(key);
        SysParamItemBean bean = new SysParamItemBean();
        List<SysParamItem> list = sysParamItemConfigMgr.listByKey(dbKey);
        if (false == CollectionUtils.isEmpty(list)) {
            BeanUtils.copyProperties(list.get(0), bean);
        } else if (StringUtils.equals(value, Config.getValue(getPropKey(key)))) {
            return;// (未保存数据库且值相等)忽略默认配置
        } else {
            // 允许名称编号等配置项重定义
            bean.setCode(dbKey);
            bean.setType(dbKey);
            bean.setName(dbKey);
            bean.setKey(dbKey);
        }
        bean.setValue(value);
        bean.setSourcetype(PrecompressorCenter.DB);
        // 带缓存刷新
        sysParamItemConfigMgr.saveBean(bean);
    }

    /**
     * 获取模块配置项的KEY
     *
     * @return
     */
    public final Set<String> keySet() {
        Set<String> keys = Config.keySet();
        Set<String> rKeys = Sets.newHashSet();
        int prefixLength = prefix.length() + 1;
        for (String key : keys) {
            if (StringUtils.startsWith(key, prefix)) {
                rKeys.add(key.substring(prefixLength));
            }
        }
        return rKeys;
    }

    /**
     * 获取配置项
     *
     * @return
     */
    public final Map<String, String> getConfig() {
        Set<String> keys = keySet();
        Map<String, String> params = Maps.newHashMap();
        for (String key : keys) {
            params.put(key, getValue(key));
        }
        return params;
    }

    /**
     * 保存配置，如果与默认值有差异，则保存数据库
     *
     * @param params
     */
    public final boolean saveConfig(Map<String, String> params) {
        Set<String> keys = keySet();
        Assert.notEmpty(params, "params不能为空");
        for (String key : params.keySet()) {
            String value = null;
            if (false == keys.contains(key) || (value = params.get(key)) == null) {
                continue;// 忽略配置文件中没有的配置项
            }
            saveValue(key, value);
        }
        return true;
    }
}
