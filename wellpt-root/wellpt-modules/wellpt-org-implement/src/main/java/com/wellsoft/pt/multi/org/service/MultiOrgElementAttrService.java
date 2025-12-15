package com.wellsoft.pt.multi.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.bean.OrgElementAttrVo;
import com.wellsoft.pt.multi.org.dao.MultiOrgElementAttrDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgElementAttrEntity;

import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/10/26
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/10/26    chenq		2019/10/26		Create
 * </pre>
 */
public interface MultiOrgElementAttrService extends
        JpaService<MultiOrgElementAttrEntity, MultiOrgElementAttrDao, String> {

    void deleteByElementUuid(String elementUuid);

    void saveDtos(List<OrgElementAttrVo> orgElementAttrs);

    List<OrgElementAttrVo> listByElementUuid(String eleUuid);

    MultiOrgElementAttrEntity getByAttrCodeAndElementUuid(String attrCode, String elementUuid);
}
