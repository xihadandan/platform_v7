package com.wellsoft.pt.app.dao.impl;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.app.entity.AppDefCodeHisEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/12/7
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/12/7    chenq		2018/12/7		Create
 * </pre>
 */
@Repository
public class AppDefCodeHisDaoImpl extends AbstractJpaDaoImpl<AppDefCodeHisEntity, String> {

    public List<AppDefCodeHisEntity> listByPage(String relaBusizUuid, String scriptType,
                                                PagingInfo page) {
        AppDefCodeHisEntity param = new AppDefCodeHisEntity();
        param.setRelaBusizUuid(relaBusizUuid);
        param.setScriptType(scriptType);
        return this.listByEntityAndPage(param, page, " createTime desc");
    }
}
