package com.wellsoft.pt.repository.dao;

import com.wellsoft.pt.repository.entity.FileInFolder;

import java.util.List;

public interface FileInFolderDao {

    boolean isFileInFolder(String fileID, String folderID, String purpose);

    List<FileInFolder> getFilesFromFolder(String folderID, String purpose);

    /**
     * 获取与该文件相关的记录
     *
     * @param fileID
     * @return
     */
    List<FileInFolder> getFoldersOfFile(String fileID);

    void delete(String uuid);

}
