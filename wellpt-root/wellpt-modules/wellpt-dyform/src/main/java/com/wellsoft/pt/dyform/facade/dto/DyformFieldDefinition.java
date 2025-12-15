package com.wellsoft.pt.dyform.facade.dto;

import org.json.JSONObject;

/**
 * Description: 字段定义门面
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
public interface DyformFieldDefinition {

    /**
     * 获取显示名称
     *
     * @return
     */
    String getDisplayName();

    /**
     * 获取字段名
     *
     * @return
     */
    String getName();

    /**
     * 获取控件类型
     *
     * @return
     */
    String getInputMode();

    /**
     * 获取字段长度
     *
     * @return
     */
    String getLength();

    /**
     * 设置字段长度
     *
     * @param string
     */
    void setLength(String string);

    /**
     * 获取字段名
     *
     * @return
     */
    String getFieldName();

    /**
     * 获取字段定义
     *
     * @return
     */
    JSONObject getFieldDefinitionJson();

}
