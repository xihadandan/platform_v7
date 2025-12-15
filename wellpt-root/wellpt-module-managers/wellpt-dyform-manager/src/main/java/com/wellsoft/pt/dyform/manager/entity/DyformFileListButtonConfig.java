package com.wellsoft.pt.dyform.manager.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * 列表式附件配置_附件按钮
 */
@Entity
@Table(name = "DYFORM_FILE_LIST_BUTTON_CONFIG")
@DynamicUpdate
@DynamicInsert
public class DyformFileListButtonConfig extends IdEntity {

    private static final long serialVersionUID = 7460312831684548785L;

    // 名称
    private String buttonName;

    // 编码
    private String code;

    // 内置按钮
    private String btnType;

    // 编辑类操作
    private String btnShowType;

    // 按钮库
    private String btnLib;

    // 事件管理
    private String eventManger;

    // 事件管理
    private String fileExtensions;

    // 默认选中
    private Integer defaultFlag;

    // 排序字段
    private Integer orderIndex;

    private List<AppDefElementI18nEntity> i18ns;

    public String getButtonName() {
        return buttonName;
    }

    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the btnType
     */
    public String getBtnType() {
        return btnType;
    }

    /**
     * @param btnType 要设置的btnType
     */
    public void setBtnType(String btnType) {
        this.btnType = btnType;
    }

    /**
     * @return the btnShowType
     */
    public String getBtnShowType() {
        return btnShowType;
    }

    /**
     * @param btnShowType 要设置的btnShowType
     */
    public void setBtnShowType(String btnShowType) {
        this.btnShowType = btnShowType;
    }

    public String getBtnLib() {
        return btnLib;
    }

    public void setBtnLib(String btnLib) {
        this.btnLib = btnLib;
    }

    public String getEventManger() {
        return eventManger;
    }

    public void setEventManger(String eventManger) {
        this.eventManger = eventManger;
    }

    public String getFileExtensions() {
        return fileExtensions;
    }

    public void setFileExtensions(String fileExtensions) {
        this.fileExtensions = fileExtensions;
    }

    public Integer getDefaultFlag() {
        return defaultFlag;
    }

    public void setDefaultFlag(Integer defaultFlag) {
        this.defaultFlag = defaultFlag;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    @Transient
    public List<AppDefElementI18nEntity> getI18ns() {
        return i18ns;
    }

    public void setI18ns(List<AppDefElementI18nEntity> i18ns) {
        this.i18ns = i18ns;
    }
}
