package com.wellsoft.pt.app.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.app.dao.AppCategoryDao;
import com.wellsoft.pt.app.entity.AppCategoryEntity;
import com.wellsoft.pt.app.service.AppCategoryService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年07月31日   chenq	 Create
 * </pre>
 */
@Service
public class AppCategoryServiceImpl
        extends AbstractJpaServiceImpl<AppCategoryEntity, AppCategoryDao, Long> implements AppCategoryService {
    @Override
    @Transactional
    public Long saveCategory(AppCategoryEntity temp) {
        AppCategoryEntity entity = temp.getUuid() == null ? new AppCategoryEntity() : getOne(temp.getUuid());
        entity.setName(temp.getName());
        entity.setSeq(temp.getSeq());
        entity.setApplyTo(temp.getApplyTo());
        save(entity);
        return entity.getUuid();
    }

    @Override
    @Transactional
    public void deleteCategory(Long uuid) {
        delete(uuid);
    }

    @Override
    public List<AppCategoryEntity> getAllCategoryByApplyTo(String applyTo) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("applyTo", applyTo);
        return dao.listByHQL("from AppCategoryEntity where applyTo=:applyTo order by createTime desc", params);
    }


}
