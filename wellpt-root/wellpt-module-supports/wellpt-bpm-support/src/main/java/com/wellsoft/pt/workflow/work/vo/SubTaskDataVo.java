package com.wellsoft.pt.workflow.work.vo;

import com.wellsoft.pt.basicdata.datastore.bean.DataStoreOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Description:
 * 子流程请求数据
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2022/1/4   Create
 * </pre>
 */
@ApiModel("子查询vo")
public class SubTaskDataVo {

    @ApiModelProperty("关键字")
    private String keyword;

    @ApiModelProperty("页码")
    private Integer pageNum = 1;

    @ApiModelProperty("一页条数")
    private Integer pageSize = 10;

    @ApiModelProperty("流程任务实例uuid")
    private String taskInstUuid;

    @ApiModelProperty("流程实例uuid")
    private String flowInstUuid;

    @ApiModelProperty("所属实例uuid")
    private String belongToTaskInstUuid;

    @ApiModelProperty("父流程实例uuid")
    private String parentFlowInstUuid;

    @ApiModelProperty("子流程实例uuid列表")
    private List<String> subFlowInstUuids;

    @ApiModelProperty("排序信息")
    private List<DataStoreOrder> orders;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getPageNum() {
        return pageNum == null ? 1 : pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize == null ? 10 : pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getTaskInstUuid() {
        return taskInstUuid;
    }

    public void setTaskInstUuid(String taskInstUuid) {
        this.taskInstUuid = taskInstUuid;
    }

    public String getFlowInstUuid() {
        return flowInstUuid;
    }

    public void setFlowInstUuid(String flowInstUuid) {
        this.flowInstUuid = flowInstUuid;
    }

    public String getParentFlowInstUuid() {
        return parentFlowInstUuid;
    }

    public void setParentFlowInstUuid(String parentFlowInstUuid) {
        this.parentFlowInstUuid = parentFlowInstUuid;
    }

    public List<String> getSubFlowInstUuids() {
        return subFlowInstUuids;
    }

    public void setSubFlowInstUuids(List<String> subFlowInstUuids) {
        this.subFlowInstUuids = subFlowInstUuids;
    }

    public String getBelongToTaskInstUuid() {
        return belongToTaskInstUuid;
    }

    public void setBelongToTaskInstUuid(String belongToTaskInstUuid) {
        this.belongToTaskInstUuid = belongToTaskInstUuid;
    }

    /**
     * @return the orders
     */
    public List<DataStoreOrder> getOrders() {
        return orders;
    }

    /**
     * @param orders 要设置的orders
     */
    public void setOrders(List<DataStoreOrder> orders) {
        this.orders = orders;
    }
}
