package com.wellsoft.pt.repository.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.MongoClientException;
import com.mongodb.gridfs.GridFSDBFile;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.exception.WellException;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.UuidUtils;
import com.wellsoft.context.util.file.ZipUtils;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.repository.dao.FileDao;
import com.wellsoft.pt.repository.dao.FileInFolderDao;
import com.wellsoft.pt.repository.dao.FolderDao;
import com.wellsoft.pt.repository.dao.FolderOperateLogDao;
import com.wellsoft.pt.repository.dao.base.impl.BaseMongoDaoImpl;
import com.wellsoft.pt.repository.dao.impl.FileDaoImpl;
import com.wellsoft.pt.repository.dto.FileChunkInfoResponseDto;
import com.wellsoft.pt.repository.dto.LogicFileInfoExt;
import com.wellsoft.pt.repository.entity.*;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.FileWaitUploadService;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.repository.support.*;
import com.wellsoft.pt.repository.support.enums.EnumOperateType;
import com.wellsoft.pt.repository.support.enums.EnumReplicaType;
import com.wellsoft.pt.repository.support.json.OracleEntityPropertyFilter;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.security.util.TenantContextHolder;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tools.ant.filters.StringInputStream;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.*;
import java.beans.IntrospectionException;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.*;

@Service
@Transactional
public class MongoFileServiceImpl extends BaseServiceImpl implements MongoFileService {
    public final static String pathSeparator = File.separator;
    public final static String appDataDir = Config.APP_DATA_DIR + pathSeparator + "mongofilesyn" + pathSeparator
            + "export" + pathSeparator;
    private final String folderJSONFileName = "folderJSON";
    Logger logger = Logger.getLogger(this.getClass());
    @Autowired
    FileDao fileDao;
    @Autowired
    FolderDao folderDao;
    @Autowired
    FileInFolderDao fileInFolderDao;
    @Autowired
    FolderOperateLogDao folderOperateLogDao;
    @Autowired
    FileWaitUploadService fileWaitUploadService;

    JsonConfig jsonConfig = new JsonConfig();

    {
        jsonConfig.setJsonPropertyFilter(new OracleEntityPropertyFilter());
    }

    public static String getCurrentTenantId() {
        return TenantContextHolder.getTenantId();
    }

    @Override
    public void setCurrentTenantId(String tenantId) {
        ((FileDaoImpl) this.fileDao).setCurrentTenantId(tenantId);
        // ((FolderDaoImpl)this.folderDao).setCurrentTenantId(tenantId);
    }

    public static void main(String[] args) throws Exception {
        // ZipUtils.zipFolder("E:\\temp", "F:\\temp.zip");
        // ZipUtils.decompress("F:\\temp.zip");
        /*
         * File filedir = new File("F:\\flash"); for (File file :
         * filedir.listFiles()) { System.out.println(file.getAbsolutePath()); }
         */

        // JSONObject o =
        // JSONObject.fromObject("{\"id\":\"system.out.println\"}");
        // System.out.println(o.getString("id"));

        NetWorkUtils.isReachable(InetAddress.getByName("10.24.36.250"), 38128, 1000);
    }

    @Override
    public MongoFileEntity saveFile(String fileName, InputStream inputStream) {

        return this.saveFileWithTenantId(getCurrentTenantId(), fileName, inputStream);
    }

    @Override
    public MongoFileEntity saveChunkFile(String fileName, String md5, int chunkIndex, int chunkSize, long fileSize,
                                         InputStream inputStream) {
        return this.saveChunkFileWithTenantId(getCurrentTenantId(), fileName, md5, chunkIndex, chunkSize, fileSize,
                inputStream);
    }

    @Override
    public List<GridFSDBFile> findFileByMetadataMd5(String md5) {
        return fileDao.findFileByMetadataMd5(getCurrentTenantId(), md5);
    }

    @Override
    public GridFSDBFile findFileByMd5(String md5) {
        return fileDao.findFileByMd5(getCurrentTenantId(), md5);
    }

    @Override
    public void deleteByGridFSDBFile(GridFSDBFile gridFSDBFile) {
        fileDao.deleteByGridFSDBFile(getCurrentTenantId(), gridFSDBFile);
    }

    public MongoFileEntity saveChunkFileWithTenantId(String tenantId, String fileName, String md5, int chunkIndex,
                                                     int chunkSize, long fileSize, InputStream inputStream) {
        String fileID = UuidUtils.createUuid();
        //        String mimeType = getMimeType(fileName);
        MongoFileEntity fileEntity = null;
        try {
            if (StringUtils.isBlank(tenantId)) {
                tenantId = getCurrentTenantId();
            }
            //                        void saveChunkFile(String dbName, String fileID, String fileName,
            //                    String md5, int chunkIndex, long fileSize,
            //            InputStream inputStream);
            this.fileDao.saveChunkFile(tenantId, fileID, fileName, md5, chunkIndex, chunkSize, fileSize, inputStream);
        } catch (MongoClientException e) {
            testMongoServerConnection();
            throw e;
        } catch (Exception e) {
            testMongoServerConnection();
            logger.error(e.getMessage(), e);
        }

        return fileEntity;
    }

    @Override
    public FileChunkInfoResponseDto getFileChunkInfo(String md5, int chunkSize) {
        FileChunkInfoResponseDto fileChunkCheckResponseDto = new FileChunkInfoResponseDto();

        boolean hasMd5FileFlag = false;
        List<Integer> chunkIndexList = new ArrayList<>();

        GridFSDBFile fileByMd5 = this.findFileByMd5(md5);
        if (fileByMd5 != null) {
            hasMd5FileFlag = true;
        }

        List<GridFSDBFile> gridFSDBFileList = this.findFileByMetadataMd5(md5);
        for (GridFSDBFile gridFSDBFile : gridFSDBFileList) {
            Object chunkSizeObject = gridFSDBFile.getMetaData().get("chunkSize");
            if (chunkSizeObject == null) {
                this.deleteByGridFSDBFile(gridFSDBFile);
                continue;
            }
            int dataChunkSize = (int) chunkSizeObject;
            if (dataChunkSize != chunkSize) {
                this.deleteByGridFSDBFile(gridFSDBFile);
            } else {
                chunkIndexList.add((int) gridFSDBFile.getMetaData().get("chunkIndex"));
            }
        }

        fileChunkCheckResponseDto.setChunkIndexList(chunkIndexList);
        fileChunkCheckResponseDto.setHasMd5FileFlag(hasMd5FileFlag);
        return fileChunkCheckResponseDto;
    }

