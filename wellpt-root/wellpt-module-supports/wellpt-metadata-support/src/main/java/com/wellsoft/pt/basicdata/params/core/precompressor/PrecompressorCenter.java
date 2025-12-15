package com.wellsoft.pt.basicdata.params.core.precompressor;

import com.wellsoft.pt.basicdata.params.core.precompressor.impl.JvmPrecompressor;
import com.wellsoft.pt.basicdata.params.core.precompressor.impl.PropPrecompressor;
import com.wellsoft.pt.basicdata.params.entity.SysParamItem;

import java.util.LinkedList;
import java.util.List;

/**
 * Description: 根据ParamItem的sourceType类型选择具体的填充对象
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
public class PrecompressorCenter {
    public static final int DB = 0;
    public static final int JVM = 1;
    public static final int PROP = 2;
    private static Precompressor[] precompressors = {new JvmPrecompressor(), new PropPrecompressor()};

    public static List<SysParamItem> pack() {
        LinkedList<SysParamItem> list = new LinkedList<SysParamItem>();
        for (Precompressor p : precompressors) {
            list.addAll(p.pack());
        }
        return list;
    }
}
