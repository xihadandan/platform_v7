package com.wellsoft.pt.repository.entity.mongo.file;

import com.mongodb.gridfs.GridFSDBFile;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.InputStream;
import java.util.Date;

/**
 * Description: 文件持久化类
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
public class MongoFileEntity {

    private final GridFSDBFile dbFile;
    private LogicFileInfo logicFileInfo = null;

    public MongoFileEntity() {
        this.dbFile = null;
    }

    public MongoFileEntity(GridFSDBFile dbFile) {
        this.dbFile = dbFile;
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
        return this.dbFile.getId().toString();
    }

    public String getFileName() {
        return this.dbFile.getFilename();
    }

    public long getChunkSize() {
        return this.dbFile.getChunkSize();
    }

    public long getLength() {
        return this.dbFile.getLength();
    }

    public String getContentType() {
        return this.dbFile.getContentType();
    }

    public String getMd5() {
        return this.dbFile.getMD5();
    }

    public Date getUploadDate() {
        return this.dbFile.getUploadDate();
    }

    @JsonIgnore
    public InputStream getInputstream() {
        return dbFile.getInputStream();
    }

    public LogicFileInfo getLogicFileInfo() {
        return logicFileInfo;
    }

    public void setLogicFileInfo(LogicFileInfo logicFileInfo) {
        this.logicFileInfo = logicFileInfo;
        dbFile.put("filename", logicFileInfo.getFileName());// 设置逻辑文件名
    }

}
