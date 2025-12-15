package com.wellsoft.pt.dyform.manager.dto;

import com.wellsoft.pt.dyform.manager.entity.DyformFileListSourceConfig;

/**
 * 列表式附件配置_附件来源DTO
 */
public class DyformFileListSourceConfigDto extends DyformFileListSourceConfig {
    public static final String ROW_STATUS_DELETED = "deleted";
    private String rowStatus;

    public String getRowStatus() {
        return rowStatus;
    }

    public void setRowStatus(String rowStatus) {
        this.rowStatus = rowStatus;
    }
}
