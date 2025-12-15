package com.wellsoft.pt.workflow.service;

/**
 * Description: 流程应急处理层
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2022/2/8.1	    zenghw		2022/2/8		    Create
 * </pre>
 * @date 2022/2/8
 */
public interface FlowEmergencyService {

    /**
     * 指定时间区间内，已办权限丢失找回接口
     *
     * @param startDateTime
     * @param endDateTime
     * @return void
     **/
    public void addDonePermission(String startDateTime, String endDateTime);
}
