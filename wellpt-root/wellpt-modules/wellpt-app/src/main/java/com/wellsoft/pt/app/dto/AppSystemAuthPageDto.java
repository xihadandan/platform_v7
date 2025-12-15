package com.wellsoft.pt.app.dto;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wellsoft.pt.app.entity.AppSystemPageSettingEntity;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年03月04日   chenq	 Create
 * </pre>
 */
public class AppSystemAuthPageDto implements Serializable {

    private String theme;
    private String definitionJson;
    private String title;
    private String pageName;
    private String pageId;
    private String pageUuid;
    private String system;
    private AppSystemPageSettingEntity pageSetting;
    private List<AppSystemAuthPageDto> allAuthPages = Lists.newArrayList();
    private Set<String> effectiveRoles = Sets.newHashSet(); // 使当前页面授权生效的角色（非用户的所有角色）

    /**
     * 页面组件功能资源未授权集合
     * 页面组件功能资源权限只有被设置为保护的情况下（默认是不保护的），才需要判断是否有权限，所以通过未授权的逆向方式能够判断是否开启保护以及是否有权限
     */
    private List<String> unauthorizedResource = Lists.newArrayList();

    public AppSystemAuthPageDto() {
    }


    public AppSystemAuthPageDto(String pageId, String pageUuid, String pageName) {
        this.pageId = pageId;
        this.pageUuid = pageUuid;
        this.pageName = pageName;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getDefinitionJson() {
        return definitionJson;
    }

    public void setDefinitionJson(String definitionJson) {
        this.definitionJson = definitionJson;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getPageUuid() {
        return pageUuid;
    }

    public void setPageUuid(String pageUuid) {
        this.pageUuid = pageUuid;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public AppSystemPageSettingEntity getPageSetting() {
        return pageSetting;
    }

    public void setPageSetting(AppSystemPageSettingEntity pageSetting) {
        this.pageSetting = pageSetting;
    }

    public List<AppSystemAuthPageDto> getAllAuthPages() {
        return allAuthPages;
    }

    public void setAllAuthPages(List<AppSystemAuthPageDto> allAuthPages) {
        this.allAuthPages = allAuthPages;
    }

    public List<String> getUnauthorizedResource() {
        return unauthorizedResource;
    }

    public void setUnauthorizedResource(List<String> unauthorizedResource) {
        this.unauthorizedResource = unauthorizedResource;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public Set<String> getEffectiveRoles() {
        return effectiveRoles;
    }

    public void setEffectiveRoles(Set<String> effectiveRoles) {
        this.effectiveRoles = effectiveRoles;
    }
}
