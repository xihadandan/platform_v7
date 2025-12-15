package com.wellsoft.pt.integration.security;

import com.wellsoft.pt.integration.request.SendRequest;
import com.wellsoft.pt.integration.support.DataItem;
import com.wellsoft.pt.integration.support.InputStreamDataSource;
import com.wellsoft.pt.integration.support.StreamingData;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.activation.DataHandler;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class OperationZip {

    public static void main(String args[]) {

        //		String file = "E://wellpt/app_data/aaa.zip";
        //		String saveRootDirectory = "E://zip";
        //		zipFileRead(file, saveRootDirectory);

        File file = new File("E://wellpt/app_data/zip/000000000RY_BATCHID20131217173011.zip");
        //解析zip
        SendRequest request = new SendRequest();
        try {
            ZipFile zipFile = new ZipFile(file);
            String zipName = file.getName();
            String[] zipNameArray = zipName.split("_");
            //一个批次
            request.setBatchId(zipNameArray[1].replace(".zip", ""));
            request.setTypeId(zipNameArray[0]);
            List<DataItem> datdItems = new ArrayList<DataItem>();

            @SuppressWarnings("unchecked")
            Enumeration<ZipEntry> enu = (Enumeration<ZipEntry>) zipFile.entries();

            DataItem dataItem = null;
            List<StreamingData> streamingDatas = null;
            while (enu.hasMoreElements()) {
                //一条数据
                ZipEntry zipElement = (ZipEntry) enu.nextElement();
                String fileName = zipElement.getName();
                InputStream read = zipFile.getInputStream(zipElement);
                if (fileName != null && fileName.indexOf(".") != -1) {//文件
                    if (fileName.indexOf(".xml") > -1) {//xml
                        BufferedReader reader = new BufferedReader(new InputStreamReader(read));
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        try {
                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        } finally {
                            try {
                                read.close();
                            } catch (IOException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                        String text = new String(sb.toString().getBytes("utf-8"));
                        //						StringBuffer out = new StringBuffer();
                        //						byte[] b = new byte[4096];
                        //						for (int n; (n = read.read(b)) != -1;) {
                        //							out.append(new String(b, 0, n));
                        //						}
                        //						String text = new String(out.toString().getBytes("utf-8"));

                        dataItem.setText(text);
                        Document document = DocumentHelper.parseText(text);
                        //获得根节点
                        Element root = document.getRootElement();
                    } else {//附件
                        StreamingData sd = new StreamingData();
                        sd.setFileName(fileName);
                        sd.setDataHandler(new DataHandler(new InputStreamDataSource(read, "octet-stream")));
                        streamingDatas.add(sd);
                    }
                } else {//文件夹
                    if (dataItem != null) {
                        dataItem.setStreamingDatas(streamingDatas);
                        datdItems.add(dataItem);
                    }
                    dataItem = new DataItem();
                    streamingDatas = new ArrayList<StreamingData>();
                    String[] fileNameArray = fileName.split("_");
                    dataItem.setDataId(fileNameArray[0]);
                    dataItem.setRecVer(Integer.parseInt(fileNameArray[1].replace("/", "")));
                }
            }
            dataItem.setStreamingDatas(streamingDatas);
            datdItems.add(dataItem);
            request.setDataList(datdItems);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        //发送数据
        request = request;
    }

    private static void zipFileRead(String file, String saveRootDirectory) {
        try {
            ZipFile zipFile = new ZipFile(file);
            @SuppressWarnings("unchecked")
            Enumeration<ZipEntry> enu = (Enumeration<ZipEntry>) zipFile.entries();
            while (enu.hasMoreElements()) {
                ZipEntry zipElement = (ZipEntry) enu.nextElement();
                String fileName = zipElement.getName();
                InputStream read = zipFile.getInputStream(zipElement);
                if (fileName != null && fileName.indexOf(".") != -1) {//是否为文件 （文件带有路径如：/images/a.jpg）
                    execute(zipElement, read, saveRootDirectory);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void execute(ZipEntry ze, InputStream read, String saveRootDirectory) throws FileNotFoundException,
            IOException {
        //如果只读取图片，自行判断就OK.
        String fileName = ze.getName();
        //        if(fileName.lastIndexOf(".jpg")!= -1 || fileName.lastIndexOf(".bmp")!= -1
        //            || fileName.lastIndexOf(".jpeg")!= -1){//指定要解压出来的文件格式（这些格式可抽取放置在集合或String数组通过参数传递进来，方法更通用）
        File file = new File(saveRootDirectory + fileName);
        if (!file.exists()) {
            File rootDirectoryFile = new File(file.getParent());
            //创建目录
            if (!rootDirectoryFile.exists()) {
                boolean ifSuccess = rootDirectoryFile.mkdirs();
                if (ifSuccess) {
                    System.out.println("文件夹创建成功!");
                } else {
                    System.out.println("文件创建失败!");
                }
            }
            //创建文件
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        //写入文件
        BufferedOutputStream write = new BufferedOutputStream(new FileOutputStream(file));
        int cha = 0;
        while ((cha = read.read()) != -1) {
            write.write(cha);
        }
        //要注意IO流关闭的先后顺序
        write.flush();
        write.close();
        read.close();
        //        }
    }
}
