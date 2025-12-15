package com.wellsoft.pt.multi.org.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.dao.impl.MultiOrgSystemUnitAttrDaoImpl;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnitAttr;
import com.wellsoft.pt.multi.org.service.MultiOrgSystemUntAttrService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2020/4/18
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2020/4/18    chenq		2020/4/18		Create
 * </pre>
 */
@Service
public class MultiOrgSystemUntAttrServiceImpl extends
        AbstractJpaServiceImpl<MultiOrgSystemUnitAttr, MultiOrgSystemUnitAttrDaoImpl, String> implements
        MultiOrgSystemUntAttrService {
    @Override
    public String getAttrValue(String attrCode, String unitId) {
        MultiOrgSystemUnitAttr example = new MultiOrgSystemUnitAttr();
        example.setAttrCode(attrCode);
        example.setSystemUnitId(unitId);
        List<MultiOrgSystemUnitAttr> multiOrgSystemUnitAttrList = this.dao.listByEntity(example);
        return CollectionUtils.isNotEmpty(
                multiOrgSystemUnitAttrList) ? multiOrgSystemUnitAttrList.get(
                0).getAttrValue() : null;
    }

    @Override
    public String getCurrentSystemUnitAttrValue(String attrCode) {
        return this.getAttrValue(attrCode, SpringSecurityUtils.getCurrentUserUnitId());
    }

    @Override
    @Transactional
    public void saveUpdateCurrentUnitAttr(MultiOrgSystemUnitAttr attr) {
        MultiOrgSystemUnitAttr attrEntity = getCurrentUnitOneAttr(attr.getAttrCode());
        if (attrEntity != null) {
            attrEntity.setAttrValue(attr.getAttrValue());
            attrEntity.setRemark(attr.getRemark());
            attrEntity.setAttrName(attr.getAttrName());
            this.dao.save(attrEntity);
            return;
        }
        attr.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        this.dao.save(attr);
    }

    @Override
    public MultiOrgSystemUnitAttr getCurrentUnitOneAttr(String attrCode) {
        MultiOrgSystemUnitAttr example = new MultiOrgSystemUnitAttr();
        example.setAttrCode(attrCode);
        example.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        List<MultiOrgSystemUnitAttr> multiOrgSystemUnitAttrList = this.dao.listByEntity(example);
        return CollectionUtils.isNotEmpty(
                multiOrgSystemUnitAttrList) ? multiOrgSystemUnitAttrList.get(
                0) : null;
    }
}
