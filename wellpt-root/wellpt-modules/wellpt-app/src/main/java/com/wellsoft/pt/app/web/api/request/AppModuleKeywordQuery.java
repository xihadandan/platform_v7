package com.wellsoft.pt.app.web.api.request;

import com.wellsoft.context.jdbc.support.PagingInfo;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年07月05日   chenq	 Create
 * </pre>
 */
public class AppModuleKeywordQuery implements Serializable {

    private String keyword;

    private List<Long> tagUuids;

    private Long categoryUuid;

    private PagingInfo page;

    private String orderBy;

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

    public List<Long> getTagUuids() {
        return tagUuids;
    }

    public void setTagUuids(List<Long> tagUuids) {
        this.tagUuids = tagUuids;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public Long getCategoryUuid() {
        return categoryUuid;
    }

    public void setCategoryUuid(Long categoryUuid) {
        this.categoryUuid = categoryUuid;
    }
}
