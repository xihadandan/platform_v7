package com.wellsoft.pt.api.response;

import com.wellsoft.pt.api.WellptResponse;

/**
 * Description: 登录响应
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-8-19.1  zhengky	2014-8-19	  Create
 * </pre>
 * @date 2014-8-19
 */
public class SecurityLoginResponse extends WellptResponse {

    public final static String SYSTEM_MESSAGE_TYPE = "SYSTEM_MESSAGE_TYPE";
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -507458900251831372L;
    /**
     * @Fields smsverify : TODO
     */
    private boolean smsverify;
    /**
     * @Fields module : TODO
     */
    private String module;
    /**
     * @Fields maxUpload : TODO
     */
    private int maxUpload;
    /**
     * @Fields removeCache : TODO
     */
    private boolean removeCache;
    /**
     * @Fields needCA : TODO
     */
    private boolean needCA;
    /**
     * @Fields uid : TODO
     */
    private String uid;
    /**
     * @Fields host : TODO
     */
    private String host;
    /**
     * @Fields access_token : TODO
     */
    private String access_token;
    /**
     * @Fields locUploadInterval : TODO
     */
    private int locUploadInterval;
    /**
     * @Fields senderDic : TODO
     */
    private Object senderDic;
    /**
     * @Fields orgJspFileId : TODO
     */
    private String orgJspFileId;

    /**
     * @return the uid
     */
    public String getUid() {
        return uid;
    }

    /**
     * @param uid the uid to set
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the access_token
     */
    public String getAccess_token() {
        return access_token;
    }

    /**
     * @param access_token the access_token to set
     */
    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    /**
     * @return the smsverify
     */
    public boolean isSmsverify() {
        return smsverify;
    }

    /**
     * @param smsverify the smsverify to set
     */
    public void setSmsverify(boolean smsverify) {
        this.smsverify = smsverify;
    }

    /**
     * @return the module
     */
    public String getModule() {
        return module;
    }

    /**
     * @param module the module to set
     */
    public void setModule(String module) {
        this.module = module;
    }

    /**
     * @return the maxUpload
     */
    public int getMaxUpload() {
        return maxUpload;
    }

    /**
     * @param maxUpload the maxUpload to set
     */
    public void setMaxUpload(int maxUpload) {
        this.maxUpload = maxUpload;
    }

    /**
     * @return the removeCache
     */
    public boolean isRemoveCache() {
        return removeCache;
    }

    /**
     * @param removeCache the removeCache to set
     */
    public void setRemoveCache(boolean removeCache) {
        this.removeCache = removeCache;
    }

    /**
     * @return the needCA
     */
    public boolean isNeedCA() {
        return needCA;
    }

    /**
     * @param needCA the needCA to set
     */
    public void setNeedCA(boolean needCA) {
        this.needCA = needCA;
    }

    /**
     * @return the locUploadInterval
     */
    public int getLocUploadInterval() {
        return locUploadInterval;
    }

    /**
     * @param locUploadInterval the locUploadInterval to set
     */
    public void setLocUploadInterval(int locUploadInterval) {
        this.locUploadInterval = locUploadInterval;
    }

    /**
     * @return the senderDic
     */
    public Object getSenderDic() {
        return senderDic;
    }

    /**
     * @param senderDic the senderDic to set
     */
    public void setSenderDic(Object senderDic) {
        this.senderDic = senderDic;
    }

    /**
     * @return the orgJspFileId
     */
    public String getOrgJspFileId() {
        return orgJspFileId;
    }

    /**
     * @param orgJspFileId the orgJspFileId to set
     */
    public void setOrgJspFileId(String orgJspFileId) {
        this.orgJspFileId = orgJspFileId;
    }


}
