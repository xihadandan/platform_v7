package com.wellsoft.pt.workflow.work.vo;

import java.util.List;

/**
 * Description:
 * 请求排序字段vo
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2022/1/5   Create
 * </pre>
 */
public class SortFieldVo {


    //扩展字段
    private List<String> extraColumns;

    public List<String> getExtraColumns() {
        return extraColumns;
    }

    public void setExtraColumns(List<String> extraColumns) {
        this.extraColumns = extraColumns;
    }

}
