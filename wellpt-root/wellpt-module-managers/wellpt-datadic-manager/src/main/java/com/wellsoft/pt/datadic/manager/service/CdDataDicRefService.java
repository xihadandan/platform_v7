package com.wellsoft.pt.datadic.manager.service;

import com.wellsoft.pt.datadic.manager.dao.CdDataDicRefDao;
import com.wellsoft.pt.datadic.manager.dto.CdDataDicRefDto;
import com.wellsoft.pt.datadic.manager.entity.CdDataDicRefEntity;
import com.wellsoft.pt.jpa.service.JpaService;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/6/11
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/6/11    chenq		2019/6/11		Create
 * </pre>
 */
public interface CdDataDicRefService extends
        JpaService<CdDataDicRefEntity, CdDataDicRefDao, String> {

    void saveDataDicRef(CdDataDicRefDto dto);

    void deleteDataDicRef(CdDataDicRefDto dto);
}
