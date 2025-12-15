package com.wellsoft.pt.repository.web;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.repository.entity.FileEntity;
import com.wellsoft.pt.repository.entity.FileUpload;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Description:系统文件由jcr统一管理 这里文件可以存放在三个地方 ：文件目录下面，临时文件目录，模块目录的具体单据下面
 * 文件目录为上传不知道划分到模块下的文件所用 临时文件为上传文件异步提交后放置文件的地方，正式上传需要将文件进行移动 模块目录为具体的模块对应的名称，如
 * 某个动态表单对应的模块，模块全局唯一，可以用表名来代替
 *
 * @author lilin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-21.1	lilin		2012-11-21		Create
 * </pre>
 * @date 2012-11-21
 */
interface FileService {

    /**
     * 下载多个文件
     *
     * @param moduleName 模块名
     * @param nodeName   节点名
     * @return
     */
    public List<FileEntity> downFiles(String moduleName, String nodeName);

    /**
     * 下载文件目录下的文件
     *
     * @param nodeName 节点名
     * @return
     */
    public List<FileEntity> downFiles(String nodeName);

    /**
     * 下载模块下节点对应文件的数据流
     *
     * @param moduleName
     * @param nodeName
     * @param fileName
     * @return
     */
    public InputStream downFile(String moduleName, String nodeName, String fileName);

    /**
     * 下载文件目录下节点对应文件的数据流
     *
     * @param nodeName
     * @param filename
     * @return
     */
    public InputStream downFile(String nodeName, String filename);

    /**
     * 获取模块节点下的文件列表
     *
     * @param moduleName
     * @param nodeName
     * @return fileentity 中不包含文件流，只有文件名称等属性
     */
    public List<FileEntity> getFiles(String moduleName, String nodeName);

    /**
     * 获取模块节点下的文件列表
     *
     * @param moduleName
     * @param nodeName
     * @return fileentity 中不包含文件流，只有文件名称等属性
     */
    public List<FileEntity> getFiles2(String moduleName, String nodeName);

    /**
     * 获取文件目录节点下的文件列表
     *
     * @param nodeName
     * @return fileentity 中不包含文件流，只有文件名称等属性
     */
    public List<FileEntity> getFiles(String nodeName);

    /**
     * 删除模块节点下所有文件
     *
     * @param moduleName
     * @param nodeName
     */
    public void deleteFiles(String moduleName, String nodeName);

    /**
     * 删除文件目录下节点和所有文件
     *
     * @param nodeName
     */
    public void deleteFiles(String nodeName);

    /**
     * 删除文件目录下某节点的下某一个文件
     *
     * @param nodeName
     * @param fileName
     */
    public void deleteFile(String nodeName, String fileName);

    /**
     * 删除模块下某节点的下某一个文件
     *
     * @param moduleName
     * @param nodeName
     * @param fileName
     */
    public void deleteFile(String moduleName, String nodeName, String fileName);

    /**
     * 重命名文件
     *
     * @param nodeName
     * @param oldFileName
     * @param newFileName
     */
    public void renameFile(String nodeName, String oldFileName, String newFileName);

    /**
     * 重命名文件
     *
     * @param moduleName
     * @param nodeName
     * @param oldFileName
     * @param newFileName
     */
    public void renameFile(String moduleName, String nodeName, String oldFileName, String newFileName);

    /**
     * 文件是否存在
     *
     * @param moduleName
     * @param nodeName
     * @param fileName
     * @return
     */
    public boolean isExistFile(String moduleName, String nodeName, String fileName);

    /**
     * 文件是否存在
     *
     * @param nodeName
     * @param fileName
     * @return
     */
    public boolean isExistFile(String nodeName, String fileName);

    /**
     * 获取文件列表
     *
     * @param uuid
     * @return
     */
    // public List<FileEntity> getFiles(String uuid);
    public FileEntity getFile(String moduleName, String nodeName, String fileName);

    public void copyFiles(String moduleName, String nodeName, String newNodeName);

    /**
     * 将文档转换成SWF文件
     *
     * @param uuid
     * @return
     */
    public void convert2SWF(List<java.io.File> files);

    /**
     * 将文件流生成的相应的pdf文件
     *
     * @param uuid
     * @return
     */
    public void convert2PDF(InputStream fileStream, String fileName);

    /**
     * 将文件流生成的相应的swf文件
     *
     * @param uuid
     * @return
     */
    public FileEntity convert2SWF(InputStream fileStream, String fileName);

    public boolean isTempExistFile(String tempid, String fileName);

    public void referenceFiles(String moduleName, String nodeName, String newnodeName);

    public void uploadRefenceFile(String moduleName, String nodeName, FileEntity file);

    public void referenceFile(String moduleName, String nodeName, String fileName, String newnodeName);

    /**
     * 删除uuid文件夹下所有文件
     *
     * @param uuid
     */
    // public void deleteFile(String uuid);

    /**
     * 删除uuid文件加下，指定文件名的文件
     *
     * @param uuid
     * @param filename
     */
    // public void deletefile(String uuid, String filename);

    /**
     * 判断是否uuid文件夹下，文件名为filename的文件是否存在
     *
     * @param uuid
     * @param fileName
     * @return
     */
    // public boolean isExistFile(String uuid, String fileName);

    /**
     * 更改文件名
     *
     * @param uuid
     * @param oldFileName
     * @param newFileName
     */
    // public void rename(String uuid, String oldFileName, String newFileName);

    /**
     * @Title: create
     * @Description: 在文档库中建立一个document
     * @param token
     * @param doc
     *            需要建立的文档，主要为设置了相关属性.
     * @param is
     *            该文档的二进制流
     * @return Document 返回类型 返回建立好文档后的document
     * @throws
     */
    // public String create(File file, String fileName, String folderPath)
    // throws IOException;

    /**
     * @param token
     * @param docPath 文件路径
     * @return 返回类型
     * @throws RepositoryException
     * @throws AccessDeniedException
     * @throws
     * @Title: delete
     * @Description: 删除文档
     */
    // public void delete(String id);
    //
    // public Document getDocumentByName(String username);
    //
    // public Document getDocumentById(String id);
    //
    // public void rename(String id, String newName);
    //
    // public Document getProperties(String docPath);
    //
    // public void setProperties(Document doc);
    //
    // public InputStream getContent(String id, boolean checkout) throws
    // Exception;
    //
    // public InputStream getContent(String id) throws Exception;
    //
    // public File getFile(String id) throws Exception;
    //
    // public InputStream getContentByVersion(String docPath, String versionId);
    //
    // public void setContent(String id, File file) throws Exception;
    //
    // public void purge(String docPath);
    //
    // public List<Document> getChilds(String fldPath);
    //
    // public void move(String id, String fldPath) throws Exception;
    //
    // public void copy(String docPath, String fldPath) throws IOException;
    //
    // public String getPath(String uuid);
    //
    // public boolean isValid(String docPath);
    //
    // public String createFile(File file, String fileName, String folderPath)
    // throws IOException;

    void saveFileUpload(FileUpload fileUpload);

    void setFileUploadSign(FileUpload fileUpload);

    <ITEM extends Serializable> List<ITEM> queryFileUpload(String hql, final Map<String, ?> values,
                                                           Class<ITEM> itemClass, PagingInfo pagingInfo);
}
