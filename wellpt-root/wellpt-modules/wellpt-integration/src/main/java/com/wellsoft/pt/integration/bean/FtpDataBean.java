package com.wellsoft.pt.integration.bean;

import com.wellsoft.pt.integration.entity.ExchangeDataBatch;

/**
 * Description: 如何描述该类
 *
 * @author ruanhg
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-27.1	ruanhg		2014-10-27		Create
 * </pre>
 * @date 2014-10-27
 */
public class FtpDataBean extends ExchangeDataBatch {

    private String ftp_host;

    private String ftp_pass_word;

    private Integer ftp_post;

    private String ftp_user_name;

    public String getFtp_host() {
        return ftp_host;
    }

    public void setFtp_host(String ftp_host) {
        this.ftp_host = ftp_host;
    }

    public String getFtp_pass_word() {
        return ftp_pass_word;
    }

    public void setFtp_pass_word(String ftp_pass_word) {
        this.ftp_pass_word = ftp_pass_word;
    }

    public Integer getFtp_post() {
        return ftp_post;
    }

    public void setFtp_post(Integer ftp_post) {
        this.ftp_post = ftp_post;
    }

    public String getFtp_user_name() {
        return ftp_user_name;
    }

    public void setFtp_user_name(String ftp_user_name) {
        this.ftp_user_name = ftp_user_name;
    }

}
