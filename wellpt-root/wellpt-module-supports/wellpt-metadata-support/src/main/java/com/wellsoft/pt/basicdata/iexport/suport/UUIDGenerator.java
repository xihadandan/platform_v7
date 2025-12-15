/*
 * @(#)2019年4月10日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.suport;

import com.wellsoft.context.enums.Separator;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年4月10日.1	zhulh		2019年4月10日		Create
 * </pre>
 * @date 2019年4月10日
 */
public class UUIDGenerator implements IdentifierGenerator {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.suport.IdentifierGenerator#generate(com.wellsoft.pt.basicdata.iexport.suport.IexportDataRecord)
     */
    @Override
    public IexportDataIdentifier generate(IexportDataRecord iexportDataRecord) {
        String pkName = iexportDataRecord.getPrimaryKeyName();
        StringBuilder pkSb = new StringBuilder();
        List<String> pkNames = Arrays.asList(StringUtils.split(pkName, Separator.SEMICOLON.getValue()));
        Iterator<String> it = pkNames.iterator();
        while (it.hasNext()) {
            it.next();
            pkSb.append(UUID.randomUUID().toString());
            if (it.hasNext()) {
                pkSb.append(Separator.SEMICOLON.getValue());
            }
        }
        IexportDataIdentifier identifier = new IexportDataIdentifier(pkName, pkSb.toString());
        return identifier;
    }

}
