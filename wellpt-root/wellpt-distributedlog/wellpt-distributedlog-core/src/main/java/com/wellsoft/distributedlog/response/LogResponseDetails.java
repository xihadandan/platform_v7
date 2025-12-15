package com.wellsoft.distributedlog.response;

import com.wellsoft.distributedlog.dto.LogDTO;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2021年07月05日   chenq	 Create
 * </pre>
 */
public class LogResponseDetails {

    private long total = 0L;

    private List<LogDTO> logDTOs;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<LogDTO> getLogDTOs() {
        return logDTOs;
    }

    public void setLogDTOs(List<LogDTO> logDTOs) {
        this.logDTOs = logDTOs;
    }
}
