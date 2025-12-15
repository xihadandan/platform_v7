package com.wellsoft.pt.dms.bean;

import java.io.Serializable;

/**
 * Description: 数据标签DTO
 *
 * @author chenq
 * @date 2018/6/11
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/6/11    chenq		2018/6/11		Create
 * </pre>
 */

public class DmsDataLabelDto implements Serializable {

    private static final long serialVersionUID = 5940440099546579761L;

    private String uuid;

    private String userId; //归属用户

    private String labelName;//标签名称

    private String labelColor;//标签颜色

    private String labelRelaUuid;//标签与数据的关系UUID

    private String moduleId;//归属模块ID

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getLabelColor() {
        return labelColor;
    }

    public void setLabelColor(String labelColor) {
        this.labelColor = labelColor;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getLabelRelaUuid() {
        return labelRelaUuid;
    }

    public void setLabelRelaUuid(String labelRelaUuid) {
        this.labelRelaUuid = labelRelaUuid;
    }
}
