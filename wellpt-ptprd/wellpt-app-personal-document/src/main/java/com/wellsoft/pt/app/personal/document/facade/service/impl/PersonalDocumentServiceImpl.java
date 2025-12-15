/*
 * @(#)Jan 31, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.personal.document.facade.service.impl;

import com.wellsoft.pt.app.personal.document.facade.service.PersonalDocumentService;
import com.wellsoft.pt.app.personal.document.support.PersonalDocumentUtils;
import com.wellsoft.pt.common.marker.service.ReadMarkerService;
import com.wellsoft.pt.dms.facade.api.DmsFileServiceFacade;
import com.wellsoft.pt.dms.model.DmsFile;
import com.wellsoft.pt.dms.model.DmsFolder;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Service
@Transactional
public class PersonalDocumentServiceImpl extends BaseServiceImpl implements PersonalDocumentService {

    @Autowired
    private DmsFileServiceFacade dmsFileServiceFacade;

    @Autowired
    private ReadMarkerService readMarkerService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.personal.document.service.PersonalDocumentService#getMyFolder()
     */
    @Override
    public DmsFolder getMyFolder() {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        String userId = userDetails.getUserId();
        String myFolderUuid = DigestUtils.md5Hex(userId);
        DmsFolder dmsFolder = dmsFileServiceFacade.getFolder(myFolderUuid);
        if (dmsFolder == null) {
            dmsFolder = createMyFolder(myFolderUuid);
        }
        return dmsFolder;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.personal.document.facade.service.PersonalDocumentService#createMyFolder(java.lang.String)
     */
    @Override
    public DmsFolder createMyFolder(String myFolderUuid) {
        String rootFolderUuid = PersonalDocumentUtils.getRootFolderUuid();
        DmsFolder rootFolder = dmsFileServiceFacade.getFolderWithoutPermission(rootFolderUuid);
        // 创建个人文档根夹
        if (rootFolder == null) {
            UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
            MultiOrgSystemUnit systemUnit = userDetails.getSystemUnit();
            String personalRootFolderName = "个人文档";
            if (systemUnit != null) {
                personalRootFolderName = systemUnit.getName() + "_" + personalRootFolderName;
            }
            dmsFileServiceFacade.createFolder(rootFolderUuid, personalRootFolderName);
        }
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        // 创建我的文件夹
        dmsFileServiceFacade.createFolder(myFolderUuid, "我的文件夹_" + userDetails.getLoginName(), rootFolderUuid);
        // 添加个人文件夹的私有管理权限
        dmsFileServiceFacade.addUserPrivateAdminPermission(myFolderUuid);
        return dmsFileServiceFacade.getFolder(myFolderUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.personal.document.facade.service.PersonalDocumentService#recordRecentVisitFile(com.wellsoft.pt.dms.model.DmsFile)
     */
    @Override
    public void recordRecentVisitFile(DmsFile dmsFile) {
        readMarkerService.markRead(dmsFile.getUuid(), SpringSecurityUtils.getCurrentUserId());
    }

}
