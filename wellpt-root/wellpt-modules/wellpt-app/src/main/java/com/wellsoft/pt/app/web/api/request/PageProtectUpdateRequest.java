package com.wellsoft.pt.app.web.api.request;

import com.wellsoft.pt.app.dto.AppPageResourceDto;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年07月12日   chenq	 Create
 * </pre>
 */
public class PageProtectUpdateRequest implements Serializable {
    private String pageDefinitionUuid;
    private List<AppPageResourceDto> resourceDtos;
    private Boolean isProtected;

    public String getPageDefinitionUuid() {
        return pageDefinitionUuid;
    }

    public void setPageDefinitionUuid(String pageDefinitionUuid) {
        this.pageDefinitionUuid = pageDefinitionUuid;
    }

    public List<AppPageResourceDto> getResourceDtos() {
        return resourceDtos;
    }

    public void setResourceDtos(List<AppPageResourceDto> resourceDtos) {
        this.resourceDtos = resourceDtos;
    }

    public Boolean getIsProtected() {
        return isProtected;
    }

    public void setIsProtected(Boolean aProtected) {
        isProtected = aProtected;
    }
}
