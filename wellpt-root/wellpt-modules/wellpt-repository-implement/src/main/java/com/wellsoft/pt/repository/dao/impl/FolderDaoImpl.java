package com.wellsoft.pt.repository.dao.impl;

import com.wellsoft.pt.jpa.hibernate.HibernateDao;
import com.wellsoft.pt.repository.dao.FileDao;
import com.wellsoft.pt.repository.dao.FileInFolderDao;
import com.wellsoft.pt.repository.dao.FolderDao;
import com.wellsoft.pt.repository.dao.base.BaseMongoDao;
import com.wellsoft.pt.repository.entity.FileInFolder;
import com.wellsoft.pt.repository.entity.Folder;
import com.wellsoft.pt.repository.entity.FolderOperateLog;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.support.enums.EnumOperateType;
import com.wellsoft.pt.security.util.TenantContextHolder;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.beans.IntrospectionException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Repository
public class FolderDaoImpl extends HibernateDao<Folder, String> implements FolderDao {
    private final String LOGIC_FOLDER_TABLE = "repo_folder_log";
    Logger logger = Logger.getLogger(FolderDaoImpl.class);
    @Autowired
    FileDao fileDao;
    @Autowired
    FileInFolderDao fileInFolderDao;
    @Autowired
    private BaseMongoDao baseMongoDao;
    private String currentTenantId = null;

    public String getCurrentTenantId() {
        if (currentTenantId == null) {
            return TenantContextHolder.getTenantId();
        } else {
            return currentTenantId;
        }
    }

    @Override
    public void saveFilesToFolderSort(String folderID, List<String> fileIDs, String purpose) {
        List<FileInFolder> fileInFolderList = fileInFolderDao.getFilesFromFolder(folderID, purpose);
        for (int i = 0; i < fileIDs.size(); i++) {
            String fileUuid = fileIDs.get(i);
            for (FileInFolder fileInFolder : fileInFolderList) {
                if (StringUtils.equals(fileInFolder.getFileUuid(), fileUuid)) {
                    fileInFolder.setSeq(i);
                }
            }
        }
        for (FileInFolder fileInFolder : fileInFolderList) {
            this.getSession().save(fileInFolder);
        }
    }

    @Override
    public void pushFilesToFolder(String folderID, List<String> fileIDs, String purpose) throws IntrospectionException,
            IllegalAccessException, InvocationTargetException, FileNotFoundException, FileExistsException {
        Assert.notNull(folderID, "parameter[folderID]  is null");
        Assert.notNull(fileIDs, "parameter[fileIDs]  is null");
        Assert.notEmpty(fileIDs, "parameter[fileIDs]  is empty");
        Assert.notNull(purpose, "parameter[purpose]  is null");
        long time1 = System.currentTimeMillis();
        Folder folder = this.getFolder(folderID);
        boolean isNewFolder = false;//是不是新的文件夹
        if (folder == null) {
            isNewFolder = true;
            folder = new Folder();
            folder.setUuid(folderID);
            folder.doBindCreateTimeAsNow();
            folder.doBindCreatorAsCurrentUser();
            folder.doBindDbNameAsCurrentTenantId();
        }

        List<FileInFolder> files = new ArrayList<FileInFolder>();
        long time3 = System.currentTimeMillis();

        for (String fileID : fileIDs) {
            if (!isNewFolder //非新文件夹才需要判断文件有没有在文件夹中。
                    && this.isFileInFolder(folderID, fileID, purpose)) {//关系已存在
                continue;
            }

            if (!fileDao.isExistFile(fileID)) {//文件不存在，
                throw new FileNotFoundException("file[" + fileID + "] does not exist");
            } else {//文件存在
                FileInFolder file = new FileInFolder();
                file.setFileUuid(fileID);
                file.setPurpose((purpose == null || purpose.trim().length() == 0) ? "attach" : purpose.trim());
                file.doBindCreateTimeAsNow();
                file.doBindCreatorAsCurrentUser();
                file.doBindModifierAsCurrentUser();
                file.doBindModifyTimeAsNow();
                file.setFolder(folder);
                files.add(file);

            }
        }
        long time4 = System.currentTimeMillis();
        System.out.println("***push file into folder,query file exist spend " + ((time4 - time3) / 1000.0) + "s******");
        folder.doBindModifierAsCurrentUser();
        folder.doBindModifyTimeAsNow();

        //如果这个注释掉的话，就会导致同一个夹多个purpose文件操作出问题
        //参照bug, http://oa.well-soft.com:3000/zentao/bug-view-176.html
        folder.getFiles().addAll(files);

        //folder.doAddLog(EnumOperateType.PUSH, files);//如果该代码被注释掉，那么文件操作时将不再有日志
        this.save(folder);

        FolderOperateLog log = new FolderOperateLog();
        log.doBindUuid();
        log.doBindOperateType(EnumOperateType.PUSH);
        log.doBindCreateTimeAsNow();
        log.doBindCreatorAsCurrentUser();
        log.doBindModifierAsCurrentUser();
        log.doBindModifyTimeAsNow();
        log.doBindFiles(files);
        log.setFolder(folder);
        this.getSession().save(log);

        for (FileInFolder file : files) {
            this.getSession().save(file);
        }

        long time2 = System.currentTimeMillis();

        System.out.println("*********push file into folder:totally spend " + ((time2 - time1) / 1000.0) + "s******");
    }

