package com.wellsoft.pt.app.dto;

import com.wellsoft.pt.app.entity.AppSystemInfoEntity;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年01月16日   chenq	 Create
 * </pre>
 */
public class AppSystemInfoDto extends AppSystemInfoEntity {
    private static final long serialVersionUID = 4096000895779639305L;

    private String layoutConf;
    private String themeStyle;
    private Boolean userLayoutDefinable;
    private Boolean userThemeDefinable;

    private AppProdVersionDto prodVersion;

    public String getLayoutConf() {
        return layoutConf;
    }

    public void setLayoutConf(String layoutConf) {
        this.layoutConf = layoutConf;
    }

    public String getThemeStyle() {
        return themeStyle;
    }

    public void setThemeStyle(String themeStyle) {
        this.themeStyle = themeStyle;
    }

    public Boolean getUserLayoutDefinable() {
        return userLayoutDefinable;
    }

    public void setUserLayoutDefinable(Boolean userLayoutDefinable) {
        this.userLayoutDefinable = userLayoutDefinable;
    }

    public Boolean getUserThemeDefinable() {
        return userThemeDefinable;
    }

    public void setUserThemeDefinable(Boolean userThemeDefinable) {
        this.userThemeDefinable = userThemeDefinable;
    }

    public AppProdVersionDto getProdVersion() {
        return prodVersion;
    }

    public void setProdVersion(AppProdVersionDto prodVersion) {
        this.prodVersion = prodVersion;
    }
}
