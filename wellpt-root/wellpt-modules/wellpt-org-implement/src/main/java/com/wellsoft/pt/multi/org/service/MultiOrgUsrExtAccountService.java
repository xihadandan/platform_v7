package com.wellsoft.pt.multi.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.dao.MultiOrgUsrExtAccountDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgUsrExtAccountEntity;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/10/9
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/10/9    chenq		2019/10/9		Create
 * </pre>
 */
public interface MultiOrgUsrExtAccountService extends
        JpaService<MultiOrgUsrExtAccountEntity, MultiOrgUsrExtAccountDao, String> {
    MultiOrgUsrExtAccountEntity getByExternal(String externalLoginId, String externalActType);
}
