package com.wellsoft.pt.dyform.implement.definition.service.impl;

import com.wellsoft.pt.dyform.implement.definition.dao.FormCommonFieldRefDao;
import com.wellsoft.pt.dyform.implement.definition.entity.FormCommonFieldRef;
import com.wellsoft.pt.dyform.implement.definition.service.FormCommonFieldRefService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 表单公共字段引用信息service接口实现类
 *
 * @author qiufy
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年4月23日.1	qiufy		2019年4月23日		Create
 * </pre>
 * @date 2019年4月23日
 */
@Service
public class FormCommonFieldRefServiceImpl extends
        AbstractJpaServiceImpl<FormCommonFieldRef, FormCommonFieldRefDao, String> implements FormCommonFieldRefService {

    @Autowired
    FormCommonFieldRefDao dyformFormCommonFieldDefinitionDao;

    /**
     * 获取关联表单数据
     */
    @Override
    @Transactional(readOnly = true)
    public List<FormCommonFieldRef> getFormCommonFieldByFieldUuid(String fieldUuid) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("fieldUuid", fieldUuid);
        List<FormCommonFieldRef> list = listByNameSQLQuery("getRefFormDefintion", params);
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public Long countRefsByFieldUuid(String fieldUuid) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("fieldUuid", fieldUuid);
        String hql = "select count(1) from FormCommonFieldRef t where t.commonFieldUuid = :fieldUuid";
        return dyformFormCommonFieldDefinitionDao.countByHQL(hql, params);
    }

    @Override
    @Transactional
    public void saveRefs(String formUuid, Collection<String> refIds) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("formUuid", formUuid);
        String hql = "delete from FormCommonFieldRef t where t.formUuid = :formUuid";
        dyformFormCommonFieldDefinitionDao.deleteByHQL(hql, params);
        if (null == refIds || refIds.isEmpty()) {
            return;
        }
        for (String refId : refIds) {
            FormCommonFieldRef entity = new FormCommonFieldRef();
            entity.setFormUuid(formUuid);
            entity.setCommonFieldUuid(refId);
            dyformFormCommonFieldDefinitionDao.save(entity);
        }
    }

}
