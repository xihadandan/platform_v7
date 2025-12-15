package com.wellsoft.pt.repository.dao;

import com.wellsoft.pt.repository.entity.Folder;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import org.apache.commons.io.FileExistsException;

import java.beans.IntrospectionException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface FolderDao {

    /**
     * 新创建一个文件夹, 调用其他push开头的方法也可以达到创建文件夹的效果，但是如果已知该folderID对应的文件夹从来没创建过，调用该方法则效率更高<br/>
     * <b>如果调用者在不确定该folderID对应的文件夹是否已经有存在，切勿调用该方法。而应调用其他push开头的方法</b><br/>
     * NOTE:该folderID之前有创建过文件夹但已被删，这种情况也不得调用该方法<br/>
     *
     * @param folderID
     * @param fileIDs  该参数为空时只创建一个空文件夹
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IntrospectionException
     * @throws FileNotFoundException     void createFolder(String folderID, List<String> fileIDs ) throws IntrospectionException, IllegalAccessException, InvocationTargetException, FileNotFoundException;
     */

    void saveFilesToFolderSort(String folderID, List<String> fileIDs, String purpose);

    /**
     * 将文件列表push到文件夹中,如果该文件夹不存在，则会主动去创建
     *
     * @param folderID
     * @param fileIDs
     * @return
     * @throws IntrospectionException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws FileNotFoundException
     * @throws FileExistsException
     */
    void pushFilesToFolder(String folderID, List<String> fileIDs, String purpose) throws IntrospectionException,
            IllegalAccessException, InvocationTargetException, FileNotFoundException, FileExistsException;

    /**
     * 将文件push到文件夹中
     *
     * @param folderID
     * @param fileID
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IntrospectionException
     * @throws FileNotFoundException
     * @throws FileExistsException
     */
    void pushFileToFolder(String folderID, String fileID, String purpose) throws IntrospectionException,
            IllegalAccessException, InvocationTargetException, FileNotFoundException, FileExistsException;

    /**
     * 将文件 从文件夹中pop出来
     *
     * @param folderID
     * @param fileID
     * @throws IntrospectionException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws FileNotFoundException
     * @throws IOException
     */
    void popFileFromFolder(String folderID, String fileID) throws IntrospectionException, IllegalAccessException,
            InvocationTargetException, FileNotFoundException, IOException;

    public void popFilesFromFolder(String folderID, List<String> fileIDs) throws IntrospectionException,
            IllegalAccessException, InvocationTargetException, FileNotFoundException, IOException;

    /**
     * 获取文件夹中的文件
     *
     * @param folderID
     * @param purpose  该字段为null时表示获取所有的文件
     * @return
     * @throws FileNotFoundException
     * @throws IntrospectionException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    List<MongoFileEntity> getFilesFromFolder(String folderID, String purpose) throws FileNotFoundException;

    /**
     * 获取文件的逻辑信息
     *
     * @param folderID
     * @param purpose
     * @return
     * @throws FileNotFoundException
     * @Deprecated @http://zen.well-soft.com:81/zentao/bug-view-49229.html#不同字段存在同一个LogicFileInfo时，会合并到一个字段。fileDao.getLogicFileInfo有缓存
     */
    @Deprecated
    public List<LogicFileInfo> getLogicFilesFromFolder(String folderID, String purpose) throws FileNotFoundException;

    /**
     * 获取已被删除的文件夹的日志
     * @param folderID
     * @return
     */
    /*List<Folder> getDeletedFolderLog(String folderID);*/

    /**
     * 获取文件夹信息
     *
     * @param folderID
     * @return
     * @throws IntrospectionException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws FileNotFoundException
     */
    public Folder getFolder(String folderID) throws FileNotFoundException;

    /**
     * 判断文件是否存在
     *
     * @param folderID
     * @param fileID
     * @return
     */
    boolean isFileInFolder(String folderID, String fileID);

    /**
     * 判断文件是否存在,适用表单控件
     *
     * @param folderID
     * @param fileID
     * @param purpose
     * @return
     */
    boolean isFileInFolder(String folderID, String fileID, String purpose);

    /**
     * 判断文件夹是否存在
     *
     * @param folderID
     * @return
     */
    public boolean isFolderExist(String folderID);

    /**
     * 删除文件夹,保存至日志表中
     *
     * @param folderID
     * @throws IntrospectionException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws FileNotFoundException
     * @throws IOException
     */
    void deleteFolder(String folderID) throws IntrospectionException, IllegalAccessException,
            InvocationTargetException, FileNotFoundException, IOException;

    /**
     *  从日志表中还原文件夹,如果日志中也没有文件, 则返回 null
     * @param folderID
     * @return
     * @throws FileNotFoundException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IntrospectionException
     */
	/*Folder recovery(String folderID) throws IntrospectionException, IllegalAccessException, InvocationTargetException,
			FileNotFoundException;*/

    /**
     * 销毁文件夹，不可还原
     *
     * @param folderID
     * @throws Exception
     */
    void destroyFolder(String folderID);

    void save(Folder folder);

    /**
     * 4.1	判断文件夹是否存在, 如果不存在则创建
     * @param folderID
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IntrospectionException
     */
    //public LogicFolderInfo isFolderExistOrCreate(String folderID) throws IntrospectionException, IllegalAccessException, InvocationTargetException;

    //public LogicFolderInfo createFolder(String folderID) throws IntrospectionException, IllegalAccessException, InvocationTargetException;
}
