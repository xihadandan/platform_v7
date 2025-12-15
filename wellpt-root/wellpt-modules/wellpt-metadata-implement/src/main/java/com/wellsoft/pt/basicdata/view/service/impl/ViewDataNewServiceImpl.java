/*
 * @(#)2013-3-19 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.view.service.impl;

import com.wellsoft.pt.basicdata.view.service.ViewDataNewService;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*import com.wellsoft.pt.file.entity.*;
 import com.wellsoft.pt.file.facade.*;*/

/**
 * Description: 如何描述该类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-19.1	wubin		2013-3-19		Create
 * </pre>
 * @date 2013-3-19
 */
@Service
@Transactional
public class ViewDataNewServiceImpl implements ViewDataNewService {

    /*
     * @Autowired private FileManagerApiFacade fileManagerApiFacade;
     */

    @Autowired
    private DyFormFacade dyFormApiFacade;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDataService#getViewById()
     */
    @Override
    public List getViewById() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 如何描述该方法
     *
     * (non-Javadoc)
     * @see com.wellsoft.pt.basicdata.view.service.ViewDataNewService#changeGGTopStatus(java.lang.String)
     */
    /*
     * @Override public String changeGGTopStatus(String fmUuid, String status) {
     * String[] fmUuids = fmUuid.split(";"); for (String fm : fmUuids) { FmFile
     * fmFile = fileManagerApiFacade.getFmFileByUuid(fm); String formUuid =
     * fmFile.getDynamicFormId(); String dataUuid = fmFile.getDynamicDataId();
     * //设置公告的置顶字段为置顶状态 DyFormData dyFormData =
     * dyFormApiFacade.getDyFormData(formUuid, dataUuid);
     * dyFormData.setFieldValue("SFZD", status);
     * dyFormApiFacade.saveFormData(dyFormData);
     * fileManagerApiFacade.saveFmFile("", dyFormData); } return null; }
     */

}
