package com.wellsoft.pt.di.component.frontdb;

import com.wellsoft.pt.di.anotation.EndpointParameter;
import com.wellsoft.pt.di.component.AbstractEndpoint;
import com.wellsoft.pt.di.component.WithoutConsumer;
import com.wellsoft.pt.di.enums.DIParameterDomType;
import org.apache.commons.io.IOUtils;
import org.xerial.snappy.SnappyInputStream;
import sun.misc.BASE64Decoder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/30
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/30    chenq		2019/8/30		Create
 * </pre>
 */
public class FrontDbDataResotreEndpoint extends
        AbstractEndpoint<FrontDbDataRestoreDIComponent, FrontDbDataResotreProducer, WithoutConsumer> {
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
    //对应同步表状态位
    @EndpointParameter(domType = DIParameterDomType.SELECT, name = "数据类型", dataJSON = "{ \"同步数据\": 0, \"反馈数据\": 2}")
    private Integer syncType = 0;

    @Override
    public String endpointPrefix() {
        return "front-db-restore";
    }

    @Override
    public String endpointName() {
        return "数据交换-前置库数据还原端点";
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

    public Object dencryptedStringAsObject(String encrypted) {
        ByteArrayInputStream byteArrayInputStream = null;
        Object obj = null;
        try {
            byte[] bytes = new BASE64Decoder().decodeBuffer(encrypted);
            byteArrayInputStream = new ByteArrayInputStream(bytes);
            ObjectInputStream inputStream = new ObjectInputStream(
                    new SnappyInputStream(byteArrayInputStream));
            obj = inputStream.readObject();
            IOUtils.closeQuietly(inputStream);
        } catch (Exception e) {
            logger.error("解密数据异常：", e);
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(byteArrayInputStream);
        }
        return obj;
    }


    public Object ungzipFile2Object(File file) {
        Object obj = null;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(
                    new SnappyInputStream(fileInputStream));
            obj = objectInputStream.readObject();
        } catch (Exception e) {
            logger.error("解压文件数据流为对象异常：", e);
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(fileInputStream);
        }
        return obj;

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

    public Integer getSyncType() {
        return syncType;
    }

    public void setSyncType(Integer syncType) {
        this.syncType = syncType;
    }
}
