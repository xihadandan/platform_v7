package com.wellsoft.pt.dms.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Auther: yt
 * @Date: 2021/7/21 10:47
 * @Description:
 */
public class DmsDocExchangeForwardOrgDto implements Serializable {

    private static final long serialVersionUID = -4293298185044394420L;


    private String forwardUnitId; //转发单位Id

    private String forwardUnitName; //转发单位名称

    private Date forwardDate; //转发时间

    private List<DmsDocExchangeRecordDetailDto> forwardList; //转发记录


    public String getForwardUnitId() {
        return forwardUnitId;
    }

    public void setForwardUnitId(String forwardUnitId) {
        this.forwardUnitId = forwardUnitId;
    }

    public String getForwardUnitName() {
        return forwardUnitName;
    }

    public void setForwardUnitName(String forwardUnitName) {
        this.forwardUnitName = forwardUnitName;
    }

    public Date getForwardDate() {
        return forwardDate;
    }

    public void setForwardDate(Date forwardDate) {
        this.forwardDate = forwardDate;
    }

    public List<DmsDocExchangeRecordDetailDto> getForwardList() {
        return forwardList;
    }

    public void setForwardList(List<DmsDocExchangeRecordDetailDto> forwardList) {
        this.forwardList = forwardList;
    }
}
