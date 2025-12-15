/*
 * @(#)Aug 18, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.news.support;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.dms.facade.api.DmsFileServiceFacade;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import org.apache.commons.lang.StringUtils;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Aug 18, 2017.1	zhulh		Aug 18, 2017		Create
 * </pre>
 * @date Aug 18, 2017
 */
public class NewsUtils {

    /**
     * @param userId
     * @param folderUuid
     * @param dyFormData
     * @return
     */
    public static boolean isNewsCreatorOrAdmin(String userId, String folderUuid, DyFormData dyFormData) {
        DmsFileServiceFacade dmsFileServiceFacade = ApplicationContextHolder.getBean(DmsFileServiceFacade.class);
        // 判断当前用户对夹是否有管理员角色
        boolean hasAdminRole = dmsFileServiceFacade.hasRole(userId, folderUuid, "APP_NEWS_ADMIN");
        if (hasAdminRole) {
            return hasAdminRole;
        }

        String creator = (String) dyFormData.getFieldValue(IdEntity.CREATOR);
        hasAdminRole = StringUtils.equals(userId, creator);
        return hasAdminRole;
    }

}
