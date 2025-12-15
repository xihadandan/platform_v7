package com.wellsoft.pt.app.web.api.request;

import com.wellsoft.context.jdbc.support.PagingInfo;

import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年09月11日   chenq	 Create
 * </pre>
 */
public class AppVersionParamKeywordQuery implements Serializable {
    private static final long serialVersionUID = 1690763285299919735L;

    private String keyword;

    private PagingInfo page;

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
}
