package com.wellsoft.pt.theme.controller.request;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.theme.entity.ThemePackEntity;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年06月20日   chenq	 Create
 * </pre>
 */
public class ThemePackQueryRequest implements Serializable {
    private List<Long> tagUuids;
    private ThemePackEntity.Status status;
    private ThemePackEntity.Type type;
    private PagingInfo page;
    private String keyword;

    public List<Long> getTagUuids() {
        return tagUuids;
    }

    public void setTagUuids(List<Long> tagUuids) {
        this.tagUuids = tagUuids;
    }

    public ThemePackEntity.Status getStatus() {
        return status;
    }

    public void setStatus(ThemePackEntity.Status status) {
        this.status = status;
    }

    public ThemePackEntity.Type getType() {
        return type;
    }

    public void setType(ThemePackEntity.Type type) {
        this.type = type;
    }

    public PagingInfo getPage() {
        return page;
    }

    public void setPage(PagingInfo page) {
        this.page = page;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