    @Override
    public void pushFileToFolder(String folderID, String fileID, String purpose) throws IntrospectionException,
            IllegalAccessException, InvocationTargetException, FileNotFoundException, FileExistsException {
        Assert.notNull(folderID, "parameter[folderID]  is null");
        Assert.notNull(fileID, "parameter[fileID]  is null");
        Assert.notNull(purpose, "parameter[purpose]  is null");
        List<String> fileIDs = new ArrayList<String>();
        fileIDs.add(fileID);
        this.pushFilesToFolder(folderID, fileIDs, purpose);
    }

    @Override
    public void popFileFromFolder(String folderID, String fileID) throws IntrospectionException,
            IllegalAccessException, InvocationTargetException, FileNotFoundException, IOException {
        Assert.notNull(folderID, "parameter[folderID]  is null");
        Assert.notNull(fileID, "parameter[fileID]  is null");
        List<String> fileIDs = new ArrayList<String>();
        fileIDs.add(fileID);
        this.popFilesFromFolder(folderID, fileIDs);
    }

    @Override
    public void popFilesFromFolder(String folderID, List<String> fileIDs) throws IntrospectionException,
            IllegalAccessException, InvocationTargetException, FileNotFoundException, IOException {
        Assert.notNull(folderID, "parameter[folderID]  is null");
        Assert.notNull(fileIDs, "parameter[fileIDs]  is null");
        Assert.notEmpty(fileIDs, "parameter[fileIDs]  is empty");

        Folder folder = this.getFolder(folderID);
        if (folder == null) {
            throw new FileNotFoundException("folder[" + folderID + "] does not exist");
        }
        List<FileInFolder> removedFiles = new ArrayList<FileInFolder>();
        List<FileInFolder> dbFiles = folder.getFiles();
        Iterator<String> it1 = fileIDs.iterator();
        while (it1.hasNext()) {
            String fileID = it1.next();
            Iterator<FileInFolder> it = dbFiles.iterator();
            boolean isExist = false;
            while (it.hasNext()) {
                FileInFolder file = it.next();
                if (file.getFileUuid().equals(fileID)) {
                    it.remove();
                    removedFiles.add(file);
                    isExist = true;
                    break;
                }
            }

            if (!isExist) {//在数据库里面不存在，所以不需要保存到日志中
                it1.remove();
            }
        }

        folder.doAddLog(EnumOperateType.POP, removedFiles);

        if (folder.getFiles().size() == 0) {//没有引用其他文件时直接删除至日志表中
            this.deleteFolder(folder.getUuid());
        } else {
            this.save(folder);
        }

    }

