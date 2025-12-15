package com.wellsoft.pt.app.web.api.request;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.app.entity.AppUserWidgetDefEntity;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年07月26日   chenq	 Create
 * </pre>
 */
public class DefWidgetQueryRequest implements Serializable {

    private String keyword;

    private PagingInfo page;

    private AppUserWidgetDefEntity.Type type;

    private List<String> appId;

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

    public AppUserWidgetDefEntity.Type getType() {
        return type;
    }

    public void setType(AppUserWidgetDefEntity.Type type) {
        this.type = type;
    }

    public List<String> getAppId() {
        return appId;
    }

    public void setAppId(List<String> appId) {
        this.appId = appId;
    }
}
