package com.wellsoft.pt.common.i18n;

import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.cache.Cache;
import com.wellsoft.pt.cache.CacheManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Map;

public class AppCodeI18nMessageSource {

    public static String getMessage(String code, String applyTo) {
        return getMessage(code, applyTo, null);
    }

    public static String getMessage(String code, String applyTo, String locale) {
        CacheManager cacheManager = ApplicationContextHolder.getBean(CacheManager.class);
        Cache cache = cacheManager.getCache(ModuleID.I18N);
        String key = String.format("%s%s%s%s%s%s%s", "i18n", Separator.COLON.getValue(), applyTo, Separator.COLON.getValue(), code, Separator.COLON.getValue(), StringUtils.defaultIfBlank(locale, LocaleContextHolder.getLocale().toString()));
        return cache.get(key, String.class);
    }

    public static String getMessage(String code, String applyTo, String locale, Map<String, Object> params) {
        String msg = getMessage(code, applyTo, locale);
        if (StringUtils.isNotEmpty(msg)) {
            return new StringSubstitutor(params, "{", "}", '\\').replace(msg);
        }
        return null;
    }

    public static String getMessage(String code, String applyTo, String locale, String defaultValue) {
        CacheManager cacheManager = ApplicationContextHolder.getBean(CacheManager.class);
        Cache cache = cacheManager.getCache(ModuleID.I18N);
        String key = String.format("%s%s%s%s%s%s%s", "i18n", Separator.COLON.getValue(), applyTo, Separator.COLON.getValue(), code, Separator.COLON.getValue(), StringUtils.defaultIfBlank(locale, LocaleContextHolder.getLocale().toString()));
        return StringUtils.defaultIfBlank(cache.get(key, String.class), defaultValue);
    }

    public static String getMessage(String code, String applyTo, String locale, Map<String, Object> params, String defaultValue) {
        String msg = getMessage(code, applyTo, locale);
        if (StringUtils.isNotEmpty(msg)) {
            return new StringSubstitutor(params, "{", "}", '\\').replace(msg);
        }
        return defaultValue;
    }
}
