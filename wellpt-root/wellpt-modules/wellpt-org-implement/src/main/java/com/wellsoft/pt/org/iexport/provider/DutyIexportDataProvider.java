/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.iexport.provider;

import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.iexport.suport.TableMetaData;
import com.wellsoft.pt.multi.org.entity.MultiOrgDuty;
import com.wellsoft.pt.org.iexport.acceptor.DutyIexportData;

/**
 * Description: 职务表接口
 *
 * @author linz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-16.1	linz		2015-6-16		Create
 * </pre>
 * @date 2015-6-16
 */
//@Service
//@Transactional(readOnly = true)
public class DutyIexportDataProvider extends AbstractIexportDataProvider<MultiOrgDuty, String> {
    static {
        TableMetaData.register(IexportType.Duty, "职务", MultiOrgDuty.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.Duty;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getData(java.lang.String)
     */
    @Override
    public IexportData getData(String uuid) {
        MultiOrgDuty duty = this.dao.get(MultiOrgDuty.class, uuid);
        if (duty == null) {
            return new ErrorDataIexportData(IexportType.Duty, "找不到对应的职务依赖关系,可能已经被删除", "职务", uuid);
        }
        return new DutyIexportData(duty);
    }


    @Override
    public String getTreeName(MultiOrgDuty multiOrgDuty) {
        return new DutyIexportData(multiOrgDuty).getName();
    }

}
