package com.wellsoft.context.component.select2;

/**
 * Description: select2查询接口
 *
 * @author Xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年2月1日.1	Xiem		2016年2月1日		Create
 * </pre>
 * @date 2016年2月1日
 */
public interface Select2QueryApi {

    /**
     * select2查询接口
     *
     * @param queryInfo
     * @return
     */
    public Select2QueryData loadSelectData(Select2QueryInfo queryInfo);

    /**
     * 通过ID查找Text.对于远程查找分页需要实现，否则无法设置选中。
     *
     * @param queryInfo
     * @param id
     * @return
     */
    public Select2QueryData loadSelectDataByIds(Select2QueryInfo queryInfo);

}
