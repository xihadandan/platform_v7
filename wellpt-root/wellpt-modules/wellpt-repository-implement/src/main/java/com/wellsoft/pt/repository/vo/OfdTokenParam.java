package com.wellsoft.pt.repository.vo;

/**
 * Description: 获取token的基础参数类
 *
 * @author linzc
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期        修改内容
 * 2021/9/18.1    linzc       2021/9/18     Create
 * </pre>
 */
public class OfdTokenParam {

    private String app_key;

    private String app_secret;

    public String getApp_key() {
        return app_key;
    }

    public void setApp_key(String app_key) {
        this.app_key = app_key;
    }

    public String getApp_secret() {
        return app_secret;
    }

    public void setApp_secret(String app_secret) {
        this.app_secret = app_secret;
    }
}
