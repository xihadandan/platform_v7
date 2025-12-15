package com.wellsoft.pt.basicdata.params.facade;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.params.entity.SysParamItem;
import com.wellsoft.pt.cache.Cache;
import com.wellsoft.pt.cache.CacheManager;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Description:
 * 此方法不应用于bean的构造方法中，对于静态方法部分适用。
 * 调用此方法必须保证SysParamInit以初始化
 *
 * @author Lmw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-07-20.1	Lmw		2015-07-20		Create
 * </pre>
 * @date 2015-07-20
 */
public class SystemParams {
    private static final String SYSTEMPARAM_ID = "SystemParam";

    public static String getCacheKey(String key) {
        return SYSTEMPARAM_ID + "." + key;
    }

    public static String getValue(String key) {
        String value = Config.getValue(key);
        if (StringUtils.isBlank(value)) {
            value = System.getProperty(key);
            if (StringUtils.isBlank(value)) {
                CacheManager cacheManager = ApplicationContextHolder.getBean(CacheManager.class);
                Cache cache = cacheManager.getCache(ModuleID.SECURITY);
                String cacheKey = getCacheKey(key);
                value = (String) cache.getValue(cacheKey);
                if (StringUtils.isBlank(value)) {
                    SysParamItemConfigMgr sysParamItemService = ApplicationContextHolder
                            .getBean(SysParamItemConfigMgr.class);
                    List<SysParamItem> sysParamItems = sysParamItemService.listByKey(key);
                    if (sysParamItems.isEmpty()) {
                        return value;
                    } else {
                        value = sysParamItems.get(0).getValue();
                        cache.put(cacheKey, value);
                    }
                }
            }
        }
        return value;
    }

    public static String getValue(String key, String defaultValue) {
        String value = getValue(key);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        } else {
            return value;
        }
    }
}
