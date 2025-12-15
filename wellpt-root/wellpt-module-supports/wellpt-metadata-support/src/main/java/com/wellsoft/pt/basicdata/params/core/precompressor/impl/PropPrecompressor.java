package com.wellsoft.pt.basicdata.params.core.precompressor.impl;

import com.wellsoft.context.config.Config;
import com.wellsoft.pt.basicdata.params.core.precompressor.Precompressor;
import com.wellsoft.pt.basicdata.params.core.precompressor.PrecompressorCenter;
import com.wellsoft.pt.basicdata.params.entity.SysParamItem;

import java.util.LinkedList;
import java.util.List;

/**
 * Description: 填充配置文件中的参数
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
public class PropPrecompressor implements Precompressor {
    @Override
    public List<SysParamItem> pack() {
        List<SysParamItem> list = new LinkedList<SysParamItem>();

        for (String key : Config.keySet()) {
            SysParamItem item = new SysParamItem();
            item.setCode(key);
            item.setType(key);
            item.setName(key);
            item.setKey(key);
            item.setValue(String.valueOf(Config.getValue(key)));
            item.setSourcetype(PrecompressorCenter.PROP);
            list.add(item);
        }
        return list;
    }
}
