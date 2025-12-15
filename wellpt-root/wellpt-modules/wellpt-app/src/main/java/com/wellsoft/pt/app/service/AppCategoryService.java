package com.wellsoft.pt.app.service;

import com.wellsoft.pt.app.dao.AppCategoryDao;
import com.wellsoft.pt.app.entity.AppCategoryEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年07月28日   chenq	 Create
 * </pre>
 */
public interface AppCategoryService extends JpaService<AppCategoryEntity, AppCategoryDao, Long> {

    Long saveCategory(AppCategoryEntity categoryEntity);

    void deleteCategory(Long uuid);

    List<AppCategoryEntity> getAllCategoryByApplyTo(String applyTo);
}
