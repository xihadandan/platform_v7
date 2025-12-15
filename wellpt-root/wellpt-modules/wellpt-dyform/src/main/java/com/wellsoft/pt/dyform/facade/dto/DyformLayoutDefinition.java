package com.wellsoft.pt.dyform.facade.dto;

/**
 * Description: 单据布局
 *
 * @author hongjz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月28日.1	hongjz		2018年3月28日		Create
 * </pre>
 * @date 2018年3月28日
 */
public interface DyformLayoutDefinition {

    public String getTitle();

    public void setTitle(String title);

    public String getCode();

    public void setCode(String code);

    /**
     * 获取显示或者隐藏
     *
     * @return 0:隐藏， 1:显示
     */
    public int getShowTye();

    /**
     * 设置显示或者隐藏
     *
     * @param showTye 0:隐藏， 1:显示
     */
    public void setShowTye(int showTye);
}
