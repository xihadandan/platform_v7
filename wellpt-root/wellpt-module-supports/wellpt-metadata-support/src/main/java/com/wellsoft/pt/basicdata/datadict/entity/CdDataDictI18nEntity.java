package com.wellsoft.pt.basicdata.datadict.entity;

import com.wellsoft.pt.common.i18n.entity.I18nEntity;
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
 * 2025年03月25日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "cd_data_dict_i18n")
@DynamicUpdate
@DynamicInsert
public class CdDataDictI18nEntity extends I18nEntity {

    public CdDataDictI18nEntity() {
    }

    public CdDataDictI18nEntity(Long dataUuid, String dataId, String dataCode, String locale, String content) {
        super(dataUuid, dataId, dataCode, locale, content);
    }
}
