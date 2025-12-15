/*
 * @(#)2019年4月10日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.suport;


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
public interface IdentifierGenerator {

    IexportDataIdentifier generate(IexportDataRecord iexportDataRecord);

}
