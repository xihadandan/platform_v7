/*
 * @(#)2016年8月24日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.function.ext;

import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.function.AbstractAppFunctionSourceLoader;
import com.wellsoft.pt.app.function.AppFunctionSource;
import com.wellsoft.pt.app.function.SimpleAppFunctionSource;
import com.wellsoft.pt.app.js.JavaScriptTemplate;
import com.wellsoft.pt.app.support.AppFunctionType;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: JavaScript模板功能加载
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年8月24日.1	zhulh		2016年8月24日		Create
 * </pre>
 * @date 2016年8月24日
 */
@Service
@Transactional(readOnly = true)
public class JavaScriptTemplateAppFunctionSourceLoader extends AbstractAppFunctionSourceLoader {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionType()
     */
    @Override
    public String getAppFunctionType() {
        return AppFunctionType.JavaScriptTemplate;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionSources()
     */
    @Override
    public List<AppFunctionSource> getAppFunctionSources() {
        List<AppFunctionSource> appFunctionSources = new ArrayList<AppFunctionSource>();
        List<JavaScriptTemplate> javaScriptTemplates = AppContextHolder.getContext().getAllJavaScriptTemplates();
        for (JavaScriptTemplate javaScriptTemplate : javaScriptTemplates) {
            String uuid = DigestUtils.md5Hex(javaScriptTemplate.getId() + getAppFunctionType());
            String fullName = javaScriptTemplate.getName();
            String name = "JavaScript模板功能_" + javaScriptTemplate.getName();
            String id = javaScriptTemplate.getId();
            String code = javaScriptTemplate.getId();
            String category = getAppFunctionType();
            String content = javaScriptTemplate.getContent();
            Map<String, Object> extras = new HashMap<String, Object>();
            extras.put("jsTemplate", id);
            extras.put("id", id);
            if (StringUtils.length(content) <= 3000) {
                extras.put("content", content);
            } else {
                extras.put("content", StringUtils.substring(content, 0, 3000) + "...");
            }
            appFunctionSources.add(new SimpleAppFunctionSource(uuid, fullName, name, id, code, null, null, category,
                    false, category, false, extras));
        }
        return appFunctionSources;
    }

}
