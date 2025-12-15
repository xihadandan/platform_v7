package com.wellsoft.pt.fulltext.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 索引扩展字典表
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年09月09日   chenq	 Create
 * </pre>
 */

@Entity
@Table(name = "INDEX_EXT_DIC")
@DynamicUpdate
@DynamicInsert
@Deprecated
public class IndexExtDicEntity extends IdEntity {

    private String dict;
    private String stopwords;

    public String getDict() {
        return dict;
    }

    public void setDict(String dict) {
        this.dict = dict;
    }

    public String getStopwords() {
        return stopwords;
    }

    public void setStopwords(String stopwords) {
        this.stopwords = stopwords;
    }
}
