package com.wellsoft.pt.cg.core.sniffer.impl;

import com.wellsoft.pt.cg.core.Context;
import com.wellsoft.pt.cg.core.sniffer.Sniffer;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
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
@Service
@Transactional
public abstract class AbstractSniffer extends BaseServiceImpl implements Sniffer {

    /**
     * 嗅探并填充到上下文
     *
     * @param context
     */
    @Override
    public void snifferAndImpact(Context context) {
        impact(context, sniffer(context));
    }
}
