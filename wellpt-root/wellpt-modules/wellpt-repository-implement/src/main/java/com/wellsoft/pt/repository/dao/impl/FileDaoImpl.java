package com.wellsoft.pt.repository.dao.impl;

import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.UuidUtils;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.jpa.hibernate.HibernateDao;
import com.wellsoft.pt.jpa.support.QueryItemResultTransformer;
import com.wellsoft.pt.repository.dao.FileDao;
import com.wellsoft.pt.repository.dao.base.BaseMongoDao;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.support.BeanMapUtils;
import com.wellsoft.pt.repository.support.enums.EnumReplicaType;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.security.util.TenantContextHolder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.Resource;
import java.beans.IntrospectionException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 文件操作接口
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-3-16.1	Hunt		2014-3-16		Create
 * </pre>
 * @date 2014-3-16
 */

@Repository
public class FileDaoImpl extends HibernateDao<LogicFileInfo, String> implements FileDao {

    private static final String FILE_IN_USED = "from LogicFileInfo where physicalFileId = :physicalFileId and uuid != :fileID";
    private final String PHYSICAL_FILE_TABLE = "fs";
    private final String PHYSICAL_REPLICA_FILE_TABLE = "fs.replica";
    private final String LOGIC_FILE_TABLE = "repo_file_log";
    @Resource(name = "nativeDao")
    protected NativeDao nativeDao;
    @Autowired
    private BaseMongoDao baseMongoDao;
    private String currentTenantId = null;

    public FileDaoImpl() {

    }

    @Override
    public void saveChunkFile(String dbName, String fileID, String fileName, String md5, int chunkIndex, int chunkSize,
                              long fileSize, InputStream inputStream) {
        Assert.notNull(dbName, "parameter[dbName]  is null");
        Assert.notNull(fileID, "parameter[fileID]  is null");
        Assert.notNull(fileName, "parameter[fileName]  is null");
        Assert.notNull(inputStream, "parameter[inputStream]  is null");
        baseMongoDao.saveChunkFile(fileID, fileName, dbName, getPHYSICAL_TABLE(), md5, chunkIndex, chunkSize, fileSize,
                inputStream);
    }

    @Override
    public List<GridFSDBFile> findFileByMetadataMd5(String dbName, String md5) {
        Assert.notNull(dbName, "parameter[dbName]  is null");
        return baseMongoDao.findFileByMetadataMd5(dbName, getPHYSICAL_TABLE(), md5);
    }

    @Override
    public GridFSDBFile findFileByMd5(String dbName, String md5) {
        Assert.notNull(dbName, "parameter[dbName]  is null");
        return baseMongoDao.findFileByMd5(dbName, getPHYSICAL_TABLE(), md5);
    }

    @Override
    public void deleteByGridFSDBFile(String dbName, GridFSDBFile gridFSDBFile) {
        baseMongoDao.deleteByGridFSDBFile(dbName, getPHYSICAL_TABLE(), gridFSDBFile);
    }

    @Override
    public MongoFileEntity saveFile(String dbName, String fileID, String fileName, String contentType,
                                    InputStream inputStream) {
        return this.saveFile(dbName, fileID, false, fileName, contentType, inputStream, "", "");
    }

    @Override
    public MongoFileEntity saveFile(String dbName, String fileID, Boolean bsMode, String fileName, String contentType,
                                    InputStream inputStream, String fileSourceIcon, String source) {
        Assert.notNull(dbName, "parameter[dbName]  is null");
        Assert.notNull(fileID, "parameter[fileID]  is null");
        Assert.notNull(fileName, "parameter[fileName]  is null");
        Assert.notNull(contentType, "parameter[contentType]  is null");
        Assert.notNull(inputStream, "parameter[inputStream]  is null");
        return this.save(dbName, fileID, bsMode, fileName, contentType, inputStream, "", "", "", "", fileSourceIcon,
                source);
    }

    @Override
    public MongoFileEntity saveFile(String dbName, String fileName, String contentType, InputStream inputStream) {
        Assert.notNull(fileName, "parameter[fileName]  is null");
        Assert.notNull(contentType, "parameter[contentType]  is null");
        Assert.notNull(inputStream, "parameter[inputStream]  is null");

        // FileEntity file = baseMongoDao.saveFile(getPHYSICAL_TABLE(),
        // fileName, contentType, inputStream, null);
        return this.save(dbName, null, fileName, contentType, inputStream, "", "", "", "");
    }

