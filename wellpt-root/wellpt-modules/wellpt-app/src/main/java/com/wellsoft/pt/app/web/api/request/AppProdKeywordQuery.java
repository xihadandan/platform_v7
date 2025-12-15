package com.wellsoft.pt.app.web.api.request;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.app.entity.AppProduct;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年07月31日   chenq	 Create
 * </pre>
 */
public class AppProdKeywordQuery implements Serializable {

    private String keyword;

    private List<AppProduct.Status> status;

    private Long categoryUuid;

    private List<String> sids;

    private PagingInfo page;

    private List<String> excludeIds;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public PagingInfo getPage() {
        return page;
    }

    public void setPage(PagingInfo page) {
        this.page = page;
    }

    public List<AppProduct.Status> getStatus() {
        return status;
    }

    public void setStatus(List<AppProduct.Status> status) {
        this.status = status;
    }

    public List<String> getSids() {
        return sids;
    }

    public void setSids(List<String> sids) {
        this.sids = sids;
    }

    public Long getCategoryUuid() {
        return categoryUuid;
    }

    public void setCategoryUuid(Long categoryUuid) {
        this.categoryUuid = categoryUuid;
    }

    public List<String> getExcludeIds() {
        return excludeIds;
    }

    public void setExcludeIds(List<String> excludeIds) {
        this.excludeIds = excludeIds;
    }
}