package com.wellsoft.pt.dm.controller.request;

import com.wellsoft.pt.dm.enums.MarkType;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年05月30日   chenq	 Create
 * </pre>
 */
public class UpdateDataMarkRequest implements Serializable {
    private static final long serialVersionUID = 7289906375380806167L;

    private List<Long> dataUuids;
    private MarkType type;

    public List<Long> getDataUuids() {
        return dataUuids;
    }

    public void setDataUuids(List<Long> dataUuids) {
        this.dataUuids = dataUuids;
    }

    public MarkType getType() {
        return type;
    }

    public void setType(MarkType type) {
        this.type = type;
    }
}
