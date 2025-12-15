package com.wellsoft.pt.handover.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
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
 * 2022/3/28.1	    zenghw		2022/3/28		    Create
 * </pre>
 * @date 2022/3/28
 */
@ApiModel(value = "工作交接")
public class GetWorkHandoverByUuidDto {
    // UUID
    @ApiModelProperty("uuid")
    protected String uuid;

    // 版本号
    @ApiModelProperty("数据版本号")
    private Integer recVer;

    // 创建人
    @ApiModelProperty("创建人")
    private String creator;

    // 创建时间
    @ApiModelProperty("创建时间")
    private String createTime;

    // 修改人
    @ApiModelProperty("修改人")
    private String modifier;

    // 修改时间
    @ApiModelProperty("修改时间")
    private Date modifyTime;

    // 附件属性
    @ApiModelProperty("附件属性")
    private String attach;

    @ApiModelProperty("系统单位Id")
    private String systemUnitId;

    // 交接人ID-单选
    @ApiModelProperty("交接人ID")
    private String handoverPersonId;
    // 任务实际开始执行的时间
    @ApiModelProperty("任务实际开始执行的时间")
    private String handoverWorkTime;
    // 是否通知接收人-NoticeHandoverPersonFlagEnum 0:不通知；1通知
    @ApiModelProperty("是否通知接收人")
    private Integer noticeHandoverPersonFlag;
    // 交接内容：流程定义内容显示值
    @ApiModelProperty("交接内容：流程定义内容显示值")
    private String handoverContentsName;
    // 交接内容：流程定义内容
    @ApiModelProperty("交接内容：流程定义内容")
    private String handoverContentsId;
    // 接收人ID-单选
    @ApiModelProperty("接收人ID")
    private String receiverId;
    // 交接人名称-单选
    @ApiModelProperty("交接人名称")
    private String handoverPersonName;
    // 工作交接状态-WorkHandoverStatusEnum 1未执行；2执行中；3已完成；
    @ApiModelProperty("工作交接状态")
    private Integer workHandoverStatus;
    // 接收人名称-单选
    @ApiModelProperty("接收人名称")
    private String receiverName;
    // 交接执行时间-HandoverworktimesettingEnum 1系统空闲时执行 ;2立即执行
    @ApiModelProperty("交接执行时间")
    private Integer handoverWorkTimeSetting;
    // 工作类型-HandoverWorkTypeEnum 流程： flow;
    @ApiModelProperty("工作类型")
    private String handoverWorkType;

    @ApiModelProperty("工作类型显示值")
    private String handoverWorkTypeName;

    @ApiModelProperty("工作类型对应的交接内容集合")
    private List<WhWorkTypeToHandoverCountItemDto> whWorkTypeToHandoverCountItemDtoList;
    @ApiModelProperty("工作类型对应的交接内容+统计数据")
    private List<WhWorkTypeToHandoverCountDto> workTypeToHandoverCountDtoList;

    @ApiModelProperty("操作人姓名")
    private String modifierName;

    public String getModifierName() {
        return this.modifierName;
    }

    public void setModifierName(final String modifierName) {
        this.modifierName = modifierName;
    }

    public List<WhWorkTypeToHandoverCountItemDto> getWhWorkTypeToHandoverCountItemDtoList() {
        return this.whWorkTypeToHandoverCountItemDtoList;
    }

    public void setWhWorkTypeToHandoverCountItemDtoList(
            final List<WhWorkTypeToHandoverCountItemDto> whWorkTypeToHandoverCountItemDtoList) {
        this.whWorkTypeToHandoverCountItemDtoList = whWorkTypeToHandoverCountItemDtoList;
    }

    public List<WhWorkTypeToHandoverCountDto> getWorkTypeToHandoverCountDtoList() {
        return this.workTypeToHandoverCountDtoList;
    }

    public void setWorkTypeToHandoverCountDtoList(
            final List<WhWorkTypeToHandoverCountDto> workTypeToHandoverCountDtoList) {
        this.workTypeToHandoverCountDtoList = workTypeToHandoverCountDtoList;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public Integer getRecVer() {
        return this.recVer;
    }

    public void setRecVer(final Integer recVer) {
        this.recVer = recVer;
    }

    public String getCreator() {
        return this.creator;
    }

    public void setCreator(final String creator) {
        this.creator = creator;
    }

    public String getModifier() {
        return this.modifier;
    }

    public void setModifier(final String modifier) {
        this.modifier = modifier;
    }

    public Date getModifyTime() {
        return this.modifyTime;
    }

    public void setModifyTime(final Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getAttach() {
        return this.attach;
    }

    public void setAttach(final String attach) {
        this.attach = attach;
    }

    public String getSystemUnitId() {
        return this.systemUnitId;
    }

    public void setSystemUnitId(final String systemUnitId) {
        this.systemUnitId = systemUnitId;
    }

    public String getHandoverPersonId() {
        return this.handoverPersonId;
    }

    public void setHandoverPersonId(final String handoverPersonId) {
        this.handoverPersonId = handoverPersonId;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(final String createTime) {
        this.createTime = createTime;
    }

    public String getHandoverWorkTime() {
        return this.handoverWorkTime;
    }

    public void setHandoverWorkTime(final String handoverWorkTime) {
        this.handoverWorkTime = handoverWorkTime;
    }

    public Integer getNoticeHandoverPersonFlag() {
        return this.noticeHandoverPersonFlag;
    }

    public void setNoticeHandoverPersonFlag(final Integer noticeHandoverPersonFlag) {
        this.noticeHandoverPersonFlag = noticeHandoverPersonFlag;
    }

    public String getHandoverContentsName() {
        return this.handoverContentsName;
    }

    public void setHandoverContentsName(final String handoverContentsName) {
        this.handoverContentsName = handoverContentsName;
    }

    public String getHandoverContentsId() {
        return this.handoverContentsId;
    }

    public void setHandoverContentsId(final String handoverContentsId) {
        this.handoverContentsId = handoverContentsId;
    }

    public String getReceiverId() {
        return this.receiverId;
    }

    public void setReceiverId(final String receiverId) {
        this.receiverId = receiverId;
    }

    public String getHandoverPersonName() {
        return this.handoverPersonName;
    }

    public void setHandoverPersonName(final String handoverPersonName) {
        this.handoverPersonName = handoverPersonName;
    }

    public Integer getWorkHandoverStatus() {
        return this.workHandoverStatus;
    }

    public void setWorkHandoverStatus(final Integer workHandoverStatus) {
        this.workHandoverStatus = workHandoverStatus;
    }

    public String getReceiverName() {
        return this.receiverName;
    }

    public void setReceiverName(final String receiverName) {
        this.receiverName = receiverName;
    }

    public Integer getHandoverWorkTimeSetting() {
        return this.handoverWorkTimeSetting;
    }

    public void setHandoverWorkTimeSetting(final Integer handoverWorkTimeSetting) {
        this.handoverWorkTimeSetting = handoverWorkTimeSetting;
    }

    public String getHandoverWorkType() {
        return this.handoverWorkType;
    }

    public void setHandoverWorkType(final String handoverWorkType) {
        this.handoverWorkType = handoverWorkType;
    }

    public String getHandoverWorkTypeName() {
        return this.handoverWorkTypeName;
    }

    public void setHandoverWorkTypeName(final String handoverWorkTypeName) {
        this.handoverWorkTypeName = handoverWorkTypeName;
    }
}
