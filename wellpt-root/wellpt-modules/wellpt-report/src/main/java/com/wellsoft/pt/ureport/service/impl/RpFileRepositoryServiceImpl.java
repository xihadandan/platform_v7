package com.wellsoft.pt.ureport.service.impl;

import com.bstek.ureport.provider.report.ReportFile;
import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.util.UuidUtils;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.ureport.dao.RpFileRepositoryDaoImpl;
import com.wellsoft.pt.ureport.entity.RpFileRepositoryEntity;
import com.wellsoft.pt.ureport.service.RpFileRepositoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.rowset.serial.SerialClob;
import java.io.InputStream;
import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/9/25
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/9/25    chenq		2018/9/25		Create
 * </pre>
 */
@Service
public class RpFileRepositoryServiceImpl extends
        AbstractJpaServiceImpl<RpFileRepositoryEntity, RpFileRepositoryDaoImpl, String> implements
        RpFileRepositoryService {
    @Autowired
    MongoFileService mongoFileService;

    @Override
    public RpFileRepositoryEntity getByFileName(String fileName) {
        List<RpFileRepositoryEntity> entities = this.dao.listByFieldEqValue("fileName", fileName);
        return CollectionUtils.isNotEmpty(entities) ? entities.get(0) : null;
    }

    @Override
    public InputStream getRpFileInputStream(String fileName) {
        RpFileRepositoryEntity entity = this.getByFileName(
                fileName);
        if (entity != null && StringUtils.isNotBlank(entity.getFileId())) {
            return mongoFileService.getFile(entity.getFileId()).getInputstream();
        } else if (entity != null && StringUtils.isBlank(entity.getFileId())) {
            try {
                return IOUtils.toInputStream(
                        IOUtils.toString(entity.getContent().getCharacterStream()), Charsets.UTF_8);
            } catch (Exception e) {
                logger.error("获取报表内容输入流异常：", e);
            }
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteRpFile(String file) {
        RpFileRepositoryEntity entity = this.getByFileName(file);
        if (entity != null && StringUtils.isNotBlank(entity.getFileId())) {
            try {
                mongoFileService.deleteFile(entity.getFileId());
            } catch (Exception e) {
            }
            this.delete(entity);

        }
    }

    @Override
    public List<ReportFile> getAllReportFiles(String purpose) {
        List<RpFileRepositoryEntity> rpFileRepositoryEntities = "MongoDB".equalsIgnoreCase(
                purpose) ? this.dao.listByFieldIsNotNull("fileId") : this.dao.listByFieldIsNull(
                "fileId");
        List<ReportFile> files = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(rpFileRepositoryEntities)) {
            for (RpFileRepositoryEntity entity : rpFileRepositoryEntities) {
                ReportFile file = new ReportFile(entity.getFileName(), entity.getModifyTime());
                files.add(file);
            }
        }
        return files;
    }

    @Override
    @Transactional
    public void saveRpFile(String file, String content, String purpose) {
        RpFileRepositoryEntity existRpFile = this.getByFileName(file);
        String fileId = UuidUtils.createUuid();
        RpFileRepositoryEntity rpFileRepositoryEntity = new RpFileRepositoryEntity();
        if (existRpFile != null) {
            rpFileRepositoryEntity = existRpFile;
            fileId = existRpFile.getFileId();
        }
        if ("MongoDB".equalsIgnoreCase(purpose)) {
            MongoFileEntity fileEntity = mongoFileService.saveFile(fileId, file,
                    IOUtils.toInputStream(content, Charsets.UTF_8));
            rpFileRepositoryEntity.setFileId(fileEntity.getFileID());
        } else {
            try {
                rpFileRepositoryEntity.setContent(new SerialClob(content.toCharArray()));
            } catch (Exception e) {
                logger.error("保存报表内容字符串到数据库Content字段异常：", e);
            }
        }
        rpFileRepositoryEntity.setFileName(file);
        this.save(rpFileRepositoryEntity);
    }

    @Override
    @Transactional
    public void deleteRps(List<String> uuids) {
        List<RpFileRepositoryEntity> entities = this.listByUuids(uuids);
        for (RpFileRepositoryEntity entity : entities) {
            if (entity != null && StringUtils.isNotBlank(entity.getFileId())) {
                try {
                    mongoFileService.deleteFile(entity.getFileId());
                } catch (Exception e) {
                }

            }
            this.delete(entity);
        }

    }

    @Override
    public RpFileRepositoryEntity getDetail(String uuid) {
        RpFileRepositoryEntity entity = getOne(uuid);
        if (entity.getContent() != null) {
            try {
                entity.setContentStr(IOUtils.toString(entity.getContent().getCharacterStream()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
        return entity;
    }

    @Override
    public List<RpFileRepositoryEntity> listLikeFileName(String fileName) {
        return StringUtils.isNotBlank(fileName) ? this.dao.listByFieldAnyLike("fileName", fileName)
                : this.dao.listByEntityAndPage(new RpFileRepositoryEntity(), new PagingInfo(0, 10), "modifyTime desc");
    }
}