    @Override
    public MongoFileEntity saveFile(String dbName, String fileName, String contentType, InputStream inputStream,
                                    String digest_value, String digest_algorithm, String signature_value, String certificate) {
        Assert.notNull(fileName, "parameter[fileName]  is null");
        Assert.notNull(contentType, "parameter[contentType]  is null");
        Assert.notNull(inputStream, "parameter[inputStream]  is null");

        Assert.notNull(digest_value, "parameter[digest_value]  is null");
        Assert.notNull(digest_algorithm, "parameter[digest_algorithm]  is null");
        Assert.notNull(signature_value, "parameter[signature_value]  is null");
        Assert.notNull(certificate, "parameter[certificate]  is null");

        return this.save(dbName, null, fileName, contentType, inputStream, digest_value, digest_algorithm,
                signature_value, certificate);
    }

    @Override
    public MongoFileEntity saveFile(String dbName, String fileID, String fileName, String contentType,
                                    InputStream inputStream, String digest_value, String digest_algorithm, String signature_value,
                                    String certificate) {
        return this.saveFile(dbName, fileID, fileName, contentType, inputStream, digest_value, digest_algorithm,
                signature_value, certificate, "", "");
    }

    @Override
    public MongoFileEntity saveFile(String dbName, String fileID, String fileName, String contentType,
                                    InputStream inputStream, String digest_value, String digest_algorithm, String signature_value,
                                    String certificate, String fileSourceIcon, String source) {
        Assert.notNull(fileID, "parameter[fileID]  is null");
        Assert.notNull(fileName, "parameter[fileName]  is null");
        Assert.notNull(contentType, "parameter[contentType]  is null");
        Assert.notNull(inputStream, "parameter[inputStream]  is null");

        Assert.notNull(digest_value, "parameter[digest_value]  is null");
        Assert.notNull(digest_algorithm, "parameter[digest_algorithm]  is null");
        Assert.notNull(signature_value, "parameter[signature_value]  is null");
        Assert.notNull(certificate, "parameter[certificate]  is null");

        MongoFileEntity file = this.save(dbName, fileID, false, fileName, contentType, inputStream, digest_value,
                digest_algorithm, signature_value, certificate, fileSourceIcon, source);

        return file;
    }

    private MongoFileEntity save(String dbName, String fileID, String fileName, String contentType,
                                 InputStream inputStream, String digest_value, String digest_algorithm, String signature_value,
                                 String certificate) {
        return this.save(dbName, fileID, false, fileName, contentType, inputStream, digest_value, digest_algorithm,
                signature_value, certificate, "", null);
    }

    private MongoFileEntity save(String dbName, String fileID, Boolean bsMode, String fileName, String contentType,
                                 InputStream inputStream, String digest_value, String digest_algorithm, String signature_value,
                                 String certificate, String fileSourceIcon, String source) {

        Assert.notNull(fileName, "parameter[fileName]  is null");
        Assert.notNull(contentType, "parameter[contentType]  is null");
        Assert.notNull(inputStream, "parameter[inputStream]  is null");

        Assert.notNull(digest_value, "parameter[digest_value]  is null");
        Assert.notNull(digest_algorithm, "parameter[digest_algorithm]  is null");
        Assert.notNull(signature_value, "parameter[signature_value]  is null");
        Assert.notNull(certificate, "parameter[certificate]  is null");
        MongoFileEntity entity = this.savePhysicalFile(dbName, getPHYSICAL_TABLE(), null, fileName, contentType,
                inputStream);

        // 保存到逻辑文件表中
        LogicFileInfo logicFileInfo = null;

        if (StringUtils.isBlank(fileID)) {
            // 由系统自动产生文件ID
            logicFileInfo = new LogicFileInfo();
            logicFileInfo.setUuid(UuidUtils.createUuid());
            logicFileInfo.doBindCreateTimeAsNow();
            logicFileInfo.doBindCreatorAsCurrentUser();
        } else {// 由用户分配文件ID,所以要检查该文件ID是否已经被使用
            try {
                logicFileInfo = this.getLogicFileInfo(fileID);
                // 记录（编辑/保存）历史
                String origUuid = logicFileInfo.getOrigUuid();
                if (StringUtils.isBlank(origUuid)) {
                    // 第一次保存，历史数据
                    origUuid = logicFileInfo.getUuid();
                }
                if (true == bsMode) {
                    logicFileInfo = new LogicFileInfo();
                    logicFileInfo.doBindCreateTimeAsNow();
                    logicFileInfo.doBindCreatorAsCurrentUser();
                    logicFileInfo.setUuid(UuidUtils.createUuid());
                    logicFileInfo.setOrigUuid(origUuid);
                } else if (null != logicFileInfo) {
                    LogicFileInfo hisLogicFileInfo = new LogicFileInfo();
                    BeanUtils.copyProperties(logicFileInfo, hisLogicFileInfo);
                    hisLogicFileInfo.setUuid(UuidUtils.createUuid());// 改掉旧fileID，然后新建
                    hisLogicFileInfo.setOrigUuid(origUuid);
                    save(hisLogicFileInfo);
                    logicFileInfo.doBindCreateTimeAsNow();
                    logicFileInfo.doBindCreatorAsCurrentUser();
                }
            } catch (FileNotFoundException e) {// 如果找不到该文件,新建文件
                // logger.error(e.getMessage(), e);, 这个日志不要再打了,
                // 因为这是符合逻辑预期的异常，不是业务上的异常
                logicFileInfo = new LogicFileInfo();
                logicFileInfo.doBindCreateTimeAsNow();
                logicFileInfo.doBindCreatorAsCurrentUser();
                logicFileInfo.setUuid(fileID);
            }
        }

        logicFileInfo.setFileSize(entity.getLength());
        logicFileInfo.setFileName(fileName);
        logicFileInfo.setContentType(contentType);
        logicFileInfo.setDigestValue(digest_value);
        logicFileInfo.setDigestAlgorithm(digest_algorithm);
        logicFileInfo.setSignatureValue(signature_value);
        logicFileInfo.setCertificate(certificate);
        logicFileInfo.setPhysicalFileId(entity.getPhysicalID());
        logicFileInfo.setSourceIcon(fileSourceIcon);
        logicFileInfo.setSource(StringUtils.isBlank(source) ? "上传" : source);

        logicFileInfo.doBindModifierAsCurrentUser();
        logicFileInfo.doBindModifyTimeAsNow();

        logicFileInfo.setDbName(dbName);

        this.save(logicFileInfo);

        // 组装成文件信息给用户
        MongoFileEntity file = entity;
        file.setLogicFileInfo(logicFileInfo);
        //		try {
        //			file = getFile(logicFileInfo.getUuid());
        //		} catch (FileNotFoundException e) {
        //			logger.error(e.getMessage(), e);
        //		}

        return file;
    }

