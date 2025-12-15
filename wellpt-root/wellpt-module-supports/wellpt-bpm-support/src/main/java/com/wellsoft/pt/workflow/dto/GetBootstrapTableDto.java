package com.wellsoft.pt.workflow.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Description: 表格组件列表输出对象
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/8/21.1	    zenghw		2021/8/21		    Create
 * </pre>
 * @date 2021/8/21
 */
@ApiModel("表格组件列表输出对象")
public class GetBootstrapTableDto implements Serializable {

    private static final long serialVersionUID = 7719630605874776241L;


    @ApiModelProperty("数据排序")
    private List<BootstrapTableOrderDto> dataOrders;

    private PagingInfoDto pagingInfo;

    @ApiModelProperty("表格列")
    private List<TableColumnDto> tableColumns;

    @ApiModelProperty("表格数据")
    private List<Map<String, Object>> datas;

    public List<BootstrapTableOrderDto> getDataOrders() {
        return this.dataOrders;
    }

    public void setDataOrders(final List<BootstrapTableOrderDto> dataOrders) {
        this.dataOrders = dataOrders;
    }

    public PagingInfoDto getPagingInfo() {
        return this.pagingInfo;
    }

    public void setPagingInfo(final PagingInfoDto pagingInfo) {
        this.pagingInfo = pagingInfo;
    }

    public List<TableColumnDto> getTableColumns() {
        return this.tableColumns;
    }

    public void setTableColumns(final List<TableColumnDto> tableColumns) {
        this.tableColumns = tableColumns;
    }

    public List<Map<String, Object>> getDatas() {
        return this.datas;
    }

    public void setDatas(final List<Map<String, Object>> datas) {
        this.datas = datas;
    }
}
