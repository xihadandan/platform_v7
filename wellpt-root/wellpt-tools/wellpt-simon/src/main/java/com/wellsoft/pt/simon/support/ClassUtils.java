/*
 * @(#)2019年11月24日 V1.0
 * 
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.simon.support;

import org.apache.commons.lang3.StringUtils;

/**
 * Description: 如何描述该类
 *  
 * @author zhongzh
 * @date 2019年11月24日
 * @version 1.0
 * 
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年11月24日.1	zhongzh		2019年11月24日		Create
 * </pre>
 *
 */
public class ClassUtils {

    public static String getAbbreviatedName(final Class<?> cls) {
        if (cls == null) {
            return "";
        }
        return getAbbreviatedName(cls.getName());
    }

    public static String getAbbreviatedName(final String className) {
        if (className == null) {
            return "";
        }
        int packageLevels = StringUtils.countMatches(className, ".");
        String[] output = new String[packageLevels + 1];
        int endIndex = className.length() - 1;
        for (int level = packageLevels; level >= 0; level--) {
            int startIndex = className.lastIndexOf('.', endIndex);
            String part = className.substring(startIndex + 1, endIndex + 1);
            // ClassName is always complete
            // if no space is left still the first char is used
            output[level] = level == packageLevels ? part : part.substring(0, 1);
            endIndex = startIndex - 1;
        }
        return StringUtils.join(output, '.');
    }

    public static String shiftPrefix(final Class<?> cls, final String prefix) {
        if (cls == null) {
            return "";
        }
        return shiftPrefix(cls.getName(), prefix);
    }

    public static String shiftPrefix(final String className, final String prefix) {
        if (className == null) {
            return "";
        }
        return prefix == null ? className : className.substring(prefix.length());
    }
}
