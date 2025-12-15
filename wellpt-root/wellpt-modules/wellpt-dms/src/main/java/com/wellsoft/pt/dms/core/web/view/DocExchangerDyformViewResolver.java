package com.wellsoft.pt.dms.core.web.view;

import com.wellsoft.pt.basicdata.datastore.service.CdDataStoreDefinitionService;
import com.wellsoft.pt.dms.config.support.Configuration;
import com.wellsoft.pt.dms.config.support.DocExchangeStore;
import com.wellsoft.pt.dms.config.support.Store;
import com.wellsoft.pt.dms.core.context.ActionContext;
import com.wellsoft.pt.dms.core.support.DataType;
import com.wellsoft.pt.dms.core.web.View;
import com.wellsoft.pt.dms.entity.DmsDocExchangeConfigEntity;
import com.wellsoft.pt.dms.service.DmsDocExchangeConfigService;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Description: 文档交换器 表单视图解析
 *
 * @author chenq
 * @date 2018/5/21
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/21    chenq		2018/5/21		Create
 * </pre>
 */
@Component
public class DocExchangerDyformViewResolver extends DyFormViewResolver {

    @Autowired
    private DyFormFacade dyFormApiFacade;

    @Autowired
    private DmsDocExchangeConfigService dmsDocExchangeConfigService;
    @Autowired
    private CdDataStoreDefinitionService cdDataStoreDefinitionService;

    @Override
    public View resolveView(ActionContext actionContext, HttpServletRequest request) {
        Configuration configuration = actionContext.getConfiguration();
        Store store = configuration.getStore(actionContext, request);
        String dataType = store.getDataType();
        String configUuid = store.getConfigUuid();
        String formUuid = store.getFormUuid();
        if (StringUtils.isNotBlank(configUuid)) {
            DmsDocExchangeConfigEntity configEntity = dmsDocExchangeConfigService.getOne(configUuid);
            if (configEntity.getExchangeType() == 0) {
                dataType = DataType.DYFORM.getId();
            } else {
                dataType = DataType.FILE.getId();
            }
            formUuid = configEntity.getDyformUuid();
        }
        if (store instanceof DocExchangeStore && isDyFormType(dataType)) {
            return new DocExchangerDyformView(formUuid, getIdValue(request));
        } else if (store instanceof DocExchangeStore && dataType.equals("FILE")) {
            return new DocExchangerDyformView(null, null);
        }
        return null;
    }

    /**
     * @param request
     * @return
     */
    private String getDataStoreId(HttpServletRequest request) {
        return request.getParameter("dataStoreId");
    }


    @Override
    public int getOrder() {
        return 19;
    }
}
