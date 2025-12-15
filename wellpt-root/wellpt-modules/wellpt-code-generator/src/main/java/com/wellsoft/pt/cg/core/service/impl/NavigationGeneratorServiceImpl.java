/*
 * @(#)2015-8-13 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.cg.core.service.impl;

import com.wellsoft.pt.cg.core.Context;
import com.wellsoft.pt.cg.core.service.NavigationGeneratorService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-8-13.1	zhulh		2015-8-13		Create
 * </pre>
 * @date 2015-8-13
 */
@Service
@Transactional
public class NavigationGeneratorServiceImpl extends BaseServiceImpl implements NavigationGeneratorService {

    //@Autowired
    //private CmsService cmsService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.cg.core.service.NavigationGeneratorService#generate(com.wellsoft.pt.cg.core.Context, java.lang.String)
     */
    @Override
    public void generate(Context context, String tableName) {/*
		String uuid = context.getConfigJson().getNavigationTemplateUuid();
		String parentNavigationUuid = context.getConfigJson().getParentNavigationUuid();
		CmsCategory cmsCategory = this.dao.get(CmsCategory.class, uuid);

		CmsCategory bean = new CmsCategory();
		BeanUtils.copyProperties(cmsCategory, bean);
		bean.setCateName(tableName);
		String code = bean.getCode() + "_" + tableName;
		bean.setTitle(context.getName());
		bean.setShowTitle(context.getName());
		bean.setCode(code);
		bean.setUuid(null);
		cmsService.saveCmsCategoryBean(bean);

		// 设置上级导航
		CmsCategory parent = null;
		if (StringUtils.isNotBlank(parentNavigationUuid)) {
			parent = this.dao.get(CmsCategory.class, parentNavigationUuid);
		} else {
			parent = cmsCategory.getParent();
		}
		if (parent != null) {
			List<CmsCategory> cmsCategories = this.dao.findBy(CmsCategory.class, "code", code);
			for (CmsCategory category : cmsCategories) {
				category.setParent(parent);
				category.setRemark(parent.getUuid());
				this.dao.save(category);
			}
		}
	*/
    }

}
