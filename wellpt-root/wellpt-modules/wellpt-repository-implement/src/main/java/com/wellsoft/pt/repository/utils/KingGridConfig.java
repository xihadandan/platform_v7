package com.wellsoft.pt.repository.utils;

import com.wellsoft.pt.app.context.AppContextConfigurerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 * 金格配置
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/11/11   Create
 * </pre>
 */
@Configuration
public class KingGridConfig extends AppContextConfigurerAdapter {

    @Value("${kingGrid.file.uploadFilePath}")
    private String uploadFilePath;

    @Value("${kingGrid.file.targetFilePath}")
    private String targetFilePath;

    @Value("${ofd.convert.kingGrid.url}")
    private String url;

    //管理url
    @Value("${ofd.convert.kingGrid.adminUrl}")
    private String adminUrl;

    @Value("${ofd.convert.kingGrid.app_key}")
    private String appKey;

    @Value("${ofd.convert.kingGrid.app_secret}")
    private String appSecret;

    public String getUploadFilePath() {
        return uploadFilePath;
    }

    public String getTargetFilePath() {
        return targetFilePath;
    }

    public String getUrl() {
        return url;
    }

    public String getAdminUrl() {
        return adminUrl;
    }

    public String getAppKey() {
        return appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }
}
