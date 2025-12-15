package com.wellsoft.pt.workflow.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * Description: 分页信息
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/9/1.1	    zenghw		2021/9/1		    Create
 * </pre>
 * @date 2021/9/1
 */
@ApiModel("分页信息")
public class PagingInfoDto implements Serializable {
    private static final long serialVersionUID = 1916174216512069712L;
    @ApiModelProperty("总条数")
    protected long totalCount = -1;
    @ApiModelProperty("当前页码")
    private int currentPage;
    @ApiModelProperty("每页大小")
    private int pageSize;
    @ApiModelProperty("总页数")
    private long totalPages;

    public int getCurrentPage() {
        return this.currentPage;
    }

    public void setCurrentPage(final int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalCount() {
        return this.totalCount;
    }

    public void setTotalCount(final long totalCount) {
        this.totalCount = totalCount;
    }

    public long getTotalPages() {
        return this.totalPages;
    }

    public void setTotalPages(final long totalPages) {
        this.totalPages = totalPages;
    }
}
