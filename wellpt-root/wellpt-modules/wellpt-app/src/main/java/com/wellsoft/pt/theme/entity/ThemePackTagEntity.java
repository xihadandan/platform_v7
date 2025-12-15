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
 * 2023年06月19日   Qiong	 Create
 * </pre>
 */
@Table(name = "THEME_PACK_TAG")
@DynamicInsert
@DynamicUpdate
@javax.persistence.Entity
public class ThemePackTagEntity extends Entity {

    public ThemePackTagEntity() {
    }

    public ThemePackTagEntity(Long tagUuid, Long packUuid) {
        this.tagUuid = tagUuid;
        this.packUuid = packUuid;
    }

    private Long tagUuid;
    private Long packUuid;

    public Long getTagUuid() {
        return tagUuid;
    }

    public void setTagUuid(Long tagUuid) {
        this.tagUuid = tagUuid;
    }

    public Long getPackUuid() {
        return packUuid;
    }

    public void setPackUuid(Long packUuid) {
        this.packUuid = packUuid;
    }
}
