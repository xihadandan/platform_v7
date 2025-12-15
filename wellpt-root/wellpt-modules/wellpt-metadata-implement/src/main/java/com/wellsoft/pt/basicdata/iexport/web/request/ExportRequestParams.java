package com.wellsoft.pt.basicdata.iexport.web.request;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年11月09日   chenq	 Create
 * </pre>
 */
public class ExportRequestParams implements Serializable {
    private List<String> uuids;
    private List<String> types;

    // 今日 、 昨日至今 、 一周内、 ?日 至今
    private Integer modifyDays;

    private Boolean exportDependency = true;

    private Boolean thread = false; // 多线程导出

    public List<String> getUuids() {
        return uuids;
    }

    public void setUuids(List<String> uuids) {
        this.uuids = uuids;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public Integer getModifyDays() {
        return modifyDays;
    }

    public void setModifyDays(Integer modifyDays) {
        this.modifyDays = modifyDays;
    }

    public Boolean getExportDependency() {
        return exportDependency;
    }

    public void setExportDependency(Boolean exportDependency) {
        this.exportDependency = exportDependency;
    }

    public Boolean getThread() {
        return thread;
    }

    public void setThread(Boolean thread) {
        this.thread = thread;
    }
}
