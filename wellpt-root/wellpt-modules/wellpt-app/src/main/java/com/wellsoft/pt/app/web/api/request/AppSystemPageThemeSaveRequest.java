package com.wellsoft.pt.app.web.api.request;

import com.wellsoft.pt.app.entity.AppSystemPageThemeEntity;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年03月02日   chenq	 Create
 * </pre>
 */
public class AppSystemPageThemeSaveRequest implements Serializable {

    private static final long serialVersionUID = -6313442289913735818L;

    private String system;

    private List<AppSystemPageThemeEntity> pageThemes;

    private String theme;

    private Boolean userThemeDefinable;

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public List<AppSystemPageThemeEntity> getPageThemes() {
        return pageThemes;
    }

    public void setPageThemes(List<AppSystemPageThemeEntity> pageThemes) {
        this.pageThemes = pageThemes;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public Boolean getUserThemeDefinable() {
        return userThemeDefinable;
    }

    public void setUserThemeDefinable(Boolean userThemeDefinable) {
        this.userThemeDefinable = userThemeDefinable;
    }
}
