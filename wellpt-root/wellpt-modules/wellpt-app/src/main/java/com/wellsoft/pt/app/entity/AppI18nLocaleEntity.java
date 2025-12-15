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
 * 2025年01月14日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "APP_I18N_LOCALE")
@DynamicUpdate
@DynamicInsert
public class AppI18nLocaleEntity extends com.wellsoft.context.jdbc.entity.Entity {


    private String name;
    private String locale;
    private Integer seq;
    private String remark;
    private String translateCode;
    private String sortLetters;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public String getTranslateCode() {
        return translateCode;
    }

    public void setTranslateCode(String translateCode) {
        this.translateCode = translateCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }
}
