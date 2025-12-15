package com.wellsoft.pt.dyform.implement.definition.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.dyform.implement.definition.dao.FormCommonFieldCategoryDao;
import com.wellsoft.pt.dyform.implement.definition.dao.FormCommonFieldDefinitionDao;
import com.wellsoft.pt.dyform.implement.definition.entity.FormCommonFieldCategory;
import com.wellsoft.pt.dyform.implement.definition.service.FormCommonFieldCategoryService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FormCommonFieldCategoryServiceImpl extends
        AbstractJpaServiceImpl<FormCommonFieldCategory, FormCommonFieldCategoryDao, String> implements
        FormCommonFieldCategoryService {

    @Autowired
    FormCommonFieldCategoryDao formCommonFieldCategoryDao;

    @Autowired
    FormCommonFieldDefinitionDao formCommonFieldDefinitionDao;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.context.component.select2.Select2UpdateApi#update(Select2DataBean, com.wellsoft.context.component.select2.Select2QueryInfo)
     */
    @Override
    @Transactional
    public void update(Select2DataBean bean, Select2QueryInfo queryInfo) {
        String id = bean.getId();
        if (StringUtils.isNotBlank(id)) {
            FormCommonFieldCategory entity = formCommonFieldCategoryDao.getOne(id);
            if (entity != null) {
                bean.setText(entity.getCategoryName());
                formCommonFieldCategoryDao.delete(entity);
            }
        } else if (StringUtils.isNoneBlank(bean.getText())) {
            FormCommonFieldCategory entity = new FormCommonFieldCategory();
            entity.setCategoryName(bean.getText());
            String moduleId = null, json = (String) queryInfo.getParams().get("json");
            if (StringUtils.isNotBlank(json)) {
                JSONObject jsonObject = JSONObject.fromObject(json);
                moduleId = jsonObject.optString("moduleId");
            }
            entity.setModuleId(StringUtils.isBlank(moduleId) ? "Global" : moduleId);
            formCommonFieldCategoryDao.save(entity);
            bean.setId(entity.getUuid());
        }
    }

    /**
     * 判断对于分类是否被引用
     *
     * @param id
     * @return
     */
    @Override
    public Boolean isInRef(String id) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("categoryUuid", id);
        String countHql = "select count(t.uuid) from FormCommonFieldDefinition t where t.categoryUuid = :categoryUuid";
        Boolean isInRef = formCommonFieldCategoryDao.countByHQL(countHql, values) > 0;
        return isInRef;
    }

    @Override
    public Select2QueryData loadSelectData(Select2QueryInfo queryInfo) {
        PagingInfo page = queryInfo.getPagingInfo();
        Map<String, Object> params = new HashMap<String, Object>();
        String moduleId = (String) queryInfo.getParams().get("moduleId");
        params.put("moduleId", moduleId == null ? "" : moduleId);
        params.put("searchValue", queryInfo.getSearchValue());
        String hql = "from FormCommonFieldCategory t where (t.moduleId = 'Global' or t.moduleId = :moduleId) and t.categoryName like '%' || :searchValue || '%' order by t.seq asc, t.createTime desc";
        List<FormCommonFieldCategory> categorys = formCommonFieldCategoryDao.listByHQLAndPage(hql, params, page);
        Select2QueryData select2 = new Select2QueryData(queryInfo.getPagingInfo());
        for (FormCommonFieldCategory category : categorys) {
            select2.addResultData(new Select2DataBean(category.getUuid(), category.getCategoryName()));
        }
        return select2;
    }

    @Override
    public Select2QueryData loadSelectDataByIds(Select2QueryInfo queryInfo) {
        Select2QueryData select2 = new Select2QueryData();
        if (queryInfo.getIds()[0].length() > 0) {
            FormCommonFieldCategory category = formCommonFieldCategoryDao.getOne(queryInfo.getIds()[0]);
            select2.addResultData(new Select2DataBean(category.getUuid(), category.getCategoryName()));
        }
        return select2;
    }


    @Override
    @Transactional
    public void moveFieldCategoryAfterOther(String uuid, String afterUuid) {
        List<FormCommonFieldCategory> formCommonFieldCategoryList = dao.listAllByOrderPage(null, "seq asc");
        List<String> uuids = Lists.transform(formCommonFieldCategoryList,
                new Function<FormCommonFieldCategory, String>() {

                    @Nullable
                    @Override
                    public String apply(@Nullable FormCommonFieldCategory formCommonFieldCategory) {
                        return formCommonFieldCategory.getUuid();
                    }
                });
        List<String> uuidList = Lists.newArrayList(uuids);
        int uuidIndex = uuidList.indexOf(uuid);
        if (org.apache.commons.lang.StringUtils.isNotBlank(afterUuid)) {
            uuidList.remove(uuidIndex);
            int otherUuidIndex = uuidList.indexOf(afterUuid);
            uuidList.add(otherUuidIndex + 1, uuid);
        } else {
            uuidList.remove(uuidIndex);
            uuidList.add(0, uuid);
        }

        int seq = 1;
        for (String uid : uuidList) {
            Map<String, Object> params = Maps.newHashMap();
            params.put("seq", seq++);
            params.put("uuid", uid);
            dao.updateByHQL("update FormCommonFieldCategory set seq=:seq where uuid=:uuid", params);
        }
    }

}
