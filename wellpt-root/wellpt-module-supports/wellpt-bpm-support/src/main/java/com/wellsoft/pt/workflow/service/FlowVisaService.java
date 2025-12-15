package com.wellsoft.pt.workflow.service;

import com.wellsoft.pt.workflow.dto.*;

import java.io.BufferedInputStream;
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
 * 2021/8/25.1	    zenghw		2021/8/25		    Create
 * </pre>
 * @date 2021/8/25
 */
public interface FlowVisaService {

    /**
     * 返回指定条数的最近使用意见
     *
     * @param userId       当前用户ID
     * @param flowInstUuid 流程实例UUID
     * @param num          返回的条数
     * @return java.util.List<com.wellsoft.pt.workflow.dto.FlowOpinionDto>
     **/
    public List<FlowOpinionDto> getUserRecentOpinions(String userId, String flowInstUuid, Integer num);

    /**
     * 返回当前用户的常用意见
     *
     * @param userId 当前用户ID
     * @param num    返回的条数
     * @return java.util.List<com.wellsoft.pt.workflow.dto.FlowOpinionDto>
     **/
    public List<FlowOpinionDto> getUserCommonOpinions(String userId, Integer num);

    /**
     * 返回指定环节的环节意见立场
     *
     * @param flowInstUuid 流程实例UUID
     * @param taskId       指定环节ID
     * @return java.util.List<com.wellsoft.pt.workflow.dto.UnitElementDto>
     **/
    public List<UnitElementDto> getTaskOpinion(String flowInstUuid, String taskId);

    /**
     * 手写签批附件列表
     *
     * @param flowInstUuid 流程实例UUID
     * @param fieldCodes   附件字段编码集合
     * @return java.util.List<com.wellsoft.pt.workflow.dto.GetInspectionFileListDto>
     **/
    public List<GetInspectionFileListDto> getInspectionFileList(String flowInstUuid, String[] fieldCodes);

    /**
     * 获取手写签批记录接口
     *
     * @param flowInstUuid 流程实例uuid
     * @return java.util.List<com.wellsoft.pt.workflow.dto.GetInspectionLogsDto>
     **/
    public String getInspectionLogs(String flowInstUuid);

    /**
     * 表单指定字段获取总附件数量
     *
     * @param flowInstUuid 流程实例UUID
     * @param fieldCodes   附件字段编码集合
     * @return java.lang.Integer
     **/
    public Integer getFileNumsByFormFields(String flowInstUuid, String[] fieldCodes);

    /**
     * 更新手写签批附件
     *
     * @param inspectionFile 新的手写签批附件
     * @return java.lang.Boolean
     **/
    public String updateInspectionFile(String fileUuid, byte[] inspectionFile);

    /**
     * 更新手写签批附件
     *
     * @param flowInstUuid 流程实例UUID
     * @param fileUuid     附件uuid
     * @param inputStream  转化后的手写签批附件
     * @return java.lang.Boolean
     **/
    public Boolean updateInspectionFile(String flowInstUuid, String fileUuid, BufferedInputStream inputStream);

    /**
     * 表格组件数据量查询接口
     *
     * @param bootstrapTableUuid 表格组件UUID
     * @param bootstrapTableKey  表格组件 关键字查询
     * @param pageNum            分页：第几页
     * @param pageSize           分页：每页条数
     * @return
     **/
    public GetBootstrapTableDto getBootstrapTableList(String bootstrapTableUuid, String bootstrapTableKey, Integer pageNum, Integer pageSize);

    /**
     * 表格组件数量查询接口
     *
     * @param bootstrapTableUuid 表格组件UUID
     * @return
     **/
    public Integer getBootstrapTableListAllNum(String bootstrapTableUuid);

    /**
     * 提交手写签批流程
     *
     * @return java.lang.Boolean
     **/
    public Boolean submitFlow(SubmitFlowDto submitFlowDto);

    /**
     * 保存手写签批流程
     * 保存附件
     * 保存签署意见和意见立场
     *
     * @return java.lang.Boolean
     **/
    public Boolean saveFlow(SubmitFlowDto submitFlowDto);

    /**
     * 退回流程
     *
     * @param rollbackFlowDto
     * @return java.lang.Boolean
     **/
    public Boolean rollbackFlow(RollbackFlowDto rollbackFlowDto);

    /**
     * 获取签署意见和意见立场
     *
     * @param flowInstUuid
     * @param userId
     * @return com.wellsoft.pt.workflow.dto.GetFlowInstanceDetailDto
     **/
    public GetSignOpinionAndOpinionPositionDto getSignOpinionAndOpinionPosition(String flowInstUuid, String userId);
}
