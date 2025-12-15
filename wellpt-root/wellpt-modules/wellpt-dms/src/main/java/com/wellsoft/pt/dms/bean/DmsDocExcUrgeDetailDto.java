package com.wellsoft.pt.dms.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * Description: 催办详情数据DTO
 *
 * @author chenq
 * @date 2018/5/18
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/18    chenq		2018/5/18		Create
 * </pre>
 */
public class DmsDocExcUrgeDetailDto implements Serializable {
    private static final long serialVersionUID = -47523747759604881L;

    private String uuid;

    private String toUserName;

    private String toUserId;

    private Date createTime;

    private String content;

    private String urgeWay;

    private String urgeWayName;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrgeWay() {
        return urgeWay;
    }

    public void setUrgeWay(String urgeWay) {
        this.urgeWay = urgeWay;
    }

    public String getUrgeWayName() {
        return urgeWayName;
    }

    public void setUrgeWayName(String urgeWayName) {
        this.urgeWayName = urgeWayName;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }
}
