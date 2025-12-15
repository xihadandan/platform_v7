/*
 * @(#)2014-8-11 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.FileUploadRequest;
import com.wellsoft.pt.api.response.FileUploadResponse;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author linz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-11.1	zhulh		2014-8-11		Create
 * </pre>
 * @date 2015年6月26日 14:49:49
 */
@Service(ApiServiceName.FILEUPLOAD)
@Transactional
public class FileUploadServiceImpl extends BaseServiceImpl implements WellptService<FileUploadRequest> {

    @Autowired
    private MongoFileService mongoFileService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(FileUploadRequest fileUploadApiRequest) {
        FileUploadResponse response = new FileUploadResponse();
        MongoFileEntity mongoFileEntity = mongoFileService.saveFile(fileUploadApiRequest.getFileName(),
                fileUploadApiRequest.getInputStream());
        String folderId = fileUploadApiRequest.getFolderID();
        List<String> idList = new ArrayList<String>();
        String fileId = mongoFileEntity.getFileID();
        idList.add(fileId);
        if (folderId != null && !"".equals(folderId)) {
            mongoFileService.pushFilesToFolder(folderId, idList, fileUploadApiRequest.getPurpose());
        }
        response.setFileId(fileId);
        response.setMsg("sucess");
        response.setSuccess(true);
        return response;
    }
}