    @Override
    public List<MongoFileEntity> getFilesFromFolder(String folderID, String purpose) throws FileNotFoundException {
        List<FileInFolder> list = this.fileInFolderDao.getFilesFromFolder(folderID, purpose);
        if (list == null || list.size() == 0) {
            return new ArrayList<MongoFileEntity>();
        }

        Iterator<FileInFolder> it = list.iterator();
        List<MongoFileEntity> files = new ArrayList<MongoFileEntity>();
        while (it.hasNext()) {
            FileInFolder file = it.next();
            try {
                files.add(fileDao.getFile(file.getFileUuid()));
            } catch (Exception e) {
                logger.warn(e.getMessage());
            }
        }

        return files;
    }

    public List<LogicFileInfo> getLogicFilesFromFolder(String folderID, String purpose) throws FileNotFoundException {
        List<FileInFolder> list = this.fileInFolderDao.getFilesFromFolder(folderID, purpose);
        if (list == null || list.size() == 0) {
            return new ArrayList<LogicFileInfo>();
        }

        Iterator<FileInFolder> it = list.iterator();
        List<LogicFileInfo> files = new ArrayList<LogicFileInfo>();
        while (it.hasNext()) {
            FileInFolder file = it.next();
            LogicFileInfo logicFileInfo = fileDao.getLogicFileInfo(file.getFileUuid());
            if (logicFileInfo != null) {
                logicFileInfo.setPurpose(file.getPurpose());
                files.add(logicFileInfo);
            }

        }

        return files;
    }

    @Override
    public Folder getFolder(String folderID) {
        return this.findUnique(Restrictions.eq("uuid", folderID));
    }

    @Override
    public boolean isFileInFolder(String folderID, String fileID) {
        return isFileInFolder(folderID, fileID, null);
    }

    @Override
    public boolean isFileInFolder(String folderID, String fileID, String purpose) {
        Folder folder = this.getFolder(folderID);
        if (folder == null) {
            return false;
        }
        return this.fileInFolderDao.isFileInFolder(fileID, folder.getUuid(), purpose);
    }

    @Override
    public boolean isFolderExist(String folderID) {
        Folder folder = this.getFolder(folderID);
        return folder != null;
    }

    @Override
    public void deleteFolder(String folderID) throws IntrospectionException, IllegalAccessException,
            InvocationTargetException {
        Folder folder = this.getFolder(folderID);
        if (folder == null) {
            return;
        }

        System.out.println(folder.getUuid());

        this.delete(folder);

		/*	MapConfig cfg = new MapConfig();
			cfg.setMapPropertyFilter(new PropertyFilter() {
				@Override
				public boolean apply(Object clazzObj, String name, Object value) {
					Class clazz = (Class) clazzObj;
					if (clazz == Folder.class) {//如果是文件类型的,就不要转换成map
						return true;
					}
					return false;
				}
			});
			baseMongoDao.createDocument(getLOGIC_FOLDER_TABLE(), BeanMapUtils.toMap(folder, cfg), null);*/
    }

	/*	@Override
		public Folder recovery(String logID) throws IntrospectionException, IllegalAccessException,
				InvocationTargetException {
			DBObject dbObj = baseMongoDao.findDocumentById(getLOGIC_FOLDER_TABLE(), logID);

			return Folder.doParse(dbObj);
		}*/

    @Override
    public void destroyFolder(String folderID) {
        Folder folder = this.getFolder(folderID);
        if (folder == null) {
            return;
        }
        this.delete(folder);

    }

    /**
     * 获取文件夹在mongodb中的日志表
     * @return
     */
	/*public String getLOGIC_FOLDER_TABLE() {
		return LOGIC_FOLDER_TABLE + "." + getCurrentTenantId();
	}*/

	/*@Override
	public List<Folder> getDeletedFolderLog(String folderID) {
		List<Folder> folders = new ArrayList<Folder>();
		BasicDBObject query = new BasicDBObject("uuid", folderID);
		DBCursor cursor = baseMongoDao.findDocument(getLOGIC_FOLDER_TABLE(), query);
		while (cursor.hasNext()) {
			DBObject dbObj = cursor.next();

			folders.add(Folder.doParse(dbObj));
		}

		return folders;
	}*/

}
