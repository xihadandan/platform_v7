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
 * 2024年03月02日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "APP_SYSTEM_PAGE_THEME")
@DynamicUpdate
@DynamicInsert
public class AppSystemPageThemeEntity extends SysEntity {
    private String pageId;
    private String theme;

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
