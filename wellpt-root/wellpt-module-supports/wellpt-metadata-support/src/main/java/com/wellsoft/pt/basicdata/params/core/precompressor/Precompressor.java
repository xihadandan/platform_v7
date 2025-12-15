package com.wellsoft.pt.basicdata.params.core.precompressor;

import com.wellsoft.pt.basicdata.params.entity.SysParamItem;

import java.util.List;

/**
 * Description: 根据ParamItem的sourceType类型
 *
 * @author Lmw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-07-20.1	Lmw		2015-07-20		Create
 * </pre>
 * @date 2015-07-20
 */
public interface Precompressor {
    /**
     * 填充数据
     *
     * @param paramItem
     */
    List<SysParamItem> pack();
}
