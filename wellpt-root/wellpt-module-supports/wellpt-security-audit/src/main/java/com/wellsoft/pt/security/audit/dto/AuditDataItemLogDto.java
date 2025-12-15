package com.wellsoft.pt.security.audit.dto;

import com.wellsoft.pt.security.audit.entity.AuditDataItemLogEntity;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年12月26日   chenq	 Create
 * </pre>
 */
public class AuditDataItemLogDto extends AuditDataItemLogEntity {
    private static final long serialVersionUID = 7910458827135497744L;

    public AuditDataItemLogDto() {
    }

    public AuditDataItemLogDto(String dataItemName, String dataItemCode, String dataType, String newValue, String oldValue) {
        this.dataItemName = dataItemName;
        this.dataItemCode = dataItemCode;
        this.dataType = dataType;
        this.newValue = newValue;
        this.oldValue = oldValue;
    }


}