    @Override
    public List<FileUpload> saveFileByMd5(String md5, String fileName, String fileSourceIcon) {
        List<FileUpload> uploadFiles = new ArrayList<>();

        String uuid = UuidUtils.createUuid();
        MongoFileEntity entity = this.fileDao.saveByMd5(getCurrentTenantId(), uuid, fileName, md5, fileSourceIcon);
        if (entity != null) {
            FileUpload fileUpload = new FileUpload();
            fileUpload.setFileID(uuid);
            fileUpload.setContentType(entity.getContentType());
            fileUpload.setUserId(SpringSecurityUtils.getCurrentUserId());
            fileUpload.setUserName(SpringSecurityUtils.getCurrentUserName());
            fileUpload.setDepartmentId(SpringSecurityUtils.getCurrentUserDepartmentId());
            fileUpload.setDepartmentName(SpringSecurityUtils.getCurrentUserDepartmentName());
            fileUpload.setFilename(fileName);
            fileUpload.setFileSize(entity.getLength());
            fileUpload.setCreator(SpringSecurityUtils.getCurrentUserId());
            fileUpload.setCreateTime(new Date());
            fileUpload.setUploadTime(new Date());

            uploadFiles.add(fileUpload);
        }

        return uploadFiles;
    }

    @Override
    public MongoFileEntity saveFile(String fileID, String fileName, InputStream inputStream) {
        return saveFile(fileID, false, fileName, inputStream, null);
    }

    @Override
    public MongoFileEntity saveFile(String fileID, Boolean bsMode, String fileName, InputStream inputStream,
                                    String source) {
        return this.saveFileWithTenantId(getCurrentTenantId(), fileID, bsMode, fileName, inputStream, "", source);
    }

    @Override
    public MongoFileEntity saveFile(String fileID, String fileName, InputStream inputStream, String fileSourceIcon,
                                    String source) {
        return this.saveFileWithTenantId(getCurrentTenantId(), fileID, false, fileName, inputStream, fileSourceIcon,
                source);
    }

    @Override
    public MongoFileEntity saveFile(String fileName, InputStream inputStream, String digest_value,
                                    String digest_algorithm, String signature_value, String certificate) {
        return this.saveFileWithTenantId(getCurrentTenantId(), fileName, inputStream, digest_value, digest_algorithm,
                signature_value, certificate);
    }

    @Override
    public MongoFileEntity saveFile(String fileID, String fileName, InputStream inputStream, String digest_value,
                                    String digest_algorithm, String signature_value, String certificate, String fileSourceIcon, String source) {
        return this.saveFileWithTenantId(getCurrentTenantId(), fileID, fileName, inputStream, digest_value,
                digest_algorithm, signature_value, certificate, fileSourceIcon, source);
    }

    @Override
    public MongoFileEntity saveFile(String fileID, String fileName, InputStream inputStream, String digest_value,
                                    String digest_algorithm, String signature_value, String certificate) {
        return this.saveFileWithTenantId(getCurrentTenantId(), fileID, fileName, inputStream, digest_value,
                digest_algorithm, signature_value, certificate, "", "");
    }

    @Override
    @Transactional
    public LogicFileInfo saveFileName(String fileID, String newFileName) throws FileNotFoundException {
        LogicFileInfo logicFileInfo = fileDao.getLogicFileInfo(fileID);
        String origUuid = logicFileInfo.getOrigUuid();
        if (StringUtils.isBlank(origUuid)) {
            // 第一次保存，历史数据
            origUuid = logicFileInfo.getUuid();
        }
        LogicFileInfo hisLogicFileInfo = new LogicFileInfo();
        BeanUtils.copyProperties(logicFileInfo, hisLogicFileInfo, new String[]{IdEntity.UUID, IdEntity.REC_VER});
        hisLogicFileInfo.setOrigUuid(origUuid);
        hisLogicFileInfo.setUuid(UuidUtils.createUuid());
        // 保存历史版本
        ((FileDaoImpl) fileDao).getSession().save(hisLogicFileInfo);
        // 最新时间
        logicFileInfo.setCreateTime(new Date());
        logicFileInfo.setFileName(newFileName);
        logicFileInfo.setSource("重命名");
        this.fileDao.saveFile(logicFileInfo);
        return logicFileInfo;
    }

    public MongoFileEntity saveFileWithTenantId(String tenantId, String fileName, InputStream inputStream) {
        // String mimeType = new
        // MimetypesFileTypeMap().getContentType(fileName);
        MongoFileEntity fileEntity = null;

        fileEntity = this.saveFileWithTenantId(tenantId, UuidUtils.createUuid(), false, fileName, inputStream);

        return fileEntity;
    }

    public MongoFileEntity saveFileWithTenantId(String tenantId, String fileID, Boolean bsMode, String fileName,
                                                InputStream inputStream) {
        return this.saveFileWithTenantId(tenantId, fileID, bsMode, fileName, inputStream, "", null);
    }

    public MongoFileEntity saveFileWithTenantId(String tenantId, String fileID, Boolean bsMode, String fileName,
                                                InputStream inputStream, String fileSourceIcon, String source) {
        String mimeType = getMimeType(fileName);
        MongoFileEntity fileEntity = null;
        try {
            if (StringUtils.isBlank(tenantId)) {
                tenantId = getCurrentTenantId();
            }
            fileEntity = this.fileDao.saveFile(tenantId, fileID, bsMode, fileName, mimeType, inputStream,
                    fileSourceIcon, source);
        } catch (MongoClientException e) {
            testMongoServerConnection();
            throw e;
        } catch (Exception e) {
            testMongoServerConnection();
            logger.error(e.getMessage(), e);
        }

        return fileEntity;
    }

