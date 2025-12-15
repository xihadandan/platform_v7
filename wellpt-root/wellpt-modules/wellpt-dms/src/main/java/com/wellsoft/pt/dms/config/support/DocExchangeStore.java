package com.wellsoft.pt.dms.config.support;

import com.google.common.collect.Lists;
import com.wellsoft.pt.dms.enums.DocExchangeNotifyWayEnum;

import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/5/14
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/14    chenq		2018/5/14		Create
 * </pre>
 */
public class DocExchangeStore extends Store {

    private List<DocExchangeNotifyWayEnum> notifyTypes = Lists.newArrayList();

    private boolean auditFlow;

    private String flowUuid;

    private boolean encrypt;

    private boolean urge;

    private boolean sign;

    private boolean feedback;

    public List<DocExchangeNotifyWayEnum> getNotifyTypes() {
        return notifyTypes;
    }

    public void setNotifyTypes(List<DocExchangeNotifyWayEnum> notifyTypes) {
        this.notifyTypes = notifyTypes;
    }

    public boolean isAuditFlow() {
        return auditFlow;
    }

    public void setAuditFlow(boolean auditFlow) {
        this.auditFlow = auditFlow;
    }

    public String getFlowUuid() {
        return flowUuid;
    }

    public void setFlowUuid(String flowUuid) {
        this.flowUuid = flowUuid;
    }

    public boolean isEncrypt() {
        return encrypt;
    }

    public void setEncrypt(boolean encrypt) {
        this.encrypt = encrypt;
    }

    public boolean isUrge() {
        return urge;
    }

    public void setUrge(boolean urge) {
        this.urge = urge;
    }

    public boolean isSign() {
        return sign;
    }

    public void setSign(boolean sign) {
        this.sign = sign;
    }

    public boolean isFeedback() {
        return feedback;
    }

    public void setFeedback(boolean feedback) {
        this.feedback = feedback;
    }
}
