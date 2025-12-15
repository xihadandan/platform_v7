package com.wellsoft.pt.dms.core.support;

import com.wellsoft.pt.basicdata.datastore.bean.DataStoreParams;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年12月07日   chenq	 Create
 * </pre>
 */
public class DyformPrevNextActionData extends ActionData {

    private String primaryField;

    private int index;

    private String dataUuid;

    private DataStoreParams dataStoreParams;

    private Boolean first;

    private Boolean last;


    public String getDataUuid() {
        return dataUuid;
    }

    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

    public DataStoreParams getDataStoreParams() {
        return dataStoreParams;
    }

    public void setDataStoreParams(DataStoreParams dataStoreParams) {
        this.dataStoreParams = dataStoreParams;
    }

    public String getPrimaryField() {
        return primaryField;
    }

    public void setPrimaryField(String primaryField) {
        this.primaryField = primaryField;
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Boolean getFirst() {
        return first;
    }

    public void setFirst(Boolean first) {
        this.first = first;
    }

    public Boolean getLast() {
        return last;
    }

    public void setLast(Boolean last) {
        this.last = last;
    }

    public static enum ActionEnum {
        PREV, NEXT;

        ActionEnum() {
        }
    }

}
