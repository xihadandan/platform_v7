package com.wellsoft.pt.app.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yt
 * @title: AppPageDefinitionPathBean
 * @date 2020/7/10 14:32
 * 工作台路径
 */
public class AppPageDefinitionPathBean implements Serializable {

    private String uuid;
    //系统uuid
    private String appPiUuid;
    //是否用户默认
    private Boolean userDef;
    // 名称
    private String name;
    // ID
    private String id;
    // 编号
    private String code;
    //归属
    private String ascription;
    //来源 1：用户，2：组织，3：角色，4：默认
    private String source;
    //计算路径
    private String calculatePath;
    // 创建时间 用于排序处理
    private Date createTime;
    //访问url
    private String url;
    //页面容器类型
    private String wtype;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAscription() {
        return ascription;
    }

    public void setAscription(String ascription) {
        this.ascription = ascription;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCalculatePath() {
        return calculatePath;
    }

    public void setCalculatePath(String calculatePath) {
        this.calculatePath = calculatePath;
    }

    public Boolean getUserDef() {
        return userDef;
    }

    public void setUserDef(Boolean userDef) {
        this.userDef = userDef;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getAppPiUuid() {
        return appPiUuid;
    }

    public void setAppPiUuid(String appPiUuid) {
        this.appPiUuid = appPiUuid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWtype() {
        return wtype;
    }

    public void setWtype(String wtype) {
        this.wtype = wtype;
    }
}
