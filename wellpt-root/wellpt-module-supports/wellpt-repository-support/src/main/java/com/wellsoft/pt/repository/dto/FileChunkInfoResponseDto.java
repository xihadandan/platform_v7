package com.wellsoft.pt.repository.dto;

import com.wellsoft.pt.repository.entity.FileUpload;

import java.util.List;

public class FileChunkInfoResponseDto {
    List<Integer> chunkIndexList;

    boolean hasMd5FileFlag;

    List<FileUpload> uploadFiles;

    public List<Integer> getChunkIndexList() {
        return chunkIndexList;
    }

    public void setChunkIndexList(List<Integer> chunkIndexList) {
        this.chunkIndexList = chunkIndexList;
    }

    public boolean isHasMd5FileFlag() {
        return hasMd5FileFlag;
    }

    public void setHasMd5FileFlag(boolean hasMd5FileFlag) {
        this.hasMd5FileFlag = hasMd5FileFlag;
    }

    public List<FileUpload> getUploadFiles() {
        return uploadFiles;
    }

    public void setUploadFiles(List<FileUpload> uploadFiles) {
        this.uploadFiles = uploadFiles;
    }
}
