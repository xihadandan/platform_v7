/*
 * @(#)2013-12-9 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bm.service.impl;

import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.pt.bm.dao.PublicityAttachDao;
import com.wellsoft.pt.bm.entity.PublicityAttach;
import com.wellsoft.pt.bm.service.SelfPublicityAsyncService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-9.1	zhulh		2013-12-9		Create
 * </pre>
 * @date 2013-12-9
 */
@Service
@Transactional
public class SelfPublicityAsyncServiceImpl extends BaseServiceImpl implements SelfPublicityAsyncService {

    private static final String QUERY_PUBLICITY_ATTACH = "from PublicityAttach publicity_attach where publicity_attach.puuid = :puuid";

    @Autowired
    private PublicityAttachDao publicityAttachDao;

    @Autowired
    private MongoFileService mongoFileService;

    /**
     * (non-Javadoc)
     *
     * @throws Exception
     * @see com.wellsoft.pt.bm.service.SelfPublicityAsyncService#asyncAttach(java.lang.String)
     */
    @Override
    public void asyncAttach(String uuid) throws Exception {
        PublicityAttach example = new PublicityAttach();
        example.setPuuid(uuid);
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("puuid", uuid);
        List<PublicityAttach> publicityAttachs = publicityAttachDao.listByHQL(QUERY_PUBLICITY_ATTACH, values);

        // 同步JCR文件
        List<MongoFileEntity> fileEntities = mongoFileService.getFilesFromFolder(uuid, null);

        // 1、获取要上传到JCR的附件
        List<PublicityAttach> uploadAttachs = getUploadAttachs(publicityAttachs, fileEntities);
        // 增加
        if (fileEntities == null || fileEntities.isEmpty()) {
            for (PublicityAttach publicityAttach : uploadAttachs) {
                Blob fileBlob = publicityAttach.getBody();
                if (fileBlob == null) {
                    continue;
                }

                // 附件上传到JCR
                mongoFileService.saveFile(publicityAttach.getFjmc(), publicityAttach.getBody().getBinaryStream());

                // 删除数据库中附件数据
                publicityAttach.setBody(null);
                publicityAttachDao.save(publicityAttach);
            }
        }

        // 2、获取要从JCR中删除的附件
        List<MongoFileEntity> deleteEntities = getDeleteEntities(publicityAttachs, fileEntities);
        for (MongoFileEntity fileEntity : deleteEntities) {
            mongoFileService.deleteFile(fileEntity.getId());
        }
    }

    /**
     * @param publicityAttachs
     * @param fileEntities
     * @return
     */
    private List<PublicityAttach> getUploadAttachs(List<PublicityAttach> publicityAttachs,
                                                   List<MongoFileEntity> fileEntities) {
        List<PublicityAttach> uploadAttachs = new ArrayList<PublicityAttach>();
        Map<String, MongoFileEntity> fileEntityMap = ConvertUtils.convertElementToMap(fileEntities, "filename");
        for (PublicityAttach publicityAttach : publicityAttachs) {
            if (StringUtils.isBlank(publicityAttach.getFjmc()) || publicityAttach.getBody() == null) {
                continue;
            }

            if (fileEntityMap.containsKey(publicityAttach.getFjmc())) {
                continue;
            }

            uploadAttachs.add(publicityAttach);
        }

        return uploadAttachs;
    }

    /**
     * @param publicityAttachs
     * @param fileEntities
     * @return
     */
    private List<MongoFileEntity> getDeleteEntities(List<PublicityAttach> publicityAttachs,
                                                    List<MongoFileEntity> fileEntities) {
        List<MongoFileEntity> deleteEntities = new ArrayList<MongoFileEntity>();
        Map<String, PublicityAttach> publicityAttachMap = ConvertUtils.convertElementToMap(publicityAttachs, "fjmc");

        for (MongoFileEntity fileEntity : fileEntities) {
            if (publicityAttachMap.containsKey(fileEntity.getFileName())) {
                continue;
            }

            deleteEntities.add(fileEntity);
        }
        return deleteEntities;
    }
}
