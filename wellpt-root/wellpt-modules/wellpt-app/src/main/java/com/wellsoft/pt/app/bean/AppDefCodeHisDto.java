package com.wellsoft.pt.app.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/12/7
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/12/7    chenq		2018/12/7		Create
 * </pre>
 */
public class AppDefCodeHisDto implements Serializable {
    private static final long serialVersionUID = 8329055249542051015L;

    private String uuid;

    private String relaBusizUuid;

    private String script;

    private String scriptType;

    private String author;

    private String remark;

    private Date createTime;

    private String creator;

    public String getRelaBusizUuid() {
        return relaBusizUuid;
    }

    public void setRelaBusizUuid(String relaBusizUuid) {
        this.relaBusizUuid = relaBusizUuid;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getScriptType() {
        return scriptType;
    }

    public void setScriptType(String scriptType) {
        this.scriptType = scriptType;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
