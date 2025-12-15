package com.wellsoft.pt.dyform.facade.dto;

/**
 * Description: 页签
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-3.1	hunt		2014-10-3		Create
 * </pre>
 * @date 2014-10-3
 */
public class DyformTab {
    private String displayName;
    private String name;
    private boolean isActive;
    private boolean hide;

    /**
     * 获取显示名称
     *
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 设置显示名称
     *
     * @param displayName 要设置的displayName
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * 获取tab名称
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * 设置tab名称
     *
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取是否启用
     *
     * @return the isActive
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * 设置是否启用
     *
     * @param isActive 要设置的isActive
     */
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * 获取是否隐藏
     *
     * @return the hide
     */
    public boolean isHide() {
        return hide;
    }

    /**
     * 设置是否隐藏
     *
     * @param hide 要设置的hide
     */
    public void setHide(boolean hide) {
        this.hide = hide;
    }

}
