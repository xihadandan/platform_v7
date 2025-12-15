package com.wellsoft.pt.multi.org.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.dao.MultiOrgUsrExtAccountDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgUsrExtAccountEntity;
import com.wellsoft.pt.multi.org.service.MultiOrgUsrExtAccountService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

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
@Service
public class MultiOrgUsrExtAccountServiceImpl extends
        AbstractJpaServiceImpl<MultiOrgUsrExtAccountEntity, MultiOrgUsrExtAccountDao, String> implements
        MultiOrgUsrExtAccountService {
    @Override
    public MultiOrgUsrExtAccountEntity getByExternal(String externalLoginId, String externalActType) {
        MultiOrgUsrExtAccountEntity example = new MultiOrgUsrExtAccountEntity();
        example.setExternalAccountId(externalLoginId);
        example.setExternalAccountType(externalActType);
        List<MultiOrgUsrExtAccountEntity> extAccountEntityList = this.dao.listByEntity(example);
        return CollectionUtils.isNotEmpty(extAccountEntityList) ? extAccountEntityList.get(0) : null;
    }
}
