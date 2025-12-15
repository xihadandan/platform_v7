package com.wellsoft.pt.repository.dao.base;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.support.enums.EnumQueryPattern;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 文件操作接口
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-3-12.1	hunt		2014-3-12		Create
 * </pre>
 * @date 2014-3-12
 */
public interface BaseMongoDao {
    public final String dbTimePattern = "yyyyMMddmmss";

    /**
     * 将文件持久化
     *
     * @param filePath,保存的路径 <br/> 保存到mongodb里面时，该字段将作为bucket <br/>如果该值为null则默认为fs
     * @param fileName       文件名
     * @param file           文件
     * @param validateMd5    是否需要md5加密
     * @return
     */
    public MongoFileEntity saveFile(String dbName, String filePath, String fileName, String contentType,
                                    InputStream inputStream, String id, boolean validateMd5);

    /**
     * 通过id精确定位filePath文件路径下的文件记录
     *
     * @param filePath 文件路径<br/>如果该值为null则默认为fs
     * @param id       文件在保存时,会返回一个对象给用户,该对象中的id即为该参数.
     * @return
     * @throws FileNotFoundException
     */
    public MongoFileEntity findFileById(String dbName, String filePath, String id);

    /**
     * 通过file模糊搜索filePath文件路径下的文件记录
     *
     * @param filePath 文件路径 <br/>如果该值为null则默认为fs
     * @param fileName 文件名
     * @param pattern  文件名模糊匹配方式
     * @return
     */
    public List<MongoFileEntity> findFileByName(String dbName, String filePath, String fileName,
                                                EnumQueryPattern pattern);

    public List<GridFSDBFile> findProtoFiles(String dbName, String filePath, String synbeforedays);

    /**
     *根据fileName定位更新文件
     * @param filePath 文件路径 <br/>如果该值为null则默认为fs
     * @param fileName
     * @param id  文件在保存时,会返回一个对象给用户,该对象中的id即为该参数.
     * @return
     */
    // mongodb drive 没有提供该接口
    // public int updateFileNameById(String filePath,String fileName, String
    // id);

    /**
     * 根据id定位更新文件
     *
     * @param filePath 文件路径 <br/>如果该值为null则默认为fs
     * @param fileName
     * @param file
     * @return
     */
    /*
     * public FileEntity updateFileById(String filePath,String id, File file);
     */

    void saveChunkFile(String fileID, String fileName, String dbName, String filePath, String md5, int chunkIndex, int chunkSize, long fileSize, InputStream inputStream);

    List<GridFSDBFile> findFileByMetadataMd5(String dbName, String filePath, String md5);

    GridFSDBFile findFileByMd5(String dbName, String filePath, String md5);

    /**
     * 根据id 精确删除文件记录
     *
     * @param filePath 文件路径 <br/>如果该值为null则默认为fs
     * @param id       文件在保存时,会返回一个对象给用户,该对象中的id即为该参数.
     */
    public void deleteFileById(String dbName, String filePath, String id);

    /**
     * 根据fileName精确 删除文件记录
     *
     * @param filePath 文件路径 <br/>如果该值为null则默认为fs
     * @param fileName
     */
    public void deleteFileByFileName(String dbName, String filePath, String fileName);

    public DBObject findDocumentById(String dbName, String collectionName, String id);

    /**
     * 创建文档,如果创建失败则返回null, id为null时表示id为驱动产生。
     *
     * @param collectionName
     * @param params
     * @param id
     * @return
     */
    public DBObject createDocument(String dbName, String collectionName, Map<String, Object> params, String id);

    /**
     * 创建文档,如果创建失败则返回null
     *
     * @param collectionName
     * @param dbObject
     * @return
     */
    public DBObject createDocument(String dbName, String collectionName, DBObject dbObject);

    void deleteByGridFSDBFile(String dbName, String filePath, GridFSDBFile gridFSDBFile);

    public void deleteDocumentById(String dbName, String collectionName, String id);

    void deleteDocument(String dbName, String collectionName, DBObject query);

    public DBObject findOneDocument(String dbName, String collectionName, DBObject query);

    public DBCursor findDocument(String dbName, String collectionName, DBObject query);

    DBCursor findDocument(String dbName, String collectionName, DBObject query, DBObject projection);

    /**
     * 根据id更新document
     *
     * @param collectionName
     * @param params
     * @param id
     * @return
     */
    public int updateDocument(String dbName, String collectionName, Map<String, Object> params, String id);

    public int updateDocument(String dbName, String collectionName, DBObject dbObject, String id);

    public int updateDocument(String dbName, String collectionName, DBObject dbObject, DBObject query);

    public MongoFileEntity findOneFile(String dbName, String filePath, DBObject query);

    /**
     * 更新文件名称
     *
     * @param fileID   文件uuid
     * @param fileName 要更改成的文件名
     */
    public void updateFileName(String dbName, String physicalFileId, String fileName);

    /**
     * 查找ObjectId主键DBObject
     *
     * @param collectionName
     * @param id
     * @return
     */
    public DBObject findDocumentByObjectIdId(String dbName, String collectionName, String id);

    /**
     * @param dbName
     * @param collectionName
     * @param query
     * @return
     */
    long countDocument(String dbName, String collectionName, DBObject query);


    void saveOrUpdate(String collectionName, Map<String, Object> data, Map<String, Object> query);

    Set<String> getCollectionNames();

}