    @Override
    public MongoFileEntity saveByMd5(String dbName, String fileID, String fileName, String md5, String fileSourceIcon) {

        Assert.notNull(fileName, "parameter[fileName]  is null");
        Assert.notNull(md5, "parameter[md5]  is null");
        MongoFileEntity entity = baseMongoDao.findOneFile(dbName, getPHYSICAL_TABLE(), new BasicDBObject("md5", md5));

        if (entity != null) {
            LogicFileInfo newLogicFileInfo = new LogicFileInfo();

            String physicalFileId = entity.getPhysicalID();
            List<LogicFileInfo> logicFileInfoList = this.getLogicFileInfoByPhysicalFileId(physicalFileId);
            if (CollectionUtils.isNotEmpty(logicFileInfoList)) {
                LogicFileInfo logicFileInfo = logicFileInfoList.get(0);
                //                LogicFileInfo newLogicFileInfo = new LogicFileInfo();
                BeanUtils.copyProperties(logicFileInfo, newLogicFileInfo);

                newLogicFileInfo.setUuid(fileID);
                newLogicFileInfo.setFileName(fileName);
                newLogicFileInfo.setSourceIcon(fileSourceIcon);
                newLogicFileInfo.doBindCreateTimeAsNow();
                newLogicFileInfo.doBindCreatorAsCurrentUser();
                newLogicFileInfo.doBindModifyTimeAsNow();
                newLogicFileInfo.doBindModifierAsCurrentUser();
                newLogicFileInfo.doBindDbNameAsCurrentTenantId();

                this.save(newLogicFileInfo);

            } else {
                // 保存到逻辑文件表中
                try {
                    newLogicFileInfo = this.getLogicFileInfo(fileID);
                } catch (FileNotFoundException e) {// 如果找不到该文件,新建文件
                    // logger.error(e.getMessage(), e);, 这个日志不要再打了,
                    // 因为这是符合逻辑预期的异常，不是业务上的异常
                    newLogicFileInfo = new LogicFileInfo();
                    newLogicFileInfo.doBindCreateTimeAsNow();
                    newLogicFileInfo.doBindCreatorAsCurrentUser();
                    newLogicFileInfo.setUuid(fileID);
                }

                newLogicFileInfo.setFileSize(entity.getLength());
                newLogicFileInfo.setFileName(fileName);
                // 计算MD5消息摘要
                newLogicFileInfo.setContentType(new MimetypesFileTypeMap().getContentType(fileName));
                newLogicFileInfo.setDigestValue(md5);
                newLogicFileInfo.setDigestAlgorithm("MD5");
                newLogicFileInfo.setSignatureValue("");
                newLogicFileInfo.setCertificate("");
                newLogicFileInfo.setPhysicalFileId(entity.getPhysicalID());
                newLogicFileInfo.setSourceIcon(fileSourceIcon);

                newLogicFileInfo.doBindModifierAsCurrentUser();
                newLogicFileInfo.doBindModifyTimeAsNow();

                newLogicFileInfo.setDbName(dbName);

                this.save(newLogicFileInfo);
            }

            // 组装成文件信息给用户
            entity.setLogicFileInfo(newLogicFileInfo);

            return entity;
        } else {
            return null;
        }

    }