    @SuppressWarnings("static-method")
    private String getMimeType(String fileName) {
        return new MimetypesFileTypeMap().getContentType(fileName);

    }

    public MongoFileEntity saveFileWithTenantId(String tenantId, String fileName, InputStream inputStream,
                                                String digest_value, String digest_algorithm, String signature_value, String certificate) {

        MongoFileEntity fileEntity = null;

        fileEntity = this.saveFileWithTenantId(tenantId, UuidUtils.createUuid(), fileName, inputStream, digest_value,
                digest_algorithm, signature_value, certificate, "", "");

        return fileEntity;
    }

    public MongoFileEntity saveFileWithTenantId(String tenantId, String fileID, String fileName,
                                                InputStream inputStream, String digest_value, String digest_algorithm, String signature_value,
                                                String certificate, String fileSourceIcon, String source) {
        String mimeType = getMimeType(fileName);
        MongoFileEntity fileEntity = null;

        fileEntity = this.fileDao.saveFile(tenantId, fileID, fileName, mimeType, inputStream, digest_value,
                digest_algorithm, signature_value, certificate, fileSourceIcon, source);

        return fileEntity;
    }

    @Override
    public void updateSignature(String fileId, String digest_value, String digest_algorithm, String signature_value,
                                String certificate) {
        this.fileDao.updateSignature(fileId, digest_value, digest_algorithm, signature_value, certificate);

    }

    @Override
    public void pushFilesToFolder(String folderID, List<String> fileIDs, String purpose) {
        purpose = (purpose == null || purpose.trim().length() == 0) ? "attach" : purpose;
        try {
            this.folderDao.pushFilesToFolder(folderID, fileIDs, purpose.toLowerCase());
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        } catch (IntrospectionException e) {
            logger.error("exception was invoked when convert bean to Map:" + e.getMessage(), e);
        } catch (IllegalAccessException e) {
            logger.error("exception was invoked when convert bean to Map:" + e.getMessage(), e);
        } catch (InvocationTargetException e) {
            logger.error("exception was invoked when convert bean to Map:" + e.getMessage(), e);
        } catch (FileExistsException e) {
            logger.error(e.getMessage(), e);
        }

    }

    @Override
    public void saveFilesToFolderSort(String folderID, List<String> fileIDs, String purpose) {
        this.folderDao.saveFilesToFolderSort(folderID, fileIDs, purpose.toLowerCase());
    }

    @Override
    public void saveFileNames(String folderID, List<Object> files, String purpose) {
        if (CollectionUtils.isEmpty(files)) {
            return;
        }

        Map<String, String> fileMap = new HashMap<>();
        for (Object obj : files) {
            if (obj instanceof LogicFileInfo) {
                LogicFileInfo file = (LogicFileInfo) obj;
                fileMap.put(file.getFileID(), file.getFileName());
            } else {
                Map<String, String> fileInfo = (Map<String, String>) obj;
                fileMap.put(fileInfo.get("fileID"), fileInfo.get("fileName"));
            }
        }

        //		List<LogicFileInfo> saveLogicFileInfoList = new ArrayList<>();
        if (!fileMap.isEmpty()) {
            List<LogicFileInfo> logicFileInfoList = fileDao.getLogicFileInfosByUuids(fileMap.keySet());
            for (LogicFileInfo logicFileInfo : logicFileInfoList) {
                String newFileName = fileMap.get(logicFileInfo.getFileID());
                if (StringUtils.isNotBlank(newFileName)
                        && !StringUtils.equals(logicFileInfo.getFileName(), newFileName)) {
                    //					logicFileInfo.setFileName(newFileName);
                    //					saveLogicFileInfoList.add(logicFileInfo);
                    try {
                        saveFileName(logicFileInfo.getFileID(), newFileName);
                    } catch (FileNotFoundException ex) {
                    }
                }
            }
            //			for (LogicFileInfo logicFileInfo : saveLogicFileInfoList) {
            //				fileDao.save(logicFileInfo);
            //			}
        }

    }

    @Override
    public void pushFilesToFolder(String fromFolderId, String toFolderId, String fromPurpose, String toPurpose) {
        List<MongoFileEntity> fromFiles = getFilesFromFolder(fromFolderId, fromPurpose);
        if (CollectionUtils.isNotEmpty(fromFiles)) {
            List<String> fileIDs = Lists.transform(fromFiles, new Function<MongoFileEntity, String>() {
                @Nullable
                @Override
                public String apply(@Nullable MongoFileEntity mongoFileEntity) {
                    return mongoFileEntity.getFileID();
                }
            });
            pushFilesToFolder(toFolderId, fileIDs, toPurpose);
        }
    }

    @Override
    public void pushFileToFolder(String folderID, String fileID, String purpose) {

        List<String> fileIDs = new ArrayList<String>();
        fileIDs.add(fileID);
        this.pushFilesToFolder(folderID, fileIDs, purpose);
    }

    @Override
    public List<MongoFileEntity> getFilesFromFolder(String folderID, String purpose) {
        Assert.notNull(folderID, "parameter[folderID]  is null");

        purpose = (purpose == null || purpose.trim().length() == 0 || purpose.equals("null")) ? null : purpose
                .toLowerCase();
        List<MongoFileEntity> fileEntities = null;
        try {
            fileEntities = this.folderDao.getFilesFromFolder(folderID, purpose);
        } catch (FileNotFoundException e) {
            logger.info(e.getMessage(), e);
        } catch (MongoClientException e) {
            testMongoServerConnection();
            throw e;
        }

        return fileEntities;
    }

