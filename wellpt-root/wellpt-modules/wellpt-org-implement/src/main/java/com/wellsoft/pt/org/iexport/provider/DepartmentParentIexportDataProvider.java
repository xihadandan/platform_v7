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
import com.wellsoft.pt.org.entity.Department;
import com.wellsoft.pt.org.iexport.acceptor.DepartmentParentIexportData;

/**
 * Description: 部门接口
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
public class DepartmentParentIexportDataProvider extends AbstractIexportDataProvider<Department, String> {
    static {
        TableMetaData.register(IexportType.DepartmentParent, "部门", Department.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.DepartmentParent;
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
        Department department = this.dao.get(Department.class, uuid);
        if (department == null) {
            return new ErrorDataIexportData(IexportType.Department, "找不到对应的部门依赖关系,可能已经被删除", "部门", uuid);
        }
        return new DepartmentParentIexportData(department);
    }

    @Override
    public String getTreeName(Department department) {
        return new DepartmentParentIexportData(department).getName();
    }

}
