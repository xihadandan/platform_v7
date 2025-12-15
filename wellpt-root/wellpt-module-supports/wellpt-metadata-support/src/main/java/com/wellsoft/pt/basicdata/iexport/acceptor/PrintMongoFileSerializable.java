package com.wellsoft.pt.basicdata.iexport.acceptor;

import java.io.Serializable;

public class PrintMongoFileSerializable implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 5571772543521591003L;

    private String fileId;
    private String fileName;
    private byte[] fileArray;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getFileArray() {
        return fileArray;
    }

    public void setFileArray(byte[] fileArray) {
        this.fileArray = fileArray;
    }

}
