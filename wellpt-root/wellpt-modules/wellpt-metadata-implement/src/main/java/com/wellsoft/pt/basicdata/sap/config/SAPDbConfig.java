package com.wellsoft.pt.basicdata.sap.config;

/**
 * sapDBconfig
 * Description: 如何描述该类
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-7-28.1  zhengky	2014-7-28	  Create
 * </pre>
 * @date 2014-7-28
 */
public class SAPDbConfig {

    public String client;
    public String userid;
    public String password;
    public String language;
    public String hostname;
    public String sysnumber;

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getSysnumber() {
        return sysnumber;
    }

    public void setSysnumber(String sysnumber) {
        this.sysnumber = sysnumber;
    }

}
