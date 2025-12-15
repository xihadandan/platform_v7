package com.wellsoft.pt.basicdata.params.core.precompressor.impl;

import com.wellsoft.pt.basicdata.params.core.precompressor.Precompressor;
import com.wellsoft.pt.basicdata.params.core.precompressor.PrecompressorCenter;
import com.wellsoft.pt.basicdata.params.entity.SysParamItem;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * Description: 填充Jvm中的参数
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
public class JvmPrecompressor implements Precompressor {

    @Override
    public List<SysParamItem> pack() {
        List<SysParamItem> list = new LinkedList<SysParamItem>();
        Properties ps = System.getProperties();
        for (Map.Entry<Object, Object> entry : ps.entrySet()) {
            SysParamItem item = new SysParamItem();
            String key = String.valueOf(entry.getKey());
            item.setCode(key);
            item.setType(key);
            item.setName(key);
            item.setKey(String.valueOf(entry.getKey()));
            item.setValue(String.valueOf(entry.getValue()));
            item.setSourcetype(PrecompressorCenter.JVM);
            list.add(item);
        }
        return list;
    }
}
