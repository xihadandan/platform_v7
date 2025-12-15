package com.wellsoft.pt.log.dto;

import com.wellsoft.pt.log.entity.LogManageDetailsEntity;
import com.wellsoft.pt.log.entity.LogManageOperationEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/6/28.1	    zenghw		2021/6/28		    Create
 * </pre>
 * @date 2021/6/28
 */
public class LogManageOperationDto extends LogManageOperationEntity {

    /**
     * 管理日志详情列表
     **/
    private List<LogManageDetailsEntity> logManageDetailsEntity = new ArrayList<>();

    public List<LogManageDetailsEntity> getLogManageDetailsEntity() {
        return logManageDetailsEntity;
    }

    public void setLogManageDetailsEntity(List<LogManageDetailsEntity> logManageDetailsEntity) {
        this.logManageDetailsEntity = logManageDetailsEntity;
    }
}
