package com.wellsoft.pt.dm.controller.request;

import com.wellsoft.pt.dm.enums.MarkType;
import org.apache.commons.compress.utils.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年06月01日   chenq	 Create
 * </pre>
 */
public class UpdateDataRnRequest implements Serializable {

    private static final long serialVersionUID = 802826425524069408L;

    private List<Long> dataUuids = Lists.newArrayList();
    private List<Long> relaDataUuids = Lists.newArrayList();
    private MarkType type = MarkType.ONE_TO_ONE;
    private String relaId;
    private Boolean override = true;

    public List<Long> getDataUuids() {
        return dataUuids;
    }

    public void setDataUuids(List<Long> dataUuids) {
        this.dataUuids = dataUuids;
    }

    public List<Long> getRelaDataUuids() {
        return relaDataUuids;
    }

    public void setRelaDataUuids(List<Long> relaDataUuids) {
        this.relaDataUuids = relaDataUuids;
    }

    public MarkType getType() {
        return type;
    }

    public void setType(MarkType type) {
        this.type = type;
    }

    public Boolean getOverride() {
        return override;
    }

    public void setOverride(Boolean override) {
        this.override = override;
    }

    public String getRelaId() {
        return relaId;
    }

    public void setRelaId(String relaId) {
        this.relaId = relaId;
    }
}
