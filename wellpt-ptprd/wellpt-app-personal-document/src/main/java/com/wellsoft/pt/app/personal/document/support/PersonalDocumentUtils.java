/*
 * @(#)Jan 31, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.personal.document.support;

import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Jan 31, 2018.1	zhulh		Jan 31, 2018		Create
 * </pre>
 * @date Jan 31, 2018
 */
public class PersonalDocumentUtils {

    /**
     * 返回个人文档所在的根目录UUID
     *
     * @return
     */
    public static String getRootFolderUuid() {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        String rootFolderId = "PersonalDocument_" + userDetails.getSystemUnitId();
        String rootFolderUuid = DigestUtils.md5Hex(rootFolderId);
        return rootFolderUuid;
    }

    /**
     * 返回我的文档UUID
     *
     * @return
     */
    public static String getMyFolderUuid() {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        String userId = userDetails.getUserId();
        String myFolderUuid = DigestUtils.md5Hex(userId);
        return myFolderUuid;
    }

}
