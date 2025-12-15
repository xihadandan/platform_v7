package com.wellsoft.pt.dms.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;

import javax.persistence.MappedSuperclass;
import java.util.Date;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/7/3
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/7/3    chenq		2018/7/3		Create
 * </pre>
 */
@MappedSuperclass
public class DataActiveReportEntity extends TenantEntity {
    private static final long serialVersionUID = -5050968963877159807L;

    private String dataValue;//数据真实值：uuid、或者id，等唯一标识数据的值

    private String dataText;//数据展示文本

    private Date lastActiveTime;//最后一次活动时间

    private Integer activeCount;//活动次数


    public String getDataValue() {
        return dataValue;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }

    public String getDataText() {
        return dataText;
    }

    public void setDataText(String dataText) {
        this.dataText = dataText;
    }

    public Date getLastActiveTime() {
        return lastActiveTime;
    }

    public void setLastActiveTime(Date lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }

    public Integer getActiveCount() {
        return activeCount;
    }

    public void setActiveCount(Integer activeCount) {
        this.activeCount = activeCount;
    }


}
