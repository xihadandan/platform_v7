package com.wellsoft.pt.dyform.facade.dto;

/**
 * Description: 区块
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
public class DyformBlock {
    private String blockCode;
    private String blockTitle;
    private boolean hide;
    private String id;

    /**
     * 获取区块编号
     *
     * @return
     */
    public String getBlockCode() {
        return blockCode;
    }

    /**
     * 设置区块编号
     *
     * @param blockCode
     */
    public void setBlockCode(String blockCode) {
        this.blockCode = blockCode;
    }

    /**
     * 获取区块标题
     *
     * @return
     */
    public String getBlockTitle() {
        return blockTitle;
    }

    /**
     * 设置区块标题
     *
     * @param blockTitle
     */
    public void setBlockTitle(String blockTitle) {
        this.blockTitle = blockTitle;
    }

    /**
     * 获取是否隐藏
     *
     * @return
     */
    public boolean isHide() {
        return hide;
    }

    /**
     * 设置是否隐藏
     *
     * @param hide
     */
    public void setHide(boolean hide) {
        this.hide = hide;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }
}
