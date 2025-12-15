package com.wellsoft.pt.ureport.facade.service;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.ureport.dto.RpFileRepositoryDto;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/5/10
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/5/10    chenq		2019/5/10		Create
 * </pre>
 */
public interface RpFileRepositoryFacadeService extends Facade {

    public Select2QueryData loadSelectData(Select2QueryInfo queryInfo);

    public RpFileRepositoryDto getRpFileDetail(String uuid);
}
