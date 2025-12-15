package com.wellsoft.pt.api.test;

import com.wellsoft.pt.api.DefaultWellptClient;
import com.wellsoft.pt.api.WellptClient;
import com.wellsoft.pt.api.request.FileUploadRequest;
import com.wellsoft.pt.api.response.FileUploadResponse;

import java.io.File;
import java.io.FileInputStream;

public class Test1 {

    public static void main(String[] args) {
        String baseAddress = "http://localhost:8080/webservices/wellpt/rest/service";
        WellptClient wellptClient = new DefaultWellptClient(baseAddress, "T001", "adm_pt", "0");
        // 1、获取用户权限
        FileUploadRequest fileUploadApiRequest = new FileUploadRequest();
        try {
            fileUploadApiRequest.setInputStream(new FileInputStream(new File("D:\\AppData\\视图优化建议.txt")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        fileUploadApiRequest.setFileName("视图优化建议.txt");
        fileUploadApiRequest.setFolderID("text");
        FileUploadResponse response = wellptClient.execute(fileUploadApiRequest);
        System.out.println(response.getFileId());
    }
}
