/*
 * @(#)2019年5月14日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.license.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.servlet.License;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.security.license.service.LicenseService;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Map;
import java.util.prefs.Preferences;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年5月14日.1	zhulh		2019年5月14日		Create
 * </pre>
 * @date 2019年5月14日
 */
@Service
public class LicenseServiceImpl implements LicenseService, InitializingBean {

    private Map<String, Object> licenseInfo = Maps.newHashMap();

    @Override
    public Map<String, Object> getLicenseInfo() {
        return licenseInfo;
    }

    /**
     * Invoked by the containing {@code BeanFactory} after it has set all bean properties
     * and satisfied {@link BeanFactoryAware}, {@code ApplicationContextAware} etc.
     * <p>This method allows the bean instance to perform validation of its overall
     * configuration and final initialization when all bean properties have been set.
     *
     * @throws Exception in the event of misconfiguration (such as failure to set an
     *                   essential property) or if initialization fails for any other reason
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Preferences preferences = Preferences.userNodeForPackage(License.class);
        String licenseContent = new String(preferences.getByteArray("content", null), StandardCharsets.UTF_8);
        Map<String, Object> licenseMap = JsonUtils.json2Object(licenseContent, Map.class);

        Calendar calendar = Calendar.getInstance();
        int runningDay = preferences.getInt("runningDay", 10);
        long startTime = preferences.getLong("startTime", calendar.getTimeInMillis());
        calendar.setTimeInMillis(startTime);
        String profile = BooleanUtils.isTrue((Boolean) licenseMap.get("validate")) ? Config.ENV_PRD : Config.ENV_DEV;
        licenseInfo.put("profile", profile);
        licenseInfo.put("runningDay", runningDay);
        licenseInfo.put("startTime", calendar.getTime());
    }
}
