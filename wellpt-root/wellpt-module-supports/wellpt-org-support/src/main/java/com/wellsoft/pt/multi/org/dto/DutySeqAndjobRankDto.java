package com.wellsoft.pt.multi.org.dto;

import com.wellsoft.context.jdbc.support.BaseQueryItem;

/**
 * Description: 职务与职级对应关系
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/12/2.1	    zenghw		2021/12/2		    Create
 * </pre>
 * @date 2021/12/2
 */
public class DutySeqAndjobRankDto implements BaseQueryItem {

    private String jobRankId;

    private String dutySeqCode;

    public String getJobRankId() {
        return this.jobRankId;
    }

    public void setJobRankId(final String jobRankId) {
        this.jobRankId = jobRankId;
    }

    public String getDutySeqCode() {
        return this.dutySeqCode;
    }

    public void setDutySeqCode(final String dutySeqCode) {
        this.dutySeqCode = dutySeqCode;
    }
}