    private String getTenantId() {
        String tenantId = SpringSecurityUtils.getCurrentTenantId();
        return tenantId;
    }

    public MongoFileEntity savePhysicalFile(String tenantId, String tblName, String fileID, String fileName,
                                            String contentType, InputStream inputStream) {
        return this.savePhysicalFile(tenantId, tblName, fileID, fileName, contentType, inputStream, true);
    }

    public MongoFileEntity savePhysicalFile(String tenantId, String tblName, String fileID, String fileName,
                                            String contentType, InputStream inputStream, boolean validateMd5) {
        if (tblName == null) {
            tblName = this.getPHYSICAL_TABLE();
        }
        MongoFileEntity file = baseMongoDao.saveFile(tenantId, tblName, fileName, contentType, inputStream, fileID,
                validateMd5);
        return file;
    }

    public MongoFileEntity saveReplicaFile(String fileID, String fileName, String contentType, InputStream inputStream) {
        String tenantId = this.getCurrentTenantId();
        return this.savePhysicalFile(tenantId, getPHYSICAL_REPLICA_FILE_TABLE(), fileID, fileName, contentType,
                inputStream);
    }

    @SuppressWarnings("unused")
    @Override
    public MongoFileEntity getFile(String fileID) throws FileNotFoundException {
        Assert.notNull(fileID, "parameter[fileID]  is null ");

        LogicFileInfo logicFileInfo = this.getLogicFileInfo(fileID);

        MongoFileEntity fileEntity = baseMongoDao.findFileById(logicFileInfo.getDbName(), getPHYSICAL_TABLE(),
                logicFileInfo.getPhysicalFileId());// 从物理表中获取文件信息

        if (fileEntity == null) {
            throw new FileNotFoundException("cann't find physical file, fileID " + fileID + " in table "
                    + getPHYSICAL_TABLE());
        }

        if (logicFileInfo != null) {
            fileEntity.setLogicFileInfo(logicFileInfo);// 组合成文件
        } else {
            throw new FileNotFoundException("cann't find logic file, fileID [" + fileID + "]");
        }

        return fileEntity;
    }

    @Override
    public Boolean existFile(String physicalFileId) {
        MongoFileEntity fileEntity = baseMongoDao.findFileById(this.getCurrentTenantId(), getPHYSICAL_TABLE(),
                physicalFileId);// 从物理表中获取文件信息
        if (fileEntity == null) {
            return false;
        }
        return true;
    }

    public LogicFileInfo getLogicFileInfo(String fileID) throws FileNotFoundException {
        LogicFileInfo logicFileInfo = this.findUnique(Restrictions.eq("uuid", fileID));
        if (logicFileInfo != null) {
            return logicFileInfo;
        } else {
            throw new FileNotFoundException("cann't find logic file, fileID [" + fileID + "]");
        }
    }

    @Override
    public List<LogicFileInfo> getLogicFileInfosByUuids(Collection<String> fileIdList) {
        return this.get(fileIdList);
    }

    @Override
    public void deleteFile(String fileID) throws IntrospectionException, IllegalAccessException,
            InvocationTargetException {
        Assert.notNull(fileID, "parameter[fileID]  is null");
        // 获取完整的文件信息
        LogicFileInfo file = null;
        try {
            file = this.getFile(fileID).getLogicFileInfo();
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            return;
        }
        this.delete(fileID);// 只删除文件的逻辑信息,不删除文件的物理信息,物理信息由后台任务去删除

        // 保存到日志中
        this.baseMongoDao.createDocument(file.getDbName(), getLOGIC_FILE_TABLE(), BeanMapUtils.toMap(file, null), null);

    }

    @Override
    public void destroyFile(String fileID) {
        LogicFileInfo logicFileInfo;
        try {
            logicFileInfo = this.getLogicFileInfo(fileID);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            return;
        }

        String physicalFileId = logicFileInfo.getPhysicalFileId();
        String swfFieldId = logicFileInfo.getSwfUuid();
        this.delete(fileID);// 删除文件的逻辑信息同时删除文件的物理信息
        String tblName = this.getPHYSICAL_TABLE();

        if (swfFieldId != null && swfFieldId.trim().length() > 0) {// 删除副本文件
            this.baseMongoDao.deleteFileById(logicFileInfo.getDbName(), this.getPHYSICAL_REPLICA_FILE_TABLE(),
                    swfFieldId);
        }

        this.baseMongoDao.deleteFileById(logicFileInfo.getDbName(), tblName, physicalFileId);
    }

