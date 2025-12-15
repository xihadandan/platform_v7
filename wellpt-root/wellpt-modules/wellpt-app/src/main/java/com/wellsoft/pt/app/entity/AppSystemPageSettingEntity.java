package com.wellsoft.pt.app.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

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
@Entity
@Table(name = "APP_SYSTEM_PAGE_SETTING")
@DynamicUpdate
@DynamicInsert
public class AppSystemPageSettingEntity extends SysEntity {
    private String layoutConf;
    private String themeStyle;
    private Boolean userThemeDefinable;
    private Boolean userLayoutDefinable;

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

    public Boolean getUserThemeDefinable() {
        return userThemeDefinable;
    }

    public void setUserThemeDefinable(Boolean userThemeDefinable) {
        this.userThemeDefinable = userThemeDefinable;
    }

    public Boolean getUserLayoutDefinable() {
        return userLayoutDefinable;
    }

    public void setUserLayoutDefinable(Boolean userLayoutDefinable) {
        this.userLayoutDefinable = userLayoutDefinable;
    }
}
