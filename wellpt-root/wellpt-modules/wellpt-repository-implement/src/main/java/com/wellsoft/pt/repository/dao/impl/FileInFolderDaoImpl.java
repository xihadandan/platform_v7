package com.wellsoft.pt.repository.dao.impl;

import com.wellsoft.pt.jpa.hibernate.HibernateDao;
import com.wellsoft.pt.repository.dao.FileInFolderDao;
import com.wellsoft.pt.repository.entity.FileInFolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FileInFolderDaoImpl extends HibernateDao<FileInFolder, String> implements FileInFolderDao {

    @Override
    public void delete(String uuid) {
        super.delete(uuid);
    }

    @Override
    public boolean isFileInFolder(String fileID, String folderID, String purpose) {
        Map<String, Object> values = new HashMap<String, Object>();
        String hql = "FROM FileInFolder O WHERE O.fileUuid = :fileUuid AND O.folder.uuid = :folderUuid ";
        values.put("folderUuid", folderID);
        values.put("fileUuid", fileID);
        if (StringUtils.isNoneBlank(purpose)) {
            hql += " AND O.purpose = :purpose";
            values.put("purpose", purpose);
        }
        List<FileInFolder> fileInFolders = this.find(hql, values);
        return (fileInFolders != null && fileInFolders.size() > 0);
    }

    @Override
    public List<FileInFolder> getFilesFromFolder(String folderID, String purpose) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("folderUuid", folderID);
        if (StringUtils.isBlank(purpose)) {
            return this.find("FROM FileInFolder O WHERE O.folder.uuid = :folderUuid order by O.seq", values);
        } else {
            values.put("purpose", purpose);
            return this.find(
                    "FROM FileInFolder O WHERE O.purpose = :purpose AND O.folder.uuid = :folderUuid order by O.seq",
                    values);
        }
    }

    @Override
    public List<FileInFolder> getFoldersOfFile(String fileID) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("fileID", fileID);
        return this.find("FROM FileInFolder O WHERE O.fileUuid = :fileID ", values);
    }

}
