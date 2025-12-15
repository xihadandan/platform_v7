package com.wellsoft.pt.app.iexport;

import com.wellsoft.pt.app.entity.AppProdVersionLoginEntity;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年11月20日   chenq	 Create
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class AppProdVersionLoginIexportDataProvider extends AbstractIexportDataProvider<AppProdVersionLoginEntity, Long> {
    @Override
    public String getType() {
        return IexportType.AppProdVersionLogin;
    }

    @Override
    public String getTreeName(AppProdVersionLoginEntity appProdVersionLoginEntity) {
        return "登录页: " + appProdVersionLoginEntity.getName();
    }
}
