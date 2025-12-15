package com.wellsoft.pt.dyform.facade.dto;

import java.util.List;

/**
 * Description: 从表定义接口
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
public interface DyformSubformFormDefinition {

    /**
     * 获取显示名称
     *
     * @return
     */
    String getDisplayName();

    /**
     * 获取表名
     *
     * @return
     */
    String getName();

    /**
     * 获取字段定义
     *
     * @return
     */
    List<DyformSubformFieldDefinition> getSubformFieldDefinitions();

    /**
     * 获取表单定义UUID
     *
     * @return
     */
    String getFormUuid();

    /**
     * 获取表单ID
     *
     * @return
     */
    String getOuterId();

    /**
     * 是否显示从表标题
     *
     * @return
     */
    Object getIsGroupShowTitle();

}
