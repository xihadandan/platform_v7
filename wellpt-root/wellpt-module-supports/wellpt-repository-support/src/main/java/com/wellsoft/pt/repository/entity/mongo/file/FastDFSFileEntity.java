package com.wellsoft.pt.repository.entity.mongo.file;

import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.support.FastDFSUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.csource.fastdfs.FileInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/12/20
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/12/20    chenq		2019/12/20		Create
 * </pre>
 */
public class FastDFSFileEntity extends MongoFileEntity {

    private final FileInfo fileInfo;

    private final String fileId;
    private String filename;
    private LogicFileInfo logicFileInfo = null;
    private String contentType;
    private byte[] bytes;

    public FastDFSFileEntity(FileInfo fileInfo, String fileId) {
        this.fileInfo = fileInfo;
        this.fileId = fileId;
    }

    /**
     * 该文件对外的ID
     *
     * @return
     */
    public String getId() {
        return this.logicFileInfo.getUuid();
    }

    /**
     * 该文件对外的ID
     *
     * @return
     */
    public String getFileID() {
        return this.logicFileInfo.getUuid();
    }

    public String getPhysicalID() {
        return fileId;
    }

    public String getFileName() {
        return this.logicFileInfo != null ? this.logicFileInfo.getFileName() : this.filename;
    }

    public String getContentType() {
        return this.logicFileInfo != null ? this.logicFileInfo.getContentType() : null;
    }

    public String getMd5() {
        return null;
    }

    public Date getUploadDate() {
        return this.fileInfo.getCreateTimestamp();
    }

    @Override
    public long getChunkSize() {
        return this.fileInfo.getFileSize();
    }

    @Override
    public LogicFileInfo getLogicFileInfo() {
        return logicFileInfo;
    }

    public void setLogicFileInfo(LogicFileInfo logicFileInfo) {
        this.logicFileInfo = logicFileInfo;
        this.filename = logicFileInfo.getFileName();
        this.contentType = logicFileInfo.getContentType();

    }

    @Override
    public long getLength() {
        return this.fileInfo.getFileSize();
    }

    public InputStream getInputstream() {
        try {
            String tempFilePath = System.getProperty(
                    "java.io.tmpdir") + "/" + "FastDFS_TEMP/" + this.fileId;
            File tempFile = new File(tempFilePath);
            if (!tempFile.exists()) { //下载到系统的临时目录
                String[] parts = this.fileId.split("/");
                String[] directory = new String[parts.length - 1];
                for (int i = 0; i < directory.length; i++) {
                    directory[i] = parts[i];
                }
                FileUtils.forceMkdir(new File(System.getProperty(
                        "java.io.tmpdir") + "/" + "FastDFS_TEMP/" + StringUtils.join(directory,
                        "/")));
                tempFile.createNewFile();
                FastDFSUtils.downloadFile(this.fileId, tempFilePath);
                tempFile = new File(tempFilePath);
            }
            return new FileInputStream(tempFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
