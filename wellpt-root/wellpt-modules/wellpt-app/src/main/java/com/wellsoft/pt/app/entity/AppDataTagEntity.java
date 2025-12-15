package com.wellsoft.pt.app.entity;

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
 * 2023年07月28日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "APP_DATA_TAG")
@DynamicUpdate
@DynamicInsert
public class AppDataTagEntity extends com.wellsoft.context.jdbc.entity.Entity {


    private Long tagUuid;
    private String dataId;
    private Integer seq;

    public AppDataTagEntity() {
    }

    public AppDataTagEntity(Long tagUuid, String dataId, Integer seq) {
        this.tagUuid = tagUuid;
        this.dataId = dataId;
        this.seq = seq;
    }

    public Long getTagUuid() {
        return tagUuid;
    }

    public void setTagUuid(Long tagUuid) {
        this.tagUuid = tagUuid;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }
}
