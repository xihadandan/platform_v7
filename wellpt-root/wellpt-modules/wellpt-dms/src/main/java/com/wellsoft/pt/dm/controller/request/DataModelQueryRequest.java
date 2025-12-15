package com.wellsoft.pt.dm.controller.request;

import com.wellsoft.pt.dm.entity.DataModelEntity;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年01月28日   chenq	 Create
 * </pre>
 */
public class DataModelQueryRequest implements Serializable {

    private static final long serialVersionUID = -8072866685545184087L;
    private List<String> module;
    private List<DataModelEntity.Type> type;

    public List<String> getModule() {
        return module;
    }

    public void setModule(List<String> module) {
        this.module = module;
    }

    public List<DataModelEntity.Type> getType() {
        return type;
    }

    public void setType(List<DataModelEntity.Type> type) {
        this.type = type;
    }
}
