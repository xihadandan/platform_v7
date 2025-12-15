package com.wellsoft.pt.repository.service;

import com.mongodb.gridfs.GridFSDBFile;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.groovy.GroovyUseable;
import com.wellsoft.pt.repository.dto.FileChunkInfoResponseDto;
import com.wellsoft.pt.repository.dto.LogicFileInfoExt;
import com.wellsoft.pt.repository.entity.FileUpload;
import com.wellsoft.pt.repository.entity.Folder;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 文件操作
 *
 * @author Hunt
 */

/**
 * @author Administrator
 */
@GroovyUseable
public interface MongoFileService {

    /**
     * 保存文件
     *
     * @param fileName    不得为空
     * @param inputStream 不得为空
     * @return 返回null表示保存失败
     */

    MongoFileEntity saveFile(String fileName, InputStream inputStream);

    MongoFileEntity saveChunkFile(String fileName, String md5, int chunkIndex, int chunkSize, long fileSize,
                                  InputStream inputStream);

    List<GridFSDBFile> findFileByMetadataMd5(String md5);

    GridFSDBFile findFileByMd5(String md5);

    void deleteByGridFSDBFile(GridFSDBFile gridFSDBFile);

    /**
     * 根据md5获取对应文件块后端存储情况并保存文件
     *
     * @param md5       md5
     * @param chunkSize 每个文件块大小
     * @return
     */
    FileChunkInfoResponseDto getFileChunkInfo(String md5, int chunkSize);

    List<FileUpload> saveFileByMd5(String md5, String fileName, String fileSourceIcon);

    /**
     * 保存文件，文件ID由用户指定 ,使用该接口效率极为低下,因为需要先判断文件是否存在，如果存在则覆盖，如果不存在则新建
     *
     * @param fileID
     * @param fileName
     * @param inputStream
     * @return
     */
    MongoFileEntity saveFile(String fileID, String fileName, InputStream inputStream);

    MongoFileEntity saveFile(String fileID, Boolean newVer, String fileName, InputStream inputStream, String source);

    MongoFileEntity saveFile(String fileID, String fileName, InputStream inputStream, String fileSourceIcon,
                             String source);

    /**
     * 保存文件及签名
     *
     * @param fileName
     * @param inputStream
     * @param digest_value
     * @param digest_algorithm
     * @param signature_value
     * @param certificate
     * @return 返回null表示保存失败
     */

    public MongoFileEntity saveFile(String fileName, InputStream inputStream, String digest_value,
                                    String digest_algorithm, String signature_value, String certificate);

    MongoFileEntity saveFile(String fileID, String fileName, InputStream inputStream, String digest_value,
                             String digest_algorithm, String signature_value, String certificate, String fileSourceIcon, String source);

    /**
     * 保存文件及签名, 文件ID由用户指定 ,使用该接口效率极为低下,因为需要先判断文件是否存在，如果存在则覆盖，如果不存在则新建
     *
     * @param fileID
     * @param fileName
     * @param inputStream
     * @param digest_value
     * @param digest_algorithm
     * @param signature_value
     * @param certificate
     * @return 返回null表示保存失败
     */
    public MongoFileEntity saveFile(String fileID, String fileName, InputStream inputStream, String digest_value,
                                    String digest_algorithm, String signature_value, String certificate);

    /**
     * 保存文件
     * @param fileName 不得为空

     * @param inputStream 不得为空
     * @return 返回null表示保存失败
     */

    // MongoFileEntity saveFileWithTenantId(String tenantId, String fileName,
    // InputStream inputStream);

    /**
     * 保存文件，文件ID由用户指定 ,使用该接口效率极为低下,因为需要先判断文件是否存在，如果存在则覆盖，如果不存在则新建
     * @param fileID
     * @param fileName
     * @param inputStream
     * @return
     */
    // MongoFileEntity saveFileWithTenantId(String tenantId, String fileID,
    // String fileName, InputStream inputStream);

    /**
     * 保存文件及签名
     * @param fileName

     * @param inputStream
     * @param digest_value
     * @param digest_algorithm
     * @param signature_value
     * @param certificate
     * @return 返回null表示保存失败
     */

    // public MongoFileEntity saveFileWithTenantId(String tenantId, String
    // fileName, InputStream inputStream,
    // String digest_value, String digest_algorithm, String signature_value,
    // String certificate);

    /**
     * 保存文件及签名, 文件ID由用户指定 ,使用该接口效率极为低下,因为需要先判断文件是否存在，如果存在则覆盖，如果不存在则新建
     * @param fileID
     * @param fileName
     * @param inputStream
     * @param digest_value
     * @param digest_algorithm
     * @param signature_value
     * @param certificate
     * @return 返回null表示保存失败
     */
    // public MongoFileEntity saveFileWithTenantId(String tenantId, String
    // fileID, String fileName,
    // InputStream inputStream, String digest_value, String digest_algorithm,
    // String signature_value,
    // String certificate);