    @Override
    public MongoFileEntity copyFile(String fileID, String fileName) throws IntrospectionException, IllegalAccessException,
            InvocationTargetException, FileNotFoundException {
        Assert.notNull(fileID, "parameter[fileID]  is null");
        MongoFileEntity srcfileEntity = getFile(fileID);

        LogicFileInfo logicInfo = srcfileEntity.getLogicFileInfo();

        MongoFileEntity destFileEntity = this.saveFile(srcfileEntity.getLogicFileInfo().getDbName(),
                StringUtils.isNotBlank(fileName) ? fileName: srcfileEntity.getFileName(), srcfileEntity.getContentType(), srcfileEntity.getInputstream(),
                logicInfo.getDigestValue() == null ? "" : logicInfo.getDigestValue(),
                logicInfo.getDigestAlgorithm() == null ? "" : logicInfo.getDigestAlgorithm(),
                logicInfo.getSignatureValue() == null ? "" : logicInfo.getSignatureValue(),
                logicInfo.getCertificate() == null ? "" : logicInfo.getCertificate());

        return destFileEntity;
    }

    @Override
    public boolean isExistFile(String fileID) {
        Assert.notNull(fileID, "parameter[fileID]  is null");
        try {
            getFile(fileID);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }

    }

    @Override
    public Boolean isFileInUsed(String physicalFileId, String fileID) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("fileID", fileID);
        params.put("physicalFileId", physicalFileId);
        List<LogicFileInfo> list = query(FILE_IN_USED, params, LogicFileInfo.class);
        return list != null && list.size() > 0;
    }

    @Override
    public MongoFileEntity recoveryFile(String fileID) throws IntrospectionException, IllegalAccessException,
            InvocationTargetException, FileNotFoundException {

        /*
         * //恢复实体文件 MongoFileEntity deletedFileEntity
         * =baseMongoDao.findFileById(getDELETE_PHYSICAL_TABLE(), fileID);
         * if(deletedFileEntity == null){ throw new
         * FileNotFoundException("cann't find file objectid=" + fileID +" in " +
         * getDELETE_PHYSICAL_TABLE()); }
         * baseMongoDao.saveFile(getPHYSICAL_TABLE(),
         * deletedFileEntity.getFileName(), deletedFileEntity.getContentType(),
         * deletedFileEntity.getInputstream(), deletedFileEntity.getId() );
         * baseMongoDao.deleteFileById(getDELETE_PHYSICAL_TABLE(),
         * deletedFileEntity.getId());
         *
         *
         * //恢复逻辑文件 DBObject dbObject =
         * baseMongoDao.findDocumentById(getDELETE_LOGIC_TABLE(), fileID);
         * if(dbObject == null){ throw new
         * FileNotFoundException("cann't find file objectid=" + fileID +"  in "
         * + getDELETE_LOGIC_TABLE()); }
         *
         * LogicFileInfo deletedLogicInfo = LogicFileInfo.parse(dbObject);
         *
         * deletedFileEntity.setLogicFileInfo(deletedLogicInfo);
         *
         *
         * deletedLogicInfo.pushOperateLog(EnumOperateType.RECOVERY, null);
         *
         * Map<String, Object> params = BeanMapUtils.toMap(deletedLogicInfo);
         *
         * baseMongoDao.createDocument(getLOGIC_TABLE(), params,
         * deletedFileEntity.getId());
         *
         * baseMongoDao.deleteDocumentById(getDELETE_LOGIC_TABLE(),
         * deletedFileEntity.getId());
         *
         * return deletedFileEntity;
         */
        return null;

    }

    @Override
    public void popLinkFolder(String fileID, String folderID) throws IntrospectionException, IllegalAccessException,
            InvocationTargetException, IOException {

        /*
         * DBObject dbObject = new BasicDBObject(); dbObject.put("$pull", new
         * BasicDBObject("folder_id", folderID));
         *
         * //baseMongoDao.updateDocument(getLOGIC_TABLE(), dbObject, fileID);
         *
         *
         * OperateLog log =
         * LogicFileInfo.generateOperateLog(EnumOperateType.POP, folderID);
         *
         *
         * dbObject.put("$push", new BasicDBObject("operate_log",
         * BeanMapUtils.toMap(log)));
         *
         * baseMongoDao.updateDocument(getLOGIC_TABLE(), dbObject, fileID);
         *
         *
         *
         * DBObject fileDbObj = baseMongoDao.findDocumentById(getLOGIC_TABLE(),
         * fileID);
         *
         * if(fileDbObj == null){ return; }
         *
         * LogicFileInfo file = LogicFileInfo.parse(fileDbObj);
         *
         * if(file.getFolder_id() == null || file.getFolder_id().size() == 0){
         * this.deleteFile(fileID); }
         */

    }

    @Override
    public void pushLinkFolder(String fileID, String folderID) throws IntrospectionException, IllegalAccessException,
            InvocationTargetException, FileNotFoundException {
        /*
         * DBObject dbObject = new BasicDBObject(); dbObject.put("$push", new
         * BasicDBObject("folder_id", folderID)); OperateLog log =
         * LogicFileInfo.generateOperateLog(EnumOperateType.PUSH, folderID);
         *
         * baseMongoDao.updateDocument(getLOGIC_TABLE(), dbObject, fileID);
         *
         * dbObject.put("$push", new BasicDBObject("operate_log",
         * BeanMapUtils.toMap(log)));
         * baseMongoDao.updateDocument(getLOGIC_TABLE(), dbObject, fileID);
         */

    }

    public void updateSignature(String fileID, String digestValue, String digestAlgorithm, String signatureValue,
                                String certificate) {
        Assert.notNull(fileID, "parameter[fileID]  is null");

        Assert.notNull(certificate, "parameter[certificate]  is null");
        Assert.notNull(signatureValue, "parameter[signatureValue]  is null");
        Assert.notNull(digestAlgorithm, "parameter[digestAlgorithm]  is null");
        Assert.notNull(digestValue, "parameter[digestValue]  is null");

        /*
         * DBObject dbObject = new BasicDBObject(); dbObject.put("digest_value",
         * digest_value); dbObject.put("digest_algorithm", digest_algorithm);
         * dbObject.put("signature_value", signature_value);
         * dbObject.put("certificate", certificate);
         */

        /*
         * BasicDBObject query = new BasicDBObject(); Pattern pattern =
         * Pattern.compile(
         * EnumQueryPattern.getMatchPatternStr(EnumQueryPattern.RIGTH_MATCH,
         * fileId), Pattern.CASE_INSENSITIVE); query.append("_id", pattern);
         */

        LogicFileInfo logicFileInfo;
        try {
            logicFileInfo = this.getLogicFileInfo(fileID);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            return;
        }

        logicFileInfo.setDigestValue(digestValue);
        logicFileInfo.setDigestAlgorithm(digestAlgorithm);
        logicFileInfo.setSignatureValue(signatureValue);
        logicFileInfo.setCertificate(certificate);

        logicFileInfo.doBindModifierAsCurrentUser();
        logicFileInfo.doBindModifyTimeAsNow();
        this.save(logicFileInfo);
    }

    public void appendReplicaToFile(EnumReplicaType replicaType, String replicaFileID, String fileID)
            throws FileNotFoundException {
        Assert.notNull(fileID, "parameter[fileID]  is null");

        Assert.notNull(replicaFileID, "parameter[replicaFileID]  is null");
        Assert.notNull(replicaType, "parameter[replicaType]  is null");

        // this.getFile(fileID);

        /*
         * DBObject query = new BasicDBObject(); query.put("_id", fileID);
         * DBObject dbObj = new BasicDBObject(); dbObj.put("$pull", new
         * BasicDBObject("replicaset", new BasicDBObject("type",
         * replicaType.getType())));
         * baseMongoDao.updateDocument(getLOGIC_TABLE(), dbObj, query);
         *
         *
         * dbObj = new BasicDBObject(); dbObj.put("$push", new
         * BasicDBObject("replicaset", (new
         * BasicDBObject("type",replicaType.getType())).append("_id",
         * replicaFileID))); int affectedCount =
         * baseMongoDao.updateDocument(getLOGIC_TABLE(), dbObj, query);
         * if(affectedCount < 1){ throw new FileNotFoundException("this file[" +
         * fileID + "] does not exist"); }
         */
        LogicFileInfo logicFileInfo;
        try {
            logicFileInfo = this.getLogicFileInfo(fileID);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            return;
        }

        if (replicaType.equals(EnumReplicaType.SWF)) {

            logicFileInfo.setSwfUuid(replicaFileID);

            logicFileInfo.doBindModifierAsCurrentUser();
            logicFileInfo.doBindModifyTimeAsNow();

            this.save(logicFileInfo);
        }

    }

    @Override
    public MongoFileEntity getReplicaFile(String fileID, EnumReplicaType type) throws FileNotFoundException {
        Assert.notNull(fileID, "parameter[fileID]  is null");
        Assert.notNull(type, "parameter[type]  is null");

        /*
         * DBObject query = new BasicDBObject(); query.put("_id", fileID);
         * query.put("$elemMatch", new BasicDBObject("replicatset", new
         * BasicDBObject("type", type.getType()))); MongoFileEntity file =
         * this.getFile(fileID); LogicFileInfo logicFile =
         * file.getLogicFileInfo(); List<ReplicaFile> replicaFiles =
         * logicFile.getReplicaset(); if(replicaFiles == null ||
         * replicaFiles.size() == 0){ return null; }
         *
         * String replicaFileID = null; for(ReplicaFile replica: replicaFiles){
         * if(replica.getType().equals(type.getType())){ replicaFileID =
         * replica.get_id(); } }
         *
         * return replicaFileID == null ? null
         * :this.baseMongoDao.findFileById(getPHYSICAL_REPLICA_FILE_TABLE(),
         * replicaFileID);
         */

        LogicFileInfo logicFileInfo;

        logicFileInfo = this.getLogicFileInfo(fileID);
        String replicaFileID = logicFileInfo.getSwfUuid();
        if (replicaFileID == null) {
            return null;
        } else {
            MongoFileEntity swfMongFileEntity = this.baseMongoDao.findFileById(logicFileInfo.getDbName(),
                    getPHYSICAL_REPLICA_FILE_TABLE(), replicaFileID);
            swfMongFileEntity.setLogicFileInfo(logicFileInfo);
            return swfMongFileEntity;
        }

    }

    @Override
    public void destroyReplicaFile(String fileID, EnumReplicaType type) throws FileNotFoundException {

        /*
         * MongoFileEntity file = this.getFile(fileID); LogicFileInfo logicFile
         * = file.getLogicFileInfo(); List<ReplicaFile> replicaFiles =
         * logicFile.getReplicaset(); if(replicaFiles == null ||
         * replicaFiles.size() == 0){ return ; }
         *
         * String replicaFileID = null; for(ReplicaFile replica: replicaFiles){
         * if(replica.getType().equals(type.getType())){ replicaFileID =
         * replica.get_id(); } }
         *
         * if(replicaFileID == null){ return; }
         *
         * DBObject query = new BasicDBObject(); query.put("_id", fileID);
         * DBObject dbObj = new BasicDBObject(); dbObj.put("$pull", new
         * BasicDBObject("replicaset", new BasicDBObject("type",
         * type.getType()))); baseMongoDao.updateDocument(getLOGIC_TABLE(),
         * dbObj, query);
         *
         *
         *
         *
         *
         *
         * baseMongoDao.deleteFileById(getPHYSICAL_REPLICA_FILE_TABLE(),
         * replicaFileID);
         */

        LogicFileInfo logicFileInfo = this.getLogicFileInfo(fileID);
        String swfFileID = logicFileInfo.getSwfUuid();
        logicFileInfo.setSwfUuid(null);

        logicFileInfo.doBindModifierAsCurrentUser();
        logicFileInfo.doBindModifyTimeAsNow();
        this.save(logicFileInfo);
        if (swfFileID != null && swfFileID.trim().length() > 0) {
            baseMongoDao.deleteFileById(logicFileInfo.getDbName(), getPHYSICAL_REPLICA_FILE_TABLE(), swfFileID);
        }

    }

    @Override
    public void createReplicaFile(String fileID, EnumReplicaType type, InputStream swfInputstream)
            throws FileNotFoundException {

        String fileName = fileID + "." + type.getExtName();
        String mimeType = new MimetypesFileTypeMap().getContentType(fileName);
        String swfFileID = UuidUtils.createUuid();
        this.appendReplicaToFile(type, swfFileID, fileID);

        this.saveReplicaFile(swfFileID, fileName, mimeType, swfInputstream);
    }

    public String getPHYSICAL_TABLE() {
        return PHYSICAL_FILE_TABLE;
    }

    public String getPHYSICAL_REPLICA_FILE_TABLE() {
        return PHYSICAL_REPLICA_FILE_TABLE;
    }

    /**
     * 获取文件夹在mongodb中的日志表
     *
     * @return
     */
    public String getLOGIC_FILE_TABLE() {
        return LOGIC_FILE_TABLE + "." + getCurrentTenantId();
    }

    public String getCurrentUserId() {
        return SpringSecurityUtils.getCurrentUserId();
    }

    public String getCurrentTenantId() {
        if (currentTenantId == null) {
            return TenantContextHolder.getTenantId();
        } else {
            return currentTenantId;
        }
    }

    public void setCurrentTenantId(String currentTenantId) {
        this.currentTenantId = currentTenantId;
    }

    public BaseMongoDao getBaseMongoDao() {
        return baseMongoDao;
    }

    public void setBaseMongoDao(BaseMongoDao baseMongoDao) {
        this.baseMongoDao = baseMongoDao;
    }

    @Override
    public void saveFile(LogicFileInfo logicFileInfo) {
        this.save(logicFileInfo);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<QueryItem> getFilesByPage(String dbName, int firstResult, int maxResults) {
        String synbeforedays = Config.getValue("mongodb.server.gc.synbeforedays");
        if (synbeforedays == null || synbeforedays.trim().length() == 0) {
            synbeforedays = "2";
        }
        SQLQuery sqlQuery = this
                .getSession()
                .createSQLQuery(
                        "select * from REPO_FILE where  create_time  < (sysdate-numtodsinterval(3,'hour')) and  create_time  > (sysdate-numtodsinterval("
                                + synbeforedays + ",'day')) order by create_time desc ")
                // .addEntity(LogicFileInfo.class)
                ;
        List<QueryItem> queryItems = sqlQuery.setFirstResult(firstResult).setMaxResults(maxResults)
                .setResultTransformer(QueryItemResultTransformer.INSTANCE).list();
        return queryItems;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<QueryItem> getFilesByPage(String dbName, int firstResult, int maxResults, String validTime) {
        if (validTime == null || validTime.trim().length() == 0) {
            validTime = "4";
        }
        SQLQuery sqlQuery = this
                .getSession()
                .createSQLQuery(
                        "select * from REPO_FILE where  create_time  < (sysdate-numtodsinterval(3,'hour')) and  create_time  > (sysdate-numtodsinterval("
                                + validTime + ",'day')) order by create_time desc ");
        List<QueryItem> queryItems = sqlQuery.setFirstResult(firstResult).setMaxResults(maxResults)
                .setResultTransformer(QueryItemResultTransformer.INSTANCE).list();
        return queryItems;
    }

    @Override
    public List<GridFSDBFile> findProtoFiles(String dbName, String synbeforedays) {
        return this.baseMongoDao.findProtoFiles(dbName, getPHYSICAL_TABLE(), synbeforedays);
    }

    @Override
    public void destroyPhysicalFile(String physicalFileId) {

        for (LogicFileInfo lFileInfo : this.getLogicFileInfoByPhysicalFileId(physicalFileId)) {
            this.baseMongoDao.deleteFileById(lFileInfo.getDbName(), this.getPHYSICAL_TABLE(), physicalFileId);
        }

    }

    @Override
    public List<LogicFileInfo> getLogicFileInfoByPhysicalFileId(String physicalFileId) {
        return this.find(Restrictions.eq("physicalFileId", physicalFileId));
    }

    @Override
    public List<LogicFileInfo> getLogicFileInfoByPhysicalFileId(List<String> physicalFileIds) {
        return this.find(Restrictions.in("physicalFileId", physicalFileIds));
    }

    public LogicFileInfo getUniqueLogicFileInfoByPhysicalFileId(String physicalFileId) {
        List<LogicFileInfo> lFileInfos = this.getLogicFileInfoByPhysicalFileId(physicalFileId);
        return (lFileInfos == null || lFileInfos.size() == 0) ? null : lFileInfos.get(0);
    }

    @Override
    public MongoFileEntity findProtoFile(String physicalFileId) {
        MongoFileEntity file = null;
        for (LogicFileInfo lFileInfo : this.getLogicFileInfoByPhysicalFileId(physicalFileId)) {
            file = this.baseMongoDao.findFileById(lFileInfo.getDbName(), this.getPHYSICAL_TABLE(), physicalFileId);
        }
        return file;
    }

    @Override
    public Boolean updateFileName(String fileID, String fileName) {
        try {
            MongoFileEntity file = this.getFile(fileID);
            LogicFileInfo lfileInfo = file.getLogicFileInfo();
            lfileInfo.setFileName(fileName);
            this.save(lfileInfo);
            this.baseMongoDao.updateFileName(lfileInfo.getDbName(), lfileInfo.getPhysicalFileId(), fileName);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage() + "\n cann't find file [" + fileID + "]");
        }
        return true;
    }

    @Override
    public MongoFileEntity getFileByPhysicalFileId(String physicalFileId) throws FileNotFoundException {
        LogicFileInfo lFileInfo = this.getUniqueLogicFileInfoByPhysicalFileId(physicalFileId);
        return this.getFile(lFileInfo.getFileID());
    }

    /**
     * 查询附件历史
     */
    @Override
    public List<LogicFileInfo> queryFileHistory(String fileID) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("fileID", fileID);
        return nativeDao.namedQuery("queryFileHistory", values, LogicFileInfo.class);
    }

    @Override
    public MongoFileEntity getPhysicalFile(String physicalFileId) {
        return this.baseMongoDao.findFileById(this.getCurrentTenantId(), getPHYSICAL_TABLE(), physicalFileId);
    }

}
