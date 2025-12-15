package com.wellsoft.pt.bpm.engine.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 流程环节信息分发
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-12-4.1	zhulh		2012-12-4		Create
 * </pre>
 * @date 2012-12-4
 */
@Entity
@Table(name = "WF_TASK_INFO_DISTRIBUTION")
@DynamicUpdate
@DynamicInsert
public class TaskInfoDistribution extends IdEntity {
    private static final long serialVersionUID = -6371095800880351054L;

    // 流程实例UUID
    private String flowInstUuid;
    // 环节实例UUID
    private String taskInstUuid;
    // 分发内容
    private String content;
    // 分发附件
    private String repoFileIds;

    /**
     * @return the flowInstUuid
     */
    public String getFlowInstUuid() {
        return flowInstUuid;
    }

    /**
     * @param flowInstUuid 要设置的flowInstUuid
     */
    public void setFlowInstUuid(String flowInstUuid) {
        this.flowInstUuid = flowInstUuid;
    }

    /**
     * @return the taskInstUuid
     */
    public String getTaskInstUuid() {
        return taskInstUuid;
    }

    /**
     * @param taskInstUuid 要设置的taskInstUuid
     */
    public void setTaskInstUuid(String taskInstUuid) {
        this.taskInstUuid = taskInstUuid;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content 要设置的content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the repoFileIds
     */
    public String getRepoFileIds() {
        return repoFileIds;
    }

    /**
     * @param repoFileIds 要设置的repoFileIds
     */
    public void setRepoFileIds(String repoFileIds) {
        this.repoFileIds = repoFileIds;
    }

}
