package com.wellsoft.pt.multi.org.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.bean.OrgElementAttrVo;
import com.wellsoft.pt.multi.org.dao.MultiOrgElementAttrDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgElementAttrEntity;
import com.wellsoft.pt.multi.org.service.MultiOrgElementAttrService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Service
public class MultiOrgElementAttrServiceImpl extends
        AbstractJpaServiceImpl<MultiOrgElementAttrEntity, MultiOrgElementAttrDao, String> implements
        MultiOrgElementAttrService {
    @Override
    @Transactional
    public void deleteByElementUuid(String elementUuid) {
        if (StringUtils.isNotBlank(elementUuid))
            this.dao.deleteByEntities(this.dao.listByFieldEqValue("elementUuid", elementUuid));
    }

    @Override
    @Transactional
    public void saveDtos(List<OrgElementAttrVo> orgElementAttrs) {
        // 进行code字段校验
        for (OrgElementAttrVo vo : orgElementAttrs) {
            String code = vo.getCode();
            if (StringUtils.isEmpty(code)) {
                throw new RuntimeException("属性编码不能为空");
            }
        }
        for (OrgElementAttrVo vo : orgElementAttrs) {
            MultiOrgElementAttrEntity entity = new MultiOrgElementAttrEntity();
            BeanUtils.copyProperties(vo, entity);
            this.save(entity);
        }
    }

    @Override
    public List<OrgElementAttrVo> listByElementUuid(String eleUuid) {
        List<MultiOrgElementAttrEntity> attrEntities = this.dao.listByFieldEqValue("elementUuid", eleUuid);
        List<OrgElementAttrVo> voList = Lists.newArrayList();
        for (MultiOrgElementAttrEntity a : attrEntities) {
            OrgElementAttrVo vo = new OrgElementAttrVo();
            BeanUtils.copyProperties(a, vo);
            voList.add(vo);
        }
        return voList;
    }

    @Override
    public MultiOrgElementAttrEntity getByAttrCodeAndElementUuid(String attrCode, String elementUuid) {
        MultiOrgElementAttrEntity example = new MultiOrgElementAttrEntity();
        example.setCode(attrCode);
        example.setElementUuid(elementUuid);
        List<MultiOrgElementAttrEntity> attrEntities = this.dao.listByEntity(example);
        return CollectionUtils.isNotEmpty(attrEntities) ? attrEntities.get(0) : null;
    }
}
