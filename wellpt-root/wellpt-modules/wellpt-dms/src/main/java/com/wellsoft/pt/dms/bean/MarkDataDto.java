package com.wellsoft.pt.dms.bean;

import com.wellsoft.pt.dms.entity.DataMarkEntity;

import java.io.Serializable;
import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/6/19
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/6/19    chenq		2018/6/19		Create
 * </pre>
 */
public class MarkDataDto implements Serializable {

    private List<DataMarkEntity> dataList;

    private String entityClassName;//标记关联实体类

    private String tableName;//需要标记的数据表

    private String statusColumn;//对应更新标记的数据表字段

    private String updateTimeColumn;//更新时间状态字段

    private String listenerClassName;//操作侦听类

    private boolean isDeleteMark = false;

    public List<DataMarkEntity> getDataList() {
        return dataList;
    }

    public void setDataList(List<DataMarkEntity> dataList) {
        this.dataList = dataList;
    }

    public String getEntityClassName() {
        return entityClassName;
    }

    public void setEntityClassName(String entityClassName) {
        this.entityClassName = entityClassName;
    }


    public boolean getIsDeleteMark() {
        return isDeleteMark;
    }

    public void setIsDeleteMark(boolean deleteMark) {
        this.isDeleteMark = deleteMark;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }


    public String getListenerClassName() {
        return listenerClassName;
    }

    public void setListenerClassName(String listenerClassName) {
        this.listenerClassName = listenerClassName;
    }

    public String getStatusColumn() {
        return statusColumn;
    }

    public void setStatusColumn(String statusColumn) {
        this.statusColumn = statusColumn;
    }

    public String getUpdateTimeColumn() {
        return updateTimeColumn;
    }

    public void setUpdateTimeColumn(String updateTimeColumn) {
        this.updateTimeColumn = updateTimeColumn;
    }
}