    /**
     * 重命名文件
     *
     * @param fileID
     * @param newFileName
     * @return
     * @throws FileNotFoundException
     */
    LogicFileInfo saveFileName(String fileID, String newFileName) throws FileNotFoundException;

    /**
     * 更新签名
     *
     * @param fileId           not null
     * @param digest_value     not null
     * @param digest_algorithm not null
     * @param signature_value  not null
     * @param certificate      not null
     */
    public void updateSignature(String fileId, String digest_value, String digest_algorithm, String signature_value,
                                String certificate);

    /**
     * 将文件列表push到文件夹中
     *
     * @param folderID
     * @param fileIDs
     * @param purpose  字段名字
     */
    void pushFilesToFolder(String folderID, List<String> fileIDs, String purpose);

    /**
     * 保存文件排序
     *
     * @param folderID
     * @param fileIDs
     * @param purpose
     */
    void saveFilesToFolderSort(String folderID, List<String> fileIDs, String purpose);

    /**
     * 保存文件名称
     *
     * @param folderID
     * @param files
     * @param purpose
     */
    void saveFileNames(String folderID, List<Object> files, String purpose);

    void pushFilesToFolder(String fromFolderId, String toFolderId, String fromPurpose, String toPurpose);

    /**
     * 将文件push到文件夹中
     *
     * @param folderID
     * @param fileID
     * @param purpose  字段名字
     */
    void pushFileToFolder(String folderID, String fileID, String purpose);

    /**
     * 获取文件列表
     *
     * @param nodeName
     * @param purpose  字段名字, 该参数为null时表示获取所有的文件
     * @return
     */
    public List<MongoFileEntity> getFilesFromFolder(String folderID, String purpose);

    /**
     * 获取不包含IO信息的文件列表
     *
     * @param folderID
     * @param purpose
     * @return
     */
    public List<LogicFileInfo> getNonioFilesFromFolder(String folderID, String purpose);

    /**
     * 获取不包含IO信息的文件列表
     *
     * @param folderIDs
     * @return
     */
    Map<String, List<LogicFileInfo>> getNonioFilesFromFolders(Collection<String> folderIDs);

    /**
     * 获取不包含IO信息的文件列表(带附件信息，ps：尺寸)
     *
     * @param folderID
     * @param purpose
     * @return
     */
    public List<LogicFileInfoExt> getNonioFilesFromFolderExt(String folderID, String purpose);

    /**
     * 获取单个文件
     *
     * @param fileID
     * @return
     */
    public MongoFileEntity getFile(String fileID);

    public LogicFileInfo getLogicFileInfo(String fileID);

    /**
     * 将文件夹中的所有文件清空
     *
     * @param nodeName
     */
    public void popAllFilesFromFolder(String folderID);

    /**
     * 清除文件夹中的某个文件
     *
     * @param folderID
     * @param fileID
     */
    public void popFileFromFolder(String folderID, String fileID);

    /**
     * 判断文件是否在文件夹中
     *
     * @param folderID
     * @param fileID
     * @return 存在返回true, 不存在返回false
     */
    public boolean isFileInFolder(String folderID, String fileID);

    /**
     * 该方法仅用于测试
     *
     * @param tenantId
     */
    public void setCurrentTenantId(String tenantId);

    /**
     * 产生swf文件副本
     *
     * @param fileID
     */
    void createReplicaOfSWF(String fileID);

    /**
     * 产生swf文件副本
     * @param inputstream
     * @param fileID
     */
    /* void createReplicaOfSWF(InputStream inputstream, String fileID); */

    /**
     * 获取文件的swf副本文件,该副本文件没有逻辑信息
     *
     * @param fileID
     * @throws FileNotFoundException
     */
    MongoFileEntity getReplicaOfSWF(String fileID) throws FileNotFoundException;

    /**
     * 销毁swf副本文件
     *
     * @param fileID
     */
    void destroyReplicaOfSWF(String fileID);

    /**
     * 销毁文件夹, 不可还原
     *
     * @param folderID
     * @throws Exception
     */
    void destroyFolder(String folderID);

    /**
     * 删除文件,转移至日志中,可恢复
     *
     * @param fileID
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IntrospectionException
     * @throws Exception
     */
    void deleteFile(String fileID) throws IntrospectionException, IllegalAccessException, InvocationTargetException;

    /**
     * 销毁文件,如果文件被文件夹引用了，这时会抛出异常, 不可恢复
     *
     * @param fileID
     */
    void destroyFile(String fileID);

