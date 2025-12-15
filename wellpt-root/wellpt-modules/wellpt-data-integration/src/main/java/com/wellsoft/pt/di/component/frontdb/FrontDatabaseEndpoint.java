package com.wellsoft.pt.di.component.frontdb;

import com.wellsoft.pt.di.anotation.EndpointParameter;
import com.wellsoft.pt.di.component.AbstractEndpoint;
import com.wellsoft.pt.di.enums.DIParameterDomType;
import com.wellsoft.pt.di.enums.EdpParameterType;
import com.wellsoft.pt.di.util.CamelContextUtils;
import org.apache.camel.Exchange;
import org.apache.camel.spi.Synchronization;
import org.apache.commons.io.IOUtils;
import org.xerial.snappy.SnappyOutputStream;
import sun.misc.BASE64Encoder;

import java.io.*;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/26
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/26    chenq		2019/8/26		Create
 * </pre>
 */
public class FrontDatabaseEndpoint extends
        AbstractEndpoint<FrontDatabaseDIComponent, FrontDatabaseProducer, FrontDatabaseConsumer> {

    @EndpointParameter(domType = DIParameterDomType.INPUT, name = "JDBC URL")
    protected String jdbcUrl;

    @EndpointParameter(domType = DIParameterDomType.INPUT, name = "用户名")
    protected String user;

    @EndpointParameter(domType = DIParameterDomType.INPUT, name = "密码")
    protected String password;

    @EndpointParameter(name = "FTP服务地址")
    private String ftpAddress;

    @EndpointParameter(name = "FTP服务用户账号")
    private String ftpUser;

    @EndpointParameter(name = "FTP服务用户密码")
    private String ftpPassword;

    //对应同步表的方向位
    @EndpointParameter(domType = DIParameterDomType.SELECT, type = EdpParameterType.PRODUCER, name = "网络类型", dataJSON = "{ \"内网\": 0, \"外网\": 1 }")
    private Integer networkType = 0;

    //对应同步表状态位
    @EndpointParameter(domType = DIParameterDomType.SELECT, type = EdpParameterType.CONSUMER, name = "数据类型", dataJSON = "{ \"同步数据\": 0, \"反馈数据\": 2}")
    private Integer syncType = 0;


    @EndpointParameter(name = "每次处理数据量", type = EdpParameterType.CONSUMER)
    private Integer limit = 100;


    @Override
    public String endpointPrefix() {
        return "front-db";
    }

    @Override
    public String endpointName() {
        return "数据交换-前置库";
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFtpAddress() {
        return ftpAddress;
    }

    public void setFtpAddress(String ftpAddress) {
        this.ftpAddress = ftpAddress;
    }

    public String getFtpUser() {
        return ftpUser;
    }

    public void setFtpUser(String ftpUser) {
        this.ftpUser = ftpUser;
    }

    public String getFtpPassword() {
        return ftpPassword;
    }

    public void setFtpPassword(String ftpPassword) {
        this.ftpPassword = ftpPassword;
    }

    public Integer getNetworkType() {
        return networkType;
    }

    public void setNetworkType(Integer networkType) {
        this.networkType = networkType;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getSyncType() {
        return syncType;
    }

    public void setSyncType(Integer syncType) {
        this.syncType = syncType;
    }


    public String encryptedDataAsString(Object data) {
        return new BASE64Encoder().encode(gzipData2ByteStream(data).toByteArray());
    }


    public ByteArrayOutputStream gzipData2ByteStream(Object data) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    new SnappyOutputStream(byteArrayOutputStream));
            objectOutputStream.writeObject(data);
            objectOutputStream.flush();
            IOUtils.closeQuietly(objectOutputStream);
            return byteArrayOutputStream;
        } catch (Exception e) {
            logger.error("压缩对象数据流异常：", e);
            throw new RuntimeException("压缩对象数据流异常");
        }
    }


    public void gzipUploadFile2Ftp(InputStream inputstream, String fileName) {
        File zipFile = new File(
                System.getProperty("java.io.tmpdir") + "/" + fileName + ".zip");
        FileOutputStream fileOutputStream = null;
        SnappyOutputStream snappyOutputStream = null;
        try {
            //压缩文件
            fileOutputStream = new FileOutputStream(zipFile);
            snappyOutputStream = new SnappyOutputStream(fileOutputStream);
            IOUtils.copyLarge(inputstream, snappyOutputStream);
            fileOutputStream.flush();
            IOUtils.closeQuietly(inputstream);
            IOUtils.closeQuietly(snappyOutputStream);
            IOUtils.closeQuietly(fileOutputStream);
            zipFile = new File(
                    System.getProperty("java.io.tmpdir") + "/" + fileName + ".zip");
            inputStreamUpload2Ftp(new FileInputStream(zipFile), fileName + ".zip",
                    new FileUploadFtpDoneSynchroniztion(zipFile));

        } catch (Exception e) {
            logger.error("压缩文件流到临时目录下异常：", e);
            throw new RuntimeException(e);
        }
    }

    public void inputStreamUpload2Ftp(final InputStream inputStream, String fileName,
                                      FileUploadFtpDoneSynchroniztion fileUploadFtpDoneSynchroniztion) {
        //上传ftp
        CamelContextUtils.producer().sendBody(
                String.format("ftp://%s?username=%s&password=%s&fileName=%s&binary=true",
                        this.getFtpAddress(), this.getFtpUser(), this.getFtpPassword(), fileName),
                inputStream);
        IOUtils.closeQuietly(inputStream);
        fileUploadFtpDoneSynchroniztion.file.delete();
    }

    public void byteUpload2Ftp(byte[] bytes, String fileName) {
        this.inputStreamUpload2Ftp(new ByteArrayInputStream(bytes), fileName,
                new FileUploadFtpDoneSynchroniztion());
    }

    private class FileUploadFtpDoneSynchroniztion implements Synchronization {

        private File file;

        public FileUploadFtpDoneSynchroniztion() {
        }

        public FileUploadFtpDoneSynchroniztion(File file) {
            this.file = file;
        }

        @Override
        public void onComplete(Exchange exchange) {
            if (this.file != null && this.file.exists()) {
                this.file.delete();
            }
        }

        @Override
        public void onFailure(Exchange exchange) {

        }
    }

}
