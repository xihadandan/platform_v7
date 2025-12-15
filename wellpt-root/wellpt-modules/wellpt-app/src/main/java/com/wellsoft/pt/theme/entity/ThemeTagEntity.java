package com.wellsoft.pt.theme.entity;

import com.wellsoft.context.jdbc.entity.Entity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Table;

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
@Table(name = "THEME_TAG")
@DynamicInsert
@DynamicUpdate
@javax.persistence.Entity
public class ThemeTagEntity extends Entity {
    public String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