    private void testMongoServerConnection() {
        try {
            if (BooleanUtils.toBoolean(Config.getValue("fastDFS.enable", "false"))) {
                return;
            }
            NetWorkUtils.isReachable(InetAddress.getByName(BaseMongoDaoImpl.getMongoServerUrl()),
                    Integer.parseInt(BaseMongoDaoImpl.getMongoServerPort()), 1000);
        } catch (NumberFormatException e1) {
            logger.info(e1.getMessage(), e1);
        } catch (UnknownHostException e1) {
            logger.info(e1.getMessage(), e1);
        }
    }

    public void printLineInfo() {
        Throwable ex = new Throwable();
        StackTraceElement[] stackElements = ex.getStackTrace();
        if (stackElements != null) {
            for (int i = 0; i < stackElements.length; i++) {
                logger.info("stackInfo:" + stackElements[i].getClassName() + ":" + stackElements[i].getFileName() + ":"
                        + stackElements[i].getLineNumber() + ":" + stackElements[i].getMethodName());

            }
        }

    }

    @Override
    public List<LogicFileInfo> getNonioFilesFromFolder(String folderID, String purpose) {
        Assert.notNull(folderID, "parameter[folderID]  is null");
        // printLineInfo();
        List<LogicFileInfo> fileEntities = null;
        try {
            String sql = "select tt.*,t.purpose from repo_file_in_folder t,repo_file tt where t.file_uuid = tt.uuid and t.folder_uuid = :folderID";
            Map<String, Object> values = Maps.newHashMap();
            values.put("folderID", folderID);
            if (StringUtils.isNotBlank(purpose)) {
                sql += " and t.purpose = :purpose";
                values.put("purpose", purpose.toLowerCase());
            }
            sql += "  order by t.seq asc";
            fileEntities = nativeDao.query(sql, values, LogicFileInfo.class);
            /*
             * see@http://zen.well-soft.com:81/zentao/bug-view-49229.html#不同字段存在同一个LogicFileInfo时，会合并到一个字段。fileDao.getLogicFileInfo有缓存
             * fileEntities = this.folderDao.getLogicFilesFromFolder(folderID, purpose == null ? null : purpose.toLowerCase());
             */
        } catch (MongoClientException e) {
            testMongoServerConnection();
            throw e;
        }

        return fileEntities;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.repository.service.MongoFileService#getNonioFilesFromFolders(java.util.Collection)
     */
    @Override
    public Map<String, List<LogicFileInfo>> getNonioFilesFromFolders(Collection<String> folderIDs) {
        Map<String, List<LogicFileInfo>> fileEntityMap = Maps.newHashMap();
        List<LogicFileInfoQueryItem> fileEntities = null;
        try {
            String sql = "select tt.*,t.purpose, t.folder_uuid from repo_file_in_folder t,repo_file tt where t.file_uuid = tt.uuid and t.folder_uuid in(:folderIDs)";
            Map<String, Object> values = Maps.newHashMap();
            values.put("folderIDs", folderIDs);
            sql += "  order by t.seq asc";
            fileEntities = nativeDao.query(sql, values, LogicFileInfoQueryItem.class);
            for (LogicFileInfoQueryItem logicFileInfo : fileEntities) {
                String folderUuid = logicFileInfo.getFolderUuid();
                if (!fileEntityMap.containsKey(folderUuid)) {
                    fileEntityMap.put(folderUuid, new ArrayList<LogicFileInfo>());
                }
                fileEntityMap.get(folderUuid).add(logicFileInfo);
            }
        } catch (MongoClientException e) {
            testMongoServerConnection();
            throw e;
        }
        return fileEntityMap;
    }

    @Override
    public List<LogicFileInfoExt> getNonioFilesFromFolderExt(String folderID, String purpose) {
        List<LogicFileInfoExt> fileEntities2 = null;
        List<LogicFileInfo> fileEntities = getNonioFilesFromFolder(folderID, purpose);
        if (CollectionUtils.isNotEmpty(fileEntities)) {
            fileEntities2 = Lists.newArrayList();
            for (LogicFileInfo entity : fileEntities) {
                LogicFileInfoExt entityExt = new LogicFileInfoExt();
                BeanUtils.copyProperties(entity, entityExt);
                fileEntities2.add(entityExt);
                // 计算图片尺寸
                if (RepoUtils.isImage(entity.getFileName())) {
                    MongoFileEntity file = getFile(entity.getFileID());
                    if (null != file) {
                        InputStream inputStream = null;
                        try {
                            Image src = ImageIO.read(inputStream = file.getInputstream());
                            int oriWidth = src.getWidth(null);
                            int oriHeight = src.getHeight(null);
                            entityExt.setWidth(oriWidth);
                            entityExt.setHeight(oriHeight);
                        } catch (Exception ex) {
                            logger.warn(ex.getMessage(), ex);
                        } finally {
                            IOUtils.closeQuietly(inputStream);
                        }
                    }
                }
            }
        }
        return fileEntities2;
    }

    @Override
    public MongoFileEntity getFile(String fileID) {
        try {
            return this.fileDao.getFile(fileID);
        } catch (FileNotFoundException e) {
            // logger.info(e.getMessage(), e);
            logger.error("Cannot find the file[" + fileID + "] from mongoDB");
        } catch (MongoClientException e) {
            testMongoServerConnection();
            throw e;
        }
        return null;
    }

    @Override
    public LogicFileInfo getLogicFileInfo(String fileID) {
        MongoFileEntity file = this.getFile(fileID);
        if (file != null) {
            return file.getLogicFileInfo();
        }
        return null;
    }

    @Override
    public Boolean existFile(String physicalFileId) {
        return this.fileDao.existFile(physicalFileId);
    }

    @Override
    public void popAllFilesFromFolder(String folderID) {
        try {
            this.folderDao.deleteFolder(folderID);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } catch (IntrospectionException e) {
            logger.error("exception was invoked when convert bean to Map:" + e.getMessage(), e);
        } catch (IllegalAccessException e) {
            logger.error("exception was invoked when convert bean to Map:" + e.getMessage(), e);
        } catch (InvocationTargetException e) {
            logger.error("exception was invoked when convert bean to Map:" + e.getMessage(), e);
        } catch (MongoClientException e) {
            testMongoServerConnection();
            throw e;
        }

    }

    @Override
    public void popFileFromFolder(String folderID, String fileID) {
        try {
            this.folderDao.popFileFromFolder(folderID, fileID);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } catch (IntrospectionException e) {
            logger.error("exception was invoked when convert bean to Map:" + e.getMessage(), e);
        } catch (IllegalAccessException e) {
            logger.error("exception was invoked when convert bean to Map:" + e.getMessage(), e);
        } catch (InvocationTargetException e) {
            logger.error("exception was invoked when convert bean to Map:" + e.getMessage(), e);
        } catch (MongoClientException e) {
            testMongoServerConnection();
            throw e;
        }
    }

    @Override
    public void deleteFile(String fileID) throws IntrospectionException, IllegalAccessException,
            InvocationTargetException {
        // 先判断文件有没有被文件夹引用

        if (isFileInFolder(fileID)) {
            throw new WellException("can not delete this file[" + fileID
                    + "] , file is linked by folders, please pop first");
        }
        this.fileDao.deleteFile(fileID);

    }

    @Override
    public boolean isFileInFolder(String fileID) {
        List<FileInFolder> ships = this.fileInFolderDao.getFoldersOfFile(fileID);
        if (ships != null && ships.size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public void destroyFile(String fileID) {
        // 先判断流有没有被文件引用
        if (isFileInUsed(fileID)) {
            try {
                this.deleteFile(fileID); // 只删除逻辑文件,都没有在folder会递归删除逻辑文件
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            return;
        }
        this.fileDao.destroyFile(fileID);
    }

    public boolean isFileInUsed(String fileID) {
        try {
            LogicFileInfo logicFileInfo = this.fileDao.getLogicFileInfo(fileID);
            String physicalFileId = logicFileInfo.getPhysicalFileId();
            return fileDao.isFileInUsed(physicalFileId, fileID);
        } catch (FileNotFoundException e) {
            logger.info(e.getMessage(), e);
        }
        return true; // 保险,默认为使用
    }

    @Override
    public void createReplicaOfSWF(String fileID) {
        MongoFileEntity dbFile = this.getFile(fileID);
        String fileName = dbFile.getFileName();
        if (fileName.toLowerCase().indexOf(".doc") > -1 || fileName.toLowerCase().indexOf(".docx") > -1
                || fileName.toLowerCase().indexOf(".ppt") > -1 || fileName.toLowerCase().indexOf(".pptx") > -1
                || fileName.toLowerCase().indexOf(".xls") > -1 || fileName.toLowerCase().indexOf(".xlsx") > -1
                || fileName.toLowerCase().indexOf(".txt") > -1 || fileName.toLowerCase().indexOf(".pdf") > -1) {

            FileUploadHandler handler = new FileUploadHandler();

            FileEntity file = handler.test(dbFile.getInputstream(), fileName);

            if (file == null) {// 该副本已存在
                return;
            }

            // 产生副本
            InputStream swfInputstream = file.getFile(); // 将副本保存到数据库

            // 将副本附加到目标文件中
            try {
                // this.fileDao.appendReplicaToFile(EnumReplicaType.SWF/*副本类型*/,
                // swfFile.getId(),fileID);

                this.fileDao.createReplicaFile(fileID, EnumReplicaType.SWF/* 副本类型 */, swfInputstream);

            } catch (FileNotFoundException e) {
                logger.error(e.getMessage(), e);
            } catch (MongoClientException e) {
                testMongoServerConnection();
                throw e;
            } finally {
                try {
                    swfInputstream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }

            // 产生swf文件副本

        }

    }

    @Override
    public MongoFileEntity getReplicaOfSWF(String fileID) throws FileNotFoundException {
        return this.fileDao.getReplicaFile(fileID, EnumReplicaType.SWF);
    }

    @Override
    public void destroyReplicaOfSWF(String fileID) {
        try {
            this.fileDao.destroyReplicaFile(fileID, EnumReplicaType.SWF);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        } catch (MongoClientException e) {
            testMongoServerConnection();
            throw e;
        }
    }

    @Override
    public boolean isFileInFolder(String folderID, String fileID) {
        if (folderID == null || folderID.trim().length() == 0) {
            return false;
        }

        if (fileID == null || fileID.trim().length() == 0) {
            return false;
        }

        return this.folderDao.isFileInFolder(folderID, fileID);
    }

    public FileDao getFileDao() {
        return fileDao;
    }

    public void setFileDao(FileDao fileDao) {
        this.fileDao = fileDao;
    }

    public FolderDao getFolderDao() {
        return folderDao;
    }

    public void setFolderDao(FolderDao folderDao) {
        this.folderDao = folderDao;
    }

    @Override
    public void destroyFolder(String folderID) {
        this.folderDao.destroyFolder(folderID);
    }

    @Override
    public String createFolderID() {
        return UuidUtils.createUuid();
    }

    @Override
    public List<Folder> getFoldersOfFile(String fileID) {
        List<FileInFolder> list = this.fileInFolderDao.getFoldersOfFile(fileID);
        List<Folder> folders = new ArrayList<Folder>();
        if (list != null) {
            for (FileInFolder file : list) {
                folders.add(file.getFolder());
            }
        }
        return folders;
    }

    @Override
    public MongoFileEntity copyFile(String fileID) throws FileNotFoundException, IntrospectionException,
            IllegalAccessException, InvocationTargetException {
        return fileDao.copyFile(fileID, null);
    }

    @Override
    public MongoFileEntity copyFileAndRename(String fileID, String fileName) throws FileNotFoundException, IntrospectionException, IllegalAccessException, InvocationTargetException {
        MongoFileEntity fileEntity = fileDao.copyFile(fileID, fileName);
        return fileEntity;
    }

    @Override
    public Folder getFolderByFolderID(String folderID) {
        try {
            return this.getFolderDao().getFolder(folderID);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Date getLastModifyTimeOfFolder(String folderID) {
        return this.folderOperateLogDao.getLastModifyTimeOfFolder(folderID);
    }

    @Override
    public File exportChangeInfo(String folderID, Date time) {
        if (!this.isFolderExist(folderID)) {
            logger.info("folderID[" + folderID + "] is not exist");
            return null;
        }
        Date lastModifyTime = this.getLastModifyTimeOfFolder(folderID);
        if (time != null && (lastModifyTime == null || time.after(lastModifyTime))) {
            logger.info("no any change after [" + time + "] in folder[" + folderID + "]");
            return null;
        }
        String folderName = folderID + "_" + (time == null ? "all" : time.getTime());
        String exportDir = appDataDir + folderName;

        File dir = new File(exportDir);
        dir.mkdirs();
        JSONObject exportData = new JSONObject();

        exportData.put("foderID", folderID);
        List<FolderOperateLog> operateLogs = folderOperateLogDao.getLogsAfterTime(folderID, time);
        JSONArray logJSONArray = new JSONArray();
        JSONArray fileJSONArray = new JSONArray();
        for (FolderOperateLog log : operateLogs) {
            JSONObject logJson = JSONObject.fromObject(log, jsonConfig);
            System.out.println(logJson.toString());
            logJSONArray.add(logJson);
            System.out.println(logJSONArray.toString());
            EnumOperateType type = EnumOperateType.type2EnumObj(log.getOperateType());
            if (type == EnumOperateType.PUSH) {
                String fileJson = log.getFileJson().trim();
                JSONArray files = JSONArray.fromObject(fileJson);
                for (int j = 0; j < files.size(); j++) {
                    String fileUuid = files.getJSONObject(j).getString("fileUuid");
                    MongoFileEntity fileEntity = null;
                    MongoFileEntity swffileEntity = null;
                    try {
                        LogicFileInfo lFile = this.fileDao.getLogicFileInfo(fileUuid);
                        fileJSONArray.add(JSONObject.fromObject(lFile));
                        fileEntity = this.fileDao.getFile(fileUuid);
                        swffileEntity = this.getReplicaOfSWF(fileUuid);
                    } catch (FileNotFoundException e1) {
                        logger.error(e1.getMessage(), e1);
                        continue;
                    }
                    File destFolder = new File(dir.getAbsolutePath() + pathSeparator);
                    destFolder.mkdir();
                    writeToDir(destFolder, fileEntity, swffileEntity);

                }
            }
        }
        exportData.put("logs", logJSONArray);
        exportData.put("files", fileJSONArray);
        File destfile = new File(dir, folderJSONFileName);
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = new StringInputStream(exportData.toString(), "utf-8");
            destfile.createNewFile();
            fos = new FileOutputStream(destfile);
            IOUtils.copyLarge(is, fos);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (fos != null) {
                IOUtils.closeQuietly(fos);
            }
            if (is != null) {
                IOUtils.closeQuietly(is);
            }
        }
        String zipFileName = appDataDir + folderName + ".zip";
        try {
            ZipUtils.zipFolder(exportDir, zipFileName);
            FileUtils.deleteDirectory(dir);

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } catch (MongoClientException e) {
            testMongoServerConnection();
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return new File(zipFileName);

    }

    private void writeToDir(File destFolder, MongoFileEntity fileEntity, MongoFileEntity swffileEntity) {
        File file = new File(destFolder, fileEntity.getPhysicalID());
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = fileEntity.getInputstream();
            file.createNewFile();
            fos = new FileOutputStream(file);
            IOUtils.copyLarge(is, fos);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (fos != null) {
                IOUtils.closeQuietly(fos);
            }
            if (is != null) {
                IOUtils.closeQuietly(is);
            }

        }
        if (swffileEntity == null) {
            return;
        }
        File swffile = new File(destFolder, swffileEntity.getPhysicalID());
        try {
            is = swffileEntity.getInputstream();
            swffile.createNewFile();
            fos = new FileOutputStream(swffile);
            IOUtils.copyLarge(is, fos);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (fos != null) {
                IOUtils.closeQuietly(fos);
            }
            if (is != null) {
                IOUtils.closeQuietly(is);
            }

        }

    }

    @Override
    public boolean importChangeInfo(File zipFile) throws IOException {
        String folderName = UuidUtils.createUuid();
        String exportDir = appDataDir + folderName;
        File dir = new File(exportDir);
        dir.mkdirs();
        UnZip.depress(zipFile.getAbsolutePath(), exportDir);
        File folderJSONFile = getFolderJSONFile(dir);
        if (folderJSONFile == null) {
            logger.warn("cann't find any valid file");
            return false;
        }

        FileReader reader = new FileReader(folderJSONFile);
        int fileLen = (int) folderJSONFile.length();
        char[] chars = new char[fileLen];
        reader.read(chars);
        reader.close();

        try {
            String json = String.valueOf(chars);
            ImExJson imexJson = new ImExJson(json);
            Folder folder = this.getFolderByFolderID(imexJson.folderID);
            if (folder == null) {
                folder = new Folder();
                folder.setUuid(imexJson.folderID);
                folder.doBindCreateTimeAsNow();
                folder.doBindCreatorAsCurrentUser();
                folder.doBindModifierAsCurrentUser();
                folder.doBindModifyTimeAsNow();
            }
            List<FolderOperateLog> logs = imexJson.logs;
            for (FolderOperateLog log : logs) {
                if (EnumOperateType.POP == EnumOperateType.type2EnumObj(log.getOperateType())) {
                    // pop文件
                    this.popFileFromFolder(folder, log);
                } else {
                    // push文件
                    this.pushFileToFolder(folder, dir, log, imexJson);
                }
            }
            this.folderDao.save(folder);
        } catch (MongoClientException e) {
            testMongoServerConnection();
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }

        return true;
    }

    private void pushFileToFolder(Folder folder, File dir, FolderOperateLog log, ImExJson imexJson) {
        System.out.println(log.getFileJson());
        String fileJson = log.getFileJson().trim();
        JSONArray files = JSONArray.fromObject(fileJson);
        for (int j = 0; j < files.size(); j++) {
            FileInFolder fileFolder = (FileInFolder) JSONObject.toBean(files.getJSONObject(j), FileInFolder.class);

            fileFolder.setUuid(null);
            folder.getFiles().add(fileFolder);
            String fileUuid = fileFolder.getFileUuid();
            // if (this.fileInFolderDao.isFileInFolder(fileUuid,
            // folder.getUuid())) {
            // continue;
            // }
            if (this.isLogicFileInfoExist(fileUuid)) {
                continue;
            }
            LogicFileInfo logicFileInfo = imexJson.getLogicFileInfo(fileUuid);
            File file = getFileFormZip(logicFileInfo.getPhysicalFileId(), dir);
            try {
                if (file == null)
                    continue;
                System.out.println(fileUuid);
                this.fileDao.saveFile(logicFileInfo);
                this.fileDao.savePhysicalFile(logicFileInfo.getDbName(), null, logicFileInfo.getPhysicalFileId(),
                        logicFileInfo.getFileName(), logicFileInfo.getContentType(), new FileInputStream(file));

            } catch (FileNotFoundException e) {
                logger.error(e.getMessage(), e);
            }

            File swffile = getFileFormZip(logicFileInfo.getSwfUuid(), dir);
            if (swffile == null) {
                continue;
            }
            try {
                this.fileDao.savePhysicalFile(logicFileInfo.getDbName(), null, logicFileInfo.getSwfUuid(),
                        logicFileInfo.getFileName(), logicFileInfo.getContentType(), new FileInputStream(swffile));
            } catch (FileNotFoundException e) {
                logger.error(e.getMessage(), e);
            }
        }

        this.addLogs4Folder(folder, log);

    }

    private void addLogs4Folder(Folder folder, FolderOperateLog log) {
        if (this.isLogExist(log)) {
            return;
        }
        folder.getLogs().add(log);

    }

    private boolean isLogExist(FolderOperateLog log) {

        return this.folderOperateLogDao.getLogByUuid(log.getUuid()) == null ? false : true;
    }

    private boolean isLogicFileInfoExist(String fileUuid) {

        return this.getFile(fileUuid) == null ? false : true;
    }

    private File getFileFormZip(String fileUuid, File dir) {
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                File destFile = getFileFormZip(fileUuid, file);
                if (destFile == null) {
                    continue;
                } else {
                    return destFile;
                }
            }
        } else {
            if (dir.getName().equals(fileUuid)) {
                return dir;
            }
        }
        return null;
    }

    @SuppressWarnings("static-method")
    private void popFileFromFolder(Folder folder, FolderOperateLog log) {
        JSONArray files = JSONArray.fromObject(log.getFileJson());
        for (int j = 0; j < files.size(); j++) {
            String fileUuid = files.getJSONObject(j).getString("fileUuid");
            Iterator<FileInFolder> it = folder.getFiles().iterator();
            while (it.hasNext()) {
                FileInFolder file = it.next();
                if (file.getFileUuid().equals(fileUuid)) {
                    it.remove();
                }
            }
        }
        this.addLogs4Folder(folder, log);
    }

    @Override
    public boolean isFolderExist(String folderID) {
        return this.getFolderByFolderID(folderID) == null ? false : true;
    }

    private File getFolderJSONFile(File dir) {
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                File destFile = getFolderJSONFile(file);
                if (destFile == null) {
                    continue;
                } else {
                    return destFile;
                }
            }
        } else {
            if (dir.getName().equals(folderJSONFileName)) {
                return dir;
            }
        }
        return null;
    }

    @Override
    public boolean isFolerChanged(String folderID, Date time) {
        List<FolderOperateLog> fileInfos = this.folderOperateLogDao.getLogsAfterTime(folderID, time);
        if (fileInfos == null || fileInfos.size() == 0) {
            return false;
        } else {
            return true;
        }

    }

    @Override
    public void testInOneTra() {
        try {
            File file = new File("c:/3.txt");

            FileInputStream is = new FileInputStream(file);
            MongoFileEntity mongofile = this.saveFile(this.getCurrentTenantId(), file.getName(), is);
            List<String> fileids = new ArrayList<String>();
            fileids.add(mongofile.getFileID());
            this.pushFilesToFolder(UuidUtils.createUuid(), fileids, "test");
            is.close();
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

    }

    @Override
    public List<QueryItem> getFilesByPageWithTenantId(String tenantID, int firstResult, int maxResults) {
        return this.fileDao.getFilesByPage(tenantID, firstResult, maxResults);
    }

    @Override
    public List<QueryItem> getFilesByPage(int firstResult, int maxResults) {
        return this.getFilesByPageWithTenantId(getCurrentTenantId(), firstResult, maxResults);
    }

    @Override
    public List<GridFSDBFile> findProtoFilesWithTenantId(String tenantID, String synbeforedays) {
        return this.fileDao.findProtoFiles(tenantID, synbeforedays);
    }

    @Override
    public List<GridFSDBFile> findProtoFiles(String synbeforedays) {
        return this.fileDao.findProtoFiles(getCurrentTenantId(), synbeforedays);
    }

    @Override
    public MongoFileEntity findProtoFile(String physicalFileId) {

        return this.fileDao.findProtoFile(physicalFileId);
    }

    @Override
    public void destroyProtoFile(String physicalFileId) {

        this.fileDao.destroyPhysicalFile(physicalFileId);
    }

    @Override
    public List<LogicFileInfo> getFilesByPhysicalFileId(String physicalFileId) {
        List<LogicFileInfo> files = this.fileDao.getLogicFileInfoByPhysicalFileId(physicalFileId);
        return files;
    }

    @Override
    public List<LogicFileInfo> getFilesByPhysicalFileId(List<String> physicalFileIds) {
        return this.fileDao.getLogicFileInfoByPhysicalFileId(physicalFileIds);
    }

    @Override
    public MongoFileEntity savePhysicalFile(String tenantId, String fileID, String fileName, String contentType,
                                            InputStream inputStream) {
        try {
            return this.fileDao.savePhysicalFile(tenantId, null, fileID, fileName, contentType, inputStream);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<QueryItem> getFilesByPage(String tenantId, int firstResult, int maxResults, String validTime) {
        return this.fileDao.getFilesByPage(tenantId, firstResult, maxResults, validTime);
    }

    @Override
    public void updateFileName(String fileID, String fileName) {
        this.fileDao.updateFileName(fileID, fileName);
    }

    @Override
    public boolean isExistFile(String fileID) {
        return this.fileDao.isExistFile(fileID);
    }

    @Override
    public MongoFileEntity getFile(String physicalFileId, String tenantId) throws FileNotFoundException {
        return this.fileDao.getFileByPhysicalFileId(physicalFileId);
    }

    @Override
    public MongoFileEntity savePhysicalFileWithoutVerifyMD5(String tenantId, String fileID, String fileName,
                                                            String contentType, InputStream inputStream) {
        try {
            return this.fileDao.savePhysicalFile(tenantId, null, fileID, fileName, contentType, inputStream, false);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 查询附件历史
     *
     * @param fileID
     * @return
     */
    @Override
    public List<LogicFileInfo> queryFileHistory(String fileID) {
        return fileDao.queryFileHistory(fileID);
    }

    @Override
    @Transactional
    public MongoFileEntity uploadNoStreamFileMD5(String fileID, String fileName, String digest_value, String digest_algorithm, String signature_value, String certificate, String fileSourceIcon, String source, Boolean newVer) {
        MongoFileEntity file = null;
        if (StringUtils.isNotBlank(fileID)) {
            //FIXME: 更新文件
            file = this.saveFile(fileID, newVer, fileName, new ByteArrayInputStream(new byte[0])
                    , source);
        } else {
            file = this.saveFile(UuidUtils.createUuid(), fileName, new ByteArrayInputStream(new byte[0]),
                    digest_value, "MD5", "", "", null, source);
        }
        if (file != null) {
            FileWaitUploadEntity waitUploadEntity = new FileWaitUploadEntity();
            waitUploadEntity.setFileUuid(file.getFileID());
            waitUploadEntity.setMd5(digest_value);
            waitUploadEntity.setFileName(fileName);
            fileWaitUploadService.save(waitUploadEntity);
        }
        return file;
    }

    @Override
    @Transactional
    public void updateFileInputstream(String fileID, String filename, String contentType, InputStream inputStream) {
        try {
            LogicFileInfo logicFileInfo = this.fileDao.getLogicFileInfo(fileID);
            MongoFileEntity mongoFileEntity = this.fileDao.savePhysicalFile(logicFileInfo.getDbName(), null, fileID, filename, logicFileInfo.getContentType(), inputStream);
            logicFileInfo.setPhysicalFileId(mongoFileEntity.getPhysicalID());
            logicFileInfo.setFileSize(mongoFileEntity.getLength());
            this.fileDao.save(logicFileInfo);
        } catch (Exception e) {
            logger.error("更新mongo文件流异常：", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public MongoFileEntity getPhysicalFile(String physicalFileId) {
        return this.fileDao.getPhysicalFile(physicalFileId);
    }

    @Override
    public List<LogicFileInfo> getLogicFileInfo(List<String> fileIds) {
        return this.fileDao.getLogicFileInfosByUuids(fileIds);
    }

    @Override
    @Transactional
    public void popAllFilesFromFolder(String folderID, String purpose) {
        try {
            List<FileInFolder> fileInFolders = fileInFolderDao.getFilesFromFolder(folderID, purpose);
            if (CollectionUtils.isNotEmpty(fileInFolders)) {
                for (FileInFolder f : fileInFolders) {
                    fileInFolderDao.delete(f.getUuid());
                    if (!isFileInFolder(f.getFileUuid())) {
                        fileDao.deleteFile(f.getFileUuid());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("删除文件异常：", e);
            throw new RuntimeException(e);
        }

    }

    class ImExJson {
        public String folderID = null;
        public List<FolderOperateLog> logs = new ArrayList<FolderOperateLog>();
        public List<LogicFileInfo> files = new ArrayList<LogicFileInfo>();
        private JSONObject json = null;

        public ImExJson(String jsonData) {
            System.out.println(jsonData);
            json = JSONObject.fromObject(jsonData);
            try {
                folderID = json.getString("foderID");
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

            JSONArray logJSONArray = json.getJSONArray("logs");
            // Map<String, Class> classMap = new HashMap<String, Class>();
            // classMap.put("fileJson", String.class);
            for (int i = 0; i < logJSONArray.size(); i++) {
                JSONObject logJSON = logJSONArray.getJSONObject(i);
                JSONArray fileJson = logJSON.getJSONArray("fileJson");
                // logJSON.put("fileJson", "\"" + fileJson.toString() + "\"");
                logJSON.put("fileJson", fileJson.toString() + "  ");
                System.out.println(logJSON.toString());
                FolderOperateLog log = (FolderOperateLog) JSONObject.toBean(logJSON, FolderOperateLog.class);
                logs.add(log);
            }

            JSONArray fileJSONArray = json.getJSONArray("files");
            for (int i = 0; i < fileJSONArray.size(); i++) {
                JSONObject fileJSON = fileJSONArray.getJSONObject(i);

                // classMap.put("logs", FolderOperateLog.class);
                LogicFileInfo fileInfo = (LogicFileInfo) JSONObject.toBean(fileJSON, LogicFileInfo.class);
                files.add(fileInfo);
            }

        }

        public LogicFileInfo getLogicFileInfo(String fileUuid) {

            for (LogicFileInfo fileInfo : files) {
                if (fileInfo.getUuid().equals(fileUuid)) {
                    return fileInfo;
                }
            }
            return null;
        }
    }

}
