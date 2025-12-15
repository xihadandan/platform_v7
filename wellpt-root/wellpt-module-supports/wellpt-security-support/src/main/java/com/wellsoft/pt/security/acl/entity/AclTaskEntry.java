package com.wellsoft.pt.security.acl.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 流程环节实例权限实体
 *
 * @author liuxj
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本  修改人    修改日期      修改内容
 * V1.0   liuxj    2024/12/19    Create
 * </pre>
 * @date 2024/12/19
 */
@Entity
@Table(name = "acl_task_entry")
public class AclTaskEntry extends IdEntity {

    private static final long serialVersionUID = -8649987256584750162L;

    @ApiModelProperty("身份标识 用户，群组，角色")
    private String sid;

    @ApiModelProperty("环节实例id")
    private String objectIdIdentity;


    @ApiModelProperty("查阅权限")
    private Boolean readAuth =false;

    @ApiModelProperty("待办")
    private Boolean todoAuth=false;

    @ApiModelProperty("已办")
    private Boolean doneAuth=false;

    @ApiModelProperty("关注")
    private Boolean attentionAuth=false;

    @ApiModelProperty("未阅")
    private Boolean unReadAuth=false;

    @ApiModelProperty("已阅")
    private Boolean flagReadAuth=false;

    @ApiModelProperty("督办")
    private Boolean superviseAuth=false;

    @ApiModelProperty("监控")
    private Boolean monitorAuth=false;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getObjectIdIdentity() {
        return objectIdIdentity;
    }

    public void setObjectIdIdentity(String objectIdIdentity) {
        this.objectIdIdentity = objectIdIdentity;
    }

    @Column(name = "read_auth", nullable = false)
    public Boolean getReadAuth() {
        return readAuth;
    }

    public void setReadAuth(Boolean consultAuth) {
        this.readAuth = consultAuth;
    }

    @Column(name = "todo_auth", nullable = false)
    public Boolean getTodoAuth() {
        return todoAuth;
    }

    public void setTodoAuth(Boolean todoAuth) {
        this.todoAuth = todoAuth;
    }

    @Column(name = "done_auth", nullable = false)
    public Boolean getDoneAuth() {
        return doneAuth;
    }

    public void setDoneAuth(Boolean doneAuth) {
        this.doneAuth = doneAuth;
    }

    @Column(name = "attention_auth", nullable = false)
    public Boolean getAttentionAuth() {
        return attentionAuth;
    }

    public void setAttentionAuth(Boolean attentionAuth) {
        this.attentionAuth = attentionAuth;
    }

    @Column(name = "unread_auth", nullable = false)
    public Boolean getUnReadAuth() {
        return unReadAuth;
    }

    public void setUnReadAuth(Boolean unReadAuth) {
        this.unReadAuth = unReadAuth;
    }

    @Column(name = "flag_read_auth", nullable = false)
    public Boolean getFlagReadAuth() {
        return flagReadAuth;
    }

    public void setFlagReadAuth(Boolean flagReadAuth) {
        this.flagReadAuth = flagReadAuth;
    }

    @Column(name = "supervise_auth", nullable = false)
    public Boolean getSuperviseAuth() {
        return superviseAuth;
    }

    public void setSuperviseAuth(Boolean superviseAuth) {
        this.superviseAuth = superviseAuth;
    }

    @Column(name = "monitor_auth", nullable = false)
    public Boolean getMonitorAuth() {
        return monitorAuth;
    }

    public void setMonitorAuth(Boolean monitorAuth) {
        this.monitorAuth = monitorAuth;
    }
}
