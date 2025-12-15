package com.wellsoft.pt.dyform.manager.dto;

import com.wellsoft.pt.dyform.manager.entity.DyformFileListButtonConfig;

public class DyformFileListButtonConfigDto extends DyformFileListButtonConfig {

    public static final String ROW_STATUS_DELETED = "deleted";
    private String rowStatus;

    public String getRowStatus() {
        return rowStatus;
    }

    public void setRowStatus(String rowStatus) {
        this.rowStatus = rowStatus;
    }
}
