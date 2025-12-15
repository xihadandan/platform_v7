package com.wellsoft.pt.cg.core.sniffer;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.cg.core.Type;
import com.wellsoft.pt.cg.core.sniffer.impl.DyformSniffer;
import com.wellsoft.pt.cg.core.sniffer.impl.EntitySniffer;
import com.wellsoft.pt.cg.core.sniffer.impl.FlowSniffer;
import com.wellsoft.pt.cg.core.sniffer.impl.TableSniffer;

import java.util.HashMap;
import java.util.Map;

/**
 * 嗅探器工厂
 *
 * @author lmw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-7-8.1	lmw		2015-7-8		Create
 * </pre>
 * @date 2015-7-8
 */
public class SnifferFactory {
    private static Map<String, Sniffer> sniffers = new HashMap<String, Sniffer>();

    /**
     * 获取缓存中的生成器
     *
     * @param type
     * @return
     */
    public static Sniffer intendSniffer(int type) {
        return intendSniffer(type, false);
    }

    /**
     * 准备代码生成器
     *
     * @param type
     * @param isNew 是否是新的生成器
     * @return
     */
    public static Sniffer intendSniffer(int type, boolean isNew) {
        Sniffer sniffer = sniffers.get(type);

        if (isNew)
            return newSniffer(type);

        if (sniffer != null)
            return sniffer;

        switch (type) {
            case Type.GENTYPE_ENTITY:
                sniffer = new EntitySniffer();
                break;
            case Type.GENTYPE_FLOW_DEFINITION:
                sniffer = new FlowSniffer();
                break;
            case Type.GENTYPE_TABLE:
                sniffer = ApplicationContextHolder.getBean(TableSniffer.class);
                break;
            case Type.GENTYPE_DYFORM_DEFINITION:
                sniffer = new DyformSniffer();
                break;
            default:
                throw new RuntimeException("getSniffer error: the  " + type + " is not support");
        }
        sniffers.put(String.valueOf(type), sniffer);
        return sniffer;
    }

    /**
     * 生成新的生成器
     *
     * @param type
     * @return
     */
    private static Sniffer newSniffer(int type) {
        switch (type) {
            case Type.GENTYPE_ENTITY:
                return new EntitySniffer();
            case Type.GENTYPE_FLOW_DEFINITION:
                return new FlowSniffer();
            case Type.GENTYPE_TABLE:
                return new TableSniffer();
            default:
                throw new RuntimeException("getSniffer error: the  " + type + " is not support");
        }
    }
}