    /**
     * 创建唯一的folderID
     *
     * @return
     */
    String createFolderID();

    /**
     * 获取引用该文件的文件夹
     *
     * @param fileID
     */
    List<Folder> getFoldersOfFile(String fileID);

    MongoFileEntity copyFile(String fileID) throws FileNotFoundException, IntrospectionException,
            IllegalAccessException, InvocationTargetException;

    MongoFileEntity copyFileAndRename(String fileID, String fileName) throws FileNotFoundException, IntrospectionException,
            IllegalAccessException, InvocationTargetException;

    /**
     * 获取文件夹信息
     *
     * @param folderID
     * @return
     */
    Folder getFolderByFolderID(String folderID);

    /**
     * 获取文件夹最后的更新时间
     *
     * @param folderID
     * @return
     */
    Date getLastModifyTimeOfFolder(String folderID);

    /**
     * 文件夹在指定的时间之后是否有更新
     *
     * @param folderID
     * @param modifyTime
     * @return
     */
    boolean isFolerChanged(String folderID, Date modifyTime);

    /**
     * 导出文件夹被更新过的信息,没有被数据被更新时，则返回null
     *
     * @param folderID
     * @param modifyTime
     * @return
     * @throws FileNotFoundException
     */
    File exportChangeInfo(String folderID, Date modifyTime) throws FileNotFoundException;

    /**
     * 导入文件夹被更新过的信息
     *
     * @param changeInfo
     * @return
     * @throws Exception
     */
    boolean importChangeInfo(File changeInfo) throws Exception;

    /**
     * 文件夹是否存在
     *
     * @param folderID
     * @return
     */
    boolean isFolderExist(String folderID);

    /**
     * 判断文件是否存在于夹中
     *
     * @param fileId
     * @return
     */
    boolean isFileInFolder(String fileId);

    /**
     * 该方法请
     */
    void testInOneTra();

    /**
     * 分页查询文件
     *
     * @return
     */
    public List<QueryItem> getFilesByPage(int firstResult, int maxResults);

    public List<QueryItem> getFilesByPageWithTenantId(String tenantID, int firstResult, int maxResults);

    /**
     * 获取所有在mongo中的原文件
     *
     * @param synbeforedays
     * @return
     */
    public List<GridFSDBFile> findProtoFiles(String synbeforedays);

    public List<GridFSDBFile> findProtoFilesWithTenantId(String tenantID, String synbeforedays);

    /**
     * 从mongo中文件原文件
     *
     * @param physicalFileId
     * @return
     */
    public void destroyProtoFile(String physicalFileId);

    List<LogicFileInfo> getFilesByPhysicalFileId(String physicalFileId);

    List<LogicFileInfo> getFilesByPhysicalFileId(List<String> physicalFileId);

    public MongoFileEntity findProtoFile(String physicalFileId);

    // boolean savePhysicalFile(String tenantId, String fileID, String fileName,
    // String contentType, InputStream inputStream);

    public Boolean existFile(String physicalFileId);

    MongoFileEntity savePhysicalFile(String tenantId, String fileID, String fileName, String contentType,
                                     InputStream inputStream);

    /**
     * 分页查询文件
     *
     * @return
     * @validTime 有效时间
     */
    public List<QueryItem> getFilesByPage(String tenantId, int firstResult, int maxResults, String validTime);

    /**
     * 更新文件名称
     *
     * @param fileID   文件uuid
     * @param fileName 要更改成的文件名
     */
    public void updateFileName(String fileUUID, String fileName);

    boolean isExistFile(String fileID);

    MongoFileEntity getFile(String physicalFileId, String tenantId) throws FileNotFoundException;

    /**
     * 内外网同步使用,上传时不做MD5唯一性判定
     *
     * @param fileID
     * @param fileName
     * @param contentType
     * @param inputStream
     * @return
     */
    MongoFileEntity savePhysicalFileWithoutVerifyMD5(String tenantId, String fileID, String fileName,
                                                     String contentType, InputStream inputStream);

    /**
     * 查询附件历史
     *
     * @param fileID
     * @return
     */
    public List<LogicFileInfo> queryFileHistory(String fileID);

    MongoFileEntity uploadNoStreamFileMD5(String fileID, String fileName, String digest_value,
                                          String digest_algorithm, String signature_value, String certificate, String fileSourceIcon, String source, Boolean newVer);

    void updateFileInputstream(String fileID, String filename, String contentType, InputStream inputStream);

    MongoFileEntity getPhysicalFile(String physicalFileId);

    List<LogicFileInfo> getLogicFileInfo(List<String> fileIds);

    void popAllFilesFromFolder(String folderID, String purpose);
}
